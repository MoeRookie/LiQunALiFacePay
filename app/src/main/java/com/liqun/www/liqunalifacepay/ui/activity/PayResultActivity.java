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
import com.liqun.www.liqunalifacepay.data.utils.JointDismantleUtils;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;
import com.szsicod.print.escpos.PrinterAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeResponseBean.*;

public class PayResultActivity extends AppCompatActivity
implements View.OnClickListener {

    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private static final String EXTRA_PTRB = "com.liqun.www.liqunalifacepay.ptrb";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private final static int time = 10000;
    private MyCountDownTimer cdt;
    private LinearLayout mLLPaySuccess;
    private LinearLayout mLLPayFail;
    private TextView mTvTotalPrice1,mTvCount,mTvTotalPrice2,mTvTotalPrice3,mTvCount1,mTvErrHint;
    public PrinterAPI mPrinter = PrinterAPI.getInstance();
    private int mCount;
//             1、获取到printtxt字段内容,若内容非空则开始打印
//             2、连接usb,成功后开始打印printxt的内容
//             3、打印完成后继续调用打印文本->打印二维码->打印文本的功能
//             4、打印最终完成后断开打印机
//            if (!TextUtils.isEmpty(printtxt)) {
//                // 连接usb
//                if(mPrinter.isConnect()){
//                    mPrinter.disconnect();
//                }
//                // 开启子线程
//                new Thread(){
//                    @Override
//                    public void run() {
//                        super.run();
//                        InterfaceAPI io = new USBAPI(PayResultActivity.this);
//                        // 如果打印机连接API成功
//                        if (PrinterAPI.SUCCESS == mPrinter.connect(io)) {
//                            // 打印printxt的内容
//                            // 设置排版
////                            mPrinter.set58mm();
//                            // 设置字体样式
//                            mPrinter.setFontStyle(0);
//                            try {
//                                // 如果字符串流入打印机成功
//                                if (PrinterAPI.SUCCESS == mPrinter.printString(
//                                        printtxt,
//                                        "GBK",
//                                        // 流入
//                                        true)) {
//                                    String printtxt1 = "支付宝扫下方二维码 , 更多优惠及精彩内容为你呈现";
//                                    // 打印之后的文本
//                                    if (PrinterAPI.SUCCESS == mPrinter.printString(
//                                            printtxt1,"GBK",true)) {
//                                        // 打印二维码
//                                        if (PrinterAPI.SUCCESS == mPrinter.printQRCode(
//                                                "https://m.alipay.com/9y5i54d",
//                                                6,
//                                                false)) {
//                                            // 打印并换行
//                                            mPrinter.printFeed();
//                                        }
//                                        // 打印最后的文本切纸并关闭打印机
//                                        String printtxt2 = "\n\t\t    利群集团";
//                                        if (PrinterAPI.SUCCESS == mPrinter.printString(
//                                                printtxt2,"GBK",true)) {
//                                            // 切纸
//                                            mPrinter.cutPaper(66, 0);
//                                            // 关闭打印机
//                                            mPrinter.disconnect();
//                                        }
//                                    }
//                                }
//                            } catch (UnsupportedEncodingException e) {
//                                L.e("==================不支持的编码格式===============");
//                            }
//                        }
//                    }
//                }.start();
//            }

    private Button mBtnReturnHome1,mBtnReturnHome2,mBtnContinuePay;
    private float mTotalPrice;

    public static Intent newIntent(
            Context packageContext, PaymentTypeResponseBean ptrb, float totalPrice, int count)
    {
        Intent intent = new Intent(packageContext, PayResultActivity.class);
        intent.putExtra(EXTRA_PTRB, ptrb);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        intent.putExtra(EXTRA_COUNT, count);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_result);
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        // 支付成功界面
        mLLPaySuccess = findViewById(R.id.ll_pay_success);
        mTvTotalPrice1 = findViewById(R.id.tv_total_price1);
        mTvCount = findViewById(R.id.tv_count);
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
            mCount = intent.getIntExtra(EXTRA_COUNT, 0);
            mTotalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            PaymentTypeResponseBean ptrb =
                    (PaymentTypeResponseBean) intent.getSerializableExtra(EXTRA_PTRB);
            String retflag = ptrb.getRetflag();
            String retmsg = ptrb.getRetmsg();
            switch (retflag) {
                case "0":
                case "2":
                    // 显示支付成功界面,隐藏支付失败界面
                    setLayoutVisibility(true);
                    // 设置支付成功界面显示
                    mTvTotalPrice1.setText("￥" + mTotalPrice);
                    mTvCount.setText("共" + mCount + "件商品");
                    mTvTotalPrice2.setText("￥" + mTotalPrice);
                    /**
                     * 打印小票
                     * 1.连接usb;
                     * 2.成功后开始打印服务端返回的文本;
                     * 3.成功后开始打印自己拼接的文本;
                     * 4.成功后开始打印二维码;
                     * 5.成功后打印"利群集团(须居中显示)"字样;
                     * 6.断开usb连接并启动计时器;
                     */
                    // 启用计时器
                    cdt.start();
                    break;
                case "1":
                    // 显示支付失败界面,隐藏支付成功界面
                    setLayoutVisibility(false);
                    mTvTotalPrice3.setText("￥" + mTotalPrice);
                    mTvCount1.setText("共" + mCount + "件商品");
                    mTvErrHint.setText(retmsg);
                    break;
            }
        }
    }


    private void setListener() {
        mBtnReturnHome1.setOnClickListener(this);
        mBtnReturnHome2.setOnClickListener(this);
        mBtnContinuePay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_return_home1: // 返回首页1
                enterHome();
                cdt.cancel();
                break;
            case R.id.btn_return_home2: // 返回首页2
                enterHome();
                break;
            case R.id.btn_continue_pay: // 继续支付
                // 跳转扫码付款界面
                Intent intent = ScanCodePayActivity.newIntent(
                        this,
                        mCount,
                        mTotalPrice);
                startActivity(intent);
                break;
        }
        cdt.onFinish();
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
            mBtnReturnHome1.setText("返回首页("+ millisUntilFinished/1000 + "s)");
        }

        @Override
        public void onFinish() {
            enterHome();
            finish();
        }
    }
}
