package com.cedarhd;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.utils.LogUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 短信群发 并将数据保存到数据库
 * */
public class SmsActity extends BaseActivity {
	String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	private EditText edit_no;
	private EditText edit_body;
	private Button button;
	private String body;// 短信内容
	private String[] address; //
	String p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_send);
		edit_no = (EditText) findViewById(R.id.phone);
		edit_body = (EditText) findViewById(R.id.body);
		button = (Button) findViewById(R.id.b_send);
		edit_no.setText(getIntent().getExtras().getString("tels").toString());
		edit_body.setText(getIntent().getExtras().getString("body").toString());

		// 注册监听
		registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				p = edit_no.getText().toString();
				body = edit_body.getText().toString();
				LogUtils.i("VIEWIVVVV", p);
				LogUtils.i("VIEWIVVVV", body);
				address = p.split(","); // 不同电话号码用,隔开 如果是中文，请修改否则无法拆分
				Set<String> addr = new HashSet<String>();

				for (int i = 0; i < address.length; i++) {
					addr.add(address[i]);
				}
				sendSMS(addr, body);
			}

		});

	}

	public void sendSMS(Set<String> phone, String body) {

		SmsManager msg = SmsManager.getDefault();
		Intent send = new Intent(SENT_SMS_ACTION);
		// 短信发送广播
		PendingIntent sendPI = PendingIntent.getBroadcast(this, 0, send, 0);
		Intent delive = new Intent(DELIVERED_SMS_ACTION);
		// 发送结果广播
		PendingIntent deliverPI = PendingIntent
				.getBroadcast(this, 0, delive, 0);
		LogUtils.i("panduan", body.length() + "");
		if (body.length() >= 70) {
			String[] strings = body.split("@");
			List<String> msgs = msg.divideMessage(body);
			for (int i = 0; i < strings.length; i++) {
				String info = strings[i];
				msg.sendTextMessage(p.replace(" ", ""), null, info, sendPI,
						deliverPI);
			}
		}

	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(sendMessage);
	}

	BroadcastReceiver sendMessage = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent intent) {
			LogUtils.i("onReceive", "onReceive---" + getResultCode());
			// 判断短信是否成功
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				Toast.makeText(SmsActity.this, "发送成功！", Toast.LENGTH_SHORT)
						.show();
				finish();
				break;
			default:
				Toast.makeText(SmsActity.this, "发送失败！", Toast.LENGTH_SHORT)
						.show();
				finish();
				break;
			}
		}
	};
}
