package com.cedarhd.helpers;

import java.util.Calendar;

/**
 * 闰年月算法
 * 
 * @author Vincent Lee
 * 
 */
public class CalendarHelper {

	private int daysOfMonth = 0; // 某月的天数

	/** 指定月第一天是星期几 */
	private int dayOfWeek = 0; //

	// 判断是否为闰年
	public boolean isLeapYear(int year) {
		if (year % 100 == 0 && year % 400 == 0) {
			return true;
		} else if (year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	// 得到某月有多少天数
	public int getDaysOfMonth(boolean isLeapyear, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			daysOfMonth = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			daysOfMonth = 30;
			break;
		case 2:
			if (isLeapyear) {
				daysOfMonth = 29;
			} else {
				daysOfMonth = 28;
			}

		}
		return daysOfMonth;
	}

	/** 指定某年中某月的第一天是星期几 */
	public int getWeekdayOfMonthFirst(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

	/** 指定某一天是星期几 */
	public int getWeekdayOfMonth(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
		return dayOfWeek;
	}

	/**
	 * 根据一周中所在天数返回对应中文 0：周日; 1：周一，2：周二，...6：周六
	 */
	public static String getWeekdayOfMonth(int weekDay) {
		String weekStr = "";
		switch (weekDay) {
		case 0:
			weekStr = "周日";
			break;
		case 1:
			weekStr = "周一";
			break;
		case 2:
			weekStr = "周二";
			break;
		case 3:
			weekStr = "周三";
			break;
		case 4:
			weekStr = "周四";
			break;
		case 5:
			weekStr = "周五";
			break;
		case 6:
			weekStr = "周六";
			break;
		}
		return weekStr;
	}
}
