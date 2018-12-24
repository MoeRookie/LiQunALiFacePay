package com.liqun.www.liqunalifacepay.application;

public class ConstantValue {
    /**
     * 日结确认密码
     */
    public static final String DAY_END_PWD = "666666";
    /**
     * 服务端返回消息需监听的端口
     */
    public static final int PORT_SERVER_RETURN = 2001;
    /**
     * 服务端接收消息需要监听的端口
     */
    public static final int PORT_SERVER_RECEIVE = 3012;
    /**
     * POS后台服务器地址
     */
    public static final String IP_SERVER_ADDRESS = "128.192.80.113";

    /**
     * 交易流水的tag
     */
    public static final String TAG_DEAL_RECORD = "POSCERTIFY";
    /**
     * 会员卡的tag
     */
    public static final String TAG_VIP_CARD = "VIPCERTIFY";
    /**
     * 扫描商品的tag
     */
    public static final String TAG_SCAN_GOODS = "BARCODECERTIFY";
    /**
     * 取消商品的tag
     */
    public static final String TAG_CANCEL_GOODS = "CANCELBARCODECERTIFY";
    /**
     * 取消交易的tag
     */
    public static final String TAG_CANCEL_DEAL = "CANCELTRADECERTIFY";
    /**
     * 重新获取整比交易明细的tag
     */
    public static final String TAG_RE_OBTAIN_DEAL_DETAILS = "TRADECERTIFY";
    /**
     * 准备付款的tag
     */
    public static final String TAG_PREPARE_PAYMENT = "PAYREQUESTCERTIFY";
    /**
     * 取消付款的tag
     */
    public static final String TAG_CANCEL_PAYMENT = "CANCELPAYCERTIFY";
    /**
     * 付款方式的tag
     */
    public static final String TAG_PAYMENT_TYPE = "PAYCERTIFY";
    /**
     * 日结的tag
     */
    public static final String TAG_DAY_END = "OFFLINECERTIFY";
    /**
     * 日结的flag
     */
    public static final String FLAG_DAY_END = "0";
    /**
     * 储值卡查询tag
     */
    public static final String TAG_DEPOSIT_CARD_QUERY = "CZKCERTIFY";
    /**
     * 管理员授权tag
     */
    public static final String TAG_LICENSE_MANAGER = "CANCELCERTIFY";

    /**
     * 自助收银IP端口
     */
    public static final String SELF_GATHERING_PORT = "20001";
    /**
     * 门店名称
     */
    public static final String SHOP_NAME = "shop_name";
    /**
     * Pos后台IP端口
     */
    public static final String POS_SERVER_PORT = "pos_server_port";
    /**
     * Pos后台IP地址
     */
    public static final String POS_SERVER_IP = "pos_server_ip";
    /**
     * 款台号
     */
    public static final String CATWALK_NO = "catwalk_no";
    /**
     * 门店商户号
     */
    public static final String SHOP_MERCHANT_NO = "shop_merchant_no";
    /**
     * 门店编号
     */
    public static final String SHOP_NO = "shop_no";
    /**
     * 设置界面内容的key
     */
    public static final String KEY_SETTING_CONTENT = "key_setting_content";
    /**
     * 设置需要的密码值
     */
    public static final String VALUE_SETTING_PWD = "111333777";
    /**
     * 购物袋信息的key
     */
    public static final String KEY_SHOPPING_BAG_MSG = "key_shopping_bag_msg";

}
