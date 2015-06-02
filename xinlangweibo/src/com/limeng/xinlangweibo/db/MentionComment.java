package com.limeng.xinlangweibo.db;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.limeng.xinlangweibo.R;
import com.limeng.xinlangweibo.ShowDetailStatus;
import com.limeng.xinlangweibo.R.id;
import com.limeng.xinlangweibo.R.layout;
import com.limeng.xinlangweibo.adapter.StatusAdapter;
import com.limeng.xinlangweibo.pojo.Status;

public class MentionComment extends Activity {
    
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mention_status);
        
        progress_layout = findViewById(R.id.message_progress);
        message_lv = (ListView) findViewById(R.id.message_lv);
        statusAdapter = new StatusAdapter(this);
        
        message_lv.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
                Intent intent = new Intent(MentionComment.this, ShowDetailStatus.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("status", statusList.get(positon));
                intent.putExtras(bundle);
                startActivity(intent);
                
            }
        });
        // 获取@我的微博
        btn_writer = (Button) findViewById(R.id.btn_writer);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) findViewById(R.id.txt_wb_title);
        title.setText("@我的评论");
    }
}
