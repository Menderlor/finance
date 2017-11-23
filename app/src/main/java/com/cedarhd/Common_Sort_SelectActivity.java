package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cedarhd.adapter.Common_Sort_SelectListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelper;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.models.Demand;
import com.cedarhd.models.部门;

import java.util.ArrayList;
import java.util.List;

public class Common_Sort_SelectActivity extends BaseActivity {

	PullToRefreshListView mListView;
	// private ListViewOnScrollListener mListViewOnScrollListener = null;
	ListViewHelper mListViewHelper;
	MyProgressBar mProgressBar;
	// 上级Id
	List<String> mPIds;
	public static String mSort = "1";
	Demand mDemand = null;

	List<部门> m部门List;
	BaseAdapter simpleAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.common_sort_select);

		String tableName = "";
		String methodName = "";
		String filter = "上级=-1";
		String title = "分类选择";

		mPIds = new ArrayList<String>();

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			return;
		}

		if (bundle.keySet().contains("tableName")) {
			tableName = bundle.getString("tableName");
		}
		if (bundle.keySet().contains("methodName")) {
			methodName = bundle.getString("methodName");
		}
		if (bundle.keySet().contains("filter")) {
			filter = bundle.getString("filter");
		}
		if (bundle.keySet().contains("title")) {
			title = bundle.getString("title");
			setTitle(title + "选择");
		}

		SetDemand(tableName, methodName, filter);

		findViews();
		setOnClickListener();
		Init();
	}

	void SetDemand(String tableName, String methodName, String filter) {
		mDemand = new Demand();
		mDemand.表名 = tableName;
		mDemand.方法名 = methodName;
		mDemand.条件 = filter;
		mDemand.每页数量 = 20;
		mDemand.偏移量 = 0;
		mDemand.是否分页 = false;
	}

	void Init() {
		// Demand demand = new Demand();
		// demand.表名 = "任务分类";
		// demand.方法名 = "任务分类_分页";
		// demand.条件 = "上级=-1";
		// demand.每页数量 = "20";
		// demand.偏移量 = "0";
		if (mDemand == null) {
			return;
		}

		// List<HashMap<String, Object>> hashMaplst = new
		// ArrayList<HashMap<String, Object>>();
		m部门List = new ArrayList<部门>();
		simpleAdapter = GetSimpleAdapter(m部门List);
		mListView.setAdapter(simpleAdapter);

		mListViewHelper = new ListViewHelper(this, 部门.class,
				Common_Sort_SelectActivity.this, mDemand, mListView, m部门List,
				simpleAdapter, mProgressBar, 40);

		refresh();
	}

	void refresh() {
		mListViewHelper.loadLocalData();
	}

	void RefreshData(String str) {

		m部门List.clear();
		mListViewHelper.loadLocalData();
		// mListViewOnScrollListener.InitData();
	}

	public void setOnClickListener() {

		Button buttonCancel_C = (Button) findViewById(R.id.buttonCancel_C);
		buttonCancel_C.setText("返回上级" + mDemand.表名);
		buttonCancel_C.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPIds.size() > 0) {
					RefreshData("上级 = " + mPIds.get(0));
					mPIds.remove(0);
				}
			}
		});

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView listView = (ListView) parent;
				部门 map = (部门) listView.getItemAtPosition(position);

				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putString("SortId", map.get编号() + "");
				b.putString("SortName", map.get名称());
				i.putExtras(b);
				Common_Sort_SelectActivity.this
						.setResult(
								User_SelectActivityNew_zmy.COMMON_SORT_SELECT_RESULT_CODE,
								i);
				Common_Sort_SelectActivity.this.finish();
			}
		});
	}

	public void findViews() {
		mListView = (PullToRefreshListView) findViewById(R.id.listView1);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress);
	}

	private View.OnClickListener myAdapterCBListener = new OnClickListener() {
		public void onClick(View v) {
			Button button = (Button) v;
			@SuppressWarnings("unchecked")
			部门 map = (部门) button.getTag();

			switch (button.getId()) {
			case R.id.button1:
				mPIds.add(0, map.get上级() + "");
				RefreshData("上级 = " + map.get编号());
				break;
			case R.id.button2:
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("SortId", map.get编号() + "");
				bundle.putString("SortName", map.get名称());
				intent.putExtras(bundle);
				Common_Sort_SelectActivity.this
						.setResult(
								User_SelectActivityNew_zmy.COMMON_SORT_SELECT_RESULT_CODE,
								intent);
				Common_Sort_SelectActivity.this.finish();
				break;
			default:
				;
			}
		}
	};

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		Common_Sort_SelectActivity.this.setResult(
				User_SelectActivityNew_zmy.COMMON_SORT_SELECT_RESULT_CODE,
				intent);
		Common_Sort_SelectActivity.this.finish();
	};

	BaseAdapter GetSimpleAdapter(List<部门> list) {
		Common_Sort_SelectListViewAdapter mCommon_Sort_SelectListViewAdapter = new Common_Sort_SelectListViewAdapter(
				Common_Sort_SelectActivity.this,
				R.layout.common_sort_select_listviewlayout, list,
				myAdapterCBListener);
		return mCommon_Sort_SelectListViewAdapter;
	}
	//
	//
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // 这里重写返回键
	// RefreshData("上级 = " + Pid);
	// return true;
	// }
	// return false;
	// }

}