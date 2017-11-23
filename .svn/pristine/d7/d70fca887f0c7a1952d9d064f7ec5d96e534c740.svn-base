package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.DiamondDetailAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.钻石收发记录;
import com.cedarhd.models.钻石积分;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class DiamondDetailsActivity extends BaseActivity {
	private PullToRefreshListView detail_listView;
	/** 名字 */
	private TextView detail_name;
	/** 钻石排名 */
	private TextView detail_rank;
	/** 钻石数量 */
	private TextView detail_diamondl_num;
	private TextView detail_zan_num;
	private ImageButton back;
	private AvartarView avartarView;
	private HttpUtils httpUtils;
	private MyProgressBar progressBar;
	Demand demand;
	QueryDemand queryDemand;
	DiamondDetailAdapter adapter;
	private ListViewHelperNet<钻石收发记录> mListViewHelperNet;
	private List<钻石收发记录> list;
	private boolean isFling;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diamondl_details);
		httpUtils = new HttpUtils();
		initView();
		钻石积分 diamondl = (钻石积分) getIntent().getSerializableExtra("item");
		int position = getIntent().getIntExtra("position", 0);
		settitle(diamondl, position);
		list = new ArrayList<钻石收发记录>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		// demand.用户编号 = Global.mUser.Id;
		demand.附加条件 = "";
		demand.偏移量 = 10;
		demand.方法名 = "Diamond/GetDiamondRecordList";
		demand.条件 = "";
		demand.附加条件 = "接收人=" + diamondl.接收人;
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		adapter = new DiamondDetailAdapter(DiamondDetailsActivity.this,
				R.layout.record_item, list);
		detail_listView.setAdapter(adapter);
		mListViewHelperNet = new ListViewHelperNet<钻石收发记录>(this, 钻石收发记录.class,
				demand, detail_listView, list, adapter, progressBar,
				queryDemand);
		reload();
		setonclicklistener();

	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils
				.IsHaveInternet(DiamondDetailsActivity.this);
		if (!isConnectedInternet) {
			Toast.makeText(DiamondDetailsActivity.this,
					"需要连接到3G或者wifi因特网才能获取最新信息！", Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			list.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}

	private void initView() {
		detail_listView = (PullToRefreshListView) findViewById(R.id.detadil_listView);
		detail_name = (TextView) findViewById(R.id.detail_name);
		detail_rank = (TextView) findViewById(R.id.detail_rank);
		back = (ImageButton) findViewById(R.id.details_back);
		detail_diamondl_num = (TextView) findViewById(R.id.detail_diamondl_num);
		avartarView = (AvartarView) findViewById(R.id.details_photo);
		detail_zan_num = (TextView) findViewById(R.id.detail_zan_num);
		progressBar = (MyProgressBar) findViewById(R.id.progress_detailed);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void setonclicklistener() {
		detail_listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				mListViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
				list.clear();
				try {
					// 下拉刷新 导入数据
					mListViewHelperNet.loadServerData(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		detail_listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				// 获得是否Fling标志
				isFling = (arg1 == OnScrollListener.SCROLL_STATE_FLING);
				LogUtils.i("MyISFling", String.valueOf(isFling));
				adapter.setFling(isFling);

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	private void settitle(钻石积分 diamondl, int position) {
		detail_name.setText(diamondl.接收人姓名);
		detail_rank.setText(String.valueOf(position + 1));
		detail_diamondl_num.setText(String.valueOf(diamondl.钻石数量));
		detail_zan_num.setText(String.valueOf(diamondl.赞数量));
		new AvartarViewHelper(DiamondDetailsActivity.this,
				String.valueOf(diamondl.接收人), avartarView, false);
	}
}
