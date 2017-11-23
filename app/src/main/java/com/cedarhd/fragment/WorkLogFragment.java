package com.cedarhd.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.User_SelectActivityNew_zmy;
import com.cedarhd.WorkLogActivity;
import com.cedarhd.adapter.LogListViewAdapter;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ShakeListenerUtils;
import com.cedarhd.helpers.ShakeListenerUtils.OnShakeListener;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.日志;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工作日志列表
 * 
 * @author KJX
 * 
 */
public class WorkLogFragment extends Fragment {

	public static final String TAG = "WorkLogFragment";
	private static final int SHOW_DATAPICKFrom = 0;
	private static final int SHOW_DATAPICKTo = 1;
	public static final int REQUEST_CODE_SELECT_ID = 2;

	/*** 日志评论成功 */
	public static final int RESULT_COMMENT_SUCCESS = 4;

	/*** 日志修改成功 */
	public static final int RESULT_UPDATE_SUCCESS = 3;

	/*** 请求修改日志 */
	public static final int REQUEST_CODE_UPDATE_WORKLOG = 101;

	/** 记录选中item的数据源集合中编号index */
	private int selectedPos;
	public static boolean isResume; // 是否在Resume中刷新
	public static boolean isConnectedInternet; // 是否连接了网络
	private Demand demand;
	private QueryDemand queryDemand; // 本地数据查询条件
	// 数据库的列名(包含关系,like)
	private String value = "";// 查询数据库的字段值
	private Context context;
	private TextView tv_select_me_workloglist;
	PullToRefreshListView mListView;
	LogListViewAdapter listViewAdapter;
	private MyProgressBar mProgressBar;
	private BoeryunSearchView mSearchview;
	private RelativeLayout rl_search_root;
	java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	List<日志> m日志list;
	HandlerLog handler;
	// private ListViewHelperNew mListViewHelperNew = null;
	// private ListViewHelperKjx mListViewHelperKjx = null;
	private ListViewHelperNet<日志> mlistViewHelperNet;

	// private TextView tv_choose;
	private RelativeLayout rl_choose; // 选择员工
	private RelativeLayout rl_choose_me;
	public static final int REQUEST_CODE_LOG_NEW = 10;

	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	DictionaryHelper dictionaryHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		View view = inflater.inflate(R.layout.workloglist_fragment, null);
		findViews(view);
		setOnClickListener(view);
		init();
		createDialog();
		return view;
	}

	/** 摇一摇监听类 */
	ShakeListenerUtils mShakeListener = null;

	@Override
	public void onResume() {
		super.onResume();
		// 摇一摇
		mShakeListener = new ShakeListenerUtils(getActivity());
		mShakeListener.setOnShakeListener(new shakeLitener());

	}

	AlertDialog dialog;
	private String statusStr;
	HttpUtils httpUtils;
	String path = Global.BASE_URL + Global.EXTENSION + "ReadStatus/SetAllRead/"
			+ 1;

	// String path=Global.BASE_URL + Global.EXTENSION+
	// "ReadStatus/SetAllRead/"+2;
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
						System.out.println(statusStr + path);
						JSONObject object;
						try {
							object = new JSONObject(statusStr);
							if (object.getInt("Status") == 1) {
								handler.sendEmptyMessage(UPDATE_READ);
								// fetchRemind();
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
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mShakeListener.stop();
	}

	public void findViews(View view) {
		handler = new HandlerLog();
		dictionaryHelper = new DictionaryHelper(context);
		mListView = (PullToRefreshListView) view.findViewById(R.id.listView1);
		mProgressBar = (MyProgressBar) view
				.findViewById(R.id.progress_worklist);
		mSearchview = (BoeryunSearchView) view
				.findViewById(R.id.searchview_loglist);
		rl_search_root = (RelativeLayout) view
				.findViewById(R.id.rl_search_root_loglist);
	}

	private void init() {
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "日志";
		// demand.方法名 = "Log/GetWorkLogList/";
		demand.方法名 = "Log/Get员工日志_v1_30/";
		demand.条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;

		queryDemand = new QueryDemand();
		queryDemand.fildName = "时间";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "Time";
		m日志list = new ArrayList<日志>();
		listViewAdapter = new LogListViewAdapter(context,
				R.layout.loglist_listviewlayout, m日志list, null);
		mListView.setAdapter(listViewAdapter);

		mlistViewHelperNet = new ListViewHelperNet<日志>(getActivity(), 日志.class,
				demand, mListView, m日志list, listViewAdapter, mProgressBar,
				queryDemand);

		reload();
	}

	private void reload() {
		isConnectedInternet = HttpUtils.IsHaveInternet(context);
		if (!isConnectedInternet) {
			Toast.makeText(context, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
		} else {
			m日志list.clear();
			listViewAdapter.notifyDataSetChanged();
			mlistViewHelperNet.loadServerData(true);
		}
	}

	private void setOnClickListener(View view) {
		rl_choose = (RelativeLayout) view
				.findViewById(R.id.rl_choose_workloglist);
		rl_choose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到选择员工的Activity
				Intent intent = new Intent(context,
						User_SelectActivityNew_zmy.class);
				intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE,
						true);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ID);
			}
		});

		// 查看我的日志
		rl_choose_me = (RelativeLayout) view
				.findViewById(R.id.rl_choose_me_workloglist);
		tv_select_me_workloglist = (TextView) view
				.findViewById(R.id.tv_select_me_workloglist);

		mSearchview.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				search(str);
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
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final 日志 item = (日志) listView.getItemAtPosition(position);

				selectedPos = position - 1;
				LogUtils.e("pos", "selectedPos=" + selectedPos);
				Intent intent = new Intent(context, WorkLogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Log", item);
				LogUtils.e("keno2", "id:" + item.Id);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_UPDATE_WORKLOG);
				readLog(position, item);
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
	}

	/** 获得ListView第一个ITEM和当前可见第一个ITEM的距离 */
	public int getScrollY() {
		View firstItem = mListView.getChildAt(0);
		if (firstItem == null) {
			return 0;
		}
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int top = firstItem.getTop();
		return firstVisiblePosition * firstItem.getHeight() - top;
	}

	public void setTitle() {
		String titleName = dictionaryHelper.getUserNameById(value);
		if (!TextUtils.isEmpty(titleName)) {
			tv_select_me_workloglist.setText(titleName + "的日志");
		}
	}

	public static final int UPDATE_READ = 1201;

	private class HandlerLog extends Handler {
		public static final int GET_LOG_NOW_SUCCESS = 0;
		public static final int GET_LOG_NOW_FAILED = 1;
		public static final int UPDATE_LOG_SUCCESS = 3; // 修改日志成功
		public static final int UPDATE_LOG_FAILED = 4;

		@Override
		public void handleMessage(Message msg) {
			int whatMsg = msg.what;
			switch (whatMsg) {
			case GET_LOG_NOW_SUCCESS:
				Date curDate = new Date(System.currentTimeMillis());
				String strTime = format.format(curDate);
				日志 log = new 日志();
				List<日志> list = (List<日志>) msg.obj;
				if (list.size() > 0) {
					日志 e = list.get(0);
					log.setId(e.Id);
					log.setTime(e.Time);
					log.setContent(e.Content);
					if (e.Id == 0) {
						log.setPersonnel(Integer.parseInt(Global.mUser.Id));
						log.setPersonnelName(Global.mUser.UserName);
					} else {
						log.setPersonnel(e.Personnel);
						log.setPersonnelName(e.PersonnelName);
					}

					log.setClient(e.Client);
					log.setSuppliers(e.Suppliers);
					log.setProject(e.Project);
					log.setClientRecord(e.ClientRecord);
				} else {
					log.setId(0);
					// log.setTime(curDate);
					log.setTime(ViewHelper.getDateString());
					log.setPersonnel(Integer.parseInt(Global.mUser.Id));
				}

				Intent intent = new Intent(context, WorkLogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Log", log);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case GET_LOG_NOW_FAILED:
				break;
			case UPDATE_LOG_SUCCESS: // 修改日志成功
				listViewAdapter.notifyDataSetChanged();
				break;
			case UPDATE_READ:
				init();
				Toast.makeText(getActivity(), "所有日志已经设置为已读", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SELECT_ID) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String mUserSelectId = bundle.getString("UserSelectId");
			String mUserSelectName = bundle.getString("UserSelectName");
			LogUtils.i("selectUser", mUserSelectId + "--" + mUserSelectName);
			if (!TextUtils.isEmpty(mUserSelectName)) {
				// String[] date = mUserSelectId.split("'");
				// value = date[1];// 只能取得一个用户的id
				// 取到编号 是 "85"这样的数字
				value = mUserSelectId.replace("'", "");
				setTitle();
				queryDemand.eqDemand.put("Personnel", value);
				demand.用户编号 = value;
				m日志list.clear();
				listViewAdapter.notifyDataSetChanged();
				// mListViewHelperKjx.loadLocalData();
				mlistViewHelperNet.loadServerData(false);
				// mListViewHelperKjx.reI();
			}
		} else if (requestCode == REQUEST_CODE_UPDATE_WORKLOG) {
			日志 itemLog = null;
			if (data != null) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					itemLog = (日志) bundle.getSerializable(TAG);
				}
			}

			if (itemLog != null) {
				if (resultCode == RESULT_UPDATE_SUCCESS) {
					LogUtils.i("REQUEST_CODE_UPDATE_WORKLOG",
							"RESULT_UPDATE_SUCCESS");
					listViewAdapter.getDataList().get(selectedPos)
							.setContent(itemLog.Content);
					listViewAdapter.getDataList().remove(selectedPos);
					listViewAdapter.getDataList().add(selectedPos, itemLog);
					listViewAdapter.notifyDataSetChanged();
				} else if (resultCode == RESULT_COMMENT_SUCCESS) {
					LogUtils.i("REQUEST_CODE_UPDATE_WORKLOG",
							"RESULT_COMMENT_SUCCESS");
					listViewAdapter.getDataList().get(selectedPos)
							.setDiscussCount(itemLog.DiscussCount);
					listViewAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	/**
	 * 设置日志为已读
	 * 
	 * @param position
	 *            ListView点中项的编号
	 * @param item
	 */
	private void readLog(int position, final 日志 item) {
		if (!TextUtils.isEmpty(item.ReadTime)) {
			return;
		}
		listViewAdapter.getDataList().get(position - 1).ReadTime = ViewHelper
				.getDateString();
		listViewAdapter.notifyDataSetChanged();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// mDataHelper.ReadLog(item, context, handler);
					mDataHelper.ReadDynamic(item.Id, 2);
				} catch (Exception e) {
					LogUtils.e("erro", "查看员工日志异常:" + e);
				}
			}
		}).start();
	}

	private void search(String str) {
		demand.条件 = "";
		demand.附加条件 = "内容 like '%" + str + "%'";
		mlistViewHelperNet.setmDemand(demand);
		reload();
	}

	/** 下拉刷新 */
	public void refresh() {
		mlistViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
		try {
			mlistViewHelperNet.mDataList.clear();
			listViewAdapter.notifyDataSetChanged();
			mlistViewHelperNet.loadServerData(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
