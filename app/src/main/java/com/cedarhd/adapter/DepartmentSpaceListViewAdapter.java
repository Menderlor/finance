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
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.models.帖子;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSpaceListViewAdapter<HandlerNewContact> extends BaseAdapter {
	View.OnClickListener myAdapterCBListener;
	private List<帖子> mList;
	private Context mContext;
	private DictionaryHelper dictionaryHelper;
	int mlistviewlayoutId;
	private boolean isFling;// 是否滑动标识位

	public DepartmentSpaceListViewAdapter(Context pContext, int listviewlayoutId,
			List<帖子> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
		dictionaryHelper = new DictionaryHelper(pContext);
	}

	public void bindData(List<帖子> list) {
		mList = new ArrayList<帖子>();
		mList.addAll(list);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 帖子 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null  || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.avatar_departmentspacelist);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle);
			holder.textViewPublisherName = (TextView) view
					.findViewById(R.id.textViewPublisherName);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent);
			holder.ivAttach = (ImageView) view.findViewById(R.id.iv_attachment);
//			holder.ivcompanylist = (ImageView) view.findViewById(R.id.iv_companylist);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		帖子 item = getItem(position);

		// 根据屏幕宽度算出，是否省略标题多出的内容
		// double length = Math.floor(Global.mWidthPixels / 50);
		String title = item.Title;
		// if (title.length() > length) {
		// title = title.substring(0, (int) length) + "...";
		// }

		// // 根据屏幕宽度算出，是否省略多出的内容
		// length = Math.floor(Global.mWidthPixels / 17);
		// String content = item.Content;
		// if (content.length() > length) {
		// content = content.substring(0, (int) length) + "...";
		// }
//		if(position ==0){
//			holder.ivcompanylist.setVisibility(View.VISIBLE);
//		}
		String content = item.Content;
		holder.textViewTitle.setText(title);
		holder.textViewPublisherName.setText(dictionaryHelper
				.getUserNameById(item.Poster));
//		String releaseTime = DateTimeUtil
//				.ConvertLongDateToString(item.PostTime);
//		String time = (releaseTime == null) ? "" : DateDeserializer
//				.getFormatTime(releaseTime);
		String time = item.PostTime;
		holder.textViewTime.setText(time);
		holder.textViewContent.setText(content);
		holder.avartarView.setTag(position);
//		holder.ivAttach.setOnClickListener(myAdapterCBListener);
		// new AvartarViewHelper(mContext, item.Publisher, holder.avartarView,
		// position);
		new AvartarViewHelper(mContext, item.Poster, holder.avartarView,
				position, 70, 70, false);
		if (!TextUtils.isEmpty(item.Attachment)) {
			holder.ivAttach.setVisibility(View.VISIBLE);
		} else {
			holder.ivAttach.setVisibility(View.GONE);
		}
//		view.setBackgroundResource(R.color.mail_unread_bg); // 未读
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
		public TextView textViewTitle;
		public AvartarView avartarView;
		public TextView textViewPublisherName;
		public TextView textViewTime;
		public TextView textViewContent;
		public ImageView ivAttach;
//		public ImageView ivcompanylist;
	}
}
