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
import com.limeng.xinlangweibo.util.HighLight;

public class CommentsAdapter extends BaseAdapter {
    
    private List<Comment> list;
    
    private CommentsHolder holder;
    
    public void setList(List<Comment> list) {
        this.list = list;
    }
    
    private Context context;
    
    public CommentsAdapter(Context context) {
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
    
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        View view = convertView;
        
        if (null == view) {
            
            holder = new CommentsHolder();
            view = LayoutInflater.from(context).inflate(R.layout.comments_item, null);
            holder.detail_iv_head = (ImageView) view.findViewById(R.id.detail_iv_head);
            holder.comments_tv_name = (TextView) view.findViewById(R.id.comments_tv_name);
            holder.comments_time = (TextView) view.findViewById(R.id.comments_time);
            holder.comments_text = (TextView) view.findViewById(R.id.comments_text);
            view.setTag(holder);
        }
        holder = (CommentsHolder) view.getTag();
        Comment comment = list.get(position);
        UserInfo userInfo = comment.getUserInfo();
        holder.comments_tv_name.setText(userInfo.getScreen_name());
        holder.comments_time.setText(new SimpleDateFormat("MM-dd hh:ss").format(new Date(comment.getCreated_at())));
        HighLight.setHighLightText(holder.comments_text, comment.getText());
        // holder.comments_text.setText(comment.getText());
        // 使用异步下载微博用户头像图片
        Drawable head_image = AsyncImageLoader.loadDrawable(userInfo.getProfile_image_url(), holder.detail_iv_head,
                new ImageCallback() {
                    
                    @Override
                    public void imageSet(Drawable drawable, ImageView iv) {
                        iv.setImageDrawable(drawable);
                    }
                });
        if (head_image != null) {
            holder.detail_iv_head.setImageDrawable(head_image);
        }
        return view;
    }
    
    private static class CommentsHolder {
        
        ImageView detail_iv_head;
        
        TextView comments_tv_name;
        
        TextView comments_time;
        
        TextView comments_text;
        
    }
    
}
