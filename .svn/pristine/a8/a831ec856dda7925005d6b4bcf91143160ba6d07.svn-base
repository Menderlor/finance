package com.cedarhd.services;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
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

import com.cedarhd.R;
import com.cedarhd.RemindDialogActivity;
import com.cedarhd.TabMainActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ServerCall;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.动态;
import com.cedarhd.models.动态已提醒;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态提醒Service
 * 
 * @author kjx
 * @since 2014/10/09 10:10
 */
@SuppressLint("NewApi")
public class DynamicRemindService extends Service {
	private final String TAG = "DynamicRemindService";
	private static DynamicRemindService mService;
	private static boolean mIsRunning_Dynamic = false;
	private MyBinder myBinder = new MyBinder();
	private Handler handler = new Handler();
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private ServerCall serverCall = new ServerCall();

	private ORMDataHelper ormDataHelper;

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i("Service", "onCreate");
		sendPersistentNotification();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		LogUtils.i("Service", "onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// onStartCommand 的返回值决定启动的是何种类型的service
		LogUtils.i("Service", "onStartCommand");
		// 隔多长时间检查一次
		handler.postDelayed(mHeartBeat, Global.POLLING_INTERVAL);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i("Service", "onBind");
		return myBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// 解除绑定
		LogUtils.i("Service", "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		LogUtils.i("Service", "onDestroy");
		super.onDestroy();
	}

	// /**
	// * 私有化构造函数
	// */
	// Service不允许私有化构造函数
	// private DynamicRemindService() {
	// super();
	// }

	/**
	 * 获得帮助实体类
	 * 
	 * @return
	 */
	public static DynamicRemindService getServiceInstance(Context context) {
		// ormDataHelper = ORMDataHelper.getInstance(context);
		if (mService == null) {
			mService = new DynamicRemindService();
		}
		return mService;
	}

	private Runnable mHeartBeat = new Runnable() {
		public void run() {
			HeartBeatThread t = new HeartBeatThread();
			new Thread(t).start();
			// 隔130多长时间检查一次
			handler.postDelayed(mHeartBeat, 1000 * 30);
		}
	};

	private void sendPersistentNotification() {
		try {
			// Notification对象创建
			Notification.Builder builder = new Notification.Builder(this);
			// 设置通知自动消失
			builder.setAutoCancel(true);
			builder.setSmallIcon(R.drawable.logo);
			// 状态栏弹出显示文字，如果不设置则顶部通知看不到该通知的任何内容，需要下拉后才能看到通知
			builder.setTicker("波尔云移动办公");
			// 通知的内容和标题
			builder.setContentTitle("波尔云运行中");
			builder.setContentText("关闭后将不再收到新消息提醒");
			Notification notification = builder.build();
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			Intent notificationIntent = new Intent(this, TabMainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendingIntent);
			startForeground(1132, notification);
		} catch (Exception e) {
			LogUtils.e(TAG, "" + e);
		}
	}

	/**
	 * 发送通知：动态提醒
	 */
	private void sendDynamicRemind(List<动态> listDynamic) {
		LogUtils.i("judgeRemindExistedOrNot", "sendDynamicRemind size "
				+ listDynamic.size());
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Android3.0增加了Notification.Builder类，该类可以轻松地创建Notification对象。
		Notification.Builder builder = new Notification.Builder(this);
		// 设置通知自动消失
		builder.setAutoCancel(true);
		builder.setSmallIcon(R.drawable.bohrlogo);
		// 状态栏弹出显示文字，如果不设置则顶部通知看不到该通知的任何内容，需要下拉后才能看到通知
		builder.setTicker("波尔云提示您有" + listDynamic.size() + "条新动态...");
		// 通知的内容和标题
		builder.setContentTitle("您有" + listDynamic.size() + "条新动态...");
		builder.setContentText(listDynamic.get(0).Content);
		// 设置通知的默认铃声和震动
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// TODO 点击通知的跳转意图
		Intent intent = new Intent(this, TabMainActivity.class);
		intent.putExtra("currentTab", 0);
		PendingIntent pIntent = PendingIntent.getActivity(this, 1, intent,
				PendingIntent.FLAG_ONE_SHOT);
		builder.setContentIntent(pIntent);
		Notification notification = builder.build();
		manager.notify(0, notification);
	}

	/**
	 * 判断动态提醒是否存在
	 * 
	 * @param list
	 * @return 本地不存在的动态列表
	 * @throws SQLException
	 */
	public List<动态> judgeRemindExistedOrNot(List<动态> list) throws SQLException {
		List<动态> returenList = new ArrayList<动态>();
		List<动态已提醒> existedList = new ArrayList<动态已提醒>();
		if (ormDataHelper == null) {
			ormDataHelper = ORMDataHelper.getInstance(getApplicationContext());
		}
		Dao<动态已提醒, Integer> dao已提醒 = ormDataHelper.getDao(动态已提醒.class);
		existedList = dao已提醒.queryForAll();
		LogUtils.i("judgeRemindExistedOrNot", "本地数量 " + existedList.size()
				+ "，从服务器接收到：" + list.size());
		// 如果本地数据库为空，直接插入最新获取的动态编号
		if (existedList == null || existedList.size() == 0) {
			for (动态 dynamic : list) {
				LogUtils.i("judgeRemindExistedOrNot", "existedList size "
						+ existedList.size());
				int id = dynamic.Id;
				动态已提醒 item = new 动态已提醒();
				item.Id = id;
				dao已提醒.create(item);
				returenList.add(dynamic);
			}
		} else if (existedList.size() > 0) {
			// 遍历
			for (动态 dynamic : list) {
				boolean isExist = false;
				int id = dynamic.Id;
				for (int i = 0; i < existedList.size(); i++) {
					if (existedList.get(i).Id == id) {
						isExist = true;
						// break;
					}
				}
				// 如果该记录在数据库中不存在，则插入
				if (!isExist) {
					dao已提醒.create(new 动态已提醒(id));
					returenList.add(dynamic);
				}
			}
		}
		return returenList;
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
	 * 心跳机制监听最新动态
	 * 
	 * @author kjx
	 * @since 2014/10/09 11:24
	 */
	private class HeartBeatThread implements Runnable {
		public void run() {
			LogUtils.i("HeartBeatThread_Dynamic", "HeartBeatThread_Dynamic");
			if (mIsRunning_Dynamic) {
				LogUtils.i("HeartBeatThread_Dynamic", "mIsRunning_Dynamic");
				return;
			}
			mIsRunning_Dynamic = true;
			Demand demand = new Demand();
			demand.表名 = "";
			demand.用户编号 = "";
			demand.方法名 = "dynamic/getDynamicList";
			demand.条件 = "";
			demand.附加条件 = "isnull(已读)";
			demand.每页数量 = 100; // 不分页
			demand.偏移量 = 0;
			List<动态> listDynamic = zlServiceHelper.GetDynamicList(serverCall,
					demand);
			LogUtils.i("GetDynamicList",
					"GetDynamicList size=" + listDynamic.size());
			if (listDynamic.size() > 0) {
				List<动态> latestDynamic = new ArrayList<动态>();
				try {
					// 获得本地不存在的提醒
					latestDynamic = judgeRemindExistedOrNot(listDynamic);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (latestDynamic.size() > 0) {
					// 启动导航页则只提示顶部通知，否则弹出弹窗提示
					if (Global.isStartMenu) {
						sendDynamicRemind(latestDynamic);
					} else {

						isLockScreen();
						boolean isLog = false;
						for (int i = 0; i < latestDynamic.size(); i++) {
							if (latestDynamic.get(i).Type.contains("日志")) {
								isLog = true;
							}
						}
						// 不包含日志才弹出对话框提醒
						if (!isLog) {
							Intent intent = new Intent(getApplicationContext(),
									RemindDialogActivity.class);
							Bundle bundle = new Bundle();
							// 存储动态提醒长度
							bundle.putSerializable(
									RemindDialogActivity.COUNT_REMIND,
									latestDynamic.size());
							bundle.putSerializable(RemindDialogActivity.TAG,
									latestDynamic.get(0));
							if (latestDynamic.size() >= 2) {
								bundle.putSerializable(
										RemindDialogActivity.TAG2,
										latestDynamic.get(1));
							}
							intent.putExtras(bundle);
							// 启动一个新的任务在任务栈中
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						} else {
							sendDynamicRemind(latestDynamic);
						}
					}
				}
			}
			mIsRunning_Dynamic = false;
		}
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
