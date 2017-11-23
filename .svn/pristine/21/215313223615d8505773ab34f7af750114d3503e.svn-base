package com.cedarhd.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.任务;
import com.cedarhd.receiver.AlarmReceiver;
import com.cedarhd.utils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 闹钟管理帮助类
 * 
 * @author kjx
 * 
 */
public class AlarmManagerHelper {

	public static final String TAG = "AlarmManagerHelper";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	private Context context;

	// private Activity activity;

	public AlarmManagerHelper(Context context) {
		super();
		this.context = context;
		// this.activity = activity;
	}

	/**
	 * 注册任务到期提醒
	 * 
	 * @param item
	 */
	public void registerTaskAlarm(任务 item) {
		String dateTime = "";
		if (item != null && !TextUtils.isEmpty(item.AssignTime)) {
			dateTime = item.AssignTime;
		} else {
			return;
		}

		// 暂时停止注册闹钟
		if (false) {
			try {
				java.util.Date date = sdf.parse(dateTime);
				long alertTime = date.getTime();
				Intent intent = new Intent(context, AlarmReceiver.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(TAG, item);
				intent.putExtras(bundle);
				PendingIntent sender = PendingIntent.getBroadcast(context, 0,
						intent, PendingIntent.FLAG_ONE_SHOT);
				// 进行闹铃注册
				AlarmManager manager = (AlarmManager) context
						.getSystemService(Context.ALARM_SERVICE);
				manager.set(AlarmManager.RTC_WAKEUP, alertTime, sender);
				LogUtils.i(TAG, "calendar---" + alertTime);
				Toast.makeText(context, "设置闹铃+" + dateTime + "+成功!",
						Toast.LENGTH_LONG).show();
			} catch (ParseException e) {
				LogUtils.e(TAG, "" + e);
				e.printStackTrace();
			}
		}

	}

	/**
	 * 注册闹钟
	 */
	public void registerAlarm() {
		任务 item = zlServiceHelper.getLatelyTaskTime(context);
		if (item != null && item.Id != 0) {
			// 只有当任务不为空才注册任务提醒
			registerTaskAlarm(item);
		}
	}
}
