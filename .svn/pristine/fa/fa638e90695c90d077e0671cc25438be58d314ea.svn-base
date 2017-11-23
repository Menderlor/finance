package com.cedarhd.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.changhui.ChClientListActivity;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.changhui.ChQueryFilter;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

/** 弹出列表过滤条件 */
public class ClientContactFilterPopupWindow {
	private final String TAG = "ListFilterPopupWindow";
	private Context mContext;

	private DateAndTimePicker mTimePicker;
	private DictionaryQueryDialogHelper mDictionaryQueryDialogHelper;
	private PopupWindow mPopupWindow;
	private ChQueryFilter mQueryFilter;
	private View v;

	private TextView tvUser;
	private TextView tvClient;
	private TextView tvJob;
	private TextView tvDept;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private Button btnClear;
	private Button btnSure;
	private LinearLayout llBottom;

	public ClientContactFilterPopupWindow(View v, Context mContext) {
		super();
		this.mContext = mContext;
		this.v = v;
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mTimePicker = new DateAndTimePicker(mContext);
		mDictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(mContext);

		int pos[] = new int[2];
		// 获取在当前窗口内的绝对坐标
		v.getLocationOnScreen(pos);
		int height = v.getHeight() + pos[1];
		int popHeight = ViewHelper.getScreenHeight(mContext) - height;

		LogUtils.i("height",
				height + "--" + ViewHelper.getStatusBarHeight(mContext));

		View rootView = inflater.inflate(R.layout.pop_clientcontact_filter,
				null);
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
				.findViewById(R.id.tv_select_user_pop_contact_filter);
		tvClient = (TextView) rootView
				.findViewById(R.id.tv_select_client_pop_contact_filter);

		tvDept = (TextView) rootView
				.findViewById(R.id.tv_select_dept_pop_contact_filter);
		tvJob = (TextView) rootView
				.findViewById(R.id.tv_select_job_pop_contact_filter);

		tvStartTime = (TextView) rootView
				.findViewById(R.id.tv_select_startTime_pop_contact_filter);
		tvEndTime = (TextView) rootView
				.findViewById(R.id.tv_select_endTime_pop_contact_filter);

		btnClear = (Button) rootView
				.findViewById(R.id.btn_cancel_pop_contact_filter);
		btnSure = (Button) rootView
				.findViewById(R.id.btn_done_pop_contact_filter);
		llBottom = (LinearLayout) rootView
				.findViewById(R.id.ll_bottom_pop_contact_filter);
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
//				ClientBiz.selectClient(mContext);
				Intent intent = new Intent(mContext, ChClientListActivity.class);
				intent.putExtra(ChClientListActivity.EXTRA_SELECT_CLIENT, true);
				((Activity) mContext).startActivityForResult(intent, ClientBiz.SELECT_CLIENT_CODE);
			}
		});

		tvDept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDictionaryQueryDialogHelper.show("部门");
				mDictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								mQueryFilter.所在部门 = dict.Id;
								mQueryFilter.所在部门名称 = dict.Name;
								tvDept.setText(dict.getName());
							}
						});
			}
		});

		tvJob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDictionaryQueryDialogHelper.show("岗位");
				mDictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								mQueryFilter.所在岗位 = dict.Id;
								mQueryFilter.所在岗位名称 = dict.Name;
								tvDept.setText(dict.getName());
							}
						});
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

				mQueryFilter.所在岗位 = 0;
				mQueryFilter.所在岗位名称 = "";

				mQueryFilter.所在部门 = 0;
				mQueryFilter.所在部门名称 = "";

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

		llBottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPopupWindow.dismiss();
			}
		});
	}

	private void initFilterInfo() {
		tvUser.setHint(mQueryFilter.userHint);
		tvClient.setHint(mQueryFilter.clientHint);
		tvUser.setText(mQueryFilter.userName);
		tvClient.setText(mQueryFilter.clientName);
		tvDept.setText(StrUtils.pareseNull(mQueryFilter.所在部门名称));
		tvJob.setText(StrUtils.pareseNull(mQueryFilter.所在岗位名称));
		tvStartTime.setText(mQueryFilter.startTime);
		tvEndTime.setText(mQueryFilter.endTime);
	}

	public void updateUserFilter(ChQueryFilter filter) {
		if (mQueryFilter != null) {
			mQueryFilter.userId = filter.userId;
			mQueryFilter.userName = filter.userName;
			tvUser.setText(mQueryFilter.userName);
		} else {
			mQueryFilter = filter;
			initFilterInfo();
		}
	}

	public void updateClientFilter(ChQueryFilter filter) {
		if (mQueryFilter != null) {
			mQueryFilter.clientId = filter.clientId;
			mQueryFilter.clientName = filter.clientName;
			tvClient.setText(mQueryFilter.clientName);
		} else {
			mQueryFilter = filter;
			initFilterInfo();
		}
	}

	public void show(ChQueryFilter filter) {
		initViews();
		setOnEvent();

		this.mQueryFilter = filter;
		int pos[] = new int[2];
		// 获取在当前窗口内的绝对坐标
		v.getLocationOnScreen(pos);
		int height = v.getHeight() + pos[1];
		mPopupWindow.showAtLocation(v, Gravity.TOP, 0, height);
		mPopupWindow.setOutsideTouchable(false);

		initFilterInfo();
	}

	private OnSelectListener mListener;

	public void setOnClickListener(OnSelectListener listener) {
		this.mListener = listener;
	}

	public interface OnSelectListener {
		void onSelect(ChQueryFilter filter);

		/** 打开选择页面时，保存当前filter */
		void onStartSelect(ChQueryFilter filter);
	}
}
