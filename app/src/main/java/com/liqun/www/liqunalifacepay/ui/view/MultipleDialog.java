package com.liqun.www.liqunalifacepay.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

/**
 * 自定义 dialog
 */

public class MultipleDialog extends Dialog {
    private Button btnYes, btnNo;
    private TextView tvTitle;
    private RecyclerView rvMultiple;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    // 从外界设置的 title 文本
    private String strTitle;
    // 确定文本和取消文本的显示内容
    private String strYes, strNo;

    // 取消按钮被点击了的监听器
    private OnNoClickListener onNoClickListener;
    // 确定按钮被点击了的监听器
    private OnYesClickListener onYesClickListener;

    /** 设置取消按钮的显示内容和监听 */
    public void setOnNoClickListener(String str, OnNoClickListener OnNoClickListener) {
        if (str != null) {
            strNo = str;
        }
        this.onNoClickListener = OnNoClickListener;
    }

    /** 设置确定按钮的显示内容和监听 */
    public void setOnYesClickListener(String str, OnYesClickListener OnYesClickListener) {
        if (str != null) {
            strYes = str;
        }
        this.onYesClickListener = OnYesClickListener;
    }

    public MultipleDialog(Context context) {
        super(context, R.style.GlobalDialogStyle);
        layoutManager = new LinearLayoutManager(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_global);
        // 按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        // 初始化界面控件
        initView();
        // 初始化界面数据
        initData();
        // 初始化界面控件的事件
        initEvent();
    }
    /** 初始化界面控件 */
    private void initView() {
        btnYes = findViewById(R.id.btn_yes);
        btnNo = findViewById(R.id.btn_no);
        tvTitle = findViewById(R.id.tv_title);
        rvMultiple = findViewById(R.id.rv_multiple);
    }
    /** 初始化界面控件的显示数据 */
    private void initData() {
        // 如果用户自定了 title 和 message
        if (strTitle != null) {
            tvTitle.setText(strTitle);
        }
        // 如果设置按钮的文字
        if (strYes != null) {
            btnYes.setText(strYes);
        }
        if (strNo != null) {
            btnNo.setText(strNo);
        }
        // 如果用户设置rv的Adapter
        rvMultiple.setLayoutManager(layoutManager);
        if (adapter != null) {
            rvMultiple.setAdapter(adapter);
        }
    }
    /** 初始化界面的确定和取消监听器 */
    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYesClickListener != null) {
                    onYesClickListener.onYesClicked();
                }
            }
        });
        // 设置取消按钮被点击后，向外界提供监听
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNoClickListener != null) {
                    onNoClickListener.onNoClick();
                }
            }
        });
    }
    /** 从外界 Activity 为 Dialog 设置标题 */
    public void setTitle(String title) {
        strTitle = title;
    }
    /** 设置确定按钮被点击的接口 */
    public interface OnYesClickListener {
        void onYesClicked();
    }
    /** 设置取消按钮被点击的接口 */
    public interface OnNoClickListener {
        void onNoClick();
    }
    /** 从外界 Activity为Dialog设置adapter*/
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }
}
