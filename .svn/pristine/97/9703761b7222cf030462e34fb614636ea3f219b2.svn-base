package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.cedarhd.adapter.SignListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelper;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.models.Demand;
import com.cedarhd.models.考勤信息;

import java.util.ArrayList;
import java.util.List;

public class SignListActivity extends BaseActivity {

	PullToRefreshListView mListView;
	List<考勤信息> m考勤信息List;
	BaseAdapter simpleAdapter;
	MyProgressBar mProgressBar;
	private ListViewHelper mListViewHelper = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.signlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_sign_main);

		findViews();
		setOnClickListener();
		Init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		simpleAdapter.notifyDataSetChanged();
		super.onResume();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // TODO Auto-generated method stub
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.menu_noticelist, menu);
	// return true;
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // TODO Auto-generated method stub
	// int item_id = item.getItemId();
	//
	// switch (item_id) {
	// case R.id.item_New:
	// New();
	// break;
	// case R.id.item_Search:
	// Search();
	// break;
	// case R.id.item_Refresh:
	// Refresh();
	// break;
	// case R.id.item_Cancel:
	// Back();
	// break;
	// default:
	// return false;
	// }
	// return true;
	// }

	void Init() {
		m考勤信息List = new ArrayList<考勤信息>();

		Demand demand = new Demand();
		demand.表名 = "考勤信息";
		demand.方法名 = "签到签退_分页";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;

		simpleAdapter = GetSimpleAdapter(m考勤信息List);
		mListView.setAdapter(simpleAdapter);

		mListViewHelper = new ListViewHelper(this, 考勤信息.class,
				SignListActivity.this, demand, mListView, m考勤信息List,
				simpleAdapter, mProgressBar, 40);

		Refresh();
	}

	public void setOnClickListener() {
		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Back();
				finish();
			}
		});

		// ImageView ImageViewNew = (ImageView) findViewById(R.id.imageViewNew);
		// ImageViewNew.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// New();
		// }
		// });

		// TODO
		// mListView.setOnRefreshListener(new OnPulldownRefreshListener() {
		// @Override
		// public void onPulldownRefresh() {
		// // Do work to refresh the list here.
		// mListViewHelper.mListViewLoadType = ListViewLoadType.顶部视图;
		// // Refresh();
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// mListView.onRefreshComplete();
		// }
		// });

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ListView listView = (ListView) parent;
				考勤信息 map = (考勤信息) listView.getItemAtPosition(position);
				Intent intent = new Intent(SignListActivity.this,
						SignActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Sign", map);
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});
	}

	public void findViews() {
		// mbuttonNew = (Button) findViewById(R.id.buttonNew);
		mListView = (PullToRefreshListView) findViewById(R.id.listView1);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress);
	}

	BaseAdapter GetSimpleAdapter(List<考勤信息> list) {
		SignListViewAdapter mNoticeListViewAdapter = new SignListViewAdapter(
				SignListActivity.this, R.layout.signlist_listviewlayout, list,
				null);
		return mNoticeListViewAdapter;
	}

	// void New() {
	// Intent intent = new Intent();
	// intent.setClass(this.getApplicationContext(), Notice_NewActivity.class);
	// startActivity(intent);
	// }

	void Search() {

	}

	void Refresh() {
		// mListViewHelper.getData();
		mListViewHelper.loadLocalData();
	}

	void Back() {
		Intent intent = new Intent();
		intent.setClass(this.getApplicationContext(), MenuNewActivity.class);
		startActivity(intent);
	}
}