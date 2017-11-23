package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cedarhd.adapter.DynamicAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
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
 * 动态提醒窗口挂件Activity
 * 
 * @author kjx
 * @since 2014/10/16 18:56
 */
public class RemindDialogActivity extends BaseActivity {
	public static final String TAG = "RemindDialogActivity";
	public static final String TAG2 = "RemindDialogActivity2";
	public static final String COUNT_REMIND = "RemindDialogActivity_COUNT";

	private ImageView ivQuit;
	private Context context;
	private ListView lv;
	private MyProgressBar mPbar;
	private DynamicAdapter mListAdapter;
	private ZLServiceHelper zlServiceHelper;
	private ORMDataHelper ormDataHelper;
	private int coutDynamic;
	private 动态 mDynamic = new 动态();
	private 动态 mDynamic2 = new 动态();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind_dialog);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Bundle bundle = getIntent().getExtras();
		mDynamic = (动态) bundle.get(TAG);
		coutDynamic = bundle.getInt(COUNT_REMIND);
		if (coutDynamic >= 2) {
			mDynamic2 = (动态) bundle.get(TAG2);
		}
		findViews();
		initView();
		LogUtils.i("DynamicNews", "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i("DynamicNews", "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.i("DynamicNews", "onPause");
	}

	private void findViews() {
		lv = (ListView) findViewById(R.id.lv_remind_dialog);
		mPbar = (MyProgressBar) findViewById(R.id.pbar_remind_dialog);
	}

	private void initView() {
		context = RemindDialogActivity.this;
		zlServiceHelper = new ZLServiceHelper();
		ormDataHelper = ORMDataHelper.getInstance(context);
		Demand demand = new Demand();
		demand.表名 = "";
		demand.用户编号 = "";
		demand.方法名 = "dynamic/getDynamicList";
		demand.条件 = "";
		demand.附加条件 = "isnull(已读)";
		demand.每页数量 = 20;
		demand.偏移量 = 0;

		mPbar.setVisibility(View.VISIBLE);
		new QueryDynamicTask().execute(demand);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				动态 item动态 = mListAdapter.getItem(position);
				startNewActivitys(item动态);
				finish();
			}
		});

		ivQuit = (ImageView) findViewById(R.id.btn_quit);
		ivQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
		final int id = item.Id;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 点击动态，设置为已读
				zlServiceHelper.ReadDynamicById(id, context);
			}
		}).start();
		String type = item.getType();
		if (!TextUtils.isEmpty(type)) {
			if (type.contains("任务")) {
				final 任务 task = item.getTask();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadTask(task, context, handler);
					}
				}).start();
				if (task != null) {
					startTaskIntent(task);
				} else {
					Toast.makeText(context, "该任务无法打开", Toast.LENGTH_SHORT)
							.show();
				}

			} else if (type.contains("日志")) {
				final 日志 log = item.getLog();// 设置为已读
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadLog(log, context, handler);
					}
				}).start();
				if (log != null) {
					startLogIntent(log);
				} else {
					Toast.makeText(context, "该日志无法打开", Toast.LENGTH_SHORT)
							.show();
				}

			} else if (type.contains("通知")) {
				final 通知 notice = item.getNotice();
				// 设置为已读
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 点击动态，设置为已读
						zlServiceHelper.ReadDynamicById(id, context);
						zlServiceHelper.ReadNotice(notice, context, handler);
					}
				}).start();
				if (notice != null) {
					Intent intent = new Intent(context, NoticeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("Notice", notice);
					intent.putExtras(bundle);
					startActivity(intent);

				} else {
					Toast.makeText(context, "该通知无法打开", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (type.contains("客户联系记录")) {
				// 此处动态包括，新客户联系记录和新客户联系记录评论
				final 客户联系记录 contacts = item.getContacts();

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
				}

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
				bundle.putSerializable("flow", workFlow);
				intent.putExtras(bundle);
				intent.setClass(context, CreateVmFormActivity.class);
				startActivity(intent);
			}
		}
	}

	private void startTaskIntent(任务 item) {
		Intent intent = new Intent(context, TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("tasklist", item);
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
			Intent intent = new Intent(context,
					WorkLogListFragmentActivity.class);
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

	private class QueryDynamicTask extends AsyncTask<Demand, Void, List<动态>> {
		@Override
		protected List<动态> doInBackground(Demand... demand) {
			// ServerCall serverCall = new ServerCall();
			// JsonUtils jsonUtils = new JsonUtils();
			// String data = serverCall.makeServerCalll_Post(demand[0]);
			// List<动态> list = jsonUtils.ConvertJsonToList_动态(data);
			List<动态> list = new ArrayList<动态>();
			list.add(mDynamic);
			if (coutDynamic >= 2) {
				list.add(mDynamic2);
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<动态> result) {
			super.onPostExecute(result);
			mPbar.setVisibility(View.GONE);
			mListAdapter = new DynamicAdapter(result, context);
			lv.setAdapter(mListAdapter);
		}
	}
}
