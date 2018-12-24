package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 准备付款
 */
public class PreparePaymentBean {
    public static class PreparePaymentRequestBean{
        private String ip; // 机具的ip地址
        private int count; // 商品条数
        private float qty; // 总数量(包含重量)
        private float total; // 总金额

        public PreparePaymentRequestBean() {
        }

        public PreparePaymentRequestBean(String ip, int count, float qty, float total) {
            this.ip = ip;
            this.count = count;
            this.qty = qty;
            this.total = total;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public float getQty() {
            return qty;
        }

        public void setQty(float qty) {
            this.qty = qty;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "PreparePaymentRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", count=" + count +
                    ", qty=" + qty +
                    ", total=" + total +
                    '}';
        }
    }
    public static class PreparePaymentResponseBean{
        private String flow_no; // 流水号
        private float qty; // 数量合计(重量)
        private float total; // 应付总金额
        private float totaldsc; // 优惠金额
        private String retflag; // 返回标志(返回标志0 正常 1异常)
        private String retmsg; // 返回的信息

        public PreparePaymentResponseBean() {
        }

        public PreparePaymentResponseBean(String flow_no, float qty, float total, float totaldsc, String retflag, String retmsg) {
            this.flow_no = flow_no;
            this.qty = qty;
            this.total = total;
            this.totaldsc = totaldsc;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getFlow_no() {
            return flow_no;
        }

        public void setFlow_no(String flow_no) {
            this.flow_no = flow_no;
        }

        public float getQty() {
            return qty;
        }

        public void setQty(float qty) {
            this.qty = qty;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getTotaldsc() {
            return totaldsc;
        }

        public void setTotaldsc(float totaldsc) {
            this.totaldsc = totaldsc;
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
            return "PreparePaymentResponseBean{" +
                    "flow_no='" + flow_no + '\'' +
                    ", qty=" + qty +
                    ", total=" + total +
                    ", totaldsc=" + totaldsc +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
