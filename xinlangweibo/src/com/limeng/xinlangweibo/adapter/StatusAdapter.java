/**
 *WeiboAdapter.java
 *2011-9-29 下午08:18:26
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.limeng.xinlangweibo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limeng.xinlangweibo.R;
import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.pojo.UserInfo;
import com.limeng.xinlangweibo.util.AsyncImageLoader;
import com.limeng.xinlangweibo.util.AsyncImageLoader.ImageCallback;
import com.limeng.xinlangweibo.util.HighLight;

public class StatusAdapter extends BaseAdapter {
    
    private List<Status> list;
    
    private WeiboHolder holder;
    
    public void setList(List<Status> list) {
        this.list = list;
    }
    
    private Context context;
    
    public StatusAdapter(Context context) {
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
            
            holder = new WeiboHolder();
            view = LayoutInflater.from(context).inflate(R.layout.wb_item_template, null);
            holder.txt_wb_item_uname = (TextView) view.findViewById(R.id.txt_wb_item_uname);
            holder.txt_wb_item_content = (TextView) view.findViewById(R.id.txt_wb_item_content);
            holder.txt_wb_item_from = (TextView) view.findViewById(R.id.txt_wb_item_from);
            holder.img_wb_item_head = (ImageView) view.findViewById(R.id.img_wb_item_head);
            holder.txt_wb_item_time = (TextView) view.findViewById(R.id.txt_wb_item_time);
            holder.txt_wb_item_redirect = (TextView) view.findViewById(R.id.txt_wb_item_redirect);
            holder.txt_wb_item_comment = (TextView) view.findViewById(R.id.txt_wb_item_comment);
            holder.img_wb_item_V = (ImageView) view.findViewById(R.id.img_wb_item_V);
            holder.lyt_wb_item_sublayout = (LinearLayout) view.findViewById(R.id.lyt_wb_item_sublayout);
            holder.img_wb_item_content_pic = (ImageView) view.findViewById(R.id.img_wb_item_content_pic);
            holder.txt_wb_item_subcontent = (TextView) view.findViewById(R.id.txt_wb_item_subcontent);
            holder.img_wb_item_content_subpic = (ImageView) view.findViewById(R.id.img_wb_item_content_subpic);
            view.setTag(holder);
        }
        Status status = list.get(position);
        holder = (WeiboHolder) view.getTag();
        holder.txt_wb_item_time.setText(status.getCreated_at());
        holder.txt_wb_item_redirect.setText(status.getReposts_count());
        holder.txt_wb_item_comment.setText(status.getComments_count());
        
        HighLight.setHighLightText(holder.txt_wb_item_content, status.getText());
        // holder.txt_wb_item_content.setText(status.getText());
        holder.txt_wb_item_from.setText(status.getSource());
        
        UserInfo userInfo = status.getUserInfo();
        holder.txt_wb_item_uname.setText(userInfo.getScreen_name());
        // 使用异步下载微博用户头像图片
        Drawable head_image = AsyncImageLoader.loadDrawable(userInfo.getProfile_image_url(), holder.img_wb_item_head,
                new ImageCallback() {
                    
                    @Override
                    public void imageSet(Drawable drawable, ImageView iv) {
                        iv.setImageDrawable(drawable);
                    }
                });
        if (head_image != null) {
            holder.img_wb_item_head.setImageDrawable(head_image);
        }
        
        // 判断是否通过认证
        if (userInfo.isVerified()) {
            holder.img_wb_item_V.setVisibility(View.VISIBLE);
        } else {
            holder.img_wb_item_V.setVisibility(View.GONE);
        }
        
        // 判断微博中是否含有图片
        if (status.getThumbnail_pic() != null) {
            holder.img_wb_item_content_pic.setVisibility(View.VISIBLE);
            // 使用异步下载图片
            Drawable weibo_image = AsyncImageLoader.loadDrawable(status.getThumbnail_pic(),
                    holder.img_wb_item_content_pic, new ImageCallback() {
                        
                        @Override
                        public void imageSet(Drawable drawable, ImageView iv) {
                            iv.setImageDrawable(drawable);
                        }
                    });
            if (weibo_image != null) {
                holder.img_wb_item_content_pic.setImageDrawable(weibo_image);
            }
        } else {
            holder.img_wb_item_content_pic.setVisibility(View.GONE);
        }
        
        // 判断是否有转发
        if (status.getRetweeted_status() != null) {
            Status retweeted_status = status.getRetweeted_status();
            holder.lyt_wb_item_sublayout.setVisibility(View.VISIBLE);
            // holder.txt_wb_item_subcontent.setText(retweeted_status.getText());
            HighLight.setHighLightText(holder.txt_wb_item_subcontent, retweeted_status.getText());
            // 判断转发微博中是否含有图片
            if (retweeted_status.getThumbnail_pic() != null) {
                holder.img_wb_item_content_subpic.setVisibility(View.VISIBLE);
                // 使用异步下载图片
                Drawable image = AsyncImageLoader.loadDrawable(retweeted_status.getThumbnail_pic(),
                        holder.img_wb_item_content_subpic, new ImageCallback() {
                            
                            @Override
                            public void imageSet(Drawable drawable, ImageView iv) {
                                iv.setImageDrawable(drawable);
                            }
                        });
                if (image != null) {
                    holder.img_wb_item_content_subpic.setImageDrawable(image);
                }
            } else {
                holder.img_wb_item_content_subpic.setVisibility(View.GONE);
            }
            
        } else {
            holder.lyt_wb_item_sublayout.setVisibility(View.GONE);
        }
        
        return view;
    }
    
    private static class WeiboHolder {
        
        ImageView img_wb_item_head;
        
        TextView txt_wb_item_uname;
        
        ImageView img_wb_item_V;
        
        TextView txt_wb_item_time;
        
        TextView txt_wb_item_content;
        
        ImageView img_wb_item_content_pic;
        
        LinearLayout lyt_wb_item_sublayout;
        
        TextView txt_wb_item_subcontent;
        
        ImageView img_wb_item_content_subpic;
        
        TextView txt_wb_item_from;
        
        TextView txt_wb_item_redirect;
        
        TextView txt_wb_item_comment;
        
    }
    
}
