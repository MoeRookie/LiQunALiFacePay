package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 取消交易
 */
public class CancelDealBean {
    public static class CancelDealRequestBean{
        private String ip; // 机具的ip地址
        private String flag; // 标志(默认为0)

        public CancelDealRequestBean() {
        }

        public CancelDealRequestBean(String ip, String flag) {
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
            return "CancelDealRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }
    public static class CancelDealResponseBean{
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public CancelDealResponseBean() {
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
            return "CancelDealResponseBean{" +
                    "retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
