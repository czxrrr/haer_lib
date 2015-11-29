package com.elinc.im.haer.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by HUBIN on 2015/7/24.
 */
public class Book extends BmobObject {
    private String title;
    private String intro;
    private User submiter;
    private User onwer;
    private List<String> tags;
    private String bookAvatar;
    private BmobRelation answerList;

    public User getOnwer() {
        return onwer;
    }

    public void setOnwer(User onwer) {
        this.onwer = onwer;
    }

    private BmobRelation followerList;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public User getSubmiter() {
        return submiter;
    }

    public void setSubmiter(User submiter) {
        this.submiter = submiter;
        this.onwer=submiter;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getBookAvatar() {
        return bookAvatar;
    }

    public void setBookAvatar(String bookAvatar) {
        this.bookAvatar = bookAvatar;
    }

    public BmobRelation getAnswerList() {
        return answerList;
    }

    public void setAnswerList(BmobRelation answerList) {
        this.answerList = answerList;
    }

    public BmobRelation getFollowerList() {
        return followerList;
    }

    public void setFollowerList(BmobRelation followerList) {
        this.followerList = followerList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
