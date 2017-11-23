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
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.DiscussListHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.models.评论;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表
 * 
 * @author Administrator
 * 
 */
public class ClientContactDiscussActivity extends BaseActivity {
	private LinearLayout ll_discuss;
	private ListView lv_discuss;
	private DiscussListHelper discussListHelper;
	List<评论> listDiscuss = new ArrayList<评论>();
	private ZLServiceHelper zlServiceHelper;
	private HandlerNewContact handler;
	private Context context;
	private 客户联系记录 item;
	public static final String TAG = "ClientContactDiscussActivity";
	private ImageView ivcancel;
	private ImageView ivnew;

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
				listDiscuss = (List<评论>) msg.obj;
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
		setContentView(R.layout.discuss_constact);
		Bundle bundle = getIntent().getExtras();
		item = (客户联系记录) bundle.get(ClientConstactInfoActivity.TAG);
		findviews();
		if (discussListHelper == null) {
			discussListHelper = new DiscussListHelper(
					ClientContactDiscussActivity.this, listDiscuss, lv_discuss,
					ll_discuss);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.getContactsDiscuss(item.getId() + "",
							handler);
				} catch (Exception e) {
					Toast.makeText(context, "客户评论异常", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}).start();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ll_discuss = (LinearLayout) findViewById(R.id.rl_discuss_content_constact_info);
		lv_discuss = (ListView) findViewById(R.id.lv_discuss_constact_info);
		zlServiceHelper = new ZLServiceHelper();
		context = ClientContactDiscussActivity.this;
		handler = new HandlerNewContact();
		ivcancel = (ImageView) findViewById(R.id.ivCance_contact1);
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivnew = (ImageView) findViewById(R.id.ivDone_contact1);
		ivnew.setVisibility(View.GONE);
	}
}
