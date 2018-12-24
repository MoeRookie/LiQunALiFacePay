package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 会员卡
 */
public class VipCardBean {
    public static class VipCardRequestBean{
        private String ip; // 机具的ip地址
        private String vipno; // 会员卡磁条信息
        private String certifytype; // 刷卡方式(0 表示刷卡或者扫描,1 表示手机号)

        public VipCardRequestBean() {
        }

        public VipCardRequestBean(String ip, String vipno, String certifytype) {
            this.ip = ip;
            this.vipno = vipno;
            this.certifytype = certifytype;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getVipno() {
            return vipno;
        }

        public void setVipno(String vipno) {
            this.vipno = vipno;
        }

        public String getCertifytype() {
            return certifytype;
        }

        public void setCertifytype(String certifytype) {
            this.certifytype = certifytype;
        }

        @Override
        public String toString() {
            return "VipCardRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", vipno='" + vipno + '\'' +
                    ", certifytype='" + certifytype + '\'' +
                    '}';
        }
    }
    public static class VipCardResponseBean{
        private String vipno; // 会员卡号
        private String jfgrade; // 积分
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public VipCardResponseBean() {
        }

        public VipCardResponseBean(String vipno, String jfgrade, String retflag, String retmsg) {
            this.vipno = vipno;
            this.jfgrade = jfgrade;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getVipno() {
            return vipno;
        }

        public void setVipno(String vipno) {
            this.vipno = vipno;
        }

        public String getJfgrade() {
            return jfgrade;
        }

        public void setJfgrade(String jfgrade) {
            this.jfgrade = jfgrade;
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
            return "VipCardResponseBean{" +
                    "vipno='" + vipno + '\'' +
                    ", jfgrade='" + jfgrade + '\'' +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
