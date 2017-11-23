package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.adapter.LogListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperKjx;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.WorkLog;
import com.cedarhd.models.日志;
import com.cedarhd.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试专用Activity
 * 
 * @author bohr
 * 
 */
public class TestActivity extends BaseActivity {
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private static final int SHOW_DATAPICKFrom = 0;
	private static final int SHOW_DATAPICKTo = 1;
	public static final int REQUEST_CODE_SELECT_ID = 2;

	private int mYear;
	private int mMonth;
	private int mDay;

	private Demand demand;
	private QueryDemand queryDemand; // 本地数据查询条件
	private String value;// 查询数据库的字段值

	private PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;

	java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	List<日志> m日志list;

	HandlerLog handler;

	private ListViewHelperKjx mListViewHelperKjx = null;

	private TextView tv_choose;// 选择人员的按钮

	public static final int REQUEST_CODE_LOG_NEW = 10;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// value = Global.mUser.Id;
		setContentView(R.layout.activity_test);
		handler = new HandlerLog();
		findViews();
		setOnClickListener();
		Init();
		reload();
	}

	void Init() {
		demand = new Demand();
		demand.用户编号 = value;
		demand.表名 = "员工日志";
		demand.方法名 = "Log/GetWorkLogList/";
		demand.条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		queryDemand = new QueryDemand();
		// queryDemand.eqDemand.put("Personnel", value);
		m日志list = new ArrayList<日志>();
		LogListViewAdapter listViewAdapter = new LogListViewAdapter(
				TestActivity.this, R.layout.loglist_listviewlayout, m日志list,
				null);
		mListView.setAdapter(listViewAdapter);
		mListViewHelperKjx = new ListViewHelperKjx(this, 日志.class,
				TestActivity.this, demand, queryDemand, mListView, m日志list,
				listViewAdapter, mProgressBar);
	}

	private void reload() {
		LogUtils.i("listviewr", "reload()");
		m日志list.clear();
		mListViewHelperKjx.loadServerData(true);
	}

	public void setOnClickListener() {
		tv_choose = (TextView) findViewById(R.id.tv_choose);
		tv_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到选择员工的Activity
				Intent intent = new Intent(TestActivity.this,
						User_SelectActivityNew.class);
				intent.putExtra(User_SelectActivityNew.SELECT_EMPLOYEE, true);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ID);
			}
		});
		//
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView imageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		imageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// New();
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						try {
							mListViewHelperKjx.mListViewLoadType = ListViewLoadType.顶部视图;
							mListViewHelperKjx.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView listView = (ListView) parent;
				日志 item = (日志) listView.getItemAtPosition(position);
				Intent intent = new Intent(TestActivity.this,
						WorkLogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Log", item);
				LogUtils.i("keno2", "id:" + item.Id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == -1) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String filter = bundle.getString("filter");

			// 查询
			// reload();
		}

		if (requestCode == REQUEST_CODE_LOG_NEW) {
			// if (resultCode == WorkLogNewActivity.RESULT_RETURN_SUCCESS) {
			// // reload();
			// }
		}

		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_ID) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String mUserSelectId = bundle.getString("UserSelectId");
			String mUserSelectName = bundle.getString("UserSelectName");
			if (!TextUtils.isEmpty(mUserSelectName)) {
				String[] date = mUserSelectId.split("'");
				value = date[1];// 只能取得一个用户的id
				queryDemand.eqDemand.put("Personnel", value);
				demand.用户编号 = value;
				reload();
			}
		}
	}

	public void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.lv_new_test);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress);
	}

	void Back() {
		finish();
	}

	public class HandlerLog extends Handler {
		public static final int GET_LOG_NOW_SUCCESS = 0;
		public static final int GET_LOG_NOW_FAILED = 1;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int whatMsg = msg.what;

			switch (whatMsg) {
			case GET_LOG_NOW_SUCCESS:
				Date curDate = new Date(System.currentTimeMillis());
				String strTime = format.format(curDate);
				日志 log = new 日志();
				List<WorkLog> list = (List<WorkLog>) msg.obj;
				if (list.size() > 0) {
					WorkLog e = list.get(0);
					log.setId(Integer.parseInt(e.Id));
					log.setTime(e.Time);
					log.setContent(e.Content);
					if (e.Id.equals("0")) {
						log.setPersonnel(Integer.parseInt(Global.mUser.Id));
						log.setPersonnelName(Global.mUser.UserName);
					} else {
						log.setPersonnel(e.Personnel);
						log.setPersonnelName(e.PersonnelName);
					}

					log.setClient(e.Client);
					log.setSuppliers(e.Suppliers);
					log.setProject(e.Project);
					log.setClientRecord(e.ClientRecord);
				} else {
					log.setId(0);
					// log.setTime(curDate);
					log.setTime(ViewHelper.getDateString());
					log.setPersonnel(Integer.parseInt(Global.mUser.Id));
				}

				Intent intent = new Intent(TestActivity.this,
						WorkLogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Log", log);
				intent.putExtras(bundle);
				startActivity(intent);
				break;
			case GET_LOG_NOW_FAILED:
				break;
			}
			super.handleMessage(msg);
		}
	}

}
