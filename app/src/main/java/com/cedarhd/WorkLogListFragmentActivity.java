package com.cedarhd;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.WorkLogViewPagerAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.DoubleDatePickerDialog;
import com.cedarhd.fragment.WorkSummaryFragment;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.User;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.view.Indicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 新版日志，周志，月报页面 使用ViewPager+Fragment方式
 * 
 * 旧版 WorkLogListActivity
 * 
 * @author kjx
 * @since 2014/09/16
 */
public class WorkLogListFragmentActivity extends BaseActivity {
	private final String[] titleStrings = new String[] { "日报", "周报", "月报" };
	private final int REQUEST_CODE_ADD_WORK_SUMMARY = 12;

	private List<Fragment> listFragment = new ArrayList<Fragment>();
	private Resources resource;
	private FragmentManager fm;
	private int currentPage = 0;
	private ViewPager vPager;
	private WorkLogViewPagerAdapter adapter;
	private Context mContext;
	private TextView tvTitle;// 标题
	private Indicator indicator;

	// private TextView tvWorkLogList;// 日志
	// private TextView tvWeekLogList;// 周报
	// private View viewWorkLog;
	// private View viewWeekLog;

	// private List<TextView> tvList = new ArrayList<TextView>();
	// private List<View> viewList = new ArrayList<View>();
	// private WorkLogFragment logFragmnet;
	// private WeekLogFragment weekFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_worklog_list);
		initViews();

		initQueryFilter();
	}

	private void initViews() {
		resource = getResources();
		fm = getSupportFragmentManager();

		mContext = WorkLogListFragmentActivity.this;
		listFragment.add(WorkSummaryFragment.newInstance(1));
		listFragment.add(WorkSummaryFragment.newInstance(2));
		listFragment.add(WorkSummaryFragment.newInstance(3));
		adapter = new WorkLogViewPagerAdapter(fm, listFragment);

		tvTitle = (TextView) findViewById(R.id.tv_title_worklog_list);
		// tvWorkLogList = (TextView) findViewById(R.id.tv_worklog_list);
		// tvWeekLogList = (TextView) findViewById(R.id.tv_week_log_list);
		// viewWorkLog = findViewById(R.id.view_worklog_list);
		// viewWeekLog = findViewById(R.id.view_week_log_list);
		// tvList.add(tvWorkLogList);
		// tvList.add(tvWeekLogList);
		// viewList.add(viewWorkLog);
		// viewList.add(viewWeekLog);

		vPager = (ViewPager) findViewById(R.id.vp_worklog_list);
		vPager.setAdapter(adapter);
		indicator = (Indicator) findViewById(R.id.indicator_ch_log_info);
		indicator.setRelateViewPager(vPager);
		indicator.setTabItemTitles(Arrays.asList(titleStrings));

		// tvWorkLogList.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// setTab();
		// currentPage = 0;
		// setCurrentPager(0);
		// tvTitle.setText("日志列表");
		// }
		// });
		// tvWeekLogList.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// setTab();
		// currentPage = 1;
		// setCurrentPager(1);
		// tvTitle.setText("周工作总结");
		// }
		// });

		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView ivFilter = (ImageView) findViewById(R.id.iv_filter_worklog_list);
		ivFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showFilterDialog();
			}
		});
		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createWorkSummary();
			}
		});
		// vPager.setOnPageChangeListener(new OnPageChangeListener() {
		// @Override
		// public void onPageSelected(int pos) {
		// setTab();
		//
		// currentPage = pos;
		// // tvList.get(pos).setTextColor(
		// // resource.getColor(R.color.theme_text));
		// // viewList.get(pos).setVisibility(View.VISIBLE);
		// // tvTitle.setText(titleStrings[pos]);
		// }
		//
		// @Override
		// public void onPageScrolled(int pos, float arg1, int arg2) {
		//
		// }
		//
		// @Override
		// public void onPageScrollStateChanged(int pos) {
		//
		// }
		// });

	}

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		// tvWorkLogList.setTextColor(getResources().getColor(R.color.gray));
		// tvWeekLogList.setTextColor(getResources().getColor(R.color.gray));
		// viewWorkLog.setVisibility(View.INVISIBLE);
		// viewWeekLog.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置当前显示页面
	 */
	public void setCurrentPager(int pos) {
		vPager.setCurrentItem(pos);
		// tvList.get(pos).setTextColor(resource.getColor(R.color.theme_text));
		// viewList.get(pos).setVisibility(View.VISIBLE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("currentPage", "currentPage:" + currentPage + "---"
				+ resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_ADD_WORK_SUMMARY) {
				reloadFragment("");
			} else if (requestCode == UserBiz.SELECT_MULTI_USER_REQUEST_CODE) {
				mSelectUser = UserBiz.onActivityMultiUserSelected(requestCode,
						resultCode, data);
				tvSelectUser.setText(mSelectUser.getUserNames() + "");
			}
		}
	}

	private void reloadFragment(String filter) {
		WorkSummaryFragment currFragment = (WorkSummaryFragment) listFragment
				.get(vPager.getCurrentItem());
		if (currFragment != null) {
			currFragment.reLoadData(filter);
		}
	}

	/**
	 * 新建工作日志
	 * 
	 * @param content
	 */
	private void createWorkSummary() {
		int type = 1;
		switch (vPager.getCurrentItem()) {
		case 0:
			type = 1;
			break;
		case 1:
			type = 2;
			break;
		case 2:
			type = 3;
			break;
		default:
			break;
		}

		Intent intent = new Intent(this, WeekLogInfoActivity.class);
		intent.putExtra(WeekLogInfoActivity.EXTRA_TYPE, type);
		startActivityForResult(intent, REQUEST_CODE_ADD_WORK_SUMMARY);
	}

	private Dialog mFilterDialog;
	private TextView tvSelectUser;
	private TextView tvSelectDate;
	private String mStartTime;
	private String mEndTime;
	private User mSelectUser;

	private void initQueryFilter() {
		mFilterDialog = new Dialog(mContext, R.style.styleNoFrameDialog);
		mFilterDialog.setCanceledOnTouchOutside(true);
		mFilterDialog.setCancelable(true);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_filter_log, null);
		initDialog(view);
		mFilterDialog.setContentView(view);
		// mFilterDialog.setCancelable(false);
	}

	private void showFilterDialog() {
		if (mFilterDialog != null) {
			mFilterDialog.show();
			WindowManager.LayoutParams lp = mFilterDialog.getWindow()
					.getAttributes();
			lp.width = (int) (ViewHelper.getScreenWidth(mContext)); // 设置宽度
			lp.gravity = Gravity.TOP;
			mFilterDialog.getWindow().setAttributes(lp);
		}
	}

	private void initDialog(View view) {
		tvSelectUser = (TextView) view
				.findViewById(R.id.tv_select_user_log_filter);
		tvSelectDate = (TextView) view.findViewById(R.id.tv_time_log_filter);

		TextView tvCancel = (TextView) view
				.findViewById(R.id.btn_cancel_log_filter);
		TextView tvSave = (TextView) view
				.findViewById(R.id.btn_done_log_filter);

		tvSelectUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UserBiz.selectMultiUser(mContext, "");
				// Intent intent = new Intent(mContext,
				// User_SelectActivityNew_zmy.class);
				// Bundle bundle = new Bundle();
				// bundle.putString("UserSelectId", "");
				// intent.putExtras(bundle);
				// startActivityForResult(intent,
				// UserBiz.SELECT_MULTI_USER_REQUEST_CODE);
			}
		});

		tvSelectDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Calendar c = Calendar.getInstance();
				// 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
				new DoubleDatePickerDialog(mContext, 0,
						new DoubleDatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker startDatePicker,
									int startYear, int startMonthOfYear,
									int startDayOfMonth,
									DatePicker endDatePicker, int endYear,
									int endMonthOfYear, int endDayOfMonth) {
								String textString = String.format(
										"%d-%d-%d  至     %d-%d-%d", startYear,
										startMonthOfYear + 1, startDayOfMonth,
										endYear, endMonthOfYear + 1,
										endDayOfMonth);

								mStartTime = startYear + "-"
										+ (startMonthOfYear + 1) + "-"
										+ startDayOfMonth;
								mEndTime = endYear + "-" + (endMonthOfYear + 1)
										+ "-" + endDayOfMonth;
								tvSelectDate.setText(textString);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DATE), true).show();
			}
		});

		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tvSelectDate.setText("");
				tvSelectUser.setText("");
				mSelectUser = null;
				mStartTime = "";
				mEndTime = "";
				reloadFragment("");
				mFilterDialog.dismiss();
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mStartTime)
						&& TextUtils.isEmpty(mEndTime)
						&& TextUtils.isEmpty(mSelectUser.getUserIds())) {
					Toast.makeText(mContext, "还没选择过滤条件", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				reloadFragment(getQueryFilterStr());
				mFilterDialog.dismiss();
			}
		});
	}

	private String getQueryFilterStr() {
		String queryFilterStr = "1=1";
		if (!TextUtils.isEmpty(mStartTime) && !TextUtils.isEmpty(mEndTime)) {
			queryFilterStr += " and "
					+ String.format("制单时间 <='%s' and 制单时间>='%s'", mEndTime,
							mStartTime);
		}

		if (mSelectUser != null && !TextUtils.isEmpty(mSelectUser.getUserIds())) {
			queryFilterStr += " and "
					+ String.format("制单人 in ( %s )", mSelectUser.getUserIds());
		}
		// if (!TextUtils.isEmpty(mSearchKey)) {
		// queryFilterStr += " and " + "(已完成工作 like '%" + mSearchKey + "%')";
		// }

		return queryFilterStr;
	}

}
