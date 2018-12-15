package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItem;
import com.liqun.www.liqunalifacepay.ui.adapter.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

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
        mRvSetting = findViewById(R.id.rv_setting);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mRvSetting.setLayoutManager(lm);
        mAdapter = new SettingAdapter(this,mItemList);
        mRvSetting.setAdapter(mAdapter);
    }
}
