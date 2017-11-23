package com.cedarhd.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtil {
	/** 将json时间类型 转换成String，格式为 yyyy-MM-dd HH:mm */
	public static String ConvertLongDateToString(String date_s) {
		String str = "";
		try {
			Date date = null;
			String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
			Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
			Matcher matcher = pattern.matcher(date_s);
			String result = matcher.replaceAll("$2");
			date = new Date(new Long(result));

			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			str = format.format(date);
		} catch (Exception ex) {
		}
		return str;
	}

	/** 转换成 yyyy-MM-dd 格式 */
	public static String ConvertDateToString(String date_s) {
		String str = "";
		try {
			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd");

			Date date = format.parse(date_s);

			str = format.format(date);
		} catch (Exception ex) {
		}
		return str;
	}

	/** 转换成 yyyy-MM-dd 格式 */
	public static Date ConvertStringDateToDate(String date_s) {
		Date date = null;
		try {
			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd");
			date = format.parse(date_s);
		} catch (Exception ex) {
		}
		return date;
	}

	/** 转换成 yyyy-MM-dd HH:mm 格式 */
	public static String ConvertLongDateToString(Date date) {
		String str = "";
		try {
			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			str = format.format(date);
		} catch (Exception ex) {
		}
		return str;
	}

	/** 将 yyyy-MM-dd HH:mm 格式 的字符日期 转为Date */
	public static Date ConvertStringToLongDate(String date) {
		Date dateReturn = null;
		try {
			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			dateReturn = format.parse(date);
		} catch (Exception ex) {
		}
		return dateReturn;
	}

	/** 转换成yyyy-MM-dd 格式 */
	public static String ConvertDateToString(Date date) {
		String str = "";
		try {
			java.text.SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd");
			str = format.format(date);
		} catch (Exception ex) {
		}
		return str;
	}

	/**
	 * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
	 * 
	 * @param timeStamp
	 *            毫秒
	 * @return
	 */
	public static String convertTimeToFormat(long timeStamp) {
		long curTime = System.currentTimeMillis();
		long time = (curTime - timeStamp) / (long) 1000;
		LogUtils.i("updateTime2", time + "s");
		if (time < 10 && time >= 0) {
			return "刚刚";
		} else if (time < 60 && time >= 10) {
			return time + "秒前";
		} else if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		} else if (time >= 3600 && time < 3600 * 24) {
			return time / 3600 + "小时前";
		} else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
			return time / 3600 / 24 + "天前";
		} else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 + "个月前";
		} else if (time >= 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 / 12 + "年前";
		} else {
			return "刚刚";
		}
	}
}
