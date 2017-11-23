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
import android.widget.Toast;

import com.cedarhd.ClientConstactInfoActivity;
import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.NoticeActivity;
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
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.任务;
import com.cedarhd.models.动态;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.日志;
import com.cedarhd.models.流程;
import com.cedarhd.models.通知;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 未读动态
 *
 * @author Administrator
 *
 */
@Deprecated
public class DynamicNoReadFragment extends Fragment {
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
		reload();
	}

	private void reload() {
		mListViewHelperNet.mDataList.clear();
		mListAdapter.notifyDataSetChanged();
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
					// mListViewHelperNet.loadServerData(true);
					reload();
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
		demand.附加条件 = "isnull(已读时间)";
		demand.每页数量 = 10;
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
				startNewActivitys(item动态);
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
	private void startNewActivitys(动态 item) {
		String type = item.getType();
		final int id = item.Id;
		if (!TextUtils.isEmpty(type)) {
			if (type.contains("任务")) {
				final 任务 task = item.getTask();
				if (task == null) {
					Toast.makeText(context, "该任务已删除", Toast.LENGTH_SHORT)
							.show();
					return;
				} else {
					startTaskIntent(task);
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadTask(task, context, handler);
					}
				}).start();
			} else if (type.contains("日志")) {
				final 日志 log = item.getLog();
				if (log != null) {
					startLogIntent(log);
				} else {
					Toast.makeText(context, "该日志无法打开", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				// 设置为已读
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadLog(log, context, handler);
					}
				}).start();
			} else if (type.contains("通知")) {
				final 通知 notice = item.getNotice();
				if (notice != null) {
					Intent intent = new Intent(context, NoticeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("Notice", notice);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Toast.makeText(context, "该通知无法打开", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				// 设置为已读
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadNotice(notice, context, handler);
					}
				}).start();

			} else if (type.contains("客户联系记录")) {
				// 此处动态包括，新客户联系记录和新客户联系记录评论
				final 客户联系记录 contacts = item.getContacts();
				if (contacts != null) {
					Intent intent = new Intent(context,
							ClientConstactInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ClientConstactInfoActivity.TAG,
							contacts);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Toast.makeText(context, "该客户联系记录评论无法打开", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				// 设置为已读
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper
								.ReadContacts(contacts, context, handler);
					}
				}).start();

			} else if (type.contains("审批流程")) {
				流程 workFlow = item.WorkFlow;
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", workFlow.ClassTypeId);// 115表示报销申请单
				bundle.putString("dataId", workFlow.FormDataId + "");
				bundle.putString("typeName", workFlow.FormName);
				bundle.putBoolean("isNotSubmit", true);
				if (workFlow.NextStepAudit != null
						&& workFlow.NextStepAudit.equals(Global.mUser.Id)) {
					bundle.putBoolean("isAudit", true);
				}
				intent.putExtras(bundle);
				intent.setClass(context, CreateVmFormActivity.class);
				startActivity(intent);
			}
		}
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
			// TODO 此處會有問題
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
