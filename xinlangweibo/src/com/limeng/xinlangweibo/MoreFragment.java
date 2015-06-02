package com.limeng.xinlangweibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.limeng.xinlangweibo.adapter.MoreListViewAdapter;

public class MoreFragment extends Fragment {
    
    int mNum;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
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
        
        View v = inflater.inflate(R.layout.fragment_more, null);
        
        btn_writer = (Button) v.findViewById(R.id.btn_writer);
        btn_refresh = (Button) v.findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) v.findViewById(R.id.txt_wb_title);
        title.setText("更多");
        // 使用自定义的构造器 传值
        MoreListViewAdapter adapter = new MoreListViewAdapter(getActivity());
        ListView lv = (ListView) v.findViewById(R.id.lv);
        lv.setAdapter(adapter);
        // 构造数据
        final List<Map<String, Object>> testDatas = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> item1 = new HashMap<String, Object>();
        item1.put("left", Integer.valueOf(R.drawable.more_setting));
        item1.put("title", "系统设置");
        item1.put("right", Boolean.TRUE);
        
        Map<String, Object> item2 = new HashMap<String, Object>();
        item2.put("left", Integer.valueOf(R.drawable.more_feedback));
        item2.put("title", "意见反馈");
        item2.put("right", Boolean.TRUE);
        
        Map<String, Object> item3 = new HashMap<String, Object>();
        item3.put("left", Integer.valueOf(R.drawable.more_help));
        item3.put("title", "使用协议");
        item3.put("right", Boolean.TRUE);
        
        Map<String, Object> item4 = new HashMap<String, Object>();
        item4.put("left", Integer.valueOf(R.drawable.more_about));
        item4.put("title", "关于我们");
        item4.put("right", Boolean.TRUE);
        
        Map<String, Object> item5 = new HashMap<String, Object>();
        item5.put("left", Integer.valueOf(R.drawable.more_queryonline));
        item5.put("title", "检查更新");
        item5.put("right", Boolean.FALSE);
        // 添加到List中
        testDatas.add(item1);
        testDatas.add(item2);
        testDatas.add(item3);
        testDatas.add(item4);
        testDatas.add(item5);
        
        adapter.setDatas(testDatas);
        adapter.notifyDataSetChanged();
        // 添加Item监听器
        lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 启动“系统设置”
                        break;
                    case 1:
                        // 启动“意见反馈”
                        break;
                    case 2:
                        // 启动“使用协议”
                        break;
                    case 3:
                        // 启动“关于我们”
                        break;
                    case 4:
                        // 启动“检查更新”
                        break;
                
                }
                
            }
            
        });
        return v;
    }
}
