package com.liqun.www.liqunalifacepay.data.bean;

public class SettingItemBean {
    private String title; // 标题
    private String content; // 内容

    public SettingItemBean() {
    }

    public SettingItemBean(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SettingItemBean{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
