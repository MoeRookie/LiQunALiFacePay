package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.*;

public class SelectPayTypeActivity extends AppCompatActivity
implements View.OnClickListener {
    private static final String EXTRA_RETMSG = "com.liqun.www.liqunalifacepay.retmsg";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;
    private Message mMessage;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 连接服务器失败
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
                case 2:
                    // 读取服务器失败
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                    break;
            }
        }
    };
    private float mTotalPrice = 0.00f;
    private int mCount;

    /**
     * 处理请求结果
     * @param obj
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelPaymentResponseBean) {
            CancelPaymentResponseBean cprb = (CancelPaymentResponseBean) obj;
            String retflag = cprb.getRetflag();
            String retmsg = cprb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = "取消付款成功！";
            }
            // 成功后关闭当前服务端侦听&当前界面
            closeServer();
            showWarnDialog(retmsg);
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

    private Button mBtnClose;
    private ImageButton mIbScanCode,mIbSmile;

    public static Intent newIntent(Context packageContext, String retmsg, int count, float totalPrice) {
        Intent intent = new Intent(packageContext, SelectPayTypeActivity.class);
        intent.putExtra(EXTRA_RETMSG, retmsg);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE,totalPrice);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pay_type);
        initError();
        initUI();
        setListener();
    }

    private void initUI() {
        mBtnClose = findViewById(R.id.btn_close);
        mIbScanCode = findViewById(R.id.ib_scan_code);
        mIbSmile = findViewById(R.id.ib_smile);
    }

    private void setListener() {
        mBtnClose.setOnClickListener(this);
        mIbScanCode.setOnClickListener(this);
        mIbSmile.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                // 取消付款(成功后关闭当前服务端侦听&当前界面)
                requestNetWorkServer(
                        ConstantValue.TAG_CANCEL_PAYMENT,
                        new CancelPaymentRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
                break;
            case R.id.ib_scan_code: // 扫码支付
                closeServer();
                Intent intent = ScanCodePayActivity.newIntent(this,mCount,mTotalPrice);
                startActivity(intent);
                finish();
                break;
            case R.id.ib_smile:
                break;
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

    /**
     * 设置错误信息
     */
    private void initError() {
        Intent intent = getIntent();
        if (intent != null) {
            String errStr = intent.getStringExtra(EXTRA_RETMSG);
            if (!TextUtils.isEmpty(errStr)) {
                showWarnDialog(errStr);
            }else{
                mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
                mCount = intent.getIntExtra(EXTRA_COUNT, 0);
                // 1.开启服务端侦听
                initNetWorkServer();
            }
        }
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
                    finish();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
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
