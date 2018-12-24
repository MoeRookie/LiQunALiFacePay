package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 获取交易流水
 */
public class DealRecordBean {
    public static class DealRecordRequestBean{
        private String ip; // 机具的ip地址
        private String operators; // 操作员号
        private String flag; // 默认为0

        public DealRecordRequestBean() {
        }

        public DealRecordRequestBean(String ip, String operators, String flag) {
            this.ip = ip;
            this.operators = operators;
            this.flag = flag;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getOperators() {
            return operators;
        }

        public void setOperators(String operators) {
            this.operators = operators;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "DealRecordRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", operators='" + operators + '\'' +
                    ", flag='" + flag + '\'' +
                    '}';
        }
    }
    public static class DealRecordResponseBean{
        private String flow_no; // 流水号
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public DealRecordResponseBean() {
        }

        public DealRecordResponseBean(String flow_no, String retflag, String retmsg) {
            this.flow_no = flow_no;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getFlow_no() {
            return flow_no;
        }

        public void setFlow_no(String flow_no) {
            this.flow_no = flow_no;
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
            return "DealRecordResponseBean{" +
                    "flow_no='" + flow_no + '\'' +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
