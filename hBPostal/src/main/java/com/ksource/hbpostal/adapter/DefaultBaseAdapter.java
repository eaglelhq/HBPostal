package com.ksource.hbpostal.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class DefaultBaseAdapter<T> extends BaseAdapter {

	public List<T> datas;

	public DefaultBaseAdapter(List<T> datas){
		this.datas = datas;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
