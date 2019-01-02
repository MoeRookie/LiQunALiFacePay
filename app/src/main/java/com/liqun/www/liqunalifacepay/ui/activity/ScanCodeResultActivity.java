package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

public class ScanCodeResultActivity extends AppCompatActivity {

    private static final String EXTRA_FLAG = "com.liqun.www.liqunalifacepay.flag";
    private static final String EXTRA_MSG = "com.liqun.www.liqunalifacepay.msg";
    private static final String EXTRA_COUNT = "com.liqun.www.liqunalifacepay.count";
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    private LinearLayout mLLPaySuccess;
    private LinearLayout mLLPayFail;
    private TextView mTvTotalPrice1,mTvCount,mTvTotalPrice2,mTvTotalPrice3,mTvCount1,mTvErrHint;

    public static Intent newIntent(Context packageContext, int count, float totalPrice, int flag, String message) {
        Intent intent = new Intent(packageContext, ScanCodeResultActivity.class);
        intent.putExtra(EXTRA_COUNT, count);
        intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
        intent.putExtra(EXTRA_FLAG, flag);
        intent.putExtra(EXTRA_MSG, message);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_result);
        initUI();
        initData();
    }

    private void initUI() {
        // 支付成功界面
        mLLPaySuccess = findViewById(R.id.ll_pay_success);
        mTvTotalPrice1 = findViewById(R.id.tv_total_price1);
        mTvCount = findViewById(R.id.tv_count);
        mTvTotalPrice2 = findViewById(R.id.tv_total_price2);
        // 支付失败界面
        mLLPayFail = findViewById(R.id.ll_pay_fail);
        mTvTotalPrice3 = findViewById(R.id.tv_total_price_3);
        mTvCount1 = findViewById(R.id.tv_count1);
        mTvErrHint = findViewById(R.id.tv_err_hint);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            int flag = intent.getIntExtra(EXTRA_FLAG, -1);
            float totalPrice = intent.getFloatExtra(EXTRA_TOTAL_PRICE, 0.00f);
            int count = intent.getIntExtra(EXTRA_COUNT, 0);
            String message = intent.getStringExtra(EXTRA_MSG);
            switch (flag) {
                case 3:
                    // 显示支付成功界面,隐藏支付失败界面
                    setLayoutVisibility(true);
                    // 设置支付成功界面显示
                    mTvTotalPrice1.setText("￥" + totalPrice);
                    mTvCount.setText("共" + count + "件商品");
                    mTvTotalPrice2.setText("￥" + totalPrice);
                    // 开始请求打印小票
                    break;
                case 4:
                case 5:
                case 6:
                    // 显示支付失败界面,隐藏支付成功界面
                    setLayoutVisibility(false);
                    mTvTotalPrice3.setText("￥" + totalPrice);
                    mTvCount1.setText("共" + count + "件商品");
                    mTvErrHint.setText(message);
                    break;
            }
        }
    }

    /**
     * 设置支付结果界面显示状态
     * @param visibility 显示与否
     */
    private void setLayoutVisibility(boolean visibility) {
        mLLPaySuccess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mLLPayFail.setVisibility(visibility?View.GONE:View.VISIBLE);
    }
}
