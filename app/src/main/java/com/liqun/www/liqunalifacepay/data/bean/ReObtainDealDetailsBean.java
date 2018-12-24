package com.liqun.www.liqunalifacepay.data.bean;

/**
 * 重新获取整比交易明细
 */
public class ReObtainDealDetailsBean {
    public static class ReObtainDealDetailsRequestBean{
        private String ip; // 机具的ip地址
        private String flow_no; // 流水号

        public ReObtainDealDetailsRequestBean() {
        }

        public ReObtainDealDetailsRequestBean(String ip, String flow_no) {
            this.ip = ip;
            this.flow_no = flow_no;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getFlow_no() {
            return flow_no;
        }

        public void setFlow_no(String flow_no) {
            this.flow_no = flow_no;
        }

        @Override
        public String toString() {
            return "ReObtainDealDetailsRequestBean{" +
                    "ip='" + ip + '\'' +
                    ", flow_no='" + flow_no + '\'' +
                    '}';
        }
    }
    public static class ReObtainDealDetailsResponseBean{
        private int flow_id; // 商品行号
        private String incode; // 商品编码
        private String barcode; //  商品条码(前端扫描条码)
        private String fname; // 商品名称
        private String specs; // 规格
        private String units; // 单位
        private float unitprice; // 原零售价
        private float price; // 实际售价
        private float qty; // 数量或者是重量
        private float disc; // 折扣
        private float total; // 总金额
        private float dsctotal; // 优惠金额(暂无)
        private String dsctype; // 优惠方式(默认0)
        private String plutype; // 是否称重的标志(0普通1称重)
        private float weight; // 商品重量(plutype=0 默认为0,plutype=1 返回商品重量)
        private String goodsno; // 商品条码
        private String retflag; // 返回标志(0 正常 1异常)
        private String retmsg; // 返回的信息

        public ReObtainDealDetailsResponseBean() {
        }

        public ReObtainDealDetailsResponseBean(int flow_id, String incode, String barcode, String fname, String specs, String units, float unitprice, float price, float qty, float disc, float total, float dsctotal, String dsctype, String plutype, float weight, String goodsno, String retflag, String retmsg) {
            this.flow_id = flow_id;
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

        public int getFlow_id() {
            return flow_id;
        }

        public void setFlow_id(int flow_id) {
            this.flow_id = flow_id;
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
            return "ReObtainDealDetailsResponseBean{" +
                    "flow_id=" + flow_id +
                    ", incode='" + incode + '\'' +
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
