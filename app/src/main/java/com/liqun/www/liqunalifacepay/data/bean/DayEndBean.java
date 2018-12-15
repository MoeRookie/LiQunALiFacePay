package com.liqun.www.liqunalifacepay.data.bean;

public class DayEndBean {
    public static class DayEndRequestBean{
        String ip; // 机具的ip地址
        String flag; // 标志位

        public DayEndRequestBean() {
        }

        public DayEndRequestBean(String ip, String flag) {
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
            return "DayEndRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }
    public static class DayEndResponseBean{
        String retflag; // 返回标志 0 正常 1异常
        String retmsg; // 返回的信息

        public DayEndResponseBean() {
        }

        public DayEndResponseBean(String retflag, String retmsg) {
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
            return "DayEndResponseBean{" +
                    "retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
