package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cedarhd.adapter.TestListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.listview.ListViewSimpleHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.测量信息;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MeasureListActivity extends BaseActivity {
	public static final String TAG = "MeasureListActivity";
	private int orderId = -1; // 订单编号
	ListView listView;
	List<测量信息> mList;
	TestListViewAdapter mListAdapter;
	ProgressBar mProgressBar;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private ListViewSimpleHelper listViewSimpleHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.measure_list);
		orderId = getIntent().getIntExtra(TAG, -1);
		LogUtils.i("testList", "--" + orderId);
		findViews();
		init();
		setOnClickListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i(TAG, "onResume");
		LogUtils.i("testList", "--" + orderId);
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

		mListAdapter = new TestListViewAdapter(MeasureListActivity.this,
				R.layout.test_listview_item, mList);
		listView.setAdapter(mListAdapter);
		listViewSimpleHelper = new ListViewSimpleHelper(this, 测量信息.class,
				MeasureListActivity.this, demand, listView, mList,
				mListAdapter, mProgressBar, 80);
	}

	public void findViews() {
		listView = (ListView) findViewById(R.id.listView_measure_list);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_measure_list);
		mProgressBar.setVisibility(View.GONE);
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_measure_list);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew_measure_list);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MeasureListActivity.this,
						TestNewActivity.class);
				if (orderId != -1) {
					Bundle bundle = new Bundle();
					bundle.putInt(TestNewActivity.TAG, orderId);
					intent.putExtras(bundle);
				}
				startActivity(intent);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				测量信息 item = (测量信息) listView.getItemAtPosition(position);
				Intent intent = new Intent(MeasureListActivity.this,
						TestInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TestInfoActivity.TAG, item);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

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
		listViewSimpleHelper.loadLocalData(columnName, columnLikeName, orderId
				+ "");
		listViewSimpleHelper.loadServerData(true);
	}
}
