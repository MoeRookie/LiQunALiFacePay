package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 管理员授权
 */
public class LicenseManagerBean {
    public static class LicenseManagerRequestBean{
        private String ip; // 机具的ip地址
        private String cardno; // 磁条信息

        public LicenseManagerRequestBean() {
        }

        public LicenseManagerRequestBean(String ip, String cardno) {
            this.ip = ip;
            this.cardno = cardno;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        @Override
        public String toString() {
            return "LicenseManagerRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", cardno='" + cardno + '\'' +
                    '}';
        }
    }
    public static class LicenseManagerResponseBean{
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public LicenseManagerResponseBean() {
        }

        public LicenseManagerResponseBean(String retflag, String retmsg) {
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
            return "LicenseManagerResponseBean{" +
                    "retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
