package com.liqun.www.liqunalifacepay.data.utils;

import android.content.Context;
import android.widget.Toast;

import com.liqun.www.liqunalifacepay.application.ALiFacePayApplication;

public class CommonUtils {
    private static Toast toast;

    public static void showLongToast(String msg){
        showToast(ALiFacePayApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
//        Toast.makeText(ALiFacePayApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
    }
    public static void showShortToast(String msg){
        showToast(ALiFacePayApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
//        Toast.makeText(ALiFacePayApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
    }
    public static void showLongToast(int rId){
        showLongToast(ALiFacePayApplication.getInstance().getString(rId));
    }
    public static void showShortToast(int rId){
        showShortToast(ALiFacePayApplication.getInstance().getString(rId));
    }

    public static Toast showToast(Context context, String msg, int length){
        if (toast==null){
            toast = Toast.makeText(context,msg,length);
        }else{
            toast.setText(msg);
        }
        return toast;
    }
}
