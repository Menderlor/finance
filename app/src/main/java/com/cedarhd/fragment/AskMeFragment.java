package com.cedarhd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.R;
import com.cedarhd.adapter.FlowListViewAdapter;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.流程;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 待我审批页面
 * 
 * @author kjx
 * @since 2015/03/12
 */
public class AskMeFragment extends Fragment {
	private String url = Global.BASE_URL;
	public String methodName; // 根据方法显示（区分）具体的页面内容

	private PullToRefreshListView lv;
	private ListViewHelperNet<流程> mListViewHelperNet;
	private TextView emptyView;
	private MyProgressBar pbar;
	private FlowListViewAdapter adapter;
	private List<流程> mList = new ArrayList<流程>();
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	public final int REQUEST_CODE_ASKFOR_ME = 12;// 待我审批

	public AskMeFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.all_askform_fragment, null);
		lv = (PullToRefreshListView) view.findViewById(R.id.lv_all_askform);
		pbar = (MyProgressBar) view.findViewById(R.id.pbar_askforme);
		emptyView = (TextView) view.findViewById(R.id.tv_empty);
		adapter = new FlowListViewAdapter(getActivity(),
				R.layout.askforleavelist_item, mList, null);
		lv.setAdapter(adapter);
		lv.setEmptyView(emptyView);
		setOnClickLisener();
		init();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		url = url + methodName;
		LogUtils.i("onresume", "methodName onresume" + methodName);
		reload();
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
		mListViewHelperNet = new ListViewHelperNet<流程>(getActivity(), 流程.class,
				demand, lv, mList, adapter, pbar, queryDemand);
	}

	private void setOnClickLisener() {

		// 待我审批 进入审批; 申请进入 查看申请详情
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position;
				ListView listView = (ListView) parent;
				if (position > 0) {
					position -= 1;
				}
				final 流程 map = mList.get(position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("flow", map);
				// 待我审批则显示审核输入框
				if (methodName.equals("Flow/GetApprovalFlow/")) {
					bundle.putBoolean("isAudit", true);
				} else {
					bundle.putBoolean("isAudit", false);
				}

				intent.putExtras(bundle);

				intent.setClass(getActivity(), CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_ASKFOR_ME);

				setReaded(pos, map);
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				reload();
			}
		});
	}

	private void reload() {
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

	/**
	 * 设置流程为已读
	 * 
	 * @param position
	 *            ListView点中项的编号
	 * @param item
	 */
	private void setReaded(int position, final 流程 item) {
		if (!TextUtils.isEmpty(item.已读时间)) {
			return;
		}
		adapter.getDataList().get(position - 1).已读时间 = ViewHelper
				.getDateString();
		adapter.notifyDataSetChanged();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.ReadDynamic(item.Id, 4);
				} catch (Exception e) {
					LogUtils.e("erro", "查看员工日志异常:" + e);
				}
			}
		}).start();
	}
}
