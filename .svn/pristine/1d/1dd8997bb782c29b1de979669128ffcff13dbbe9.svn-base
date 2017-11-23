package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.ListItemContactHistory;

import java.util.ArrayList;

public class ContactHistoryDetailListViewAdapter extends BaseAdapter {
	// View.OnClickListener myAdapterCBListener;
	private ArrayList<ListItemContactHistory> mArrayList;
	private Context mContext;
	int mlistviewlayoutId;

	public ContactHistoryDetailListViewAdapter(Context pContext,
			int listviewlayoutId, ArrayList<ListItemContactHistory> pList) {
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
			holder.sectionName = (TextView) view
					.findViewById(R.id.textViewSectionName);
			holder.content = (TextView) view.findViewById(R.id.textViewContent);
			// holder.textViewCustomerClassification = (TextView) view
			// .findViewById(R.id.textViewCustomerClassification);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ListItemContactHistory item = (ListItemContactHistory) getItem(position);
		// if (item.get("Read").toString().contains("'" + Global.mUser.Id +
		// "';"))
		// view.setBackgroundResource(R.color.mail_read_bg);
		// else
		// view.setBackgroundResource(R.color.mail_unread_bg);

		holder.sectionName.setText(item.mSection);
		holder.content.setText(item.mContent);
		// holder.textViewCustomerClassification.setText("客户类型：" +
		// item.get("ClassificationName").toString());
		// holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);
		return view;
	}

	final class ViewHolder {
		public TextView sectionName;
		public TextView content;
	}

	@Override
	public ListItemContactHistory getItem(int position) {
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
