package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.问题反馈;

import java.util.List;

/**
 * 任务列表内容适配器
 */
public class FeedbackListViewAdapter extends BaseAdapter {
	private List<问题反馈> mList;
	private Context mContext;
	int mlistviewlayoutId;
	DictionaryHelper dictionaryHelper;
	String[] arrs; // 状态数组
	//
	// // 黄色启动，暂停浅灰，绿色完成，搁置灰色，蓝色提交，重启红色
	// int[] stateColors = new int[] { R.color.yellow, R.color.lightgray,
	// R.color.green, R.color.gray, R.color.blue, R.color.red };

	// // 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
	int[] stateColors = new int[] { 0xFFFFFF00, 0xFFD3D3D3, 0xFF008000,
			0xFF808080, 0xFF0000FF, 0xFFFF0000 };

	public FeedbackListViewAdapter(Context pContext, int listviewlayoutId,
			List<问题反馈> pList) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;

		dictionaryHelper = new DictionaryHelper(mContext);
		arrs = mContext.getResources().getStringArray(R.array.statelist);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 问题反馈 getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		if (view == null || (holder = (ViewHolder) view.getTag()) == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			holder = new ViewHolder();
			holder.tvState = (TextView) view
					.findViewById(R.id.tv_state_tasklist);
			holder.textViewPublisherName = (TextView) view
					.findViewById(R.id.textViewPublishName_tasklist);
			holder.textViewPartcipant = (TextView) view
					.findViewById(R.id.textViewParticipant_tasklist);
			holder.textViewTitle = (TextView) view
					.findViewById(R.id.textViewTitle_tasklist);
			holder.textViewState = (TextView) view
					.findViewById(R.id.textViewState_tasklist);
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.control_avatar_tasklist);
			holder.textViewTime = (TextView) view
					.findViewById(R.id.textViewTime_tasklist);
			holder.textViewContent = (TextView) view
					.findViewById(R.id.textViewContent_tasklist);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		问题反馈 item = mList.get(position);
		holder.textViewTitle.setText(item.Title);
		// 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
		if (item.Status >= 1 && item.Status <= 6) {
			holder.textViewState.setText(arrs[item.Status - 1]);
			holder.tvState.setBackgroundColor(stateColors[item.Status - 1]);
		} else {
			holder.tvState.setBackgroundColor(0x0000000); // 状态异常透明
			holder.textViewState.setText("状态异常");
		}
		holder.textViewPartcipant.setText("参与人："
				+ dictionaryHelper.getUserNamesById(item.Participant));
		holder.textViewPublisherName.setText(dictionaryHelper
				.getUserNameById(item.Publisher));
		String time = item.Time == null ? "" : DateDeserializer
				.getFormatTime(item.AssignTime); // 开始时间
		holder.textViewTime.setText(time);
		holder.textViewContent.setText(item.Content);
		holder.avartarView.setTag(position);
		new AvartarViewHelper(mContext, item.Publisher, holder.avartarView,
				position, 70, 70, false);
		view.setBackgroundResource(R.color.mail_read_bg);
		String nowDate = ViewHelper.getDateString();
		if (item.Status == 1) { // 状态为启动
			// 如果任务超期(指定任务完成日期小于当前日期 )
			if (!TextUtils.isEmpty(item.CompletionTime)
					&& DateDeserializer.compareDate(item.CompletionTime,
							nowDate)) {
				view.setBackgroundResource(R.drawable.listviewitemshape);
			}
		}

		String read = item.Readed;
		String userId = Global.mUser.Id;
		String leftUserId = "," + userId;
		String rightUserId = userId + ",";
		if (read == null
				|| (!read.contains(leftUserId) && !read.contains(rightUserId))) {
			view.setBackgroundResource(R.color.mail_unread_bg); // 未读
		} else {
			view.setBackgroundResource(R.color.mail_read_bg); // 已读
		}
		return view;
	}

	final class ViewHolder {
		public TextView tvState; // 状态色块
		public TextView textViewState;
		public TextView textViewPartcipant;
		public TextView textViewTitle;
		public TextView textViewPublisherName;
		public AvartarView avartarView;
		public TextView textViewTime;
		public TextView textViewContent;
	}
}
