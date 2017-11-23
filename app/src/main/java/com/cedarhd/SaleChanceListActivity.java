package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.SalesChanceListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.销售机会;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售机会列表
 * 
 * @author BOHR
 * 
 */
public class SaleChanceListActivity extends BaseActivity {
	public final static String TAG = "SaleChanceListActivity";
	public static final String SELECT_SALE_CHANCE = "SELECT_SALE_CHANCE";// 选中销售机会
	private boolean isSelect_sale_chance = false;// 是否选择销售机会
	private PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;
	private List<销售机会> mList;
	// private ListViewHelper mListViewHelper;
	// private ListViewHelperKjx listViewHelperkjx;
	private ListViewHelperNet<销售机会> listViewHelperNet;
	private ZLServiceHelper zlServiceHelper;
	private SalesChanceListViewAdapter listViewAdapter;
	private ImageView imageViewNew;

	private int clientId = -1; // 客户id,从其他页面传递过来的
	public static boolean isResume; // 是否在Resume中刷新

	private QueryDemand queryDemand; // 本地查询条件
	Demand demand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sale_chance_list);
		findViews();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			LogUtils.i("clientId", "clientId:" + clientId);

			isSelect_sale_chance = bundle.getBoolean(SELECT_SALE_CHANCE, false);
		}
		init();
		setOnClickListener();

		if (clientId == -1) {
			reload();
		} else {
			imageViewNew.setVisibility(View.GONE);
			reloadFromClient(clientId);
		}
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

	private void init() {
		demand = new Demand();
		demand.表名 = "销售机会";
		demand.方法名 = "Customer/GetBusinessOppList";
		demand.条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		mList = new ArrayList<销售机会>();
		listViewAdapter = new SalesChanceListViewAdapter(this,
				R.layout.sale_chance_listview_item, mList, null);
		mListView.setAdapter(listViewAdapter);
		zlServiceHelper = new ZLServiceHelper();
		listViewHelperNet = new ListViewHelperNet<销售机会>(
				SaleChanceListActivity.this, 销售机会.class, demand, mListView,
				mList, listViewAdapter, mProgressBar, queryDemand);
	}

	/**
	 * 重新加载
	 */
	private void reload() {
		mList.clear();
		// listViewHelperkjx.loadServerData(true);
		listViewHelperNet.loadServerData(true);
	}

	/**
	 * 重新加载
	 */
	private void reloadFromClient(int value) {
		mList.clear();
		queryDemand.eqDemand.clear();
		queryDemand.eqDemand.put("CustomerId", value + "");
		// List<String> columnName = new ArrayList<String>();// 查询数据库的字段名
		// List<String> columnLikeName = new ArrayList<String>();//
		// // 查询数据库的字段名(like包含关系)
		// columnName.add("CustomerId");
		// mListViewHelper.loadLocalData(columnName, columnLikeName, value +
		// "");
		// mListViewHelper.loadServerData(true, value + "");
		reload();
	}

	public void setOnClickListener() {
		// 返回上级界面
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel_sale_chance);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 添加
		imageViewNew = (ImageView) findViewById(R.id.imageViewNew_sale_chance);
		imageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SaleChanceListActivity.this,
						SaleChanceInfoActivity.class);
				// 如果是从其他页面跳转过来的，新建默认客户为指定客户
				if (clientId != -1) {
					Bundle bundle = new Bundle();
					bundle.putInt("ClientInfoActivity_clientId", clientId);
					intent.putExtras(bundle);
				}
				startActivity(intent);
			}
		});

		// 下拉刷新，从服务器加载数据
		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						listViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
						reload();
					}
				});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				final 销售机会 item = (销售机会) listView.getItemAtPosition(position);
				if (isSelect_sale_chance) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(SELECT_SALE_CHANCE, item);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					// TODO 点击查看销售机会详细信息
					Intent intent = new Intent(SaleChanceListActivity.this,
							SaleChanceInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(TAG, item);
					intent.putExtras(bundle);
					startActivity(intent);

					readClientContact(position, item);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listView_sale_chance);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_sale_chance);
	}

	/**
	 * 设置客户联系记录为已读
	 * 
	 * @param position
	 *            ListView点中项的编号
	 * @param item
	 */
	private void readClientContact(int position, final 销售机会 item) {
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
					zlServiceHelper.ReadDynamic(item.Id, 8);
				} catch (Exception e) {
					Log.e("erro", "查看销售机会异常:" + e);
				}
			}
		}).start();
	}
}
