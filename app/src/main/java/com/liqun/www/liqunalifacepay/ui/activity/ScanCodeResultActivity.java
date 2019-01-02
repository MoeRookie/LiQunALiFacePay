package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.aware.DiscoverySession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.LoadingDialog;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.*;

public class ScanCodeResultActivity extends AppCompatActivity
implements View.OnClickListener {

    private static final String EXTRA_FLAG = "com.liqun.www.liqunalifacepay.flag";
    private static final String EXTRA_MSG = "com.liqun.www.liqunalifacepay.msg";
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private static final String EXTRA_BAR_CODE = "com.liqun.www.liqunalifacepay.bar_code";
    private static final String EXTRA_SIGNED_BAR_CODE = "com.liqun.www.liqunalifacepay.signed_bar_code";
    private LinearLayout mLLPaySuccess;
    private LinearLayout mLLPayFail;
    private TextView mTvTotalPrice1,mTvCount,mTvTotalPrice2,mTvTotalPrice3,mTvCount1,mTvErrHint;
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;
    private Message mMessage;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 连接服务器异常
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
                    break;
                case 1:
                    // 处理返回数据
                    if (msg.obj != null) {
                        handleResult(msg.obj);
                    }
                    break;
                case 2:
                    // 连接客户端异常
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                    break;
            }
        }
    };

    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private int mCount;
    private float mTotalPrice;

    private void handleResult(Object obj) {
        if (obj instanceof PaymentTypeResponseBean) {
            PaymentTypeResponseBean ptrb = (PaymentTypeResponseBean) obj;
            // 关闭服务端侦听
            closeServer();
            String retflag = ptrb.getRetflag();
            String retmsg = ptrb.getRetmsg();
//            if ("0".equals(retflag) || "2".equals(retflag)) {
//                // 打印小票(打印完小票之后启动计时器)
//            } else if ("1".equals(retflag)) {
//                showWarnDialog(retmsg);
//            }

        }
    }

    /**
     * 显示警告类型的对话框
     * @param msg 消息
     */
    private void showWarnDialog(String msg) {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
            mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClicked() {
                    mWarnDialog.dismiss();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
    }
    private Button mBtnReturnHome1,mBtnReturnHome2,mBtnContinuePay;

    public static Intent newIntent(Context packageContext, int count, float totalPrice, int flag, String message, String barCode, String signedBarCode) {
        Intent intent = new Intent(packageContext, ScanCodeResultActivity.class);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        intent.putExtra(EXTRA_FLAG, flag);
        intent.putExtra(EXTRA_MSG, message);
        intent.putExtra(EXTRA_BAR_CODE, barCode);
        intent.putExtra(EXTRA_SIGNED_BAR_CODE, signedBarCode);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_result);
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        // 支付成功界面
        mLLPaySuccess = findViewById(R.id.ll_pay_success);
        mTvTotalPrice1 = findViewById(R.id.tv_total_price1);
        mTvCount = findViewById(R.id.tv_count);
        mTvTotalPrice2 = findViewById(R.id.tv_total_price2);
        mBtnReturnHome1 = findViewById(R.id.btn_return_home1);
        // 支付失败界面
        mLLPayFail = findViewById(R.id.ll_pay_fail);
        mTvTotalPrice3 = findViewById(R.id.tv_total_price_3);
        mTvCount1 = findViewById(R.id.tv_count1);
        mTvErrHint = findViewById(R.id.tv_err_hint);
        mBtnReturnHome2 = findViewById(R.id.btn_return_home2);
        mBtnContinuePay = findViewById(R.id.btn_continue_pay);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            int flag = intent.getIntExtra(EXTRA_FLAG, -1);
            mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            mCount = intent.getIntExtra(EXTRA_COUNT, 0);
            String message = intent.getStringExtra(EXTRA_MSG);
            String barCode = intent.getStringExtra(EXTRA_BAR_CODE);
            String signedBarCode = intent.getStringExtra(EXTRA_SIGNED_BAR_CODE);
            switch (flag) {
                case 3:
                    // 显示支付成功界面,隐藏支付失败界面
                    setLayoutVisibility(true);
                    // 设置支付成功界面显示
                    mTvTotalPrice1.setText("￥" + mTotalPrice);
                    mTvCount.setText("共" + mCount + "件商品");
                    mTvTotalPrice2.setText("￥" + mTotalPrice);
                    // 开始请求打印小票
                    requestNetWorkServer(
                            ConstantValue.TAG_PAYMENT_TYPE,
                            new PaymentTypeRequestBean(
                                    ALiFacePayApplication.getInstance().getHostIP(),
                                    "07", // 支付方式 - 支付宝
                                    mTotalPrice, // 总金额
                                    barCode, // 条码值
                                    "", // 支付密码
                                    "", // 校验码
                                    "", // 银行卡交易参考号
                                    "0",
                                    signedBarCode
                            )
                    );
                    break;
                case 4:
                case 5:
                case 6:
                    // 显示支付失败界面,隐藏支付成功界面
                    setLayoutVisibility(false);
                    mTvTotalPrice3.setText("￥" + mTotalPrice);
                    mTvCount1.setText("共" + mCount + "件商品");
                    mTvErrHint.setText(message);
                    break;
            }
        }
    }
    /**
     * 连接服务端,请求数据
     */
    private void requestNetWorkServer(final String tag, final Object requestBean) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                // 拼接请求串
                String msg = JointDismantleUtils.jointRequest(
                        tag,
                        requestBean
                );
                L.e(msg);
                //建立tcp的服务
                try {
                    Socket socket = new Socket(
                            ConstantValue.IP_SERVER_ADDRESS,
                            ConstantValue.PORT_SERVER_RECEIVE);
                    //获取到Socket的输出流对象
                    OutputStream outputStream = socket.getOutputStream();
                    // 将输出流包装成打印流
                    PrintWriter printWriter=new PrintWriter(outputStream);
                    printWriter.print(msg);
                    printWriter.flush();
                    socket.close();
                } catch (IOException e) {
                    mMessage = Message.obtain();
                    mMessage.what = 0;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void setListener() {
        // 开启服务端侦听
        initNetWorkServer();
        mBtnReturnHome1.setOnClickListener(this);
        mBtnReturnHome2.setOnClickListener(this);
        mBtnContinuePay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return_home1: // 返回首页1
                Intent intent = HomeActivity.newIntent(this);
                startActivity(intent);
                break;
            case R.id.btn_return_home2: // 返回首页2
                break;
            case R.id.btn_continue_pay: // 继续支付
                // 启用扫码付款界面
                intent = ScanCodePayActivity.newIntent(
                        this,
                        mCount,
                        mTotalPrice);
                startActivity(intent);
                break;
        }
        // finish当前界面
        finish();
    }
    /**
     * 启用服务端侦听
     */
    private void initNetWorkServer() {
        sServerSocketThread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sServerSocket = new ServerSocket(2001);
                    while (true) {
                        Socket socket = sServerSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                        SocketServerThread socketThread = new SocketServerThread(socket);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        sServerSocketThread.start();
    }
    public static void closeServer(){
        if (sServerSocket != null) {
            try {
                sServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                L.e("=======================哎呀,关闭服务端侦听失败啦=======================");
            }
        }
        if (sServerSocketThread != null) {
            sServerSocketThread.interrupt();
        }
    }

    /**
     * Socket多线程处理类 用来处理服务端接收到的客户端请求(处理Socket对象)
     */
    class SocketServerThread extends Thread {
        private Socket socket;
        public SocketServerThread(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            super.run();
            // 根据输入输出流和客户端连接
            try {
                // 得到一个输入流，接收客户端传递的信息
                InputStream inputStream = socket.getInputStream();
                // 提高效率，将自己字节流转为字符流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // 加入缓冲区
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String temp = null;
                String info = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    info += temp;
                }
                L.e(info);
                /**
                 * 为了避免出现msg被重用的问题,每次的msg对象都要通过Message.obtain()方法获取
                 */
                mMessage = Message.obtain();
                mMessage.what = 1;
                mMessage.obj = JointDismantleUtils.dismantleResponse(info);
                //关闭资源
                socket.close();
            } catch (IOException e) {
                mMessage = Message.obtain();
                mMessage.what = 2;
                e.printStackTrace();
            }finally {
                mHandler.sendMessage(mMessage);
            }
        }
    }
    /**
     * 设置支付结果界面显示状态
     * @param visibility 显示与否
     */
    private void setLayoutVisibility(boolean visibility) {
        mLLPaySuccess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mLLPayFail.setVisibility(visibility?View.GONE:View.VISIBLE);
    }
}
