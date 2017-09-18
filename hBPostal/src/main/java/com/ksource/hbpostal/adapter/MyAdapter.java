package com.ksource.hbpostal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ksource.hbpostal.R;
import com.ksource.hbpostal.activity.ActivityCate;

/**
 * 商品分类-一级分类列表Adapter
 */
public class MyAdapter extends BaseAdapter {

	private Context context;
	private String[] strings;
	public static int mPosition;
	
	public MyAdapter(Context context, String[] strings){
		this.context =context;
		this.strings = strings;
	}
	
	@Override
	public int getCount() {
		return strings.length;
	}

	@Override
	public Object getItem(int position) {
		return strings[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
		TextView tv = (TextView) convertView.findViewById(R.id.tv_tishi);
		mPosition = position;
		tv.setText(strings[position]);
		if (position == ActivityCate.mPosition) {
			convertView.setBackgroundResource(R.drawable.tongcheng_all_bg01);
			tv.setTextColor(Color.rgb(1, 133, 62));
		} else {
			convertView.setBackgroundColor(Color.WHITE);
			tv.setTextColor(Color.BLACK);
		}
		return convertView;
	}
}
