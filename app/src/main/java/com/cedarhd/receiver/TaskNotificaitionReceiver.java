package com.cedarhd.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cedarhd.AlarmActivity;
import com.cedarhd.helpers.SharedPreferencesHelper;

/***
 * 任务提醒广播接收器
 * 
 * @author K
 * 
 */
public class TaskNotificaitionReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferencesHelper sp = new SharedPreferencesHelper(context,
				"task");
		String content = sp.getValue("content");
		Intent i = new Intent(context, AlarmActivity.class);
		i.putExtra("content", content);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
