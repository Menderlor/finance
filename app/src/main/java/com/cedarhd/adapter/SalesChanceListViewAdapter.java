package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.销售机会;

import java.util.List;

public class SalesChanceListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<销售机会> mHashMapList;
	private Context mContext;
	int mlistviewlayoutId;

	public SalesChanceListViewAdapter(Context pContext, int listviewlayoutId,
			List<销售机会> pList, OnClickListener listener) {
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
	public 销售机会 getItem(int arg0) {
		return mHashMapList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public List<销售机会> getDataList() {
		return mHashMapList;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.imageView1 = (ImageView) view
					.findViewById(R.id.iv_ico_salechance);
			holder.textViewCustomerName = (TextView) view
					.findViewById(R.id.tv_clientname_salechance);
			holder.textViewContactsName = (TextView) view
					.findViewById(R.id.tv_salechanceName_salechance);
			holder.textViewPhone = (TextView) view
					.findViewById(R.id.tv_phone_salechance);
			// holder.textViewAddress = (TextView) view
			// .findViewById(R.id.tv_address_salechance);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.tv_content_salechance);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		销售机会 item = getItem(position);
		holder.textViewCustomerName.setText(item.getCustomerName());
		holder.textViewContactsName.setText(item.getContacts());
		holder.textViewPhone.setText(item.getPhone());
		holder.textViewContent.setText(item.getContent());
		// view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		String read = item.ReadTime;
		if (!TextUtils.isEmpty(read)) {
			// view.setBackgroundResource(R.color.mail_read_bg); // 已读
		} else {
			// view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		}
		return view;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView textViewCustomerName;
		public TextView textViewContactsName;
		public TextView textViewPhone;
		public TextView textViewContent;
	}
}
