package com.liqun.www.liqunalifacepay.data.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 付款方式
 */
public class PaymentTypeBean {
    public static class PaymentTypeRequestBean{
        private String ip; // 机具的ip地址
        private String payno; // 付款方式(01 现金付款,02 金卡付款,03 银行卡付款,07支付宝（预留）,08 微信付款)
        private float total; // 付款金额
        private String payid; // 付款卡号 当payno=02 或payno=03 或payno=07 或payno=08 时使用
        private String paymm; // 支付密码；当payno=02或payno=03 时使用
        private String verifycode; // 校验码(金卡时使用)
        private String reference; // 银行卡交易参考号
        private String flag; // 是否立即支付(默认为0)
        private String terminal_params; // 机具信息

        public String getTerminal_params() {
            return terminal_params;
        }

        public void setTerminal_params(String terminal_params) {
            this.terminal_params = terminal_params;
        }



        public PaymentTypeRequestBean() {
        }

        public PaymentTypeRequestBean(String ip, String payno, float total, String payid, String paymm, String verifycode, String reference, String flag, String terminal_params) {
            this.ip = ip;
            this.payno = payno;
            this.total = total;
            this.payid = payid;
            this.paymm = paymm;
            this.verifycode = verifycode;
            this.reference = reference;
            this.flag = flag;
            this.terminal_params = terminal_params;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPayno() {
            return payno;
        }

        public void setPayno(String payno) {
            this.payno = payno;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public String getPayid() {
            return payid;
        }

        public void setPayid(String payid) {
            this.payid = payid;
        }

        public String getPaymm() {
            return paymm;
        }

        public void setPaymm(String paymm) {
            this.paymm = paymm;
        }

        public String getVerifycode() {
            return verifycode;
        }

        public void setVerifycode(String verifycode) {
            this.verifycode = verifycode;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "PaymentTypeRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", payno='" + payno + '\'' +
                    ", total=" + total +
                    ", payid='" + payid + '\'' +
                    ", paymm='" + paymm + '\'' +
                    ", verifycode='" + verifycode + '\'' +
                    ", reference='" + reference + '\'' +
                    ", flag='" + flag + '\'' +
                    ", terminal_params='" + terminal_params + '\'' +
                    '}';
        }
    }
    /**
     * itempay : [{"payno":"07","total":0.2,"change":0,"payid":"lqbh8jy39000861190108190124","ckic":"0","scye":"0"}]
     * retflag : 2
     * retmsg :
     * printtxt : welcome1
     */
    public static class PaymentTypeResponseBean
    implements Serializable{
        private String retflag;
        private String retmsg;
        private String printtxt;
        private List<ItemBean> itempay;

        public PaymentTypeResponseBean() {
        }

        public PaymentTypeResponseBean(String retflag, String retmsg, String printtxt, List<ItemBean> itempay) {
            this.retflag = retflag;
            this.retmsg = retmsg;
            this.printtxt = printtxt;
            this.itempay = itempay;
        }

        /**
         * payno : 07
         * total : 0.2
         * change : 0
         * payid : lqbh8jy39000861190108190124
         * ckic : 0
         * scye : 0
         */
        public static class ItemBean
        implements Serializable{
            private String payno;
            private double total;
            private int change;
            private String payid;
            private String ckic;
            private String scye;

            public ItemBean() {
            }

            public ItemBean(String payno, double total, int change, String payid, String ckic, String scye) {
                this.payno = payno;
                this.total = total;
                this.change = change;
                this.payid = payid;
                this.ckic = ckic;
                this.scye = scye;
            }

            public String getPayno() {
                return payno;
            }

            public void setPayno(String payno) {
                this.payno = payno;
            }

            public double getTotal() {
                return total;
            }

            public void setTotal(double total) {
                this.total = total;
            }

            public int getChange() {
                return change;
            }

            public void setChange(int change) {
                this.change = change;
            }

            public String getPayid() {
                return payid;
            }

            public void setPayid(String payid) {
                this.payid = payid;
            }

            public String getCkic() {
                return ckic;
            }

            public void setCkic(String ckic) {
                this.ckic = ckic;
            }

            public String getScye() {
                return scye;
            }

            public void setScye(String scye) {
                this.scye = scye;
            }
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

        public String getPrinttxt() {
            return printtxt;
        }

        public void setPrinttxt(String printtxt) {
            this.printtxt = printtxt;
        }

        public List<ItemBean> getItempay() {
            return itempay;
        }

        public void setItempay(List<ItemBean> itempay) {
            this.itempay = itempay;
        }
    }
}
