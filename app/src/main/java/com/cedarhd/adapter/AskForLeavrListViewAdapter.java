package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.请假;
import com.cedarhd.utils.LogUtils;

import java.util.List;

/**
 * @author Administrator 请假ListView的适配器
 */
public class AskForLeavrListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<请假> mList;
	private Context mContext;
	int mlistviewlayoutId;

	public AskForLeavrListViewAdapter(Context pContext, int listviewlayoutId,
			List<请假> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 请假 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		if (position >= 15) {
			LogUtils.e("", "");
		}
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle);
			holder.textViewPublisherName = (TextView) view
					.findViewById(R.id.textViewPublisherName);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			holder.textViewUserId = (TextView) view
					.findViewById(R.id.textViewUserId);
			holder.textViewTimeType = (TextView) view
					.findViewById(R.id.textViewTimeType);
			holder.textViewVacationType = (TextView) view
					.findViewById(R.id.textViewVacationType);
			holder.textViewDeadline = (TextView) view
					.findViewById(R.id.textViewDeadline);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		请假 item = getItem(position);
		// if (item.Read.contains("'" + Global.mUser.Id + "';"))
		// view.setBackgroundResource(R.color.mail_read_bg);
		// else
		// view.setBackgroundResource(R.color.mail_unread_bg);

		holder.textViewTitle.setText(item.getEmployee() + "请假单");
		holder.textViewPublisherName.setText(item.getEmployee());
		holder.textViewTime.setText(item.getUpdateTime());// DateTimeUtil.ConvertLongDateToString(item.ReleaseTime));
		holder.textViewContent.setText(item.getAskForLeaveCase());
		holder.textViewUserId.setText("" + item.getUserId());
		holder.textViewTimeType.setText("时间类型:" + item.getTimeType());
		holder.textViewVacationType.setText(item.getVacationType());
		holder.textViewDeadline
				.setText("请假天数:" + item.getAskForLeaveDeadline());
		holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);
		return view;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView textViewTitle;
		public TextView textViewPublisherName;
		public TextView textViewTime;
		public TextView textViewContent;
		public TextView textViewUserId;
		public TextView textViewTimeType;
		public TextView textViewVacationType;
		public TextView textViewDeadline;
	}
}
