package com.cedarhd;

/**
 * 设置提醒页面
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.models.推送设置;
import com.cedarhd.services.TaskAlarmService;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;

import java.util.List;

public class RemindActivity extends BaseActivity {
	private Context mContext;
	private HttpUtils httpUtils = new HttpUtils();

	private SharedPreferencesHelper spHelper;

	private ImageButton back;
	/**
	 * 通知提醒布局，图片，boolean值
	 */
	private RelativeLayout notice_tixing;
	private ImageView notice_image;
	private boolean isnotice = true;
	/**
	 * 任务提醒布局，图片，boolean值
	 */
	private RelativeLayout task_tixing;
	private ImageView task_imaView;
	private boolean istask = true;
	/**
	 * 日志提醒布局，图片，boolean值
	 */
	private RelativeLayout log_tixing;
	private ImageView log_imaView;
	private boolean islog = true;
	/**
	 * 客户提醒
	 */
	private RelativeLayout client_tixing;
	private ImageView client_imaView;
	private boolean isclient = true;
	/**
	 * 订单提醒
	 */
	private RelativeLayout order_tixing;
	private ImageView order_imaView;
	private boolean isorder = true;
	/**
	 * 申请提醒
	 */
	private RelativeLayout apply_tixing;
	private ImageView apply_imaView;
	private boolean isapply = true;
	/**
	 * 联系记录提醒
	 */
	private RelativeLayout contact_tixing;
	private ImageView contact_imaView;
	private boolean iscontact = true;
	/**
	 * 销售机会提醒
	 */
	private RelativeLayout sale_tixing;
	private ImageView sale_imaView;
	private boolean issale = true;

	/**
	 * 闹钟提醒
	 */
	private ImageView iv_alarm;
	private boolean isAlarm = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind);
		initData();
		findView();

		getSettingList();

		setAlarm(isAlarm);
	}

	private void initData() {
		mContext = RemindActivity.this;
		spHelper = new SharedPreferencesHelper(mContext,
				PreferencesConfig.APP_USER_INFO);
		isAlarm = spHelper.getBooleanValue(
				PreferencesConfig.IS_OPEN_ALAMR_TASK, false);
	}

	private void findView() {
		back = (ImageButton) findViewById(R.id.remind_title_back);
		notice_tixing = (RelativeLayout) findViewById(R.id.notice_tixing);
		notice_image = (ImageView) findViewById(R.id.notice_image);
		task_tixing = (RelativeLayout) findViewById(R.id.task_tixing);
		task_imaView = (ImageView) findViewById(R.id.task_image);
		log_tixing = (RelativeLayout) findViewById(R.id.log_tixing);
		log_imaView = (ImageView) findViewById(R.id.log_image);
		client_tixing = (RelativeLayout) findViewById(R.id.clent_tixing);
		client_imaView = (ImageView) findViewById(R.id.clent_image);
		order_tixing = (RelativeLayout) findViewById(R.id.order_tixing);
		order_imaView = (ImageView) findViewById(R.id.order_image);
		apply_tixing = (RelativeLayout) findViewById(R.id.apply_tixing);
		apply_imaView = (ImageView) findViewById(R.id.apply_image);
		contact_tixing = (RelativeLayout) findViewById(R.id.contact_tixing);
		contact_imaView = (ImageView) findViewById(R.id.contact_image);
		sale_tixing = (RelativeLayout) findViewById(R.id.sale_tixing);
		sale_imaView = (ImageView) findViewById(R.id.sale_image);

		iv_alarm = (ImageView) findViewById(R.id.task_alarm_image);
		notice_tixing.setOnClickListener(mlistener);
		back.setOnClickListener(mlistener);
		task_tixing.setOnClickListener(mlistener);
		log_tixing.setOnClickListener(mlistener);
		client_tixing.setOnClickListener(mlistener);
		order_tixing.setOnClickListener(mlistener);
		apply_tixing.setOnClickListener(mlistener);
		contact_tixing.setOnClickListener(mlistener);
		sale_tixing.setOnClickListener(mlistener);
		iv_alarm.setOnClickListener(mlistener);
	}

	private View.OnClickListener mlistener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int isNotify = 0;
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.remind_title_back:
				finish();
				break;
			case R.id.notice_tixing:
				if (isnotice) {
					isnotice = false;
					isNotify = 0;
					notice_image.setImageResource(R.drawable.isfalse);
				} else {
					isnotice = true;
					isNotify = 1;
					notice_image.setImageResource(R.drawable.istrue);
				}
				updateSetting(1, isNotify);
				break;
			case R.id.task_tixing:
				if (istask) {
					istask = false;
					task_imaView.setImageResource(R.drawable.isfalse);
					isNotify = 0;
				} else {
					istask = true;
					isNotify = 1;
					task_imaView.setImageResource(R.drawable.istrue);
				}

				updateSetting(3, isNotify);
				break;
			case R.id.log_tixing:
				if (islog) {
					islog = false;
					isNotify = 0;
					log_imaView.setImageResource(R.drawable.isfalse);
				} else {
					islog = true;
					isNotify = 1;
					log_imaView.setImageResource(R.drawable.istrue);
				}
				updateSetting(2, isNotify);
				break;
			case R.id.clent_tixing:
				if (isclient) {
					isclient = false;
					isNotify = 0;
					client_imaView.setImageResource(R.drawable.isfalse);
				} else {
					isclient = true;
					isNotify = 1;
					client_imaView.setImageResource(R.drawable.istrue);
				}
				updateSetting(7, isNotify);
				break;
			case R.id.order_tixing:
				if (isorder) {
					isorder = false;
					isNotify = 1;
					order_imaView.setImageResource(R.drawable.isfalse);
				} else {
					isorder = true;
					isNotify = 1;
					order_imaView.setImageResource(R.drawable.istrue);
				}
				// updateSetting(7, isNotify);
				break;
			case R.id.apply_tixing:
				if (isapply) {
					isapply = false;
					isNotify = 0;
					apply_imaView.setImageResource(R.drawable.isfalse);
				} else {
					isapply = true;
					isNotify = 1;
					apply_imaView.setImageResource(R.drawable.istrue);
				}
				updateSetting(4, isNotify);
				break;
			case R.id.contact_tixing:
				if (iscontact) {
					iscontact = false;
					isNotify = 0;
					contact_imaView.setImageResource(R.drawable.isfalse);
				} else {
					iscontact = true;
					isNotify = 1;
					contact_imaView.setImageResource(R.drawable.istrue);
				}
				updateSetting(8, isNotify);
				break;
			case R.id.sale_tixing:
				if (issale) {
					issale = false;
					isNotify = 0;
					sale_imaView.setImageResource(R.drawable.isfalse);
				} else {
					issale = true;
					isNotify = 1;
					sale_imaView.setImageResource(R.drawable.istrue);
				}
				// updateSetting(4, isNotify);
				break;
			case R.id.task_alarm_image:
				if (isAlarm) {
					isAlarm = false;
					iv_alarm.setImageResource(R.drawable.isfalse);

					TaskAlarmService service = TaskAlarmService
							.getServiceInstance(mContext);
					service.stopForeground(true);
					service.stopSelf();
				} else {
					isAlarm = true; // 开启，关闭
					iv_alarm.setImageResource(R.drawable.istrue);

					startService(new Intent(mContext, TaskAlarmService.class));
					// startActivity(new Intent(mContext,
					// SettingAlarmTaskActivity.class)); // 打开定时任务页面
				}
				spHelper.putBooleanValue(PreferencesConfig.IS_OPEN_ALAMR_TASK,
						isAlarm);
				break;
			}
		}
	};

	private void setAlarm(boolean isAlarm) {
		if (isAlarm) {
			iv_alarm.setImageResource(R.drawable.istrue);
		} else {
			iv_alarm.setImageResource(R.drawable.isfalse);
		}
	}

	/**
	 * 用于网络请求下来的数据对显示的图做出区分判断
	 * 
	 * @param isnotice
	 * @param istask
	 * @param islog
	 * @param isclient
	 * @param isorder
	 * @param isapply
	 * @param iscontact
	 * @param issale
	 */
	private void setInit(boolean isnotice, boolean istask, boolean islog,
			boolean isclient, boolean isorder, boolean isapply,
			boolean iscontact, boolean issale) {
		if (isnotice) {
			notice_image.setImageResource(R.drawable.istrue);
		} else {
			notice_image.setImageResource(R.drawable.isfalse);
		}
		if (istask) {
			task_imaView.setImageResource(R.drawable.istrue);
		} else {
			task_imaView.setImageResource(R.drawable.isfalse);
		}
		if (islog) {
			log_imaView.setImageResource(R.drawable.istrue);
		} else {
			log_imaView.setImageResource(R.drawable.isfalse);
		}
		if (isclient) {
			client_imaView.setImageResource(R.drawable.istrue);
		} else {
			client_imaView.setImageResource(R.drawable.isfalse);
		}
		if (isorder) {
			order_imaView.setImageResource(R.drawable.istrue);
		} else {
			order_imaView.setImageResource(R.drawable.isfalse);
		}
		if (isapply) {
			apply_imaView.setImageResource(R.drawable.istrue);
		} else {
			apply_imaView.setImageResource(R.drawable.isfalse);
		}
		if (iscontact) {
			contact_imaView.setImageResource(R.drawable.istrue);
		} else {
			contact_imaView.setImageResource(R.drawable.isfalse);
		}
		if (issale) {
			sale_imaView.setImageResource(R.drawable.istrue);
		} else {
			sale_imaView.setImageResource(R.drawable.isfalse);
		}
	}

	private void getSettingList() {
		final String url = Global.BASE_URL + "dynamic/getNotificationSettings";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String jsonResult = httpUtils.httpGet(url);
					List<推送设置> list = JsonUtils.ConvertJsonToList(jsonResult,
							推送设置.class);
					Message msg = handler.obtainMessage();
					msg.obj = list;
					msg.what = SUCCEDD_GET_SETTING_LIST;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	/***
	 * 设置动态提醒
	 * 
	 * @param dynamicType
	 *            枚举 [动态数据类型] 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户
	 *            8:客户联系记录 9:销售机会
	 * @param isNotify
	 *            是否提醒：1为提醒，0为不提醒
	 */
	private void updateSetting(int dynamicType, int isNotify) {
		final String url = Global.BASE_URL + "dynamic/setNotificationSettings/"
				+ dynamicType + "/" + isNotify;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String jsonResult = httpUtils.httpGet(url);
					Log.i("jsonResult", jsonResult);
					List<推送设置> list = JsonUtils.ConvertJsonToList(jsonResult,
							推送设置.class);
					Message msg = handler.obtainMessage();
					msg.obj = list;
					msg.what = SUCCEDD_GET_SETTING_LIST;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private boolean isNotify(int isNotify) {
		if (isNotify == 1) {
			// 1为提醒,0为不提醒
			return true;
		} else {
			return false;
		}
	}

	private void initSetting(List<推送设置> list) {
		if (list == null || list.size() == 0) {
			isnotice = istask = islog = isclient = isorder = isapply = iscontact = issale = true;
			setInit(isnotice, istask, islog, isclient, isorder, isapply,
					iscontact, issale);
		} else {
			for (int i = 0; i < list.size(); i++) {
				推送设置 notifySet = list.get(i);
				// 数据类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户
				// 8:客户联系记录 9:销售机会

				// 0代表提醒
				switch (notifySet.DynamicType) {
				case 1:// 公告通知
					isnotice = isNotify(notifySet.IsNotification);
					break;
				case 2:// 员工日志
					islog = isNotify(notifySet.IsNotification);
					break;
				case 3:// 任务
					istask = isNotify(notifySet.IsNotification);
					break;
				case 4:// 申请审批
					isapply = isNotify(notifySet.IsNotification);
					break;
				case 5:// 邮件
					break;
				case 6:// 通讯录
					break;
				case 7:// 客户
					isclient = isNotify(notifySet.IsNotification);
					break;
				case 8:// 客户联系记录
					iscontact = isNotify(notifySet.IsNotification);
					break;
				case 9:// 销售机会
					issale = isNotify(notifySet.IsNotification);
					break;

				default:
					break;
				}
			}
			setInit(isnotice, istask, islog, isclient, isorder, isapply,
					iscontact, issale);
		}
	}

	private final int SUCCEDD_GET_SETTING_LIST = 1;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEDD_GET_SETTING_LIST:
				List<推送设置> list = (List<推送设置>) msg.obj;
				initSetting(list);
				break;
			default:
				break;
			}
		};
	};
}
