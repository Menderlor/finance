package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.cedarhd.adapter.CompanySpaceListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.帖子;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 公司空间-分享
 * 
 */
public class CompanySpaceListActivity extends BaseActivity {

	private Context mContext;
	PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;
	private ImageView ivnew;
	private ListViewHelperNet<帖子> mListViewHelperKjx = null;
	private QueryDemand queryDemand;
	private Demand demand;
	private boolean isFling;
	public static boolean isResume; // 是否在Resume中刷新
	private String value;// 查询数据库的字段值
	private CompanySpaceListViewAdapter mListAdapter;
	public static List<帖子> m帖子List;
	private ImageView add;

	public static final int REQUEST_CODE_NEW_COMPANYSPACE = 0;
	public final static int SUCESS_READ_COMPANYSPACE = 10;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCESS_READ_COMPANYSPACE) {
				mListAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		value = Global.mUser.Id;
		setContentView(R.layout.activity_companyspacelist);
		findviews();
		setonclicklistener();

		mContext = CompanySpaceListActivity.this;
		m帖子List = new ArrayList<帖子>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "tiezi/GetTieziList/";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 5;
		demand.偏移量 = 0;
		queryDemand.fildName = "更新时间";
		queryDemand.sortFildName = "UpdateTime";
		LogUtils.i("out", m帖子List.size() + ".............................");
		mListAdapter = new CompanySpaceListViewAdapter(this, m帖子List);
		mListView.setAdapter(mListAdapter);
		mListViewHelperKjx = new ListViewHelperNet<帖子>(this, 帖子.class, demand,
				mListView, m帖子List, mListAdapter, mProgressBar, queryDemand);

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

	private void reload() {
		m帖子List.clear();
		mListAdapter.notifyDataSetChanged();
		mListViewHelperKjx.loadServerData(true);
	}

	private void findviews() {
		// 初始化控件
		mListView = (PullToRefreshListView) findViewById(R.id.listView12);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_companyspacelist);
		ivnew = (ImageView) findViewById(R.id.imageViewNews);
		add = (ImageView) findViewById(R.id.imageViewNews);
	}

	private void setonclicklistener() {
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CompanySpaceListActivity.this,
						CompanySpaceNewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
			}
		});

		// 添加监听事件
		ivnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CompanySpaceListActivity.this,
						CompanySpaceNewActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_COMPANYSPACE);
			}
		});

		mListView.setOnRefreshListener(new OnRefreshListener() {
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

//		mListView.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//
//			}
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// 获得是否Fling标志
//				isFling = (scrollState == OnScrollListener.SCROLL_STATE_FLING);
//				LogUtils.i("MyISFling", String.valueOf(isFling));
//				mListAdapter.setFling(isFling);
//			}
//		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position = mListView.getHeaderViewsCount();
				帖子 item = m帖子List.get(pos);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ShareInfoActivity.TAG_INFO, item);
				Intent intent = new Intent(mContext, ShareInfoActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
}
