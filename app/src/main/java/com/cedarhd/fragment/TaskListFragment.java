package com.cedarhd.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.TaskInfoActivity;
import com.cedarhd.TaskTabListActivity;
import com.cedarhd.adapter.TaskListViewAdapter;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ShakeListenerUtils;
import com.cedarhd.helpers.ShakeListenerUtils.OnShakeListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.任务;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/***
 * 任务列表
 * 
 * @author KJX
 * 
 *         2015-04-08
 */
public class TaskListFragment extends Fragment {

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
	private boolean isUnRead;
	private QueryDemand queryDemand; // 查询条件
	List<任务> taskList;
	private PullToRefreshListView mListView;
	private TaskListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	private BoeryunSearchView mSearchview;
	private RelativeLayout rl_search_root;
	private ImageView ivCalenderMode;

	// private ListViewHelperKjx mListViewHelperKjx = null;
	private ListViewHelperNet<任务> mlistViewHelperNet;
	private ZLServiceHelper zlServiceHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			// 获取查询activity传递过来的条件
			moreFilter = getArguments().getString(TaskTabListActivity.TAG);
			isUnRead = getArguments().getBoolean(
					TaskTabListActivity.IS_UNREAD_TASK, false);
		}
	}

	/** 标记是不是当前fragment可见 */
	public boolean isVisible;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tasklist, null);
		initUI(view);
		setOnClickEvent();
		createDialog();
		initData();
		isVisible = true;
		reload();
		return view;
	}

	/** 摇一摇监听类 */
	ShakeListenerUtils mShakeListener;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			// 摇一摇
			if (isVisible) {
				mShakeListener = new ShakeListenerUtils(
						TaskTabListActivity.activity);
				mShakeListener.setOnShakeListener(new shakeLitener());
			}
		} else {
			// if(isflag){
			mShakeListener = new ShakeListenerUtils(
					TaskTabListActivity.activity);
			isVisible = false;
			mShakeListener.stop();
			// }

		}
	}

	AlertDialog dialog;
	private String statusStr;
	HttpUtils httpUtils;
	public static final int UPDATE_READ = 1201;
	String path = Global.BASE_URL + Global.EXTENSION + "ReadStatus/SetAllRead/"
			+ 3;

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示");
		builder.setMessage("是否将数据设置为已读");
		httpUtils = new HttpUtils();
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				new Thread(new Runnable() {
					@Override
					public void run() {
						statusStr = httpUtils.httpGet(path);
						LogUtils.i("out", statusStr + path);
						JSONObject object;
						try {
							object = new JSONObject(statusStr);
							if (object.getInt("Status") == 1) {
								handler.sendEmptyMessage(UPDATE_READ);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
	}

	private class shakeLitener implements OnShakeListener {

		public void onShake() {
			if (!dialog.isShowing()) {
				dialog.show();
				mShakeListener.stop();
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_READ:
				reload();
				Toast.makeText(TaskTabListActivity.activity, "任务已经设置为已读！",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 返回resultCode=ok，刷新页面
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_TASK_INFO:
				reload();
				break;

			default:
				break;
			}
		}

	}

	private void initUI(View view) {
		mListView = (PullToRefreshListView) view
				.findViewById(R.id.listView_tasklist_fragment);
		mProgressBar = (MyProgressBar) view
				.findViewById(R.id.progress_tasklist_fragment);
		mSearchview = (BoeryunSearchView) view
				.findViewById(R.id.searchview_tasklist_fragment);
		rl_search_root = (RelativeLayout) view
				.findViewById(R.id.rl_search_tasklist_fragment);
		ivCalenderMode = (ImageView) view
				.findViewById(R.id.iv_calendar_mode_tasklist_fragment);
		mListView.setSelected(true);
	}

	private void initData() {
		LogUtils.i(TAG, "init:" + value);
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "任务";
		demand.方法名 = isUnRead ? "Task/GetOtherUnreadList/"
				: "Task/GetOtherList/";
		demand.条件 = "";
		demand.附加条件 = moreFilter;
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "UpdateTime";
		context = getActivity();
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

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						refresh();
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
				startActivityForResult(intent, REQUEST_CODE_TASK_INFO);
				// startActivity(intent);
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
			}
		});

		ivCalenderMode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到日历模块
				// startActivity(new Intent(getActivity(),
				// TaskCalenderActivity.class));
				// getActivity().finish();
				if (mChangeCalenderMode != null) {
					mChangeCalenderMode.onChanged();
				}
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

	private IChangeCalenderMode mChangeCalenderMode;

	/***
	 * 监听切换日历模式
	 * 
	 * @param iCalenderMode
	 */
	public void changeCalenderMode(IChangeCalenderMode iCalenderMode) {
		this.mChangeCalenderMode = iCalenderMode;
	}

	/** 下拉刷新 */
	public void refresh() {
		LogUtils.i("listview2", "ListView下拉刷新");
		if (!HttpUtils.IsHaveInternet(context)) {
			Toast.makeText(context, "未获取网络数据，请检查网络连接", Toast.LENGTH_LONG)
					.show();
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

	public interface IChangeCalenderMode {
		void onChanged();
	}
}
