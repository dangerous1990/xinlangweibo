package com.limeng.xinlangweibo;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

/**
 * 用户授权操作
 * 
 * @author limeng
 */
public class OAuthActivity extends Activity {
    
    private Weibo mWeibo;
    
    private static final String ACTIVITY_CALLBACK_URL = "sina://OAuthActivity";
    
    private static final String CONSUMER_KEY = "2682458073";// 替换为开发者的appkey，例如"1646212860";
    
    private static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    
    public static Oauth2AccessToken accessToken;
    
    private static final String TAG = "OAuthActivity";
    
    private Dialog dialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.oauth);
        mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
        View dialogView = View.inflate(this, R.layout.oauth_dialog, null);
        dialog = new Dialog(this, R.style.oauth_style);
        dialog.setContentView(dialogView);
        dialog.show();
        Button oauth_start = (Button) dialogView.findViewById(R.id.oauth_start);
        
        oauth_start.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mWeibo.authorize(OAuthActivity.this, new WeiboAuthListener() {
                    
                    @Override
                    public void onComplete(Bundle values) {
                        String token = values.getString("access_token");
                        String expires_in = values.getString("expires_in");
                        String uid = values.getString("uid");
                        OAuthActivity.accessToken = new Oauth2AccessToken(token, expires_in);
                        if (OAuthActivity.accessToken.isSessionValid()) {
                            AccessTokenKeeper.keepAccessToken(getApplicationContext(), accessToken);
                            Toast.makeText(OAuthActivity.this, "认证成功" + uid, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(OAuthActivity.this, FragmentTabs.class);
                            // Bundle bundle = new Bundle();
                            // bundle.putString("uid", uid);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        }
                    }
                    
                    @Override
                    public void onWeiboException(WeiboException e) {
                        Toast.makeText(getApplicationContext(), "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                    
                    @Override
                    public void onError(WeiboDialogError e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Auth error : " + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                    }
                    
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Auth cancel", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
}
