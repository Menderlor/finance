package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.TestListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNew;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.测量信息;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 测量记录列表 使用PullToRrefreshListView
 * 
 * 另外一个 MeasureListActivity
 * 
 * @author BOHR
 * 
 */
public class TestListActivity extends BaseActivity {
	public static final String TAG = "TestListActivity";
	private int orderId = -1; // 订单编号
	PullToRefreshListView mListView;
	List<测量信息> mList;
	TestListViewAdapter mListAdapter;
	MyProgressBar mProgressBar;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private ListViewHelperNew mListViewHelperNew;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_list);
		orderId = getIntent().getIntExtra(TAG, -1);
		LogUtils.i("testList", "--" + orderId);
		findViews();
		init();
		setOnClickListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i(TAG, "onResume");
		LogUtils.i("testList", "--" + orderId);
		// reload();
		reloadFromOrder();
	}

	private void init() {
		mList = new ArrayList<测量信息>();
		Demand demand = new Demand();
		demand.表名 = "";
		demand.方法名 = "Cabinet/GetMeasurements";
		demand.条件 = "" + orderId + "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		mListAdapter = new TestListViewAdapter(TestListActivity.this,
				R.layout.test_listview_item, mList);
		mListView.setAdapter(mListAdapter);
		mListViewHelperNew = new ListViewHelperNew(this, 测量信息.class,
				TestListActivity.this, demand, mListView, mList, mListAdapter,
				mProgressBar, 80);
	}

	public void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listView_test_list);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_test_list);
		mProgressBar.setVisibility(View.GONE);
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_test_list);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew_test_list);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestListActivity.this,
						TestNewActivity.class);
				if (orderId != -1) {
					Bundle bundle = new Bundle();
					bundle.putInt(TestNewActivity.TAG, orderId);
					intent.putExtras(bundle);
				}
				startActivity(intent);
			}
		});

		// mListView.setOnRefreshListener(new OnPulldownRefreshListener() {
		// @Override
		// public void onPulldownRefresh() {
		// mListViewHelperNew.mListViewLoadType = ListViewLoadType.顶部视图;
		// try {
		// // 下拉刷新 导入数据
		// mListViewHelperNew.loadServerData(true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				测量信息 item = (测量信息) listView.getItemAtPosition(position);
				Intent intent = new Intent(TestListActivity.this,
						TestInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TestInfoActivity.TAG, item);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

	}

	private void reload() {
		mList.clear();
		mListViewHelperNew.loadLocalData();
		mListViewHelperNew.loadServerData(true);
	}

	/**
	 * 重新加载
	 */
	private void reloadFromOrder() {
		mList.clear();
		List<String> columnName = new ArrayList<String>();// 查询数据库的字段名
		List<String> columnLikeName = new ArrayList<String>();//
		// 查询数据库的字段名(like包含关系)
		columnName.add("Order");
		mListViewHelperNew.loadLocalData(columnName, columnLikeName, orderId
				+ "");
		// mListViewHelperNew.loadServerData(true, orderId + "");
		mListViewHelperNew.loadServerData(true);
	}

}
