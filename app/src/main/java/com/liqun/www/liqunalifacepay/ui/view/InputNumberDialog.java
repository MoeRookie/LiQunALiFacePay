package com.liqun.www.liqunalifacepay.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

/**
 * 自定义 dialog
 */

public class InputNumberDialog extends Dialog {

    private static final int MAX_LENGTH = 20;
    // 加载布局的资源id
    private int layoutResId;
    private ImageView ivClear;
    private NumberKeyboardView nkv;
    private EditText etNumber;

    public void setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
    }

    // 从外界设置的title文本
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    private TextView tvTitle;
    private Button btnYes, btnNo;

    // 确定按钮被点击了的监听器
    private OnYesClickListener onYesClickListener;

    /** 设置确定按钮的显示内容和监听 */
    public void setOnYesClickListener(OnYesClickListener OnYesClickListener) {
        this.onYesClickListener = OnYesClickListener;
    }

    public InputNumberDialog(Context context) {
        super(context, R.style.GlobalDialogStyle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
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
        // 共有的
        btnNo = findViewById(R.id.btn_no);
        etNumber = findViewById(R.id.et_number);
        // 设置光标不显示
        etNumber.setCursorVisible(false);
        // 设置不可编辑
        etNumber.setFocusable(false);
        etNumber.setFocusableInTouchMode(false);
        nkv = findViewById(R.id.nkv);
        btnYes = findViewById(R.id.btn_yes);
        // 如果是输入手机号界面
        if (layoutResId == R.layout.dialog_input_phone_num) {
            tvTitle = findViewById(R.id.tv_title);
            ivClear = findViewById(R.id.iv_clear);
        }
    }

    private void initData() {
        if (title != null) {
            tvTitle.setText(title);
        }
    }
    /** 初始化界面的确定和取消监听器 */
    private void initEvent() {
        // 取消按钮被点击后,关闭当前对话框
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        nkv.setIOnKeyboardListener(new NumberKeyboardView.IOnKeyboardListener() {
            @Override
            public void onInsertKeyEvent(String text) {
                if (etNumber.getText().length() <= MAX_LENGTH) {
                    etNumber.append(text);
                }
            }
            @Override
            public void onDeleteKeyEvent() {
                int start = etNumber.length() - 1;
                if (start >= 0) {
                    etNumber.getText().delete(start, start + 1);
                }
            }
        });
        // 确定按钮被点击后，向外界提供监听
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onYesClickListener != null) {
                    onYesClickListener.onYesClicked();
                }
            }
        });
        // 如果是输入手机号界面
        if (layoutResId == R.layout.dialog_input_phone_num) {
            ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etNumber.setText("");
                }
            });
        }
    }
    /** 设置确定按钮被点击的接口 */
    public interface OnYesClickListener {
        void onYesClicked();
    }
}
