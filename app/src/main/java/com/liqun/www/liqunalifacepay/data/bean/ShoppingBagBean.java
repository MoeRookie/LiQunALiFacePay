package com.liqun.www.liqunalifacepay.data.bean;

public class ShoppingBagBean {
    private String type; // 型号
    private String  productNo; // 商品编码
    private String price; // 价格
    private boolean isSelected; // 是否被选中
    public ShoppingBagBean() {
    }

    public ShoppingBagBean(String type, String productNo, String price, boolean isSelected) {
        this.type = type;
        this.productNo = productNo;
        this.price = price;
        this.isSelected = isSelected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "ShoppingBagBean{" +
                "type='" + type + '\'' +
                ", productNo='" + productNo + '\'' +
                ", price='" + price + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
