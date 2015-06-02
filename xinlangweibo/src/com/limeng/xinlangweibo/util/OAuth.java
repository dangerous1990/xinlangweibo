package com.limeng.xinlangweibo.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.SortedSet;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

import com.limeng.xinlangweibo.pojo.User;

/**
 * OAuth认证
 * 
 * @author limeng
 */
public class OAuth {
    
    private CommonsHttpOAuthConsumer httpOauthConsumer;
    
    private OAuthProvider httpOauthprovider;
    
    public String APP_KEY;
    
    public String APP_SECRET;
    
    public static final String BOUNDARY = "7cd4a6d158c";
    
    public static final String MP_BOUNDARY = "--" + BOUNDARY;
    
    public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
    
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    
    private static OAuth instance = null;
    
    /**
     * 获得OAuth类的实例
     * 
     * @return
     */
    public static OAuth getInstance() {
        if (instance == null) {
            instance = new OAuth();
        }
        return instance;
    }
    
    private OAuth() {
        // 修改成自己的APP_KEY和APP_SECRET
        this("2682458073", "2360de44af8ff082ddd4576248686a54");
    }
    
    private OAuth(String app_key, String app_secret) {
        this.APP_KEY = app_key;
        this.APP_SECRET = app_secret;
    }
    
    /**
     * 请求AccessToken
     * 
     * @param activity
     * @param callBackUrl
     * @return
     */
    public Boolean requestAccessToken(Activity activity, String callBackUrl) {
        Boolean flag = false;
        try {
            httpOauthConsumer = new CommonsHttpOAuthConsumer(APP_KEY, APP_SECRET);
            
            httpOauthprovider = new DefaultOAuthProvider("http://api.t.sina.com.cn/oauth/request_token",
                    "http://api.t.sina.com.cn/oauth/access_token", "http://api.t.sina.com.cn/oauth/authorize");
            String authUrl = httpOauthprovider.retrieveRequestToken(httpOauthConsumer, callBackUrl);
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
    
    /**
     * 获得AccessToken
     * 
     * @param intent
     * @return User
     */
    public User getAccessToken(Intent intent) {
        Uri uri = intent.getData();
        // 处理获取返回的oauth_verifier参数
        String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
        try {
            httpOauthprovider.setOAuth10a(true);
            httpOauthprovider.retrieveAccessToken(httpOauthConsumer, verifier);
        } catch (OAuthMessageSignerException ex) {
            ex.printStackTrace();
        } catch (OAuthNotAuthorizedException ex) {
            ex.printStackTrace();
        } catch (OAuthExpectationFailedException ex) {
            ex.printStackTrace();
        } catch (OAuthCommunicationException ex) {
            ex.printStackTrace();
        }
        SortedSet<String> user_id = httpOauthprovider.getResponseParameters().get("user_id");
        String userToken = httpOauthConsumer.getToken();
        String userSecret = httpOauthConsumer.getTokenSecret();
        User user = new User(user_id.first(), userToken, userSecret);
        return user;
    }
    
    /**
     * 请求数据
     * 
     * @param token
     *            用户的token
     * @param tokenSecret
     *            用户的tokenSecret
     * @param url
     *            需要获取的数据url（通过url来确定需要获取的数据）
     * @param params
     *            参数
     * @return
     */
    public HttpResponse signRequest(String token, String tokenSecret, String url, List params) {
        HttpPost post = new HttpPost(url);
        ByteArrayOutputStream bos = null;
        String file = null;
        try {
            // 参数的编码转换为utf-8
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            
            for (int i = 0; i < params.size(); i++) {
                BasicNameValuePair nameValuePair = (BasicNameValuePair) params.get(i);
                if (nameValuePair.getName().equals("pic")) {
                    file = nameValuePair.getValue();
                }
            }
            
            byte[] data = null;
            bos = new ByteArrayOutputStream(1024 * 50);
            if (!TextUtils.isEmpty(file)) {
                paramToUpload(bos, params);
                // 设置表单类型和分隔符
                post.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
                Bitmap bf = BitmapFactory.decodeFile(file);
                imageContentToUpload(bos, bf);
                data = bos.toByteArray();
                ByteArrayEntity formEntity = new ByteArrayEntity(data);
                post.setEntity(formEntity);
            }
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        post.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        
        return signRequest(token, tokenSecret, post);
    }
    
    public HttpResponse signRequest(String token, String tokenSecret, HttpPost post) {
        httpOauthConsumer = new CommonsHttpOAuthConsumer(APP_KEY, APP_SECRET);
        httpOauthConsumer.setTokenWithSecret(token, tokenSecret);
        HttpResponse response = null;
        try {
            httpOauthConsumer.sign(post);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
        try {
            response = new DefaultHttpClient().execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    /**
     * Upload weibo contents into output stream .
     * 
     * @param baos
     *            : output stream for uploading weibo
     * @param params
     *            : post parameters for uploading
     * @return void
     */
    
    private static void paramToUpload(OutputStream baos, List<BasicNameValuePair> params) {
        BasicNameValuePair key = null;
        for (int loc = 0; loc < params.size(); loc++) {
            key = params.get(loc);
            StringBuilder temp = new StringBuilder(10);
            temp.setLength(0);
            temp.append(MP_BOUNDARY).append("\r\n");
            temp.append("content-disposition: form-data; name=\"").append(key.getName()).append("\"\r\n\r\n");
            temp.append(key.getValue()).append("\r\n");
            byte[] res = temp.toString().getBytes();
            try {
                baos.write(res);
            } catch (IOException e) {
                
            }
        }
    }
    
    private static void imageContentToUpload(OutputStream out, Bitmap imgpath) {
        StringBuilder temp = new StringBuilder();
        
        temp.append(MP_BOUNDARY).append("\r\n");
        temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"").append("temp.png").append("\"\r\n");
        String filetype = "image/png";
        temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
        System.out.println("----2----" + temp.toString());
        byte[] res = temp.toString().getBytes();
        BufferedInputStream bis = null;
        try {
            out.write(res);
            // 压缩图片
            imgpath.compress(CompressFormat.PNG, 75, out);
            out.write("\r\n".getBytes());
            out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
