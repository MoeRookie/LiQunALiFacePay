package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.ShoppingBagBean;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.adapter.ShoppingBag1Adapter;
import com.liqun.www.liqunalifacepay.ui.utils.SoftInputUtils;
import com.liqun.www.liqunalifacepay.ui.view.GlobalDialog;

import java.util.ArrayList;
import java.util.List;

public class ShoppingBagActivity extends AppCompatActivity {

    private static final String PRODUCT_NO = "product_no";
    private static final String PRICE = "price";
    private String mOperator;
    private RecyclerView mRvBags;
    private Button mBtnSave;
    private List<ShoppingBagBean> mBagList = new ArrayList<>();
    private LinearLayoutManager mlm;
    private ShoppingBag1Adapter mAdapter;
    private GlobalDialog mDialog;
    private TextView mTvTitle,mTvMessage;
    private EditText mEtContent;
    private ShoppingBagBean mBagBean;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, ShoppingBagActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_bag);
        initUI();
        initData();
        setListener();
    }

    private void initUI() {
        mRvBags = findViewById(R.id.rv_bags);
        mBtnSave = findViewById(R.id.btn_save);
    }

    private void initData() {
        String json = SpUtils.getString(
                getApplicationContext(),
                ConstantValue.SHOPPING_BAG_MSG,
                ""
        );
        if (!TextUtils.isEmpty(json)) {
            List<ShoppingBagBean> bagList =
                    JSONArray.parseArray(json, ShoppingBagBean.class);
            if (mBagList.size() > 0) {
                mBagList.clear();
            }
            mBagList.addAll(bagList);
        }else{
            mBagList.add(new ShoppingBagBean(
                    "小号购物袋", "2499997", "0.1", false));
            mBagList.add(new ShoppingBagBean(
                    "中号购物袋", "2499998", "0.2", false));
            mBagList.add(new ShoppingBagBean(
                    "大号购物袋", "2499999", "0.3", false));
            mBagList.add(new ShoppingBagBean(
                    "特大号购物袋", "2499958", "0.4", false));
            // 保存为json
            saveBagMsg();
        }
        // 显示购物袋信息
        mlm = new LinearLayoutManager(this);
        mAdapter = new ShoppingBag1Adapter(this, mBagList);
        mRvBags.setLayoutManager(mlm);
        mRvBags.setAdapter(mAdapter);
    }

    /**
     * 保存购物袋信息
     */
    private void saveBagMsg() {
        String content = JSONArray.toJSONString(mBagList);
        SpUtils.putString(
                getApplicationContext(),
                ConstantValue.SHOPPING_BAG_MSG,
                content
        );
    }

    private void setListener() {
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBagMsg();
                for (int i = 0; i < mBagList.size(); i++) {
                    L.e("BagBean" + (i + 1) + "=" + mBagList.get(i).toString());
                }
                finish();
            }
        });
        mAdapter.setOnItemCheckedChangeListener(new ShoppingBag1Adapter.OnItemCheckedChangeListener() {
            @Override
            public void onItemCheckedChanged(int i) {
                // 1.获取到当前的购物袋对象
                mBagBean = mBagList.get(i);
                // 2.设置其状态取反并刷新界面
                mBagBean.setSelected(!mBagBean.isSelected());
                mAdapter.notifyDataSetChanged();
            }
        });
        mAdapter.setOnItemBagCodeClickListener(new ShoppingBag1Adapter.OnItemBagCodeClickListener() {
            @Override
            public void onItemBagCodeClicked(int i) {
                // 1.获取到当前的购物袋对象
                mBagBean = mBagList.get(i);
                // 2.弹出修改当前购物袋编码对话框title=当前购物袋型号
                mOperator = PRODUCT_NO;
                showUpdateBagDialog();
            }
        });
        mAdapter.setOnItemBagPriceClickListener(new ShoppingBag1Adapter.OnItemBagPriceClickListener() {
            @Override
            public void onItemBagPriceClicked(int i) {
                // 1.获取到当前的购物袋对象
                mBagBean = mBagList.get(i);
                // 2.弹出修改当前购物袋价格对话框title=当前购物袋型号
                mOperator = PRICE;
                showUpdateBagDialog();
            }
        });
    }

    /**
     *
     * 显示更新购物袋信息的对话框
     */
    private void showUpdateBagDialog() {
        if (mDialog == null) {
            mDialog = new GlobalDialog(this);
            mDialog.setOnNoClickListener("取消", new GlobalDialog.OnNoClickListener() {
                @Override
                public void onNoClick() {
                    mDialog.dismiss();
                }
            });
            mDialog.setOnYesClickListener("确定", new GlobalDialog.OnYesClickListener() {
                @Override
                public void onYesClicked() {
                    String content = mEtContent.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        mTvMessage.setVisibility(View.VISIBLE);
                        mTvMessage.setText(R.string.content_null_err);
                        return;
                    }
                    if (PRODUCT_NO.equals(mOperator)) {
                        mBagBean.setProductNo(content);
                    } else if (PRICE.equals(mOperator)) {
                        mBagBean.setPrice(content);
                    }
                    mAdapter.notifyDataSetChanged();
                    mDialog.dismiss();
                }
            });
        }
        mDialog.show();
        if (mTvTitle == null) {
            mTvTitle = mDialog.findViewById(R.id.tv_title);
        }
        mTvTitle.setText(mBagBean.getType());
        if (mTvMessage == null) {
            mTvMessage = mDialog.findViewById(R.id.tv_message);
        }
        mTvMessage.setVisibility(View.GONE);
        if (mEtContent == null) {
            mEtContent = mDialog.findViewById(R.id.et_content);
        }
        // 选中所有内容并弹出键盘
        mEtContent.setText(PRODUCT_NO.equals(mOperator)?mBagBean.getProductNo():mBagBean.getPrice());
        // 设置对话框内容全部选中
        mEtContent.selectAll();
        // 弹出键盘
        SoftInputUtils.openSoftInput(this);
    }
}
