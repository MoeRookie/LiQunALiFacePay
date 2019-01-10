package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayResponseBean.JsonBean;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.szsicod.print.escpos.PrinterAPI;
import com.szsicod.print.io.InterfaceAPI;
import com.szsicod.print.io.USBAPI;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.FacePayBean.FacePayResponseBean.JsonBean.*;

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
                // 打印小票
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
        mBtnReturnHome1.setOnClickListener(this);
        mBtnReturnHome2.setOnClickListener(this);
        mBtnContinuePay.setOnClickListener(this);
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
            finish();
        }
    }
}
