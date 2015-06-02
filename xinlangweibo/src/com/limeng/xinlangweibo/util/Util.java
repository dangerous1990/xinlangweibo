package com.limeng.xinlangweibo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.limeng.xinlangweibo.pojo.Comment;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;

public class Util {
    
    public static Status getOneStatus(JSONObject json_object) {
        Status status = new Status();
        
        // 获得一条wiebo id
        String id;
        try {
            id = json_object.getString("id");
            
            // 获得发weibo的时间
            String created_at = json_object.getString("created_at");
            // 通过字符串构造发微博的时间
            Date startDate = new Date(created_at);
            created_at = new SimpleDateFormat("MM-dd hh:mm").format(startDate);
            // 比较发表微博时间和当前时间之间距离时常
            // created_at = new DateUtils().twoDateDistance(startDate, new Date());
            // 获得weibo内容
            String text = json_object.getString("text");
            // 转发数
            String reposts_count = json_object.getString("reposts_count");
            // 评论数
            String comments_count = json_object.getString("comments_count");
            // 获取微博来源
            String source = "来自：" + Html.fromHtml(json_object.getString("source"));
            status.setId(id);
            status.setCreated_at(created_at);
            status.setText(text);
            status.setReposts_count(reposts_count);
            status.setComments_count(comments_count);
            status.setSource(source);
            // 判断微博存在带图片信息
            if (json_object.has("thumbnail_pic")) {
                // 获得缩略图url链接
                String thumbnail_pic = json_object.getString("thumbnail_pic");
                status.setThumbnail_pic(thumbnail_pic);
            }
            if (json_object.has("bmiddle_pic")) {
                String bmiddle_pic = json_object.getString("bmiddle_pic");
                status.setBmiddle_pic(bmiddle_pic);
            }
            // 获取转发微博的信息 并放到微博信息里面
            if (json_object.has("retweeted_status")) {
                Status r_status = new Status();
                JSONObject retweeted_status = json_object.getJSONObject("retweeted_status");
                String retweeted_status_text = retweeted_status.getString("text");
                r_status.setText(retweeted_status_text);
                // 获得缩略图url链接
                if (retweeted_status.has("thumbnail_pic")) {
                    String thumbnail_pic = retweeted_status.getString("thumbnail_pic");
                    r_status.setThumbnail_pic(thumbnail_pic);
                }
                if (retweeted_status.has("bmiddle_pic")) {
                    String bmiddle_pic = retweeted_status.getString("bmiddle_pic");
                    r_status.setBmiddle_pic(bmiddle_pic);
                }
                status.setRetweeted_status(r_status);
            }
            UserInfo userInfo = new UserInfo();
            // 获得用户数据
            if (json_object.has("user")) {
                JSONObject u = json_object.getJSONObject("user");
                // 获得发weibo 用户id
                String userId = u.getString("id");
                // 获得发weibo 用户的名称
                String userName = u.getString("screen_name");
                // 获得发weibo 用户的头像url链接
                String userIcon = u.getString("profile_image_url");
                // 获取用户认证信息
                boolean verified = (Boolean.valueOf(u.getString("verified"))).booleanValue();
                userInfo.setId(userId);
                userInfo.setVerified(verified);
                userInfo.setProfile_image_url(userIcon);
                userInfo.setScreen_name(userName);
            }
            status.setUserInfo(userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
    
    public static Comment getOneComment(JSONObject json_object) {
        Comment comment = new Comment();
        try {
            UserInfo userInfo = new UserInfo();
            // 获得用户数据
            JSONObject u = json_object.getJSONObject("user");
            // 获得发评论 用户id
            String userId = u.getString("id");
            // 获得发评论 用户的名称
            String userName = u.getString("screen_name");
            // 获得发评论 用户的头像url链接
            String userIcon = u.getString("profile_image_url");
            // 获取用户认证信息
            boolean verified = (Boolean.valueOf(u.getString("verified"))).booleanValue();
            userInfo.setId(userId);
            userInfo.setVerified(verified);
            userInfo.setProfile_image_url(userIcon);
            userInfo.setScreen_name(userName);
            comment.setUserInfo(userInfo);
            // 获得一条评论 id
            String id = json_object.getString("id");
            // 获得发评论的时间
            String created_at = json_object.getString("created_at");
            String text = json_object.getString("text");
            comment.setCreated_at(created_at);
            comment.setId(id);
            comment.setText(text);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return comment;
    }
    
    public static UserInfo getOneUser(JSONObject json_object) {
        
        return null;
    }
    
    public static String convertString(String str) {
        Long wan = Long.valueOf(str);
        long i = 0;
        if (wan > 10000) {
            i = wan / 10000L;
            return i + "万";
        }
        
        return str;
    }
    
    public static void reSetListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 80;
//        if (params.height < 304) {
//            params.height = 304;
//        }
        listView.setLayoutParams(params);
    }
}
