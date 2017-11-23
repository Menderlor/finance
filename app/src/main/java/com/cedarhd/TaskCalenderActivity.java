package com.cedarhd;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cedarhd.adapter.CalendarAdapter;
import com.cedarhd.adapter.SingleCalendarAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.control.SlideMenu;
import com.cedarhd.helpers.CalendarHelper;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DateAndTimePicker.ISelected;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ServerCall;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.任务;
import com.cedarhd.models.每日任务数量;
import com.cedarhd.receiver.TaskNotificaitionReceiver;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 * 可收缩日历 任务列表
 * 
 */
public class TaskCalenderActivity extends BaseActivity {
	private final String TAG = "TaskCalenderActivity";
	private Context context;

	/** 选择员工 */
	public static final int REQUEST_CODE_SELECT_USER = 3;

	/** 新建任务 */
	public static final int REQUEST_CODE_ADD_TASK = 5;

	/** 项目编号 */
	public static final String PROJECT_ID = "projectId";
	public static final String PROJECT_NAME = "projectName";

	/** 是否收缩为 只显示单周数据 */
	private boolean isWeekMode = false;

	private String mProjectId = "";// 项目编号
	private String mProjectName = "";// 项目编号

	/** 当前执行人 */
	private String mUerId;
	/** 当前选中天 */
	private int currenSelectedDay;

	/** 每一格日历的宽高 */
	private int cellWidth;
	private Demand demand;
	private ServerCall serverCall;
	private ZLServiceHelper zlServiceHelper;
	private CalendarHelper sc;

	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	/** 每次添加gridview到viewflipper中时给的标记 */
	private int gvFlag = 0;

	/** 月历日期格数 */
	private int gridCount;
	private boolean isFirstVisble = true;
	private boolean isLastVisble = false;

	private static List<任务> mList = new ArrayList<任务>();
	private CommanAdapter<任务> mDataAdapter;

	private DateAndTimePicker dateAndTimePicker;

	private GestureDetector gestureDetector = null;
	private GestureDetector lv_gestureDetector = null;
	private CalendarAdapter adapterMonth = null;
	private SingleCalendarAdapter adapterWeek = null;
	private ViewFlipper flipper = null;
	private GridView gridView = null;
	private ListView lv;
	private TextView tvSelectDate;

	/** 查看任务列表 */
	private ImageView ivTaskList;

	// 头部显示月份
	private TextView tvMonth;
	private TextView tvWeek;
	private TextView tvYear;

	/** 选择用户 */
	private TextView tvSelectUser;
	/** 新建 */
	private ImageView ivAdd;
	/** 查看更多 */
	private TextView tvMore;

	/** 切换日历模式箭头 */
	private LinearLayout llArrow;

	/** 切换日历模式箭头 */
	private ImageView ivArrow;

	/** 语音输入 */
	private ImageView ivTalking;
	/** 提交发布任务 */
	private ImageView ivSubmit;
	/** 内容输入框 */
	private EditText etContent;
	/**
	 * back按钮
	 */
	private ImageView back;

	/***
	 * 任务内容
	 */
	private String content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_task_calender);
		initData();
		initViews();
		setOnClickListener();

		recordMode();
	}

	/** 记录上次打开模式 */
	private void recordMode() {
		SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(
				context, PreferencesConfig.APP_USER_INFO);
		sharedPreferencesHelper.putBooleanValue(
				PreferencesConfig.IS_CALENDER_MODE_OPEN_TASK, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null && requestCode == REQUEST_CODE_SELECT_USER) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				String userId = bundle.getString("UserSelectId");
				String userName = bundle.getString("UserSelectName");
				// 选择执行人
				if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(userName)) {
					tvSelectUser.setText(userName);
					etContent.setHint("给" + userName + "发条任务..");
					mUerId = StrUtils.deleteSign(userId);
					reload(getFilterStr());
					downLoadTaskCount();
				}
			} else if (requestCode == REQUEST_CODE_ADD_TASK) {
				reload(getFilterStr());
			}
		}
	}

	private void initData() {
		demand = new Demand();
		serverCall = new ServerCall();
		zlServiceHelper = new ZLServiceHelper();
		sc = new CalendarHelper();
		mProjectId = getIntent().getStringExtra(PROJECT_ID);
		mProjectName = getIntent().getStringExtra(PROJECT_NAME);

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
		currenSelectedDay = day_c;
		mUerId = Global.mUser.Id;
	}

	private void initViews() {
		gestureDetector = new GestureDetector(this, new MyGestureListener());
		lv_gestureDetector = new GestureDetector(this,
				new MyListViewGestureListener());
		dateAndTimePicker = new DateAndTimePicker(context);
		flipper = (ViewFlipper) findViewById(R.id.test_flipper);
		tvSelectDate = (TextView) findViewById(R.id.tv_date_task_calendar);
		lv = (ListView) findViewById(R.id.lv_task_calendar);
		tvMonth = (TextView) findViewById(R.id.tv_month_task_calendar);
		tvWeek = (TextView) findViewById(R.id.tv_week_task_calendar);
		tvYear = (TextView) findViewById(R.id.tv_year_task_calendar);
		tvSelectUser = (TextView) findViewById(R.id.tv_select_user_task_calendar);
		tvMore = (TextView) findViewById(R.id.tv_more_task_calendar);
		ivAdd = (ImageView) findViewById(R.id.iv_add_task_calendar);
		ivTalking = (ImageView) findViewById(R.id.iv_talking_task_calendar);
		ivSubmit = (ImageView) findViewById(R.id.iv_save_task_calendar);
		etContent = (EditText) findViewById(R.id.et_content_task_calendar);
		back = (ImageView) findViewById(R.id.iv_back_task_calendar);
		llArrow = (LinearLayout) findViewById(R.id.ll_arrow_top);
		ivArrow = (ImageView) findViewById(R.id.iv_arrow_top);
		ivTaskList = (ImageView) findViewById(R.id.iv_task_list_calender);
		flipper.removeAllViews();
		gridCount = getGridCount();
		adapterMonth = new CalendarAdapter(this, jumpMonth, jumpYear, year_c,
				month_c, day_c, gridCount);
		addGridView();
		// addGridView_single();
		gridView.setAdapter(adapterMonth);
		// gvSingleRow.setAdapter(calV);
		flipper.addView(gridView, 0);
		changeCalendarToWeek();
		// addTextToTopTextView(currentMonth);
		initDataAdapter();
		lv.setAdapter(mDataAdapter);
		// String nowDate = getSelectDate();
		tvSelectDate.setText("今日任务");
		setCurrentDateShowInfo();

		reload(getFilterStr());
		// 加载数据
		// reload("执行时间 like '%" + getSelectDate() + "%'");
		// reload("执行时间 like '%" + + "%'");
	}

	/***
	 * 设置当前显示日期
	 */
	private void setCurrentDateShowInfo() {
		int selectYear = year_c + jumpYear;
		int selectMonth = month_c + jumpMonth;
		if (selectMonth > 0) {
			// 往下一个月滑动
			if (selectMonth % 12 == 0) {
				selectYear = year_c + selectMonth / 12 - 1;
				selectMonth = 12;
			} else {
				selectYear = year_c + selectMonth / 12;
				selectMonth = selectMonth % 12;
			}
		} else {
			// 往上一个月滑动
			selectYear = year_c - 1 + selectMonth / 12;
			selectMonth = selectMonth % 12 + 12;
			if (selectMonth % 12 == 0) {

			}
		}

		int currentWeek = sc.getWeekdayOfMonth(year_c, selectMonth,
				currenSelectedDay);
		tvMonth.setText(selectMonth + "月");
		tvWeek.setText(CalendarHelper.getWeekdayOfMonth(currentWeek));
		tvYear.setText(selectYear + "年");
	}

	/** 获取最新查询条件 */
	private String getFilterStr() {
		String filter = "";
		// int currentMonth = month_c + jumpMonth;
		// String selectDate = year_c + "-" + String.format("%02d",
		// currentMonth)
		// + "-" + String.format("%02d", currenSelectedDay);
		String selectDate = getSelectDate();
		if (!TextUtils.isEmpty(selectDate)) {
			filter += " 执行时间 like '%" + selectDate + "%'";
		}

		if (!TextUtils.isEmpty(mUerId)) {
			filter += " and (执行人='" + mUerId + "' or 参与人 like '%" + mUerId
					+ "%')";
		}

		if (!TextUtils.isEmpty(mProjectId)) {
			filter += " and 分类='" + mProjectId + "'";
		}

		LogUtils.i("filter", filter);
		return filter;
	}

	private String getSelectDate() {
		int selectYear = year_c + jumpYear;
		int selectMonth = month_c + jumpMonth;
		if (selectMonth > 0) {
			// 往下一个月滑动
			if (selectMonth % 12 == 0) {
				selectYear = year_c + selectMonth / 12 - 1;
				selectMonth = 12;
			} else {
				selectYear = year_c + selectMonth / 12;
				selectMonth = selectMonth % 12;
			}
		} else {
			// 往上一个月滑动
			selectYear = year_c - 1 + selectMonth / 12;
			selectMonth = selectMonth % 12 + 12;
			if (selectMonth % 12 == 0) {

			}
		}
		String selectDate = selectYear + "-"
				+ String.format("%02d", selectMonth) + "-"
				+ String.format("%02d", currenSelectedDay);
		return selectDate;
	}

	private void initDataAdapter() {
		mDataAdapter = new CommanAdapter<任务>(mList, context,
				R.layout.item_calendar_task) {
			@Override
			public void convert(int position, final 任务 item,
					BoeryunViewHolder viewHolder) {
				((SlideMenu) viewHolder.getConvertView()).close(false);
				TextView tvContent = viewHolder
						.getView(R.id.tv_content_calendar_task);
				final ImageView ivCheck = viewHolder
						.getView(R.id.iv_check_calendar_task);
				TextView ivDelete = (TextView) viewHolder
						.getView(R.id.tv_delete_calendar_task);
				if (item.Status == 3) {
					// ivCheck.setImageResource(R.drawable.select_clerk_zmyok);
					ivCheck.setImageResource(R.drawable.ico_check_green);
				} else {
					// ivCheck.setImageResource(R.drawable.select_clerk_zmyfalse);
					ivCheck.setImageResource(R.drawable.ico_check_gray);
				}
				tvContent.setText(item.Content + "");

				tvContent.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startTaskInfo(item);
					}
				});

				ivCheck.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (item.Status != 3) {// 如果状态不是完成
							item.Status = 3;
							Builder builder = new Builder(context);
							builder.setTitle("是否设置任务为完成");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											ivCheck.setImageResource(R.drawable.ico_check_green);
											saveTask(item, true);
										}
									}).setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							AlertDialog dialog = builder.create();
							dialog.show();
						} else {
							Toast.makeText(context, "已完成", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});

				ivDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Global.mUser.Id.equals(item.Publisher + "")) {
							mDataAdapter.remove(item);
							new Thread(new Runnable() {
								@Override
								public void run() {
									boolean isSuccess = zlServiceHelper
											.deleteTask(item.getId());
									Message msg = handler.obtainMessage();
									msg.obj = item;
									if (isSuccess) {
										msg.what = SUCCEESS_DELETE_TASK;
										handler.sendMessage(msg);
									} else {
										msg.what = FAILURE_DELETE_TASK;
										handler.sendMessage(msg);
									}
								}
							}).start();
						} else {
							Toast.makeText(context, "只能删除自己发布的任务",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		};
	};

	private void setOnClickListener() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 返回
		tvMonth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 新建任务
		ivAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TaskNewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ADD_TASK);
			}
		});

		tvMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 查看更多，选择 任务列表模式或查看任务统计
				// showSelectPopwindow();

				// 直接进入任务统计
				Intent intent = new Intent(context, TaskMoreActivity.class);
				startActivity(intent);
			}
		});

		tvSelectUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectUser(REQUEST_CODE_SELECT_USER);
			}
		});

		llArrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isWeekMode) {
					changeCalendarToMonth();
				} else {
					changeCalendarToWeek();
				}
			}
		});

		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (firstVisibleItem == 0) {
					isFirstVisble = true;
				} else {
					isFirstVisble = false;
				}

				if (firstVisibleItem + visibleItemCount >= totalItemCount) {
					isLastVisble = true;
				} else {
					isLastVisble = false;
				}
			}
		});

		ivTalking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SpeechDialogHelper(context, (Activity) context, etContent,
						true);
			}
		});

		ivSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = etContent.getText().toString();
				if (TextUtils.isEmpty(content)) {
					Toast.makeText(context, "任务内容不能为空哦", Toast.LENGTH_SHORT)
							.show();
				} else {
					任务 task = new 任务();
					task.Content = content;
					task.Executor = Integer.parseInt(mUerId);
					task.Publisher = Integer.parseInt(Global.mUser.Id);
					task.Status = 1;
					task.AssignTime = getSelectDate() + " 09:00:01";
					ProgressDialogHelper.show(context, "保存中,请稍候...");
					saveTask(task, false);
				}
			}
		});

		lv.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return lv_gestureDetector.onTouchEvent(event);
			}
		});

		ivTaskList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TaskTabListActivity.class);
				if (!TextUtils.isEmpty(mProjectId)) {
					intent.putExtra(TaskTabListActivity.PROJECT_ID, mProjectId);
					intent.putExtra(TaskTabListActivity.PROJECT_NAME,
							mProjectName);
				}
				startActivity(intent);
				finish();
				// 设置切换动画
				overridePendingTransition(R.anim.tran_enter, R.anim.tran_exit);
			}
		});

		dateAndTimePicker.setOnSelectedListener(new ISelected() {
			@Override
			public void onSelected(String date) {
				// Toast.makeText(context, "选取时间：" + date, Toast.LENGTH_SHORT)
				// .show();
				SharedPreferencesHelper sp = new SharedPreferencesHelper(
						context, "task");
				sp.putValue("content", content);
				LogUtils.i(TAG, date);
				Date d = ViewHelper.formatStrToDate(date);
				sendAlarm(d.getTime());
			}
		});
	}

	/**
	 * 保存任务
	 * 
	 * @param item
	 *            任务实体
	 * @param isEditStatus
	 *            是否是修改状态
	 */
	private void saveTask(final 任务 item, final boolean isEditStatus) {
		// ProgressDialogHelper.show(context, "保存中,请稍候...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessed = zlServiceHelper.EditTask(item);
					if (isEditStatus) {
						if (isSuccessed) {
							handler.sendEmptyMessage(UPDATE_TASK_STATUS_SUCCESS);
						} else {
							handler.sendEmptyMessage(UPDATE_TASK_STATUS_FAILED);
						}
					} else {
						if (isSuccessed) {
							content = item.Content;
							handler.sendEmptyMessage(SAVE_TASK_SUCCESS);
						} else {
							handler.sendEmptyMessage(SAVE_TASK_FAILED);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(UPDATE_TASK_STATUS_FAILED);
				}
			}
		}).start();
	}

	private void startTaskInfo(final 任务 task) {
		Intent intent = new Intent(context, TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(TaskInfoActivity.TAG, task);
		intent.putExtras(bundle);
		startActivity(intent);
		if (TextUtils.isEmpty(task.ReadTime)) {
			// 读任务
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						zlServiceHelper.ReadDynamic(task.Id, 3);
					} catch (Exception e) {
						LogUtils.e("erro", "" + e);
					}
				}
			}).start();
		}
	}

	private void selectUser(int requestCode) {
		// 跳转到选择员工的Activity
		Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
		intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true);
		startActivityForResult(intent, requestCode);
	}

	private int mScrollState = 0;
	private float startX;
	private float startY;
	private int gridMaxHeight = cellWidth * 6;
	private int marginTop;

	LinearLayout.LayoutParams flipParams = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			int gvFlag = 0;
			float distanceX = e1.getX() - e2.getX();
			float distanceY = e1.getY() - e2.getY();
			if (Math.abs(distanceX) > Math.abs(distanceY)) {
				// 每次添加gridview到viewflipper中时给的标记
				if (distanceX > 120) {
					// 像左滑动
					enterNextMonth(gvFlag);
					return true;
				} else if (distanceX < -120) {
					// 向右滑动
					enterPrevMonth(gvFlag);
					return true;
				}
			} else {
				if (distanceY > 120 && isWeekMode == false) {
					isWeekMode = true;
					changeCalendarToWeek();
					return true;
				} else if (distanceY < -30 && isWeekMode == true) {
					// // 向下滑动
					isWeekMode = false;
					changeCalendarToMonth();
					return true;
				}
				// Toast.makeText(context, "纵向滑动:" + distanceY,
				// Toast.LENGTH_SHORT)
				// .show();
				// gridView.setLayoutParams(params);
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/** listView滑动手势 */
	private class MyListViewGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1 == null || e2 == null) {
				return false;
			}
			int deltaY = (int) (e2.getY() - e1.getY());
			int deltaX = (int) (e2.getX() - e1.getX());
			// LogUtils.i(TAG, deltaY + "--" + deltaX);
			if (Math.abs(deltaX) < Math.abs(deltaY)) {
				gridMaxHeight += deltaY / 2;
				marginTop += deltaY / 2;

				LogUtils.i("onScroll_lv", marginTop + "+++" + deltaY);
				if (!isWeekMode && deltaY < 0 && isLastVisble) { //
					// 月历模式，上拉，并且处于底部
					changeCalendarToWeek();
					return true;
				} else if (isWeekMode && deltaY > 0 && isFirstVisble) { //
					// 周模式下，下拉,滑动到顶部
					changeCalendarToMonth();
					return true;
				}
			} else {
				return true;
			}
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (e1 == null || e2 == null) {
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
			int deltaY = (int) (e2.getY() - e1.getY());
			int deltaX = (int) (e2.getX() - e1.getX());
			// LogUtils.i(TAG, deltaY + "--" + deltaX);
			LogUtils.i(TAG, distanceY + "--" + distanceX);
			if (Math.abs(distanceX) < Math.abs(distanceY)) {
				gridMaxHeight += deltaY / 2;
				marginTop += deltaY / 2;

				LogUtils.i("onScroll_lv", marginTop + "+++" + deltaY);
				if (!isWeekMode && deltaY < 0 && isLastVisble) { // 月历模式，上拉，并且处于底部
					changeCalendarToWeek();
					return true;
				} else if (isWeekMode && deltaY > 0 && isFirstVisble) { // 周模式下，下拉,滑动到顶部
					changeCalendarToMonth();
					return true;
				}
				return super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				return true;
			}

		}
	}

	/**
	 * 移动到下一个月
	 * 
	 * @param gvFlag
	 */
	private void enterNextMonth(int gvFlag) {
		addGridView(); // 添加一个gridView
		jumpMonth++; // 下一个月
		gvFlag++;
		flipper.addView(gridView, gvFlag);
		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_left_out));
		flipper.showNext();
		flipper.removeViewAt(0);

		int currentDay = jumpMonth == 0 ? day_c : 1;
		setCurrentDateShowInfo();

		if (isWeekMode) {
			adapterWeek = new SingleCalendarAdapter(context, getResources(),
					jumpMonth, jumpYear, year_c, month_c, currentDay);
			gridView.setAdapter(adapterWeek);

		} else {
			gridCount = getGridCount();
			setCalendarHeight();
			adapterMonth = new CalendarAdapter(this, jumpMonth, jumpYear,
					year_c, month_c, currentDay, gridCount);
			gridView.setAdapter(adapterMonth);
			downLoadTaskCount();
		}
		// addTextToTopTextView(currentMonth); // 移动到下一月后，将当月显示在头标题中
	}

	/**
	 * 移动到上一个月
	 * 
	 * @param gvFlag
	 */
	private void enterPrevMonth(int gvFlag) {
		addGridView(); // 添加一个gridView

		gvFlag++;
		// addTextToTopTextView(currentMonth); // 移动到上一月后，将当月显示在头标题中
		flipper.addView(gridView, gvFlag);

		flipper.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_right_out));
		flipper.showPrevious();
		flipper.removeViewAt(0);

		jumpMonth--; // 上一个月
		int currentDay = jumpMonth == 0 ? day_c : 1;
		// gridCount = getGridCount();
		setCurrentDateShowInfo();
		//
		// setCalendarHeight();
		// adapterMonth = new CalendarAdapter(this, jumpMonth, jumpYear, year_c,
		// month_c, currentDay, gridCount);

		if (isWeekMode) {
			adapterWeek = new SingleCalendarAdapter(context, getResources(),
					jumpMonth, jumpYear, year_c, month_c, currentDay);
			gridView.setAdapter(adapterWeek);

		} else {
			gridCount = getGridCount();
			setCalendarHeight();
			adapterMonth = new CalendarAdapter(this, jumpMonth, jumpYear,
					year_c, month_c, currentDay, gridCount);
			gridView.setAdapter(adapterMonth);

			downLoadTaskCount();
		}

	}

	/***
	 * 获取月历显示多少天 35或42
	 * 
	 * @return
	 */
	private int getGridCount() {
		int dayOfWeek_first = sc.getWeekdayOfMonthFirst(year_c, month_c
				+ jumpMonth);
		int daysOfMonth = 0;
		if (dayOfWeek_first == 0) {// 星期天放在末
			dayOfWeek_first = 7;
		}
		boolean isLeapyear = sc.isLeapYear(year_c); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month_c); // 某月的总天数
		int count = dayOfWeek_first - 1 + daysOfMonth;
		int dayNumber = 35;
		if (count > 35) {
			dayNumber = 42;
		} else {
			// 区分5行和6行的情况
			dayNumber = 35;
		}
		return dayNumber;
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 * 
	 * @param view
	 */
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		textDate.append(adapterMonth.getShowYear()).append("年")
				.append(adapterMonth.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
	}

	private void addGridView() {
		DisplayMetrics display = getResources().getDisplayMetrics();
		int Width = display.widthPixels;
		cellWidth = Width / 7;

		gridView = new GridView(this);
		// 设置列数和列宽
		gridView.setNumColumns(7);
		gridView.setColumnWidth(cellWidth);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return TaskCalenderActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String itemClickDay = "";
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				if (isWeekMode) {
					// 单周模式选中选中号数
					// 点击日历选中号数
					itemClickDay = adapterWeek.getItem(position).split("\\.")[0];
					int itemDay = Integer.parseInt(itemClickDay);
					// if (itemDay < position + 1) {
					// // // 下月末的数据
					// } else
					// int nextDay = 7 - adapterWeek.getNextVisblePos();
					if (position < adapterWeek.getFirstVisblePos()) {
						// 上月末的数据
					}
					// else if (position + 1 > nextDay) {
					// // 下月初的天数
					// }
					else {
						// 本月日期 可以点击
						currenSelectedDay = Integer.parseInt(itemClickDay);
						adapterWeek.markSelected(position);
						currenSelectedDay = itemDay;
						LogUtils.i("Time", getFilterStr());

						setCurrentDateShowInfo();
						reload(getFilterStr());
					}
				} else {
					// 点击日历选中号数
					itemClickDay = adapterMonth.getItem(position).split("\\.")[0];
					if ((position <= 6 && itemClickDay.length() == 2)) {
						// 如果上月末的数 或 下月初的数值 不能点击
					} else if ((position > adapterMonth.getCount() - 7 && itemClickDay
							.length() == 1)) {
					} else {
						adapterMonth.markSelected(position);
						currenSelectedDay = Integer.parseInt(itemClickDay);
						// tvSelectDate.setText(getSelectDate());
						LogUtils.i("Time", getFilterStr());
						setCurrentDateShowInfo();
						reload(getFilterStr());
					}
				}

			}
		});
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		gridView.setLayoutParams(params);
	}

	/** 改变日历为单周 */
	private void changeCalendarToWeek() {
		/* 位移动画 */
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, -0.8f);
		animation.setDuration(1 * 300);
		// 设置加速模式
		// animation.setInterpolator(new DecelerateInterpolator());
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				adapterWeek = new SingleCalendarAdapter(context,
						getResources(), jumpMonth, jumpYear, year_c, month_c,
						currenSelectedDay);
				gridView.setAdapter(adapterWeek);

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, cellWidth + 5);
				flipper.setLayoutParams(params);
				isWeekMode = true;
				ivArrow.setImageResource(R.drawable.shang_04);
			}
		});
		gridView.startAnimation(animation);
	}

	/** 改变日历为月历 */
	private void changeCalendarToMonth() {
		/* 位移动画 */
		TranslateAnimation animation = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, -1,
				TranslateAnimation.RELATIVE_TO_PARENT, 0);
		animation.setDuration(1 * 200);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				gridCount = getGridCount();
				setCalendarHeight();

				adapterMonth = new CalendarAdapter(context, jumpMonth,
						jumpYear, year_c, month_c, currenSelectedDay, gridCount);
				gridView.setAdapter(adapterMonth);

				isWeekMode = false;

				// 固定样式
				ivArrow.setImageResource(R.drawable.shang_04);

				downLoadTaskCount();
				// gridView.setLayoutParams(params);
			}
		});
		gridView.startAnimation(animation);
	}

	private void reload(String moreFilter) {
		demand.表名 = "任务";
		demand.方法名 = "Task/GetOtherList/";
		demand.条件 = "";
		demand.附加条件 = moreFilter;
		demand.每页数量 = 100;
		demand.偏移量 = 0;
		ProgressDialogHelper.show(context);
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String result = serverCall.makeServerCalll_Post(demand);
					List<任务> list = JsonUtils.ConvertJsonToList(result,
							任务.class);
					if (list != null && list.size() >= 0) {
						Message msg = handler.obtainMessage();
						msg.obj = list;
						msg.what = SUCCEESS_LOAD_DATA;
						handler.sendMessage(msg);
					} else {
						handler.sendEmptyMessage(FAILURE_LOAD_DATA);
					}
				}
			}).start();
		} catch (Exception e) {
			LogUtils.e("erro", e + "");
			handler.sendEmptyMessage(FAILURE_LOAD_DATA);
		}
	}

	/**
	 * 设置日历的高度
	 */
	private void setCalendarHeight() {
		int flipperHeight = cellWidth * 5;
		if (gridCount > 35) {
			flipperHeight = cellWidth * 6;
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, flipperHeight);
		// ViewGroup.MarginLayoutParams gvParams = new
		// ViewGroup.MarginLayoutParams(
		// ViewGroup.LayoutParams.MATCH_PARENT, flipperHeight);
		FrameLayout.LayoutParams gvParams = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		flipper.setLayoutParams(params);
		gridView.setLayoutParams(gvParams);
	}

	private void showSelectPopwindow() {
		// 获得LayoutInflater的另外一种方式
		LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(context);
		// 加载指定布局作为PopupWindow的显示内容
		View contentView = inflater
				.inflate(R.layout.pop_task_mode_select, null);
		int[] location = new int[2];
		tvMore.getLocationOnScreen(location);
		int mScreenWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		int popHeight = 110; // 弹出Popup的高度，默认只显示日志和任务
		// 初始化popupWindow,指定显示内容和宽高
		PopupWindow popupWindow = new PopupWindow(contentView,
				(int) ViewHelper.dip2px(context, 150), (int) ViewHelper.dip2px(
						context, popHeight));
		int locationX = mScreenWidth - (int) ViewHelper.dip2px(context, 150);
		// int locationY = realHeight - ivAdd.getHeight();
		int locationY = location[1] + tvMore.getHeight();
		LogUtils.i(TAG, "y=" + locationY);
		findViewsFromPop(contentView, popupWindow);
		// setPopFoucus();
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAtLocation(ivAdd, Gravity.NO_GRAVITY, locationX,
				locationY);
	}

	private void findViewsFromPop(View contentView,
			final PopupWindow popupWindow) {
		contentView.findViewById(R.id.ll_tasklist_mode).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 任务列表
						Intent intent = new Intent(context,
								TaskTabListActivity.class);
						startActivity(intent);
						popupWindow.dismiss();
					}
				});

		contentView.findViewById(R.id.ll_task_summary).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 进入任务统计
						Intent intent = new Intent(context,
								TaskMoreActivity.class);
						startActivity(intent);
						popupWindow.dismiss();
					}
				});
	}

	private static final int SUCCEESS_LOAD_DATA = 1;
	private static final int FAILURE_LOAD_DATA = 2;
	private static final int UPDATE_TASK_STATUS_SUCCESS = 3;
	private static final int UPDATE_TASK_STATUS_FAILED = 4;

	/** 保存任务成功 */
	private static final int SAVE_TASK_SUCCESS = 5;

	/** 保存任务失败 */
	private static final int SAVE_TASK_FAILED = 6;
	private static final int SUCCEESS_DELETE_TASK = 7;
	private static final int FAILURE_DELETE_TASK = 8;

	/** 成功获得当前月任务数量列表 */
	private static final int SUCCEESS_GET_TASK_COUNT = 9;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEESS_LOAD_DATA:
				ProgressDialogHelper.dismiss();
				mList = (List<任务>) msg.obj;
				mDataAdapter.changeData(mList);
				lv.setAdapter(mDataAdapter);
				break;
			case FAILURE_LOAD_DATA:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载数据失败", Toast.LENGTH_SHORT).show();
				break;
			case UPDATE_TASK_STATUS_SUCCESS:
				ProgressDialogHelper.dismiss();
				reload(getFilterStr());
				downLoadTaskCount();
				break;
			case UPDATE_TASK_STATUS_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "修改状态异常!", Toast.LENGTH_SHORT).show();
				break;
			case SAVE_TASK_SUCCESS:
				ProgressDialogHelper.dismiss();
				reload(getFilterStr());
				etContent.setText("");
				closeSoftInput();
				// TODO
				// dateAndTimePicker.showDateWheel("设置提醒时间");
				break;
			case SAVE_TASK_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "保存异常!", Toast.LENGTH_SHORT).show();
				break;
			case SUCCEESS_DELETE_TASK:
				// 任务 item = (任务) msg.obj;
				// mDataAdapter.remove(item);
				break;
			case FAILURE_DELETE_TASK:
				任务 item = (任务) msg.obj;
				mDataAdapter.addTop(item, false);
				Toast.makeText(context, "删除任务失败异常!", Toast.LENGTH_SHORT).show();
				break;
			case SUCCEESS_GET_TASK_COUNT:
				List<每日任务数量> list = (List<每日任务数量>) msg.obj;
				if (!isWeekMode) {
					adapterMonth.setmCountList(list);
					adapterMonth.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}
		};
	};

	private void sendAlarm(long millisecond) {
		Intent intent = new Intent(context, TaskNotificaitionReceiver.class);
		intent.setAction("repeating");
		// PendingIntent.FLAG_NO_CREATE
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		// 设定一个五秒后的时间
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		// alarm.cancel(sender);
		// // 每5秒执行一次
		// AlarmManager.RTC_WAKEUP;
		// millisecond -= new Date().getTime();
		// alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, millisecond, sender);
		alarm.set(AlarmManager.RTC, millisecond, sender);
		LogUtils.i("alarm", "启动闹钟..." + intent.getAction() + "间隔=="
				+ millisecond);
		Toast.makeText(context, millisecond + "毫秒后alarm开启", Toast.LENGTH_SHORT)
				.show();
	}

	/***
	 * 关闭软键盘
	 */
	private void closeSoftInput() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/***
	 * 下载当前月任务数量
	 */
	private void downLoadTaskCount() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + "task/GetTaskCountByDate";
				HttpUtils httpUtils = new HttpUtils();
				Demand demand = new Demand();
				demand.偏移量 = 0;
				int stepYear = year_c + jumpYear;
				int stepMonth = month_c + jumpMonth;
				demand.附加条件 = "执行人='" + mUerId + "' AND 执行时间 like '"
						+ String.format("%d-%02d", stepYear, stepMonth) + "%'";
				if (!TextUtils.isEmpty(mProjectId)) {
					demand.附加条件 += " 分类='" + mProjectId + "'";
				}
				Log.i(TAG, demand.附加条件);
				try {
					JSONObject jo = JsonUtils.initJsonObj(demand);
					String result = httpUtils.postSubmit(url, jo);
					Log.i(TAG, result);
					List<每日任务数量> list = JsonUtils.ConvertJsonToList(result,
							每日任务数量.class);
					Log.i(TAG, "数量" + list.size() + "\n" + demand.附加条件);
					Message msg = handler.obtainMessage();
					msg.obj = list;
					msg.what = SUCCEESS_GET_TASK_COUNT;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
