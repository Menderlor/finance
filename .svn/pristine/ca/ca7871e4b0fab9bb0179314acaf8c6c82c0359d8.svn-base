package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;

import java.util.HashMap;
import java.util.List;

public class Task_SenderListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<HashMap<String, Object>> mHashMapList;
	private Context mContext;
	int mlistviewlayoutId;

	public Task_SenderListViewAdapter(Context pContext, int listviewlayoutId,
			List<HashMap<String, Object>> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mHashMapList = pList;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mHashMapList.size();
	}

	@Override
	public HashMap<String, Object> getItem(int arg0) {
		return mHashMapList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.textViewId = (TextView) view.findViewById(R.id.textViewId);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle);
			holder.textViewPublisherName = (TextView) view
					.findViewById(R.id.textViewPublisherName);
			holder.textViewExecutorName = (TextView) view
					.findViewById(R.id.textViewExecutorName);
			holder.textViewStatusName = (TextView) view
					.findViewById(R.id.textViewStatusName);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		HashMap<String, Object> item = getItem(position);

		// 根据屏幕宽度算出，是否省略标题多出的内容
		double length = Math.floor(Global.mWidthPixels / 35);
		String title = item.get("Title").toString();
		if (item.get("Title").toString().length() > length) {
			title = item.get("Title").toString().substring(0, (int) length)
					+ "...";
		}

		// 根据屏幕宽度算出，内容是否省略
		length = Math.floor(Global.mWidthPixels / 11);
		String content = item.get("Content").toString();
		if (content.length() > length) {
			content = content.substring(0, (int) length) + "...";
		}

		holder.textViewId.setText(item.get("Id").toString());
		holder.textViewTitle.setText(title);
		holder.textViewPublisherName.setText("发送人："
				+ item.get("PublisherName").toString());
		holder.textViewExecutorName.setText("执行人："
				+ item.get("ExecutorName").toString());
		holder.textViewStatusName.setText(item.get("StatusName").toString());
		holder.textViewContent.setText(content);
		return view;
	}

	final class ViewHolder {
		public TextView textViewId;
		public TextView textViewTitle;
		public TextView textViewPublisherName;
		public TextView textViewExecutorName;
		public TextView textViewStatusName;
		public TextView textViewContent;
	}
}
