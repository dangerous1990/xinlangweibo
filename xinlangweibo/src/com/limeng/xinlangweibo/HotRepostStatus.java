package com.limeng.xinlangweibo;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.limeng.xinlangweibo.adapter.StatusAdapter;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.Util;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.SuggestionsAPI;
import com.weibo.sdk.android.net.RequestListener;

public class HotRepostStatus extends Activity {
    
    private static final String TAG = "HotRepostDailyActivity";
    
    // 微博列表
    private ArrayList<Status> statusList = new ArrayList<Status>();
    
    private StatusAdapter statusAdapter;
    
    private ListView message_lv;
    
    private View progress_layout;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.hot_status);
        
        progress_layout = findViewById(R.id.message_progress);
        message_lv = (ListView) findViewById(R.id.message_lv);
        statusAdapter = new StatusAdapter(this);
        
        message_lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
                Intent intent = new Intent(HotRepostStatus.this, ShowDetailStatus.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", statusList.get(positon));
                intent.putExtras(bundle);
                startActivity(intent);
                
            }
        });
        btn_writer = (Button) findViewById(R.id.btn_writer);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.txt_wb_title);
        title.setText("热门转发微博");
        getHotRepostDaily();
        super.onCreate(savedInstanceState);
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
            statusAdapter.setList((ArrayList<Status>) msg.obj);
            message_lv.setAdapter(statusAdapter);
        }
    };
    
    /**
     * 热门转发微博评论
     */
    private void getHotRepostDaily() {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        SuggestionsAPI statusesAPI = new SuggestionsAPI(token);
        statusesAPI.favoritesHot(10, 2, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                
            }
            
            @Override
            public void onComplete(String buffer) {
                
                try {
                    Log.e(TAG,buffer);
                    JSONArray base_json = new JSONArray(buffer);
//                    JSONObject base_json = new JSONObject(buffer);
//                    JSONArray json_data = base_json.getJSONArray("statuses");
                    for (int i = 0; i < base_json.length(); i++) {
                        JSONObject json_object = base_json.getJSONObject(i);
                        if (json_object != null) {
                            // 创建一个对象存储每条微博数据
                            Status status = Util.getOneStatus(json_object);
                            statusList.add(status);
                        }
                    }
                    Message msg = handlerStatus.obtainMessage(1, statusList);
                    handlerStatus.sendMessage(msg);
                } catch (JSONException e) {
                   
                    e.printStackTrace();
                }
                
            }
        });
    }
}
