package com.liqun.www.liqunalifacepay.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

/**
 * 自定义 dialog
 */

public class PwdDialog extends Dialog {
    private Button btnYes, btnNo;
    private TextView tvTitle, tvMessage;
    private EditText etEnter;

    // 从外界设置的 title 文本、消息文本、提示文本
    private String strTitle, strMessage, strHint;
    // 从外界设置的输入类型
    private int typeInput = -1;
    // 确定文本和取消文本的显示内容
    private String strYes, strNo;

    // 取消按钮被点击了的监听器
    private onNoOnclickListener noOnclickListener;
    // 确定按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;

    /** 设置取消按钮的显示内容和监听 */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            strNo = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /** 设置确定按钮的显示内容和监听 */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            strYes = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public PwdDialog(Context context) {
        super(context, R.style.PwdDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pwd);
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
        tvMessage = findViewById(R.id.tv_message);
        etEnter = findViewById(R.id.et_pwd);
    }
    /** 初始化界面控件的显示数据 */
    private void initData() {
        // 如果用户自定了 title 和 message
        if (strTitle != null) {
            tvTitle.setText(strTitle);
        }
        if (strMessage != null) {
            tvMessage.setText(strMessage);
        }
        if (strHint != null) {
            etEnter.setHint(strHint);
        }
        if (typeInput != -1) {
            etEnter.setInputType(typeInput);
        }
        // 如果设置按钮的文字
        if (strYes != null) {
            btnYes.setText(strYes);
        }
        if (strNo != null) {
            btnNo.setText(strNo);
        }
    }
    public String getEditTextStr() {
        return etEnter.getText().toString().trim();
    }
    /** 初始化界面的确定和取消监听器 */
    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        // 设置取消按钮被点击后，向外界提供监听
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
    }
    /** 从外界 Activity 为 Dialog 设置标题 */
    public void setTitle(String title) {
        strTitle = title;
    }
    /** 从外界 Activity 为 Dialog 设置 dialog 的 message */
    public void setMessage(String message) {
        strMessage = message;
    }
    /** 从外界 Activity 为 Dialog 设置 EditText 的 hint */
    public void setHint(String hint) {
        strHint = hint;
    }
    /** 设置编辑输入类型 */
    public void setEtInputType(int type) {
        typeInput = type;
    }
    /** 设置确定按钮被点击的接口 */
    public interface onYesOnclickListener {
        void onYesClick();
    }
    /** 设置取消按钮被点击的接口 */
    public interface onNoOnclickListener {
        void onNoClick();
    }
}
