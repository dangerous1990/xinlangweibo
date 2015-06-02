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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limeng.xinlangweibo.adapter.StatusAdapter;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.limeng.xinlangweibo.util.Util;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.AUTHOR_FILTER;
import com.weibo.sdk.android.api.WeiboAPI.SRC_FILTER;
import com.weibo.sdk.android.api.WeiboAPI.TYPE_FILTER;
import com.weibo.sdk.android.net.RequestListener;

public class MessageFragment extends Fragment {
    
    private static final String TAG = "MessageFragment";
    
    // 微博列表
    private LinkedList<Status> statusList = new LinkedList<Status>();
    
    private StatusAdapter statusAdapter;
    
    private ListView message_lv;
    
    private View progress_layout;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
    private LinearLayout lllayout;
    
    private LinearLayout refresh_layout;
    
    private TextView more_text;
    
    private View more_layout;
    
    int mNum;
    
    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */
    public static IndexFragment newInstance(int num) {
        IndexFragment f = new IndexFragment();
        
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
        
        View v = inflater.inflate(R.layout.fragment_message, null);
        refresh_layout = (LinearLayout) v.findViewById(R.id.index_refresh_loading);
        progress_layout = v.findViewById(R.id.message_progress);
        btn_writer = (Button) v.findViewById(R.id.btn_writer);
        btn_refresh = (Button) v.findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        message_lv = (ListView) v.findViewById(R.id.message_lv);
        statusAdapter = new StatusAdapter(getActivity());
        
        message_lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
                Intent intent = new Intent(getActivity(), ShowDetailStatus.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", statusList.get(positon));
                intent.putExtras(bundle);
                startActivity(intent);
                
            }
        });
        // 获取@我的微博
        title = (TextView) v.findViewById(R.id.txt_wb_title);
        title.setText("@我的微博");
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
        
        // 底部加载
        more_layout = View.inflate(getActivity(), R.layout.more_layout, null);
        lllayout = (LinearLayout) more_layout.findViewById(R.id.ll_more_loading);
        more_text = (TextView) more_layout.findViewById(R.id.text_more);
        
        more_layout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(!statusList.isEmpty()){
                    lllayout.setVisibility(View.VISIBLE);
                    more_text.setVisibility(View.GONE);
                    Long max_id = Long.valueOf(statusList.get((statusList.size() - 1)).getId());
                    getStatuses(0, max_id);
                }
               
            }
        });
        message_lv.addFooterView(more_layout, null, true);
        message_lv.setAdapter(statusAdapter);
        return v;
        
    }
    
    @Override
    public void onResume() {
        if (!statusList.isEmpty()) {
            message_lv.setAdapter(statusAdapter);
            statusAdapter.setList(statusList);
            statusAdapter.notifyDataSetChanged();
            progress_layout.setVisibility(View.GONE);
            
        } else {
            getStatuses(0, 0);
        }
        super.onResume();
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
    /**
     * 获取@我的微博
     * @param since_id
     * @param max_id
     */
    @SuppressWarnings("deprecation")
    private void getStatuses(final long since_id, final long max_id) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this.getActivity());
        StatusesAPI statusesAPI = new StatusesAPI(token);
        statusesAPI.mentions(since_id, max_id, 10, 1, AUTHOR_FILTER.ALL, SRC_FILTER.ALL, TYPE_FILTER.ALL, false,
                new RequestListener() {
                    
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
                                    
                                    // 将单条微博数据设置到集合中
                                    statusList.add(status);
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
}
