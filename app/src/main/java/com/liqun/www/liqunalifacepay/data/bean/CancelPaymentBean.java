package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 取消付款
 */
public class CancelPaymentBean {
    public static class CancelPaymentRequestBean{
        private String ip; // 机具的ip地址
        private String flag; // 默认为0

        public CancelPaymentRequestBean() {
        }

        public CancelPaymentRequestBean(String ip, String flag) {
            this.ip = ip;
            this.flag = flag;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "CancelPaymentRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }
    public static class CancelPaymentResponseBean{
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public CancelPaymentResponseBean() {
        }

        public CancelPaymentResponseBean(String retflag, String retmsg) {
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getRetflag() {
            return retflag;
        }

        public void setRetflag(String retflag) {
            this.retflag = retflag;
        }

        public String getRetmsg() {
            return retmsg;
        }

        public void setRetmsg(String retmsg) {
            this.retmsg = retmsg;
        }

        @Override
        public String toString() {
            return "CancelPaymentResponseBean{" +
                    "retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
