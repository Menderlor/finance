package com.cedarhd.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.QueryFilter;
import com.cedarhd.utils.LogUtils;

/** 弹出列表过滤条件 */
public class ListFilterPopupWindow {
	private final String TAG = "ListFilterPopupWindow";
	private Context mContext;

	private DateAndTimePicker mTimePicker;
	private PopupWindow mPopupWindow;
	private QueryFilter mQueryFilter;
	private View v;

	private TextView tvUser;
	private TextView tvClient;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private Button btnClear;
	private Button btnSure;

	public ListFilterPopupWindow(View v, Context mContext) {
		super();
		this.mContext = mContext;
		this.v = v;
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mTimePicker = new DateAndTimePicker(mContext);

		int pos[] = new int[2];
		// 获取在当前窗口内的绝对坐标
		v.getLocationOnScreen(pos);
		int height = v.getHeight() + pos[1];
		int popHeight = ViewHelper.getScreenHeight(mContext) - height;

		LogUtils.i("height",
				height + "--" + ViewHelper.getStatusBarHeight(mContext));

		View rootView = inflater.inflate(R.layout.pop_filter_list, null);
		mPopupWindow = new PopupWindow(rootView, LayoutParams.MATCH_PARENT,
				popHeight, true);
		mPopupWindow.setAnimationStyle(R.style.AnimationFadeTop);
		mPopupWindow.update();
		// 点击空白处 对话框消失
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(mContext
				.getResources(), (Bitmap) null));

		tvUser = (TextView) rootView
				.findViewById(R.id.tv_select_user_pop_filter);
		tvClient = (TextView) rootView
				.findViewById(R.id.tv_select_client_pop_filter);
		tvStartTime = (TextView) rootView
				.findViewById(R.id.tv_select_startTime_pop_filter);
		tvEndTime = (TextView) rootView
				.findViewById(R.id.tv_select_endTime_pop_filter);

		btnClear = (Button) rootView.findViewById(R.id.btn_cancel_pop_filter);
		btnSure = (Button) rootView.findViewById(R.id.btn_done_pop_filter);
	}

	private void setOnEvent() {
		tvUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onStartSelect(mQueryFilter);
				}
				UserBiz.selectSinalUser(mContext);
			}
		});

		tvClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onStartSelect(mQueryFilter);
				}
				ClientBiz.selectClient(mContext);
			}
		});

		tvStartTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTimePicker.showDateWheel(tvStartTime);
			}
		});

		tvEndTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTimePicker.showDateWheel(tvEndTime);
			}
		});

		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mQueryFilter.userId = 0;
				mQueryFilter.userName = "";

				mQueryFilter.clientId = 0;
				mQueryFilter.clientName = "";

				mQueryFilter.startTime = "";
				mQueryFilter.endTime = "";

				initFilterInfo();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onSelect(mQueryFilter);
					mPopupWindow.dismiss();
				}
			}
		});
	}

	private void initFilterInfo() {
		tvUser.setHint(mQueryFilter.userHint);
		tvClient.setHint(mQueryFilter.clientHint);
		tvUser.setText(mQueryFilter.userName);
		tvClient.setText(mQueryFilter.clientName);
		tvStartTime.setText(mQueryFilter.startTime);
		tvEndTime.setText(mQueryFilter.endTime);
	}

	public void updateUserFilter(QueryFilter filter) {
		if (mQueryFilter != null) {
			mQueryFilter.userId = filter.userId;
			mQueryFilter.userName = filter.userName;
			tvUser.setText(mQueryFilter.userName);
		} else {
			mQueryFilter = filter;
			initFilterInfo();
		}
	}

	public void updateClientFilter(QueryFilter filter) {
		if (mQueryFilter != null) {
			mQueryFilter.clientId = filter.clientId;
			mQueryFilter.clientName = filter.clientName;
			tvClient.setText(mQueryFilter.clientName);
		} else {
			mQueryFilter = filter;
			initFilterInfo();
		}
	}

	public void show(QueryFilter filter) {
		initViews();
		setOnEvent();

		this.mQueryFilter = filter;
		int pos[] = new int[2];
		// 获取在当前窗口内的绝对坐标
		v.getLocationOnScreen(pos);
		int height = v.getHeight() + pos[1];
		mPopupWindow.showAtLocation(v, Gravity.TOP, 0, height);

		initFilterInfo();
	}

	private OnSelectListener mListener;

	public void setOnClickListener(OnSelectListener listener) {
		this.mListener = listener;
	}

	public interface OnSelectListener {
		void onSelect(QueryFilter filter);

		/** 打开选择页面时，保存当前filter */
		void onStartSelect(QueryFilter filter);
	}
}
