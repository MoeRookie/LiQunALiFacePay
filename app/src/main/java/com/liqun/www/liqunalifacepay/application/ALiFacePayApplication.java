package com.liqun.www.liqunalifacepay.application;

import android.app.Application;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ALiFacePayApplication extends Application {
    private static ALiFacePayApplication instance;
    private static String hostIP;
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
}
