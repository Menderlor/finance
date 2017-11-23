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
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.帖子;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

public class CompanySpaceInputDiscussActivity extends BaseActivity {
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
	private 帖子 m帖子;
	private HandlerNewContact mHandlerNewContact = new HandlerNewContact();
	private String time = "";
	private String id = "";

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public static 论坛回帖 furom;

	public class HandlerNewContact extends Handler {
		public static final int UPDATE_Reply_SUCCESS = 2;
		public static final int UPDATE_Reply_FAILED = 3;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_Reply_SUCCESS:
				MessageUtil.ToastMessage(context, "回复成功！");
				Toast.makeText(CompanySpaceInputDiscussActivity.this, "回复成功!",
						Toast.LENGTH_SHORT).show();
				furom = new 论坛回帖(neirong, Global.mUser.UserName);
				// CompanySpaceListViewAdapter.mList.get(positon).ReplyList
				// .add(furom);
				finish();
				// CompanySpaceListViewAdapter.adapter.notifyDataSetChanged();
				break;
			case UPDATE_Reply_FAILED:
				MessageUtil.ToastMessage(context, "回复失败！");
				Toast.makeText(CompanySpaceInputDiscussActivity.this, "回复失败！",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		}
	}

	int positon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_content);
		flag = getIntent().getBooleanExtra(EDITECONTENT, false);
		Bundle bundle = getIntent().getExtras();
		m帖子 = (帖子) bundle.get("bundle");
		positon = bundle.getInt("position");
		findviews();
		setonclicklistener();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		ivcancel = (ImageView) findViewById(R.id.ivCancel_taskinfo_content);
		ivsubmit = (ImageView) findViewById(R.id.ivSubmit_taskinfo_content);
		etContent = (EditText) findViewById(R.id.etContent_taskinfo_content);
		speek = (Button) findViewById(R.id.btn_speek2_taskinfo_content);
		context = CompanySpaceInputDiscussActivity.this;
		zlServiceHelper = new ZLServiceHelper();
		time = ViewHelper.getDateString();
	}

	private String result;
	private String neirong;

	private void setonclicklistener() {
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ivsubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = etContent.getText().toString();
				neirong = content;
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								result = zlServiceHelper.publishRepaly(m帖子.Id,
										content);
								LogUtils.i("pycontent", content);
								LogUtils.i("pyid", m帖子.Id + "");
								if (result.contains("1")) {
									mHandlerNewContact
											.sendEmptyMessage(mHandlerNewContact.UPDATE_Reply_SUCCESS);
								} else {
									mHandlerNewContact
											.sendEmptyMessage(mHandlerNewContact.UPDATE_Reply_FAILED);
								}
							} catch (Exception e) {
							}
						}
					}).start();
				} else {
					Toast.makeText(CompanySpaceInputDiscussActivity.this,
							"回复内容不能为空", Toast.LENGTH_LONG).show();
				}
				finish();
			}
		});
		speek.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new SpeechDialogHelper(context,
						CompanySpaceInputDiscussActivity.this, etContent, true);
			}
		});

	}
}
