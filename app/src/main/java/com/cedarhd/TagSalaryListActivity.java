package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.SalaryListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.考勤信息;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤列表
 */
public class TagSalaryListActivity extends BaseActivity {
	private PullToRefreshListView mListView;
	private ImageView mimageViewCancel;
	// private TextView tv_choose;
	private TextView tv_title;
	private RelativeLayout rl_choose;
	private List<考勤信息> m考勤信息list;
	private SalaryListViewAdapter mListAdapter;
	private MyProgressBar mProgressBar;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	// private ListViewHelper mListViewHelper = null;
	// private ListViewHelperKjx mListViewHelperKjx = null;
	private ListViewHelperNet<考勤信息> mListViewHelperNet;
	private QueryDemand queryDemand; // 本地查询条件
	Demand demand;

	public static final int REQUEST_CODE_SELECT_ID = 2;

	private String value = "";// 查询数据库的字段值
	public static boolean isConnectedInternet; // 是否连接了网络

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_tagsalarylist);
		findViews();
		setOnClickListener();

		m考勤信息list = new ArrayList<考勤信息>();
		demand = new Demand();
		demand.用户编号 = "";
		demand.表名 = "考勤信息";
		demand.方法名 = "Attendance/GetAttendanceList/";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 40;
		demand.偏移量 = 0;
		// queryDemand.fildName = "考勤日期";
		// queryDemand.sortFildName = "AttendanceDate";
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "UpdateTime";
		queryDemand.localFildName = "UpdateTime";
		mListAdapter = new SalaryListViewAdapter(TagSalaryListActivity.this,
				R.layout.tagsalarylist_listviewlayout, m考勤信息list, null);
		mListView.setAdapter(mListAdapter);
		mListViewHelperNet = new ListViewHelperNet<考勤信息>(this, 考勤信息.class,
				demand, mListView, m考勤信息list, mListAdapter, mProgressBar,
				queryDemand);
		mListViewHelperNet.loadServerData(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 加载数据
	 * 
	 * @param columnName
	 *            查询本地数据库的字段名
	 * @param value
	 *            查询本地数据库的字段值
	 */
	private void reload() {
		isConnectedInternet = HttpUtils
				.IsHaveInternet(TagSalaryListActivity.this);
		if (!isConnectedInternet) {
			Toast.makeText(TagSalaryListActivity.this,
					"需要连接到3G或者wifi因特网才能获取最新信息！", Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			m考勤信息list.clear();
			mListViewHelperNet.loadServerData(true);
		}
	}

	public void findViews() {
		queryDemand = new QueryDemand();
		mListView = (PullToRefreshListView) findViewById(R.id.listView_tagsalarylist_show);
		mimageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress);
		rl_choose = (RelativeLayout) findViewById(R.id.rl_choose_taglist);
		tv_title = (TextView) findViewById(R.id.tv_title_tag);
	}

	public void setOnClickListener() {
		mimageViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		rl_choose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到选择员工的Activity
				Intent intent = new Intent(TagSalaryListActivity.this,
						User_SelectActivityNew_zmy.class);
				intent.putExtra(User_SelectActivityNew_zmy.SELECT_EMPLOYEE,
						true);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ID);
			}
		});

		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
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

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView listView = (ListView) parent;
				final 考勤信息 map = (考勤信息) listView.getItemAtPosition(position);
				Intent intent = new Intent(TagSalaryListActivity.this,
						TagSalaryInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Attendence", map);
				intent.putExtras(bundle);
				startActivity(intent);

				mListAdapter.getDataList().get(position - 1).Read = ViewHelper
						.getDateString();
				mListAdapter.notifyDataSetChanged();
				// 读任务
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.ReadDynamic(map.Id, 3);
						} catch (Exception e) {
							LogUtils.e("erro", "" + e);
						}
					}
				}).start();
			}
		});
	}

	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_ID) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			String mUserSelectId = bundle.getString("UserSelectId");
			String mUserSelectName = bundle.getString("UserSelectName");
			if (!TextUtils.isEmpty(mUserSelectName)) {
				// String[] date = mUserSelectId.split("'");
				// value = date[1];// 只能取得一个用户的id
				// 取到编号 是 "85"这样的数字
				value = mUserSelectId.replace("'", "");
				DictionaryHelper dictionaryHelper = new DictionaryHelper(
						TagSalaryListActivity.this);
				tv_title.setText(dictionaryHelper.getUserNameById(value)
						+ "的考勤");
				queryDemand.eqDemand.clear();
				queryDemand.eqDemand.put("Employee", value);
				demand.用户编号 = value;
				reload();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper
							.deleteTempTagInfo(TagSalaryListActivity.this);
					zlServiceHelper.deleteTempData(TagSalaryListActivity.this,
							考勤信息.class, "UpdateTime", true);
				} catch (Exception e) {
					Toast.makeText(TagSalaryListActivity.this, "删除临时信息异常", 0)
							.show();
				}
			}
		}).start();
	}
}