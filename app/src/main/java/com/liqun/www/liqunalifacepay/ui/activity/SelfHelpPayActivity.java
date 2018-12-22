package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.InputBarCodeDialog;
import com.liqun.www.liqunalifacepay.ui.view.NumberKeyboardView;

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
        // 等待取消交易结果的服务端监听
//        initNetWorkServer();
    }

    /**
     * 开启本地服务端,以监听此时作为客户端的服务器返回数据
     */
//    private void initNetWorkServer() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    // 建立Tcp的服务端,并且监听一个端口
//                    ServerSocket serverSocket = new ServerSocket(
//                            ConstantValue.PORT_SERVER_RETURN);
//                    // 接受客户端的连接
//                    Socket socket = serverSocket.accept(); // 接受客户端的连接(该方法是一个阻塞型的方法,当没有客户端与其连接时会一直等待下去)
//                    // 获取输入流对象,读取客户端发送的内容
//                    InputStream inputStream = socket.getInputStream();
//                    byte[] buf = new byte[1024];
//                    int length = 0;
//                    length = inputStream.read(buf);
//                    L.i("服务端返回 = " + new String(buf, 0, length));
//                    /**
//                     * 为了避免出现msg被重用的问题,每次的msg对象都要通过Message.obtain()方法获取
//                     */
//                    mMessage = Message.obtain();
//                    mMessage.what = 2;
//                    mMessage.obj = JointDismantleUtils.dismantleResponse(
//                            new String(buf, 0, length)
//                    );
//                    //关闭资源
//                    serverSocket.close();
//                } catch (IOException e) {
//                    mMessage = Message.obtain();
//                    mMessage.what = 0;
//                    e.printStackTrace();
//                } finally {
//                    mHandler.sendMessage(mMessage);
//                }
//            }
//        }.start();
//    }

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
                // 请求获取商品信息
            }
        });
    }
}
