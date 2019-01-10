package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.CancelDealBean;
import com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean;
import com.liqun.www.liqunalifacepay.data.bean.DealRecordBean;
import com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayResponseBean.JsonBean;
import com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean;
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;
import com.szsicod.print.escpos.PrinterAPI;
import com.szsicod.print.io.InterfaceAPI;
import com.szsicod.print.io.USBAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;

import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayResponseBean.JsonBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.*;

public class FacePayResultActivity extends AppCompatActivity
implements View.OnClickListener {

    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private static final String EXTRA_PAY_RESULT = "com.liqun.www.liqunalifacepay.pay_result";
    private final static int time = 10000;
    private MyCountDownTimer cdt;
    private LinearLayout mLLPaySuccess;
    private LinearLayout mLLPayFail;
    private TextView mTvTotalPrice1,mTvCount,mTvTotalPrice2,mTvTotalPrice3,mTvCount1,mTvErrHint;
    private int mCount;

    private Button mBtnReturnHome1,mBtnReturnHome2,mBtnContinuePay;
    private float mTotalPrice;
    private LinearLayout mLLALiPayDisCounts;
    private TextView mTvAccount,mTvALiPayDiscounts;
    private boolean mIsSuccess = true;
    public PrinterAPI mPrinter = PrinterAPI.getInstance();
    public static Thread sServerSocketThread;
    public static ServerSocket sServerSocket;
    private Message mMessage;
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
                    showWarnDialog(
                            getString(R.string.connect_server_fail)
                    );
                    break;
            }
        }
    };
    private WarnDialog mWarnDialog;
    private TextView mTvMessage;

    /**
     * 显示警告类型的对话框
     * @param msg
     */
    private void showWarnDialog(String msg) {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
            mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClicked() {
                    mWarnDialog.dismiss();
                }
            });
        }
        mWarnDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mWarnDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setText(msg);
    }

    public static Intent newIntent(
            Context packageContext,
            float totalPrice,
            int count,
            FacePayResponseBean fprb) {
        Intent intent = new Intent(packageContext, FacePayResultActivity.class);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_PAY_RESULT,fprb);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        // 支付成功界面
        mLLPaySuccess = findViewById(R.id.ll_pay_success);
        mTvAccount = findViewById(R.id.tv_account);
        mTvTotalPrice1 = findViewById(R.id.tv_total_price1);
        mTvCount = findViewById(R.id.tv_count);
        mLLALiPayDisCounts = findViewById(R.id.ll_alipay_discounts);
        mTvALiPayDiscounts = findViewById(R.id.tv_alipay_discounts);
        mTvTotalPrice2 = findViewById(R.id.tv_total_price2);
        mBtnReturnHome1 = findViewById(R.id.btn_return_home1);
        // 支付失败界面
        mLLPayFail = findViewById(R.id.ll_pay_fail);
        mTvTotalPrice3 = findViewById(R.id.tv_total_price_3);
        mTvCount1 = findViewById(R.id.tv_count1);
        mTvErrHint = findViewById(R.id.tv_err_hint);
        mBtnReturnHome2 = findViewById(R.id.btn_return_home2);
        mBtnContinuePay = findViewById(R.id.btn_continue_pay);
    }

    private void initData() {
        cdt = new MyCountDownTimer(time,1000);
        Intent intent = getIntent();
        if (intent != null) {
            mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            mCount = intent.getIntExtra(EXTRA_COUNT, 0);
            final FacePayResponseBean fprb = (FacePayResponseBean) intent.getSerializableExtra(EXTRA_PAY_RESULT);
            AlipayTradePayResponseBean payResponse = fprb.getJson().getAlipay_trade_pay_response();
            String totalAmount = payResponse.getTotal_amount();
            String code = payResponse.getCode();
            if ("10000".equals(code)) { // 成功
                // 设置刷脸付成功
                mIsSuccess = true;
                String aliPayAccount = payResponse.getBuyer_logon_id();
                String receiptAmountStr = payResponse.getReceipt_amount();// 实收金额
                String buyerPayAmountStr = payResponse.getBuyer_pay_amount();// 付款金额
                double receiptAmount = Double.valueOf(receiptAmountStr);
                double buyerPayAmount = Double.valueOf(buyerPayAmountStr);
                DecimalFormat df = new DecimalFormat("#.00");
                double discounts = receiptAmount-buyerPayAmount;
                String disCountsStr = df.format(discounts);
                // 显示支付成功界面,隐藏支付失败界面
                setLayoutVisibility(true);
                // 设置支付成功界面显示
                mTvAccount.setVisibility(View.VISIBLE);
                mTvAccount.setText("支付宝账号" + aliPayAccount);
                mTvTotalPrice1.setText("￥" + mTotalPrice);
                mTvCount.setText("共" + mCount + "件商品");
                mTvTotalPrice2.setText("￥" + mTotalPrice);
                if (Double.valueOf(disCountsStr) > 0.00) {
                    // 设置优惠项显示
                    mLLALiPayDisCounts.setVisibility(View.VISIBLE);
                    // 设置优惠金额
                    mTvALiPayDiscounts.setText("￥"+disCountsStr);
                }
                // 请求支付结果
                requestNetWorkServer(
                        ConstantValue.TAG_PAYMENT_TYPE,
                        new PaymentTypeRequestBean(
                                ALiFacePayApplication.getInstance().getHostIP(),
                                "32",
                                Float.valueOf(totalAmount),
                                payResponse.getOut_trade_no(),
                                "",
                                "",
                                "",
                                "0",
                                null
                        )
                );
            }else{ // 失败
                // 设置刷脸付失败
                mIsSuccess = false;
                // 显示支付失败界面,隐藏支付成功界面
                setLayoutVisibility(false);
                mTvTotalPrice3.setText("￥" + mTotalPrice);
                mTvCount1.setText("共" + mCount + "件商品");
                mTvErrHint.setText("支付失败 : "+payResponse.getMsg());
            }
            cdt.start();
        }
    }


    private void setListener() {
        initNetWorkServer();
        mBtnReturnHome1.setOnClickListener(this);
        mBtnReturnHome2.setOnClickListener(this);
        mBtnContinuePay.setOnClickListener(this);
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
     * 处理服务端返回的结果
     * @param obj 对象
     */
    private void handlerServerResult(Object obj) {
        if (obj instanceof PaymentTypeResponseBean) {
            final PaymentTypeResponseBean ptrb = (PaymentTypeResponseBean) obj;
            String retflag = ptrb.getRetflag();
            String retmsg = ptrb.getRetmsg();
            if ("1".equals(retflag)) {
                showWarnDialog(retmsg);
            } else if ("0".equals(retflag) || "2".equals(retflag)) {
                // 打印小票
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        InterfaceAPI io = new USBAPI(FacePayResultActivity.this);
                        // 如果打印机连接API成功
                        if (PrinterAPI.SUCCESS == mPrinter.connect(io)) {
                            // 打印printxt的内容
                            // 设置字体样式
                            mPrinter.setFontStyle(0);
                            try {
                                // 如果字符串流入打印机成功
                                if (PrinterAPI.SUCCESS == mPrinter.printString(
                                        ptrb.getPrinttxt(),
                                        "GBK",
                                        // 流入
                                        true)) {
                                    String printtxt1 = "支付宝扫下方二维码 , 更多优惠及精彩内容为你呈现";
                                    // 打印之后的文本
                                    if (PrinterAPI.SUCCESS == mPrinter.printString(
                                            printtxt1,"GBK",true)) {
                                        // 打印二维码
                                        if (PrinterAPI.SUCCESS == mPrinter.printQRCode(
                                                "https://m.alipay.com/9y5i54d",
                                                6,
                                                false)) {
                                            // 打印并换行
                                            mPrinter.printFeed();
                                        }
                                        // 打印最后的文本切纸并关闭打印机
                                        String printtxt2 = "\n\t\t    利群集团";
                                        if (PrinterAPI.SUCCESS == mPrinter.printString(
                                                printtxt2,"GBK",true)) {
                                            // 切纸
                                            mPrinter.cutPaper(66, 0);
                                            // 关闭打印机
                                            mPrinter.disconnect();
                                        }
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                L.e("==================不支持的编码格式===============");
                            }
                        }
                    }
                }.start();
            }
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
                byte[] buf = new byte[5120];
                int length = 0;
                length = inputStream.read(buf);
                String info = "";
                info = new String(buf, 0, length);
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
                    mMessage.what = 2;
                    mHandler.sendMessage(mMessage);
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return_home1: // 返回首页1
                cdt.cancel();
                cdt.onFinish();
                break;
            case R.id.btn_return_home2: // 返回首页2
                cdt.cancel();
                cdt.onFinish();
                break;
            case R.id.btn_continue_pay: // 继续支付
                // 跳转扫码付款界面
                closeServer();
                Intent intent = ScanCodePayActivity.newIntent(
                        this,
                        mCount,
                        mTotalPrice);
                startActivity(intent);
                break;
        }
    }

    private void enterHome() {
        Intent intent = HomeActivity.newIntent(this);
        startActivity(intent);
    }

    /**
     * 设置支付结果界面显示状态
     * @param visibility 显示与否
     */
    private void setLayoutVisibility(boolean visibility) {
        mLLPaySuccess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mLLPayFail.setVisibility(visibility?View.GONE:View.VISIBLE);
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
            if (mIsSuccess) {
                mBtnReturnHome1.setText("返回首页("+ millisUntilFinished/1000 + "s)");
            }else{
                mBtnReturnHome2.setText("返回首页("+ millisUntilFinished/1000 + "s)");
            }
        }

        @Override
        public void onFinish() {
            enterHome();
            closeServer();
            finish();
        }
    }
    /**
     * 关闭服务端侦听
     */
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
}
