package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.CheckBoxListViewItem;

import java.util.List;

public class CheckBoxListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<CheckBoxListViewItem> mList;
	private Context mContext;

	public CheckBoxListViewAdapter(Context pContext,
			List<CheckBoxListViewItem> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mList = pList;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public CheckBoxListViewItem getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, R.layout.checkboxlistviewitem, null);
			holder = new ViewHolder();
			holder.tv = (TextView) view.findViewById(R.id.item_tv);
			holder.cb = (CheckBox) view.findViewById(R.id.item_cb);
			view.setTag(holder);
		}

		//
		if (myAdapterCBListener != null) {
			holder.cb.setOnClickListener(myAdapterCBListener);
		}
		// holder.cb.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// boolean isChecked = ((CheckBox) v).isChecked();
		//
		// }
		// });

		CheckBoxListViewItem item = getItem(position);
		// holder.tv.setText(item.Name);
		holder.cb.setTag(item);
		holder.cb.setText(item.Name);
		holder.cb.setChecked(item.IsChecked);
		return view;
	}
}

class ViewHolder {
	public TextView tv = null;
	public CheckBox cb = null;
}