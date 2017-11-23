package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.周工作总结;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import java.util.List;

/**
 * 周工作总结ListView的适配器
 * 
 * @author kjx
 */
public class WeekLogListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<周工作总结> mList;
	private Context mContext;
	int mlistviewlayoutId;
	private DictionaryHelper dictHelper;
	private int REQUEST_CODE_ASKFOR_ME = 18;

	public WeekLogListViewAdapter(Context pContext, int listviewlayoutId,
			List<周工作总结> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
		dictHelper = new DictionaryHelper(pContext);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 周工作总结 getItem(int arg0) {
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
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.tv_content_weeklog);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.tv_time_weeklog);
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.av_weeklog);
			holder.tvDept = (TextView) view.findViewById(R.id.tv_dept_weeklog);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		// 判断是否读过
		final 周工作总结 item = getItem(position);
		// String read = item.已读时间;
		// if (!TextUtils.isEmpty(read)) {
		// view.setBackgroundResource(R.color.mail_read_bg); // 已读
		// } else {
		// view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		// }

		String content = TextUtils.isEmpty(item.本周已完成工作) ? "" : item.本周已完成工作;
		holder.textViewTitle.setText(content + "");
		String timeStr = DateDeserializer.getFormatDate(item.开始时间) + " 至 "
				+ DateDeserializer.getFormatDate(item.结束时间);
		holder.textViewTime.setText(timeStr);
		holder.avartarView.setTag(position);
		holder.tvDept.setText(StrUtils.pareseNull(dictHelper.get分公司(mContext,
				item.创建人).get名称()));
		new AvartarViewHelper(mContext, item.创建人, holder.avartarView, position,
				70, 70, true);
		return view;
	}

	/***
	 * 
	 * @return
	 */
	public List<周工作总结> getDataList() {
		return mList;
	}

	public void setmList(List<周工作总结> mList) {
		this.mList = mList;
	}

	final class ViewHolder {
		public TextView textViewTitle;
		public TextView textViewTime;
		public AvartarView avartarView;
		public TextView tvDept;
	}
}
