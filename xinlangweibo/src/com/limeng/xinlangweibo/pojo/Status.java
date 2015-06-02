package com.limeng.xinlangweibo.pojo;

import java.io.Serializable;

/**
 * 一条微博的具体信息
 * 
 * @author limeng
 */
public class Status implements Serializable {
    
    private static final long serialVersionUID = 4189105379082101026L;
    
    /**
     * 微博ID
     */
    private String id;
    
    /**
     * 微博内容
     */
    private String text;
    
    /**
     * 微博来源
     */
    private String source;
    
    /**
     * 微博转发数
     */
    private String reposts_count;
    
    /**
     * 微博评论数
     */
    private String comments_count;
    
    /**
     * 微博发表时间
     */
    private String created_at;
    
    /**
     * 发微博人信息
     */
    private UserInfo userInfo;
    
    /**
     * 微博缩略图
     */
    private String thumbnail_pic;
    
    /**
     * 微博中等图片
     */
    private String bmiddle_pic;
    
    /**
     * 转发微博信息
     */
    private Status retweeted_status;
    
    /**
     * 地理信息
     */
    private Geo geo;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getReposts_count() {
        return reposts_count;
    }
    
    public void setReposts_count(String reposts_count) {
        this.reposts_count = reposts_count;
    }
    
    public String getComments_count() {
        return comments_count;
    }
    
    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }
    
    public String getCreated_at() {
        return created_at;
    }
    
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getThumbnail_pic() {
        return thumbnail_pic;
    }
    
    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }
    
    public String getBmiddle_pic() {
        return bmiddle_pic;
    }
    
    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }
    
    public Status getRetweeted_status() {
        return retweeted_status;
    }
    
    public void setRetweeted_status(Status retweeted_status) {
        this.retweeted_status = retweeted_status;
    }
    
    public Geo getGeo() {
        return geo;
    }
    
    public void setGeo(Geo geo) {
        this.geo = geo;
    }
    
    @Override
    public String toString() {
        return "Status [id=" + id + ", text=" + text + ", source=" + source + ", reposts_count=" + reposts_count
                + ", comments_count=" + comments_count + ", created_at=" + created_at + ", userInfo=" + userInfo
                + ", thumbnail_pic=" + thumbnail_pic + ", bmiddle_pic=" + bmiddle_pic + ", retweeted_status="
                + retweeted_status + ", geo=" + geo + "]";
    }
    
}
