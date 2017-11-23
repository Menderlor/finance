package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.OrderListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelper;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.订单;

import java.util.ArrayList;
import java.util.List;

public class ErpOrderListActivity extends BaseActivity {

	public static final String TAG = "erpOrderInfo";
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private static final int SHOW_DATAPICKFrom = 0;
	private static final int SHOW_DATAPICKTo = 1;

	PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;

	List<订单> oderlist;

	private ListViewHelper mListViewHelper = null;

	public static final int REQUEST_CODE_LOG_NEW = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderlist);
		findViews();
		setOnClickListener();
		init();
	}

	void init() {
		Demand demand = new Demand();
		// demand.表名 = "订单";
		// demand.方法名 = "查询_分页";
		demand.表名 = "订单";
		demand.方法名 = "Cabinet/GetOrderList";
		demand.条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		oderlist = new ArrayList<订单>();
		OrderListViewAdapter listViewAdapter = new OrderListViewAdapter(this,
				R.layout.orderlist_listviewlayout, oderlist, null);

		mListView.setAdapter(listViewAdapter);

		mListViewHelper = new ListViewHelper(this, 订单.class,
				ErpOrderListActivity.this, demand, mListView, oderlist,
				listViewAdapter, mProgressBar, 80);
		reload();
	}

	/**
	 * 重新加载
	 */
	private void reload() {
		oderlist.clear();
		mListViewHelper.loadLocalData();
		mListViewHelper.loadServerData(true);
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

		// 下拉刷新，从服务器加载数据
		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mListViewHelper.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							// 下拉刷新 导入数据
							mListViewHelper.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
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
				订单 item = (订单) listView.getItemAtPosition(position);

				// TODO 点击查看订单详细信息
				Intent intent = new Intent(ErpOrderListActivity.this,
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
			reload();
		}

		if (requestCode == REQUEST_CODE_LOG_NEW) {
			// if (resultCode == WorkLogNewActivity.RESULT_RETURN_SUCCESS) {
			// reload();
			// }
		}
	}

	public void findViews() {
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

}
