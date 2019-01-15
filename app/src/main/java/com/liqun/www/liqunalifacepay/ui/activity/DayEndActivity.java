package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SocketUtils;
import com.liqun.www.liqunalifacepay.ui.view.GlobalDialog;
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

import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndResponseBean;

public class DayEndActivity extends AppCompatActivity{
    private TextView mTvBack;
    private TextView mTvSure;
    private Button mBtnDayEnd;
    private GlobalDialog mGlobalDialog;
    private LoadingDialog mLoadingDialog;
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;
    private EditText mEtPwd;
    private Message mMessage;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = getString(R.string.day_end_success);
            switch (msg.what) {
                case 0:
                    message = getString(R.string.connect_client_fail);
                    // 隐藏提示加载中的对话框&显示警告类的对话框
                    dismissLoadingDialog(message);
                    break;
                case 1:
                    if (msg.obj != null) {
                        handleResult(msg.obj);
                    }
                    break;
                case 2:
                    message = getString(R.string.connect_server_fail);
                    dismissLoadingDialog(message);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private static Thread sServerSocketThread;
    private static ServerSocket sServerSocket;

    /**
     * 隐藏提示"加载中"的对话框并提示信息
     * @param msg 信息串
     */
    private void dismissLoadingDialog(String msg) {
        // 隐藏提示"加载中"的对话框
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        // 显示警告类型的对话框
        showWarnDialog(msg);
    }

    /**
     * 显示警告类型的对话框
     * @param msg 报错信息串
     */
    private void showWarnDialog(String msg) {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
        }
        mWarnDialog.setMessage(msg);
        mWarnDialog.show();
        mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClicked() {
                if (mWarnDialog.isShowing()) {
                    mWarnDialog.dismiss();
                    // 关闭当前服务端侦听
                    SocketUtils.closeServer(sServerSocket,sServerSocketThread);
                    // 挂掉当前界面
                    finish();
                }
            }
        });
    }

    /**
     * 处理请求返回结果
     * @param
     */
    private void handleResult(Object obj) {
        // 日结
        if (obj instanceof DayEndResponseBean) {
            DayEndResponseBean derb = (DayEndResponseBean) obj;
            String retflag = derb.getRetflag();
            String retmsg = derb.getRetmsg();
            if ("0".equals(retflag)) {
                retmsg = getString(R.string.day_end_success);
            }
            dismissLoadingDialog(retmsg);
        }
    }

    private DayEndRequestBean mRequestBean;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, DayEndActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_end);
        initUI();
        setListener();
        initRequestBean();
    }

    private void initUI() {
        mTvBack = findViewById(R.id.tv_back);
        mTvSure = findViewById(R.id.tv_sure);
        mBtnDayEnd = findViewById(R.id.btn_day_end);
    }

    private void setListener() {
        /**
         * 点击"返回"|"确定"按钮都返回到上一个界面
         * 即便是"确定"具有积极结果的意义(成功日结)
         */
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.挂掉当前界面
                finish();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1.挂掉当前界面
                finish();
            }
        });
        /**
         * 点击"日结"按钮
         * 开启本地服务端,以监听此时作为客户端的服务器返回数据
         * 弹出确认密码的对话框(因为不是什么人都可以做日结的,必须要有一个验证的过程)
         */
        mBtnDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 等待日结服务端
                initNetWorkServer();
                // 弹出确认密码弹框
                showGlobalDialog();
            }
        });
    }

    private void initRequestBean() {
        mRequestBean = new DayEndRequestBean();
        mRequestBean.setIp(ALiFacePayApplication.getInstance().getHostIP());
        mRequestBean.setFlag(ConstantValue.FLAG_DAY_END);
    }
    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
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
                mMessage.what = 0;
            }finally {
                mHandler.sendMessage(mMessage);
            }
        }
    }
    /**
     * 弹出确认密码弹框
     * 使用自定义dialog可以避免因使用了头条的适配方式而导致原生dialog适配出错的问题
     */
    private void showGlobalDialog() {
        if (mGlobalDialog == null) {
            mGlobalDialog = new GlobalDialog(this);
            // 设置对话框标题
            String title = getString(R.string.input_pwd);
            mGlobalDialog.setTitle(title);
            // 设置输入的文本类型
            mGlobalDialog.setEtInputType(
                    InputType.TYPE_CLASS_NUMBER
                            |InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            setDialogListener();
        }
        // 显示对话框
        mGlobalDialog.show();
        if (mGlobalDialog.isShowing()) {
            mTvMessage = mGlobalDialog.findViewById(R.id.tv_message);
            mEtPwd = mGlobalDialog.findViewById(R.id.et_content);
        }
    }

    /**
     * 监听输入密码对话框取消&确定按钮被点击
     */
    private void setDialogListener() {
        String noStr = getString(R.string.cancel);
        String yesStr = getString(R.string.sure);
        mGlobalDialog.setOnNoClickListener(noStr, new GlobalDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                if (mGlobalDialog.isShowing()) {
                    mGlobalDialog.dismiss();
                }
                // 1.关闭当前服务端侦听
                SocketUtils.closeServer(sServerSocket,sServerSocketThread);
                // 2.挂掉当前界面
                finish();
            }
        });
        mGlobalDialog.setOnYesClickListener(yesStr, new GlobalDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    setErrorMsgLayout(true,R.string.pwd_not_null);
                    return;
                }
                if (!ConstantValue.DAY_END_PWD.equals(pwd)) {
                    setErrorMsgLayout(true,R.string.pwd_err);
                    mEtPwd.setText("");
                    return;
                }
                setErrorMsgLayout(false,R.string.empty);
                // 隐藏msg控件&关掉对话框&发起日结请求
                mGlobalDialog.dismiss();
                requestNetWorkServer(
                        ConstantValue.TAG_DAY_END,
                        new DayEndRequestBean(
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
        showLoadingDialog();
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
                    mMessage.what = 2;
                    mHandler.sendMessage(mMessage);
                }
            }
        }.start();
    }
    /**
     * 设置报错后的对话框布局
     * @param isError 是否报错
     * @param errStrId 报错提示字符串id
     */
    private void setErrorMsgLayout(boolean isError, int errStrId) {
        // 设置msg控件显示&设置显示内容&中断流程
        mTvMessage.setVisibility(isError?View.VISIBLE:View.GONE);
        mTvMessage.setText(errStrId);
    }
    /**
     * 显示提示"加载中 . . ."类型的对话框
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, R.style.LoadingDialogStyle);
        }
        mLoadingDialog.show();
        mLoadingDialog.setMessage(R.string.loading_day_end);
    }
}
