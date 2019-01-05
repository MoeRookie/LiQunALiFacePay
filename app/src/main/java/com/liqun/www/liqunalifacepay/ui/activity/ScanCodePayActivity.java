package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alipay.xdevicemanager.api.XDeviceManager;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeResponseBean;

//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
//重要约定：在正常情况下，客户端代码需要保证这个Activity是整个App的生命周期
public class ScanCodePayActivity extends AppCompatActivity {
    private XDeviceManager mXDeviceManager;
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private TextView mBtnCancelPay,mTvTotalPrice;
    private final static int time = 118000;
    private MyCountDownTimer cdt;
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;
    private Message mMessage;
    private float mTotalPrice = 0.00f;
    private StringBuffer mSb = new StringBuffer();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 连接服务器失败
                    // 弹出警告对话框,点击确定返回到主界面
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
                    break;
                case 1:
                    // 处理返回结果
                    if (msg.obj != null) {
                        handlerServerResult(msg.obj);
                    }
                    break;
                case 2: // 读取服务器失败
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                    break;
                case 3: // 加签失败
                    if (msg.obj != null) {
                        String message = (String) msg.obj;
                        showWarnDialog(message);
                    }
                    break;

            }
        }
    };
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private LoadingDialog mLoadingDialog;
    private int mCount;
    private String mBarCode;
    private String mSignedBarCode;

    /**
     * 处理服务端返回结果
     * @param obj 对象
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelPaymentResponseBean) { // 取消付款
            CancelPaymentResponseBean cprb = (CancelPaymentResponseBean) obj;
            String retflag = cprb.getRetflag();
            String retmsg = cprb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "取消付款成功！";
            }
            showWarnDialog(retmsg);
        }
        if (obj instanceof PaymentTypeResponseBean) {
            PaymentTypeResponseBean ptrb = (PaymentTypeResponseBean) obj;
            closeServer(); // 关闭服务端侦听,防止支付结果界面的请求被其捕获
            // 跳转支付结果界面
            Intent intent = PayResultActivity.newIntent(
                    ScanCodePayActivity.this,
                    ptrb,
                    mTotalPrice,
                    mCount
            );
            startActivity(intent);
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
                    cdt.cancel();
                    cdt.onFinish();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
    }

    public static Intent newIntent(Context packageContext, int count, float totalPrice) {
        Intent intent = new Intent(packageContext, ScanCodePayActivity.class);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
        cdt = new MyCountDownTimer(time,1000);
        cdt.start();
        mXDeviceManager = ALiFacePayApplication.getInstance().getXDeviceManager();
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        mBtnCancelPay = findViewById(R.id.btn_cancel_pay);
        mTvTotalPrice = findViewById(R.id.tv_total_price);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mCount = intent.getIntExtra(EXTRA_COUNT, 0);
            mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            mTvTotalPrice.setText("￥ " + mTotalPrice);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开启服务端侦听
        initNetWorkServer();
    }

    private void setListener() {
        mBtnCancelPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发起取消支付的请求
                requestNetWorkServer(
                        ConstantValue.TAG_CANCEL_PAYMENT,
                        new CancelPaymentRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
            }
        });
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
    // 监听键盘按下
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent){
        // 每次按下获取键值后存贮(如果不是回车,则拼接)
        if (paramKeyEvent.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            mSb.append(asciiToString(String.valueOf(paramKeyEvent.getUnicodeChar())));
        }else{
            // 获取用户支付宝付款码码值
            mBarCode = mSb.toString().trim();
            // 请求支付
            // 加签
                int result[] = new int[1];
                mSignedBarCode = mXDeviceManager.sign(mBarCode.getBytes(), result);
                if (result[0] != 0) {
                    // 加签失败
                    L.e("加签失败：" + result[0]);
                    mMessage = Message.obtain();
                    mMessage.what = 4;
                    mMessage.obj = "加签失败:"+result[0];
                    mHandler.sendMessage(mMessage);
                } else{
                    showLoadingDialog();
                    requestNetWorkServer(
                            ConstantValue.TAG_PAYMENT_TYPE,
                            new PaymentTypeRequestBean(
                                    ALiFacePayApplication.getInstance().getHostIP(),
                                    "07", // 支付方式 - 支付宝
                                    mTotalPrice, // 总金额
                                    mBarCode, // 条码值
                                    "", // 支付密码
                                    "", // 校验码
                                    "", // 银行卡交易参考号
                                    "0",
                                    mSignedBarCode
                            )
                    );
                }
                    // 2.请求支付
            // 清空stringBuffer
            mSb.delete(0,mSb.length());
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }
    /**
     * 弹出加载类型的对话框
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this,R.style.LoadingDialogStyle);
        }
        mLoadingDialog.show();
        mLoadingDialog.setMessage("支付中 . . .");
    }

    /**
     * 将请求时间转换为订单时间
     * @param requestTime 请求时间
     * @return 订单时间
     */
    private String getOrderTime(String requestTime) {
        // 字符串->日期
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            Date date = oldFormat.parse(requestTime);
            // 格式化日期 ->
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            // 获取日期字符串和时间字符串
            return newFormat.format(date);
        } catch (ParseException e) {
            L.e("======================字符串转日期异常=====================");
            e.printStackTrace();
        }
        return null;
    }


    /**
     * ascii码转换为字符串
     * @param paramString ascii码值
     * @return 对应的字符串
     */
    private String asciiToString(String paramString) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append((char) Integer.parseInt(paramString));
        return localStringBuffer.toString();

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
    class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mBtnCancelPay.setText(getString(R.string.cancel_pay) + "(" + millisUntilFinished/1000 + "s)");
        }

        @Override
        public void onFinish() {
            // 关闭服务端监听
            closeServer();
            finish();
        }
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
}
