package com.limeng.xinlangweibo.pojo;

/**
 * 评论内容
 * 
 * @author limeng
 */
public class Comment {
    
    public String getCreated_at() {
        return created_at;
    }
    
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public UserInfo getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * 评论id
     */
    private String id;
    
    /**
     * 评论时间
     */
    private String created_at;
    
    /**
     * 评论内容
     */
    private String text;
    
    /**
     * 评论人
     */
    private UserInfo userInfo;
    
    /**
     * 评论的微博具体信息
     */
    private Status status;
    
    /**
     * 评论来源评论，当本评论属于对另一评论的回复时返回此字段
     */
    private Comment reply_comment;
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Comment getReply_comment() {
        return reply_comment;
    }
    
    public void setReply_comment(Comment reply_comment) {
        this.reply_comment = reply_comment;
    }
    
    @Override
    public String toString() {
        return "Comment [id=" + id + ", created_at=" + created_at + ", text=" + text + ", userInfo="
                + userInfo.toString() + "]";
    }
    
}
