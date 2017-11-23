package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.Client;

import java.util.List;

/**
 * 客户内容适配器
 * 
 * @author BOHR
 * 
 */
public class CustomerListViewAdapter extends BaseAdapter {

	private List<Client> m客户List;
	private Context mContext;
	int mlistviewlayoutId;

	public CustomerListViewAdapter(Context pContext, int listviewlayoutId,
			List<Client> pList) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.m客户List = pList;

	}

	@Override
	public int getCount() {
		return m客户List.size();
	}

	@Override
	public Client getItem(int arg0) {
		return m客户List.get(arg0);
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
			holder.imageView1 = (ImageView) view
					.findViewById(R.id.iv_customer_item);
			holder.textViewCustomerName = (TextView) view
					.findViewById(R.id.textViewCustomerName_item);
			holder.textViewContactName = (TextView) view
					.findViewById(R.id.textViewContactName_item);
			holder.textViewPhone = (TextView) view
					.findViewById(R.id.textViewPhone_item);
			holder.textViewAddress = (TextView) view
					.findViewById(R.id.textViewAddress_item);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Client item = getItem(position);
		// if (item.get("Read").toString().contains("'" + Global.mUser.Id +
		// "';"))
		// view.setBackgroundResource(R.color.mail_read_bg);
		// else
		// view.setBackgroundResource(R.color.mail_unread_bg);
		holder.textViewCustomerName.setText(item.getCustomerName());
		String contactName = TextUtils.isEmpty(item.getContacts()) ? "" : item
				.getContacts();
		holder.textViewContactName.setText(contactName);
		holder.textViewPhone.setText(item.getPhone());
		holder.textViewAddress.setText(item.getAddress());
		holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);

		String read = item.ReadTime;
		if (!TextUtils.isEmpty(read)) {
			view.setBackgroundResource(R.color.mail_read_bg); // 已读
		} else {
			view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		}
		return view;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView textViewCustomerName;
		public TextView textViewContactName;
		public TextView textViewPhone;
		public TextView textViewAddress;
	}

	public List<Client> getM客户List() {
		return m客户List;
	}

	public void setM客户List(List<Client> m客户List) {
		this.m客户List = m客户List;
	}

}
