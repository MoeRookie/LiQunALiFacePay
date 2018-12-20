package com.liqun.www.liqunalifacepay.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.liqun.www.liqunalifacepay.data.utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ALiFacePayApplication extends Application {
    private static ALiFacePayApplication instance;
    private String hostIP;
    private String shoppingBagMsg;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ALiFacePayApplication getInstance() {
        return instance;
    }
    /**
     * 获取ip地址
     * @return
     */
    public String getHostIP() {
        if (hostIP == null) {
            try {
                Enumeration nis = NetworkInterface.getNetworkInterfaces();
                InetAddress ia = null;
                while (nis.hasMoreElements()) {
                    NetworkInterface ni = (NetworkInterface) nis.nextElement();
                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {
                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        if (!"127.0.0.1".equals(ip)) {
                            hostIP = ia.getHostAddress();
                            break;
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return hostIP;
    }

    /**
     * 获取购物袋信息
     * @return 购物袋json
     */
    public String getShoppingBagMsg(){
        if (shoppingBagMsg == null) {
            // 1. 判断sd卡是否可用,是否挂载上
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File rootDir = Environment.getExternalStorageDirectory();
                File file = new File(rootDir,"pocket.txt");
                FileInputStream fis = null;
                InputStreamReader isr = null;
                try {
                    fis = new FileInputStream(file);
                    isr = new InputStreamReader(fis,"UTF-8");
                    char[] input  = new char[fis.available()];
                    isr.read(input);
                    shoppingBagMsg = new String(input);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (isr != null) {
                            isr.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return shoppingBagMsg;
    }
}
