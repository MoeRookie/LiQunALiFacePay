package com.liqun.www.liqunalifacepay.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

public class WarnDialog extends Dialog {
    private TextView mTvMessage;
    private Button mBtnConfirm;

    // 从外界设置的消息文本
    private String strMessage;
    // 确定按钮被点击了的监听器
    private OnConfirmClickListener onConfirmClickListener;
    /** 设置确定按钮的显示内容和监听 */
    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public WarnDialog(Context context) {
        super(context, R.style.GlobalDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_warn);
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
        mTvMessage = findViewById(R.id.tv_message);
        mBtnConfirm = findViewById(R.id.btn_confirm);
    }
    /** 初始化界面控件的显示数据 */
    private void initData() {
        // 如果用户自定义message
        if (strMessage != null) {
            mTvMessage.setText(strMessage);
        }
    }
    /** 初始化界面的确定和取消监听器 */
    private void initEvent() {
        // 设置确定按钮被点击后，向外界提供监听
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onConfirmClickListener != null) {
                    onConfirmClickListener.onConfirmClicked();
                }
            }
        });
    }
    /** 从外界 Activity 为 Dialog 设置 dialog 的 message */
    public void setMessage(String message) {
        strMessage = message;
    }
    /** 设置确定按钮被点击的接口 */
    public interface OnConfirmClickListener {
        void onConfirmClicked();
    }
}
