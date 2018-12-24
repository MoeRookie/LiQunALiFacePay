package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 储值卡查询
 */
public class DepositCardQueryBean {
    public static class DepositCardQueryRequestBean{
        private String ip; // 机具的ip地址
        private String cardno; // 磁条信息
        private String password; // 密码默认为空
        private String verifycode; // 校验默认为空

        public DepositCardQueryRequestBean() {
        }

        public DepositCardQueryRequestBean(String ip, String cardno, String password, String verifycode) {
            this.ip = ip;
            this.cardno = cardno;
            this.password = password;
            this.verifycode = verifycode;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getVerifycode() {
            return verifycode;
        }

        public void setVerifycode(String verifycode) {
            this.verifycode = verifycode;
        }

        @Override
        public String toString() {
            return "DepositCardQueryRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", cardno='" + cardno + '\'' +
                    ", password='" + password + '\'' +
                    ", verifycode='" + verifycode + '\'' +
                    '}';
        }
    }
    public static class DepositCardQueryResponseBean {
        private String cardno; // 储值卡卡号
        private String total; // 卡内金额
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public DepositCardQueryResponseBean() {
        }

        public DepositCardQueryResponseBean(String cardno, String total, String retflag, String retmsg) {
            this.cardno = cardno;
            this.total = total;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
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
            return "DepositCardQueryResponseBean{" +
                    "cardno='" + cardno + '\'' +
                    ", total='" + total + '\'' +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
