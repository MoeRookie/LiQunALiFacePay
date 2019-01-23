package com.liqun.www.liqunalifacepay.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

public class SoftInputUtils {
    /**
     * 隐藏软键盘
     * @param activity
     */
    public static void closedSoftInput(Activity activity){
        if(null!=activity && activity.getCurrentFocus()!=null){
            if(null!=activity.getWindow()){
                activity.getWindow().getDecorView().clearFocus();
                InputMethodManager im = ((InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                if(null!=im){
                    im.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    /**
     * 打开软键盘
     * @param activity
     */
    public static void openSoftInput(final Activity activity){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 300);
    }
}
