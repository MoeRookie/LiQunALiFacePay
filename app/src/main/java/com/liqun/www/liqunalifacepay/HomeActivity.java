package com.liqun.www.liqunalifacepay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Display mDefaultDisplay;
    private ImageView mIvHead;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
    }

    private void initUI() {
        mDefaultDisplay = getWindowManager().getDefaultDisplay();
        // 适配home界面的head图片
        mIvHead = findViewById(R.id.iv_head);
        setImageDisplay(mIvHead,898,1920);
    }

    /**
     * 根据图片宽高比适配其显示
     * @param ivImage 显示图片的iv
     * @param width 图片的宽
     * @param height 图片的高
     */
    private void setImageDisplay(ImageView ivImage, int width, int height) {
        LayoutParams layoutParams = (LayoutParams) mIvHead.getLayoutParams();
        layoutParams.width = mDefaultDisplay.getWidth();
        layoutParams.height = layoutParams.width * 898 / 1920;
        mIvHead.setLayoutParams(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
