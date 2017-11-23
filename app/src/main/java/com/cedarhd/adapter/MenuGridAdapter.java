package com.cedarhd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.cedarhd.helpers.ViewHelper;

import java.util.List;

/**
 * 首页菜单GridView内容适配器
 * 
 * @author Administrator
 * 
 */
public class MenuGridAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Integer> list;

	public MenuGridAdapter(Context context, List<Integer> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
	}

	@Override
	public int getCount() // TODO Auto-generated method stub
	{
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int height = (int) ViewHelper.dip2px(context, 88);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				new LayoutParams(LayoutParams.WRAP_CONTENT, height));
		View view = inflater.inflate(list.get(position), null);
		view.setLayoutParams(lp);
		return view;
	}
}
