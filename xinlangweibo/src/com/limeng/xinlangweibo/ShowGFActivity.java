package com.limeng.xinlangweibo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.limeng.xinlangweibo.adapter.UsersAdapter;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.NowUserKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.net.RequestListener;

public class ShowGFActivity extends Activity {
    
    private static final String TAG = "ShowGFActivity";
    
    private List<UserInfo> userInfoList = new ArrayList<UserInfo>();
    
    private View progress_layout;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private ListView friend_lv;
    
    private TextView title;
    
    private UsersAdapter adapter;
    
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_friends);
        progress_layout = findViewById(R.id.friend_progress);
        btn_writer = (Button) findViewById(R.id.btn_writer);
        btn_writer.setVisibility(View.GONE);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_back_normal));
        btn_refresh.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        friend_lv = (ListView) findViewById(R.id.show_friend_lv);
        title = (TextView) findViewById(R.id.txt_wb_title);
        title.setText("关注列表");
        friend_lv = (ListView) findViewById(R.id.show_friend_lv);
        adapter = new UsersAdapter(this);
        UserInfo user = NowUserKeeper.readUserInfo(this);
        getUsers(user.getUid());
        // 点击事件
        friend_lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", userInfoList.get(arg2));
                Intent intent = new Intent(ShowGFActivity.this, ShowDetailUser.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    
    /**
     * 控制adapter的数据
     */
    @SuppressLint("HandlerLeak")
    Handler handlerStatus = new Handler() {
        
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress_layout.setVisibility(View.GONE);
            adapter.setList((ArrayList<UserInfo>) msg.obj);
            friend_lv.setAdapter(adapter);
        }
    };
    
    @SuppressLint("SimpleDateFormat")
    private void getUsers(String uid) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        FriendshipsAPI friendshipsAPI = new FriendshipsAPI(token);
        friendshipsAPI.friends(Long.valueOf(uid), 50, 0, false, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                
            }
            
            @Override
            public void onComplete(String values) {
                
                try {
                    JSONObject base_json = new JSONObject(values);
                    JSONArray json_data = base_json.getJSONArray("users");
                    
                    for (int i = 0; i < json_data.length(); i++) {
                        JSONObject json_object = json_data.getJSONObject(i);
                        if (json_object != null) {
                            // 创建一个对象存储每条微博数据
                            UserInfo userInfo = new UserInfo();
                            // 用户信息
                            String id = json_object.getString("id");
                            String screen_name = json_object.getString("screen_name");
                            String description = json_object.getString("description");
                            String gender = json_object.getString("gender");
                            String followers_count = json_object.getString("followers_count");
                            String friends_count = json_object.getString("friends_count");
                            String statuses_count = json_object.getString("statuses_count");
                            String profile_image_url = json_object.getString("profile_image_url");
                            String avatar_large = json_object.getString("avatar_large");
                            userInfo.setAvatar_large(avatar_large);
                            userInfo.setId(id);
                            userInfo.setScreen_name(screen_name);
                            userInfo.setDescription(description);
                            userInfo.setGender(gender);
                            userInfo.setFollowers_count(followers_count);
                            userInfo.setFriends_count(friends_count);
                            userInfo.setStatuses_count(statuses_count);
                            userInfo.setProfile_image_url(profile_image_url);
                            
                            // 微博信息
                            JSONObject statusObject = json_object.getJSONObject("status");
                            Status status = new Status();
                            String wid = statusObject.getString("id");
                            String created_at = statusObject.getString("created_at");
                            @SuppressWarnings("deprecation")
                            Date startDate = new Date(created_at);
                            created_at = new SimpleDateFormat("MM-dd hh:mm").format(startDate);
                            String text = statusObject.getString("text");
                            // 转发数
                            String reposts_count = statusObject.getString("reposts_count");
                            // 评论数
                            String comments_count = statusObject.getString("comments_count");
                            // 获取微博来源
                            String source = "来自：" + Html.fromHtml(statusObject.getString("source"));
                            status.setId(wid);
                            status.setCreated_at(created_at);
                            status.setText(text);
                            status.setReposts_count(reposts_count);
                            status.setComments_count(comments_count);
                            status.setSource(source);
                            // 判断微博存在带图片信息
                            if (statusObject.has("thumbnail_pic")) {
                                // 获得缩略图url链接
                                String thumbnail_pic = statusObject.getString("thumbnail_pic");
                                status.setThumbnail_pic(thumbnail_pic);
                                String bmiddle_pic = statusObject.getString("bmiddle_pic");
                                status.setBmiddle_pic(bmiddle_pic);
                            }
                            // 获取转发微博的信息 并放到微博信息里面
                            if (statusObject.has("retweeted_status")) {
                                Status r_status = new Status();
                                JSONObject retweeted_status = statusObject.getJSONObject("retweeted_status");
                                String retweeted_status_text = retweeted_status.getString("text");
                                r_status.setText(retweeted_status_text);
                                // 获得缩略图url链接
                                if (retweeted_status.has("thumbnail_pic")) {
                                    String thumbnail_pic = retweeted_status.getString("thumbnail_pic");
                                    r_status.setThumbnail_pic(thumbnail_pic);
                                    String bmiddle_pic = retweeted_status.getString("bmiddle_pic");
                                    r_status.setBmiddle_pic(bmiddle_pic);
                                }
                                status.setRetweeted_status(r_status);
                            }
                            userInfo.setStatus(status);
                            userInfoList.add(userInfo);
                        }
                    }
                    Message msg = handlerStatus.obtainMessage(0, userInfoList);
                    handlerStatus.sendMessage(msg);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "");
                    e.printStackTrace();
                }
            }
        });
    }
}
