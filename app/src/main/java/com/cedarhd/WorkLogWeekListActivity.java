package com.cedarhd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperKjx;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.models.ListViewLoadType;
/*
 * @author py 2014.9.1
 */

/** 已废弃，采用Fragment的形式 */
@Deprecated
public class WorkLogWeekListActivity extends BaseActivity {
	PullToRefreshListView mListView;
	private MyProgressBar mProgressBar;
	private RelativeLayout rl_choose; // 选择员工
	private RelativeLayout rl_choose_me;
	private ImageView ivcancel;
	private ImageView ivnew;
	public static final int REQUEST_CODE_SELECT_ID = 2;
	private ListViewHelperKjx mListViewHelperKjx = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workloglist_week);
		findviews();
		setonclicklistener();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		mListView = (PullToRefreshListView) findViewById(R.id.lv_worklist_week);
		mProgressBar = (MyProgressBar) findViewById(R.id.progress_worklist_week);
		rl_choose = (RelativeLayout) findViewById(R.id.rl_choose_workloglist_week);
		rl_choose_me = (RelativeLayout) findViewById(R.id.rl_choose_me_workloglist_week);
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel);
		ivnew = (ImageView) findViewById(R.id.imageViewNew);
	}

	private void setonclicklistener() {
		// TODO Auto-generated method stub
		rl_choose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到选择员工的Activity
				Intent intent = new Intent(WorkLogWeekListActivity.this,
						User_SelectActivityNew.class);
				intent.putExtra(User_SelectActivityNew.SELECT_EMPLOYEE, true);
				startActivityForResult(intent, REQUEST_CODE_SELECT_ID);
			}
		});
		rl_choose_me.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转到选择员工的Activity
				// value = Global.mUser.Id;
				// setTitle();
				// queryDemand.eqDemand.put("Personnel", value);
				// demand.用户编号 = value;
				// reload();
			}
		});
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WorkLogWeekListActivity.this,
						WorkLogWeekNewActivity.class);
				startActivity(intent);
			}
		});
		mListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// 还没实例化mListViewHelperKjx
						mListViewHelperKjx.mListViewLoadType = ListViewLoadType.顶部视图;
						try {
							mListViewHelperKjx.loadServerData(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WorkLogWeekListActivity.this,
						WorkLogWeekInfoActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_ID) {
		}
	}
}
