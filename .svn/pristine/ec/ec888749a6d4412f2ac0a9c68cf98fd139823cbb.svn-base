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
import com.cedarhd.models.通知;
import com.cedarhd.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class NoticeListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<通知> mList;
	private Context mContext;
	private DictionaryHelper dictionaryHelper;
	int mlistviewlayoutId;
	private boolean isFling;// 是否滑动标识位

	public NoticeListViewAdapter(Context pContext, int listviewlayoutId,
			List<通知> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
		dictionaryHelper = new DictionaryHelper(pContext);
	}

	public void bindData(List<通知> list) {
		mList = new ArrayList<通知>();
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 通知 getItem(int arg0) {
		return mList.get(arg0);
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
			holder.ivReadDot = (ImageView) view
					.findViewById(R.id.iv_dot_read_noticelist);
			holder.imageView1 = (ImageView) view.findViewById(R.id.imageView1);
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.control_avatar_noticelist);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			holder.ivAttach = (ImageView) view
					.findViewById(R.id.iv_attach_notice_list_item);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		通知 item = getItem(position);
		// 根据屏幕宽度算出，是否省略标题多出的内容
		// double length = Math.floor(Global.mWidthPixels / 50);
		String title = item.Title;

		String content = item.Content;
		holder.textViewTitle.setText(title);
		String releaseTime = DateTimeUtil
				.ConvertLongDateToString(item.ReleaseTime);
		String time = (releaseTime == null) ? "" : DateDeserializer
				.getFormatTime(releaseTime);
		holder.textViewTime.setText(time);
		holder.textViewContent.setText(content);
		holder.avartarView.setTag(position);
		AvartarViewHelper avartarViewHelper = new AvartarViewHelper(mContext,
				item.Publisher, holder.avartarView, position, 65, 65, true);
		holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);
		if (!TextUtils.isEmpty(item.Attachment)) {
			holder.ivAttach.setVisibility(View.VISIBLE);
		} else {
			holder.ivAttach.setVisibility(View.GONE);
		}
		String read = item.ReadTime;
		if (!TextUtils.isEmpty(read)) {
			// view.setBackgroundResource(R.color.mail_read_bg); // 已读
			holder.ivReadDot.setVisibility(View.INVISIBLE);
			avartarViewHelper.setRead(true);
		} else {
			// view.setBackgroundResource(R.color.mail_unread_bg); // 未读
			// holder.ivReadDot.setVisibility(View.VISIBLE);
			holder.ivReadDot.setVisibility(View.INVISIBLE);
			avartarViewHelper.setRead(false);
		}
		return view;
	}

	public List<通知> getDataList() {
		return mList;
	}

	public void setFling(boolean isFling) {
		this.isFling = isFling;
	}

	final class ViewHolder {
		public ImageView ivReadDot;
		public ImageView imageView1;
		public TextView textViewTitle;
		public AvartarView avartarView;
		public TextView textViewTime;
		public TextView textViewContent;
		public ImageView ivAttach;
	}
}
