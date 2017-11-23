package com.cedarhd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.CalendarHelper;
import com.cedarhd.helpers.LunarCalendarHelper;
import com.cedarhd.utils.LogUtils;

import java.text.SimpleDateFormat;

/**
 * 日历gridview中的每一个item显示的textview
 *
 */
public class SingleCalendarAdapter extends BaseAdapter {
	private boolean isLeapyear = false; // 是否为闰年

	private int daysOfMonth = 0; // 某月的天数
	/** 指定月的第一天是星期几 */
	private int dayOfWeek = 0; //

	/*** 选中某一天是星期几 */
	private int dayOfSelectedWeek = 0;
	private int lastDaysOfMonth = 0; // 上一个月的总天数

	/** 包含上月日期个数 */
	private int lastMonthItemCount = 0;

	/** 包含下月日期个数 */
	private int nextMonthItemCount = 0;

	private Context context;
	private String[] dayNumber = new String[7]; // 一个gridview中的日期存入此数组中
	// private static String week[] = {"周日","周一","周二","周三","周四","周五","周六"};
	private CalendarHelper sc = null;
	private LunarCalendarHelper lc = null;
	private Resources res = null;
	private Drawable drawable = null;

	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	private int currentFlag = -1; // 用于标记当天
	private int[] schDateTagFlag = null; // 存储当月所有的日程日期

	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	private String cyclical = ""; // 天干地支

	/***
	 *
	 * @param context
	 * @param rs
	 * @param jumpMonth
	 * @param jumpYear
	 * @param year_c
	 *            当前年
	 * @param month_c
	 *            当前月份
	 * @param day_c
	 *            当前日期
	 */
	public SingleCalendarAdapter(Context context, Resources rs, int jumpMonth,
								 int jumpYear, int year_c, int month_c, int day_c) {
		this.context = context;
		sc = new CalendarHelper();
		lc = new LunarCalendarHelper();
		this.res = rs;

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
		currentDay = String.valueOf(day_c); // 得到当前日期是哪天

		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth), Integer.parseInt(currentDay));

	}

	public SingleCalendarAdapter(Context context, Resources rs, int year,
								 int month, int day) {
		this.context = context;
		sc = new CalendarHelper();
		lc = new LunarCalendarHelper();
		this.res = rs;
		currentYear = String.valueOf(year);// 得到跳转到的年份
		currentMonth = String.valueOf(month); // 得到跳转到的月份
		currentDay = String.valueOf(day); // 得到跳转到的天
		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth), Integer.parseInt(currentDay));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar_item_week, null);
		}
		TextView textView = (TextView) convertView
				.findViewById(R.id.tv_day_calendar_week_item);
		String d = dayNumber[position].split("\\.")[0];
		SpannableString sp = new SpannableString(d);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0,
				d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f), 0, d.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// sp.setSpan(new ForegroundColorSpan(Color.MAGENTA), 14, 16,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);

		int selectedDay = Integer.parseInt(currentDay);

		// 下月天數
		int endDays = selectedDay + 7 - dayOfSelectedWeek - daysOfMonth;

		int startDays = selectedDay - (dayOfSelectedWeek - 1);

		if (dayOfSelectedWeek == position + 1) {
			// 设置当天的背景
			// drawable = res.getDrawable(R.drawable.calendar_item_selected_bg);
			textView.setBackground(context.getResources().getDrawable(
					R.drawable.calendar_date_focused));
			textView.setTextColor(Color.WHITE);
		} else {
			if (((endDays > 0) && position < 7 - endDays)
					|| (startDays < 0 && position > Math.abs(startDays))
					|| (startDays >= 0 && endDays <= 0)) {// 當月
				// 当前月信息显示
				textView.setTextColor(Color.BLACK);// 当月字体设黑
				drawable = new ColorDrawable(Color.rgb(23, 126, 214));
				if (position % 7 == 5 || position % 7 == 6) {
					// 当前月信息显示
					textView.setTextColor(Color.rgb(23, 126, 214));// 当月字体设黑
					drawable = new ColorDrawable(Color.rgb(23, 126, 214));
				}
			}
			textView.setBackgroundColor(context.getResources().getColor(
					R.color.white));
		}
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month, int day) {
		isLeapyear = sc.isLeapYear(year);
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonthFirst(year, month); // 某月第一天为星期几
		dayOfSelectedWeek = sc.getWeekdayOfMonth(year, month, day);
		if (dayOfSelectedWeek == 0) {
			// 星期天
			dayOfSelectedWeek = 7;
		}
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		LogUtils.d("DAY", isLeapyear + " ======  " + daysOfMonth
				+ "  ============  " + dayOfWeek + "  =========   "
				+ lastDaysOfMonth);
		getweek(year, month);
	}

	/** 将一个月中的每一天的值添加入数组dayNuber中 **/
	private void getweek(int year, int month) {
		// 农历文字
		String lunarDay = "";
		// 得到当前月的所有日程日期(这些日期需要标记)
		int selectedDay = Integer.parseInt(currentDay);

		// 选中日期的所在星期的周一
		int startDays = selectedDay - (dayOfSelectedWeek - 1);
		int endDays = selectedDay + (7 - dayOfSelectedWeek);

		// 包含上月天数
		int lastMonthDay = dayOfWeek - 1;
		lastMonthItemCount = 0;
		nextMonthItemCount = 0;
		for (int i = 1; i <= dayNumber.length; i++) {
			if (startDays <= 0) {// 包含上月末
				if (lastMonthDay > 0) { // 遍历上月末几天
					int temp = lastDaysOfMonth - lastMonthDay + 1;
					lunarDay = lc
							.getLunarDate(year, month - 1, temp + i, false);
					dayNumber[i - 1] = (temp) + "." + lunarDay;
					lastMonthDay--;
					lastMonthItemCount++; // 记录上月天数
				} else {
					startDays = 0;
					i--;
				}
				startDays++;
			} else if (endDays <= daysOfMonth) { // 居中
				int distance = i - dayOfSelectedWeek;
				String day = String.valueOf(selectedDay + distance); // 得到的日期
				lunarDay = lc.getLunarDate(year, month, i - dayOfSelectedWeek
						+ 1, false);
				dayNumber[i - 1] = selectedDay + distance + "." + lunarDay;

				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // 包含下月初
				int distance = i - dayOfSelectedWeek;
				int day = selectedDay + distance;
				if (day > daysOfMonth) {
					day -= daysOfMonth;
				}
				lunarDay = lc.getLunarDate(year, month + 1, day, false);
				dayNumber[i - 1] = day + "." + lunarDay;
			}
		}
		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
		LogUtils.d("DAYNUMBER", abc);
	}

	public void matchScheduleDate(int year, int month, int day) {

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
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 *
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
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

	/***
	 * 选中标记
	 *
	 * @param pos
	 */
	public void markSelected(int pos) {
		dayOfSelectedWeek = pos + 1;
		notifyDataSetChanged();
	}

	/**
	 * 得到本月第一个Item的位置
	 *
	 * @return
	 */
	public int getFirstVisblePos() {
		return lastMonthItemCount;
	}

	/**
	 * 得到下月 日期的个数
	 *
	 * @return
	 */
	public int getNextVisblePos() {
		return nextMonthItemCount;
	}
}
