package com.cedarhd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.ContactHistoryListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.User;
import com.cedarhd.models.changhui.CH客户联系记录;
import com.cedarhd.models.changhui.ChQueryFilter;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.widget.ClientContactFilterPopupWindow;
import com.cedarhd.widget.ClientContactFilterPopupWindow.OnSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * CH客户联系记录列表
 * 
 * @author KJX
 * 
 */
public class ClientConstactListActivity extends BaseActivity {
	public static final String TAG = "ClientConstactListActivity";
	public static final String SALE_CHANCE_ID = "SALE_CHANCE_ID";

	/** 新建联系记录 */
	private final int REQUEST_CODE_ADD_CONTACT = 101;

	/** 新建联系记录 */
	private final int REQUEST_CODE_UPDATE_CONTACT = 102;

	private int mClientId = -1;
	private int saleChanceId = -1;
	private int mItemPos = -1;

	private PullToRefreshListView mListView;
	private ImageView ivAdd;
	private ImageView ivFilter;
	private BoeryunSearchView searchView;

	List<CH客户联系记录> mList;
	private Context mContext;
	ContactHistoryListViewAdapter mListAdapter;
	MyProgressBar mProgressBar;

	private ClientContactFilterPopupWindow mFilterPopupWindow;
	private ChQueryFilter mQueryFilter;

	private ListViewHelperNet<CH客户联系记录> mlistViewHelperNet;
	private QueryDemand queryDemand; // 本地查询条件
	Demand demand;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	public static boolean isResume = false; // 是否在Resume中刷新
	private Handler handler = new Handler() {
		public static final int UPDATE_CONSTACT_SUCCESS = 3; // 修改成功
		public static final int UPDATE_CONSTACT_FAILED = 4;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_CONSTACT_SUCCESS: // 修改联系记录
				mListAdapter.notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_constract_list);
		initData();
		findViews();
		setOnClickListener();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mClientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			LogUtils.i("clientId", "-->" + mClientId);
			saleChanceId = bundle.getInt(SALE_CHANCE_ID, -1);
			LogUtils.i("SALE_CHANCE_ID", saleChanceId + "");
		}
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ADD_CONTACT:
				refresh();
				break;
			case REQUEST_CODE_UPDATE_CONTACT:
				if (mItemPos != -1) {
					mList.remove(mItemPos);
					mListAdapter.notifyDataSetChanged();
					mItemPos = -1;
				}
				refresh();
				break;
			case UserBiz.SELECT_SINAL_USER_REQUEST_CODE:
				User user = UserBiz.onActivityUserSelected(requestCode,
						resultCode, data);
				if (user.Id.length() > 0 && user.UserName.length() > 0) {
					mQueryFilter.userId = Integer.parseInt(user.Id);
					mQueryFilter.userName = user.UserName;
					mFilterPopupWindow.updateUserFilter(mQueryFilter);
				}
				break;
			case ClientBiz.SELECT_CLIENT_CODE:
				Client client = ClientBiz
						.onActivityGetClient(requestCode, data);
				mQueryFilter.clientId = client.Id;
				mQueryFilter.clientName = client.CustomerName;
				mFilterPopupWindow.updateClientFilter(mQueryFilter);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 定义过滤器
		IntentFilter filter = new IntentFilter();
		// 定义广播响应Intent指定的代号
		filter.addAction(TAG);
		// 可以添加多个Action,只要匹配其中一个即可通过
		// 动态注册 BroadcastReceiver
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isResume) {
			isResume = false;
			LogUtils.i(TAG, "onResume" + isResume + "\t" + mClientId);
			if (mClientId == -1) {
				reload();
			} else {
				queryDemand.eqDemand.clear();
				queryDemand.eqDemand.put("Customer", mClientId + "");
				// reload();

				reloadByClient();
			}
		}
	}

	private void initData() {
		mContext = this;
		mList = new ArrayList<CH客户联系记录>();
		queryDemand = new QueryDemand();
		// queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "最后更新";
		demand = new Demand();
		demand.表名 = "CH客户联系记录";
		demand.方法名 = "Customer/GetCustomerContactRecords";
		demand.条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
	}

	private void init() {

		if (mClientId != -1) {
			demand.附加条件 = "客户=" + mClientId;
			// 如果是查看指定客户的联系记录,隐藏新建按钮
			// ivAdd.setVisibility(View.INVISIBLE);
			ivFilter.setVisibility(View.INVISIBLE);
			searchView.setVisibility(View.GONE);
		}
		if (saleChanceId != -1) {
			if (TextUtils.isEmpty(demand.附加条件)) {
				demand.附加条件 = "销售机会=" + saleChanceId;
			} else {
				demand.附加条件 += "销售机会=" + saleChanceId;
			}
		}

		queryDemand.fildName = "最后更新";
		// queryDemand.sortFildName = "PrepareTime";
		// queryDemand.localFildName = "PrepareTime";
		queryDemand.sortFildName = "最后更新";
		mListAdapter = new ContactHistoryListViewAdapter(this,
				R.layout.contacthistorylist_listviewlayout, mList,
				Global.clientList, null);
		mListView.setAdapter(mListAdapter);

		mlistViewHelperNet = new ListViewHelperNet<CH客户联系记录>(this,
				CH客户联系记录.class, demand, mListView, mList, mListAdapter,
				mProgressBar, queryDemand);
		reload();
	}

	public void findViews() {
		mContext = ClientConstactListActivity.this;
		mListView = (PullToRefreshListView) findViewById(R.id.listView_client_constracts);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_client_constracts);
		mProgressBar.setVisibility(View.GONE);

		ivFilter = (ImageView) findViewById(R.id.iv_filter_client_constracts);
		searchView = (BoeryunSearchView) findViewById(R.id.searchview_client_constracts);

		mQueryFilter = new ChQueryFilter();
		mFilterPopupWindow = new ClientContactFilterPopupWindow(ivFilter,
				mContext);
		
		ivFilter.setVisibility(View.GONE);
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_client_constracts);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivAdd = (ImageView) findViewById(R.id.imageViewNew_client_constracts);
		ivAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientConstactListActivity.this,
						ClientConstactNewActivity.class);
				if (mClientId != -1) {
					Bundle bundle = new Bundle();
					bundle.putInt("ClientInfoActivity_clientId", mClientId);
					intent.putExtras(bundle);
				}
				startActivityForResult(intent, REQUEST_CODE_ADD_CONTACT);
			}
		});

		ivFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFilterPopupWindow.show(mQueryFilter);
				mFilterPopupWindow.setOnClickListener(new OnSelectListener() {
					@Override
					public void onSelect(ChQueryFilter filter) {
						mQueryFilter = filter;
						// showShortToast(filter.userName + "\n"
						// + filter.clientName);
						initMoreFilter(filter);
						reLoadData();
					}

					@Override
					public void onStartSelect(ChQueryFilter filter) {
						mQueryFilter = filter;
					}
				});

			}
		});

		searchView.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				demand.附加条件 = "内容 like '%" + str + "%'";
				reLoadData();
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						refresh();
					}
				});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mItemPos = position - mListView.getHeaderViewsCount();
				ListView listView = (ListView) parent;
				final CH客户联系记录 item = (CH客户联系记录) listView
						.getItemAtPosition(position);
				Intent intent = new Intent(ClientConstactListActivity.this,
						ClientConstactNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ClientConstactNewActivity.TAG, item);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUEST_CODE_UPDATE_CONTACT);
				readClientContact(position, item);
			}
		});

		// listview滚动监听
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 获得是否Fling标志
				boolean isFling = (scrollState == OnScrollListener.SCROLL_STATE_FLING);
				mListAdapter.setFling(isFling);
				LogUtils.i("scroll", "onScrollStateChanged--->isFling="
						+ isFling);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	private void reload() {
		mList.clear();
		mListAdapter.notifyDataSetChanged();
		LogUtils.i(TAG, "reload()");
		// mListViewHelper.loadLocalData();
		// mListViewHelper.loadServerData(true);
		// listViewHelperKjx.loadLocalData();
		// listViewHelperKjx.loadServerData(true);
		mlistViewHelperNet.setmDemand(demand);
		mlistViewHelperNet.loadServerData(true);
	}

	/**
	 * 根据客户编号加载
	 */
	private void reloadByClient() {
		mList.clear();
		mListAdapter.notifyDataSetChanged();
		LogUtils.i(TAG, "reloadByClient()");
		// listViewHelperKjx.loadLocalData();
		// listViewHelperKjx.loadServerData(false);

		mlistViewHelperNet.loadServerData(true);
	}

	/**
	 * 设置CH客户联系记录为已读
	 * 
	 * @param position
	 *            ListView点中项的编号
	 * @param item
	 */
	private void readClientContact(int position, final CH客户联系记录 item) {
		if (!TextUtils.isEmpty(item.已读时间)) {
			return;
		}
		mListAdapter.getDataList().get(position - 1).已读时间 = ViewHelper
				.getDateString();
		mListAdapter.notifyDataSetChanged();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.ReadDynamic(item.编号, 8);
				} catch (Exception e) {
					LogUtils.e("erro", "查看客户异常:" + e);
				}
			}
		}).start();
	}

	private void refresh() {
		mlistViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
		try {
			// 下拉刷新 导入数据
			mlistViewHelperNet.loadServerData(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void reLoadData() {
		demand.偏移量 = 0;
		mList.clear();
		mlistViewHelperNet.setmDemand(demand);
		mlistViewHelperNet.mDataList = mList;
		mlistViewHelperNet.setNotifyDataSetChanged();
		refresh();
	}

	private void initMoreFilter(ChQueryFilter filter) {
		demand.附加条件 = "1=1";
		if (filter != null) {
			if (filter.userId != 0) {
				demand.附加条件 += " AND 业务员=" + filter.userId;
			}

			if (filter.clientId != 0) {
				demand.附加条件 += " AND 客户=" + filter.clientId;
			}

			if (filter.所在岗位 != 0) {
				demand.附加条件 += " AND 所在岗位=" + filter.所在岗位;
			}
			if (filter.所在部门 != 0) {
				demand.附加条件 += " AND 所在部门=" + filter.所在部门;
			}

			if (!TextUtils.isEmpty(filter.startTime)) {
				// mQmContract.moreFilter += " AND 客户="
				// + filter.clientId;
			}

			if (!TextUtils.isEmpty(filter.endTime)) {
				// mQmContract.moreFilter += " AND 客户="
				// + filter.clientId;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.i("ACTION", intent.getAction());
		}
	};
}
