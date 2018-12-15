package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItem;
import com.liqun.www.liqunalifacepay.ui.adapter.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private ImageView mIvBack;
    private RecyclerView mRvSetting;
    private List<SettingItem> mItemList;
    private SettingAdapter mAdapter;
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initData();
        initUI();
        setListener();
    }

    private void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        mItemList = new ArrayList<>();
        mItemList.add(new SettingItem(
                R.string.self_gathering_ip,ALiFacePayApplication.getInstance().getHostIP()));
        mItemList.add(new SettingItem(
            R.string.self_gathering_port,ConstantValue.SELF_GATHERING_PORT
        ));
        mItemList.add(
                new SettingItem(R.string.shop_name,"")
        );
        mItemList.add(
                new SettingItem(R.string.shop_no,"")
        );
        mItemList.add(
                new SettingItem(R.string.shop_merchant_no,"")
        );
        mItemList.add(
                new SettingItem(R.string.catwalk_no,"")
        );
        mItemList.add(
                new SettingItem(R.string.pos_server_ip,"")
        );
        mItemList.add(
                new SettingItem(R.string.pos_server_port, "")
        );
        mItemList.add(
                new SettingItem(R.string.use_shopping_bag,
                        getString(R.string.no_use_shopping_bag))
        );
        mItemList.add(
                new SettingItem(R.string.debug_pattern,
                        getString(R.string.debug_close))
        );
    }

    private void initUI() {
        mIvBack = findViewById(R.id.iv_back);
        mRvSetting = findViewById(R.id.rv_setting);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mRvSetting.setLayoutManager(lm);
        mAdapter = new SettingAdapter(this,mItemList);
        mRvSetting.setAdapter(mAdapter);
        // 首先显示提示"输入密码"的对话框
        showDialog();
    }

    private void showDialog() {
        // 初始化dialog配置
        View view = View.inflate(this, R.layout.view_pwd, null);
        final EditText etPwd = view.findViewById(R.id.et_pwd);
        final TextView tvErrHint = view.findViewById(R.id.tv_err_hint);
        final AlertDialog dialog = new AlertDialog.Builder(
                this)
                .setTitle(R.string.please_input_pwd)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(R.string.sure,null)
                .create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
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
                        dialog.dismiss();
                        mRvSetting.setVisibility(View.VISIBLE);
                    }
                });
    }
}
