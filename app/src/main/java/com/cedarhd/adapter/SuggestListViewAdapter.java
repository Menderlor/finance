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
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.客户投诉建议;
import com.cedarhd.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

public class SuggestListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<客户投诉建议> mList;
	private Context mContext;
	private DictionaryHelper dictionaryHelper;
	int mlistviewlayoutId;
	private boolean isFling;// 是否滑动标识位

	public SuggestListViewAdapter(Context pContext, int listviewlayoutId,
			List<客户投诉建议> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
		dictionaryHelper = new DictionaryHelper(pContext);
	}

	public void bindData(List<客户投诉建议> list) {
		mList = new ArrayList<客户投诉建议>();
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 客户投诉建议 getItem(int arg0) {
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
			holder.imageView1 = (ImageView) view.findViewById(R.id.imageView_suggestlist);
			holder.clientName = (TextView) view
					.findViewById(R.id.tv_clientname_suggestlist);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime_suggestlist);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent_suggestlist);
			holder.ivAttach = (ImageView) view
					.findViewById(R.id.iv_attach_suggestlist_item);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		客户投诉建议 item = getItem(position);

		// 根据屏幕宽度算出，是否省略标题多出的内容
		// double length = Math.floor(Global.mWidthPixels / 50);
//		String title = item.Title;
		// if (title.length() > length) {
		// title = title.substring(0, (int) length) + "...";
		// }

		// // 根据屏幕宽度算出，是否省略多出的内容
		// length = Math.floor(Global.mWidthPixels / 17);
		// String content = item.Content;
		// if (content.length() > length) {
		// content = content.substring(0, (int) length) + "...";
		// }

		String content = item.Content;
//		String releaseTime = DateTimeUtil
//				.ConvertLongDateToString(item.UpdateTime);
//		String time = (releaseTime == null) ? "" : DateDeserializer
//				.getFormatTime(releaseTime);
		holder.textViewTime.setText(DateTimeUtil
				.ConvertLongDateToString(item.Time));
		holder.textViewContent.setText(content);
		if(item.ClientName == null){
			holder.clientName.setText("未知客户");
		}else{
			holder.clientName.setText(item.ClientName);
		}
		
		// new AvartarViewHelper(mContext, item.Publisher, holder.avartarView,
		// position);
//		new AvartarViewHelper(mContext, item.Publisher, holder.avartarView,
//				position, 70, 70, false);
		holder.imageView1.setBackgroundResource(R.drawable.notice_icon01);
		view.setBackgroundResource(R.color.mail_read_bg);
//		view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		if (!TextUtils.isEmpty(item.Attachment)) {
			holder.ivAttach.setVisibility(View.VISIBLE);
		} else {
			holder.ivAttach.setVisibility(View.GONE);
		}
//		if (!TextUtils.isEmpty(item.getRead())) {
//			if (item.Read.trim().equals("1")) {
//				view.setBackgroundResource(R.color.mail_read_bg); // 已读
//			}
//		}
		return view;
	}

	public void setFling(boolean isFling) {
		this.isFling = isFling;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView clientName;
		public TextView textViewTime;
		public TextView textViewContent;
		public ImageView ivAttach;
	}
}
