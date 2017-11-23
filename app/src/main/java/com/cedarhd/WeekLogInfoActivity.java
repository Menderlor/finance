package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.CommentBiz;
import com.cedarhd.biz.DeptBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.BoeryunCommentItemView;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.models.changhui.FormComment;
import com.cedarhd.models.changhui.工作总结报告;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.部门;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/***
 * 工作总结报告报告详情/新建页面
 * 
 * @author Administrator
 * 
 */
public class WeekLogInfoActivity extends BaseActivity {

	public static final String TAG = "WeekLogInfoActivity";
	public static final String EXTRA_TYPE = "com.cedarhd.EXTRA_TYPE";
	private Context mContext;

	private 工作总结报告 mWorkReport;
	private int mTypeId;
	private List<日志评论> mCommentList = new ArrayList<日志评论>();
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private DateAndTimePicker dateAndTimePicker;
	private BoeryunCommentItemView mCommentItemView;

	/** 校验结果为空的行 名称 */
	private String nullRowName;

	private ImageView ivCancel;
	private ImageView ivSave;
	private TextView tvName;
	private EditText etArea;
	private EditText etStart;
	private EditText etEnd;
	private EditText etComplete;
	private EditText etUncomplete;
	private EditText etNextWeekPlan;
	private EditText etSummary;
	private TextView tvTitle;

	/** 评论内容区域 */
	private LinearLayout ll_comment_root;// 评论区

	private LinearLayout llPubCommentRoot;
	/** 发表评论 */
	private EditText etComment;// 评论内容
	private LinearLayout llPubComment;// 发表评论按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_week_log_info);
		initUI();
		initData();
		setEventListener();
	}

	private void initData() {
		mContext = this;
		dateAndTimePicker = new DateAndTimePicker(mContext);
		mCommentItemView = new BoeryunCommentItemView(mContext, mCommentList,
				ll_comment_root);
		Bundle bundle = getIntent().getExtras();
		mTypeId = getIntent().getIntExtra(EXTRA_TYPE, 1);
		if (bundle != null) {
			mWorkReport = (工作总结报告) bundle.getSerializable(TAG);
			if (mWorkReport != null) {
				try {
					int userId = Integer.parseInt(Global.mUser.Id);
					// 创建人不是自己不可编辑
					if (mWorkReport.制单人 != userId) {
						ivSave.setVisibility(View.GONE);
						setEdit(false);
					}
				} catch (Exception e) {
				}
			} else {
				initWorkReport();
				// setEdit(false);
				if (mTypeId == 1) {
					fetchThisWeekLog();
				}
			}
		} else {
			initWorkReport();
		}

		showWeekLog();
		showTitle();
	}

	private void showTitle() {
		String title = "工作总结";
		switch (mTypeId) {
		case 1:
			title = "日" + title;
			break;
		case 2:
			title = "周" + title;
			break;
		case 3:
			title = "月" + title;
			break;
		default:
			break;
		}
		tvTitle.setText(title);
	}

	private void initWorkReport() {
		mWorkReport = new 工作总结报告();
		try {
			User user = UserBiz.getGlobalUser();
			mWorkReport.员工 = Integer.parseInt(user.getId());
			mWorkReport.制单人 = mWorkReport.员工;
			部门 dept = DeptBiz.get分公司(mContext, mWorkReport.员工);
			mWorkReport.类型 = mTypeId;
			mWorkReport.负责区域 = dept.get编号();

			switch (mTypeId) {
			case 1:
				mWorkReport.开始时间 = ViewHelper.getDateToday();
				mWorkReport.结束时间 = ViewHelper.getDateToday();
				break;
			case 2: // 周
				mWorkReport.开始时间 = ViewHelper.getFirstDateStrOfThisWeek();
				mWorkReport.结束时间 = ViewHelper.getLastDateStrOfThisWeek();
				break;
			case 3:
				mWorkReport.开始时间 = ViewHelper.getFirstDateStrOfThisMonth();
				mWorkReport.结束时间 = ViewHelper.getLastDateStrOfThisMonth();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e + "");
		}
	}

	private void initUI() {
		ivCancel = (ImageView) findViewById(R.id.iv_back_weeklog);
		ivSave = (ImageView) findViewById(R.id.iv_save_weeklog);
		tvName = (TextView) findViewById(R.id.tv_name_weeklog_info);
		etArea = (EditText) findViewById(R.id.tv_area_weeklog_info);
		etStart = (EditText) findViewById(R.id.tv_start_weeklog_info);
		etEnd = (EditText) findViewById(R.id.tv_end_weeklog_info);
		etComplete = (EditText) findViewById(R.id.tv_complete_weeklog_info);
		etUncomplete = (EditText) findViewById(R.id.tv_unComplete_weeklog_info);
		etNextWeekPlan = (EditText) findViewById(R.id.tv_next_week_plan_weeklog_info);
		etSummary = (EditText) findViewById(R.id.tv_summary_weeklog_info);
		tvTitle = (TextView) findViewById(R.id.tv_title_weeklog);
		// tvName.setText("" + UserBiz.getGlobalUser().UserName);

		ll_comment_root = (LinearLayout) findViewById(R.id.ll_root_comment_weeklog);

		llPubCommentRoot = (LinearLayout) findViewById(R.id.rl_root_publish_comment_weeklog);
		llPubComment = (LinearLayout) findViewById(R.id.ll_weeklog_publish_comment);
		etComment = (EditText) findViewById(R.id.et_weeklog_discuss);

	}

	private void showWeekLog() {
		// etArea.setText("" + mWeekLog.负责区域);
		// etStart.setText("" + mWeekLog.开始时间);
		// etEnd.setText("" + mWeekLog.结束时间);
		// etComplete.setText("" + mWeekLog.本周已完成工作);
		// etUncomplete.setText(TextUtils.isEmpty(mWeekLog.本周未完成工作) ? " "
		// : mWeekLog.本周未完成工作);
		// etNextWeekPlan.setText("" + mWeekLog.下周计划完成工作);
		// etSummary.setText("" + mWeekLog.自我总结);
		tvName.setText(StrUtils.pareseNull(new DictionaryHelper(mContext)
				.getUserNameById(mWorkReport.员工)));
		etArea.setText(StrUtils.pareseNull(DeptBiz.getDeptById(mContext,
				mWorkReport.负责区域).get名称()));
		etStart.setText(StrUtils.pareseNull(mWorkReport.开始时间));
		etEnd.setText(StrUtils.pareseNull(mWorkReport.结束时间));
		etComplete.setText(StrUtils.pareseNull(mWorkReport.已完成工作));
		etUncomplete.setText(StrUtils.pareseNull(mWorkReport.未完成工作));
		etNextWeekPlan.setText(StrUtils.pareseNull(mWorkReport.计划完成工作));
		if (!TextUtils.isEmpty(mWorkReport.自我总结)) {
			if (mWorkReport.自我总结.length() > 20) {
				etSummary.setGravity(Gravity.LEFT);
			}
			etSummary.setText(TextUtils.isEmpty(mWorkReport.自我总结) ? ""
					: mWorkReport.自我总结);
		}

		if (mWorkReport.编号 != 0 && mTypeId == 1) {
			llPubCommentRoot.setVisibility(View.VISIBLE);
			loadCommentList(mWorkReport.编号);
		}
	}

	private void setEdit(Boolean isEdit) {
		etArea.setFocusable(isEdit);
		etStart.setFocusable(isEdit);
		etEnd.setFocusable(isEdit);
		etComplete.setFocusable(isEdit);
		etUncomplete.setFocusable(isEdit);
		etNextWeekPlan.setFocusable(isEdit);
		etSummary.setFocusable(isEdit);
	}

	private void setEventListener() {
		etStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTypeId == 1) {
					dateAndTimePicker.showDateWheel(etStart);
				} else {
					dateAndTimePicker.showDateWheel("选取时间", etStart, false,
							true);
				}

			}
		});

		etEnd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTypeId == 1) {
					dateAndTimePicker.showDateWheel(etEnd);
				} else {
					dateAndTimePicker.showDateWheel("选取时间", etEnd, false, true);
				}
			}
		});

		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isEmpty()) {
					Toast.makeText(mContext, "请填写" + nullRowName,
							Toast.LENGTH_SHORT).show();
				} else {
					saveWeekLog();
				}
			}
		});

		llPubComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = etComment.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					showShortToast("评论内容不能为空");
				} else {
					FormComment formComment = new FormComment();
					formComment.set内容(content);
					formComment.set员工(UserBiz.getGlobalUserIntegerId());
					formComment.set时间(ViewHelper.getDateString());
					formComment.set流程编号(mWorkReport.编号);

					pubComment(formComment);
				}
			}
		});

	}

	private boolean isEmpty() {
		String startTime = etStart.getText().toString();
		String endTime = etEnd.getText().toString();
		String completeWork = etComplete.getText().toString();
		String unCompleteWork = etUncomplete.getText().toString();
		String nextWeekPlan = etNextWeekPlan.getText().toString();
		String summary = etSummary.getText().toString();

		if (TextUtils.isEmpty(startTime)) {
			nullRowName = "开始时间";
			return true;
		} else if (TextUtils.isEmpty(endTime)) {
			nullRowName = "结束时间";
			return true;
		} else if (TextUtils.isEmpty(completeWork)) {
			nullRowName = "本周已完成工作";
			return true;
		} else if (TextUtils.isEmpty(unCompleteWork)) {
			nullRowName = "本周未完成工作";
			return true;
		} else if (TextUtils.isEmpty(nextWeekPlan)) {
			nullRowName = "下周工作计划";
			return true;
		} else if (TextUtils.isEmpty(summary)) {
			nullRowName = "自我总结";
			return true;
		}

		mWorkReport.开始时间 = startTime;
		mWorkReport.结束时间 = endTime;
		mWorkReport.已完成工作 = completeWork;
		mWorkReport.未完成工作 = unCompleteWork;
		mWorkReport.计划完成工作 = nextWeekPlan;
		mWorkReport.自我总结 = summary;
		return false;
	}

	private void fetchThisWeekLog() {
		String url = Global.BASE_URL + "log/GetTodayDayWorkReport";

		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				LogUtils.e("stri_onResponseCodeErro", result);
			}

			@Override
			public void onResponse(String response) {
				LogUtils.i("response", response);
				List<工作总结报告> list = JsonUtils.ConvertJsonToList(response,
						工作总结报告.class);
				if (list != null && list.size() > 0) {
					mWorkReport = list.get(0);
					showWeekLog();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				LogUtils.e("stri_onFailure", ex + "");
			}
		});
	}

	/***
	 * 保存工作总结报告
	 */
	private void saveWeekLog() {
		ProgressDialogHelper.show(mContext);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isSuccess = false;
				try {
					isSuccess = zlServiceHelper.SaveWorkReport(mWorkReport);
					if (isSuccess) {
						handler.sendEmptyMessage(SAVE_SUCCESS);
					} else {
						handler.sendEmptyMessage(SAVE_FAILURE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(SAVE_FAILURE);
				}
			}
		}).start();
	}

	private final int SAVE_SUCCESS = 1;
	private final int SAVE_FAILURE = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SAVE_SUCCESS:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
				setResult(RESULT_OK);
				finish();
				break;
			case SAVE_FAILURE:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};

	private void pubComment(final FormComment formComment) {
		ProgressDialogHelper.show(mContext);
		final String url = Global.BASE_URL + "Flow/AddFlowComment";
		StringRequest.postAsyn(url, formComment, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("评论失败");
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();

				mCommentItemView.addBottomComment(CommentBiz
						.FormCommentTo日志评论(formComment));
				showShortToast("评论成功");

				InputSoftHelper.hiddenSoftInput(mContext, etComment);
				etComment.setText("");
				// mCommentList.add(CommentBiz.FormCommentTo日志评论(formComment));
				// new BoeryunCommentItemView(mContext, mCommentList,
				// ll_comment_root).createCommentView();
				// showShortToast("评论成功");
				// loadCommentList(mWorkReport.编号);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("评论失败");
			}
		});
	}

	/** 下载评论列表数据 */
	private void loadCommentList(final int flowId) {

		final String url = Global.BASE_URL + "Flow/GetFlowComment/" + flowId;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {

			}

			@Override
			public void onResponse(String response) {
				List<FormComment> list = JsonUtils.ConvertJsonToList(response,
						FormComment.class);
				if (list != null && list.size() > 0) {
					ll_comment_root.setVisibility(View.VISIBLE);
					mCommentList.clear();
					for (FormComment comment : list) {
						mCommentList.add(CommentBiz.FormCommentTo日志评论(comment));
					}
					mCommentItemView.createCommentView();
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				// TODO Auto-generated method stub

			}
		});

	}
}
