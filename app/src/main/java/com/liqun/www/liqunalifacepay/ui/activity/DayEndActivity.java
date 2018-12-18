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
import com.liqun.www.liqunalifacepay.data.utils.CommonUtils;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.GlobalDialog;
import com.liqun.www.liqunalifacepay.ui.view.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private TextView mTvMessage;
    private EditText mEtPwd;
    private Message mMessage;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int err = R.string.day_end_success;
            switch (msg.what) {
                case 0:
                    err = R.string.connect_client_fail;
                    break;
                case 1:
                    err = R.string.connect_server_fail;
                    break;
                case 2:
                    if (msg.obj != null) {
                        handleResult(msg.obj);
                    }
                    break;
            }
            mLoadingDialog.dismiss();
            super.handleMessage(msg);
        }
    };

    /**
     * 处理请求返回结果
     * @param
     */
    private void handleResult(Object obj) {
        // 日结
        DayEndResponseBean bean = null;
        if (obj instanceof DayEndResponseBean) {
            bean = (DayEndResponseBean) obj;
        }
        mGlobalDialog.dismiss();
        CommonUtils.showLongToast(bean.getRetmsg());
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

    private void initRequestBean() {
        mRequestBean = new DayEndRequestBean();
        mRequestBean.setIp(ALiFacePayApplication.getInstance().getHostIP());
        mRequestBean.setFlag(ConstantValue.FLAG_DAY_END);
    }

    private void setListener() {
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
        mBtnDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 等待日结服务端
                initNetWorkServer();
                // 弹出确认密码弹框
                showDialog();
            }
        });
    }

    private void initNetWorkServer() {
        // 建立Tcp的服务端,并且监听一个端口
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ServerSocket serverSocket = new ServerSocket(
                            ConstantValue.PORT_SERVER_RETURN);
                    // 接受客户端的连接
                    Socket socket  =  serverSocket.accept(); // 接受客户端的连接(该方法是一个阻塞型的方法,当没有客户端与其连接时会一直等待下去)
                    // 获取输入流对象,读取客户端发送的内容
                    InputStream inputStream = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int length = 0;
                    length = inputStream.read(buf);
                    L.i("服务端返回 = " + new String(buf,0,length));
                    mMessage = Message.obtain();
                    mMessage.what = 2;
                    mMessage.obj = JointDismantleUtils.dismantleResponse(
                            new String(buf,0,length)
                    );
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

    private void showDialog() {
        // 使用自定义dialog可以避免因使用了头条的适配方式而导致原生dialog适配出错的问题
        mGlobalDialog = new GlobalDialog(this);
        String title = getString(R.string.input_pwd);
        // 设置对话框标题
        mGlobalDialog.setTitle(title);
        // 设置输入的文本类型
        mGlobalDialog.setEtInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        // 显示对话框
        mGlobalDialog.show();
        mTvMessage = mGlobalDialog.findViewById(R.id.tv_message);
        mEtPwd = mGlobalDialog.findViewById(R.id.et_pwd);
        // 设置弹出对话框时显示错误消息的tv不显示且不占空间
        mTvMessage.setVisibility(View.GONE);
        setDialogListener();
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
                mGlobalDialog.dismiss();
                finish();
            }
        });
        mGlobalDialog.setOnYesClickListener(yesStr, new GlobalDialog.OnYesClickListener() {
            @Override
            public void onYesClick() {
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    // 设置msg控件显示&设置显示内容&中断流程
                    mTvMessage.setVisibility(View.VISIBLE);
                    mTvMessage.setText(R.string.pwd_not_null);
                    return;
                }
                if (!ConstantValue.DAY_END_PWD.equals(pwd)) {
                    // 设置msg控件显示&设置显示内容&清空输入框内容&中断流程
                    mTvMessage.setVisibility(View.VISIBLE);
                    mTvMessage.setText(R.string.pwd_err);
                    mEtPwd.setText("");
                    return;
                }
                // 隐藏msg控件&关掉对话框&发起日结请求
                mTvMessage.setVisibility(View.GONE);
                mGlobalDialog.dismiss();
                // doDayEnd();
                doDayEnd();
            }
        });
    }

    /**
     * 日结请求
     */
    private void doDayEnd() {
        // 弹出提示"日结中,请稍后 . . ."的对话框(自定义"加载中 . . ."类型的对话框)
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
                    //利用输出流对象把数据写出即可
                    outputStream.write(msg.getBytes("utf-8"));
                    L.i("内容已写出!");
                    //关闭资源
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
        mLoadingDialog = new LoadingDialog(this, R.style.LoadingDialogStyle);
        mLoadingDialog.show();
        mLoadingDialog.setMessage(R.string.loading_day_end);
    }
}
