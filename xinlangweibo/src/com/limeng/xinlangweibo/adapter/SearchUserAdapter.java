package com.limeng.xinlangweibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.limeng.xinlangweibo.R;
import com.limeng.xinlangweibo.pojo.UserInfo;

public class SearchUserAdapter extends BaseAdapter {
    private UsersHolder holder;
    private Context cxt;
    
    private List<UserInfo> list;
    
    public SearchUserAdapter(Context cxt) {
        this.cxt = cxt;
    }
    
    public void setList(List<UserInfo> list) {
        this.list = list;
    }
    
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }
    
    @Override
    public Object getItem(int position) {
        
        return list == null ? null : list.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        
        if (null == view) {
            
            holder = new UsersHolder();
            view = LayoutInflater.from(cxt).inflate(R.layout.search_user_item, null);
            holder.user_tv_name = (TextView) view.findViewById(R.id.search_user_name);
            view.setTag(holder);
        }
        holder = (UsersHolder) view.getTag();
        UserInfo userInfo = list.get(position);
        holder.user_tv_name.setText(userInfo.getScreen_name());
        return view;
    }
    
    private static class UsersHolder {
        
        TextView user_tv_name;
        
    }
}
