package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.SuggestListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperKjx;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.客户投诉建议;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class SuggestListActivity extends BaseActivity {
	private int clientId = -1;
	PullToRefreshListView mListView;
	List<客户投诉建议> mList;
	private SuggestListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	private ListViewHelperKjx mListViewHelperKjx = null;
	private QueryDemand queryDemand;
	private Demand demand;

	private boolean isFling;
	public static boolean isResume; // 是否在Resume中刷新

	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	public String mUserSelectId = "";
	public String mUserSelectName = "";
	public static final int REQUEST_CODE_NEW_SUGGEST = 0;
	public static final int REQUEST_CODE_SELECT_ID = 1;
	// 查询数据库的字段名(like包含关系)
	private String value;// 查询数据库的字段值

	public final static int SUCESS_READ_SUGGEST = 10;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCESS_READ_SUGGEST) {
				mListAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// columnName.add("Publisher");
		// columnLikeName.add("Personnel");
		// value = Global.mUser.Id;
		setContentView(R.layout.suggestlist);
		findViews();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			LogUtils.i("clientId", "clientId:" + clientId);
		}
		setOnClickListener();

		mList = new ArrayList<客户投诉建议>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "FeedBacks/GetClientComplaintList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		mListAdapter = new SuggestListViewAdapter(SuggestListActivity.this,
				R.layout.suggestlist_listviewlayout, mList, null);
		mListView.setAdapter(mListAdapter);
		mListViewHelperKjx = new ListViewHelperKjx(this, 客户投诉建议.class,
				SuggestListActivity.this, demand, queryDemand, mListView,
				mList, mListAdapter, mProgressBar);
		if (clientId == -1) {
			reload();
		} else {
			reloadFromClient(clientId);
		}
	}

	/**
	 * 重新加载
	 */
	private void reloadFromClient(int clientId) {
		mList.clear();
		mListViewHelperKjx.queryDemand.eqDemand.clear();
		mListViewHelperKjx.queryDemand.eqDemand.put("ClientId", clientId + "");
		reload();
	}

	private void reload() {
		mList.clear();
		mListViewHelperKjx.loadServerData(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i("noticeList", "noticeList onResume");
		if (isResume) {
			isResume = false;
			reload();
		}
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createSuggest();
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mListViewHelperKjx.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							// 下拉刷新 导入数据
							mListViewHelperKjx.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		// listview滚动监听
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 获得是否Fling标志
				isFling = (scrollState == OnScrollListener.SCROLL_STATE_FLING);
				mListAdapter.setFling(isFling);
				LogUtils.i("scroll", "onScrollStateChanged--->isFling="
						+ isFling);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final 客户投诉建议 map = (客户投诉建议) listView
						.getItemAtPosition(position);
				Intent intent = new Intent(SuggestListActivity.this,
						SuggestActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Suggest", map);
				intent.putExtras(bundle);
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// zlServiceHelper.ReadNotice(map,
				// SuggestListActivity.this, handler);
				// }
				// }).start();
				startActivity(intent);
			}
		});
	}

	public void findViews() {
		// mbuttonNew = (Button) findViewById(R.id.buttonNew);
		mListView = (PullToRefreshListView) findViewById(R.id.lv_suggest);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_suggestlist);
		mProgressBar.setVisibility(View.GONE);
	}

	void createSuggest() {
		Intent intent = new Intent();
		intent.setClass(this.getApplicationContext(), SuggestNewActivity.class);
		startActivityForResult(intent, REQUEST_CODE_NEW_SUGGEST);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_NEW_SUGGEST) {
			if (resultCode == NoticeNewActivity.RESULT_CODE_SUCCESS) {
				LogUtils.d("guojianwen",
						"NoticeListActivity onActivityResult RESULT_CODE_SUCCESS");
				reload();
			} else if (resultCode == NoticeNewActivity.RESULT_CODE_FAILED) {
				LogUtils.d("guojianwen",
						"NoticeListActivity onActivityResult RESULT_CODE_FAILED");
			}
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_ID) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			mUserSelectId = bundle.getString("UserSelectId");
			mUserSelectName = bundle.getString("UserSelectName");
			LogUtils.i("mUserSelectId", mUserSelectId);
			if (mUserSelectName != null && !mUserSelectName.isEmpty()) {
				String publisher = mUserSelectId.replaceAll("'", "")
						.replaceAll(";", "");
				value = publisher;// 只能取得一个用户的id
				// columnLikeName.add("Publisher");
				queryDemand.eqDemand.clear();
				queryDemand.eqDemand.put("Publisher", value);
				demand.用户编号 = value;
				reload();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}