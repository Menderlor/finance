package com.cedarhd.biz;

import android.content.Context;

import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.AlarmTask;
import com.cedarhd.models.Demand;
import com.cedarhd.models.任务;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.List;

/***
 * 任务提醒的相关逻辑
 * 
 * @author K
 * 
 */
public class AlarmTaskBiz {
	private final static String TAG = "AlarmTaskBiz";

	/** 从服务器下载最新定时任务 */
	public static void downloadAlarmTaskList(Context context) {
		final ORMDataHelper ormDataHelper = ORMDataHelper.getInstance(context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpUtils httpUtils = new HttpUtils();
					String url = Global.BASE_URL + "task/GetAlarmTaskList";
					Demand demand = new Demand("", 10, 0);
					String response = httpUtils.postSubmit(url,
							JsonUtils.initJsonObj(demand));
					List<任务> alarmTasks = JsonUtils.ConvertJsonToList(response,
							任务.class);
					try {
						Dao<AlarmTask, Integer> dao = ormDataHelper
								.getDao(AlarmTask.class);
						dao.deleteBuilder().delete();
						for (任务 item : alarmTasks) {
							AlarmTask task = new AlarmTask();
							task.AssignTime = item.AssignTime;
							task.Executor = item.Executor;
							task.Content = item.Content;
							task.Id = item.Id;
							task.Publisher = item.Publisher;
							task.Title = item.Title;
							dao.create(task);
						}
						LogUtils.i("alarm size=", "size"
								+ dao.queryForAll().size());
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (Exception e) {
						LogUtils.e(TAG, "" + e.getMessage());
					}

				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}).start();

	}
}
