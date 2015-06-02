package com.limeng.xinlangweibo;

import java.io.IOException;
import java.util.ArrayList;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.limeng.xinlangweibo.adapter.SearchUserAdapter;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.SearchAPI;
import com.weibo.sdk.android.net.RequestListener;

public class SearchUserActivity extends Activity {
    
    private static final String TAG = "SearchUserActivity";
    
    private List<UserInfo> list = new ArrayList<UserInfo>();
    
    private SearchUserAdapter searchUserAdapter;
    
    private ListView message_lv;
    
    private View progress_layout;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        setContentView(R.layout.mention_status);
        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        
        progress_layout = findViewById(R.id.message_progress);
        message_lv = (ListView) findViewById(R.id.message_lv);
        searchUserAdapter = new SearchUserAdapter(this);
        message_lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("uid", list.get(arg2).getUid());
                Intent intent = new Intent(SearchUserActivity.this, SearchUserDetail.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_writer = (Button) findViewById(R.id.btn_writer);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.txt_wb_title);
        title.setText("搜索人");
        
        getUsers(name);
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
            searchUserAdapter.setList((ArrayList<UserInfo>) msg.obj);
            message_lv.setAdapter(searchUserAdapter);
            searchUserAdapter.notifyDataSetChanged();
        }
    };
    
    private void getUsers(String name) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        SearchAPI searchAPI = new SearchAPI(token);
        searchAPI.users(name, 10, new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                
            }
            
            @Override
            public void onError(WeiboException arg0) {
                
            }
            
            @Override
            public void onComplete(String buffer) {
                
                try {
                    JSONArray base_json = new JSONArray(buffer);
                    // JSONArray json_data = base_json.getJSONArray("users");
                    for (int i = 0; i < base_json.length(); i++) {
                        JSONObject json_object = base_json.getJSONObject(i);
                        if (json_object != null) {
                            // 创建一个对象存储每条微博数据
                            UserInfo user = new UserInfo();
                            user.setScreen_name(json_object.getString("screen_name"));
                            user.setUid(json_object.getString("uid"));
                            list.add(user);
                        }
                    }
                    Message msg = handlerStatus.obtainMessage(1, list);
                    handlerStatus.sendMessage(msg);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "");
                    e.printStackTrace();
                }
                
            }
        });
        
    }
}
