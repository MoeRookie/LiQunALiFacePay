package com.liqun.www.liqunalifacepay.ui.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.liqun.www.liqunalifacepay.R;

public class LoadingDialog extends ProgressDialog {

    private TextView mTvLoadHint;

    public LoadingDialog(Context context) {
        super(context);
    }
    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }
    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading);// loading的xml文件
        initView();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    private void initView() {
        mTvLoadHint = findViewById(R.id.tv_load_hint);
    }

    public void setMessage(String msg){
        if (!TextUtils.isEmpty(msg)) {
            mTvLoadHint.setText(msg);
        }
    }
    public void setMessage(int msgResId){
        String msg = getContext().getString(msgResId);
        if (!TextUtils.isEmpty(msg)) {
            mTvLoadHint.setText(msg);
        }
    }
}
