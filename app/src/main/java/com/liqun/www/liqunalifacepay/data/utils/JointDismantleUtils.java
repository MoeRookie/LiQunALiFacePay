package com.liqun.www.liqunalifacepay.data.utils;

import com.alibaba.fastjson.JSON;

import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndRequestBean;

public class JointDismantleUtils {
    /**
     * 拼接请求串
     * @param tag 标识符
     * @param obj 请求对象
     * @return 请求串
     */
    public static String jointRequest(String tag,Object obj){
        String request = tag + "$";
        String json = null;
        if (obj instanceof DayEndRequestBean) {
            DayEndRequestBean derb = (DayEndRequestBean) obj;
            json = JSON.toJSONString(derb);
        }
        return request + json;
    }
}
