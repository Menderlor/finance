package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;

import java.util.List;

public class ConstactStatusListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<String> mHashMapList;
	private Context mContext;

	public ConstactStatusListViewAdapter(Context pContext, List<String> pList,
			OnClickListener listener) {
		this.mContext = pContext;
		this.mHashMapList = pList;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mHashMapList.size();
	}

	@Override
	public String getItem(int arg0) {
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
			view = View.inflate(mContext,
					R.layout.taskclassify, null);
			holder = new ViewHolder();
			holder.Status = (TextView) view.findViewById(R.id.tv_classifyname);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

//		//
//		if (myAdapterCBListener != null) {
//			holder.viewBtn1.setOnClickListener(myAdapterCBListener);
//			holder.viewBtn2.setOnClickListener(myAdapterCBListener);
//		}

		String item = getItem(position);
		holder.Status.setText(item);
//		holder.viewBtn1.setTag(item);
//		holder.viewBtn2.setTag(item);
		// holder.viewBtn.setTag(item);
		// holder.viewBtn.setText(item.Name);
		// holder.viewBtn.setChecked(item.b);
		return view;
	}

	final class ViewHolder {
		// public ImageView img;
		public TextView Status;
	
	}
}
