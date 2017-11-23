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
import com.cedarhd.models.流程;
import com.cedarhd.utils.LogUtils;

import java.util.List;

/**
 * @author Administrator 流程ListView的适配器
 */
public class FlowListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<流程> mList;
	private Context mContext;
	int mlistviewlayoutId;
	private DictionaryHelper dictHelper;
	private int REQUEST_CODE_ASKFOR_ME = 18;

	public FlowListViewAdapter(Context pContext, int listviewlayoutId,
			List<流程> pList, OnClickListener listener) {
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
	public 流程 getItem(int arg0) {
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
			holder.avartarView = (AvartarView) view
					.findViewById(R.id.control_avatar_askforleavelist);
			holder.ivDotRead = (ImageView) view
					.findViewById(R.id.iv_dot_read_weekloglist);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// 判断是否读过
		final 流程 item = getItem(position);
		String formName = TextUtils.isEmpty(item.getFormName()) ? item
				.getName() : item.getFormName();
		holder.textViewTitle.setText(formName + "");
		String craetetime = item.getCraeteDate() != null ? DateDeserializer
				.getFormatTime(item.getCraeteDate()) : "";
		holder.textViewTime.setText("创建时间：" + craetetime);// DateTimeUtil.ConvertLongDateToString(item.ReleaseTime));
		holder.textViewContent.setText("下个步骤：" + item.getNextStep());
		holder.textViewUserId.setText("ID：" + item.getCreate());// 创建人

		String completetime = item.getUpStepCompleteDate() != null ? DateDeserializer
				.getFormatTime(item.getUpStepCompleteDate()) : "";
		holder.textViewTimeType.setText("上步时间：" + completetime);
		holder.textViewVacationType.setText("状态：" + item.getCurrentState());
		String nextStepAudit = item.getNextStepAudit();
		holder.textViewDeadline.setText("下步审核人："
				+ dictHelper.getUserNameById(nextStepAudit));
		holder.avartarView.setTag(position);
		int create = 0;
		try {
			create = Integer.parseInt(item.getCreate());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		AvartarViewHelper avartarViewHelper = new AvartarViewHelper(mContext,
				create, holder.avartarView, position, 65, 65, true);

		String read = item.已读时间;
		if (!TextUtils.isEmpty(read)) {
			holder.ivDotRead.setVisibility(View.INVISIBLE); // 已读
			avartarViewHelper.setRead(true);
		} else {
			// holder.ivDotRead.setVisibility(View.VISIBLE); // 未读
			holder.ivDotRead.setVisibility(View.INVISIBLE); // 未读
			avartarViewHelper.setRead(false);
		}
		return view;
	}

	/***
	 * 
	 * @return
	 */
	public List<流程> getDataList() {
		return mList;
	}

	public void setmList(List<流程> mList) {
		this.mList = mList;
	}

	final class ViewHolder {
		public ImageView imageView1;
		public TextView textViewTitle;
		public TextView textViewTime;
		public TextView textViewContent;
		public TextView textViewUserId;
		public TextView textViewTimeType;
		public TextView textViewVacationType;
		public TextView textViewDeadline;
		public AvartarView avartarView;
		public ImageView ivDotRead;
	}
}
