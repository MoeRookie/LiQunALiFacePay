package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.adapter.GoodsAdapter;
import com.liqun.www.liqunalifacepay.ui.view.InputBarCodeDialog;
import com.liqun.www.liqunalifacepay.ui.view.NumberKeyboardView;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsResponseBean;

/**
 * 取消交易&输码|扫码->商品信息界面
 * 1.启用服务端监听(复用)
 * 2.取消交易->一次请求,成功后提示并点击"确认"|倒计时结束->主界面
 * 3.输码|扫码->商品信息界面
 *  多次请求,每次获取到返回结果后显示在界面上
 * 4.复用客户端?
 *  4.1.客户端请求,重点在于拼接请求串;
 *  4.2.拼接请求串时涉及到的参数包括标识符+requestBean
 * 5.复用服务端监听?
 *  5.1.监听了同一个端口返回的数据,但只能接收到成功/失败的返回结果,如何分类?
 *  5.2.若成功,则返回实例的类型不一致;
 */
public class SelfHelpPayActivity extends AppCompatActivity
implements View.OnClickListener {
    public static SelfHelpPayActivity mActivity;
    private static final int MAX_LENGTH = 20;
    private Button mBtnCancelDeal;
    private Button mBtnPay;
    private Button mBtnInput;
    private InputBarCodeDialog mDialog;
    private TextView mTvMessage;
    private NumberKeyboardView mNKV;
    private EditText mEtBarCode;
    private Message mMessage;
    private WarnDialog mWarnDialog;
    private final static int time = 10000;
//    private MyCountDownTimer cdt;
    private LinearLayout mLLSelfPayFirst;
    private LinearLayout mLLSelfPaySecond;
    private TextView mTvResultHint;
    private RecyclerView mRvGoods;
    private LinearLayoutManager mLayoutManager;
    private List<ScanGoodsResponseBean> mList = new ArrayList<>();
    private GoodsAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 读取服务器失败
                    showWarnDialog(
                            getString(R.string.connect_client_fail)
                    );
                    break;
                case 1:
                    // 处理返回结果
                    if (msg.obj != null) {
                        handlerServerResult(msg.obj);
                    }
                    break;
                case 2:
                    // 连接服务器失败
                    // 弹出警告对话框,点击确定返回到主界面
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
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
            CancelDealResponseBean cdrb = (CancelDealResponseBean) obj;
            String retflag = cdrb.getRetflag();
            String retmsg = null;
            if ("0".equals(retflag)) {
                retmsg = "POS交易取消成功!";
            } else if ("1".equals(retflag)) {
                retmsg = cdrb.getRetmsg();
            }
            Intent intent = WarnDialogActivity.newIntent(this,retmsg);
            startActivity(intent);
            // 一定秒数后跳转主界面
//            cdt.start();
        }
        if (obj instanceof ScanGoodsResponseBean) { // 扫描商品
            ScanGoodsResponseBean sgrb = (ScanGoodsResponseBean) obj;
            String retflag = sgrb.getRetflag();
            String retmsg = sgrb.getRetmsg();
            // 手输条码
            if (mDialog.isShowing() && "1".equals(retflag)) {
                mTvMessage.setVisibility(View.VISIBLE);
                mTvMessage.setText(retmsg);
            // 扫描商品
            }else{
                // 1.隐藏默认的自助收银界面&显示商品信息列表界面
                mLLSelfPayFirst.setVisibility(View.GONE);
                mLLSelfPaySecond.setVisibility(View.VISIBLE);
                L.e("提示输入条码的对话框依然在显示中:"+ mDialog.isShowing());
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if ("".equals(retmsg)) {
                    retmsg = "添加商品成功，请继续扫描添加下一个商品！";
                }
                // 2.显示retmsg
                mTvResultHint.setText(retmsg);
                // 3.若retflag==0,则将sgrb添加到集合(index = 0)中并刷新界面
                if ("0".equals(retflag)) {
                    mList.add(0, sgrb);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SelfHelpPayActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
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
        // 默认界面
        mLLSelfPayFirst = findViewById(R.id.ll_self_pay_first);
        // 商品信息界面
        mLLSelfPaySecond = findViewById(R.id.ll_self_pay_second);
        mTvResultHint = findViewById(R.id.tv_result_hint);
        mRvGoods = findViewById(R.id.rv_goods);
        mLayoutManager = new LinearLayoutManager(this);
        mRvGoods.setLayoutManager(mLayoutManager);
        mAdapter = new GoodsAdapter(this,mList);
        mRvGoods.setAdapter(mAdapter);
    }

    private void setListener() {
//        cdt = new MyCountDownTimer(time,1000);
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
                    ServerSocket serverSocket = new ServerSocket(2001);
                    while (true) {
                        Socket socket = serverSocket.accept();// 侦听并接受到此套接字的连接,返回一个Socket对象
                        SocketServerThread socketThread = new SocketServerThread(socket);
                        socketThread.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_deal:
                // 取消交易
                requestNetWorkServer(
                        // 取消交易
                        ConstantValue.TAG_CANCEL_DEAL,
                        new CancelDealRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "0"
                        )
                );
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
                        new ScanGoodsRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
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
                    mMessage.what = 1;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
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
        if (!isFinishing()) {
            mWarnDialog.show();
        }
        mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClicked() {
                if (mWarnDialog.isShowing()) {
//                    cdt.cancel();
                    mWarnDialog.dismiss();
                    finish();
                }
            }
        });
    }
//    class MyCountDownTimer extends CountDownTimer {
//
//        /**
//         * @param millisInFuture    The number of millis in the future from the call
//         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
//         *                          is called.
//         * @param countDownInterval The interval along the way to receive
//         *                          {@link #onTick(long)} callbacks.
//         */
//        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//        }
//
//        @Override
//        public void onFinish() {
//            finish();
//        }
//    }
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
                e.printStackTrace();
            }finally {
                mHandler.sendMessage(mMessage);
            }
        }
    }
}
