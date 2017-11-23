package com.cedarhd.receiver;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.cedarhd.ExistApplication;
import com.cedarhd.LoginActivity;
import com.cedarhd.biz.AlarmTaskBiz;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.utils.LogUtils;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * 信鸽消息推送接受 广播接收器
 * 
 * @author K 2015-07-10
 * 
 */
public class XGMessagePushReceiver extends XGPushBaseReceiver {
	private final String TAG = "XGMessagePushReceiver";
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {
	}

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult result) {
		// 信鸽顶部通知栏 点击回调
		LogUtils.i(TAG, "onNotifactionClickedResult");

	}

	@Override
	public void onNotifactionShowedResult(final Context context,
			XGPushShowedResult msg) {
		String title = msg.getTitle();
		String content = msg.getContent();
		LogUtils.i(
				TAG,
				"onNotifactionShowedResult:" + title + "--"
						+ msg.getCustomContent());

		if ("下线通知".equals(title)) {
			setOfflineNotify(context, title, content);
		} else if ("新建任务".equals(title)) {
			// 更新本地提醒数据库
			AlarmTaskBiz.downloadAlarmTaskList(context);
		}
	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {

	}

	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage arg1) {
		LogUtils.i(TAG, "onTextMessage");
	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * 下线通知
	 * 
	 * @param context
	 * @param title
	 *            通知标题
	 * @param content
	 *            通知内容
	 */
	private void setOfflineNotify(final Context context, String title,
			String content) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				zlServiceHelper.clearMobileDeviceTokenV710(context);
			}
		}).start();

		SharedPreferencesHelper spfl = new SharedPreferencesHelper(context,
				PreferencesConfig.APP_USER_INFO);
		spfl.putValue(PreferencesConfig.IS_EXIST, "true");// 标识是否退出,重新登录
		spfl.putValue(PreferencesConfig.TICK_TITLE, title);
		spfl.putValue(PreferencesConfig.TICK_MESSAGE, content);

		// System.exit(0); //直接退出
		// Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(context, LoginActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// ((Activity) context).startActivity(intent);
		// String device = Build.DEVICE + Build.MODEL + Build.MANUFACTURER;
		Builder builder = new Builder(context);
		builder.setTitle("波尔云下线通知")
				.setMessage(content)
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ExistApplication.getInstance().exit(false);
					}
				})
				.setNegativeButton("重新登录",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭现有所有打开的activity
								ExistApplication.getInstance().exit(false);
								Intent intent = new Intent(context,
										LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}
						});
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		// 设置为全局对话框
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.show();
	}

	@Override
	public void onRegisterResult(Context arg0, int arg1,
			XGPushRegisterResult arg2) {
		// TODO Auto-generated method stub

	}
}
