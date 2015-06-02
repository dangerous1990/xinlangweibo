package com.limeng.xinlangweibo.pojo;

import java.io.Serializable;

public class UserInfo implements Serializable {
    
    /**
     * 用户详细信息
     */
    private static final long serialVersionUID = 3415044831451327068L;
    
    public String getScreen_name() {
        return screen_name;
    }
    
    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProfile_image_url() {
        return profile_image_url;
    }
    
    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
    
    public String getFollowers_count() {
        return followers_count;
    }
    
    public void setFollowers_count(String followers_count) {
        this.followers_count = followers_count;
    }
    
    public String getFriends_count() {
        return friends_count;
    }
    
    public void setFriends_count(String friends_count) {
        this.friends_count = friends_count;
    }
    
    public String getStatuses_count() {
        return statuses_count;
    }
    
    public void setStatuses_count(String statuses_count) {
        this.statuses_count = statuses_count;
    }
    
    public String getFavourites_count() {
        return favourites_count;
    }
    
    public void setFavourites_count(String favourites_count) {
        this.favourites_count = favourites_count;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    private String avatar_large;
    
    public String getAvatar_large() {
        return avatar_large;
    }
    
    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    /**
     * 用户uid
     */
    private String uid;
    
    /**
     * 用户id
     */
    private String id;
    
    /**
     * 用户名称
     */
    private String screen_name;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 头像地址
     */
    private String profile_image_url;
    
    /**
     * 粉丝数
     */
    private String followers_count;
    
    /**
     * 关注数
     */
    private String friends_count;
    
    /**
     * 微博数
     */
    private String statuses_count;
    
    /**
     * 收藏数
     */
    private String favourites_count;
    
    /**
     * 是否认证过
     */
    private boolean verified;
    
    /**
     * 用户的最近一条微博
     */
    private Status status;
    
    /**
     * 用户性别
     */
    private String gender;
    
    /**
     * 用户大头像地址
     */
    
    @Override
    public String toString() {
        return "UserInfo [uid=" + uid + ", id=" + id + ", screen_name=" + screen_name + ", description=" + description
                + ", profile_image_url=" + profile_image_url + ", followers_count=" + followers_count
                + ", friends_count=" + friends_count + ", statuses_count=" + statuses_count + ", favourites_count="
                + favourites_count + ", verified=" + verified + ", status=" + status + ", gender=" + gender + "]";
    }
    
}
