package com.limeng.xinlangweibo;

import java.io.IOException;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.limeng.xinlangweibo.adapter.StatusAdapter;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.NowUserKeeper;
import com.limeng.xinlangweibo.util.Util;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.ActivityInvokeAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 首页
 * 
 * @author limeng
 */
@SuppressWarnings("unchecked")
public class IndexFragment extends Fragment {
    
    private static final String TAG = "IndexFragment";
    
    private static final int REQUEST_WRITE_CODE = 1;
    
    private LinkedList<Status> statusList = new LinkedList<Status>(); // 微博列表
    
    private StatusAdapter statusAdapter;
    
    private ListView index_lv;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private View more_layout;
    
    private View progress_layout;
    
    private TextView title;
    
    private LinearLayout lllayout;
    
    private LinearLayout refresh_layout;
    
    private TextView more_text;
    
    int mNum;
    
    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    public static IndexFragment newInstance(int num) {
        IndexFragment f = new IndexFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        
        return f;
    }
    
    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }
    
    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View v = inflater.inflate(R.layout.fragment_index, null);
        
        progress_layout = v.findViewById(R.id.layout_progress);
        refresh_layout = (LinearLayout) v.findViewById(R.id.index_refresh_loading);
        btn_writer = (Button) v.findViewById(R.id.btn_writer);
        btn_refresh = (Button) v.findViewById(R.id.btn_refresh);
        index_lv = (ListView) v.findViewById(R.id.index_lv);
        statusAdapter = new StatusAdapter(getActivity());
        btn_writer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendStatus.class);
                startActivityForResult(intent, REQUEST_WRITE_CODE);
            }
        });
        btn_refresh.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Animation myAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.ref_anim);
                btn_refresh.startAnimation(myAnimation);
                String since = statusList.isEmpty() ? String.valueOf(0) : statusList.getFirst().getId();
                getStatuses(Long.valueOf(since), 0);
                refresh_layout.setVisibility(View.VISIBLE);
            }
        });
        index_lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
                Intent intent = new Intent(getActivity(), ShowDetailStatus.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", statusList.get(positon));
                intent.putExtras(bundle);
                startActivity(intent);
              //  ActivityInvokeAPI.openDetail(getActivity(),statusList.get(positon).getId());
            }
        });
        Bundle bundle = getActivity().getIntent().getExtras();
        String uid = null;
        if (bundle != null) {
            if (bundle.containsKey("uid"))
                uid = bundle.getString("uid");
        }
        title = (TextView) v.findViewById(R.id.txt_wb_title);
        UserInfo user = NowUserKeeper.readUserInfo(getActivity());
        title.setText(user.getScreen_name());
        if (user.getId().equals("limeng")) {
            getUser(uid);
        }
        
        // 底部加载
        more_layout = View.inflate(getActivity(), R.layout.more_layout, null);
        lllayout = (LinearLayout) more_layout.findViewById(R.id.ll_more_loading);
        more_text = (TextView) more_layout.findViewById(R.id.text_more);
        
        more_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                lllayout.setVisibility(View.VISIBLE);
                more_text.setVisibility(View.GONE);
                Long max_id = Long.valueOf(statusList.get((statusList.size() - 1)).getId());
                getStatuses(0, max_id);
            }
        });
        index_lv.addFooterView(more_layout, null, true);
        index_lv.setAdapter(statusAdapter);
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (!statusList.isEmpty()) {
            statusAdapter.setList(statusList);
            statusAdapter.notifyDataSetChanged();
            progress_layout.setVisibility(View.GONE);
            
        } else {
            getStatuses(0, 0);
        }
        
    }
    
    /**
     * 控制adapter的数据
     */
    @SuppressLint("HandlerLeak")
    Handler handlerStatus = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                lllayout.setVisibility(View.GONE);
                more_text.setVisibility(View.VISIBLE);
                statusAdapter.setList((LinkedList<Status>) msg.obj);
                statusAdapter.notifyDataSetChanged();
            }
            if (msg.what == 3) {
                refresh_layout.setVisibility(View.GONE);
                statusAdapter.setList((LinkedList<Status>) msg.obj);
                statusAdapter.notifyDataSetChanged();
            } else {
                progress_layout.setVisibility(View.GONE);
                statusAdapter.setList((LinkedList<Status>) msg.obj);
                statusAdapter.notifyDataSetChanged();
            }
            
        }
    };
    
    @SuppressWarnings("deprecation")
    private void getStatuses(final long since_id, final long max_id) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this.getActivity());
        StatusesAPI statusesAPI = new StatusesAPI(token);
        statusesAPI.homeTimeline(since_id, max_id, 10, 1, false, FEATURE.ALL, false, new RequestListener() {
            
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
                    JSONArray json_data = base_json.getJSONArray("statuses");
                    for (int i = 0; i < json_data.length(); i++) {
                        JSONObject json_object = json_data.getJSONObject(i);
                        if (json_object != null) {
                            // 创建一个对象存储每条微博数据
                            Status status = Util.getOneStatus(json_object);
                            if (max_id > 0) {
                                statusList.add(status);
                            } else if (since_id > 0) {
                                statusList.addFirst(status);
                            } else {
                                statusList.add(status);
                            }
                        }
                    }
                    
                    if (max_id > 0) {
                        Message max_msg = handlerStatus.obtainMessage(2, statusList);
                        handlerStatus.sendMessage(max_msg);
                    } else if (since_id > 0) {
                        Message msg = handlerStatus.obtainMessage(3, statusList);
                        handlerStatus.sendMessage(msg);
                    } else {
                        Message msg = handlerStatus.obtainMessage(1, statusList);
                        handlerStatus.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "");
                    e.printStackTrace();
                }
            }
            
        });
        
    }
    
    /**
     * 控制adapter的数据
     */
    @SuppressLint("HandlerLeak")
    Handler handlerUser = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserInfo u = (UserInfo) msg.obj;
            NowUserKeeper.keepUserInfo(getActivity(), u);
            title.setText(u.getScreen_name());
            Toast.makeText(getActivity(), "获取用户信息成功", Toast.LENGTH_SHORT).show();
            
        }
    };
    
    private void getUser(final String uid) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this.getActivity());
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
                    UserInfo userInfo = new UserInfo();
                    JSONObject json_object = new JSONObject(values);
                    userInfo.setId((json_object.getString("id")));
                    userInfo.setUid(uid);
                    userInfo.setDescription(json_object.getString("description"));
                    userInfo.setProfile_image_url(json_object.getString("profile_image_url"));
                    userInfo.setScreen_name(json_object.getString("screen_name"));
                    Message msg = handlerUser.obtainMessage(1, userInfo);
                    handlerUser.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
}
