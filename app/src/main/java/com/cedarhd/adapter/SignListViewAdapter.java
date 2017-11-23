package com.cedarhd.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.考勤信息;

import java.util.List;

public class SignListViewAdapter extends BaseAdapter {

	Context mContext;
	List<考勤信息> mList;
	int layout;

	public SignListViewAdapter(Context context, int signlistListviewlayout,
			List<考勤信息> list, Object object) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.layout = signlistListviewlayout;
		this.mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();

		if (convertView == null) {
			convertView = View.inflate(mContext, layout, null);
			viewHolder.mLayoutContent = (LinearLayout) convertView
					.findViewById(R.id.layout_content);
			viewHolder.mTextViewDateValue = (TextView) convertView
					.findViewById(R.id.textViewDateValue);
			viewHolder.mTextViewSignInTime = (TextView) convertView
					.findViewById(R.id.textViewSignInTime);
			viewHolder.mTextViewStateSignIn = (TextView) convertView
					.findViewById(R.id.textViewStateSignIn);
			viewHolder.mTextViewAddressSignIn = (TextView) convertView
					.findViewById(R.id.textViewAddressSignIn);
			viewHolder.mTextViewSignOutTime = (TextView) convertView
					.findViewById(R.id.textViewSignOutTime);
			viewHolder.mTextViewStateSignOut = (TextView) convertView
					.findViewById(R.id.textViewStateSignOut);
			viewHolder.mTextViewAddressSignOut = (TextView) convertView
					.findViewById(R.id.textViewAddressSignOut);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (position % 2 == 0) {
			viewHolder.mLayoutContent.setBackgroundColor(Color.WHITE);
		} else {
			viewHolder.mLayoutContent.setBackgroundColor(Color.LTGRAY);
		}

		// 考勤信息 item = mList.get(position);
		// viewHolder.mTextViewDateValue.setText(DateTimeUtil.ConvertDateToString(item.get考勤日期()));
		// if (item.get签到时间() != null) {
		// viewHolder.mTextViewStateSignIn.setVisibility(View.VISIBLE);
		// viewHolder.mTextViewAddressSignIn.setVisibility(View.VISIBLE);
		// viewHolder.mTextViewSignInTime.setText(DateTimeUtil.ConvertLongDateToString(item.get签到时间()));
		// if (item.is是否迟到()) {
		// viewHolder.mTextViewStateSignIn.setText("迟到");
		// viewHolder.mTextViewStateSignIn.setTextColor(Color.RED);
		// } else {
		// viewHolder.mTextViewStateSignIn.setText("正常");
		// viewHolder.mTextViewStateSignIn.setTextColor(Color.GREEN);
		// }
		// if (item.get地理位置_签到() != null) {
		// viewHolder.mTextViewAddressSignIn.setText(item.get地理位置_签到());
		// } else {
		// viewHolder.mTextViewAddressSignIn.setVisibility(View.GONE);
		// }
		// } else {
		// viewHolder.mTextViewSignInTime.setText("无");
		// viewHolder.mTextViewStateSignIn.setVisibility(View.INVISIBLE);
		// viewHolder.mTextViewAddressSignIn.setVisibility(View.GONE);
		// }
		//
		// if (item.get签退时间() != null) {
		// viewHolder.mTextViewStateSignOut.setVisibility(View.VISIBLE);
		// viewHolder.mTextViewAddressSignOut.setVisibility(View.VISIBLE);
		// viewHolder.mTextViewSignOutTime.setText(DateTimeUtil.ConvertLongDateToString(item.get签退时间()));
		// if (item.is是否早退()) {
		// viewHolder.mTextViewStateSignOut.setText("早退");
		// viewHolder.mTextViewStateSignOut.setTextColor(Color.RED);
		// } else {
		// viewHolder.mTextViewStateSignOut.setText("正常");
		// viewHolder.mTextViewStateSignOut.setTextColor(Color.GREEN);
		// }
		//
		// if (item.get地理位置_签退() != null) {
		// viewHolder.mTextViewAddressSignOut.setText(item.get地理位置_签退());
		// } else {
		// viewHolder.mTextViewAddressSignOut.setVisibility(View.GONE);
		// }
		// } else {
		// viewHolder.mTextViewStateSignOut.setVisibility(View.INVISIBLE);
		// viewHolder.mTextViewAddressSignOut.setVisibility(View.GONE);
		// viewHolder.mTextViewSignOutTime.setText("无");
		// }

		return convertView;
	}

	private class ViewHolder {
		LinearLayout mLayoutContent;
		TextView mTextViewDateValue;
		TextView mTextViewSignInTime;
		TextView mTextViewStateSignIn;
		TextView mTextViewAddressSignIn;
		TextView mTextViewSignOutTime;
		TextView mTextViewStateSignOut;
		TextView mTextViewAddressSignOut;
	}
}
