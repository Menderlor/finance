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
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.日志;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import java.util.List;

public class LogListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<日志> mDataList;
	private Context mContext;
	int mlistviewlayoutId;
	ORMDataHelper helper;
	private PictureUtils handlerUtils;
	private DictionaryHelper dictionaryHelper;

	public LogListViewAdapter(Context pContext, int listviewlayoutId,
			List<日志> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mDataList = pList;
		this.myAdapterCBListener = listener;
		handlerUtils = new PictureUtils();
		helper = ORMDataHelper.getInstance(mContext);
		dictionaryHelper = new DictionaryHelper(mContext);
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public 日志 getItem(int arg0) {
		return mDataList.get(arg0);
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
			// holder.textViewPersonnelName = (TextView) view
			// .findViewById(R.id.textViewPersonnelName);
			holder.worklog_avatar = (AvartarView) view
					.findViewById(R.id.worklog_avatar);
			holder.ivDotRead = (ImageView) view
					.findViewById(R.id.iv_dot_read_loglist);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			holder.DiscussCount = (TextView) view
					.findViewById(R.id.DiscussCount);
			holder.tvDept = (TextView) view.findViewById(R.id.tv_dept_work_log);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		日志 item = getItem(position);

		String content = TextUtils.isEmpty(item.getContent()) ? "" : item
				.getContent();
		LogUtils.i("worklog", content);
		holder.tvDept.setText(StrUtils.pareseNull(dictionaryHelper.get分公司(
				mContext, item.Personnel).get名称()));
		String time = item.getTime() != null ? DateDeserializer
				.getFormatTime(item.getTime()) : "";
		String count = item.DiscussCount != 0 ? item.DiscussCount + "评" : "";
		holder.DiscussCount.setText(count);
		holder.textViewTime.setText(time);
		holder.textViewContent.setText(content + "");

		holder.worklog_avatar.setTag(position);
		AvartarViewHelper avartarViewHelper = new AvartarViewHelper(mContext,
				item.getPersonnel(), holder.worklog_avatar, position, 60, 60,
				true);
		String read = item.ReadTime;
		holder.ivDotRead.setVisibility(View.INVISIBLE); // 暂时隐藏左侧红点
		if (!TextUtils.isEmpty(read)) {
			// holder.ivDotRead.setVisibility(View.INVISIBLE);
			avartarViewHelper.setRead(true);
		} else {
			// holder.ivDotRead.setVisibility(View.VISIBLE);
			avartarViewHelper.setRead(false);
		}
		return view;
	}

	public List<日志> getDataList() {
		return mDataList;
	}

	final class ViewHolder {
		public AvartarView worklog_avatar;
		public ImageView ivDotRead;
		public TextView tvDept;
		public TextView textViewTime;
		public TextView textViewContent;
		public TextView DiscussCount;
	}
}
