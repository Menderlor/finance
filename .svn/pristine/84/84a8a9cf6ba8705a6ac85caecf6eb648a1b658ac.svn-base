package com.cedarhd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.WeekLogInfoActivity;
import com.cedarhd.adapter.WeekLogListViewAdapter;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.周工作总结;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 周总结列表Fragment
 * 
 * @author kjx
 */
public class WeekLogFragment extends Fragment {
	private String url = Global.BASE_URL;
	private String methodName = "log/GetWeekLogList";

	private PullToRefreshListView lv;
	private TextView emptyView;
	private MyProgressBar pbar;
	private WeekLogListViewAdapter adapter;
	private List<周工作总结> mList = new ArrayList<周工作总结>();
	private ListViewHelperNet<周工作总结> mListViewHelperNet;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	public WeekLogFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_weeklog, null);
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_all_askform);
		pbar = (MyProgressBar) view.findViewById(R.id.pbar_askforme);
		emptyView = (TextView) view.findViewById(R.id.tv_empty);
		adapter = new WeekLogListViewAdapter(getActivity(),
				R.layout.item_weeklog, mList, null);
		lv.setAdapter(adapter);
		lv.setEmptyView(emptyView);
		init();
		setOnclickListener();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		url = url + methodName;
		LogUtils.i("onresume", "methodName onresume" + methodName);
		reload();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void setOnclickListener() {
		// 申请进入 查看申请详情
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				final 周工作总结 map = adapter.getItem(pos);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(WeekLogInfoActivity.TAG, map);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), WeekLogInfoActivity.class);
				startActivity(intent);
			}
		});
		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				reload();
			}
		});
	}

	private void init() {
		Demand demand = new Demand();
		demand.用户编号 = "";
		demand.表名 = "";
		demand.方法名 = methodName;
		demand.条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		QueryDemand queryDemand = new QueryDemand();
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "Time";
		mListViewHelperNet = new ListViewHelperNet<周工作总结>(getActivity(),
				周工作总结.class, demand, lv, mList, adapter, pbar, queryDemand);
	}

	private void reload() {
		// pbar.setVisibility(View.VISIBLE);
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(getActivity());
		if (!isConnectedInternet) {
			if (pbar != null) {
				pbar.setVisibility(View.GONE);
			}
			Toast.makeText(getActivity(), "需要连接移动网络或wifi才能获取最新信息！",
					Toast.LENGTH_LONG).show();
		} else {
			mList.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			mListViewHelperNet.loadServerData(true);
		}
	}
}
