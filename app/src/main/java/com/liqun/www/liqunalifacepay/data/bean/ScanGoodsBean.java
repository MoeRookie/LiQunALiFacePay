package com.liqun.www.liqunalifacepay.data.bean;

import java.io.Serializable;

/**
 * 扫描商品
 */
public class ScanGoodsBean {
    public static class ScanGoodsRequestBean {
        private String ip; // 机具的ip地址
        private String barcode; // 商品条码
        private float qty; // 数量

        public ScanGoodsRequestBean() {
        }

        public ScanGoodsRequestBean(String ip, String barcode, float qty) {
            this.ip = ip;
            this.barcode = barcode;
            this.qty = qty;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public float getQty() {
            return qty;
        }

        public void setQty(float qty) {
            this.qty = qty;
        }

        @Override
        public String toString() {
            return "ScanGoodsRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", barcode='" + barcode + '\'' +
                    ", qty=" + qty +
                    '}';
        }
    }
    public static class ScanGoodsResponseBean implements Serializable {
        private String incode; // 商品编码
        private String barcode; // 商品条码(前端扫描条码)
        private String fname; // 商品名称
        private String specs; // 规格
        private String units; // 单位
        private float unitprice; // 原零售价
        private float price; // 实际售价
        private float qty; // 数量或重量
        private float disc; // 折扣
        private float total; // 总金额
        private float dsctotal; // 优惠金额(暂时不用)
        private String dsctype; // 优惠方式(暂时不用)默认0
        private String plutype; // 是否称重的标志(0普通1称重)
        private float weight; // 商品重量(plutype=0 默认为0,plutype=1时返回商品重量)
        private String goodsno; // 商品条码
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public ScanGoodsResponseBean() {
        }

        public ScanGoodsResponseBean(String incode, String barcode, String fname, String specs, String units, float unitprice, float price, float qty, float disc, float total, float dsctotal, String dsctype, String plutype, float weight, String goodsno, String retflag, String retmsg) {
            this.incode = incode;
            this.barcode = barcode;
            this.fname = fname;
            this.specs = specs;
            this.units = units;
            this.unitprice = unitprice;
            this.price = price;
            this.qty = qty;
            this.disc = disc;
            this.total = total;
            this.dsctotal = dsctotal;
            this.dsctype = dsctype;
            this.plutype = plutype;
            this.weight = weight;
            this.goodsno = goodsno;
            this.retflag = retflag;
            this.retmsg = retmsg;
        }

        public String getIncode() {
            return incode;
        }

        public void setIncode(String incode) {
            this.incode = incode;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getSpecs() {
            return specs;
        }

        public void setSpecs(String specs) {
            this.specs = specs;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public float getUnitprice() {
            return unitprice;
        }

        public void setUnitprice(float unitprice) {
            this.unitprice = unitprice;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public float getQty() {
            return qty;
        }

        public void setQty(float qty) {
            this.qty = qty;
        }

        public float getDisc() {
            return disc;
        }

        public void setDisc(float disc) {
            this.disc = disc;
        }

        public float getTotal() {
            return total;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getDsctotal() {
            return dsctotal;
        }

        public void setDsctotal(float dsctotal) {
            this.dsctotal = dsctotal;
        }

        public String getDsctype() {
            return dsctype;
        }

        public void setDsctype(String dsctype) {
            this.dsctype = dsctype;
        }

        public String getPlutype() {
            return plutype;
        }

        public void setPlutype(String plutype) {
            this.plutype = plutype;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public String getGoodsno() {
            return goodsno;
        }

        public void setGoodsno(String goodsno) {
            this.goodsno = goodsno;
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
            return "ScanGoodsResponseBean{" +
                    "incode='" + incode + '\'' +
                    ", barcode='" + barcode + '\'' +
                    ", fname='" + fname + '\'' +
                    ", specs='" + specs + '\'' +
                    ", units='" + units + '\'' +
                    ", unitprice=" + unitprice +
                    ", price=" + price +
                    ", qty=" + qty +
                    ", disc=" + disc +
                    ", total=" + total +
                    ", dsctotal=" + dsctotal +
                    ", dsctype='" + dsctype + '\'' +
                    ", plutype='" + plutype + '\'' +
                    ", weight=" + weight +
                    ", goodsno='" + goodsno + '\'' +
                    ", retflag='" + retflag + '\'' +
                    ", retmsg='" + retmsg + '\'' +
                    '}';
        }
    }
}
