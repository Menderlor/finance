package com.cedarhd.services;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;

import com.cedarhd.R;
import com.cedarhd.TabMainActivity;
import com.cedarhd.biz.AlarmTaskBiz;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.AlarmTask;
import com.cedarhd.receiver.AlarmReceiver;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.LogUtils;

import java.sql.SQLException;
import java.util.List;

public class TaskAlarmService extends Service {

	/** 当前service绑定通知栏的编号 */
	public static final int ID_NOTIFICATION = 1345;

	/** 心跳间隔 一分钟 */
	private final int POLLING_INTERVAL = 1000 * 60;

	/** 提醒时间间隔3 分钟 */
	private final int DISTINCE_TIME = 1000 * 60 * 3;

	/** 记录心跳周期次数 ：1分钟心跳一次，60次一个小时，为一个周期，访问一次网络 **/
	private int cycleCount = 0;

	private final String TAG = "TaskAlarmService";
	private static TaskAlarmService mService;
	private MyBinder myBinder = new MyBinder();
	private Handler handler = new Handler();
	
	private boolean mIsStop;

	@Override
	public void onCreate() {
		super.onCreate();
		mIsStop=false;
		LogUtils.i(TAG, "onCreate");
		sendPersistentNotification();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtils.i(TAG, "onStart");
		
		mService=this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// onStartCommand 的返回值决定启动的是何种类型的service
		
		LogUtils.i(TAG, "onStartCommand");
		// 隔多长时间检查一次
		handler.postDelayed(mHeartBeat, POLLING_INTERVAL);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i(TAG, "onBind");
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// 解除绑定
		LogUtils.i(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		LogUtils.i(TAG, "onDestroy");
		super.onDestroy();
		
		mIsStop=true;
	}

	/**
	 * 获得帮助实体类
	 * 
	 * @return
	 */
	public static TaskAlarmService getServiceInstance(Context context) {
		// ormDataHelper = ORMDataHelper.getInstance(context);
		if (mService == null) {
			mService = new TaskAlarmService();
		}
		return mService;
	}

	private class QueryRunnable implements Runnable {
		@Override
		public void run() {
			try {
				if (cycleCount >= 59) {
					LogUtils.i(TAG, "cycleCount:" + cycleCount);
					AlarmTaskBiz.downloadAlarmTaskList(getApplicationContext());
					cycleCount = 0;
				}
				queryAssignTimeList();
				cycleCount++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private Runnable mHeartBeat = new Runnable() {
		public void run() {
			if(!mIsStop){
				new Thread(new QueryRunnable()).start();
				handler.postDelayed(mHeartBeat, POLLING_INTERVAL);
			}
		}
	};

	/**
	 * 查询到期提醒时间任务
	 */
	private void queryAssignTimeList() throws SQLException {
		ORMDataHelper ormDataHelper = ORMDataHelper
				.getInstance(getApplicationContext());
		List<AlarmTask> list = ormDataHelper.getDao(AlarmTask.class)
				.queryForAll();
		for (AlarmTask task : list) {
			LogUtils.i(TAG, "" + task.AssignTime);
			long assignTime = DateTimeUtil.ConvertStringToLongDate(
					task.AssignTime).getTime();
			long distance = System.currentTimeMillis() - assignTime;
			LogUtils.e(TAG, "distance" + task.AssignTime + "--->" + distance);
			// 五分钟以内
			if (distance <= 0 && Math.abs(distance) < DISTINCE_TIME) {
				LogUtils.i(TAG, "distance" + task.AssignTime + "=" + distance);
				Intent intent = new Intent(getApplicationContext(),
						AlarmReceiver.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(AlarmReceiver.TAG, task);
				intent.putExtras(bundle);
				intent.setAction("task");
				sendBroadcast(intent);
				// 提醒过的任务 从本地删除
				ormDataHelper.getDao(AlarmTask.class).delete(task);
			}
		}
	}

	private void sendPersistentNotification() {
		try {
			// Notification对象创建
			Notification.Builder builder = new Notification.Builder(this);
			// 设置通知自动消失
			builder.setAutoCancel(true);
			builder.setSmallIcon(R.drawable.logo);
			// 状态栏弹出显示文字，如果不设置则顶部通知看不到该通知的任何内容，需要下拉后才能看到通知
			builder.setTicker("波尔云移动办公");
			String title="波尔云";
			if(Global.mUser!=null&&!TextUtils.isEmpty(Global.mUser.UserName))
			{
				title+="("+Global.mUser.UserName+")";
			}
			// 通知的内容和标题
			builder.setContentTitle(title);
			builder.setContentText("运行中,可在通知设置中关闭");
			Notification notification = builder.build();
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			Intent notificationIntent = new Intent(this, TabMainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendingIntent);
			startForeground(ID_NOTIFICATION, notification);
		} catch (Exception e) {
			LogUtils.e(TAG, "" + e);
		}
	}

	/**
	 * 是否锁屏
	 * 
	 * @return
	 */
	private boolean isLockScreen() {
		// 电源管理服务类
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 判断屏幕是否开启, 如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
		boolean isScreenOn = pm.isScreenOn();
		LogUtils.i("PowerManager", "屏幕：" + isScreenOn);
		// 键盘锁屏管理服务类
		KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		// 判断是否锁屏
		boolean isLockKeyguard = mKeyguardManager
				.inKeyguardRestrictedInputMode();
		LogUtils.i("PowerManager", "屏幕锁：" + isLockKeyguard);
		if (!isScreenOn) {
			// 点亮亮屏
			WakeLock wakeLock = pm.newWakeLock(
					PowerManager.ACQUIRE_CAUSES_WAKEUP
							| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			wakeLock.acquire();
			LogUtils.i("PowerManager : ", "------>mKeyguardLock");
		}
		if (isLockKeyguard) {
			// 得到键盘锁管理器对象,解锁
			@SuppressWarnings("deprecation")
			KeyguardLock kl = mKeyguardManager.newKeyguardLock("unLock");
			// 参数是LogCat里用的Tag
			kl.disableKeyguard();

			// TODO 要执行的代码

			// 重新启用自动加锁，否则一直开启费电
			// kl.reenableKeyguard();
			// wakeLock.release();
		}
		return isLockKeyguard;
	}

	/**
	 * 自定义Binder用于进程间通讯
	 * 
	 */
	private class MyBinder extends Binder implements IService {
		@Override
		public void invoke() {
			// TODO
			// downLoadData();
		}
	}

}
