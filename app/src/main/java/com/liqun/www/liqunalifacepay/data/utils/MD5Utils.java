package com.liqun.www.liqunalifacepay.data.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5:
 *      将字符串转换成32位的字符串(16进制字符[0~f]) 不可逆
 * 疑问:如何提升密码强度?
 *      1. 增强密码复杂度
 *      2. "加盐"
 * Created by MoeRookie on 2018/2/5.
 */
public class MD5Utils {
    private static final String TAG = MD5Utils.class.getSimpleName();
    /**
     * 对指定的字符串使用MD5算法进行加密
     * @param psd 要加密的密码
     * @return 加密后的字符串
     */
    public static String md5(String psd){
        StringBuilder sb = null;
        try {
            // 1. 指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 2. 将需要加密的字符串转换成byte类型的数组,然后进行随机哈希过程
            byte[] bs = digest.digest(psd.getBytes());
            // 3. 循环遍历bs,然后让其生成32位字符串,固定写法
            sb = new StringBuilder();
            for (byte b:bs) {
                int i = b & 0xff;
                // int 类型的i需要转换成16进制的字符
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            L.e("MD5Util.md5().sb.toString() = "+sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
