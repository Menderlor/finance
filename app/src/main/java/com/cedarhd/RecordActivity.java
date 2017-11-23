package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cedarhd.adapter.DiamondDetailAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.钻石收发记录;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/** 钻石发放记录 */
public class RecordActivity extends BaseActivity {
	private PullToRefreshListView record_listview;
	private List<钻石收发记录> list;
	private ImageButton back;
	private HttpUtils httpUtils;
	private MyProgressBar progressBar;
	String url;
	String result;
	Demand demand;
	QueryDemand queryDemand;
	private ListViewHelperNet<钻石收发记录> mListViewHelperNet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		httpUtils = new HttpUtils();
		initView();
		list = new ArrayList<钻石收发记录>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		// demand.用户编号 = Global.mUser.Id;
		demand.附加条件 = "";
		demand.偏移量 = 10;
		demand.方法名 = "Diamond/GetDiamondRecordList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		DiamondDetailAdapter adapter = new DiamondDetailAdapter(
				RecordActivity.this, R.layout.record_item, list);
		record_listview.setAdapter(adapter);
		mListViewHelperNet = new ListViewHelperNet<钻石收发记录>(this, 钻石收发记录.class,
				demand, record_listview, list, adapter, progressBar,
				queryDemand);
		LogUtils.i("out", "adapter.getDataList()"
				+ adapter.getDataList().size());
		reload();
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils
				.IsHaveInternet(RecordActivity.this);
		if (!isConnectedInternet) {
			Toast.makeText(RecordActivity.this, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			list.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}

	private void initView() {
		record_listview = (PullToRefreshListView) findViewById(R.id.record_listView);
		back = (ImageButton) findViewById(R.id.record_back);
		progressBar = (MyProgressBar) findViewById(R.id.progress_record);
		setOnclick();
	}

	private void setOnclick() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		record_listview.setOnRefreshListener(new OnRefreshListener() {

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
	}
}
