package com.cedarhd.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.User_SelectActivityNew_zmy;
import com.cedarhd.WeekLogInfoActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.DoubleDatePickerDialog;
import com.cedarhd.control.listview.ListViewLoader;
import com.cedarhd.control.listview.PullToRefreshAndLoadMoreListView;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.User;
import com.cedarhd.models.changhui.Qm工作总结报告;
import com.cedarhd.models.changhui.工作总结报告;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 工作总结列表Fragment
 * 
 * @author kjx
 * 
 *         2016-07-11
 */
public class WorkSummaryFragment extends Fragment {
	private final static String EXTRAS_TYPE = "WorkSummaryFragment.type";

	private final static int REQUESTCODE_UPDATE_WORK = 101;

	private int mSelectPos = -1;

	private int mTypeId;
	private Context mContext;
	private String mSearchKey;

	private PullToRefreshAndLoadMoreListView lv;
	private LinearLayout llFilter;

	private Qm工作总结报告 mQmDemand;
	private QueryDemand mQueryDemand;
	private List<工作总结报告> mList;
	private CommanCrmAdapter<工作总结报告> mAdapter;
	private ListViewLoader<工作总结报告> mListViewLoader;

	private DictionaryHelper dictionaryHelper;

    public WorkSummaryFragment() {
        super();
	}

	public static WorkSummaryFragment newInstance(int typeId) {
		final WorkSummaryFragment fragment = new WorkSummaryFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRAS_TYPE, typeId);
		fragment.setArguments(args);
		return fragment;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTypeId = getArguments().getInt(EXTRAS_TYPE);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_worksummary, null);
		initViews(view);
		initData();
		setOnEvent();
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_UPDATE_WORK:
				// if (mSelectPos != -1) {
				// mList.remove(mSelectPos);
				// mAdapter.notifyDataSetChanged();
				// mSelectPos = -1;
				// }
				reLoadData();
				break;
			case UserBiz.SELECT_MULTI_USER_REQUEST_CODE:
				mSelectUser = UserBiz.onActivityMultiUserSelected(requestCode,
						resultCode, data);
				tvSelectUser.setText(mSelectUser.getUserNames() + "");
				break;
			default:
				break;
			}
		}
	}

	private void initViews(View view) {
		lv = (PullToRefreshAndLoadMoreListView) view
				.findViewById(R.id.lv_comman_loadlist);

		llFilter = (LinearLayout) view.findViewById(R.id.ll_filter_worksummary);

		if (mTypeId == 1) {// 日志
			// llFilter.setVisibility(View.VISIBLE);
			llFilter.setVisibility(View.GONE);
		} else {
			llFilter.setVisibility(View.GONE);
		}
	}

	private void initData() {
		mContext = getActivity();
		dictionaryHelper = new DictionaryHelper(mContext);
		mList = new ArrayList<工作总结报告>();
		mAdapter = getAdapter();
		mQmDemand = new Qm工作总结报告();
		mQmDemand.PageSize = 20;
		mQmDemand.Offset = 0;
		mQmDemand.NoPager = false;
		mQmDemand.Type = mTypeId;
		mQueryDemand = new QueryDemand("编号");
		mListViewLoader = new ListViewLoader<工作总结报告>(mContext,
				"Log/GetWorkReportList", lv, mAdapter, mQmDemand, mQueryDemand,
				工作总结报告.class);

		if (mTypeId == 1) {// 日志
			initQueryFilter();
		}
	}

	public void reLoadData() {
		mQmDemand.Offset = 0;
		mListViewLoader.clearData();
		mListViewLoader.startRefresh();
	}

	public void reLoadData(String filter) {
		if (!TextUtils.isEmpty(filter)) {
			filter += " AND " + getSearchFilterStr();
		} else {
			filter = getSearchFilterStr();
		}
		mQmDemand.moreFilter = filter;
		reLoadData();
	}

	private void setOnEvent() {
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int pos = position - lv.getHeaderViewsCount();
				if (pos < 0 || pos >= mList.size()) {
					return;
				}

				// VmFormBiz.startVmFromActivity(mContext, 10091,
				// mList.get(pos).编号, "周工作总结");
				Intent intent = new Intent(mContext, WeekLogInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(WeekLogInfoActivity.TAG, mList.get(pos));
				intent.putExtra(WeekLogInfoActivity.EXTRA_TYPE, mTypeId);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_UPDATE_WORK);
				mList.get(pos).已读时间 = ViewHelper.getDateString();
				mAdapter.notifyDataSetChanged();
				// startActivity(intent);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							new ZLServiceHelper().ReadDynamic(
									mList.get(pos).编号, 11);
						} catch (Exception e) {
							LogUtils.e("erro", "" + e);
						}
					}
				}).start();
			}
		});

		lv.mSearchView
				.setOnButtonClickListener(new BoeryunSearchView.OnButtonClickListener() {
					@Override
					public void OnSearch(String str) {
						mSearchKey = str;
						mQmDemand.moreFilter = getQueryFilterStr();
						reLoadData();
					}

					@Override
					public void OnCancle() {
						mSearchKey = "";
						// tvSelectDate.setText("");
						// tvSelectUser.setText("");
						mSelectUser = null;
						mStartTime = "";
						mEndTime = "";
						mQmDemand.moreFilter = "";
						reLoadData();
					}
				});

		llFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showFilterDialog();
			}
		});
	}

	private CommanCrmAdapter<工作总结报告> getAdapter() {
		return new CommanCrmAdapter<工作总结报告>(mList, mContext,
				R.layout.loglist_listviewlayout) {
			@Override
			public void convert(int position, 工作总结报告 item,
					BoeryunViewHolder viewHolder) {
				AvartarView worklog_avatar = (AvartarView) viewHolder
						.getView(R.id.worklog_avatar);
				ImageView ivDotRead = (ImageView) viewHolder
						.getView(R.id.iv_dot_read_loglist);
				TextView textViewTime = (TextView) viewHolder
						.getView(R.id.textViewTime);
				TextView textViewContent = (TextView) viewHolder
						.getView(R.id.textViewContent);
				TextView DiscussCount = (TextView) viewHolder
						.getView(R.id.DiscussCount);
				TextView tvDept = (TextView) viewHolder
						.getView(R.id.tv_dept_work_log);

				tvDept.setText(StrUtils.pareseNull(dictionaryHelper.get分公司(
						mContext, item.制单人).get名称()));
				String time = item.制单时间 != null ? DateDeserializer
						.getFormatTime(item.制单时间) : "";
				// String count = item. != 0 ? item.DiscussCount + "评" : "";
				// holder.DiscussCount.setText(count);
				textViewTime.setText(time);
				textViewContent.setText(StrUtils.pareseNull(item.已完成工作));
				worklog_avatar.setTag(position);
				AvartarViewHelper avartarViewHelper = new AvartarViewHelper(
						mContext, item.制单人, worklog_avatar, position, 60, 60,
						true);
				String read = item.已读时间;
				ivDotRead.setVisibility(View.INVISIBLE); // 暂时隐藏左侧红点
				if (!TextUtils.isEmpty(read)) {
					avartarViewHelper.setRead(true);
				} else {
					avartarViewHelper.setRead(false);
				}
			}
		};
	}

	private Dialog mFilterDialog;
	private TextView tvSelectUser;
	private TextView tvSelectDate;
	private String mStartTime;
	private String mEndTime;
	private User mSelectUser;

	private void initQueryFilter() {
		mFilterDialog = new Dialog(mContext, R.style.styleNoFrameDialog);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_filter_log, null);
		initDialog(view);
		mFilterDialog.setContentView(view);
		mFilterDialog.setCancelable(false);
	}

	private void showFilterDialog() {
		if (mFilterDialog != null) {
			mFilterDialog.show();
			WindowManager.LayoutParams lp = mFilterDialog.getWindow()
					.getAttributes();
			lp.width = (int) (ViewHelper.getScreenWidth(mContext)); // 设置宽度
			lp.gravity = Gravity.BOTTOM;
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
				// UserBiz.selectMultiUser(mContext, "");
				Intent intent = new Intent(mContext,
						User_SelectActivityNew_zmy.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", "");
				intent.putExtras(bundle);
				startActivityForResult(intent,
						UserBiz.SELECT_MULTI_USER_REQUEST_CODE);
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
				mQmDemand.moreFilter = getQueryFilterStr();
				reLoadData();
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

				mQmDemand.moreFilter = getQueryFilterStr();
				reLoadData();
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
		if (!TextUtils.isEmpty(mSearchKey)) {
			queryFilterStr += " and " + "(已完成工作 like '%" + mSearchKey + "%')";
		}

		return queryFilterStr;
	}

	private String getSearchFilterStr() {
		String queryFilterStr = "1=1";
		if (!TextUtils.isEmpty(mSearchKey)) {
			queryFilterStr += " and " + "(已完成工作 like '%" + mSearchKey + "%')";
		}
		return queryFilterStr;
	}

}
