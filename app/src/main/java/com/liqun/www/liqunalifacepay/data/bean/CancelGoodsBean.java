package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 取消商品
 */
public class CancelGoodsBean {
    public static class  CancelGoodsRequestBean{
        private String ip; // 机具的ip地址
        private int flow_id; // 排列序号
        private String barcode; // 商品条码

        public CancelGoodsRequestBean() {
        }

        public CancelGoodsRequestBean(String ip, int flow_id, String barcode) {
            this.ip = ip;
            this.flow_id = flow_id;
            this.barcode = barcode;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getFlow_id() {
            return flow_id;
        }

        public void setFlow_id(int flow_id) {
            this.flow_id = flow_id;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        @Override
        public String toString() {
            return "CancelGoodsRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", flow_id=" + flow_id +
                    ", barcode='" + barcode + '\'' +
                    '}';
        }
    }
    public static class  CancelGoodsResponseBean{
        private String barcode; // 商品条码
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public CancelGoodsResponseBean() {
        }

        public CancelGoodsResponseBean(String barcode, String retflag, String retmsg) {
            this.barcode = barcode;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
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
            return "CancelGoodsResponseBean{" +
                    "barcode='" + barcode + '\'' +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
