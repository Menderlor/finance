package com.cedarhd;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.adapter.FlowListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelper;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ListViewLoadType;
import com.cedarhd.models.流程;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 流程和审批
 */
public class AskForLeaveListActivity extends BaseActivity {
	PullToRefreshListView mListView;
	MyProgressBar mProgressBar;
	TextView askforleave_Pending;
	TextView askforleave_apply;
	TextView askforleave_all;
	private AvartarView avartarView;// 头像控件
	private PopupWindow popupWindow;
	private View popupWindow_view;
	List<流程> m流程List;
	BaseAdapter mListAdapter;
	private ListViewHelper mListViewHelper = null;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private List<TextView> list_textviews;
	List<String> columnName = new ArrayList<String>();// 数据库的列名(相等关系,==)
	List<String> columnLikeName = new ArrayList<String>();// 数据库的列名(包含关系,like)
	private String value;
	private int typeId = 110;// 流程表单编号
	private int FormDataId = 0; // 表单编号
	public static final int REQUEST_CODE_NEW_FORM = 0;

	public final int REQUEST_CODE_ASKFOR_ME = 12;// 待我审批
	public static final int APPROVAL_RESULT_SUCCEED = 13;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.askforleavelist);

		list_textviews = new ArrayList<TextView>();
		columnName.add("NextStepAudit");
		value = Global.mUser.Id;

		findViews();
		setOnClickListener();

		m流程List = new ArrayList<流程>();

		Demand demand = new Demand();
		demand.表名 = "流程";
		// 之前的方法名
		// demand.方法名 = "查询_分页";
		// 方法名使用服务器的选择器
		demand.方法名 = "Flow/GetApprovalFlow/";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		mListAdapter = new FlowListViewAdapter(AskForLeaveListActivity.this,
				R.layout.askforleavelist_item, m流程List, null);
		mListView.setAdapter(mListAdapter);
		mListViewHelper = new ListViewHelper(this, 流程.class,
				AskForLeaveListActivity.this, demand, mListView, m流程List,
				mListAdapter, mProgressBar, 80);
		// reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		reload();
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
		m流程List.clear();
		mListViewHelper.loadLocalData(columnName, columnLikeName, value);
		mListViewHelper.loadServerData(true, value);
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		ImageViewNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPopupWindow();
				popupWindow.showAsDropDown(v);
				// createNotice();
			}
		});
		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mListViewHelper.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							// 下拉刷新 导入数据
							mListViewHelper.loadServerData(true, value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		// 待我审批 进入审批; 申请进入 查看申请详情
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView) parent;
				// 流程 map = (流程) listView.getItemAtPosition(position);
				if (position > 0) {
					position -= 1;
				}
				流程 map = m流程List.get(position);
				Intent intent = new Intent(AskForLeaveListActivity.this,
						CreateVmFormActivity.class);
				Bundle bundle = new Bundle();
				FormDataId = map.getFormDataId(); // 将申请id作为全局变量保存
				bundle.putInt("id", map.getId());
				bundle.putInt("typeId", map.getClassTypeId());
				// bundle.putInt("typeId", typeId);
				bundle.putString("dataId", "" + map.getFormDataId());
				bundle.putString("typeName", map.getClassTypeName());
				bundle.putString("columnName", columnName.get(0));
				bundle.putSerializable("flow", map);
				intent.putExtras(bundle);
				zlServiceHelper.ReadFlow(map, AskForLeaveListActivity.this);
				// startActivity(intent);
				startActivityForResult(intent, REQUEST_CODE_ASKFOR_ME);
			}
		});

		// 待我审批按钮监听
		askforleave_Pending.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				typeId = 110;
				m流程List.clear();
				Demand demand = new Demand();
				demand.表名 = "流程";
				demand.方法名 = "Flow/GetApprovalFlow/";
				demand.条件 = "";
				demand.附加条件 = "";
				demand.每页数量 = 10;
				demand.偏移量 = 0;
				mListViewHelper.setmDemand(demand);
				setBackgroud(0);
				// columnName = "NextStepAudit";
				columnName.clear();
				columnName.add("NextStepAudit");
				value = Global.mUser.Id;
				reload();
			}
		});

		// 我的申请按钮监听
		askforleave_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				typeId = 110;
				m流程List.clear();
				Demand demand = new Demand();
				demand.表名 = "流程";
				demand.方法名 = "Flow/GetApplyFlow/";
				demand.条件 = "";
				demand.附加条件 = "";
				demand.每页数量 = 10;
				demand.偏移量 = 0;
				mListViewHelper.setmDemand(demand);
				setBackgroud(1);
				// columnName="Create";
				columnName.clear();
				columnName.add("Create");
				value = Global.mUser.Id;
				reload();
			}
		});

		// 查询所有按钮监听
		askforleave_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m流程List.clear();
				Demand demand = new Demand();
				demand.表名 = "流程";
				demand.方法名 = "Flow/GetAllFlow/";
				demand.条件 = "";
				demand.附加条件 = "";
				demand.每页数量 = 10;
				demand.偏移量 = 0;
				mListViewHelper.setmDemand(demand);
				setBackgroud(2);
				columnName.clear();
				columnName.add("Create");
				columnName.add("NextStepAudit");
				value = Global.mUser.Id;
				reload();
			}
		});
	}

	/**
	 * 设置viewpager 标签的按钮背景色
	 * 
	 * @param position
	 */
	private void setBackgroud(int position) {
		for (int i = 0; i < list_textviews.size(); i++) {
			if (i == position) {
				list_textviews.get(i).setBackgroundColor(0xFFECEADE);
			} else {

				list_textviews.get(i).setBackgroundColor(0x00000000);
			}
		}
	}

	public void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listView1);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress);
		askforleave_Pending = (TextView) findViewById(R.id.askforleave_Pending);
		askforleave_apply = (TextView) findViewById(R.id.askforleave_apply);
		askforleave_all = (TextView) findViewById(R.id.askforleave_all);

		list_textviews.add(askforleave_Pending);
		list_textviews.add(askforleave_apply);
		list_textviews.add(askforleave_all);
		setBackgroud(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_CODE_NEW_FORM) {
			// if (resultCode == NoticeNewActivity.RESULT_CODE_SUCCESS) {
			// LogUtils.i("guojianwen",
			// "NoticeListActivity onActivityResult RESULT_CODE_SUCCESS");
			reload();
			// } else if (resultCode == NoticeNewActivity.RESULT_CODE_FAILED){
			// LogUtils.i("guojianwen",
			// "NoticeListActivity onActivityResult RESULT_CODE_FAILED");
			// }
		}
		if (requestCode == REQUEST_CODE_ASKFOR_ME) {
			if (resultCode == APPROVAL_RESULT_SUCCEED) {
				// 审批成功
				deleteItemById();
				mListAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/***
	 * 获取PopupWindow实例
	 */
	private void getPopupWindow() {

		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	/**
	 * 初始化PopupWindow
	 */
	protected void initPopuptWindow() {
		// 填充pop视图
		popupWindow_view = getLayoutInflater().inflate(R.layout.pop, null,
				false);
		popupWindow = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});

		LinearLayout askforleave_approve = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_approve);
		LinearLayout askforleave_leave = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_leave);
		LinearLayout askforleave_more = (LinearLayout) popupWindow_view
				.findViewById(R.id.askforleave_more);

		// 报销控件监听
		askforleave_approve.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", 115);// 115表示报销申请单
				bundle.putString("dataId", "0");
				bundle.putString("typeName", "报销单");
				bundle.putString("isPhoneForm", "True");
				intent.putExtras(bundle);
				intent.setClass(AskForLeaveListActivity.this,
						CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
				popupWindow.dismiss();
			}
		});

		// 请假控件监听
		askforleave_leave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", 110);// 110表示请假单
				bundle.putString("dataId", "0"); // data=0表示新建
				bundle.putString("typeName", "手机请假单");
				bundle.putString("isPhoneForm", "True");
				intent.putExtras(bundle);
				intent.setClass(AskForLeaveListActivity.this,
						CreateVmFormActivity.class);
				startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
				popupWindow.dismiss();
			}
		});

		// 更多控件监听
		askforleave_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// intent.setClass(AskForLeaveListActivity.this,
				// AskForLeaveMoreFormActivity.class);
				// startActivityForResult(intent, REQUEST_CODE_NEW_FORM);
				intent.setClass(AskForLeaveListActivity.this,
						AskForLeaveMoreFormFragmentActivity.class);
				startActivity(intent);
				popupWindow.dismiss();
			}
		});
	}

	private void deleteItemById() {
		ORMDataHelper ormDataHelper = ORMDataHelper
				.getInstance(AskForLeaveListActivity.this);
		try {
			Dao<流程, Integer> dao = ormDataHelper.getDao(流程.class);
			DeleteBuilder builder = dao.deleteBuilder();
			LogUtils.i("deleteresult", "FormDataId----->" + FormDataId);
			builder.where().eq("FormDataId", FormDataId);
			int deleteresult = builder.delete();
			LogUtils.i("deleteresult", "----->" + deleteresult);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}