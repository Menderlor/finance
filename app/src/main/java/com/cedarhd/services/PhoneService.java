package com.cedarhd.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.Client;
import com.cedarhd.models.Demand;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import java.util.List;

/***
 * 电话监听后台服务
 * 
 * @author K
 * 
 */
public class PhoneService extends Service {
	private final String TAG = "PhoneService";
	private Context context;
	private TelephonyManager tm;
	private WindowManager wm;
	private View view;
	private TextView tvClientName;
	private TextView tvContact;
	private boolean isAdd;

	private String mIncommingNum;

	private final int SUCCESS_GET_CLIENT = 10;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_CLIENT:
				Client client = (Client) msg.obj;
				showClientInfo(client);
				break;
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.d("Service", "onCreate");
		context = getApplicationContext();
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		tm.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

		view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.window_phone, null);
		tvClientName = (TextView) view.findViewById(R.id.tv_name_window_client);
		tvContact = (TextView) view
				.findViewById(R.id.tv_contacts_window_client);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		LogUtils.d("Service", "onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// onStartCommand 的返回值决定启动的是何种类型的service
		LogUtils.d("Service", "onStartCommand");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void getClientByNet(String incomingNumber) {
		final String url = Global.BASE_URL + "Customer/GetCustomerByFilter";
		final Demand demand = new Demand();
		demand.表名 = "客户";
		demand.方法名 = "Customer/GetCustomerByFilter";
		demand.条件 = "(电话 like '%" + incomingNumber + "%' or 手机 like '%"
				+ incomingNumber + "%')";
		LogUtils.i(TAG, demand.条件);
		final HttpUtils httpUtils = new HttpUtils();
		new Thread() {
			public void run() {
				String json;
				try {
					json = httpUtils.postSubmit(url,
							JsonUtils.initJsonObj(demand));
					LogUtils.i(TAG, json);
					List<Client> clientLists = JsonUtils.ConvertJsonToList(
							json, Client.class);
					if (clientLists != null && clientLists.size() > 0) {
						Message msg = handler.obtainMessage();
						msg.obj = clientLists.get(0);
						msg.what = SUCCESS_GET_CLIENT;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void showClientInfo(Client client) {
		if (client != null) {
			// Toast.makeText(
			// context,
			// "波尔云-" + client.getCustomerName() + "\n"
			// + client.getContacts(),
			// Toast.LENGTH_LONG).show();
			LogUtils.i(TAG,
					client.getCustomerName() + "--" + client.getContacts());
			tvClientName.setText("" + client.getCustomerName());
			tvContact.setText("" + client.getContacts() + "\n" + mIncommingNum);

			final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
					| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			int x = 0;
			int y = 0;
			params.x = params.x + x;
			params.y = params.y + y;
			params.format = PixelFormat.TRANSLUCENT;
			params.type = WindowManager.LayoutParams.TYPE_TOAST;
			wm.addView(view, params);
			isAdd = true;
		}
	}

	/***
	 * 来电状态监听
	 * 
	 * @author K
	 */
	private class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				Toast.makeText(getApplicationContext(), "波尔云-显示归属地",
						Toast.LENGTH_LONG).show();
				mIncommingNum = incomingNumber;
				getClientByNet(incomingNumber);
				// ORMDataHelper ormDataHelper = ORMDataHelper
				// .getInstance(context);
				// Dao<Client, Integer> dao = ormDataHelper.getClientDao();
				// Client client = dao.queryBuilder().where()
				// .eq("Phone", incomingNumber).queryForFirst();
				// showClientInfo();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 通话中
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// 添加了来电归属显示且状态为挂断 时 ，移除该对话框
				if (isAdd) {
					wm.removeView(view);
					isAdd = !isAdd;
				}
				break;
			}
		}
	}
}
