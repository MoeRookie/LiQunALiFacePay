package com.liqun.www.liqunalifacepay.application;

import android.app.Application;

public class ALiFacePayApplication extends Application {
    private static ALiFacePayApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ALiFacePayApplication getInstance() {
        return instance;
    }
}
