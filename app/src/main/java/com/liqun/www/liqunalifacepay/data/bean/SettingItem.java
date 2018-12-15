package com.liqun.www.liqunalifacepay.data.bean;

public class SettingItem {
    private int titleId; // 标题
    private String content; // 内容

    public SettingItem() {
    }

    public SettingItem(int titleId, String content) {
        this.titleId = titleId;
        this.content = content;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SettingItem{" +
                "titleId=" + titleId +
                ", content='" + content + '\'' +
                '}';
    }
}
