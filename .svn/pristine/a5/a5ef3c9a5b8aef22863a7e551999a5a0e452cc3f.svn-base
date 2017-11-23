package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.adapter.WorkLogViewPagerAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.fragment.TaskListFragment;
import com.cedarhd.fragment.TaskListFragment.IChangeCalenderMode;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.DictionaryDialogHelper;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

/** 任务Tab列表有 列表和周历 */
public class TaskTabListActivity extends BaseActivity {
	public static TaskTabListActivity activity;
	public static final String TAG = "TaskTabListActivity";
	public static final int REQUEST_CODE_SELECT_PUBLISHER = 3; // 选择发布人
	public static final int REQUEST_CODE_SELECT_EXECUTOR = 4; // 选择执行人

	/** 新建任务 */
	public static final int REQUEST_CODE_ADD_TASK = 15;
	/** 是否未读任务 */
	public static final String IS_UNREAD_TASK = "isUnReadTask";

	public static final String PROJECT_ID = "projectId";
	public static final String PROJECT_NAME = "projectName";

	private final String[] titleStrings = new String[] { "任务周历", "所有任务", "延期任务" };
	private List<Fragment> listFragment = new ArrayList<Fragment>();
	private DictionaryDialogHelper dictDialogHelper;
	private Resources resource;
	private FragmentManager fm;
	private int currentPage = 0;
	private ViewPager vPager;
	private WorkLogViewPagerAdapter adapter;
	private Context context;
	private ImageView ivFilter;
	private TextView tvTitle;// 标题
	private TextView tvWeekList;// 周任务
	private TextView tvTaskList;// 所有任务
	private TextView tvPostTask;// 延期任务
	private View viewTaskList;
	private View viewWeekList;
	private View viewPostTask;

	private List<TextView> tvList = new ArrayList<TextView>();
	private List<View> viewList = new ArrayList<View>();
	/** 所有任务页面 */
	private TaskListFragment taskListFragmnet;

	/** 默认过滤条件，从其他页面传递 */
	private String filter;
	private String mProjectName;

	/** 周任务列表页面 */
	// private WeekTaskFragment weekFragment;

	/** 延期任务列表 */
	// private TaskListFragment postTaskListFragmnet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_tab_list);
		initViews();
		activity = this;
		initPopWindow();
		recordMode();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initEvent();
	}

	/** 记录上次打开模式 */
	private void recordMode() {
		SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(
				context, PreferencesConfig.APP_USER_INFO);
		sharedPreferencesHelper.putBooleanValue(
				PreferencesConfig.IS_CALENDER_MODE_OPEN_TASK, false);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				String mUserSelectId = bundle.getString("UserSelectId");
				String mUserSelectName = bundle.getString("UserSelectName");
				// 选择执行人
				if (requestCode == REQUEST_CODE_SELECT_EXECUTOR) {
					tv_choose_excutor_pop.setText(mUserSelectName);
					String executor = StrUtils.deleteSign(mUserSelectId);
					executorId = executor;
				}
				if (requestCode == REQUEST_CODE_SELECT_PUBLISHER) {
					tv_choose_publisher_pop.setText(mUserSelectName);
					String publisher = StrUtils.deleteSign(mUserSelectId);
					publisherId = publisher;
				}
			}

			if (requestCode == REQUEST_CODE_ADD_TASK) {
				// 下拉刷新
				taskListFragmnet.refresh();
			}

		}
	}

	private void initViews() {
		boolean isUnRead = getIntent().getBooleanExtra(IS_UNREAD_TASK, false);
		projectId = getIntent().getStringExtra(PROJECT_ID);
		mProjectName = getIntent().getStringExtra(PROJECT_NAME);

		resource = getResources();
		fm = getSupportFragmentManager();
		context = TaskTabListActivity.this;
		dictDialogHelper = DictionaryDialogHelper.getInstance(context);
		// weekFragment = new WeekTaskFragment();
		// listFragment.add(weekFragment);
		taskListFragmnet = new TaskListFragment();
		Bundle arguments = new Bundle();
		arguments.putBoolean(IS_UNREAD_TASK, isUnRead);
		if (!TextUtils.isEmpty(projectId)) {
			filter = "分类='" + projectId + "'";
			arguments.putString(TAG, filter);
		}
		taskListFragmnet.setArguments(arguments);
		listFragment.add(taskListFragmnet);
		// postTaskListFragmnet = new TaskListFragment();
		Bundle postArguments = new Bundle();
		// 延期任务执行条件 今天以前的未完成 3:完成；5:提交
		// String postFilter = "执行时间 <'" + ViewHelper.getDateToday()
		// + "' and 任务状态 not in (3,5)";
		// postArguments.putString(TAG, postFilter);
		// postTaskListFragmnet.setArguments(postArguments);
		// listFragment.add(postTaskListFragmnet);

		// /** 监听Fragment中按钮点击 */
		// weekFragment.setOnButtonClick(new OnFragmentButtonClick() {
		// @Override
		// public void onClick() {
		// setCurrentPager(2);
		// }
		// });

		adapter = new WorkLogViewPagerAdapter(fm, listFragment);
		tvTitle = (TextView) findViewById(R.id.tv_title_tabtasklist);
		tvTaskList = (TextView) findViewById(R.id.tv_tabtasklist);
		tvWeekList = (TextView) findViewById(R.id.tv_week_task_list);
		tvPostTask = (TextView) findViewById(R.id.tv_post_tabtasklist);
		viewTaskList = findViewById(R.id.view_tabtasklist);
		viewWeekList = findViewById(R.id.view_week_task_list);
		viewPostTask = findViewById(R.id.view_post_tabtasklist);
		tvList.add(tvWeekList);
		tvList.add(tvTaskList);
		tvList.add(tvPostTask);
		viewList.add(viewWeekList);
		viewList.add(viewTaskList);
		viewList.add(viewPostTask);

		vPager = (ViewPager) findViewById(R.id.vp_tabtasklist);
		vPager.setAdapter(adapter);

		if (!TextUtils.isEmpty(mProjectName)) {
			tvTitle.setText(mProjectName);
		}

	}

	private void initEvent() {
		tvWeekList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				currentPage = 0;
				setCurrentPager(0);
				tvTitle.setText("任务周历");
			}
		});

		tvTaskList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				currentPage = 1;
				setCurrentPager(1);
				tvTitle.setText("所有任务");
			}
		});

		tvPostTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				currentPage = 2;
				setCurrentPager(2);
				tvTitle.setText("延期任务");
			}
		});
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.iv_back_task_tab_list);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView ImageViewNew = (ImageView) findViewById(R.id.iv_new_task_tab_list);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addNewTask();
			}
		});

		/***
		 * 过滤条件
		 */
		ivFilter = (ImageView) findViewById(R.id.iv_filter_task_tab_list);
		ivFilter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// initPop();
				// publisherId = "";
				// executorId = "";
				// assignTime = "";
				// et_select_status_pop.setTag(0);
				// 弹出popupWindow对话框选择过滤条件
				int pos[] = new int[2];
				// 获取在当前窗口内的绝对坐标
				v.getLocationOnScreen(pos);
				int height = v.getHeight() + pos[1];
				popupWindowFilter.showAtLocation(v, Gravity.TOP, 0, height);

				// popupWindowFilter.showAsDropDown(v);
				// initTab();
			}
		});

		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setTab();

				currentPage = pos;
				tvList.get(pos).setTextColor(
						resource.getColor(R.color.theme_text));
				viewList.get(pos).setVisibility(View.VISIBLE);
				tvTitle.setText(titleStrings[pos]);
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});

		/** 跳转到日历模式 */
		taskListFragmnet.changeCalenderMode(new IChangeCalenderMode() {
			@Override
			public void onChanged() {
				startCalenderTask();
			}
		});
	}

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		tvTaskList.setTextColor(getResources().getColor(R.color.gray));
		tvWeekList.setTextColor(getResources().getColor(R.color.gray));
		tvPostTask.setTextColor(getResources().getColor(R.color.gray));
		viewTaskList.setVisibility(View.INVISIBLE);
		viewWeekList.setVisibility(View.INVISIBLE);
		viewPostTask.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置当前显示页面
	 */
	public void setCurrentPager(int pos) {
		vPager.setCurrentItem(pos);
		tvList.get(pos).setTextColor(resource.getColor(R.color.theme_text));
		viewList.get(pos).setVisibility(View.VISIBLE);
		// if (pos == 1) {
		// ivFilter.setVisibility(View.VISIBLE);
		// } else {
		// ivFilter.setVisibility(View.GONE);
		// }
		// 显示过滤条件
		ivFilter.setVisibility(View.VISIBLE);
	}

	/**
	 * 新建按钮
	 */
	private void addNewTask() {
		// 新建任务
		Intent intent = new Intent(context, TaskNewActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
	}

	/**
	 * 过滤条件对话框中的控件
	 */
	private PopupWindow popupWindowFilter; // 过滤条件对话框
	private LinearLayout ll_choose_publisher_pop;// 选择发布人
	private LinearLayout ll_choose_excutor_pop;// 选择执行人
	private TextView tv_choose_publisher_pop;// 选择发布人
	private TextView tv_choose_excutor_pop;// 选择执行人
	private TextView et_select_status_pop; // 选择状态
	private TextView tv_select_assign_time_pop; // 执行时间
	private TextView tv_select_project_pop; // 选择状态

	private Button btn_cancel_pop;// 取消
	private Button btn_done_pop;// 确定

	private String executorId = ""; // 选中执行人编号
	private String publisherId = ""; // 选中发布人编号
	private String assignTime = "";
	private String projectId = "";// 项目编号

	/**
	 * 初始化过滤条件对话框
	 */
	private void initPopWindow() {
		View popupView = getLayoutInflater().inflate(
				R.layout.pop_filter_tabtasklist, null);
		popupWindowFilter = new PopupWindow(popupView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupWindowFilter.setAnimationStyle(R.style.AnimationFade);
		popupWindowFilter.update();
		// 点击空白处 对话框消失
		popupWindowFilter.setTouchable(true);
		popupWindowFilter.setOutsideTouchable(true);
		popupWindowFilter.setBackgroundDrawable(new BitmapDrawable(
				getResources(), (Bitmap) null));
		ll_choose_excutor_pop = (LinearLayout) popupView
				.findViewById(R.id.ll_select_executor_pop_filter_tab);
		ll_choose_publisher_pop = (LinearLayout) popupView
				.findViewById(R.id.ll_select_publisher_pop_filter_tab);
		tv_choose_excutor_pop = (TextView) popupView
				.findViewById(R.id.tv_select_executor_pop_filter_tab);
		tv_choose_publisher_pop = (TextView) popupView
				.findViewById(R.id.tv_select_publisher_pop_filter_tab);
		et_select_status_pop = (TextView) popupView
				.findViewById(R.id.et_select_status_pop_filter_tab);
		tv_select_assign_time_pop = (TextView) popupView
				.findViewById(R.id.tv_select_assignTime_pop_filter_tab);
		tv_select_project_pop = (TextView) popupView
				.findViewById(R.id.et_select_project_pop_filter_tab);

		btn_cancel_pop = (Button) popupView
				.findViewById(R.id.btn_cancel_pop_filter_tab_tabtasklist);
		btn_done_pop = (Button) popupView
				.findViewById(R.id.btn_done_pop_filter_tab_tabtasklist);

		et_select_status_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// new DictDialogHelper(context, et_select_status_pop, "任务状态");
				dictDialogHelper.showDialog(et_select_status_pop, "任务状态");
			}
		});

		btn_cancel_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetFilter();
			}
		});

		ll_choose_excutor_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectUser(REQUEST_CODE_SELECT_EXECUTOR);
			}
		});

		ll_choose_publisher_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectUser(REQUEST_CODE_SELECT_PUBLISHER);
			}
		});

		tv_select_assign_time_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DateAndTimePicker dateAndTimePicker = new DateAndTimePicker(
						context);
				dateAndTimePicker.showDateWheel(tv_select_assign_time_pop,
						false);
				dateAndTimePicker.setOnSelectedListener(new ISelected() {
					@Override
					public void onSelected(String date) {
						String shortDate = ViewHelper
								.convertStrToFormatDateStr(date,
										ViewHelper.FORMAT_STR_DATE);
						tv_select_assign_time_pop.setText(shortDate);
					}
				});
			}
		});

		tv_select_project_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// new DictDialogHelper(context, tv_select_project_pop, "项目管理");
				dictDialogHelper.showDialog(tv_select_project_pop, "项目管理");
			}
		});

		// 确定，执行查询条件
		btn_done_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindowFilter.dismiss();
				// demand.附加条件 = "1=1";
				String filterStr = "1=1";
				assignTime = tv_select_assign_time_pop.getText().toString();
				if (!TextUtils.isEmpty(assignTime)) {
					filterStr += " and 执行时间 like '%" + assignTime + "%'";
				}
				if (!TextUtils.isEmpty(publisherId)) {
					filterStr += " and 发布人=" + publisherId;
				}
				if (!TextUtils.isEmpty(executorId)) {
					filterStr += " and 执行人=" + executorId;
				}
				if (!TextUtils.isEmpty(et_select_status_pop.getText()
						.toString())) {
					int status = (Integer) et_select_status_pop.getTag();
					filterStr += " and 任务状态 =" + status;
				}
				if (!TextUtils.isEmpty(tv_select_project_pop.getText()
						.toString())) {
					int project = (Integer) tv_select_project_pop.getTag();
					filterStr += " and 分类 =" + project;
				}

				if (!TextUtils.isEmpty(filter)) {
					filterStr += " and " + filter;
				}
				taskListFragmnet.reload(filterStr);
			}
		});
	}

	private void selectUser(int requestCode) {
		// 跳转到选择员工的Activity
		Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
		intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true);
		startActivityForResult(intent, requestCode);
	}

	/** 重置所有过滤条件 */
	private void resetFilter() {
		assignTime = "";
		executorId = "";
		publisherId = "";

		tv_choose_excutor_pop.setText("");
		tv_choose_publisher_pop.setText("");
		et_select_status_pop.setText("");
		tv_select_assign_time_pop.setText("");
		tv_select_project_pop.setText("");
	}

	/** 跳转到日历模块 */
	private void startCalenderTask() {
		Intent intent = new Intent(context, TaskCalenderActivity.class);
		if (!TextUtils.isEmpty(projectId)) {
			intent.putExtra(TaskCalenderActivity.PROJECT_ID, projectId);
			intent.putExtra(TaskCalenderActivity.PROJECT_NAME, mProjectName);
		}
		startActivity(intent);
		finish();
	}
}
