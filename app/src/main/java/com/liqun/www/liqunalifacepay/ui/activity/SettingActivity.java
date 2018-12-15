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
import com.liqun.www.liqunalifacepay.data.bean.SettingItemBean;
import com.liqun.www.liqunalifacepay.data.utils.SpUtils;
import com.liqun.www.liqunalifacepay.ui.adapter.SettingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private ImageView mIvBack;
    private RecyclerView mRvSetting;
    private List<SettingItemBean> mItemList;
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
        mAdapter.setOnItemClickListener(new SettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    /**
                     * 若点击了"门店名称". . ."Pos后台IP端口"
                     * _>修改以其标题作为title的弹框:
                     *      显示之前保存过的"门店名称",若首次设置则显示为空(hint="请输入 . . .")
                     *      点击"确认"按钮:
                     *          若编辑框中没有任何内容,则直接提示"内容不能为空！"
                     *          否则获取到编辑框中的内容、关闭对话框、设置给对应的列表项内容并刷新保存;
                     */
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        showEditContentDialog(position);
                        break;
                    case 8:
                        /**
                         *
                         */
//                        showSelectShoppingBagDialog(position);
                        break;
                }
            }
        });
    }

    /**
     * _>修改以其标题作为title的弹框
     * @param position 列表项的索引值
     */
    private void showEditContentDialog(final int position) {
        final SettingItemBean item = mItemList.get(position);
        String title = getString(item.getTitleId());
        String content = item.getContent();
        // 初始化dialog配置
        View view = View.inflate(this, R.layout.view_item_content, null);
        final EditText etContent = view.findViewById(R.id.et_content);
        etContent.selectAll();
        // 显示之前保存过的"门店名称"
        etContent.setText(content);
        final TextView tvErrHint = view.findViewById(R.id.tv_err_hint);
        final AlertDialog dialog = new AlertDialog.Builder(
                this)
                .setTitle(title)
                .setView(view)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                        String content = etContent.getText().toString().trim();
                        if (TextUtils.isEmpty(content)) {
                            tvErrHint.setText(R.string.content_null_err);
                            return;
                        }
                        item.setContent(content);
                        mAdapter.notifyDataSetChanged();
                        saveContent(item,position);
                        dialog.dismiss();
                    }
                });
    }

    private void saveContent(SettingItemBean item, int position) {
        switch (position) {
            case 2:
                SpUtils.putString(
                        this,
                        ConstantValue.SHOP_NAME,
                        item.getContent()
                );
                break;
            case 3:
                SpUtils.putString(
                        this,
                        ConstantValue.SHOP_NO,
                        item.getContent()
                );
                break;
            case 4:
                SpUtils.putString(
                        this,
                        ConstantValue.SHOP_MERCHANT_NO,
                        item.getContent()
                );
                break;
            case 5:
                SpUtils.putString(
                        this,
                        ConstantValue.CATWALK_NO,
                        item.getContent()
                );
                break;
            case 6:
                SpUtils.putString(
                        this,
                        ConstantValue.POS_SERVER_IP,
                        item.getContent()
                );
                break;
            case 7:
                SpUtils.putString(
                        this,
                        ConstantValue.POS_SERVER_PORT,
                        item.getContent()
                );
                break;
        }
    }

    private void initData() {
        mItemList = new ArrayList<>();
        mItemList.add(new SettingItemBean(
                R.string.self_gathering_ip,ALiFacePayApplication.getInstance().getHostIP()));
        mItemList.add(new SettingItemBean(
            R.string.self_gathering_port,ConstantValue.SELF_GATHERING_PORT
        ));
        mItemList.add(
                new SettingItemBean(R.string.shop_name,
                        SpUtils.getString(
                                this,
                                ConstantValue.SHOP_NAME,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.shop_no,
                        SpUtils.getString(
                                this,
                                ConstantValue.SHOP_NO,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.shop_merchant_no,
                        SpUtils.getString(
                                this,
                                ConstantValue.SHOP_MERCHANT_NO,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.catwalk_no,
                        SpUtils.getString(
                                this,
                                ConstantValue.CATWALK_NO,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.pos_server_ip,
                        SpUtils.getString(
                                this,
                                ConstantValue.POS_SERVER_IP,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.pos_server_port,
                        SpUtils.getString(
                                this,
                                ConstantValue.POS_SERVER_PORT,
                                ""
                        ))
        );
        mItemList.add(
                new SettingItemBean(R.string.use_shopping_bag,
                        getString(R.string.no_use_shopping_bag))
        );
        mItemList.add(
                new SettingItemBean(R.string.debug_pattern,
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
