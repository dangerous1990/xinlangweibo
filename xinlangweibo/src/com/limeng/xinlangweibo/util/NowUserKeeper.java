package com.limeng.xinlangweibo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.limeng.xinlangweibo.pojo.UserInfo;

/**
 * 该类用于保存当前用户到sharepreference，并提供读取功能
 * 
 * @author xiaowei6@staff.sina.com.cn
 */
public class NowUserKeeper {
    
    private static final String PREFERENCES_NAME = "com_dream_nowuser";
    
    /**
     * 保存UserInfo到SharedPreferences
     * 
     * @param context
     *            Activity 上下文环境
     * @param token
     *            Oauth2AccessToken
     */
    public static void keepUserInfo(Context context, UserInfo userInfo) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.putString("id", userInfo.getId());
        editor.putString("uid", userInfo.getUid());
        editor.putString("profile_image_url", userInfo.getProfile_image_url());
        editor.putString("screen_name", userInfo.getScreen_name());
        editor.commit();
    }
    
    /**
     * 清空sharepreference
     * 
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    
    /**
     * 从SharedPreferences读取UserInfo
     * 
     * @param context
     * @return Oauth2AccessToken
     */
    public static UserInfo readUserInfo(Context context) {
        UserInfo userInfo = new UserInfo();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        userInfo.setId(pref.getString("id", "limeng"));
        userInfo.setUid(pref.getString("uid", ""));
        userInfo.setProfile_image_url(pref.getString("profile_image_url", ""));
        userInfo.setScreen_name(pref.getString("screen_name", ""));
        return userInfo;
    }
}
