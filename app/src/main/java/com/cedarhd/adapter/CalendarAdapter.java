package com.cedarhd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.CalendarHelper;
import com.cedarhd.helpers.LunarCalendarHelper;
import com.cedarhd.models.每日任务数量;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 日历gridview中的
 * 
 * 
 */
@SuppressLint("NewApi")
public class CalendarAdapter extends BaseAdapter {
	/** 是否为闰年 */
	private boolean isLeapyear = false;

	/** 某月的天数 */
	private int daysOfMonth = 0;

	/** 该月第一天是星期几 */
	private int dayOfWeek_first = 0;

	/** 上一个月的总天数 */
	private int lastDaysOfMonth = 0;

	private Context context;
	/** gridview中的日期存入此数组中 个数为35或42 */
	private String[] dayNumber;

	private List<每日任务数量> mCountList;
	/**
	 * 日历显示的格数
	 */
	private int gridCount = 35;

	private CalendarHelper sc = null;
	private LunarCalendarHelper lc = null;
	private Resources res;
	private Drawable drawable;

	private String currentYear = "";
	private String currentMonth = "";

	/*** 用于标记当天 */
	private int currentFlag = -1; //

	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	private String cyclical = ""; // 天干地支

	/***
	 * 
	 * @param context
	 * @param jumpMonth
	 *            跳转月份 偏移量
	 * @param jumpYear
	 *            跳转年份 偏移量
	 * @param year_c
	 *            当前年份
	 * @param month_c
	 *            当前月份
	 * @param day_c
	 *            当前日期
	 */
	public CalendarAdapter(Context context, int jumpMonth, int jumpYear,
			int year_c, int month_c, int day_c, int gridCount) {
		this.context = context;
		sc = new CalendarHelper();
		lc = new LunarCalendarHelper();
		mCountList = new ArrayList<每日任务数量>();
		this.res = context.getResources();
		this.gridCount = gridCount;

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		currentYear = String.valueOf(stepYear); // 得到当前的年份
		currentMonth = String.valueOf(stepMonth); // 得到本月
													// （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth), day_c);
	}

	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public String getItem(int position) {
		return dayNumber[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			DisplayMetrics display = context.getResources().getDisplayMetrics();
			int Width = display.widthPixels;
			int cellWidth = Width / 7;
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, cellWidth);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar_item, null);
			convertView.setLayoutParams(params);
		}
		LinearLayout llItem = (LinearLayout) convertView
				.findViewById(R.id.ll_calendar_item);
		TextView textView = (TextView) convertView
				.findViewById(R.id.tv_day_calendar_item);
		TextView tvLunar = (TextView) convertView
				.findViewById(R.id.tv_lunar_calendar_item);
		String day = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];
		ProgressBar pBar = (ProgressBar) convertView
				.findViewById(R.id.pbar_calendar_item);
		SpannableString sp = new SpannableString(day);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0,
				day.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f), 0, day.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (dv != null || dv != "") {
			// sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1,
			// dayNumber[position].length(),
			// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			tvLunar.setText(dv);
		}
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);
		tvLunar.setTextColor(context.getResources().getColor(R.color.text_info));

		String monthStr = currentMonth.length() == 1 ? "0" + currentMonth
				: currentMonth;
		String dayStr = day.length() == 1 ? "0" + day : day;
		String date = currentYear + "-" + monthStr + "-" + dayStr;
		每日任务数量 countTask = getTaskCountByDate(date);
		pBar.setVisibility(View.VISIBLE);
		if (currentFlag - 1 == position) {
			// 设置当天的背景
			drawable = new ColorDrawable(Color.rgb(23, 126, 214));
			// textView.setBackgroundDrawable(drawable);
			llItem.setBackground(context.getResources().getDrawable(
					R.drawable.calendar_date_focused));
			textView.setTextColor(context.getResources()
					.getColor(R.color.white));
			tvLunar.setTextColor(context.getResources().getColor(R.color.white));

			if (countTask != null && countTask.任务数量 > 0) {
				pBar.setVisibility(View.VISIBLE);
				pBar.setMax((int) countTask.任务数量);
				pBar.setProgress((int) countTask.完成数量);
			} else {
				pBar.setVisibility(View.GONE);
			}
		} else {
			if (position + 1 < daysOfMonth + dayOfWeek_first
					&& position + 1 >= dayOfWeek_first) {
				// 当前月信息显示
				textView.setTextColor(Color.BLACK);// 当月字体设黑
				drawable = new ColorDrawable(Color.rgb(23, 126, 214));
				if (position % 7 == 5 || position % 7 == 6) {
					// 显示周末颜色
					textView.setTextColor(Color.rgb(23, 126, 214));// 当月字体设黑
					tvLunar.setTextColor(Color.rgb(23, 126, 214));
					drawable = new ColorDrawable(Color.rgb(23, 126, 214));
				}

				if (countTask != null && countTask.任务数量 > 0) {
					pBar.setVisibility(View.VISIBLE);
					pBar.setMax((int) countTask.任务数量);
					pBar.setProgress((int) countTask.完成数量);
				} else {
					pBar.setVisibility(View.GONE);
				}
			} else {
				convertView.setVisibility(View.GONE);// 隐藏非本月的日期
			}
			llItem.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month, int day_c) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek_first = sc.getWeekdayOfMonthFirst(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		// 标记默认选中的颜色
		currentFlag = dayOfWeek_first - 1 + day_c;
		LogUtils.d("DAY", isLeapyear + " ======  " + daysOfMonth
				+ "  ============  " + dayOfWeek_first + "  =========   "
				+ lastDaysOfMonth);
		getweek(year, month);
	}

	/*** 将一个月中的每一天的值添加入数组dayNuber中 */
	private void getweek(int year, int month) {
		int j = 1;
		String lunarDay = "";
		if (dayOfWeek_first == 0) {// 星期天放在末
			dayOfWeek_first = 7;
		}
		// int count = dayOfWeek_first - 1 + daysOfMonth;
		// if (count > 35) {
		// dayNumber = new String[gridCount];
		// } else {
		// // 区分5行和6行的情况
		// // dayNumber = new String[35];
		//
		// dayNumber = new String[gridCount];
		// }

		dayNumber = new String[gridCount];
		// 得到当前月的所有日程日期(这些日期需要标记)
		for (int i = 1; i <= dayNumber.length; i++) {
			if (i < dayOfWeek_first) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek_first + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i - 1] = (temp + i) + "." + lunarDay;
			} else if (i < daysOfMonth + dayOfWeek_first) { // 本月
				lunarDay = lc.getLunarDate(year, month,
						i - dayOfWeek_first + 1, false);
				dayNumber[i - 1] = i - dayOfWeek_first + 1 + "." + lunarDay;
				// 对于当前月才去标记当前日期
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // 下一个月
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i - 1] = j + "." + lunarDay;
				j++;
			}
		}

		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
		LogUtils.d("DAYNUMBER", abc);
	}

	/**
	 * 点击每一个item时返回item中的日期
	 * 
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position) {
		return dayNumber[position];
	}

	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * 
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek_first + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * 
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek_first + daysOfMonth + 7) - 1;
	}

	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}

	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}

	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}

	public void setmCountList(List<每日任务数量> mCountList) {
		this.mCountList = mCountList;
	}

	/***
	 * 选中标记
	 * 
	 * @param pos
	 */
	public void markSelected(int pos) {
		currentFlag = pos + 1;
		notifyDataSetChanged();
	}

	/***
	 * 根据任务日期获得任务数量
	 * 
	 * @param dateStr
	 * @return
	 */
	private 每日任务数量 getTaskCountByDate(String dateStr) {
		Log.d("foreach", dateStr);
		for (每日任务数量 item : mCountList) {
			Log.i("foreach", item.执行日期 + "---" + item.任务数量);
			if (!TextUtils.isEmpty(item.执行日期) && item.执行日期.equals(dateStr)) {
				return item;
			}
		}
		return null;
	}
}
