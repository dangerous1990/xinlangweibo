package com.limeng.xinlangweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.NowUserKeeper;

public class FriendFragment extends Fragment {
    
    private static final String TAG = "FriendFragment";
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
    int mNum;
    
    private LinearLayout ledger_ll1;
    
    private LinearLayout ledger_ll2;
    
    private LinearLayout ledger_ll3;
    
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
        
        View v = inflater.inflate(R.layout.fragment_friend, null);
        btn_writer = (Button) v.findViewById(R.id.btn_writer);
        btn_refresh = (Button) v.findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) v.findViewById(R.id.txt_wb_title);
        title.setText("我的好友");
        ledger_ll1 = (LinearLayout) v.findViewById(R.id.ledger_ll1);
        ledger_ll2 = (LinearLayout) v.findViewById(R.id.ledger_ll2);
        ledger_ll3 = (LinearLayout) v.findViewById(R.id.ledger_ll3);
        
        ledger_ll1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowGFActivity.class);
                startActivity(intent);
                
            }
        });
        ledger_ll2.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowXFActivity.class);
                startActivity(intent);
            }
        });
        ledger_ll3.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowFFActivity.class);
                startActivity(intent);
            }
        });
        
        return v;
    }
    
}
