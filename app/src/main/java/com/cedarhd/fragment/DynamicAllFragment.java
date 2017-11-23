package com.cedarhd.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.cedarhd.R;
import com.cedarhd.TaskInfoActivity;
import com.cedarhd.WorkLogActivity;
import com.cedarhd.adapter.DynamicAdapter;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.任务;
import com.cedarhd.models.动态;
import com.cedarhd.models.日志;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 全部动态
 * 
 * @author Administrator
 * 
 */
public class DynamicAllFragment extends Fragment {
	private Context context;
	private PullToRefreshListView lv;
	private ListViewHelperNet<动态> mListViewHelperNet;
	private MyProgressBar mPbar;
	private DynamicAdapter mListAdapter;
	private List<动态> mList;
	private Demand demand;
	private QueryDemand queryDemand;
	private ZLServiceHelper zlServiceHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dynamic_no_read_news,
				null);
		findViews(view);
		initView();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		mListViewHelperNet.loadServerData(true);
	}

	private void findViews(View view) {
		lv = (PullToRefreshListView) view
				.findViewById(R.id.lv_dynmic_news_no_read);
		mPbar = (MyProgressBar) view
				.findViewById(R.id.pbar_dynmic_news_no_read);

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mListViewHelperNet.mListViewLoadType = ListViewLoadType.顶部视图;
				try {
					// 下拉刷新 导入数据
					mListViewHelperNet.loadServerData(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initView() {
		context = getActivity();
		zlServiceHelper = new ZLServiceHelper();
		demand = new Demand();
		mList = new ArrayList<动态>();
		demand.表名 = "";
		demand.用户编号 = "";
		demand.方法名 = "dynamic/getDynamicList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand = new QueryDemand();
		queryDemand.sortFildName = "Time";
		queryDemand.fildName = "时间";
		mListAdapter = new DynamicAdapter(mList, context);
		lv.setAdapter(mListAdapter);
		mListViewHelperNet = new ListViewHelperNet<动态>(context, 动态.class,
				demand, lv, mList, mListAdapter, mPbar, queryDemand);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				动态 item动态 = mListAdapter.getItem(pos);
				try {
					// 不可查看动态
					startNewActivitys(item动态);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.d("ERRE", "查看动态异常：" + e.toString());
				}

				if (TextUtils.isEmpty(item动态.Read)) {
					if (mListAdapter.getList() != null
							&& mListAdapter.getList().size() > pos) {
						mListAdapter.getList().get(pos).Read = ViewHelper
								.getDateString();
						mListAdapter.notifyDataSetChanged();
					}
				}
			}
		});

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		}
	};

	/**
	 * 处理页面跳转逻辑
	 * 
	 * @param item
	 */
	private void startNewActivitys(动态 item) throws Exception {
		// String type = item.getType();
		// int dataType = item.DataType;
		final int id = item.Id;
		new Thread(new Runnable() {
			@Override
			public void run() {
				zlServiceHelper.ReadDynamicById(id, context);
			}
		}).start();
		return;

		// if (!TextUtils.isEmpty(type) || dataType != 0) {
		// if (type.contains("任务")) {
		// final 任务 task = item.getTask();
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // if(Text)
		// // 点击动态，设置为已读
		// zlServiceHelper.ReadDynamicById(id, context);
		// zlServiceHelper.ReadTask(task, context, handler);
		// }
		// }).start();
		// // if (task != null) {
		// // startTaskIntent(task);
		// // } else {
		// // Toast.makeText(context, "该任务无法打开", Toast.LENGTH_SHORT)
		// // .show();
		// // }
		// } else if (type.contains("日志")) {
		// final 日志 log = item.getLog();
		//
		// // 设置为已读
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // 点击动态，设置为已读
		// zlServiceHelper.ReadDynamicById(id, context);
		// zlServiceHelper.ReadLog(log, context, handler);
		// }
		// }).start();
		// // if (log != null) {
		// // startLogIntent(log);
		// // } else {
		// // Toast.makeText(context, "该日志无法打开", Toast.LENGTH_SHORT)
		// // .show();
		// // }
		// } else if (type.contains("通知")) {
		// final 通知 notice = item.getNotice();
		//
		// // 设置为已读
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // 点击动态，设置为已读
		// zlServiceHelper.ReadDynamicById(id, context);
		// zlServiceHelper.ReadNotice(notice, context, handler);
		// }
		// }).start();
		// // if (notice != null) {
		// // Intent intent = new Intent(context, NoticeActivity.class);
		// // Bundle bundle = new Bundle();
		// // bundle.putSerializable("Notice", notice);
		// // intent.putExtras(bundle);
		// // startActivity(intent);
		// // } else {
		// // Toast.makeText(context, "该通知无法打开", Toast.LENGTH_SHORT)
		// // .show();
		// // }
		// } else if (type.contains("客户联系记录")) {
		// // 此处动态包括，新客户联系记录和新客户联系记录评论
		// final 客户联系记录 contacts = item.getContacts();
		// // 设置为已读
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // 点击动态，设置为已读
		// zlServiceHelper.ReadDynamicById(id, context);
		// zlServiceHelper
		// .ReadContacts(contacts, context, handler);
		// }
		// }).start();
		// // if (contacts != null) {
		// // Intent intent = new Intent(context,
		// // ClientConstactInfoActivity.class);
		// // Bundle bundle = new Bundle();
		// // bundle.putSerializable(ClientConstactInfoActivity.TAG,
		// // contacts);
		// // intent.putExtras(bundle);
		// // startActivity(intent);
		// // } else {
		// // Toast.makeText(context, "该客户联系记录评论无法打开", Toast.LENGTH_SHORT)
		// // .show();
		// // }
		// }
		// // else if (type.contains("添加客户")) {
		// // 客户 client = BoeryunTypeMapper.MapperTo客户(item.Client);
		// // // 客户转化为 中文属性
		// // Intent intent = new Intent(context, ClientInfoNewActivity.class);
		// // Bundle bundle = new Bundle();
		// // bundle.putSerializable(ClientInfoNewActivity.TAG, client);
		// // intent.putExtras(bundle);
		// // startActivity(intent);
		// // } else if (type.contains("审批流程") || item.DataType == 4
		// // || item.DataType == 22) {
		// // 流程 workFlow = item.WorkFlow;
		// // Intent intent = new Intent();
		// // Bundle bundle = new Bundle();
		// // bundle.putSerializable("flow", workFlow);
		// // bundle.putBoolean("isEdit", false);
		// // intent.putExtras(bundle);
		// // intent.setClass(context, CreateVmFormActivity.class);
		// // startActivity(intent);
		// }
		// }
	}

	private void startTaskIntent(任务 item) {
		Intent intent = new Intent(context, TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(TaskInfoActivity.TAG, item);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void startLogIntent(日志 item) {
		// 如果日志时间为当天，并且日志发布人是自己,可编辑
		if (!TextUtils.isEmpty(item.Time)
				&& !DateDeserializer.dateIsBeforoNow(item.Time)
				&& Global.mUser.Id.equals(item.Personnel + "")) {
			String content = TextUtils.isEmpty(item.getContent()) ? "" : item
					.getContent();
			Intent intent = new Intent(context, WorkLogActivity.class);
			// TODO
			intent.putExtra("logContent", content);
			startActivity(intent);
		} else {
			Intent intent = new Intent(context, WorkLogActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("Log", item);
			LogUtils.i("keno2", "id:" + item.Id);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
}
