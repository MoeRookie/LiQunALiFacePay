package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.opengl.ETC1;
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
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.adapter.SettingAdapter;
import com.liqun.www.liqunalifacepay.ui.view.GlobalDialog;

import java.util.ArrayList;
import java.util.List;

/**
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
 */
public class SettingActivity extends AppCompatActivity {
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
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInstanceState();
                finish();
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
                        // 弹出以列表项标题为title、内容为content的普通对话框
                        // 取消 -> 关闭对话框
                        // 确定:
                        // 内容不能为空
                        // ->正确的内容\>确定(关闭对话框、更新当前列表项内容、刷新界面显示)
                        showUpdateDialog(mItemBean.getTitle(),mItemBean.getContent());
                        break;
                    case 8:
                        break;
                }
            }
        });
    }

    /**
     * 保存当前界面内容
     */
    private void saveInstanceState() {
        String content = JSONArray.toJSONString(mItemList);
        // 保存当前界面内容
        SpUtils.putString(
                getApplicationContext(),
                ConstantValue.KEY_SETTING_CONTENT,
                content
        );
    }

    /**
     * 弹出修改&更新内容的对话框
     * @param title 列表项标题
     * @param content 列表项内容
     */
    private void showUpdateDialog(String  title, String content) {
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
        // 设置对话框标题&内容
        setUpdateDialogDisplay(title,content);
    }

    /**
     * 设置对话框标题&内容
     * @param title 列表项标题
     * @param content 列表项内容
     */
    private void setUpdateDialogDisplay(String title, String content) {
        tvTitle.setText(title);
        etContent.setText(content);
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
                mUpdateDialog.dismiss();
            }
        });
        mUpdateDialog.setOnYesClickListener(mYesStr, new GlobalDialog.OnYesClickListener() {
            @Override
            public void onYesClicked() {
                // 获取到编辑框中的内容(空->显示tvMsg并设置错误内容)
                String content = etContent.getText().toString().trim();
                if ("".equals(content)) {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText(R.string.hint_content_null_err);
                    return;
                }
                tvMessage.setVisibility(View.GONE);
                // 否则关闭对话框、更新当前列表项内容、刷新界面显示
                mUpdateDialog.dismiss();
                mItemBean.setContent(content);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
//
//    /**
//     * _>修改以其标题作为title的弹框
//     * @param position 列表项的索引值
//     */
//    private void showEditContentDialog(final int position) {
//        final SettingItemBean item = mItemList.get(position);
//        String title = getString(item.getTitleId());
//        String content = item.getContent();
//        // 初始化dialog配置
//        View view = View.inflate(this, R.layout.view_item_content, null);
//        final EditText etContent = view.findViewById(R.id.et_content);
//        etContent.selectAll();
//        // 显示之前保存过的"门店名称"
//        etContent.setText(content);
//        final TextView tvErrHint = view.findViewById(R.id.tv_err_hint);
//        final AlertDialog dialog = new AlertDialog.Builder(
//                this)
//                .setTitle(title)
//                .setView(view)
//                .setCancelable(true)
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setPositiveButton(R.string.sure,null)
//                .create();
//        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String content = etContent.getText().toString().trim();
//                        if (TextUtils.isEmpty(content)) {
//                            tvErrHint.setText(R.string.content_null_err);
//                            return;
//                        }
//                        item.setContent(content);
//                        mAdapter.notifyDataSetChanged();
//                        saveContent(item,position);
//                        dialog.dismiss();
//                    }
//                });
//    }
//
//    private void saveContent(SettingItemBean item, int position) {
//        switch (position) {
//            case 2:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.SHOP_NAME,
//                        item.getContent()
//                );
//                break;
//            case 3:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.SHOP_NO,
//                        item.getContent()
//                );
//                break;
//            case 4:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.SHOP_MERCHANT_NO,
//                        item.getContent()
//                );
//                break;
//            case 5:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.CATWALK_NO,
//                        item.getContent()
//                );
//                break;
//            case 6:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.POS_SERVER_IP,
//                        item.getContent()
//                );
//                break;
//            case 7:
//                SpUtils.putString(
//                        this,
//                        ConstantValue.POS_SERVER_PORT,
//                        item.getContent()
//                );
//                break;
//        }
//    }

    private void initData() {
        // 从SP中读取要显示的列表内容
        String json = SpUtils.getString(
                getApplicationContext(),
                ConstantValue.KEY_SETTING_CONTENT,
                ""
        );
        if (!"".equals(json)) {
            // json字符串->settingItemBean集合
            List<SettingItemBean> itemList = JSONArray.parseArray(json, SettingItemBean.class);
            if (mItemList.size() > 0) {
                mItemList.clear();
                mItemList.addAll(itemList);
            }else{
                mItemList.addAll(itemList);
            }
        }else{
            String hostIp = ALiFacePayApplication.getInstance().getHostIP();
            String hostPort = ConstantValue.SELF_GATHERING_PORT;
            String noUseShoppingBag = getString(R.string.content_no_use_shopping_bag);
            String debugPatternClosed = getString(R.string.content_debug_closed);
            //
            mItemList.add(new SettingItemBean("自助收银IP地址",hostIp));
            // 自助收银IP端口
            mItemList.add(new SettingItemBean("自助收银IP端口",hostPort));
            // 门店名称
            mItemList.add(new SettingItemBean("门店名称","诺德"));
            // 门店编码
            mItemList.add(new SettingItemBean("门店编码",""));
            // 门店商户号
            mItemList.add(new SettingItemBean("门店商户号",""));
            // 款台号
            mItemList.add(new SettingItemBean("款台号",""));
            // Pos后台IP端口
            mItemList.add(new SettingItemBean("Pos后台IP端口",""));
            // Pos后台IP地址
            mItemList.add(new SettingItemBean("Pos后台IP地址",""));
            // 启用购物袋
            mItemList.add(new SettingItemBean("启用购物袋",noUseShoppingBag));
            // 调试模式
            mItemList.add(new SettingItemBean("调试模式",debugPatternClosed));
            // 保存为json
            saveInstanceState();
        }
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
        showConfirmDialog();
    }

    /**
     * 弹出确认密码弹框
     */
    private void showConfirmDialog() {
        if (mConfirmDialog == null) {
            // 使用自定义dialog可以避免因使用了头条的适配方式而导致原生dialog适配出错的问题
            mConfirmDialog = new GlobalDialog(this);
            // 设置对话框标题
            String title = getString(R.string.input_pwd);
            mConfirmDialog.setTitle(title);
            // 设置输入的文本类型
            mConfirmDialog.setEtInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            setConfirmDialogListener();
        }
        // 显示对话框
        mConfirmDialog.show();
        if (mConfirmDialog.isShowing()) {
            mTvMessage = mConfirmDialog.findViewById(R.id.tv_message);
            mEtPwd = mConfirmDialog.findViewById(R.id.et_content);
            // 设置弹出对话框时显示错误消息的tv不显示且不占空间
            mTvMessage.setVisibility(View.GONE);
        }
    }

    /**
     * 监听输入密码对话框取消&确定按钮被点击
     */
    private void setConfirmDialogListener() {
        mNoStr = getString(R.string.cancel);
        mYesStr = getString(R.string.sure);
        mConfirmDialog.setOnNoClickListener(mNoStr, new GlobalDialog.OnNoClickListener() {
            @Override
            public void onNoClick() {
                if (mConfirmDialog.isShowing()) {
                    mConfirmDialog.dismiss();
                }
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
                if (!ConstantValue.VALUE_SETTING_PWD.equals(pwd)) {
                    setErrorMsgLayout(true,R.string.pwd_err);
                    mEtPwd.setText("");
                    return;
                }
                setErrorMsgLayout(false,R.string.empty);
                // 隐藏msg控件&关掉对话框&发起日结请求
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
}
