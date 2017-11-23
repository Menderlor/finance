package com.cedarhd.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.TaskAlarmRemindActivity;
import com.cedarhd.TaskInfoActivity;
import com.cedarhd.helpers.AlarmManagerHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.models.AlarmTask;
import com.cedarhd.models.任务;
import com.cedarhd.utils.LogUtils;

/**
 * @Description: 闹铃时间到了会进入这个广播，在onReceive进行闹钟逻辑
 * @author kjx
 * @date 2014-06-11
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	public static String TAG = "AlarmReceiver";
	public static String ACTION_TASK = "task";
	private Context mContext;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if (intent.getAction().equals("repeating_service")) {
			// Toast.makeText(context, "收到闹钟...", Toast.LENGTH_SHORT).show();
			LogUtils.i("alarm", "收到闹钟..." + intent.getAction());
			// createNotificationService();
		} else if (intent.getAction().equals(ACTION_TASK)) {
			// 2015-10-27广播提醒 Activity的形式打开
			LogUtils.i("AlarmReceiver", "收到闹钟..." + intent.getAction());
			AlarmTask task = (AlarmTask) intent.getExtras()
					.getSerializable(TAG);
			LogUtils.i("AlarmReceiver", "任务到期：" + task.AssignTime + "--"
					+ task.Content);
			Intent i = new Intent(context, TaskAlarmRemindActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(TaskAlarmRemindActivity.TASK_ALARM, task);
			i.putExtras(bundle);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else {
			Bundle bundle = intent.getExtras();
			任务 item = (任务) bundle.getSerializable(AlarmManagerHelper.TAG);
			// String string = intent.getStringExtra(MyClockActivity.TAG);
			Toast.makeText(context, "闹铃时间到，闹铃响起~~" + item.Title,
					Toast.LENGTH_LONG).show();
			LogUtils.i("clock", "闹铃时间到，闹铃响起~~" + item.toString());
			alarmNotice(context, item); // 闹钟执行动作

			/**
			 * 闹钟结束后，再注册一个新闹钟
			 */
			AlarmManagerHelper alarmManagerHelper = new AlarmManagerHelper(
					context);
			alarmManagerHelper.registerAlarm();
		}
	}

	/**
	 * 闹钟提醒功能
	 */
	private void alarmNotice(Context context, 任务 item) {
		LogUtils.d("clock", "alarmNotice~~" + item.toString());
		// 获得通知管理
		NotificationManager myNotiManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// 设置点击通知 跳转到的页面
		Intent notifyIntent = new Intent(Global.mContext,
				TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("tasklist", item);
		notifyIntent.putExtras(bundle);
		/* 创建PendingIntent作为设置递延运行的Activity */
		PendingIntent appIntent = PendingIntent.getActivity(Global.mContext, 0,
				notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		/* 设置statusbar显示的icon */
		myNoti.icon = R.drawable.a_icon7;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = "任务到期提醒";
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
		myNoti.setLatestEventInfo(Global.mContext, "任务到期",
				"您的任务：(" + item.getTitle() + ") 已到执行时间，请尽快完成！", appIntent);
		// 设置自动清除
		myNoti.ledARGB = 0xff00ff00;
		myNoti.ledOnMS = 300;
		myNoti.ledOffMS = 1000;
		myNoti.flags |= Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_SHOW_LIGHTS;
		myNoti.defaults |= Notification.DEFAULT_SOUND;
		/* 送出Notification */
		myNotiManager.notify(0, myNoti);
		LogUtils.e("clock", "闹铃时间到，闹铃响起~~" + item.toString());
	}

}
