package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.TaskListViewAdapter;
import com.cedarhd.animation.ExpandAnimation;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.KCalendar;
import com.cedarhd.control.KCalendar.OnCalendarClickListener;
import com.cedarhd.control.KCalendar.OnCalendarDateChangedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.DictionaryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ServerDataLoader;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.ViewBean;
import com.cedarhd.models.任务;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 任务列表信息
 * 
 * @deprecated 过时，勿删，请用 TaskTabListActivity
 */
public class TaskListActivityNew extends BaseActivity {
	private final int CODE_CREATE_NEW_TASK = 1;
	public static final int REQUEST_CODE_SELECT_ID = 2;
	public static final int REQUEST_CODE_SELECT_PUBLISHER = 3; // 选择发布人
	public static final int REQUEST_CODE_SELECT_EXECUTOR = 4; // 选择执行人
	public static final int REQUEST_CODE_LOG_NEW = 10;
	public static final String TAG = "tasklist";
	public static boolean isResume; // 是否在Resume中刷新
	// // 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
	int[] stateColors = new int[] { 0xFFFFFF00, 0xFFD3D3D3, 0xFF008000,
			0xFF808080, 0xFF0000FF, 0xFFFF0000 };

	private String selectColumName;
	private SharedPreferences shPreferences;
	private LayoutInflater inflater;
	private int clientId = -1;
	private String[] arrs;
	private 任务 itemObj;
	private String value;// 查询数据库的字段值

	private TextView tv_week_calendar;// 周历
	private TextView tv_month_calendar;// 月历
	private TextView tv_filter_pop;// 选择过滤条件
	private String executorId = ""; // 选中执行人编号
	private String publisherId = ""; // 选中发布人编号
	private String assignTime = "";

	private Demand demand;
	List<任务> taskList;
	private Context context;
	private PullToRefreshListView mListView;
	private TaskListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	private BoeryunSearchView mSearchview;
	private RelativeLayout rl_search_root;
	private ListViewHelperNet<任务> mlistViewHelperNet;
	private ZLServiceHelper zlServiceHelper;
	private HandlerTaskList handlerTaskList;

	/**
	 * 过滤条件对话框中的控件
	 */
	private PopupWindow popupWindowFilter; // 过滤条件对话框
	private LinearLayout ll_choose_publisher_pop;// 选择发布人
	private LinearLayout ll_choose_excutor_pop;// 选择执行人
	private TextView tv_choose_publisher_pop;// 选择发布人
	private TextView tv_choose_excutor_pop;// 选择执行人
	private EditText et_select_status_pop; // 选择状态
	private Button btn_cancel_pop;// 取消
	private Button btn_done_pop;// 确定
	private QueryDemand queryDemand; // 查询条件
	private TextView tv_today;
	private TextView tv_thisweek;
	private TextView tv_thismonth;

	private RelativeLayout rlWeekLayout;
	private RelativeLayout rlMonthLayout;

	private DictionaryDialogHelper dictDialogHelper;

	/**
	 * 月历
	 */
	private String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式

	public class HandlerTaskList extends Handler {
		public static final int UPDATE_TASK_FAILED = 1;
		public static final int UPDATE_TASK_SUCCESS = 2;
		public static final int READ_TASK_SUCCESS = 3;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int id = msg.what;
			if (id == UPDATE_TASK_SUCCESS) {
				Toast.makeText(getApplicationContext(), "状态修改成功",
						Toast.LENGTH_SHORT).show();
				// 修改本地任务状态
				ORMDataHelper helper = ORMDataHelper
						.getInstance(getApplicationContext());
				Dao<任务, Integer> dao;
				try {
					dao = helper.getDao(任务.class);
					dao.update(itemObj);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				mListAdapter.notifyDataSetChanged();
			}
			if (id == READ_TASK_SUCCESS) { // 修改状态成功
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_new);
		value = Global.mUser.Id;
		LogUtils.i(TAG, "onStart");
		// svn修改
		findViews();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			LogUtils.i("clientId", "clientId:" + clientId);
		}
		setOnClickListener();
		init();
		if (clientId == -1) {
			reload();
		} else {
			reloadFromClient(clientId);
		}
		initPopWindow();
		initPopupWindowMonthCalender();
		// 当点击周历时，打开
		// initPopupWindowWeekCalender();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i(TAG, "onResume");
		if (isResume) {
			isResume = false;
			if (clientId == -1) {
				reload();
			} else {
				reloadFromClient(clientId);
			}
		}
	}

	// 上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		LogUtils.i(TAG, "onCreateContextMenu");
		menu.setHeaderTitle("修改任务状态");
		// mListAdapter.getItemId(position)
		// 添加菜单项
		// [1-6] 代表状态
		for (int i = 0; i < arrs.length; i++) {
			menu.add(0, i + 1, 0, arrs[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		LogUtils.i(TAG, "onCreateContextMenu");
		// 获得选中上下文菜单中的内容
		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// listview的首项为下拉刷新
		int position = acmi.position;
		LogUtils.i("keno4", " acmi.position=" + position);
		// 选中项
		itemObj = mListAdapter.getItem(position - 1);
		int id = item.getItemId();
		LogUtils.i("keno4", "-----" + id);
		this.itemObj.Status = id;
		this.itemObj.StatusName = arrs[id - 1];
		LogUtils.i("keno4", itemObj.StatusName + "-----" + id);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 修改服务器中任务状态
					zlServiceHelper.updateTask(itemObj.Id, itemObj.Status,
							handlerTaskList);
				} catch (Exception e) {
					Toast.makeText(context, "修改服务器中任务状态异常", 0).show();
				}
			}
		}).start();
		return super.onContextItemSelected(item);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CODE_CREATE_NEW_TASK) {
			if (resultCode == TaskNewActivity.RESULT_CODE_SUCCESS) {
				LogUtils.i("kjxTest", " 新建任务完毕，重新加载界面");
				reload();// 新建任务完毕，重新加载界面
			}
		}
		if (resultCode == RESULT_OK) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String mUserSelectId = bundle.getString("UserSelectId");
			String mUserSelectName = bundle.getString("UserSelectName");
			if (requestCode == REQUEST_CODE_SELECT_ID) {
				if (!TextUtils.isEmpty(mUserSelectName)) {
					String[] date = mUserSelectId.split("'");
					value = date[1];// 只能取得一个用户的id
					reload();
				}
			}
			// 选择执行人
			if (requestCode == REQUEST_CODE_SELECT_EXECUTOR) {
				tv_choose_excutor_pop.setText(mUserSelectName);
				String executor = StrUtils.deleteSign(mUserSelectId);
				queryDemand.eqDemand.put("Executor", executor);
				// itemDemand.setExecutorName(mUserSelectId); // 存选中执行人id
				executorId = executor;
			}
			if (requestCode == REQUEST_CODE_SELECT_PUBLISHER) {
				tv_choose_publisher_pop.setText(mUserSelectName);
				String publisher = StrUtils.deleteSign(mUserSelectId);
				queryDemand.eqDemand.put("Publisher", publisher);
				// itemDemand.setExecutorName(mUserSelectId); // 存选中执行人id
				publisherId = publisher;
			}
		}
	}

	void init() {
		LogUtils.i(TAG, "init:" + value);
		// TODO 网络接口功能暂不可用
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "任务";
		demand.方法名 = "Task/GetOtherList/";
		demand.条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();

		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "UpdateTime";
		Resources res = getResources();
		context = TaskListActivityNew.this;
		inflater = LayoutInflater.from(context);
		dictDialogHelper = DictionaryDialogHelper.getInstance(context);
		shPreferences = context.getSharedPreferences("listpostion",
				MODE_PRIVATE);
		zlServiceHelper = new ZLServiceHelper();
		arrs = res.getStringArray(R.array.statelist);
		handlerTaskList = new HandlerTaskList();
		taskList = new ArrayList<任务>();
		mListAdapter = new TaskListViewAdapter(TaskListActivityNew.this,
				R.layout.task_listviewlayout_new, taskList);
		mListView.setAdapter(mListAdapter);
		// mListViewHelperKjx = new ListViewHelperKjx(this, 任务.class, this,
		// demand, queryDemand, mListView, taskList, mListAdapter,
		// mProgressBar);
		mlistViewHelperNet = new ListViewHelperNet<任务>(this, 任务.class, demand,
				mListView, taskList, mListAdapter, mProgressBar, queryDemand);
		// 注册上下文菜单
		registerForContextMenu(mListView);
	}

	private void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listView_tasklist);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_tasklist);
		mSearchview = (BoeryunSearchView) findViewById(R.id.searchview_tasklist);
		rl_search_root = (RelativeLayout) findViewById(R.id.rl_search_tasklist);
		mProgressBar.setVisibility(View.GONE);
		mListView.setSelected(true);
		rlWeekLayout = (RelativeLayout) findViewById(R.id.rl_week_layout);
		rlMonthLayout = (RelativeLayout) findViewById(R.id.rl_month_layout);
	}

	/**
	 * 重新加载
	 */
	private void reload() {
		taskList.clear();
		List<String> columnName = new ArrayList<String>();// 查询数据库的字段名
		List<String> columnLikeName = new ArrayList<String>();//
		if (TextUtils.isEmpty(selectColumName)) {
			// 查询数据库的字段名(like包含关系)
			columnName.add("Publisher");
			columnName.add("Executor");
		} else {
			columnName.add(selectColumName);
		}
		columnLikeName.add("Participant");
		// mListViewHelperKjx.loadLocalData();
		// mListViewHelperKjx.loadServerData(true);
		mlistViewHelperNet.loadServerData(true);
	}

	/**
	 * 重新加载
	 */
	private void reloadFromClient(int clientId) {
		taskList.clear();
		List<String> columnName = new ArrayList<String>();// 查询数据库的字段名
		// 查询数据库的字段名(like包含关系)
		columnName.add("ClientId");

		mlistViewHelperNet.loadServerData(true);
	}

	/**
	 * 点击监听事件
	 */
	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_tasklist);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew_tasklist);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 新建任务
				Intent intent = new Intent(TaskListActivityNew.this,
						TaskNewActivity.class);
				if (clientId != -1) {
					Bundle bundle = new Bundle();
					bundle.putInt("ClientInfoActivity_clientId", clientId);
					intent.putExtras(bundle);
				}
				startActivityForResult(intent, CODE_CREATE_NEW_TASK);
			}
		});

		mSearchview.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				search(str);
			}
		});

		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				LogUtils.d("onScroll", "第一个可见项：" + firstVisibleItem);
				if (firstVisibleItem == 1) {
					rl_search_root.setVisibility(View.VISIBLE);
				} else if (firstVisibleItem > 1) {
					rl_search_root.setVisibility(View.GONE);
				}
			}
		});

		tv_week_calendar = (TextView) findViewById(R.id.tv_week_calendar);
		tv_month_calendar = (TextView) findViewById(R.id.tv_month_tasklist);
		tv_week_calendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initPopupWindowWeekCalender();
				initTab();
				rlWeekLayout.setVisibility(View.VISIBLE);
			}
		});
		tv_month_calendar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				queryDemand = new QueryDemand();
				// // 弹出popupWindowMonthCalender 月历对话框
				// popupWindowMonthCalendar
				// .showAtLocation(v, Gravity.BOTTOM, 0, 0);
				// popupWindowMonthCalendar.showAsDropDown(v);
				initTab();
				rlMonthLayout.setVisibility(View.VISIBLE);
			}
		});

		// 选择过滤条件
		tv_filter_pop = (TextView) findViewById(R.id.tv_filter_tasklist);
		tv_filter_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				queryDemand = new QueryDemand();
				initPop();
				publisherId = "";
				executorId = "";
				assignTime = "";
				et_select_status_pop.setTag(0);
				// 弹出popupWindow对话框选择过滤条件
				popupWindowFilter.showAtLocation(v, Gravity.BOTTOM, 0, 0);
				popupWindowFilter.showAsDropDown(v);
				initTab();
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						LogUtils.i("listview2", "ListView下拉刷新");
						// Refresh();
						if (!HttpUtils.IsHaveInternet(context)) {
							Toast.makeText(context, "未获取网络数据，请检查网络连接",
									Toast.LENGTH_LONG).show();
							mListView.onRefreshComplete();
						} else {
							// mListViewHelperKjx.mListViewLoadType =
							// ListViewLoadType.顶部视图;
							mlistViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
							try {
								// 下拉刷新 导入数据
								// mListViewHelperKjx.loadServerData(true);
								mlistViewHelperNet.loadServerData(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});

		// // ListView下拉刷新
		// mListView.setOnRefreshListener(new OnPulldownRefreshListener() {
		// @Override
		// public void onPulldownRefresh() {
		// LogUtils.i("listview2", "ListView下拉刷新");
		// // Refresh();
		// if (!HttpUtils.IsHaveInternet(context)) {
		// Toast.makeText(context, "未获取网络数据，请检查网络连接",
		// Toast.LENGTH_LONG).show();
		// mListView.onRefreshComplete();
		// } else {
		// mListViewHelperNew.mListViewLoadType = ListViewLoadType.顶部视图;
		// try {
		// mListViewHelperNew.loadServerData(true, value);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// });

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LogUtils.i("listview2", "ListView下拉刷新");
				// 记录第一个可见的位置
				int firstPos = mListView.getFirstVisiblePosition();
				shPreferences.edit().putInt("pos", firstPos).commit();

				ListView listView = (ListView) parent;
				itemObj = (任务) listView.getItemAtPosition(position);
				LogUtils.i("taskInfo", itemObj.Title + ":" + itemObj.Content
						+ "-->" + itemObj.Attachment);

				mListAdapter.getDataList().get(position - 1)
						.setReadTime(ViewHelper.getDateString());
				mListAdapter.notifyDataSetChanged();

				Intent intent = new Intent(TaskListActivityNew.this,
						TaskInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TAG, itemObj);
				intent.putExtras(bundle);
				// 读任务
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.ReadDynamic(itemObj.Id, 3);
						} catch (Exception e) {
							LogUtils.e(TAG, "" + e);
						}
					}
				}).start();
				startActivity(intent);
			}
		});
	}

	private void initTab() {
		rlWeekLayout.setVisibility(View.GONE);
		rlMonthLayout.setVisibility(View.GONE);
	}

	private void selectUser(int requestCode) {
		// 跳转到选择员工的Activity
		Intent intent = new Intent(TaskListActivityNew.this,
				User_SelectActivityNew_zmy.class);
		intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true);
		startActivityForResult(intent, requestCode);
	}

	private void search(String str) {
		demand.条件 = "";
		demand.附加条件 = "内容 like '%" + str + "%'";
		mlistViewHelperNet.setmDemand(demand);
		reload();
	}

	/**
	 * 初始化过滤条件对话框
	 */
	private void initPopWindow() {
		View popupView = getLayoutInflater().inflate(
				R.layout.pop_filter_tasklist, null);
		popupWindowFilter = new PopupWindow(popupView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		// 点击空白处 对话框消失
		popupWindowFilter.setTouchable(true);
		popupWindowFilter.setOutsideTouchable(true);
		popupWindowFilter.setBackgroundDrawable(new BitmapDrawable(
				getResources(), (Bitmap) null));
		ll_choose_excutor_pop = (LinearLayout) popupView
				.findViewById(R.id.ll_select_executor_pop_filter);
		ll_choose_publisher_pop = (LinearLayout) popupView
				.findViewById(R.id.ll_select_publisher_pop_filter);
		tv_choose_excutor_pop = (TextView) popupView
				.findViewById(R.id.tv_select_executor_pop_filter);
		tv_choose_publisher_pop = (TextView) popupView
				.findViewById(R.id.tv_select_publisher_pop_filter);
		et_select_status_pop = (EditText) popupView
				.findViewById(R.id.et_select_status_pop_filter);

		btn_cancel_pop = (Button) popupView
				.findViewById(R.id.btn_cancel_pop_filter_tasklist);
		btn_done_pop = (Button) popupView
				.findViewById(R.id.btn_done_pop_filter_tasklist);

		et_select_status_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictDialogHelper.showDialog(et_select_status_pop, "任务状态");
			}
		});

		btn_cancel_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindowFilter.dismiss();
			}
		});

		// 确定，执行查询条件
		btn_done_pop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindowFilter.dismiss();
				// TODO
				// 先清空本地所有数据，再查询网络数据
				zlServiceHelper.deleteTempData(context, 任务.class, "UpdateTime",
						false);
				taskList.clear();
				mListAdapter.notifyDataSetChanged();
				mProgressBar.setVisibility(View.VISIBLE);
				demand.附加条件 = "1=1";
				if (!TextUtils.isEmpty(assignTime)) {
					demand.附加条件 += " and " + assignTime;
				}
				if (!TextUtils.isEmpty(publisherId)) {
					demand.附加条件 += " and 发布人=" + publisherId;
				}
				if (!TextUtils.isEmpty(executorId)) {
					demand.附加条件 += " and 执行人=" + executorId;
				}
				if (!TextUtils.isEmpty(et_select_status_pop.getText()
						.toString())) {
					int status = (Integer) et_select_status_pop.getTag();
					demand.附加条件 += " and 任务状态 =" + status;
				}
				mlistViewHelperNet.loadServerData(true);
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

		// 选择日期
		tv_today = (TextView) popupView.findViewById(R.id.tv_today_pop_filter);
		tv_today.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initPopDateText();
				setPopSelectDateText(tv_today);
				queryDemand.likeDemand.put("AssignTime",
						ViewHelper.getDateToday());
				assignTime = " 执行时间 like '%" + ViewHelper.getDateToday() + "%'";
				LogUtils.i("assignTime", assignTime);
			}
		});
		tv_thisweek = (TextView) popupView
				.findViewById(R.id.tv_thisweek_pop_filter);
		tv_thisweek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initPopDateText();
				setPopSelectDateText(tv_thisweek);
				List<String> list = ViewHelper.getDateThisWeeks();
				queryDemand.likeListDemand.put("AssignTime", list);
				assignTime = " 执行时间 >'" + list.get(0) + "' and 执行时间<'"
						+ list.get(list.size() - 1) + "'";
				LogUtils.i("assignTime", assignTime);
			}
		});
		tv_thismonth = (TextView) popupView
				.findViewById(R.id.tv_thismonth_pop_filter);
		tv_thismonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initPopDateText();
				setPopSelectDateText(tv_thismonth);
				LogUtils.i("today", ViewHelper.getDateMonth());
				queryDemand.likeDemand.put("AssignTime",
						ViewHelper.getDateMonth());
			}
		});
	}

	private void initPop() {
		tv_choose_excutor_pop.setText("");
		tv_choose_publisher_pop.setText("");
		initPopDateText();

	}

	private void initPopDateText() {
		setPopNormalDateText(tv_today);
		setPopNormalDateText(tv_thisweek);
		setPopNormalDateText(tv_thismonth);
	}

	/**
	 * 设置过滤条件 选择日期的选中样式
	 * 
	 * @param tView
	 */
	private void setPopSelectDateText(TextView tView) {
		// 红底白字
		tView.setTextColor(0XFFFFFFFF);
		tView.setBackgroundColor(0XFFff0000);
	}

	/**
	 * 设置过滤条件 选择日期的未选中样式
	 * 
	 * @param tView
	 */
	private void setPopNormalDateText(TextView tView) {
		// 白底灰字
		tView.setTextColor(0XFF808080);
		tView.setBackgroundColor(0XFFFFFFFF);
	}

	// /////////////////////////////////////////////以下对话框为月历
	/**
	 * 初始化月历对话框
	 */
	private void initPopupWindowMonthCalender() {
		View popupView = getLayoutInflater().inflate(
				R.layout.fragment_my_calendar, null);
		rlMonthLayout.addView(popupView);
		initCalender(popupView);
	}

	/**
	 * 初始化日历
	 */
	private void initCalender(View view) {
		final TextView my_calendar_month = (TextView) view
				.findViewById(R.id.my_calendar_month);
		final KCalendar calendar = (KCalendar) view
				.findViewById(R.id.my_calendar);
		Button my_calendar_bt_enter = (Button) view
				.findViewById(R.id.my_calendar_bt_enter);
		my_calendar_month.setText(calendar.getCalendarYear() + "年"
				+ calendar.getCalendarMonth() + "月");
		if (null != date) {
			int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
			int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-")));
			my_calendar_month.setText(years + "年" + month + "月");
			calendar.showCalendar(years, month);
			calendar.setCalendarDayBgColor(date,
					R.drawable.calendar_date_focused);
		}
		List<String> list = new ArrayList<String>(); // 设置标记列表
		list.add("2014-04-01");
		list.add("2014-04-02");
		calendar.addMarks(list, 0);

		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {
			public void onCalendarClick(int row, int col, String dateFormat) {
				LogUtils.i(TAG, dateFormat);
				int month = Integer.parseInt(dateFormat.substring(
						dateFormat.indexOf("-") + 1,
						dateFormat.lastIndexOf("-")));
				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();
				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();

				} else {
					calendar.removeAllBgColor();
					calendar.setCalendarDayBgColor(dateFormat,
							R.drawable.calendar_date_focused);
					date = dateFormat;// 最后返回给全局 date
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				my_calendar_month.setText(year + "年" + month + "月");
			}
		});

		// 上月监听按钮
		RelativeLayout my_calendar_last_month = (RelativeLayout) view
				.findViewById(R.id.my_calendar_last_month);
		my_calendar_last_month.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				calendar.lastMonth();
			}

		});

		// 下月监听按钮
		RelativeLayout my_calendar_next_month = (RelativeLayout) view
				.findViewById(R.id.my_calendar_next_month);
		my_calendar_next_month.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				calendar.nextMonth();
			}
		});

		// 关闭窗口
		my_calendar_bt_enter.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// popupWindowMonthCalendar.dismiss();
				initTab();
				LogUtils.i("Mycalendar", "dismiss()" + date);
				// queryDemand.likeDemand.put("AssignTime", date);

				// 先清空本地所有数据，再查询网络数据
				zlServiceHelper.deleteTempData(context, 任务.class, "UpdateTime",
						false);
				taskList.clear();
				mListAdapter.notifyDataSetChanged();
				mProgressBar.setVisibility(View.VISIBLE);
				queryDemand.clearAll();
				queryDemand.likeDemand.put("AssignTime", date);
				demand.附加条件 = "执行时间 like '%" + date + "%'";
				// mListViewHelperKjx.setQueryDemand(queryDemand);
				// mListViewHelperKjx.loadServerData(true);

				mlistViewHelperNet.loadServerData(true);
			}
		});
	}

	// ////////////////////////////////////////以下为周历内容
	private List<ViewBean> viewList;
	private boolean animState = true; // 动画状态，同时只允许播放一个动画
	private View lastView; // 动画展开时赋值
	private Date now;

	private List<任务> list1;
	private List<任务> list2;
	private List<任务> list3;
	private List<任务> list4;
	private List<任务> list5;
	private List<任务> list6;
	private List<任务> list7;

	private RelativeLayout rl_last_week; // 上一周
	private RelativeLayout rl_next_week; // 下一周
	private TextView tv_this_week; // 查看本周
	private RelativeLayout rl_pbar_week_calendar;
	private Button btn_calendar_week;

	private RelativeLayout rl_monday_title;
	private LinearLayout ll_monday_layout;
	private RelativeLayout rl_thuesday_title;
	private LinearLayout ll_thuesday_layout;
	private RelativeLayout rl_wednesday_title;
	private LinearLayout ll_wednesday_layout;
	private RelativeLayout rl_thursday_title;
	private LinearLayout ll_thursday_layout;
	private RelativeLayout rl_friday_title;
	private LinearLayout ll_friday_layout;
	private RelativeLayout rl_saturday_title;
	private LinearLayout ll_saturday_layout;
	private RelativeLayout rl_sunday_title;
	private LinearLayout ll_sunday_layout;

	private TextView tv_monday_count;
	private TextView tv_thuesday_count;
	private TextView tv_wednesday_count;
	private TextView tv_thursday_count;
	private TextView tv_friday_count;
	private TextView tv_saturday_count;
	private TextView tv_sunday_count;

	/**
	 * 初始化周历对话框
	 */
	private void initPopupWindowWeekCalender() {
		View popupView = getLayoutInflater().inflate(R.layout.week_calendar,
				null);
		rlWeekLayout.addView(popupView);
		viewList = new ArrayList<ViewBean>();
		now = new Date();
		initWeekCalender(popupView);
		initWeekViews(popupView);
	}

	/**
	 * 初始化周历页面
	 * 
	 * @param view
	 */
	private void initWeekCalender(View view) {
		rl_last_week = (RelativeLayout) view
				.findViewById(R.id.rl_calendar_last_week);
		rl_next_week = (RelativeLayout) view
				.findViewById(R.id.rl_calendar_next_week);
		tv_this_week = (TextView) view.findViewById(R.id.tv_this_week);
		rl_pbar_week_calendar = (RelativeLayout) view
				.findViewById(R.id.rl_pbar_week_calendar);

		// 跳转到上一周
		rl_last_week.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				now = ViewHelper.getBeforWeekDate(now);
				LogUtils.i("week", ViewHelper.getDateString(now));
				initDate();
			}
		});

		// 跳转到下一周
		rl_next_week.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				now = ViewHelper.getAfterWeekDate(now);
				LogUtils.i("week", ViewHelper.getDateString(now));
				initDate();
			}
		});

		// 查看本周
		tv_this_week.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				now = new Date();
				initDate();
			}
		});

		btn_calendar_week = (Button) view.findViewById(R.id.btn_calendar_week);
		btn_calendar_week.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initTab();
			}
		});
	}

	private void initWeekViews(View view) {
		tv_monday_count = (TextView) view.findViewById(R.id.tv_monday_count);
		tv_wednesday_count = (TextView) view
				.findViewById(R.id.tv_wednesday_count);
		tv_thuesday_count = (TextView) view
				.findViewById(R.id.tv_thuesday_count);
		tv_thursday_count = (TextView) view
				.findViewById(R.id.tv_thursday_count);
		tv_friday_count = (TextView) view.findViewById(R.id.tv_friday_count);
		tv_saturday_count = (TextView) view
				.findViewById(R.id.tv_saturday_count);
		tv_sunday_count = (TextView) view.findViewById(R.id.tv_sunday_count);

		ViewBean viewBean = new ViewBean();
		// 1.将所有的viewTitle和viewLayout封装成ViewBean，并且用list保存
		rl_monday_title = (RelativeLayout) view
				.findViewById(R.id.rl_monday_title);
		ll_monday_layout = (LinearLayout) view
				.findViewById(R.id.ll_monday_layout);

		viewBean.setViewTitle(rl_monday_title);
		viewBean.setViewLayout(ll_monday_layout);
		viewList.add(viewBean);

		rl_thuesday_title = (RelativeLayout) view
				.findViewById(R.id.rl_thuesday_title);
		ll_thuesday_layout = (LinearLayout) view
				.findViewById(R.id.ll_thuesday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_thuesday_title);
		viewBean.setViewLayout(ll_thuesday_layout);
		viewList.add(viewBean);

		rl_wednesday_title = (RelativeLayout) view
				.findViewById(R.id.rl_wednesday_title);
		ll_wednesday_layout = (LinearLayout) view
				.findViewById(R.id.ll_wednesday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_wednesday_title);
		viewBean.setViewLayout(ll_wednesday_layout);
		viewList.add(viewBean);

		/**
		 * 星期四
		 */
		rl_thursday_title = (RelativeLayout) view
				.findViewById(R.id.rl_thursday_title);
		ll_thursday_layout = (LinearLayout) view
				.findViewById(R.id.ll_thursday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_thursday_title);
		viewBean.setViewLayout(ll_thursday_layout);
		viewList.add(viewBean);

		/**
		 * 星期五
		 */
		rl_friday_title = (RelativeLayout) view
				.findViewById(R.id.rl_friday_title);
		ll_friday_layout = (LinearLayout) view
				.findViewById(R.id.ll_friday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_friday_title);
		viewBean.setViewLayout(ll_friday_layout);
		viewList.add(viewBean);

		/**
		 * 星期六
		 */
		rl_saturday_title = (RelativeLayout) view
				.findViewById(R.id.rl_saturday_title);
		ll_saturday_layout = (LinearLayout) view
				.findViewById(R.id.ll_saturday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_saturday_title);
		viewBean.setViewLayout(ll_saturday_layout);
		viewList.add(viewBean);

		/**
		 * 星期天
		 */
		rl_sunday_title = (RelativeLayout) view
				.findViewById(R.id.rl_sunday_title);
		ll_sunday_layout = (LinearLayout) view
				.findViewById(R.id.ll_sunday_layout);
		viewBean = new ViewBean();
		viewBean.setViewTitle(rl_sunday_title);
		viewBean.setViewLayout(ll_sunday_layout);
		viewList.add(viewBean);
		initDate();
		setAnimation(viewList);
	}

	/**
	 * 初始化周历日期
	 */
	private void initDate() {
		ll_monday_layout.removeAllViews();
		ll_wednesday_layout.removeAllViews();
		ll_thuesday_layout.removeAllViews();
		ll_thursday_layout.removeAllViews();
		ll_friday_layout.removeAllViews();
		ll_saturday_layout.removeAllViews();
		ll_sunday_layout.removeAllViews();
		List<String> weekList = ViewHelper.getWeeks(now);
		for (int i = 0; i < weekList.size(); i++) {
			LogUtils.i("QueryWeekTasks", weekList.get(i));
		}
		LogUtils.i("QueryWeekTask", "-----" + ViewHelper.getDateString(now));
		String filter = " 执行时间 >'" + weekList.get(0) + "' and 执行时间<'"
				+ weekList.get(weekList.size() - 1) + "'";
		LogUtils.i("QueryWeekTask", "-----" + weekList.size());
		LogUtils.i("QueryWeekTask", filter);
		rl_pbar_week_calendar.setVisibility(View.VISIBLE);
		new QueryWeekTask().execute(filter);
	}

	/**
	 * 设置动画效果
	 * 
	 * @param viewList
	 */
	private void setAnimation(List<ViewBean> viewList) {
		// 迭代list，对每一个viewTitle对应的viewLayout设置动画
		for (ViewBean bean : viewList) {
			final View viewTitle = bean.getViewTitle();
			final View viewLayout = bean.getViewLayout();
			viewTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					executeAnimation(viewTitle, viewLayout);
				}
			});
		}
	}

	private void executeAnimation(View viewTitle, View viewLayout) {
		if (animState) {
			ExpandAnimation animation = new ExpandAnimation(viewLayout, 300);
			// 获得当前动画开关的状态
			boolean toggle = animation.toggle();
			if (toggle) {
				if (lastView == null) {
					// 说明之前没有打开过动画 或者 已经打开的动画都关闭了
					lastView = viewLayout;
				} else {
					// 说明之前有一个打开的动画
					if (lastView == viewLayout) {
						lastView = null;// 说明点击的是上一个打开的layout的title
					} else {// 点击的是其他的title
						executeAnimation(viewTitle, lastView); // 关闭上一个打开的title
						lastView = viewLayout;// 记住当前的viewLayout为lastView
					}
				}
			} else {
				for (ViewBean bean : viewList) {
					if (viewLayout == bean.getViewLayout()) {
						if (lastView == bean.getViewLayout()) {
							lastView = null;
						}
					}
				}
			}
			viewLayout.startAnimation(animation);
			animation.setAnimationListener(animListener);
		}
	}

	/**
	 * 动画监听
	 */
	private Animation.AnimationListener animListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			animState = false;// 动画未结束前不允许下次播放
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			animState = true;// 当前动画播放结束后再允许播放下次动画
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	};

	/**
	 * 设置星期对应的日期
	 * 
	 * @param rLayout
	 * @param llayout
	 * @param tvCount
	 * @param dayOfWeek
	 * @param list
	 */
	private void setWeekDate(RelativeLayout rLayout, LinearLayout llayout,
			TextView tvCount, int dayOfWeek, List<任务> result) {
		LinearLayout ll = (LinearLayout) rLayout.getChildAt(0);
		TextView tvDate = (TextView) ll.getChildAt(1); // 日期
		String date = getWeekDate(dayOfWeek);
		// now =
		// list = zlServiceHelper.getTaskList(context, date)
		List<任务> list = new ArrayList<任务>();
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).AssignTime.contains(date)) {
				list.add(result.get(i));
			}
		}

		tvDate.setText(date);
		tvCount.setText(list.size() + "");
		llayout.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			final 任务 item = list.get(i);
			View view = inflater.inflate(R.layout.item_task_week, null);
			TextView tvState = (TextView) view.findViewById(R.id.tv_state_item);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_item);
			tvTitle.setText(item.getContent());
			// 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
			if (item.Status >= 1 && item.Status <= 6) {
				tvState.setText(arrs[item.Status - 1]);
				tvState.setBackgroundColor(stateColors[item.Status - 1]);
			} else {
				tvState.setBackgroundColor(0x0000000); // 状态异常透明
			}
			tvTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(TaskListActivityNew.this,
							TaskInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(TAG, item);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			llayout.addView(view);
		}
	}

	/**
	 * 根据星期获得 日期
	 * 
	 * @param dayOfWeek
	 *            星期
	 * @return
	 */
	private String getWeekDate(int dayOfWeek) {
		Date date = new Date(now.getYear(), now.getMonth(), now.getDate());
		int week = date.getDay(); // 获得当前星期几
		int beforDay = week - dayOfWeek; // 今天和输入星期的差值
		int length = Math.abs(beforDay);
		for (int i = 0; i < length; i++) {
			if (beforDay < 0) { // 今天之后的
				date = ViewHelper.getTomorrow(date);
			} else if (beforDay > 0) { // 今天之前的
				date = ViewHelper.getYestody(date);
			}
		}
		return ViewHelper.getDateString(date);
	}

	private class QueryWeekTask extends AsyncTask<String, Void, List<任务>> {
		@Override
		protected List<任务> doInBackground(String... params) {
			demand.附加条件 = params[0];
			List<任务> result = ServerDataLoader.getServerTaskData(demand, "");
			LogUtils.i("QueryWeekTask", "----" + result.size());
			return result;
		}

		@Override
		protected void onPostExecute(List<任务> result) {
			// TODO
			super.onPostExecute(result);
			rl_pbar_week_calendar.setVisibility(View.GONE);
			setWeekDate(rl_monday_title, ll_monday_layout, tv_monday_count, 1,
					result);
			setWeekDate(rl_thuesday_title, ll_thuesday_layout,
					tv_thuesday_count, 2, result);
			setWeekDate(rl_wednesday_title, ll_wednesday_layout,
					tv_wednesday_count, 3, result);
			setWeekDate(rl_thursday_title, ll_thursday_layout,
					tv_thursday_count, 4, result);
			setWeekDate(rl_friday_title, ll_friday_layout, tv_friday_count, 5,
					result);
			setWeekDate(rl_saturday_title, ll_saturday_layout,
					tv_saturday_count, 6, result);
			setWeekDate(rl_sunday_title, ll_sunday_layout, tv_sunday_count, 7,
					result);
		}

	}

}
