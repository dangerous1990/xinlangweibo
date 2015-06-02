package com.limeng.xinlangweibo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.limeng.xinlangweibo.WelcomeActivity;
import com.limeng.xinlangweibo.pojo.User;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

public class Tools {
    
    private String UID;// 用户UID
    
    private static Tools instance = null;
    
    OAuth oauth = null;
    
    private Tools() {
        oauth = OAuth.getInstance();
    }
    
    public static Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }
        return instance;
    }
    
    /**
     * 检查网络状态
     * 
     * @param context
     *            上下文
     */
    public static void checkNetwork(final WelcomeActivity context) {
        if (!Tools.isNetworkAvailable(context)) {
            TextView msg = new TextView(context);
            msg.setText("当前没有可使用的网络，请检查网络");
            new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_info).setTitle("网络状态提示").setView(msg)
                    .setPositiveButton("确定", new OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                            context.finish();
                            
                        }
                        
                    }).create().show();
        }
    }
    
    /**
     * 判断是否有网络连接
     * 
     * @param context
     *            上下文
     * @return true 有网络 false 没有网络
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo netinfo : info) {
                    if (netinfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 获得服务器端返回的数据
     * 
     * @param response
     * @return 返回字符串JSON格式数据
     */
    public String getData(HttpResponse response) {
        StringBuilder buffer = null;
        try {
            InputStream is = response.getEntity().getContent();
            Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
            buffer = new StringBuilder((int) response.getEntity().getContentLength());
            
            char[] buf = new char[1024];
            int length = 0;
            while ((length = reader.read(buf)) != -1) {
                buffer.append(buf, 0, length);
            }
            reader.close();
            
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        return buffer.toString();
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
     * 获得用户的uid
     * 
     * @param context
     *            应用上下文
     * @return
     */
    
    public String getUID(Context context) {
        
        AccountAPI accountAPI = new AccountAPI(AccessTokenKeeper.readAccessToken(context));
        
        accountAPI.getUid(new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                
            }
            
            @Override
            public void onComplete(String arg0) {
                try {
                    JSONObject json_object = new JSONObject(arg0);
                    UID = json_object.getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("UID", UID);
                
            }
        });
        Log.d("UID", UID+"");
        return UID;
    }
    
    /**
     * 获得用户信息 必须包括uid
     * 
     * @param context
     * @param uid
     * @return
     */
    public User getUserInfomation(Context context, final User user) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(context);
        UsersAPI usersAPI = new UsersAPI(token);
        
        usersAPI.show(UID, new RequestListener() {
            
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
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
            }
        });
        Log.d("user", user.toString());
        return user;
        
    }
    
    
}
