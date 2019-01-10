package com.liqun.www.liqunalifacepay.data.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.liqun.www.liqunalifacepay.data.bean.PreparePaymentBean;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelDealBean.CancelDealResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelGoodsBean.CancelGoodsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelGoodsBean.CancelGoodsResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.CancelPaymentBean.CancelPaymentResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DayEndBean.DayEndResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.DealRecordBean.DealRecordRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DealRecordBean.DealRecordResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.DepositCardQueryBean.DepositCardQueryRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.DepositCardQueryBean.DepositCardQueryResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.LicenseManagerBean.LicenseManagerRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.LicenseManagerBean.LicenseManagerResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.PaymentTypeBean.PaymentTypeResponseBean.*;
import static com.liqun.www.liqunalifacepay.data.bean.PreparePaymentBean.PreparePaymentRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ReObtainDealDetailsBean.ReObtainDealDetailsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ReObtainDealDetailsBean.ReObtainDealDetailsResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.ScanGoodsBean.ScanGoodsResponseBean;
import static com.liqun.www.liqunalifacepay.data.bean.VipCardBean.VipCardRequestBean;
import static com.liqun.www.liqunalifacepay.data.bean.VipCardBean.VipCardResponseBean;

public class JointDismantleUtils {
    /**
     * 交易流水
     */
    static final String RESPONSE_TAG_DEAL_RECORD = "POSINFO";
    /**
     * 会员卡
     */
    static final String RESPONSE_TAG_VIP_CARD = "VIPINFO";
    /**
     * 扫描商品
     */
    static final String RESPONSE_TAG_SCAN_GOODS = "GOODSINFO";
    /**
     * 取消商品
     */
    static final String RESPONSE_TAG_CANCEL_GOODS = "CANCELBARCODEINFO";
    /**
     * 取消交易
     */
    static final String RESPONSE_TAG_CANCEL_DEAL = "CANCELTRADEINFO";
    /**
     * 重新获取整比交易明细
     */
    static final String RESPONSE_TAG_RE_OBTAIN_DEAL_DETAILS = "TRADEINFO";
    /**
     * 准备付款
     */
    static final String RESPONSE_TAG_PREPARE_PAYMENT = "PAYREQUESTINFO";
    /**
     * 取消付款
     */
    static final String RESPONSE_TAG_CANCEL_PAYMENT = "CANCELPAYINFO";
    /**
     * 付款方式
     */
    static final String RESPONSE_TAG_PAYMENT_TYPE = "PAYINFO";
    /**
     * 日结
     */
    static final String RESPONSE_TAG_DAY_END = "OFFLINEINFO";
    /**
     * 储值卡查询
     */
    static final String RESPONSE_TAG_DEPOSIT_CARD_QUERY = "CZKINFO";
    /**
     * 管理员授权
     */
    static final String RESPONSE_TAG_LICENSE_MANAGER = "CANCELINFO";
    /**
     * 拼接请求串
     * @param tag 标识符
     * @param obj 请求对象
     * @return 请求串
     */
    public static String jointRequest(String tag,Object obj){
        String request = tag + "$";
        String json = null;
        if (obj instanceof DealRecordRequestBean) { // 获取交易流水
            DealRecordRequestBean drrb = (DealRecordRequestBean) obj;
            json = JSON.toJSONString(drrb);
        }
        if (obj instanceof VipCardRequestBean) { // 会员卡
            VipCardRequestBean vcrb = (VipCardRequestBean) obj;
            json = JSON.toJSONString(vcrb);
        }
        if (obj instanceof ScanGoodsRequestBean) { // 扫描商品
            ScanGoodsRequestBean sgrb = (ScanGoodsRequestBean) obj;
            json = JSON.toJSONString(sgrb);
        }
        if (obj instanceof CancelGoodsRequestBean) { // 取消商品
            CancelGoodsRequestBean cgrb = (CancelGoodsRequestBean) obj;
            json = JSON.toJSONString(cgrb);
        }
        if (obj instanceof CancelDealRequestBean) { // 取消交易
            CancelDealRequestBean cdrb = (CancelDealRequestBean) obj;
            json = JSON.toJSONString(cdrb);
        }
        if (obj instanceof ReObtainDealDetailsRequestBean) { // 重新获取整比交易明细
            ReObtainDealDetailsRequestBean rddrb = (ReObtainDealDetailsRequestBean) obj;
            json = JSON.toJSONString(rddrb);
        }
        if (obj instanceof PreparePaymentRequestBean) { // 准备付款
            PreparePaymentRequestBean pprb = (PreparePaymentRequestBean) obj;
            json = JSON.toJSONString(pprb);
        }
        if (obj instanceof CancelPaymentRequestBean) { // 取消付款
            CancelPaymentRequestBean cprb = (CancelPaymentRequestBean) obj;
            json = JSON.toJSONString(cprb);
        }
        if (obj instanceof PaymentTypeRequestBean) { // 付款方式
            PaymentTypeRequestBean ptrb = (PaymentTypeRequestBean) obj;
            json = JSON.toJSONString(ptrb);
        }
        if (obj instanceof DayEndRequestBean) { // 日结
            DayEndRequestBean derb = (DayEndRequestBean) obj;
            json = JSON.toJSONString(derb);
        }
        if (obj instanceof DepositCardQueryRequestBean) { // 储值卡查询
            DepositCardQueryRequestBean dcqrb = (DepositCardQueryRequestBean) obj;
            json = JSON.toJSONString(dcqrb);
        }
        if (obj instanceof LicenseManagerRequestBean) { // 管理员授权
            LicenseManagerRequestBean lmrb = (LicenseManagerRequestBean) obj;
            json = JSON.toJSONString(lmrb);
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
        if (!TextUtils.isEmpty(result)) {
            tag = result.substring(0,result.indexOf("$"));
            msg = result.substring(result.indexOf("$")+1);
            switch (tag) {
                case RESPONSE_TAG_DEAL_RECORD: // 交易流水
                    obj = JSON.parseObject(msg, DealRecordResponseBean.class);
                    break;
                case RESPONSE_TAG_VIP_CARD: // 会员卡
                    obj = JSON.parseObject(msg, VipCardResponseBean.class);
                    break;
                case RESPONSE_TAG_SCAN_GOODS: // 扫描商品
                    obj = JSON.parseObject(msg, ScanGoodsResponseBean.class);
                    break;
                case RESPONSE_TAG_CANCEL_GOODS: // 取消商品
                    obj = JSON.parseObject(msg, CancelGoodsResponseBean.class);
                    break;
                case RESPONSE_TAG_CANCEL_DEAL: // 取消交易
                    obj = JSON.parseObject(msg, CancelDealResponseBean.class);
                    break;
                case RESPONSE_TAG_RE_OBTAIN_DEAL_DETAILS: // 重新获取整比交易明细
                    obj = JSON.parseObject(msg, ReObtainDealDetailsResponseBean.class);
                    break;
                case RESPONSE_TAG_PREPARE_PAYMENT: // 准备付款
                    obj = JSON.parseObject(msg, PreparePaymentBean.PreparePaymentResponseBean.class);
                    break;
                case RESPONSE_TAG_CANCEL_PAYMENT: // 取消付款
                    obj = JSON.parseObject(msg, CancelPaymentResponseBean.class);
                    break;
                case RESPONSE_TAG_PAYMENT_TYPE: // 付款方式
                    obj = JSON.parseObject(msg,PaymentTypeResponseBean.class);
                    break;
                case RESPONSE_TAG_DAY_END: // 日结
                    obj = JSON.parseObject(msg,DayEndResponseBean.class);
                    break;
                case RESPONSE_TAG_DEPOSIT_CARD_QUERY: // 储值卡查询
                    obj = JSON.parseObject(msg,DepositCardQueryResponseBean.class);
                    break;
                case RESPONSE_TAG_LICENSE_MANAGER: // 管理员授权
                    obj = JSON.parseObject(msg,LicenseManagerResponseBean.class);
                    break;
            }
        }
        return obj;
    }
}
