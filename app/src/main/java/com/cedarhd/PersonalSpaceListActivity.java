package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.PersonalSpaceListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperKjx;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.帖子;

import java.util.ArrayList;
import java.util.List;

/*
 * @author py 2014.8.25
 */
public class PersonalSpaceListActivity extends BaseActivity {
	PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;
	private ImageView ivcancel;
	private ImageView ivnew;
	public static final int REQUEST_CODE_NEW_COMPANYSPACE = 0;
	private ListViewHelperKjx mListViewHelperKjx = null;
	private QueryDemand queryDemand;
	private Demand demand;
	private boolean isFling;
	public static boolean isResume; // 是否在Resume中刷新
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private String value;// 查询数据库的字段值
	public final static int SUCESS_READ_COMPANYSPACE = 10;
	private PersonalSpaceListViewAdapter mListAdapter;
	private List<帖子> m帖子List;
	// private ImageView ivcompanylist;
	帖子 map;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCESS_READ_COMPANYSPACE) {
				mListAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		value = Global.mUser.Id;
		setContentView(R.layout.personalspacelist);
		findviews();
		setonclicklistener();
		m帖子List = new ArrayList<帖子>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "tiezi/GetltTieziList/";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "更新时间";
		queryDemand.sortFildName = "UpdateTime";
		mListAdapter = new PersonalSpaceListViewAdapter(
				PersonalSpaceListActivity.this,
				R.layout.companyspacelist_listviewlayout, m帖子List, null);
		mListView.setAdapter(mListAdapter);
		mListViewHelperKjx = new ListViewHelperKjx(this, 帖子.class,
				PersonalSpaceListActivity.this, demand, queryDemand, mListView,
				m帖子List, mListAdapter, mProgressBar);
		reload();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isResume) {
			isResume = false;
			reload();
		}
	}

	private void reload() {
		m帖子List.clear();
		mListViewHelperKjx.loadServerData(true);
	}

	private void findviews() {
		// 初始化控件
		mListView = (PullToRefreshListView) findViewById(R.id.listView1);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_personalspacelist);
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel);
		ivnew = (ImageView) findViewById(R.id.imageViewNew);
		// ivcompanylist = (ImageView) findViewById(R.id.iv_companylist);
	}

	private void setonclicklistener() {
		// 添加监听事件
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PersonalSpaceListActivity.this,
						CompanySpaceNewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
			}
		});
		mListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mListViewHelperKjx.mListViewLoadType = ListViewLoadType.顶部视图;
				try {
					// 下拉刷新 导入数据
					mListViewHelperKjx.loadServerData(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获得是否Fling标志
				isFling = (arg1 == OnScrollListener.SCROLL_STATE_FLING);
				mListAdapter.setFling(isFling);
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ListView listView = (ListView) arg0;
				map = (帖子) listView.getItemAtPosition(arg2);
				Intent intent = new Intent(PersonalSpaceListActivity.this,
						CompanySpaceActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("帖子", map);
				intent.putExtras(bundle);
				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// zlServiceHelper.ReadNotice(map,
				// CompanySpaceListActivity.this, handler);
				// }
				// }).start();
				startActivity(intent);
			}
		});
	}

	public void activityIntent() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(PersonalSpaceListActivity.this,
				CompanySpaceActivity.class);
		startActivity(intent);
	}
}
