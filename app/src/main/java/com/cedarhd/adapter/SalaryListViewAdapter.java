package com.cedarhd.adapter;

import android.annotation.SuppressLint;
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
import com.cedarhd.helpers.PhotoHelper;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.考勤信息;

import java.util.List;

/**
 * 考勤列表
 * 
 * @author BOHR
 * 
 */
public class SalaryListViewAdapter extends BaseAdapter {
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	View.OnClickListener myAdapterCBListener;
	private List<考勤信息> mList;
	private Context mContext;
	int mlistviewlayoutId;
	ORMDataHelper helper;
	private PictureUtils handlerUtils;
	private PhotoHelper photoHelper;

	public SalaryListViewAdapter(Context pContext, int listviewlayoutId,
			List<考勤信息> pList, OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;
		handlerUtils = new PictureUtils();
		photoHelper = new PhotoHelper(mContext);
		helper = ORMDataHelper.getInstance(mContext);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public 考勤信息 getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		View view = convertview;
		if (view == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			ViewHolder holder = new ViewHolder();
			holder.ivReadDot = (ImageView) view
					.findViewById(R.id.iv_dot_read_taglist);
			holder.tag_avatar = (AvartarView) view
					.findViewById(R.id.control_avatar_taglist);
			holder.tvSingnInPosition = (TextView) view
					.findViewById(R.id.tv_signIn_address);
			holder.tvSingnInTime = (TextView) view
					.findViewById(R.id.tv_signIn_time);
			holder.tvSingnOutPosition = (TextView) view
					.findViewById(R.id.tv_signOut_address);
			holder.tvSingnOutTime = (TextView) view
					.findViewById(R.id.tv_signOut_time);
			view.setTag(holder);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		考勤信息 item = getItem(position);

		String read = item.Read;
		if (!TextUtils.isEmpty(read)) {
			holder.ivReadDot.setVisibility(View.INVISIBLE);
		} else {
			// TODO 隐藏红色未读小圆点
			// holder.ivReadDot.setVisibility(View.VISIBLE);
			holder.ivReadDot.setVisibility(View.INVISIBLE);
		}

		holder.position = position;
		String signInPos = item.getPositionSignIn();
		holder.tvSingnInPosition.setText(signInPos);
		String signIntime = item.getSignInTime() != null ? DateDeserializer
				.getFormatTime(item.getSignInTime()) : "";
		holder.tvSingnInTime.setText(signIntime);

		String signOutPos = item.getPositionSignOut();
		holder.tvSingnOutPosition.setText(signOutPos);
		String signOuttime = item.getSignOutTime() != null ? DateDeserializer
				.getFormatTime(item.getSignOutTime()) : "";

		if (!TextUtils.isEmpty(signIntime) && item.IsLater) {
			holder.tvSingnInTime.setTextColor(0xFFFF0000);
			// 加粗显示
			holder.tvSingnInTime.getPaint().setFakeBoldText(true);
			holder.tvSingnInTime.setText(signIntime + " (迟到)");
		} else {
			holder.tvSingnInTime.setTextColor(0xFF808080);
			holder.tvSingnInTime.getPaint().setFakeBoldText(false);
			holder.tvSingnInTime.setText(signIntime);
		}

		if (!TextUtils.isEmpty(signOuttime) && item.IsEarly) {
			holder.tvSingnOutTime.setTextColor(0xFFFF0000);
			// 加粗显示
			holder.tvSingnOutTime.getPaint().setFakeBoldText(true);
			holder.tvSingnOutTime.setText(signOuttime + " (早退)");
		} else {
			holder.tvSingnOutTime.setTextColor(0xFF808080);
			holder.tvSingnOutTime.getPaint().setFakeBoldText(false);
			holder.tvSingnOutTime.setText(signOuttime);
		}

		holder.tag_avatar.setTag(position);
		int user = item.getEmployee();
		holder.tag_avatar.setTag(position);
		new AvartarViewHelper(mContext, item.getEmployee(), holder.tag_avatar,
				position, 70, 70, true);
		// LoadImgTask task = null;
		// showAvatar(position, holder, photoSerialNo, photoPath);
		return view;
	}

	public List<考勤信息> getDataList() {
		return mList;
	}

	final class ViewHolder {
		public int position;
		public ImageView ivReadDot;
		public AvartarView tag_avatar;
		public TextView tvSingnInPosition;
		public TextView tvSingnInTime;
		public TextView tvSingnOutPosition;
		public TextView tvSingnOutTime;
	}

}
