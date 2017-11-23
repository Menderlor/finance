package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.测量信息;

import java.util.List;

public class TestListViewAdapter extends BaseAdapter {
	private List<测量信息> mList;
	private Context mContext;
	int mlistviewlayoutId;
	DictionaryHelper dictionaryHelper;

	public TestListViewAdapter(Context pContext, int listviewlayoutId,
			List<测量信息> pList) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;

		dictionaryHelper = new DictionaryHelper(mContext);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 测量信息 getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = convertView;
		if (view == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			ViewHolder vh = new ViewHolder();
			vh.tvDate = (TextView) view
					.findViewById(R.id.tv_date_testlist_item);
			vh.tvAddress = (TextView) view
					.findViewById(R.id.tv_address_testlist_item);
			vh.tvDesigner = (TextView) view
					.findViewById(R.id.tv_designer_testlist_item);
			view.setTag(vh);
		}
		测量信息 item = mList.get(position);
		ViewHolder vHolder = (ViewHolder) view.getTag();
		String dateTime = item.getDate();
		if (!TextUtils.isEmpty(dateTime) && dateTime.endsWith("00:00:00")) {
			dateTime = dateTime.replaceAll("00:00:00", "");
		}
		vHolder.tvDate.setText(dateTime);
		vHolder.tvAddress.setText(item.getAddress());
		String designereName = dictionaryHelper.getUserNameById(item
				.getDesigner());
		vHolder.tvDesigner.setText(designereName);
		return view;
	}

	final class ViewHolder {
		public TextView tvDate;
		public TextView tvAddress;
		public TextView tvDesigner;
		// public TextView tvRemark;
		// public TextView tvStaff; // 制单人
		// public TextView tvType; // 测试类型
	}
}