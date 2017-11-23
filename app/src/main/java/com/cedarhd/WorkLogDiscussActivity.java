package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.LogDiscussListHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.日志;
import com.cedarhd.models.日志评论;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class WorkLogDiscussActivity extends BaseActivity {
	private LinearLayout ll_discuss;
	private ListView lv_discuss;
	// private DiscussListHelper discussListHelper;
	List<日志评论> listDiscuss = new ArrayList<日志评论>();
	private ZLServiceHelper zlServiceHelper;
	private HandlerNewContact handler;
	private Context context;
	private 日志 mLog;
	public static final String TAG = "WorkLogDiscussActivity";
	private ImageView ivcancel;
	private ImageView ivnew;
	private LogDiscussListHelper discussListHelper;

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
			if (msg.what == GET_DISCUSS_SUCCESS) {// 获得评论列表成功
				listDiscuss = (List<日志评论>) msg.obj;
				// 显示评论内容
				discussListHelper.setmList(listDiscuss);
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				ll_discuss.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discuss_worklog);
		Bundle bundle = this.getIntent().getExtras();
		mLog = (日志) bundle.getSerializable("Logdiscuss");
		LogUtils.i("py日志", mLog.toString());
		findviews();
		if (discussListHelper == null) {
			discussListHelper = new LogDiscussListHelper(context, listDiscuss,
					lv_discuss, ll_discuss);
		}

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// zlServiceHelper.getDiscuss(mLog.getId(), handler);
		// }
		// }).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				zlServiceHelper.getLogDiscuss(mLog.getId() + "", handler);
			}
		}).start();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ll_discuss = (LinearLayout) findViewById(R.id.rl_discuss_content_worklog_info);
		lv_discuss = (ListView) findViewById(R.id.lv_discuss_worklog_info);
		zlServiceHelper = new ZLServiceHelper();
		context = WorkLogDiscussActivity.this;
		handler = new HandlerNewContact();
		ivcancel = (ImageView) findViewById(R.id.ivCancel_worklog);
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew = (ImageView) findViewById(R.id.ivDone_worklog);
		ivnew.setVisibility(View.GONE);
	}
}
