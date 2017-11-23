package com.cedarhd;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.utils.LogUtils;

/**
 * 设置新消息提醒
 * 
 * @author BOHR
 * 
 */
public class SettingNoticeActivity extends BaseActivity {

	private Context context;
	private SharedPreferences sp;
	private boolean notice;
	private boolean email;
	private boolean log;
	private boolean client;
	private boolean order;
	private boolean contact;
	private boolean task;
	private boolean saleChance;
	private boolean approval;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_notice);
		findViews();
	}

	private void findViews() {
		final ImageView ivCancel = (ImageView) findViewById(R.id.iv_cancel_setting);
		final ImageView ivNotice = (ImageView) findViewById(R.id.iv_notice_remind_switch);
		final ImageView ivEmail = (ImageView) findViewById(R.id.iv_email_remind_switch);
		final ImageView ivLog = (ImageView) findViewById(R.id.iv_log_remind_switch);
		final ImageView ivClient = (ImageView) findViewById(R.id.iv_client_remind_switch);
		final ImageView ivOrder = (ImageView) findViewById(R.id.iv_order_remind_switch);
		final ImageView ivContact = (ImageView) findViewById(R.id.iv_contact_remind_switch);
		final ImageView ivTask = (ImageView) findViewById(R.id.iv_task_remind_switch);
		final ImageView ivSaleChance = (ImageView) findViewById(R.id.iv_salechance_remind_switch);
		final ImageView ivApproval = (ImageView) findViewById(R.id.iv_approval_remind_switch);

		context = SettingNoticeActivity.this;
		sp = context.getSharedPreferences("remind", MODE_PRIVATE);
		final Editor editor = sp.edit();
		notice = sp.getBoolean("notice", false);
		email = sp.getBoolean("email", false);
		log = sp.getBoolean("log", false);
		client = sp.getBoolean("client", false);
		order = sp.getBoolean("order", false);
		contact = sp.getBoolean("contact", false);
		task = sp.getBoolean("task", false);
		saleChance = sp.getBoolean("saleChance", false);
		approval = sp.getBoolean("approval", false);

		init(ivNotice, notice);
		init(ivEmail, email);
		init(ivLog, log);
		init(ivClient, client);
		init(ivOrder, order);
		init(ivContact, contact);
		init(ivTask, task);
		init(ivSaleChance, saleChance);
		init(ivApproval, approval);

		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivNotice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivNotice, notice);
				notice = !notice;
				editor.putBoolean("notice", notice);
				editor.commit();
			}
		});
		ivEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivEmail, email);
				editor.putBoolean("email", !email);
				email = !email;
				editor.commit();
			}
		});

		ivLog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivLog, log);
				editor.putBoolean("log", !log);
				log = !log;
				editor.commit();
			}
		});
		ivClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivClient, client);
				editor.putBoolean("client", !client);
				client = !client;
				editor.commit();
			}
		});
		ivOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivOrder, order);
				editor.putBoolean("order", !order);
				order = !order;
				editor.commit();
			}
		});
		ivContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivContact, contact);
				editor.putBoolean("contact", !contact);
				contact = !contact;
				editor.commit();
			}
		});

		ivTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivTask, task);
				editor.putBoolean("task", !task);
				task = !task;
				editor.commit();
			}
		});
		ivSaleChance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivSaleChance, saleChance);
				editor.putBoolean("saleChance", !saleChance);
				saleChance = !saleChance;
				editor.commit();
			}
		});
		ivApproval.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				setOnClickListener(ivApproval, approval);
				editor.putBoolean("approval", !approval);
				approval = !approval;
				editor.commit();
			}
		});
	}

	/**
	 * 初始化开关
	 * 
	 * @param iView
	 * @param flag
	 */
	private void init(ImageView iView, boolean flag) {
		if (!flag) {
			iView.setImageResource(R.drawable.switch_off_normal);
		} else {
			iView.setImageResource(R.drawable.switch_on_normal);
		}
	}

	private void setOnClickListener(ImageView iView, boolean flag) {
		LogUtils.i("remind", "setOnClickListener");
		if (flag) {
			iView.setImageResource(R.drawable.switch_off_normal);
		} else {
			iView.setImageResource(R.drawable.switch_on_normal);
		}
	}
}
