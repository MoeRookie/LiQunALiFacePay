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

public class InputBarCodeDialog extends Dialog {
    private Button btnYes, btnNo;

    // 从外界设置的消息文本
    private String strMessage;

    // 取消按钮被点击了的监听器
    private OnNoClickListener onNoClickListener;
    // 确定按钮被点击了的监听器
    private OnYesClickListener onYesClickListener;

    /** 设置取消按钮的显示内容和监听 */
    public void setOnNoClickListener(OnNoClickListener OnNoClickListener) {
        this.onNoClickListener = OnNoClickListener;
    }

    /** 设置确定按钮的显示内容和监听 */
    public void setOnYesClickListener(OnYesClickListener OnYesClickListener) {
        this.onYesClickListener = OnYesClickListener;
    }

    public InputBarCodeDialog(Context context) {
        super(context, R.style.GlobalDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_bar_code);
        // 按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        // 初始化界面控件
        initView();
        // 初始化界面控件的事件
        initEvent();
    }
    /** 初始化界面控件 */
    private void initView() {
        btnYes = findViewById(R.id.btn_yes);
        btnNo = findViewById(R.id.btn_no);
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
    /** 设置确定按钮被点击的接口 */
    public interface OnYesClickListener {
        void onYesClicked();
    }
    /** 设置取消按钮被点击的接口 */
    public interface OnNoClickListener {
        void onNoClick();
    }
}
