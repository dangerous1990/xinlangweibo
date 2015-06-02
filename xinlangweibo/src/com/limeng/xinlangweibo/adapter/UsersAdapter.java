/**
 *WeiboAdapter.java
 *2011-9-29 下午08:18:26
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.limeng.xinlangweibo.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limeng.xinlangweibo.R;
import com.limeng.xinlangweibo.pojo.Comment;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AsyncImageLoader;
import com.limeng.xinlangweibo.util.AsyncImageLoader.ImageCallback;

public class UsersAdapter extends BaseAdapter {
    
    private List<UserInfo> list;
    
    private UsersHolder holder;
    
    public void setList(List<UserInfo> list) {
        this.list = list;
    }
    
    private Context context;
    
    public UsersAdapter(Context context) {
        this.context = context;
    }
    
    public int getCount() {
        return list == null ? 0 : list.size();
    }
    
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        
        if (null == view) {
            
            holder = new UsersHolder();
            view = LayoutInflater.from(context).inflate(R.layout.user_item, null);
            holder.user_iv_head = (ImageView) view.findViewById(R.id.user_iv_head);
            holder.user_tv_name = (TextView) view.findViewById(R.id.user_tv_name);
            holder.user_tv_decription = (TextView) view.findViewById(R.id.user_tv_decription);
            // holder.comments_text = (TextView) view.findViewById(R.id.comments_text);
            view.setTag(holder);
        }
        holder = (UsersHolder) view.getTag();
        UserInfo userInfo = list.get(position);
        
        // holder.user_iv_head.setText(userInfo.getScreen_name());
        holder.user_tv_name.setText(userInfo.getScreen_name());
        holder.user_tv_decription.setText(userInfo.getDescription());
        // 使用异步下载微博用户头像图片
        Drawable head_image = AsyncImageLoader.loadDrawable(userInfo.getProfile_image_url(), holder.user_iv_head,
                new ImageCallback() {
                    
                    @Override
                    public void imageSet(Drawable drawable, ImageView iv) {
                        iv.setImageDrawable(drawable);
                    }
                });
        if (head_image != null) {
            holder.user_iv_head.setImageDrawable(head_image);
        }
        return view;
    }
    
    private static class UsersHolder {
        
        ImageView user_iv_head;
        
        TextView user_tv_name;
        
        TextView user_tv_decription;
        
        // TextView comments_text;
        
    }
    
}
