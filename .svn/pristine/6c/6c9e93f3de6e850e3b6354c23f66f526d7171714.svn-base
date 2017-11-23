package com.cedarhd;

/**
 * 任务列表界面用来显示taskmore前面传递过来的条件相对应的数据
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.TaskListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.任务;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskListActivity_zmy extends BaseActivity {
	public static final int REQUEST_CODE_SELECT_ID = 2;
	public static final int REQUEST_CODE_SELECT_PUBLISHER = 3; // 选择发布人
	public static final int REQUEST_CODE_SELECT_EXECUTOR = 4; // 选择执行人
	public static final int REQUEST_CODE_LOG_NEW = 10;

	/**
	 * 请求任务详情
	 */
	public static final int REQUEST_CODE_TASK_INFO = 101;

	public static final String TAG = "TaskListFragment";
	public static boolean isResume; // 是否在Resume中刷新
	// // 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
	int[] stateColors = new int[] { 0xFFFFFF00, 0xFFD3D3D3, 0xFF008000,
			0xFF808080, 0xFF0000FF, 0xFFFF0000 };
	private SharedPreferences shPreferences;
	private 任务 itemObj;
	private String value;// 查询数据库的字段值

	private Context context;
	private Demand demand;
	// 查询条件
	private String moreFilter;
	private QueryDemand queryDemand; // 查询条件
	List<任务> taskList;
	private PullToRefreshListView mListView;
	private TaskListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	private BoeryunSearchView mSearchview;
	private RelativeLayout rl_search_root;
	// private ListViewHelperKjx mListViewHelperKjx = null;
	private ListViewHelperNet<任务> mlistViewHelperNet;
	private ZLServiceHelper zlServiceHelper;

	private ImageButton back;
	private TextView title_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.tasklist_zmy, null);
		setContentView(view);
		Intent bundle = getIntent();
		if (bundle != null) {
			// 获取查询activity传递过来的条件
			moreFilter = getIntent().getStringExtra("");
		}
		initUI(view);
		getBundle();
		setOnClickEvent();
		initData(moreFilter);
		reload();
	}

	/**
	 * 获取传递过来的状态并依照状态加载数据
	 */
	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		String istime = bundle.getString("istime");
		String isok = bundle.getString("isok");
		String isMy = bundle.getString("isMy");
		moreFilter = "";
		if (("本周").equals(istime)) {
			LogUtils.i("out", isMy + "123");
			if (("全部").equals(isMy)) {
				LogUtils.i("out", isMy + "1234");
				if (("完成").equals(isok)) {
					LogUtils.i("out", isMy + "12345" + "");
					title_info.setText("本周完成任务");
					moreFilter = initDate() + " and 任务状态 in (3)";
				} else if (("未完成").equals(isok)) {
					title_info.setText("本周未完成任务");
					moreFilter = initDate() + " and 任务状态 not in (3)";
				}
			} else if (("我的").equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("本周完成任务");
					moreFilter = initDate() + " and 任务状态 in (3) and 执行人="
							+ Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("本周未完成任务");
					moreFilter = initDate() + " and 任务状态 not in (3) and 执行人="
							+ Global.mUser.Id;
				}
			} else if ((("我下达的")).equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("本周完成任务");
					moreFilter = initDate() + " and 任务状态 in (3) and 发布人="
							+ Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("本周未完成任务");
					moreFilter = initDate() + " and 任务状态 not in (3) and 发布人="
							+ Global.mUser.Id;
				}
			}
		} else if (("本月").equals(istime)) {
			if (("全部").equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("本月完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态 in (3)";
				} else if (("未完成").equals(isok)) {
					title_info.setText("本月未完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态  not in (3)";
				}
			} else if (("我的").equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("本月完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态 in (3) and 执行人=" + Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("本月未完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态  not in (3) and 执行人="
							+ Global.mUser.Id;
				}
			} else if ((("我下达的")).equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("本月完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态 in (3) and 发布人=" + Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("本月未完成任务");
					moreFilter = " 执行时间 >='" + getMounthDay()[0]
							+ "' and 执行时间<='" + getMounthDay()[1]
							+ "' and 任务状态  not in (3) and 发布人="
							+ Global.mUser.Id;
				}
			}
		} else if (("该时间").equals(istime)) {
			String starttime = bundle.getString("starttime");
			String endtime = bundle.getString("endtime");
			if (("全部").equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("该时间完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态 in (3)";
				} else if (("未完成").equals(isok)) {
					title_info.setText("该时间未完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态  not in (3)";
				}
			} else if (("我的").equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("该时间完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态 in (3) and 执行人="
							+ Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("该时间未完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态  not in (3) and 执行人="
							+ Global.mUser.Id;
				}
			} else if ((("我下达的")).equals(isMy)) {
				if (("完成").equals(isok)) {
					title_info.setText("该时间完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态 in (3) and 发布人="
							+ Global.mUser.Id;
				} else if (("未完成").equals(isok)) {
					title_info.setText("该时间未完成任务");
					moreFilter = " 执行时间 >'" + starttime + "' and 执行时间<'"
							+ endtime + "' and 任务状态  not in (3) and 发布人="
							+ Global.mUser.Id;
				}
			}
		} else {
			title_info.setText("所有延期任务");
			moreFilter = "执行时间 <'" + ViewHelper.getDateToday()
					+ "' and 任务状态  in (1,2,4,6)";
		}
	}

	/**
	 * 初始化周历日期
	 */
	private String initDate() {
		Date now = new Date();
		List<String> weekList = ViewHelper.getWeeks(now);
		for (int i = 0; i < weekList.size(); i++) {
			LogUtils.i("QueryWeekTasks", weekList.get(i));
		}
		LogUtils.i("QueryWeekTask", "-----" + ViewHelper.getDateString(now));
		String filter = " 执行时间 >='" + weekList.get(0) + "' and 执行时间<='"
				+ weekList.get(weekList.size() - 1) + "'";
		LogUtils.i("QueryWeekTask", "-----" + weekList.size());
		LogUtils.i("QueryWeekTask", filter);
		return filter;
	}

	/**
	 * 获取当前月的第一天和最后一天
	 */
	private String[] getMounthDay() {
		String strdate[] = new String[2];
		Calendar cale = Calendar.getInstance();
		// 获取当月第一天和最后一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String firstday, lastday;
		// 获取前月的第一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		firstday = format.format(cale.getTime());
		// 获取前月的最后一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		lastday = format.format(cale.getTime());
		LogUtils.i("out", "本月第一天和最后一天分别是 ： " + firstday + " and " + lastday);
		strdate[0] = firstday;
		strdate[1] = lastday;
		return strdate;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void initUI(View view) {
		mListView = (PullToRefreshListView) view
				.findViewById(R.id.listView_tasklist_fragment1);
		mProgressBar = (MyProgressBar) view
				.findViewById(R.id.progress_tasklist_fragment1);
		mSearchview = (BoeryunSearchView) view
				.findViewById(R.id.searchview_tasklist_fragment1);
		rl_search_root = (RelativeLayout) view
				.findViewById(R.id.rl_search_tasklist_fragment1);
		back = (ImageButton) view.findViewById(R.id.tasklist_zmy_back);
		title_info = (TextView) view.findViewById(R.id.tasklist_zmy_title);
		mListView.setSelected(true);
	}

	private void initData(String moreFilter) {
		LogUtils.i(TAG, "init:" + value);
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "任务";
		demand.方法名 = "Task/GetOtherList/";
		demand.条件 = "";
		demand.附加条件 = moreFilter;
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "UpdateTime";
		context = TaskListActivity_zmy.this;
		shPreferences = context.getSharedPreferences("listpostion",
				Context.MODE_PRIVATE);
		zlServiceHelper = new ZLServiceHelper();
		taskList = new ArrayList<任务>();
		mListAdapter = new TaskListViewAdapter(context,
				R.layout.task_listviewlayout_new, taskList);
		mListView.setAdapter(mListAdapter);
		mlistViewHelperNet = new ListViewHelperNet<任务>(context, 任务.class,
				demand, mListView, taskList, mListAdapter, mProgressBar,
				queryDemand);
	}

	/**
	 * 点击监听事件
	 */
	private void setOnClickEvent() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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
				LogUtils.i("onScroll", "第一个可见项：" + firstVisibleItem);
				if (firstVisibleItem == 1) {
					rl_search_root.setVisibility(View.VISIBLE);
				} else if (firstVisibleItem > 1) {
					rl_search_root.setVisibility(View.GONE);
				}
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						LogUtils.i("listview2", "ListView下拉刷新");
						taskList.clear();
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
				Intent intent = new Intent(context, TaskInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TaskInfoActivity.TAG, itemObj);
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
				startActivityForResult(intent, REQUEST_CODE_TASK_INFO);
				// startActivity(intent);
			}
		});
	}

	private void search(String str) {
		demand.条件 = "";
		if (!TextUtils.isEmpty(moreFilter)) {
			demand.附加条件 = moreFilter + " and 内容 like '%" + str + "%'";
		} else {
			demand.附加条件 = "内容 like '%" + str + "%'";
		}
		mlistViewHelperNet.setmDemand(demand);
		reload();
	}

	/**
	 * 重新加载
	 */
	private void reload() {
		taskList.clear();
		mListAdapter.notifyDataSetChanged();
		mlistViewHelperNet.loadServerData(true);
	}

	/**
	 * 重新加载
	 */
	public void reload(String filter) {
		moreFilter = filter;
		demand.附加条件 = moreFilter;
		mlistViewHelperNet.setmDemand(demand);
		taskList.clear();
		mListAdapter.notifyDataSetChanged();
		mlistViewHelperNet.loadServerData(true);
	}
}
