package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.订单;

import java.util.List;

public class OrderListViewAdapter extends BaseAdapter {
	View.OnClickListener myAdapterCBListener;
	private List<订单> mHashMapList;
	private Context mContext;
	int mlistviewlayoutId;

	/**
	 * 订单详情适配器构造函数
	 * 
	 * @param mHashMapList
	 *            订单列表
	 * @param mContext
	 *            当前上下文信息
	 * @param mlistviewlayoutId
	 *            ListView填充布局的Id
	 * @param myAdapterCBListener
	 *            监听事件
	 */
	public OrderListViewAdapter(Context mContext, int mlistviewlayoutId,
			List<订单> mHashMapList, OnClickListener myAdapterCBListener) {
		this.myAdapterCBListener = myAdapterCBListener;
		this.mHashMapList = mHashMapList;
		this.mContext = mContext;
		this.mlistviewlayoutId = mlistviewlayoutId;
	}

	@Override
	public int getCount() {
		return mHashMapList.size();
	}

	@Override
	public 订单 getItem(int position) {
		return mHashMapList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if (view == null || view.getTag() == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			// holder.OrderNO = (TextView) view
			// .findViewById(R.id.tv_oderlist__oderNo);
			holder.tvContactName = (TextView) view
					.findViewById(R.id.tv_oderlist_clientName);
			holder.tvAddress = (TextView) view
					.findViewById(R.id.tv_oderlist_address);
			holder.tvProject = (TextView) view
					.findViewById(R.id.tv_oderlist_project);
			holder.tvMeasureTime = (TextView) view
					.findViewById(R.id.tv_oderlist_planMessuret);
			holder.tvState = (TextView) view
					.findViewById(R.id.tv_oderlist_state);
		}

		订单 item = mHashMapList.get(position);
		// holder.OrderNO.setText("单号:" + item.OrderNo);
		// holder.tvProject.setText("定做项目:" + item.CustomProject);
		holder.tvProject.setText(item.CustomProject);
		holder.tvAddress.setText("地址:" + item.Address);
		if (!TextUtils.isEmpty(item.ClientName)) {
			holder.tvContactName.setText("联系人:" + item.ClientName);
		} else {
			holder.tvContactName.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(item.MeasureTime)) {
			// 预计测量时间不显示时分秒
			String measureTime = item.MeasureTime;
			if (measureTime.length() >= 10) {
				measureTime = measureTime.substring(0, 10);
			}
			holder.tvMeasureTime.setText("预计测量时间:" + measureTime);
		}
		holder.tvState.setText("阶段:" + item.StageName);

		String readId = "";
		view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		if (!TextUtils.isEmpty(item.Readed)) {
			readId = "," + item.Readed + ",";
			if (readId.contains("," + Global.mUser.Id + ",")) {
				view.setBackgroundResource(R.color.mail_read_bg); // 已读
			}
		}
		return view;
	}

	final class ViewHolder {
		// OrderNO, ClientName, Total, OrderTime, AccountApproveTime,
		// MeasureTime, state
		// public TextView OrderNO;
		// public TextView Total; // 金额
		public TextView tvProject; // 定做项目
		public TextView tvAddress; // 地址
		public TextView tvContactName; // 联系人
		public TextView tvMeasureTime; // 预计测量时间
		public TextView tvState; // 阶段
	}

}
