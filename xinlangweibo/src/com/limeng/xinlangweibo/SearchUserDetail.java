package com.limeng.xinlangweibo;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.AsyncImageLoader;
import com.limeng.xinlangweibo.util.NowUserKeeper;
import com.limeng.xinlangweibo.util.Util;
import com.limeng.xinlangweibo.util.AsyncImageLoader.ImageCallback;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.textservice.SentenceSuggestionsInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SearchUserDetail extends Activity {
    
    private TextView txt_wb_item_uname;
    
    private TextView txt_wb_item_content;
    
    private TextView txt_wb_item_from;
    
    private ImageView img_wb_item_head;
    
    private TextView txt_wb_item_time;
    
    private TextView txt_wb_item_redirect;
    
    private TextView txt_wb_item_comment;
    
    private ImageView img_wb_item_V;
    
    private View lyt_wb_item_sublayout;
    
    private ImageView img_wb_item_content_pic;
    
    private TextView txt_wb_item_subcontent;
    
    private ImageView img_wb_item_content_subpic;
    
    private TextView user_status_count;
    
    private TextView user_followers_count;
    
    private TextView user_detail_decription;
    
    private TextView user_friends_count;
    
    private TextView user_detail_name;
    
    private ImageView user_detail_head;
    
    private ImageView user_detail_gender;
    
    private Status status;
    
    private View progress_layout;
    
    private UserInfo user;
    
    private Button btn_share;
    
    private Button tv_back;
    
    private String uid;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_user_detail);
        progress_layout = findViewById(R.id.message_progress);
        ((TextView) findViewById(R.id.tv_title)).setText("用户信息");
        // 获取信息
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("uid")) {
            uid = bundle.getString("uid");
        }
        if (bundle.containsKey("user")) {
            user = (UserInfo) bundle.get("user");
        }
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setText("加关注");
        tv_back = (Button) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 添加事件监听
        btn_share.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (btn_share.getText().toString().equals("加关注")) {
                    createAttention(user.getScreen_name());
                } else {
                    cancelAttention(user.getScreen_name());
                }
            }
        });
        
        // 最近一条微博
        txt_wb_item_uname = (TextView) findViewById(R.id.txt_wb_item_uname);
        txt_wb_item_content = (TextView) findViewById(R.id.txt_wb_item_content);
        txt_wb_item_from = (TextView) findViewById(R.id.txt_wb_item_from);
        img_wb_item_head = (ImageView) findViewById(R.id.img_wb_item_head);
        txt_wb_item_time = (TextView) findViewById(R.id.txt_wb_item_time);
        txt_wb_item_redirect = (TextView) findViewById(R.id.txt_wb_item_redirect);
        txt_wb_item_comment = (TextView) findViewById(R.id.txt_wb_item_comment);
        img_wb_item_V = (ImageView) findViewById(R.id.img_wb_item_V);
        lyt_wb_item_sublayout = (LinearLayout) findViewById(R.id.lyt_wb_item_sublayout);
        img_wb_item_content_pic = (ImageView) findViewById(R.id.img_wb_item_content_pic);
        txt_wb_item_subcontent = (TextView) findViewById(R.id.txt_wb_item_subcontent);
        img_wb_item_content_subpic = (ImageView) findViewById(R.id.img_wb_item_content_subpic);
        // 设置不可见
        img_wb_item_head.setVisibility(View.GONE);
        img_wb_item_V.setVisibility(View.GONE);
        txt_wb_item_uname.setVisibility(View.GONE);
        // 用户信息
        user_status_count = (TextView) findViewById(R.id.user_status_count);
        user_followers_count = (TextView) findViewById(R.id.user_followers_count);
        user_detail_decription = (TextView) findViewById(R.id.user_detail_decription);
        user_friends_count = (TextView) findViewById(R.id.user_friends_count);
        user_detail_name = (TextView) findViewById(R.id.user_detail_name);
        user_detail_head = (ImageView) findViewById(R.id.user_detail_head);
        user_detail_gender = (ImageView) findViewById(R.id.user_detail_gender);
        
        getUser(uid);
    }
    
    /**
     * 控制adapter的数据
     */
    @SuppressLint("HandlerLeak")
    Handler handlerUser = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            user = (UserInfo) msg.obj;
            progress_layout.setVisibility(View.GONE);
            Toast.makeText(SearchUserDetail.this, "获取用户信息成功", Toast.LENGTH_SHORT).show();
            // 设置用户数据
            user_status_count.setText(user.getStatuses_count());
            user_followers_count.setText(user.getFollowers_count());
            user_detail_decription.setText(user.getDescription());
            user_friends_count.setText(user.getFriends_count());
            user_detail_name.setText(user.getScreen_name());
            // 使用异步下载微博用户头像图片
            Drawable head_image = AsyncImageLoader.loadDrawable(user.getAvatar_large(), user_detail_head,
                    new ImageCallback() {
                        
                        @Override
                        public void imageSet(Drawable drawable, ImageView iv) {
                            iv.setImageDrawable(drawable);
                        }
                    });
            if (head_image != null) {
                user_detail_head.setImageDrawable(head_image);
            }
            if (user.getGender().equals("f") || user.getGender().equals("F")) {
                user_detail_gender.setBackgroundResource(R.drawable.nv);
            } else if (user.getGender().equals("n") || user.getGender().equals("N")) {
                user_detail_gender.setVisibility(View.GONE);
            }
            
            status = user.getStatus();
            // 设置微博数据
            txt_wb_item_time.setText(status.getCreated_at());
            txt_wb_item_redirect.setText(status.getReposts_count());
            txt_wb_item_comment.setText(status.getComments_count());
            txt_wb_item_content.setText(status.getText());
            txt_wb_item_from.setText(status.getSource());
            
            // 判断微博中是否含有图片
            if (status.getThumbnail_pic() != null) {
                img_wb_item_content_pic.setVisibility(View.VISIBLE);
                // 使用异步下载图片
                Drawable weibo_image = AsyncImageLoader.loadDrawable(status.getBmiddle_pic(), img_wb_item_content_pic,
                        new ImageCallback() {
                            
                            @Override
                            public void imageSet(Drawable drawable, ImageView iv) {
                                iv.setImageDrawable(drawable);
                            }
                        });
                if (weibo_image != null) {
                    img_wb_item_content_pic.setImageDrawable(weibo_image);
                }
            }
            
            // 判断是否有转发
            if (status.getRetweeted_status() != null) {
                Status retweeted_status = status.getRetweeted_status();
                lyt_wb_item_sublayout.setVisibility(View.VISIBLE);
                txt_wb_item_subcontent.setText(retweeted_status.getText());
                
                // 判断转发微博中是否含有图片
                if (retweeted_status.getThumbnail_pic() != null) {
                    img_wb_item_content_subpic.setVisibility(View.VISIBLE);
                    // 使用异步下载图片
                    Drawable image = AsyncImageLoader.loadDrawable(retweeted_status.getBmiddle_pic(),
                            img_wb_item_content_subpic, new ImageCallback() {
                                
                                @Override
                                public void imageSet(Drawable drawable, ImageView iv) {
                                    iv.setImageDrawable(drawable);
                                }
                            });
                    if (image != null) {
                        img_wb_item_content_subpic.setImageDrawable(image);
                    }
                }
                
            }
        }
    };
    
    private void getUser(final String uid) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        UsersAPI userAPI = new UsersAPI(token);
        userAPI.show(Long.valueOf(uid), new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onComplete(String values) {
                
                try {
                    JSONObject json_object = new JSONObject(values);
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
                    Status status = Util.getOneStatus(statusObject);
                    userInfo.setStatus(status);
                    Message msg = handlerUser.obtainMessage(1, userInfo);
                    handlerUser.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    Handler handler = new Handler() {
        
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                Toast.makeText(SearchUserDetail.this, "取消关注成功", Toast.LENGTH_LONG).show();
                btn_share.setText("加关注");
            } else {
                Toast.makeText(SearchUserDetail.this, "取消关注失败", Toast.LENGTH_LONG).show();
            }
        };
    };
    
    private void cancelAttention(String name) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        FriendshipsAPI friendshipsAPI = new FriendshipsAPI(token);
        friendshipsAPI.destroy(name, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onError(WeiboException arg0) {
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onComplete(String arg0) {
                Message msg = handler.obtainMessage(1);
                handler.sendMessage(msg);
            }
        });
        
    }
    
    Handler create = new Handler() {
        
        public void handleMessage(Message msg) {
            if (msg.what > 0) {
                Toast.makeText(SearchUserDetail.this, "关注成功", Toast.LENGTH_LONG).show();
                btn_share.setText("取消关注");
            } else {
                Toast.makeText(SearchUserDetail.this, "关注失败", Toast.LENGTH_LONG).show();
            }
        };
    };
    
    private void createAttention(String name) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        FriendshipsAPI friendshipsAPI = new FriendshipsAPI(token);
        friendshipsAPI.create(name, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                Message msg = create.obtainMessage(-1);
                create.sendMessage(msg);
            }
            
            @Override
            public void onError(WeiboException arg0) {
                Message msg = create.obtainMessage(-1);
                create.sendMessage(msg);
            }
            
            @Override
            public void onComplete(String arg0) {
                Message msg = create.obtainMessage(1);
                create.sendMessage(msg);
            }
        });
        
    }
}
