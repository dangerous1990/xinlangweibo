package com.limeng.xinlangweibo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SqureFragment extends Fragment {
    
    int mNum;
    
    private Button btn_writer;
    
    private Button btn_refresh;
    
    private TextView title;
    
    private Spinner search_spinner;
    
    private Button go_search;
    
    private EditText search_content;
    
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
        
        View v = inflater.inflate(R.layout.fragment_squre, null);
        btn_writer = (Button) v.findViewById(R.id.btn_writer);
        btn_refresh = (Button) v.findViewById(R.id.btn_refresh);
        btn_writer.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        title = (TextView) v.findViewById(R.id.txt_wb_title);
        
        title.setText("广场");
        
        search_spinner = (Spinner) v.findViewById(R.id.search_spinner);
        go_search = (Button) v.findViewById(R.id.go_search);
        search_content = (EditText) v.findViewById(R.id.search_content);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        
        adapter.add("搜人");
        adapter.add("搜微博");
        adapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        search_spinner.setAdapter(adapter);
        
        ledger_ll1 = (LinearLayout) v.findViewById(R.id.ledger_ll1);
        ledger_ll3 = (LinearLayout) v.findViewById(R.id.ledger_ll3);
        
        go_search.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String search = (String) search_spinner.getSelectedItem();
              if(search.equals("搜人")){
                  Intent intent = new Intent(getActivity(),SearchUserActivity.class);
                  intent.putExtra("name", search_content.getText().toString());
                  startActivity(intent);
              }else{
                  Intent intent = new Intent(getActivity(),SearchStatusActivity.class);
                  intent.putExtra("name", search_content.getText().toString());
                  startActivity(intent);
              }
                
            }
        });        
        ledger_ll1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HotCommentStatus.class);
                startActivity(intent);
                
            }
        });
        ledger_ll3.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HotRepostStatus.class);
                startActivity(intent);
            }
        });
        return v;
    }
}
