package com.limeng.xinlangweibo.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.limeng.xinlangweibo.R;

public class MoreListViewAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	private List<Map<String, Object>> datas;

	public MoreListViewAdapter(Context ctx)
	{
		inflater = LayoutInflater.from(ctx);
	}

	public void setDatas(List<Map<String, Object>> datas)
	{
		this.datas = datas;
	}

	@Override
	public int getCount()
	{

		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position)
	{

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = inflater.inflate(R.layout.list_item_more, null);
		ImageView iv_left = (ImageView) v.findViewById(R.id.iv_left);
		ImageView iv_right = (ImageView) v.findViewById(R.id.iv_right);
		TextView tv_title = (TextView) v.findViewById(R.id.tv_title);

		Map<String, Object> item_data = datas.get(position);

		Integer i = (Integer) item_data.get("left");
		iv_left.setImageResource(i.intValue());

		String str = (String) item_data.get("title");
		tv_title.setText(str);

		Boolean flag = (Boolean) item_data.get("right");
		if (flag.booleanValue())
		{
			iv_right.setVisibility(View.VISIBLE);
		} else
		{
			iv_right.setVisibility(View.INVISIBLE);
		}

		if (0 == position)
		{
			v.setBackgroundResource(R.drawable.fist_selector);
		} else if (position == datas.size() - 1)
		{
			v.setBackgroundResource(R.drawable.last_selector);

		} else
		{
			v.setBackgroundResource(R.drawable.middle_selector);
		}
		return v;

	}

}
