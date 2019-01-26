package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.liqun.www.liqunalifacepay.R;
import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;
import com.liqun.www.liqunalifacepay.application.ConstantValue;
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;
import com.liqun.www.liqunalifacepay.data.bean.ShoppingBagBean;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.adapter.SettingAdapter;
import com.liqun.www.liqunalifacepay.ui.adapter.ShoppingBag1Adapter;
import com.liqun.www.liqunalifacepay.ui.utils.SoftInputUtils;
import com.liqun.www.liqunalifacepay.ui.view.GlobalDialog;
import com.liqun.www.liqunalifacepay.ui.view.MultipleDialog;
import com.liqun.www.liqunalifacepay.ui.view.WarnDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Day1119
 * 1.从SP中读取要显示的列表内容(初始化->onCreate(...))
 *                      √
 *                          json->jsonArr->list
 *  读(json,初始化list)
 *                      ×
 *                          初始化list
 *                              new ItemBean("标题","内容");
 *                              ...
 * 2.把读取到的内容显示在rv上
 * 3.根据列表项的分类,处理每一个条目的逻辑
 * 4.点击确定按钮,即写入|保存列表内容
 * Day1120
 * 1.点击"确定"保存时弹出warn对话框,点击对话框中的确定按钮才返回到上级界面;
 * 2.点击"启用购物袋"读取内容并展示在对话框中;
 * 3.分析并开始"非会员结账"流程;
 */
public class SettingActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_BAG = 0;
    private TextView mTvBack;
    private TextView mTvTitle;
    private TextView mTvSure;
    private RecyclerView mRvSetting;
    private LinearLayoutManager mLayoutManager;
    private List<SettingItemBean> mItemList = new ArrayList<>();
    private SettingAdapter mAdapter;
    private String mNoStr;
    private String mYesStr;
    private GlobalDialog mConfirmDialog;
    private GlobalDialog mUpdateDialog;
    private TextView mTvMessage;
    private EditText mEtPwd;
    private TextView tvTitle;
    private EditText etContent;
    private TextView tvMessage;
    private SettingItemBean mItemBean;
    private WarnDialog mWarnDialog;

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

    /**
     * 初始化要显示的数据
     */
    private void initData() {
        // 1.从SP中读取要显示的列表内容
        String json = SpUtils.getString(
                getApplicationContext(),
                ConstantValue.SETTING_CONTENT,
                ""
        );
        if (!TextUtils.isEmpty(json)) {
            // json字符串->settingItemBean集合
            List<SettingItemBean> itemList = JSONArray.parseArray(json, SettingItemBean.class);
            if (mItemList.size() > 0) {
                mItemList.clear();
            }
            mItemList.addAll(itemList);
        }else{
            String hostIp = ALiFacePayApplication.getInstance().getHostIP();
            String hostPort = ConstantValue.SELF_GATHERING_PORT;
            String noUseShoppingBag = getString(R.string.content_no_use_shopping_bag);
            // 自助收银IP地址
            mItemList.add(new SettingItemBean("自助收银IP地址",hostIp));
            // 自助收银IP端口
            mItemList.add(new SettingItemBean("自助收银IP端口",hostPort));
            // 门店名称
            mItemList.add(new SettingItemBean("门店名称",""));
            // 门店编码
            mItemList.add(new SettingItemBean("门店编码",""));
            // 门店商户号
            mItemList.add(new SettingItemBean("门店商户号",""));
            // 款台号
            mItemList.add(new SettingItemBean("款台号",""));
            // Pos后台IP地址
            mItemList.add(new SettingItemBean("Pos后台IP地址",""));
            // Pos后台IP端口
            mItemList.add(new SettingItemBean("Pos后台IP端口",""));
            // 启用购物袋
            mItemList.add(new SettingItemBean("启用购物袋",noUseShoppingBag));
            // 收款员
            mItemList.add(new SettingItemBean("收款员","90001"));
            // 保存为json
            saveSettingMsg();
        }
    }

    /**
     * 保存当前界面内容
     */
    private void saveSettingMsg() {
        String content = JSONArray.toJSONString(mItemList);
        // 保存当前界面内容
        SpUtils.putString(
                getApplicationContext(),
                ConstantValue.SETTING_CONTENT,
                content
        );
    }

    private void initUI() {
        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);
        // 设置此页标题
        mTvTitle.setText(R.string.title_setting);
        mTvSure = findViewById(R.id.tv_sure);
        // 把读取到的内容显示在rv上
        // fbi->rv(layoutManager、adapter)
        mRvSetting = findViewById(R.id.rv_setting);
        mLayoutManager = new LinearLayoutManager(this);
        mRvSetting.setLayoutManager(mLayoutManager);
        mAdapter = new SettingAdapter(this,mItemList);
        mRvSetting.setAdapter(mAdapter);
        // 首先显示提示"输入密码"的对话框
        showPwdDialog();
    }

    /**
     * 显示确认密码的对话框
     * 使用自定义dialog可以避免因使用了头条的适配方式而导致原生dialog适配出错的问题
     */
    private void showPwdDialog() {
        if (mConfirmDialog == null) {
            mConfirmDialog = new GlobalDialog(this);
            // 设置对话框标题
            mConfirmDialog.setTitle(getString(R.string.input_pwd));
            // 设置输入的文本类型
            mConfirmDialog.setEtInputType(
                    InputType.TYPE_CLASS_NUMBER
                            |InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            setPwdDialogListener();
        }
        // 显示对话框
        mConfirmDialog.show();
        if (mTvMessage == null) {
            mTvMessage = mConfirmDialog.findViewById(R.id.tv_message);
        }
        if (mEtPwd == null) {
            mEtPwd = mConfirmDialog.findViewById(R.id.et_content);
        }
    }
    /**
     * 监听输入密码对话框取消&确定按钮被点击
     */
    private void setPwdDialogListener() {
        mNoStr = getString(R.string.cancel);
        mYesStr = getString(R.string.sure);
        mConfirmDialog.setOnNoClickListener(mNoStr, new GlobalDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                mConfirmDialog.dismiss();
                finish();
            }
        });
        mConfirmDialog.setOnYesClickListener(mYesStr, new GlobalDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                String pwd = mEtPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    setErrorMsgLayout(true,R.string.pwd_not_null);
                    return;
                }
                if (!ConstantValue.SETTING_PWD.equals(pwd)) {
                    setErrorMsgLayout(true,R.string.pwd_err);
                    mEtPwd.setText("");
                    return;
                }
                // 隐藏msg控件
                setErrorMsgLayout(false,R.string.empty);
                // 关掉对话框
                mConfirmDialog.dismiss();
                // 显示设置内容列表
                mRvSetting.setVisibility(View.VISIBLE);
            }
        });
    }
    /**
     * 设置报错后的对话框布局
     * @param isError 是否报错
     * @param errStrId 报错提示字符串id
     */
    private void setErrorMsgLayout(boolean isError, int errStrId) {
        // 设置msg控件显示&设置显示内容&中断流程
        mTvMessage.setVisibility(isError?View.VISIBLE:View.GONE);
        mTvMessage.setText(errStrId);
    }
    private void setListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击"确定"保存后弹出warn对话框,点击对话框中的确定按钮才返回到上级界面;
                saveSettingMsg();
                showWarnDialog();
            }
        });
        mAdapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 获取当前列表项
                mItemBean = mItemList.get(position);
                switch (position) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 9:
                        // 弹出以列表项标题为title、内容为content的普通对话框
                        // 取消 -> 关闭对话框
                        // 确定:
                        // 内容不能为空
                        // ->正确的内容\>确定(关闭对话框、更新当前列表项内容、刷新界面显示)
                        // 弹出键盘
                        SoftInputUtils.openSoftInput(SettingActivity.this);
                        showUpdateDialog(mItemBean);
                        break;
                    case 8:
                        // 1.点击"启用购物袋"跳转购物袋信息列表界面
                        Intent intent = ShoppingBagActivity.newIntent(SettingActivity.this);
                        startActivityForResult(intent, REQUEST_CODE_BAG);
                        break;
                }
            }
        });
    }


    /**
     * 弹出修改&更新内容的对话框
     * @param itemBean  当前列表项
     */
    private void showUpdateDialog(SettingItemBean itemBean) {
        if (mUpdateDialog == null) {
            mUpdateDialog = new GlobalDialog(this);
            // 设置取消&确定按钮监听
            setUpdateDialogListener();
        }
        // 显示对话框
        mUpdateDialog.show();
        if(tvTitle == null){tvTitle = mUpdateDialog.findViewById(R.id.tv_title);}
        if (etContent == null) {etContent = mUpdateDialog.findViewById(R.id.et_content);}
        if (tvMessage == null) {tvMessage = mUpdateDialog.findViewById(R.id.tv_message);}
        tvMessage.setVisibility(View.GONE);
        // 设置对话框标题&内容
        setUpdateDialogDisplay(itemBean);
    }

    /**
     * 设置对话框标题&内容
     * @param itemBean  当前列表项
     */
    private void setUpdateDialogDisplay(SettingItemBean itemBean) {
        tvTitle.setText(itemBean.getTitle());
        etContent.setText(itemBean.getContent());
        // 设置对话框内容全部选中
        etContent.selectAll();
    }

    /**
     * 修改&更新内容对话框的按钮监听
     */
    private void setUpdateDialogListener() {
        mUpdateDialog.setOnNoClickListener(mNoStr, new GlobalDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                // 关闭键盘
                SoftInputUtils.closedSoftInput(SettingActivity.this);
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialog.setOnYesClickListener(mYesStr, new GlobalDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                // 获取到编辑框中的内容(空->显示tvMsg并设置错误内容)
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(R.string.hint_content_null_err);
                    return;
                }
                tvMessage.setVisibility(View.GONE);
                // 否则关闭对话框、更新当前列表项内容、刷新界面显示
                // 关闭键盘
                SoftInputUtils.closedSoftInput(SettingActivity.this);
                mUpdateDialog.dismiss();
                mItemBean.setContent(content);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 点击"确定"保存时弹出warn对话框,点击对话框中的确定按钮才返回到上级界面;
     */
    private void showWarnDialog() {
        if (mWarnDialog == null) {
            mWarnDialog = new WarnDialog(this);
            mWarnDialog.setMessage("设置保存成功!");
        }
        mWarnDialog.show();
        mWarnDialog.setOnConfirmClickListener(new WarnDialog.OnConfirmClickListener() {
            @Override
            public void onConfirmClicked() {
                if (mWarnDialog.isShowing()) {
                    mWarnDialog.dismiss();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BAG) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String bagMsg = data.getStringExtra(ShoppingBagActivity.EXTRA_BAG_MSG);
                    // 获取到购物袋信息列表项
                    SettingItemBean bagItem = mItemList.get(8);
                    // 设置购物袋信息
                    bagItem.setContent(bagMsg);
                    // 更新当前界面显示
                    mAdapter.notifyDataSetChanged();
                    // 保存当前界面信息
                    saveSettingMsg();
                }
            }
        }
    }
}
