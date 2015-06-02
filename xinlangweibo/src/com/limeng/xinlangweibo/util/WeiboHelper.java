package com.limeng.xinlangweibo.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.limeng.xinlangweibo.dao.UserDao;
import com.limeng.xinlangweibo.pojo.User;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

public class WeiboHelper {
    
    private Context context;
    
    private String UID;
    
    private boolean flag = false;
    
    public WeiboHelper() {
        
    }
    
    public WeiboHelper(Context context) {
        this.context = context;
    }
    
    /**
     * 通过url 获得对应的Drawable资源
     * 
     * @param url
     * @return
     */
    public static Drawable getDrawableFromUrl(String url) {
        try {
            URLConnection urls = new URL(url).openConnection();
            return Drawable.createFromStream(urls.getInputStream(), "image");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获得用户信息 必须包括uid
     * 
     * @param context
     * @param uid
     * @return
     */
    public User getUserInfomation(final Context context, final User user, long uid) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);
        UsersAPI usersAPI = new UsersAPI(token);
        usersAPI.show(uid, new RequestListener() {
            
            @Override
            public void onIOException(IOException e) {
                
            }
            
            @Override
            public void onError(WeiboException e) {
                
            }
            
            @Override
            public void onComplete(String values) {
                
                try {
                    
                    Log.d("String value", values);
                    JSONObject json_object = new JSONObject(values);
                    user.setUser_id(json_object.getString("id"));
                    user.setDescription(json_object.getString("description"));
                    user.setUser_name(json_object.getString("screen_name"));
                    String head_url = json_object.getString("profile_image_url");
                    user.setUser_head(getDrawableFromUrl(head_url));
                    
                    user.setToken(token.getToken());
                    user.setToken_secret("2360de44af8ff082ddd4576248686a54");
                    UserDao dao = new UserDao(context);
                    dao.insertUser(user);
                    Log.d("user", user.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }
        });
        // Log.d("user", user.toString());
        return user;
        
    }
    
 
}
