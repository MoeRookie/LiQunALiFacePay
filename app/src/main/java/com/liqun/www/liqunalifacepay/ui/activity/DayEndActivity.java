package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.utils.CommonUtils;

public class DayEndActivity extends AppCompatActivity {
    private ImageView mIvBack;
    private Button mBtnDayEnd;
    private AlertDialog mDialog;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, DayEndActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_end);
        initUI();
        initListener();
    }

    private void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBtnDayEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出确认密码弹框
                showDialog();
            }
        });
    }

    private void showDialog() {
        // 初始化dialog配置
        View view = View.inflate(this, R.layout.view_pwd, null);
        final EditText etPwd = view.findViewById(R.id.et_pwd);
        final TextView tvErrHint = view.findViewById(R.id.tv_err_hint);
        mDialog = new AlertDialog.Builder(
                this)
                .setTitle(R.string.please_input_pwd)
                .setView(view)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.sure,null)
                .create();
        mDialog.show();
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd = etPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        tvErrHint.setText(R.string.pwd_not_null);
                        return;
                    }
                    if (!ConstantValue.DAY_END_PWD.equals(pwd)) {
                        tvErrHint.setText(R.string.pwd_err);
                        etPwd.setText("");
                        return;
                    }
                    // 开始做日结
                    CommonUtils.showLongToast("要开始日结啦！");
                    mDialog.dismiss();
                }
        });
    }

    private void initUI() {
        mIvBack = findViewById(R.id.iv_back);
        mBtnDayEnd = findViewById(R.id.btn_day_end);
    }
}
