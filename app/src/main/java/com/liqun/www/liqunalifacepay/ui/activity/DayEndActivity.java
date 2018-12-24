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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
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
    private RelativeLayout mSuccessDialog;
    private TextView mTvMessage;
    private EditText mEtPwd;
    private Message mMessage;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int msgId = 0;
            switch (msg.what) {
                case 0:
                    msgId = R.string.connect_client_fail;
                    // 隐藏提示加载中的对话框&显示警告类的对话框
                    dismissLoadingDialog(true,msgId);
                    break;
                case 1:
                    msgId = R.string.connect_server_fail;
                    dismissLoadingDialog(true,msgId);
                    break;
                case 2:
                    if (msg.obj != null) {
                        handleResult(msg.obj);
                    }
                    dismissLoadingDialog(false,msgId);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 隐藏提示"加载中"的对话框并提示异常信息
     * @param isError
     * @param msgId 异常信息串id
     */
    private void dismissLoadingDialog(boolean isError, int msgId) {
        // 隐藏提示"加载中"的对话框
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        if (isError) {
            // 显示警告类型的对话框
            showWarnDialog(getString(msgId));
        }
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
        DayEndResponseBean bean = null;
        if (obj instanceof DayEndResponseBean) {
            bean = (DayEndResponseBean) obj;
            if ("0".equals(bean.getRetflag())) {
                // 设置日结完成界面显示
                mSuccessDialog.setVisibility(View.VISIBLE);
            } else if ("1".equals(bean.getRetflag())) {
                // 设置日结完成界面隐藏
                mSuccessDialog.setVisibility(View.GONE);
                // 显示⚠️类型的对话框
                showWarnDialog(bean.getRetmsg());
            }
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
        mSuccessDialog = findViewById(R.id.dialog_day_end_success);
    }

    private void initRequestBean() {
        mRequestBean = new DayEndRequestBean();
        mRequestBean.setIp(ALiFacePayApplication.getInstance().getHostIP());
        mRequestBean.setFlag(ConstantValue.FLAG_DAY_END);
    }

    private void setListener() {
        /**
         * 点击"返回"|"确定"按钮都返回到上一个界面
         * 即便是"确定"具有积极结果的意义(成功日结)
         */
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        /**
         * 点击确定按钮,返回上级界面
         */
        mSuccessDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
     */
    private void initNetWorkServer() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    // 建立Tcp的服务端,并且监听一个端口
                    ServerSocket serverSocket = new ServerSocket(
                            ConstantValue.PORT_SERVER_RETURN);
                    // 接受客户端的连接
                    Socket socket  =  serverSocket.accept(); // 接受客户端的连接(该方法是一个阻塞型的方法,当没有客户端与其连接时会一直等待下去)
                    // 获取输入流对象,读取客户端发送的内容
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                    // 加入缓冲区
                    BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                    String temp=null;
                    String info="";
                    while((temp=bufferedReader.readLine())!=null){
                        info+=temp;
                    }
                    /**
                     * 为了避免出现msg被重用的问题,每次的msg对象都要通过Message.obtain()方法获取
                     */
                    mMessage = Message.obtain();
                    mMessage.what = 2;
                    mMessage.obj = JointDismantleUtils.dismantleResponse(info);
                    //关闭资源
                    serverSocket.close();
                } catch (IOException e) {
                    mMessage = Message.obtain();
                    mMessage.what = 0;
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(mMessage);
                }
            }
        }.start();
    }

    /**
     * 弹出确认密码弹框
     */
    private void showGlobalDialog() {
        if (mGlobalDialog == null) {
            // 使用自定义dialog可以避免因使用了头条的适配方式而导致原生dialog适配出错的问题
            mGlobalDialog = new GlobalDialog(this);
            // 设置对话框标题
            String title = getString(R.string.input_pwd);
            mGlobalDialog.setTitle(title);
            // 设置输入的文本类型
            mGlobalDialog.setEtInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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
                // doDayEnd();
                /**
                 * 发起日结
                 * 开启本地服务端,以监听此时作为客户端的服务器返回数据
                 *  1.端口被占用的异常
                 *  2.读取服务端返回数据的过程中的io异常
                 */
                doDayEnd();
            }
        });
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
     * 日结请求
     */
    private void doDayEnd() {
        // 弹出提示"日结中 . . ."的对话框(自定义"加载"类型的对话框)
        showLoadingDialog();
        // 请求网络做日结
        new Thread(){
            @Override
            public void run() {
                super.run();
                /**
                 * 标识符
                 * 分隔符
                 * json str
                 *  机具ip地址
                 *  flag
                 *
                 * JointDismantleUtils
                 *  获取本机ip地址?????????????????????????????????????????
                 *  jointRequest(标识符,Object);
                 *      object -> json(fastJson)
                 *  dismantleResponse(str);
                 *      json -> object(fastJson)
                 */
                String msg = JointDismantleUtils.jointRequest(
                        ConstantValue.TAG_DAY_END,
                        mRequestBean
                );
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
                    mMessage.what = 1;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
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
