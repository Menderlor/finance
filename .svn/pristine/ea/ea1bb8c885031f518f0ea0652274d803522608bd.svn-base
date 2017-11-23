package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.流程;

import java.util.List;

public class ProcessListViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<流程> mList;

	public ProcessListViewAdapter(Context context, List<流程> list) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mList = list;
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
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.process_list_item,
					null);
			holder.mTextViewClassifi = (TextView) convertView
					.findViewById(R.id.textViewClassifi);
			holder.mTextViewState = (TextView) convertView
					.findViewById(R.id.textViewState);
			holder.mTextViewCreateDate = (TextView) convertView
					.findViewById(R.id.textViewCreateDate);
			holder.mTextViewCreateUser = (TextView) convertView
					.findViewById(R.id.textViewCreateUser);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 更改之前
		// holder.mTextViewClassifi.setText(mList.get(position).流程分类名称);
		// holder.mTextViewState.setText(mList.get(position).当前状态);
		// holder.mTextViewCreateDate.setText(mList.get(position).创建时间.toLocaleString());
		// holder.mTextViewCreateUser.setText(mList.get(position).创建人名称);
		holder.mTextViewClassifi.setText(mList.get(position).ClassTypeName);
		holder.mTextViewState.setText(mList.get(position).CurrentState);
		holder.mTextViewCreateDate.setText(mList.get(position).CraeteDate);// .toLocaleString());转化时间格式
		holder.mTextViewCreateUser.setText(mList.get(position).CreateName);
		return convertView;
	}

	private class ViewHolder {
		TextView mTextViewClassifi;
		TextView mTextViewState;
		TextView mTextViewCreateDate;
		TextView mTextViewCreateUser;
	}
}
