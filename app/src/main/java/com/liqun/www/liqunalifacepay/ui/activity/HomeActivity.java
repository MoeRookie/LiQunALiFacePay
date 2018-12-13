package com.liqun.www.liqunalifacepay.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.liqun.www.liqunalifacepay.R;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Display mDefaultDisplay;
    private ImageView mIvHead;
    private Button mBtnVip;
    private Button mBtnNoVip;
    private long[] mHits = new long[5]; // 设置多击时需要的点击次数
    private ImageView mIvLiQun;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        initListener();
    }

    private void initListener() {
        mIvLiQun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length - 1] - mHits[0] < 1000) {
                    // 满足"五击事件"后,跳转"日结"界面
                    Intent intent = DayEndActivity.newIntent(HomeActivity.this);
                    startActivity(intent);
                }
            }
        });
    }

    private void initUI() {
        mDefaultDisplay = getWindowManager().getDefaultDisplay();
        // 适配home界面的head图片
        mIvHead = findViewById(R.id.iv_head);
        setImageDisplay(mIvHead,898,1920);
        mIvLiQun = findViewById(R.id.iv_liqun);
    }

    /**
     * 根据图片宽高比适配其显示
     * @param iv 显示图片的view
     * @param width 图片的宽
     * @param height 图片的高
     */
    private void setImageDisplay(ImageView iv, int width, int height) {
        LayoutParams layoutParams = (LayoutParams) mIvHead.getLayoutParams();
        layoutParams.width = mDefaultDisplay.getWidth();
        layoutParams.height = layoutParams.width * width / height;
        iv.setLayoutParams(layoutParams);
    }
}
