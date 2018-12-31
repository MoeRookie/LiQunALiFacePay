package com.liqun.www.liqunalifacepay.data.bean;

import java.math.BigDecimal;

/**
 * 支付宝支付
 */
public class ALiPayBean {
    public static class ALiPayRequestBean {
        private String out_trade_no; // 支付订单号
        private String auth_code; // 顾客手机条码
        private String store_id; // 门店编码
        private String terminal_id; // 款台号
        private String operator_id; // 收款员号
        private BigDecimal total_amount; // 金额 单位为元,精确到小数点后两位
        private BigDecimal discountable_amount; // 参与优惠计算的金额 (预留字段,目前不填写)[暂不用]
        private BigDecimal undiscountable_amount; // 不参与优惠计算的金额  单位为元,精确到小数点后两位[暂不用]
        private String terminal_params; // 机具信息

        public ALiPayRequestBean() {
        }

        public ALiPayRequestBean(String out_trade_no, String auth_code, String store_id, String terminal_id, String operator_id, BigDecimal total_amount, BigDecimal discountable_amount, BigDecimal undiscountable_amount, String terminal_params) {
            this.out_trade_no = out_trade_no;
            this.auth_code = auth_code;
            this.store_id = store_id;
            this.terminal_id = terminal_id;
            this.operator_id = operator_id;
            this.total_amount = total_amount;
            this.discountable_amount = discountable_amount;
            this.undiscountable_amount = undiscountable_amount;
            this.terminal_params = terminal_params;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getAuth_code() {
            return auth_code;
        }

        public void setAuth_code(String auth_code) {
            this.auth_code = auth_code;
        }

        public String getStore_id() {
            return store_id;
        }

        public void setStore_id(String store_id) {
            this.store_id = store_id;
        }

        public String getTerminal_id() {
            return terminal_id;
        }

        public void setTerminal_id(String terminal_id) {
            this.terminal_id = terminal_id;
        }

        public String getOperator_id() {
            return operator_id;
        }

        public void setOperator_id(String operator_id) {
            this.operator_id = operator_id;
        }

        public BigDecimal getTotal_amount() {
            return total_amount;
        }

        public void setTotal_amount(BigDecimal total_amount) {
            this.total_amount = total_amount;
        }

        public BigDecimal getDiscountable_amount() {
            return discountable_amount;
        }

        public void setDiscountable_amount(BigDecimal discountable_amount) {
            this.discountable_amount = discountable_amount;
        }

        public BigDecimal getUndiscountable_amount() {
            return undiscountable_amount;
        }

        public void setUndiscountable_amount(BigDecimal undiscountable_amount) {
            this.undiscountable_amount = undiscountable_amount;
        }

        public String getTerminal_params() {
            return terminal_params;
        }

        public void setTerminal_params(String terminal_params) {
            this.terminal_params = terminal_params;
        }

        @Override
        public String toString() {
            return "ALiPayRequestBean{" +
                    "out_trade_no='" + out_trade_no + '\'' +
                    ", auth_code='" + auth_code + '\'' +
                    ", store_id='" + store_id + '\'' +
                    ", terminal_id='" + terminal_id + '\'' +
                    ", operator_id='" + operator_id + '\'' +
                    ", total_amount=" + total_amount +
                    ", discountable_amount=" + discountable_amount +
                    ", undiscountable_amount=" + undiscountable_amount +
                    ", terminal_params='" + terminal_params + '\'' +
                    '}';
        }
    }
    public static class ALiPayResponseBean{
        /**
         * 10000成功
         * 20000服务不可用
         * 20001授权权限不足
         * 40001缺少参数
         * 40002非法参数
         * 40004业务处理失败
         * 40006代理权限不足
        */
        private String code;
        private String msg; // success,fail
        private String tradestatus; // TRADE_SUCCESS;TRADE_CLOSE;

        public ALiPayResponseBean() {
        }

        public ALiPayResponseBean(String code, String msg, String tradestatus) {
            this.code = code;
            this.msg = msg;
            this.tradestatus = tradestatus;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getTradestatus() {
            return tradestatus;
        }

        public void setTradestatus(String tradestatus) {
            this.tradestatus = tradestatus;
        }

        @Override
        public String toString() {
            return "ALiPayResponseBean{" +
                    "code='" + code + '\'' +
                    ", msg='" + msg + '\'' +
                    ", tradestatus='" + tradestatus + '\'' +
                    '}';
        }
    }
}
