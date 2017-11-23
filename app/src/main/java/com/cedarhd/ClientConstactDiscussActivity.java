package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.客户联系记录;
import com.cedarhd.utils.MessageUtil;

public class ClientConstactDiscussActivity extends BaseActivity {
	private ImageView ivcancel;
	private ImageView ivsubmit;
	private EditText etContent;
	// private ImageView board;
	private Button speek;
	private Context context;
	public static final String EDITECONTENT = "EditContent";
	private boolean flag = false; // 标志位：判断是否是其他页面打开
	public static final String Content = "content";
	private ZLServiceHelper zlServiceHelper;
	private 客户联系记录 item;
	private HandlerNewContact mHandlerNewContact = new HandlerNewContact();

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		public static final int UPDATE_Contact_SUCCESS = 2;
		public static final int UPDATE_Contact_FAILED = 3;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPDATE_Contact_SUCCESS) { // 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							zlServiceHelper.getContactsDiscuss(item.getId()
									+ "", mHandlerNewContact);
						} catch (Exception e) {
							Toast.makeText(context, "评论异常", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}).start();
				String orderNo = item.getId() + "";
				zlServiceHelper.getDiscuss(orderNo, mHandlerNewContact);
			}
			if (msg.what == UPDATE_Contact_FAILED) {
				MessageUtil.ToastMessage(context, "评论失败！");
			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_content);
		flag = getIntent().getBooleanExtra(EDITECONTENT, false);
		Bundle bundle = getIntent().getExtras();
		item = (客户联系记录) bundle.get(ClientConstactInfoActivity.TAG);
		findviews();
		setonclicklistener();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ivcancel = (ImageView) findViewById(R.id.ivCancel_taskinfo_content);
		ivsubmit = (ImageView) findViewById(R.id.ivSubmit_taskinfo_content);
		etContent = (EditText) findViewById(R.id.etContent_taskinfo_content);
		// board = (ImageView) findViewById(R.id.iv_keybord2_taskinfo_content);
		speek = (Button) findViewById(R.id.btn_speek2_taskinfo_content);
		context = ClientConstactDiscussActivity.this;
		zlServiceHelper = new ZLServiceHelper();
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
		ivsubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = etContent.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								zlServiceHelper.publishContactsDiscuss(item.Id
										+ "", content, mHandlerNewContact);
							} catch (Exception e) {
								Toast.makeText(context, "发表评论异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				} else {
					Toast.makeText(ClientConstactDiscussActivity.this,
							"评论内容不能为空", Toast.LENGTH_LONG).show();
				}
				// rlPublishDiscuss.setVisibility(View.GONE);

				finish();
			}
		});
		speek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new SpeechDialogHelper(context,
						ClientConstactDiscussActivity.this, etContent, true);
			}
		});

	}
}
