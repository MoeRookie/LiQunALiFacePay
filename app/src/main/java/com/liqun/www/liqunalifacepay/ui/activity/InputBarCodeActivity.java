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
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SocketUtils;
import com.liqun.www.liqunalifacepay.ui.view.NumberKeyboardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class InputBarCodeActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 20;
    public static final String EXTRA_RET_MSG = "com.liqun.www.liqunalifacepay.ret_msg";
    private Button mBtnNo,mBtnYes;
    private EditText mEtBarCode;
    private NumberKeyboardView mNkv;
    private TextView mTvMessage;
    private Message mMessage;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: // 读取服务器异常
                    setDialogErrMsg(true,
                            getString(R.string.connect_client_fail));
                    break;
                case 1: // 处理结果
                    if (msg.obj != null) {
                        handleServerResult(msg.obj);
                    }
                    break;
                case 2: // 连接服务器异常
                    setDialogErrMsg(true,
                            getString(R.string.connect_server_fail));
                    break;
            }
        }
    };
    private Thread mServerSocketThread;
    private ServerSocket mServerSocket;

    /**
     * 处理服务端返回数据
     * @param obj 服务端返回的数据对象
     */
    private void handleServerResult(Object obj) {
        if (obj instanceof ScanGoodsBean.ScanGoodsResponseBean) { // 扫描商品
            ScanGoodsBean.ScanGoodsResponseBean sgrb = (ScanGoodsBean.ScanGoodsResponseBean) obj;
            String retflag = sgrb.getRetflag();
            String retmsg = sgrb.getRetmsg();
            // 手输条码
            if ("1".equals(retflag)) {
                setDialogErrMsg(true,retmsg);
            } else if ("0".equals(retflag)) {
                setDialogErrMsg(false, null);
                SocketUtils.closeServer(mServerSocket,mServerSocketThread);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_RET_MSG, sgrb);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }

    /**
     * 设置对话框报错信息
     * @param isMsgVisible 报错信息的控件是否显示
     * @param errStr 报错信息内容
     */
    private void setDialogErrMsg(boolean isMsgVisible, String errStr) {
        mTvMessage.setVisibility(isMsgVisible ? View.VISIBLE : View.GONE);
        if (mTvMessage.getVisibility() == View.VISIBLE) {
            mTvMessage.setText(errStr);
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, InputBarCodeActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_bar_code_dialog);
        initUI();
        setListener();
    }

    private void initUI() {
        mBtnNo = findViewById(R.id.btn_no);
        mEtBarCode = findViewById(R.id.et_number);
        // 设置光标不显示
        mEtBarCode.setCursorVisible(false);
        // 设置不可编辑
        mEtBarCode.setFocusable(false);
        mEtBarCode.setFocusableInTouchMode(false);
        mNkv = findViewById(R.id.nkv);
        mBtnYes = findViewById(R.id.btn_yes);
        mTvMessage = findViewById(R.id.tv_message);
    }

    private void setListener()  {
        initNetWorkServer();
        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 为防止当前的服务端侦听拦截数据,须先关闭当前的服务端侦听
                SocketUtils.closeServer(mServerSocket,mServerSocketThread);
                finish();
            }
        });
        mNkv.setIOnKeyboardListener(new NumberKeyboardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                if (mEtBarCode.getText().length() <= MAX_LENGTH) {
                    mEtBarCode.append(text);
                }
            }
            @Override
            public void onDeleteKeyEvent() {
                int start = mEtBarCode.length() - 1;
                if (start >= 0) {
                    mEtBarCode.getText().delete(start, start + 1);
                }
            }
        });
        mBtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barCodeStr = mEtBarCode.getText().toString().trim();
                if (TextUtils.isEmpty(barCodeStr)) {
                    mTvMessage.setVisibility(View.VISIBLE);
                    mTvMessage.setText(R.string.bar_code_not_null);
                    return;
                }
                if (barCodeStr.length() != 7
                        && barCodeStr.length() != 8
                        && barCodeStr.length() != 13
                        && barCodeStr.length() != 15
                        && barCodeStr.length() != 20) {
                    mTvMessage.setVisibility(View.VISIBLE);
                    mTvMessage.setText(R.string.bar_code_digit_err);
                    return;
                }
                requestNetWorkServer(
                        // 扫描商品
                        ConstantValue.TAG_SCAN_GOODS,
                        new ScanGoodsBean.ScanGoodsRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                barCodeStr,
                                1
                        )
                );
            }
        });
    }
    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
     */
    private void initNetWorkServer() {
        mServerSocketThread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    mServerSocket = new ServerSocket(2001);
                    while (true) {
                        Socket socket = mServerSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                        SocketServerThread socketThread = new SocketServerThread(socket);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mServerSocketThread.start();
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
                L.e("InputBarCodeActivity:::::::::::::::::::::::::::::::::::::::"+info);
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
                mMessage.what = 0;
                e.printStackTrace();
            }finally {
                mHandler.sendMessage(mMessage);
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
                L.e("InputBarCodeActivity::::::::::::::::::::::::::::::::::::::::"+msg);
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
                    mMessage.what = 2;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
