package com.liqun.www.liqunalifacepay.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.CommonUtils;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndResponseBean;

public class DayEndActivity extends AppCompatActivity {
    private TextView mTvBack;
    private Button mBtnDayEnd;
    private ProgressDialog mDialog;
    private Message mMessage = Message.obtain();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int err = R.string.day_end_success;
            switch (msg.what) {
                case 0:
                    err = R.string.connect_client_fail;
                    CommonUtils.showLongToast(err);
                    break;
                case 1:
                    err = R.string.connect_server_fail;
                    CommonUtils.showLongToast(err);
                    break;
                case 2:
                    if (msg.obj != null) {
                        handleResult(msg.obj);
                    }
                    break;
            }
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
        mDialog.dismiss();
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
        initListener();
        initRequestBean();
    }

    private void initRequestBean() {
        mRequestBean = new DayEndRequestBean();
        mRequestBean.setIp(ALiFacePayApplication.getInstance().getHostIP());
        mRequestBean.setFlag(ConstantValue.FLAG_DAY_END);
    }

    private void initNetWorkServer() {
        //建立Tcp的服务端,并且监听一个端口。
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ServerSocket serverSocket = new ServerSocket(
                            ConstantValue.PORT_SERVER_RETURN);
                    //接受客户端的连接
                    Socket socket  =  serverSocket.accept(); //accept()  接受客户端的连接 该方法也是一个阻塞型的方法，没有客户端与其连接时，会一直等待下去。
                    //获取输入流对象，读取客户端发送的内容。
                    InputStream inputStream = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int length = 0;
                    length = inputStream.read(buf);
                    L.i("服务端返回 = " + new String(buf,0,length));
                    mMessage.what = 2;
                    mMessage.obj = JointDismantleUtils.dismantleResponse(
                            new String(buf,0,length)
                    );
                    //关闭资源
                    serverSocket.close();
                } catch (IOException e) {
                    mMessage.what = 0;
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(mMessage);
                }
            }
        }.start();
    }

    private void initListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
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

    private void showDialog() {
        // 初始化dialog配置
        View view = View.inflate(this, R.layout.view_pwd, null);
        final EditText etPwd = view.findViewById(R.id.et_pwd);
        final TextView tvErrHint = view.findViewById(R.id.tv_err_hint);
        final AlertDialog dialog = new AlertDialog.Builder(
                this)
                .setTitle(R.string.please_input_pwd)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.sure,null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd = etPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        tvErrHint.setText(R.string.pwd_not_null);
                        return;
                    }
                    if (!ConstantValue.DAY_END_PWD.equals(pwd)) {
                        tvErrHint.setText(R.string.pwd_err);
                        etPwd.setText("");
                        return;
                    }
                    dialog.dismiss();
                    startDayEnd();
                }
        });
    }

    private void startDayEnd() {
        // 弹出提示"日结中,请稍后 . . ."的对话框
        mDialog = ProgressDialog.show(
                this,
                "日结中 . . .", "",
                true, false);
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
                 * JointDismantleUtil
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
                    //利用输出流对象把数据写出即可。
                    outputStream.write(msg.getBytes("utf-8"));
                    L.i("内容已写出!");
                    //关闭资源
                    socket.close();
                } catch (IOException e) {
                    mMessage.what = 1;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initUI() {
        mTvBack = findViewById(R.id.tv_back);
        mBtnDayEnd = findViewById(R.id.btn_day_end);
    }
}
