package com.cedarhd;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cedarhd.adapter.OrderListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.listener.OnTouchListener_Search;
import com.cedarhd.listener.TextWatcher_Search;
import com.cedarhd.listener.TextWatcher_Search.TextWatcher_SearchListener;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.订单;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单列表页面
 * 
 * @author bohr
 * 
 */
public class OrderListActivity extends BaseActivity {
	public static final String TAG = "OrderListActivity";
	ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private static final int SHOW_DATAPICKFrom = 0;
	private static final int SHOW_DATAPICKTo = 1;
	public static boolean isResume; // 是否在Resume中刷新

	PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;
	EditText mSearchView;
	OrderListViewAdapter listViewAdapter;

	java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	List<订单> oderlist;
	List<订单> m订单ListFilter;

	// private ListViewHelper mListViewHelper = null;
	private ListViewHelperNet<订单> mListViewHelperNet;

	public static final int REQUEST_CODE_LOG_NEW = 10;

	public static final int SUCESS_READED = 3; // 修改状态为已读

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			if (what == SUCESS_READED) {
				listViewAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderlist);
		findViews();
		setOnClickListener();
		init();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isResume) {
			isResume = false;
			reload();
		}
	}

	Demand demand = new Demand();
	QueryDemand queryDemand; // 查询条件

	void init() {
		// demand.表名 = "订单";
		// demand.方法名 = "查询_分页";
		demand.表名 = "订单";
		// demand.方法名 = "Cabinet/GetOrderList";
		demand.方法名 = "Cabinet/GetOrderList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		oderlist = new ArrayList<订单>();
		listViewAdapter = new OrderListViewAdapter(this,
				R.layout.orderlist_listviewlayout, oderlist, null);
		mListView.setAdapter(listViewAdapter);
		mListViewHelperNet = new ListViewHelperNet<订单>(this, 订单.class, demand,
				mListView, oderlist, listViewAdapter, mProgressBar, queryDemand);
	}

	/**
	 * 重新加载
	 */
	private void reload() {
		oderlist.clear();
		// List<String> columnName = new ArrayList<String>();// 查询数据库的字段名
		// List<String> columnLikeName = new ArrayList<String>();
		// // 查询数据库的字段名(like包含关系)
		// columnName.add("Designer");
		// mListViewHelper.loadLocalData(columnName, columnLikeName,
		// Global.mUser.Id + "");
		// mListViewHelper.loadServerData(true);

		mListViewHelperNet.loadServerData(true);
	}

	public void setOnClickListener() {
		// 返回上级界面
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_orderlist);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 添加
		ImageView imageViewNew = (ImageView) findViewById(R.id.imageViewNew_orderlist);
		imageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				New();
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mListViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							mListViewHelperNet.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		// // 下拉刷新，从服务器加载数据
		// mListView.setOnRefreshListener(new OnPulldownRefreshListener() {
		// @Override
		// public void onPulldownRefresh() {
		// mListViewHelper.mListViewLoadType = ListViewLoadType.顶部视图;
		// try {
		// mListViewHelper.loadServerData(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final 订单 item = (订单) listView.getItemAtPosition(position);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// 读订单
							zlServiceHelper.ReadOrder(item, getBaseContext(),
									handler);
						} catch (Exception e) {
							Toast.makeText(OrderListActivity.this, "查看订单异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
				// TODO 点击查看订单详细信息
				Intent intent = new Intent(OrderListActivity.this,
						OrderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TAG, item);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == -1) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String filter = bundle.getString("filter");
			// 查询
			// reload();
		}
		if (requestCode == REQUEST_CODE_LOG_NEW) {
			// if (resultCode == WorkLogNewActivity.RESULT_RETURN_SUCCESS) {
			// reload();
			// }
		}
	}

	public void findViews() {
		final Resources res = getResources();
		mSearchView = (EditText) findViewById(R.id.editTextFilter_orderlist);
		TextWatcher_Search textWatcher_Search = new TextWatcher_Search(
				mSearchView, res.getDrawable(R.drawable.txt_search_default),
				res.getDrawable(R.drawable.txt_search_clear));
		textWatcher_Search
				.setTextWatcher_SearchListener(new TextWatcher_SearchListener() {
					@Override
					public void onSearch(String str) {
						Serach_Filter(str);
					}
				});
		mSearchView.addTextChangedListener(textWatcher_Search);
		mSearchView.setOnTouchListener(new OnTouchListener_Search(mSearchView,
				OrderListActivity.this));
		mSearchView.setCompoundDrawablesWithIntrinsicBounds(null, null,
				res.getDrawable(R.drawable.txt_search_default), null);

		mListView = (PullToRefreshListView) findViewById(R.id.listView_orderlist);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_orderlist);
	}

	// 添加新订单
	void New() {
		// Intent intent = new Intent(WorkLogListActivity.this,
		// WorkLogNewActivity.class);
		// startActivityForResult(intent, REQUEST_CODE_LOG_NEW);
	}

	void Back() {
		finish();
	}

	BaseAdapter GetSimpleAdapter(List list) {
		OrderListViewAdapter adapter = new OrderListViewAdapter(this,
				R.layout.orderlist_listviewlayout, list, null);
		return adapter;
	}

	/**
	 * 过滤条件
	 * 
	 * @param str
	 */
	private void Serach_Filter(String filter) {
		oderlist = mListViewHelperNet.mDataList;
		// if (filter.replaceAll(" ", "").length() <= 0) {
		// BaseAdapter simpleAdapter = GetSimpleAdapter(oderlist);
		// mListView.setAdapter(simpleAdapter);
		// } else {
		// m订单ListFilter = new ArrayList<订单>();
		// filter = filter.trim();
		// String address = "";
		// String phone = "";
		// for (订单 bean : oderlist) {
		// address = bean.getAddress();
		// phone = bean.getPhone();
		// if (address.contains(filter) || phone.contains(filter)) {
		// m订单ListFilter.add(bean);
		// }
		// }
		// BaseAdapter simpleAdapter = GetSimpleAdapter(m订单ListFilter);
		// mListView.setAdapter(simpleAdapter);
		// simpleAdapter.notifyDataSetChanged();
		// }

		mListViewHelperNet.mDataList.clear();

		demand.附加条件 = filter;
		mListViewHelperNet.setmDemand(demand);
		mListViewHelperNet.loadServerData(true);
	}
}
