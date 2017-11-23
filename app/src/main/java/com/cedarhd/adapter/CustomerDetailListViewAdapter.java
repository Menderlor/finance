package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;

import java.util.ArrayList;

public class CustomerDetailListViewAdapter extends BaseAdapter {
	// View.OnClickListener myAdapterCBListener;
	private ArrayList<String> mArrayList;
	private Context mContext;
	int mlistviewlayoutId;

	public CustomerDetailListViewAdapter(Context pContext,
			int listviewlayoutId, ArrayList<String> pList) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mArrayList = pList;
		// this.myAdapterCBListener = listener;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
			holder.textViewFunctionName = (TextView) view
					.findViewById(R.id.textViewFunctionName);
			// holder.textViewCustomerClassification = (TextView) view
			// .findViewById(R.id.textViewCustomerClassification);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		String item = (String) getItem(position);
		// if (item.get("Read").toString().contains("'" + Global.mUser.Id +
		// "';"))
		// view.setBackgroundResource(R.color.mail_read_bg);
		// else
		// view.setBackgroundResource(R.color.mail_unread_bg);

		holder.textViewFunctionName.setText(item);
		// holder.textViewCustomerClassification.setText("客户类型：" +
		// item.get("ClassificationName").toString());
		// holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);
		return view;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView textViewFunctionName;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArrayList.size();
	}
}
