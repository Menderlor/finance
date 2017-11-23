package com.cedarhd;

/**
 * 任务更多页面
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.DoubleDatePickerDialog;
import com.cedarhd.control.RoundProgressBar;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.TaskMoreNum;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskMoreActivity extends BaseActivity {
	/**
	 * 更多页面左上角返回键
	 */
	private ImageButton taskmore_back;
	/**
	 * 本周，本月，时间段
	 */
	private TextView taskmore_week, taskmore_mounth, taskmore_date;
	/**
	 * 显示本月本周时间的任务详情
	 */
	private TextView taskmore_info;
	/**
	 * 全部任务进度，我的任务进度，我下达任务进度
	 */
	private RoundProgressBar taskmore_all, taskmore_my, taskmore_issud;
	/**
	 * 当点击本周等下面白色区域对应显示的文字完成，未完成，延期
	 */
	private TextView taskmore_ok, taskmore_no, taskmore_delay;
	/**
	 * 完成，未完成，延期对应的数字
	 */
	private TextView taskmore_ok_num, taskmore_no_num, taskmore_delay_num,
			taskmore_all_num, taskmore_unread_num;
	/**
	 * 完成未完成延期对应的布局用来设置监听事件
	 */
	private RelativeLayout layout_taskmore_ok, layout_taskmore_no,
			layout_taskmore_delay, layout_taskmore_all, layout_taskmore_unread;
	/**
	 * 用来标记选择的是周还是月还是时间段
	 */
	private String isTime;
	/**
	 * 用来标记是完成未完成或者延期
	 */
	private String isok;
	/**
	 * 用来选择是那全部任务还是我的任务还是我下达的任务
	 */
	private String isMy;
	/**
	 * 传递的bundle
	 */
	Bundle bundle;
	/**
	 * 记录开始时间与结束时间
	 */
	String startTime, endTime;
	/**
	 * 按钮监听事件
	 */
	private ImageButton in1, in2, in3;
	/**
	 * 网络请求的类
	 */
	private HttpUtils mHttpUtils;

	String path = Global.BASE_URL + Global.EXTENSION + "task/GetTaskSummary";
	Demand demand;

	/**
	 * progressdialog
	 */
	// ProgressDialog progressDialog;
	private RelativeLayout all_relative, my_relative, inssed_relative;
	private TextView all_task_zmy, my_task_zmy, my_inssed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmore);
		// progressDialog = new ProgressDialog(TaskMoreActivity.this);
		// progressDialog.setTitle("提示");
		// progressDialog.setMessage("加载中请稍后...");
		mHttpUtils = new HttpUtils();
		bundle = new Bundle();
		findview();
		startWeekThread();
	}

	private void findview() {
		all_relative = (RelativeLayout) findViewById(R.id.all_relative);
		my_relative = (RelativeLayout) findViewById(R.id.my_relative);
		inssed_relative = (RelativeLayout) findViewById(R.id.inssed_relative);
		all_task_zmy = (TextView) findViewById(R.id.all_task_zmy);
		my_task_zmy = (TextView) findViewById(R.id.task_my_zmy);
		my_inssed = (TextView) findViewById(R.id.my_inssed);
		taskmore_back = (ImageButton) findViewById(R.id.taskmore_back);
		taskmore_week = (TextView) findViewById(R.id.taskmore_week);
		taskmore_mounth = (TextView) findViewById(R.id.taskmore_mounth);
		taskmore_date = (TextView) findViewById(R.id.taskmore_date);
		taskmore_info = (TextView) findViewById(R.id.taskmore_info);
		taskmore_all = (RoundProgressBar) findViewById(R.id.taskmore_all);
		taskmore_my = (RoundProgressBar) findViewById(R.id.moretask_my);
		taskmore_issud = (RoundProgressBar) findViewById(R.id.moretask_issued);
		taskmore_ok = (TextView) findViewById(R.id.taskmore_title_ok);
		taskmore_no = (TextView) findViewById(R.id.taskmore_title_no);
		taskmore_delay = (TextView) findViewById(R.id.taskmore_title_delay);
		taskmore_ok_num = (TextView) findViewById(R.id.taskmore_title_ok_num);
		taskmore_no_num = (TextView) findViewById(R.id.taskmore_title_no_num);
		taskmore_delay_num = (TextView) findViewById(R.id.taskmore_title_delay_num);
		taskmore_all_num = (TextView) findViewById(R.id.taskmore_title_all_num);
		taskmore_unread_num = (TextView) findViewById(R.id.taskmore_title_unread_num);
		layout_taskmore_ok = (RelativeLayout) findViewById(R.id.layout_taskmore_ok);
		layout_taskmore_no = (RelativeLayout) findViewById(R.id.layout_taskmore_no);
		layout_taskmore_delay = (RelativeLayout) findViewById(R.id.layout_taskmore_delay);
		layout_taskmore_all = (RelativeLayout) findViewById(R.id.layout_taskmore_all);
		layout_taskmore_unread = (RelativeLayout) findViewById(R.id.layout_taskmore_unread);
		in1 = (ImageButton) findViewById(R.id.taskmore_inok);
		in2 = (ImageButton) findViewById(R.id.taskmore_inno);
		in3 = (ImageButton) findViewById(R.id.taskmore_in3);
		setDefault();
		setonclicklistener();
	}

	/**
	 * 开启获取周数据的线程
	 */
	String ResultWeek;
	String ResultMounth;
	String ResultTime;
	public static final int SUCCESS_WEEK = 123;
	public static final int SUCCESS_MOUNTH = 124;
	public static final int SUCCESS_TIME = 125;
	public static final int ERROR_WEEK = 112;
	public static final int ERROR_MOUNTH = 113;
	public static final int ERROR_TIME = 114;
	public TaskMoreNum moreNum;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_WEEK:
				// progressDialog.dismiss();
				GetData(ResultWeek);
				setTextNum(moreNum.所有任务已完成, moreNum.所有任务未完成, moreNum.延期任务);
				taskmore_all_num.setText(moreNum.所有任务 + "");
				taskmore_unread_num.setText(moreNum.所有未读任务 + "");
				LogUtils.i("out", moreNum.所有任务已完成 + moreNum.所有任务未完成
						+ moreNum.延期任务 + "");
				setCircleProgress(moreNum);
				break;
			case SUCCESS_MOUNTH:
				// progressDialog.dismiss();
				GetData(ResultMounth);
				setTextNum(moreNum.所有任务已完成, moreNum.所有任务未完成, moreNum.延期任务);
				setCircleProgress(moreNum);
				break;
			case SUCCESS_TIME:
				// progressDialog.dismiss();
				GetData(ResultTime);
				setTextNum(moreNum.所有任务已完成, moreNum.所有任务未完成, moreNum.延期任务);
				setCircleProgress(moreNum);
				break;
			case ERROR_WEEK:
			case ERROR_MOUNTH:
			case ERROR_TIME:
				// progressDialog.dismiss();
				Toast.makeText(TaskMoreActivity.this, "加载失败稍后重试",
						Toast.LENGTH_LONG).show();
				break;

			}
		};
	};

	/**
	 * 设置进度
	 */
	private void setCircleProgress(TaskMoreNum moreNum) {
		taskmore_all.setMax(moreNum.所有任务未完成 + moreNum.所有任务已完成);
		taskmore_all.setProgress(moreNum.所有任务已完成);
		taskmore_my.setMax(moreNum.我的任务已完成 + moreNum.我的任务未完成);
		taskmore_my.setProgress(moreNum.我的任务已完成);
		taskmore_issud.setMax(moreNum.我下达的任务已完成 + moreNum.我下达的任务未完成);
		taskmore_issud.setProgress(moreNum.我下达的任务已完成);
	}

	/**
	 * 获取实体类对象
	 * 
	 * @param str
	 */
	private void GetData(String str) {
		JSONArray array = getJSONArray(str, "Data");
		try {
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					moreNum = new TaskMoreNum();
					moreNum.所有任务已完成 = object.getInt("所有任务已完成");
					moreNum.所有任务未完成 = object.getInt("所有任务未完成");
					moreNum.我的任务已完成 = object.getInt("我的任务已完成");
					moreNum.我的任务未完成 = object.getInt("我的任务未完成");
					moreNum.我下达的任务已完成 = object.getInt("我下达的任务已完成");
					moreNum.我下达的任务未完成 = object.getInt("我下达的任务未完成");
					moreNum.延期任务 = object.getInt("延期任务");
					moreNum.所有任务 = object.getInt("所有任务");
					moreNum.所有未读任务 = object.getInt("所有未读任务");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Status的值
	 * 
	 * @param data
	 * @param TAG
	 * @return
	 */
	private int getJSON(String data) {
		// 获取json对象
		int status = 0;
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);
			status = jsonObject.getInt("Status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * jsoN解析
	 * 
	 * @param data
	 *            json字符串
	 * @param TAG
	 *            获取的内容的标记
	 * @return
	 */
	private JSONArray getJSONArray(String data, String TAG) {
		JSONArray array = null;
		try {
			// 获取json对象
			JSONObject jsonObject = new JSONObject(data);
			// 通过键名来获取对应的类
			int status = jsonObject.getInt("Status");
			if (status == 1) {
				array = jsonObject.getJSONArray(TAG);
			} else {
				array = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return array;
	}

	private void startWeekThread() {
		demand = new Demand();
		demand.条件 = initDate();
		// progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ResultWeek = mHttpUtils.postSubmit(path,
							JsonUtils.initJsonObj(demand, Demand.class));
					if (getJSON(ResultWeek) == 1) {
						handler.sendEmptyMessage(SUCCESS_WEEK);
					} else {
						handler.sendEmptyMessage(ERROR_WEEK);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 开启获取月数据的线程
	 */
	private void startMounthThread() {
		demand = new Demand();
		demand.条件 = getMounthDay();
		// progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ResultMounth = mHttpUtils.postSubmit(path,
							JsonUtils.initJsonObj(demand, Demand.class));
					if (getJSON(ResultMounth) == 1) {
						handler.sendEmptyMessage(SUCCESS_MOUNTH);
					} else {
						handler.sendEmptyMessage(ERROR_MOUNTH);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 开启获取时间段的数据线程
	 */
	private void startTiemThread() {
		demand = new Demand();
		demand.条件 = getTime();
		// progressDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ResultTime = mHttpUtils.postSubmit(path,
							JsonUtils.initJsonObj(demand, Demand.class));
					if (getJSON(ResultTime) == 1) {
						handler.sendEmptyMessage(SUCCESS_TIME);
					} else {
						handler.sendEmptyMessage(ERROR_TIME);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void setonclicklistener() {
		taskmore_back.setOnClickListener(l);
		taskmore_week.setOnClickListener(l);
		taskmore_mounth.setOnClickListener(l);
		taskmore_date.setOnClickListener(l);
		layout_taskmore_ok.setOnClickListener(l);
		layout_taskmore_no.setOnClickListener(l);
		layout_taskmore_delay.setOnClickListener(l);
		layout_taskmore_all.setOnClickListener(l);
		layout_taskmore_unread.setOnClickListener(l);
		in1.setOnClickListener(l);
		in2.setOnClickListener(l);
		in3.setOnClickListener(l);
		taskmore_all.setOnClickListener(l);
		taskmore_my.setOnClickListener(l);
		taskmore_issud.setOnClickListener(l);
	}

	/**
	 * 圆圈选中颜色,文字选中颜色,圆圈未选中颜色，文字未选中颜色
	 */
	int circle_ok = Color.argb(0xff, 236, 234, 235);
	int text_ok = Color.argb(0xff, 63, 197, 58);
	int circle_no = Color.argb(0xff, 245, 245, 245);
	int text_no = Color.argb(0xff, 150, 150, 150);

	/**
	 * 设置默认的状态是周
	 */
	private void setDefault() {
		taskmore_week.setTextColor(Color.WHITE);
		taskmore_week.setBackgroundResource(R.drawable.textview_style);
		taskmore_info.setText("本周任务详情");
		taskmore_ok.setText("本周完成任务：");
		taskmore_no.setText("本周未完成任务：");
		isTime = "本周";
		isMy = "全部";
		// 全部
		all_relative.setBackgroundColor(circle_ok);
		all_task_zmy.setTextColor(text_ok);
		// 我的
		my_relative.setBackgroundColor(circle_no);
		my_task_zmy.setTextColor(text_no);
		// 我下达的
		inssed_relative.setBackgroundColor(circle_no);
		my_inssed.setTextColor(text_no);

	}

	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.taskmore_back:
				finish();
				break;
			case R.id.taskmore_week:
				// 全部
				all_relative.setBackgroundColor(circle_ok);
				all_task_zmy.setTextColor(text_ok);
				// 我的
				my_relative.setBackgroundColor(circle_no);
				my_task_zmy.setTextColor(text_no);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_no);
				my_inssed.setTextColor(text_no);
				isTime = "";
				setText(taskmore_week);
				isTime = "本周";
				startWeekThread();
				break;
			case R.id.taskmore_mounth:
				// 全部
				all_relative.setBackgroundColor(circle_ok);
				all_task_zmy.setTextColor(text_ok);
				// 我的
				my_relative.setBackgroundColor(circle_no);
				my_task_zmy.setTextColor(text_no);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_no);
				my_inssed.setTextColor(text_no);
				isTime = "";
				isTime = "本月";
				setText(taskmore_mounth);
				startMounthThread();
				break;
			case R.id.taskmore_date:
				// 全部
				all_relative.setBackgroundColor(circle_ok);
				all_task_zmy.setTextColor(text_ok);
				// 我的
				my_relative.setBackgroundColor(circle_no);
				my_task_zmy.setTextColor(text_no);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_no);
				my_inssed.setTextColor(text_no);
				isTime = "";
				isTime = "该时间";
				setText(taskmore_date);
				break;
			case R.id.layout_taskmore_ok:
				isok = "";
				isok = "完成";
				bundle.clear();
				judgeStates(bundle);
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "完成");
				startactivity(bundle);
				break;
			case R.id.layout_taskmore_no:
				isok = "";
				isok = "未完成";
				bundle.clear();
				judgeStates(bundle);
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "未完成");
				startactivity(bundle);
				break;
			case R.id.layout_taskmore_delay:
				isok = "";
				isok = "延期";
				bundle.clear();
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "延期");
				startactivity(bundle);
				break;
			case R.id.layout_taskmore_all:// 跳转到全部任务列表
				startActivity(new Intent(TaskMoreActivity.this,
						TaskTabListActivity.class));
				break;
			case R.id.layout_taskmore_unread:// 跳转到未读任务列表
				Intent intent = new Intent(TaskMoreActivity.this,
						TaskTabListActivity.class);
				intent.putExtra(TaskTabListActivity.IS_UNREAD_TASK, true);
				startActivity(intent);
				break;
			case R.id.taskmore_inok:
				isok = "";
				isok = "完成";
				bundle.clear();
				judgeStates(bundle);
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "完成");
				startactivity(bundle);
				break;
			case R.id.taskmore_inno:
				isok = "";
				isok = "未完成";
				bundle.clear();
				judgeStates(bundle);
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "未完成");
				startactivity(bundle);
				break;
			case R.id.taskmore_in3:
				isok = "";
				isok = "延期";
				bundle.clear();
				bundle.putString("isMy", isMy);
				bundle.putString("isok", "延期");
				startactivity(bundle);
				break;
			case R.id.taskmore_all:
				// 全部
				all_relative.setBackgroundColor(circle_ok);
				all_task_zmy.setTextColor(text_ok);
				// 我的
				my_relative.setBackgroundColor(circle_no);
				my_task_zmy.setTextColor(text_no);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_no);
				my_inssed.setTextColor(text_no);
				isMy = "";
				isMy = "全部";
				taskmore_ok.setText(isTime + "完成任务：");
				taskmore_no.setText(isTime + "未完成任务：");
				setTextNum(moreNum.所有任务已完成, moreNum.所有任务未完成, moreNum.延期任务);
				break;
			case R.id.moretask_my:
				// 全部
				all_relative.setBackgroundColor(circle_no);
				all_task_zmy.setTextColor(text_no);
				// 我的
				my_relative.setBackgroundColor(circle_ok);
				my_task_zmy.setTextColor(text_ok);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_no);
				my_inssed.setTextColor(text_no);
				isMy = "";
				isMy = "我的";
				taskmore_ok.setText(isTime + isMy + "完成任务：");
				taskmore_no.setText(isTime + isMy + "未完成任务：");
				setTextNum(moreNum.我的任务已完成, moreNum.我的任务未完成, moreNum.延期任务);
				break;
			case R.id.moretask_issued:
				// 全部
				all_relative.setBackgroundColor(circle_no);
				all_task_zmy.setTextColor(text_no);
				// 我的
				my_relative.setBackgroundColor(circle_no);
				my_task_zmy.setTextColor(text_no);
				// 我下达的
				inssed_relative.setBackgroundColor(circle_ok);
				my_inssed.setTextColor(text_ok);
				isMy = "";
				isMy = "我下达的";
				taskmore_ok.setText(isTime + isMy + "完成任务：");
				taskmore_no.setText(isTime + isMy + "未完成任务：");
				setTextNum(moreNum.我下达的任务已完成, moreNum.我下达的任务未完成, moreNum.延期任务);
				break;
			}
		}
	};

	private void judgeStates(Bundle bundle) {
		if (("本周").equals(isTime)) {
			bundle.putString("istime", "本周");
		} else if (("本月").equals(isTime)) {
			bundle.putString("istime", "本月");
		} else if (("该时间").equals(isTime)) {
			bundle.putString("istime", "该时间");
			bundle.putString("starttime", startTime);
			bundle.putString("endtime", endTime);
		}
	}

	/**
	 * 跳转
	 * 
	 * @param bundle
	 */
	private void startactivity(Bundle bundle) {
		Intent intent = new Intent(TaskMoreActivity.this,
				TaskListActivity_zmy.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 选中
	 */
	int color = Color.argb(0xff, 0, 153, 255);
	/**
	 * w未选择
	 */
	int colors = Color.argb(0xff, 245, 245, 245);

	private void setText(TextView view) {
		switch (view.getId()) {
		case R.id.taskmore_week:
			/** 未选中字体为蓝色color选中字体为白色 */
			taskmore_week.setBackgroundResource(R.drawable.textview_style);
			taskmore_week.setTextColor(Color.WHITE);
			taskmore_mounth.setBackgroundColor(colors);
			taskmore_mounth.setTextColor(color);
			taskmore_date.setBackgroundColor(colors);
			taskmore_date.setTextColor(color);
			/** 设置下方区域的文字 */
			taskmore_info.setText("本周任务详情");
			taskmore_ok.setText("本周完成任务：");
			taskmore_no.setText("本周未完成任务：");
			taskmore_delay.setText("所有延期任务 ：");
			break;
		case R.id.taskmore_mounth:
			/** 未选中字体为蓝色color选中字体为白色 */
			taskmore_mounth.setBackgroundColor(color);
			taskmore_mounth.setTextColor(Color.WHITE);
			taskmore_week.setBackgroundColor(colors);
			taskmore_date.setBackgroundColor(colors);
			taskmore_week.setTextColor(color);
			taskmore_date.setTextColor(color);
			taskmore_info.setText("本月任务详情");
			taskmore_ok.setText("本月完成任务：");
			taskmore_no.setText("本月未完成任务：");
			taskmore_delay.setText("所有延期任务 ：");
			break;
		case R.id.taskmore_date:
			/** 未选中字体为蓝色color选中字体为白色 */
			taskmore_date.setBackgroundResource(R.drawable.textview_style);
			taskmore_date.setTextColor(Color.WHITE);
			taskmore_week.setBackgroundColor(colors);
			taskmore_mounth.setBackgroundColor(colors);
			taskmore_week.setTextColor(color);
			taskmore_mounth.setTextColor(color);
			Calendar c = Calendar.getInstance();
			// 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
			new DoubleDatePickerDialog(TaskMoreActivity.this, 0,
					new DoubleDatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker startDatePicker,
								int startYear, int startMonthOfYear,
								int startDayOfMonth, DatePicker endDatePicker,
								int endYear, int endMonthOfYear,
								int endDayOfMonth) {
							String textString = String.format(
									"%d-%d-%d  至     %d-%d-%d", startYear,
									startMonthOfYear + 1, startDayOfMonth,
									endYear, endMonthOfYear + 1, endDayOfMonth);
							startTime = startYear + "-"
									+ (startMonthOfYear + 1) + "-"
									+ startDayOfMonth;
							endTime = endYear + "-" + (endMonthOfYear + 1)
									+ "-" + endDayOfMonth;
							taskmore_info.setText(textString);
							startTiemThread();
						}
					}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
							.get(Calendar.DATE), true).show();
			taskmore_ok.setText("该时间内完成任务：");
			taskmore_no.setText("该时间内未完成任务：");
			taskmore_delay.setText("所有延期任务 ：");
			break;
		}
	}

	/** 根据不同的时间设置不同的数量 */
	private void setTextNum(int week, int mounth, int date) {
		taskmore_ok_num.setText(String.valueOf(week));
		taskmore_no_num.setText(String.valueOf(mounth));
		taskmore_delay_num.setText(String.valueOf(date));

	}

	/**
	 * 初始化周历日期
	 */
	private String initDate() {
		Date now = new Date();
		List<String> weekList = ViewHelper.getWeeks(now);
		for (int i = 0; i < weekList.size(); i++) {
			LogUtils.i("QueryWeekTasks", weekList.get(i));
		}
		LogUtils.i("QueryWeekTask", "-----" + ViewHelper.getDateString(now));
		String filter = " 执行时间 >='" + weekList.get(0) + "' and 执行时间<='"
				+ weekList.get(weekList.size() - 1) + "'";
		LogUtils.i("QueryWeekTask", "-----" + weekList.size());
		LogUtils.i("QueryWeekTask", filter);
		return filter;
	}

	/**
	 * 获取当前月的第一天和最后一天
	 */
	private String getMounthDay() {
		String strdate[] = new String[2];
		Calendar cale = Calendar.getInstance();
		// 获取当月第一天和最后一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String firstday, lastday;
		// 获取前月的第一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 0);
		cale.set(Calendar.DAY_OF_MONTH, 1);
		firstday = format.format(cale.getTime());
		// 获取前月的最后一天
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		lastday = format.format(cale.getTime());
		LogUtils.i("out", "本月第一天和最后一天分别是 ： " + firstday + " and " + lastday);
		strdate[0] = firstday;
		strdate[1] = lastday;
		String filter = " 执行时间 >='" + firstday + "' and 执行时间<='" + lastday
				+ "'";
		return filter;
	}

	private String getTime() {
		String filter = " 执行时间 >='" + startTime + "' and 执行时间<='" + endTime
				+ "'";
		return filter;
	}
}
