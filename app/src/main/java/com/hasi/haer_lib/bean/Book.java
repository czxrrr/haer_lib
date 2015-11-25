package com.hasi.haer_lib.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by HUBIN on 2015/11/25.
 */
public class Book extends BmobObject {

    private String title;
    private String label;
    private User uploader;
    private String avatar;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
