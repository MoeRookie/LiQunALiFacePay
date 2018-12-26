package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

public class WarnDialogActivity extends AppCompatActivity {
    private final static int time = 10000;
    private static final String EXTRA_WARN_MSG = "extra_warn_msg";
    private String msg;
    private TextView mTvMessage;
    private Button mBtnConfirm;
    private  MyCountDownTimer cdt;


    public static Intent newIntent(Context packageContext, String msg) {
        Intent intent = new Intent(packageContext, WarnDialogActivity.class);
        intent.putExtra(EXTRA_WARN_MSG, msg);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_warn);
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        mTvMessage = findViewById(R.id.tv_message);
        mBtnConfirm = findViewById(R.id.btn_confirm);
    }

    private void initData() {
        cdt = new MyCountDownTimer(time, 1000);
        cdt.start();
        Intent intent = getIntent();
        if (intent != null) {
            msg = intent.getStringExtra(EXTRA_WARN_MSG);
        }else{
            msg = "暂无消息~";
        }
        mTvMessage.setText(msg);
    }

    private void setListener() {
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdt.cancel();
                cdt.onFinish();
            }
        });
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
            Log.e("MyCountDownTimer","onTick....."+millisUntilFinished);
            mBtnConfirm.setText(getString(R.string.sure)+"(" + millisUntilFinished/1000 + "s" + ")");
        }

        @Override
        public void onFinish() {
            Log.e("MyCountDownTimer","onFinish.....");
            finish();
            SelfHelpPayActivity.mActivity.finish();
        }
    }
}
