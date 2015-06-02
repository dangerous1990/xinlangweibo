package com.limeng.xinlangweibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limeng.xinlangweibo.adapter.CommentsAdapter;
import com.limeng.xinlangweibo.pojo.Comment;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.AsyncImageLoader;
import com.limeng.xinlangweibo.util.AsyncImageLoader.ImageCallback;
import com.limeng.xinlangweibo.util.HighLight;
import com.limeng.xinlangweibo.util.Util;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.WeiboAPI.AUTHOR_FILTER;
import com.weibo.sdk.android.net.RequestListener;

@SuppressWarnings("unchecked")
public class ShowDetailStatus extends Activity {
    
    private static final int REQUEST_CODE_GET_COMMENTS = 0;
    
    private static final int REQUEST_CODE_GET_STATUS = 1;
    
    private ImageView detail_iv_head;
    
    private TextView detail_tv_name;
    
    private TextView detail_text;
    
    private ImageView detail_img;
    
    private LinearLayout detail_redirect_ll;
    
    private TextView detail_redirect_text;
    
    private ImageView detail_redirect_img;
    
    private TextView detail_from;
    
    private TextView detail_time;
    
    private ListView detail_comments_lv;
    
    private View comment_progress;
    
    private Status status;
    
    private CommentsAdapter commentsAdapter;
    
    private List<Comment> commentsList = new ArrayList<Comment>();
    
    private TextView no_comments;
    
    private Button sendComment;
    
    private Button redirect;
    
    private View more_layout;
    
    private View progress_layout;
    
    private TextView title;
    
    private LinearLayout lllayout;
    
    private LinearLayout refresh_layout;
    
    private TextView more_text;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_status_detail);
        sendComment = (Button) findViewById(R.id.btn_sendComment);
        redirect = (Button) findViewById(R.id.btn_redirect);
        // 评论
        sendComment.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDetailStatus.this, SendCommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", status);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // 转发
        redirect.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowDetailStatus.this, RepostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", status);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        
        commentsAdapter = new CommentsAdapter(this);
        comment_progress = findViewById(R.id.comment_progress);
        ((TextView) findViewById(R.id.progress_text)).setText("正在加载评论中...");
        detail_iv_head = (ImageView) findViewById(R.id.detail_iv_head);
        detail_tv_name = (TextView) findViewById(R.id.detail_tv_name);
        detail_text = (TextView) findViewById(R.id.detail_text);
        detail_img = (ImageView) findViewById(R.id.detail_img);
        detail_iv_head = (ImageView) findViewById(R.id.detail_iv_head);
        detail_redirect_ll = (LinearLayout) findViewById(R.id.detail_redirect_ll);
        detail_redirect_text = (TextView) findViewById(R.id.detail_redirect_text);
        detail_redirect_img = (ImageView) findViewById(R.id.detail_redirect_img);
        detail_from = (TextView) findViewById(R.id.detail_from);
        detail_time = (TextView) findViewById(R.id.detail_time);
        detail_comments_lv = (ListView) findViewById(R.id.detail_comments_lv);
        no_comments = (TextView) findViewById(R.id.no_comments);
        // 获取微博数据
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey("status")) {
            status = (Status) bundle.get("status");
        }
        UserInfo userInfo = status.getUserInfo();
        Drawable head_image = AsyncImageLoader.loadDrawable(userInfo.getProfile_image_url(), detail_iv_head,
                new ImageCallback() {
                    
                    @Override
                    public void imageSet(Drawable drawable, ImageView iv) {
                        iv.setImageDrawable(drawable);
                    }
                });
        if (head_image != null) {
            detail_iv_head.setImageDrawable(head_image);
        }
        detail_tv_name.setText(userInfo.getScreen_name());
        HighLight.setHighLightText(detail_text, status.getText());
        // detail_text.setText(status.getText());
        // 微博图片
        if (status.getThumbnail_pic() != null) {
            detail_img.setVisibility(View.VISIBLE);
            // 使用异步下载图片
            Drawable weibo_image = AsyncImageLoader.loadDrawable(status.getBmiddle_pic(), detail_img,
                    new ImageCallback() {
                        
                        @Override
                        public void imageSet(Drawable drawable, ImageView iv) {
                            iv.setImageDrawable(drawable);
                        }
                    });
            if (weibo_image != null) {
                detail_img.setImageDrawable(weibo_image);
            }
        }
        detail_from.setText(status.getSource());
        detail_time.setText(status.getCreated_at());
        
        // 判断是否有转发
        if (status.getRetweeted_status() != null) {
            Status retweeted_status = status.getRetweeted_status();
            detail_redirect_ll.setVisibility(View.VISIBLE);
            detail_redirect_text.setText(retweeted_status.getText());
            
            // 判断转发微博中是否含有图片
            if (retweeted_status.getThumbnail_pic() != null) {
                detail_redirect_img.setVisibility(View.VISIBLE);
                // 使用异步下载图片
                Drawable image = AsyncImageLoader.loadDrawable(retweeted_status.getBmiddle_pic(),
                        detail_redirect_img, new ImageCallback() {
                            
                            @Override
                            public void imageSet(Drawable drawable, ImageView iv) {
                                iv.setImageDrawable(drawable);
                            }
                        });
                if (image != null) {
                    detail_redirect_img.setImageDrawable(image);
                }
            }
            
        }
        
        getComments(Long.valueOf(status.getId()), 0);
        
        // 底部加载
        more_layout = View.inflate(this, R.layout.more_layout, null);
        lllayout = (LinearLayout) more_layout.findViewById(R.id.ll_more_loading);
        more_text = (TextView) more_layout.findViewById(R.id.text_more);
        
        more_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!commentsList.isEmpty()) {
                    lllayout.setVisibility(View.VISIBLE);
                    more_text.setVisibility(View.GONE);
                    Long max_id = Long.valueOf(commentsList.get((commentsList.size() - 1)).getId());
                    getComments(Long.valueOf(status.getId()), max_id);
                }
            }
        });
        detail_comments_lv.addFooterView(more_layout, null, true);
        detail_comments_lv.setAdapter(commentsAdapter);
    }
    
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(android.os.Message msg) {
            List<Comment> list = (ArrayList<Comment>) msg.obj;
            if (msg.what == 1) {
                if (!list.isEmpty()) {
                    commentsAdapter.setList(list);
                    Util.reSetListViewHeight(detail_comments_lv);
                    comment_progress.setVisibility(View.GONE);
                } else {
                    comment_progress.setVisibility(View.GONE);
                    no_comments.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == 2) {
                if (!list.isEmpty()) {
                    commentsAdapter.setList(list);
                    commentsAdapter.notifyDataSetChanged();
                    Util.reSetListViewHeight(detail_comments_lv);
                    lllayout.setVisibility(View.GONE);
                    more_text.setVisibility(View.VISIBLE);
                } else {
                    lllayout.setVisibility(View.GONE);
                    more_text.setVisibility(View.VISIBLE);
                }
            }
        };
    };
    
    /**
     * 获取当前微博的评论
     * 
     * @param id
     */
    private void getComments(long id, final long max_id) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        CommentsAPI commentsAPI = new CommentsAPI(token);
        commentsAPI.show(id, 0, max_id, 5, 1, AUTHOR_FILTER.ALL, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                
            }
            
            @Override
            public void onComplete(String buffer) {
                try {
                    JSONObject base_json = new JSONObject(buffer);
                    JSONArray json_data = base_json.getJSONArray("comments");
                    for (int i = 0; i < json_data.length(); i++) {
                        JSONObject json_object = json_data.getJSONObject(i);
                        if (json_object != null) {
                            Comment comment = Util.getOneComment(json_object);
                            commentsList.add(comment);
                        }
                    }
                    if (max_id > 0) {
                        Message msg = handler.obtainMessage(2, commentsList);
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage(1, commentsList);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
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
    
}
