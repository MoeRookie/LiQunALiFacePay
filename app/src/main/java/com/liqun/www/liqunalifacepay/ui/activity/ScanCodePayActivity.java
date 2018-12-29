package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liqun.www.liqunalifacepay.R;

public class ScanCodePayActivity extends AppCompatActivity {
    private static final String EXTRA_TOTAL_PRICE = "com.liqun.www.liqunalifacepay.total_price";
    public static Intent newIntent(Context packageContext, float totalPrice) {
        Intent intent = new Intent(packageContext, ScanCodePayActivity.class);
        intent.putExtra(EXTRA_TOTAL_PRICE,totalPrice);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code);
    }
}
