package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.DiscussListHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.评论;
import com.cedarhd.utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;

/** 已废弃，采用Fragment的形式 */
@Deprecated
public class WorkLogMonthInfoActivity extends BaseActivity {
	private ImageView ivcancel;
	private ImageView ivnew;
	private TextView tv_time_month;
	private TextView tv_employee_month;
	private EditText et_finish_month;
	private EditText et_unfinished_month;
	private EditText et_plan_month;
	private EditText et_summary_month;
	private RelativeLayout rlPublishDiscuss; // 发表评论区域
	private Button btnPublishDiscuss;// 发表评论按钮
	private EditText etDiscussContent;// 评论内容输入区
	private LinearLayout rlDiscussContent; // 评论内容显示区
	private ListView lvDiscuss; // 评论列表
	List<评论> listDiscuss = new ArrayList<评论>();
	private ImageView ivQuitDiscuss; // 取消评论
	private ImageView ivPublishDiscuss;// 发表评论按钮
	private LinearLayout llDiscuss;// 评论区
	private EditText etDiscuss;
	private Button btnDiscussCount;
	private Context context;
	private DiscussListHelper discussListHelper;
	private final int ID_TV_MORE = 101;// 查看更多的id

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		// public static final int GET_LOG_NOW_SUCCESS = 0;
		// public static final int GET_LOG_NOW_FAILED = 1;
		public static final int UPDATE_Contact_SUCCESS = 2;
		public static final int UPDATE_Contact_FAILED = 3;
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPDATE_Contact_SUCCESS) { // 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				// finish();
				// 重新加载评论列表
				// discussListHelper.setmList(listDiscuss);
				llDiscuss.setVisibility(View.VISIBLE);
				// btnDiscuss.setVisibility(View.VISIBLE);
				// addHeader();
				if (discussListHelper == null) {
					discussListHelper = new DiscussListHelper(context,
							listDiscuss, lvDiscuss, rlDiscussContent);
				}

				// new Thread(new Runnable() {
				// @Override
				// public void run() {
				// zlServiceHelper.getLogDiscuss(mLog.getId() + "",
				// handler);
				// }
				// }).start();
				// String orderNo = mLog.getId() + "";
				// zlServiceHelper.getLogDiscuss(orderNo, handler);
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				MessageUtil.ToastMessage(context, "修改失败！");
			}
			if (msg.what == GET_DISCUSS_SUCCESS) {// 获得评论列表成功
				etDiscussContent.setText("");
				listDiscuss = (List<评论>) msg.obj;
				// 显示评论内容
				rlDiscussContent.setVisibility(View.VISIBLE);
				if (listDiscuss.size() == 0) {
					rlDiscussContent.setVisibility(View.GONE);
				}
				discussListHelper.setmList(listDiscuss);
				btnDiscussCount.setText("评论" + listDiscuss.size() + "");
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				rlDiscussContent.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_log_month_info);
	}

	private void init() {
		// TODO Auto-generated method stub
		llDiscuss.setVisibility(View.VISIBLE);
		// btnDiscuss.setVisibility(View.VISIBLE);
		addHeader();
		// if (discussListHelper == null) {
		// discussListHelper = new LogDiscussListHelper(context, listDiscuss,
		// lvDiscuss, rlDiscussContent);
		// }

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// zlServiceHelper.getLogDiscuss(mLog.getId() + "", handler);
		// }
		// }).start();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel_month);
		ivnew = (ImageView) findViewById(R.id.imageViewDone_month);
		tv_time_month = (TextView) findViewById(R.id.tv_time_month);
		tv_employee_month = (TextView) findViewById(R.id.tv_employee_month);
		et_finish_month = (EditText) findViewById(R.id.et_finish_month);
		et_unfinished_month = (EditText) findViewById(R.id.et_unfinished_month);
		et_plan_month = (EditText) findViewById(R.id.et_plan_month);
		et_summary_month = (EditText) findViewById(R.id.et_summary_month);
		context = getApplicationContext();
		rlPublishDiscuss = (RelativeLayout) findViewById(R.id.rl_publich_discuss_log_info_month);
		btnPublishDiscuss = (Button) findViewById(R.id.btn_publich_discuss_log_info_month);
		etDiscussContent = (EditText) findViewById(R.id.et_discuss_content_log_info_month);
		rlDiscussContent = (LinearLayout) findViewById(R.id.rl_discuss_content_info_log_month);
		lvDiscuss = (ListView) findViewById(R.id.lv_discuss_log_month);
		ivQuitDiscuss = (ImageView) findViewById(R.id.iv_discuss_quit_log_info_month);// 退出评论
		ivPublishDiscuss = (ImageView) findViewById(R.id.iv_discuss_submit_log_info_month); // 发表评论
		llDiscuss = (LinearLayout) findViewById(R.id.ll_discuss_log_month);
		etDiscuss = (EditText) findViewById(R.id.et_work_log_discuss_month);
		etDiscuss.setFocusable(false);
		btnDiscussCount = (Button) findViewById(R.id.btn_work_log_discuss_month);
	}

	private void setonclicklistener() {
		// TODO Auto-generated method stub
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

			}
		});
		ivQuitDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.GONE);
				// rlDiscuss.setVisibility(View.VISIBLE);
			}
		});
		ivPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// rlDiscuss.setVisibility(View.VISIBLE);
				final String content = etDiscussContent.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					// new Thread(new Runnable() {
					// @Override
					// public void run() {
					// zlServiceHelper.publishLogDiscuss(mLog.Id + "",
					// content, handler);
					// }
					// }).start();
				} else {
					Toast.makeText(WorkLogMonthInfoActivity.this, "评论内容不能为空",
							Toast.LENGTH_LONG).show();
				}
				rlPublishDiscuss.setVisibility(View.GONE);
				// rlDiscuss.setVisibility(View.VISIBLE);
			}
		});
		etDiscuss.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				rlPublishDiscuss.setVisibility(View.VISIBLE);
				// InputMethodManager imm = (InputMethodManager) etDiscuss
				// .getContext().getSystemService(INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				// rlDiscuss.setVisibility(View.GONE);
			}
		});
	}

	private void addHeader() {
		TextView tvHeader = new TextView(context);
		tvHeader.setId(ID_TV_MORE);
		tvHeader.setTextColor(0xFF28B69B);
		tvHeader.setTextSize(14);
		tvHeader.setBackgroundColor(0xFFEEEEEE);
		tvHeader.setClickable(true);
		tvHeader.setText("查看更多评论");
		AbsListView.LayoutParams tvparams = new AbsListView.LayoutParams(
				Global.mWidthPixels, (int) ViewHelper.dip2px(context, 35));
		tvHeader.setPadding(20, 20, 0, 0);
		tvHeader.setLayoutParams(tvparams);
		lvDiscuss.addHeaderView(tvHeader);
		tvHeader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// int height = discussListHelper.getHeight();
				// Log.i("height", "height:" + height);
				// LinearLayout.LayoutParams params = new
				// LinearLayout.LayoutParams(
				// LayoutParams.FILL_PARENT, height);
				// rlDiscussContent.setLayoutParams(params);
				Intent intent = new Intent(WorkLogMonthInfoActivity.this,
						WorkLogDiscussActivity.class);
				Bundle bundle = new Bundle();
				// bundle.putSerializable("Logdiscuss", mLog);
				// Log.i("keno2", "id:" + mLog.Id);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
}
