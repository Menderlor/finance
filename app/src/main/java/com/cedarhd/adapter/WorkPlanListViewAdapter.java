package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;

import java.util.HashMap;
import java.util.List;

public class WorkPlanListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<HashMap<String, Object>> mHashMapList;
	private Context mContext;
	int mlistviewlayoutId;

	public WorkPlanListViewAdapter(Context pContext, int listviewlayoutId,
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
			holder.imageViewPlanStatus = (ImageView) view
					.findViewById(R.id.imageViewPlanStatus);
			holder.imageViewContactHistory = (ImageView) view
					.findViewById(R.id.imageViewContactHistory);
			holder.textViewCustomerName = (TextView) view
					.findViewById(R.id.textViewCustomerName);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		HashMap<String, Object> item = getItem(position);
		// if (item.get("Read").toString().contains("'" + Global.mUser.Id +
		// "';"))
		// view.setBackgroundResource(R.color.mail_read_bg);
		// else
		// view.setBackgroundResource(R.color.mail_unread_bg);

		holder.textViewCustomerName
				.setText(item.get("CustomerName").toString());
		holder.textViewContent.setText(item.get("Content").toString());
		return view;
	}

	final class ViewHolder {
		public ImageView imageViewPlanStatus;
		public ImageView imageViewContactHistory;
		public TextView textViewCustomerName;
		public TextView textViewContent;
	}
}
