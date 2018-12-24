package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.InputBarCodeDialog;
import com.liqun.www.liqunalifacepay.ui.view.NumberKeyboardView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsResponseBean;

/**
 * 取消交易&输码|扫码->商品信息界面
 * 1.启用服务端监听
 * 2.取消交易->一次请求,成功后提示并点击"确认"|倒计时结束->主界面
 * 3.输码|扫码->商品信息界面
 *  多次请求,每次获取到返回结果后要重新启用服务端监听,请求结果显示在界面上
 * 4.复用客户端?
 *  4.1.客户端请求,重点在于拼接请求串;
 *  4.2.拼接请求串时涉及到的参数包括标识符+requestBean
 * 5.复用服务端监听?
 *  5.1.监听了同一个端口返回的数据,但只能接收到成功/失败的返回结果,如何分类?
 *  5.2.若成功,则返回实例的类型不一致;
 */
public class SelfHelpPayActivity extends AppCompatActivity
implements View.OnClickListener {

    private static final int MAX_LENGTH = 20;
    private Button mBtnCancelDeal;
    private Button mBtnPay;
    private Button mBtnInput;
    private InputBarCodeDialog mDialog;
    private TextView mTvMessage;
    private NumberKeyboardView mNKV;
    private EditText mEtBarCode;
    private Message mMessage;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 读取服务器失败
                    break;
                case 1:
                    // 处理返回结果
                    if (msg.obj != null) {
                        handlerServerResult(msg.obj);
                    }
                    break;
                case 2:
                    // 连接服务器失败
                    break;
            }
        }
    };

    /**
     * 处理从服务端读取过来的返回结果
     * @param obj
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof CancelDealResponseBean) { // 取消交易
        }
        if (obj instanceof ScanGoodsResponseBean) { // 扫描商品
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SelfHelpPayActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_help_pay);
        initUI();
        setListener();
    }

    private void initUI() {
        // 默认
        mBtnCancelDeal = findViewById(R.id.btn_cancel_deal);
        mBtnInput = findViewById(R.id.btn_input);
        // 商品信息
        mBtnPay = findViewById(R.id.btn_pay);
    }

    private void setListener() {
        // 默认
        mBtnCancelDeal.setOnClickListener(this);
        mBtnInput.setOnClickListener(this);
        // 商品信息
        mBtnPay.setOnClickListener(this);
        // 等待结果的服务端监听
        initNetWorkServer();
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
                    mMessage.what = 1;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_deal:
                // 取消交易
                break;
            case R.id.btn_input:
                // 手输条码
                showInputBarCodeDialog();
                break;
            case R.id.btn_pay:
                // 跳转到选择支付方式界面
                Intent intent = SelectPayTypeActivity.newIntent(this);
                startActivity(intent);
                break;
        }
    }

    /**
     * 弹出条码号输入对话框
     */
    private void showInputBarCodeDialog() {
        if (mDialog == null) {
            mDialog = new InputBarCodeDialog(this);
            setDialogListener();
        }
        mDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mDialog.findViewById(R.id.tv_message);
        }
        if (mTvMessage.getVisibility() == View.VISIBLE) {
            mTvMessage.setVisibility(View.GONE);
        }
        if (mEtBarCode == null) {
            mEtBarCode = mDialog.findViewById(R.id.et_bar_code);
        }
        mEtBarCode.setCursorVisible(false);
        mEtBarCode.setFocusable(false);
        mEtBarCode.setFocusableInTouchMode(false);
        mEtBarCode.setText("");
        if (mNKV == null) {
            mNKV = mDialog.findViewById(R.id.nkv);
        }
        mNKV.setIOnKeyboardListener(new NumberKeyboardView.IOnKeyboardListener() {
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
    }

    private void setDialogListener() {
        mDialog.setOnNoClickListener(new InputBarCodeDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.setOnYesClickListener(new InputBarCodeDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                String barCodeStr = mEtBarCode.getText().toString().trim();
                if ("".equals(barCodeStr)) {
                    mTvMessage.setVisibility(View.VISIBLE);
                    mTvMessage.setText(R.string.bar_code_not_null);
                    return;
                }
                L.e("条码长度 = " + barCodeStr.length());
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
                        ConstantValue.TAG_SCAN_GOODS,
                        new ScanGoodsRequestBean(
                                ConstantValue.IP_SERVER_ADDRESS,
                                barCodeStr,
                                1
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
}
