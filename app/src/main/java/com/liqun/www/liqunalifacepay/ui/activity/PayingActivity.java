package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.liqun.www.liqunalifacepay.R;

public class PayingActivity extends AppCompatActivity {
    public static PayingActivity sInstance;
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PayingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_paying);
    }
}
