package com.liqun.www.liqunalifacepay.data.utils;

import com.alibaba.fastjson.JSON;

import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndResponseBean;

public class JointDismantleUtils {
    static final String RESPONSE_TAG_DAY_END = "OFFLINEINFO";
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

    /**
     * 返回请求结果对象
     * @param result 请求结果
     * @return 请求结果对象
     */
    public static Object dismantleResponse(String result) {
        Object obj = null;
        String tag = null;
        String msg = null;
        if (result != null) {
            tag = result.substring(0,result.indexOf("$"));
            msg = result.substring(result.indexOf("$")+1);
            switch (tag) {
                case RESPONSE_TAG_DAY_END:
                    obj = JSON.parseObject(msg,DayEndResponseBean.class);
                    break;
            }
        }
        return obj;
    }
}
