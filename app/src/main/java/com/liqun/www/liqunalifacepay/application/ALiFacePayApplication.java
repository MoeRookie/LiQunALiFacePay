package com.liqun.www.liqunalifacepay.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.widget.TextView;

import com.alipay.xdevicemanager.api.XDeviceManager;
import com.liqun.www.liqunalifacepay.data.utils.L;
import com.liqun.www.liqunalifacepay.ui.activity.SelfHelpPayActivity;

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
    //这儿需要修改为您自己的包名
    private static final String MAIN_PROCESS_NAME = "com.liqun.www.liqunalifacepay";
    private static ALiFacePayApplication instance;

    private XDeviceManager mXDeviceManager;
    private String hostIP; // 本机ip地址
    private String shoppingBagMsg; // 购物袋信息

    private String shopName; // 门店名称
    private String shopNo; // 门店编码
    private String shopMerchantNo; // 门店商户号
    private String catwalkNo; // 款台号
    private String posServerIp; // pos后台ip地址
    private String posServerPort; // pos后台ip端口

    private String bagMsg; // 启用购物袋信息
    private String operatorNo; // 操作员编号
    private String flowNo; // 流水号
    public XDeviceManager getXDeviceManager() {
        return mXDeviceManager;
    }

    public void setXDeviceManager(XDeviceManager XDeviceManager) {
        mXDeviceManager = XDeviceManager;
    }
    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getShopMerchantNo() {
        return shopMerchantNo;
    }

    public void setShopMerchantNo(String shopMerchantNo) {
        this.shopMerchantNo = shopMerchantNo;
    }

    public String getCatwalkNo() {
        return catwalkNo;
    }

    public void setCatwalkNo(String catwalkNo) {
        this.catwalkNo = catwalkNo;
    }

    public String getPosServerIp() {
        return posServerIp;
    }

    public void setPosServerIp(String posServerIp) {
        this.posServerIp = posServerIp;
    }

    public String getPosServerPort() {
        return posServerPort;
    }

    public void setPosServerPort(String posServerPort) {
        this.posServerPort = posServerPort;
    }


    public String getBagMsg() {
        return bagMsg;
    }

    public void setBagMsg(String bagMsg) {
        this.bagMsg = bagMsg;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(String flowNo) {
        this.flowNo = flowNo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //因为管控SDK会导致Application的onCreate多次调用
        if (ProccessChecker.isAppMainProcess(this, MAIN_PROCESS_NAME)) {
            //所有初始化代码放在这里
            instance = this;
            //Create the device manager
            mXDeviceManager = new XDeviceManager(this);
            mXDeviceManager.initContext();
        }
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
                File file = new File(rootDir,"ShoppingBag.txt");
                FileInputStream fis = null;
                InputStreamReader isr = null;
                try {
                    fis = new FileInputStream(file);
                    isr = new InputStreamReader(fis,"UTF-8");
                    char[] input  = new char[fis.available()];
                    isr.read(input);
                    shoppingBagMsg = new String(input);
                    L.e(shoppingBagMsg);
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
