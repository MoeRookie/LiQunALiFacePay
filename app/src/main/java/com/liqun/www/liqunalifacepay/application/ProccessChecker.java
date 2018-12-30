package com.liqun.www.liqunalifacepay.application;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

public class ProccessChecker {
    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public static boolean isAppMainProcess(Context context, String mainProccessName) {
        try {
            int pid = android.os.Process.myPid();
            String process = getAppNameByPID(context, pid);
            if (TextUtils.isEmpty(process)) {
                return true;
            } else if (mainProccessName.equalsIgnoreCase(process)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 根据Pid得到进程名
     */
    public static String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }
}