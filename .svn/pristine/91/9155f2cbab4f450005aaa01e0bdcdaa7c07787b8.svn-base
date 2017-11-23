package com.cedarhd.helpers;

import android.text.TextUtils;

import com.cedarhd.utils.LogUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateDeserializer implements JsonDeserializer<Date> {

	public Date deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		String JSONDateToMilliseconds = "\\/(Date\\((.*?)(\\+.*)?\\))\\/";
		Pattern pattern = Pattern.compile(JSONDateToMilliseconds);
		Matcher matcher = pattern.matcher(json.getAsJsonPrimitive()
				.getAsString());
		String result = matcher.replaceAll("$2");
		return new Date(new Long(result));
	}

	/**
	 * 判断两个yyyy-mm-dd的字符串时间大小
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 开始日期小于结束日期返回true,否则返回false
	 */
	public static Boolean compareDate(String startDate, String endDate) {
		Boolean result = false;
		String[] ss = startDate.split("-");
		String[] es = endDate.split("-");
		int[] starts = new int[ss.length];
		int[] ends = new int[es.length];
		for (int i = 0; i < ss.length; i++) {
			if (ss.length > 2) {
				ss[i] = ss[i].substring(0, 2);
			}
			starts[i] = Integer.valueOf(ss[i]);
		}

		for (int i = 0; i < es.length; i++) {
			if (es.length > 2) {
				es[i] = es[i].substring(0, 2);
			}
			ends[i] = Integer.valueOf(es[i]);
		}

		// 判断,当开始时间大于结束时间直接返回false
		for (int i = 0; i < ends.length; i++) {
			if (starts[i] > ends[i]) {
				return false;
			}
			result = true;
		}
		return result;
	}

	/**
	 * 判断指定时间是否在当前时间之前
	 */
	public static Boolean dateIsBeforoNow(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Boolean result = false;
		try {
			Date date = sdf.parse(dateStr);
			Date nowDate = new Date();
			nowDate.setHours(0);
			nowDate.setMinutes(0);
			nowDate.setSeconds(0);
			LogUtils.i("nowDate", nowDate.toString());
			if (date.before(nowDate)) {
				result = true;
			}
		} catch (ParseException e) {
			LogUtils.e("nowDate", e + "");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 判断是否是昨天
	 * 
	 * @param endDate
	 *            判断日期
	 */
	private static String getYestoday(String endDate) {
		String result = showDate(endDate);
		String startDate = getTodayDate();
		if (endDate.substring(0, 8).equals(startDate.substring(0, 8))) { // 年月相同
			// 判断,
			String today = startDate.substring(8, 10); // 今天号数
			String day = endDate.substring(8, 10); // 比较号数

			try {
				if (Integer.parseInt(today) - Integer.parseInt(day) == 1) {
					result = "昨天";
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return result;
	}

	/**
	 * 格式化时间
	 * 
	 * 显示今天，昨天 时间显示到分
	 * 
	 * @param date
	 * @return
	 */
	public static String getFormatTime(String dateStr) {
		String result = dateStr;
		String date = "";
		String time = "";
		if (dateStr == null || "".equals(dateStr)) {
		} else {
			if (dateStr.contains(" ")) {
				String[] arr = dateStr.split(" ");
				date = arr[0];
				time = showTime(arr[1]); // 时间
				if (date.equals(getTodayDate())) {
					result = "今天 " + time;
				} else if (compareDate(date, getTodayDate())) { // 如果是今天以前的日期
					result = getYestoday(date) + " " + time;
				} else {
					result = showDate(date) + " " + time;
				}
			}
		}
		return result;
	}

	/**
	 * 格式化时间
	 * 
	 * 显示今天，时间显示为 时分 HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String getFormatShortTime(String dateStr) {
		String time = "";
		if (dateStr == null || "".equals(dateStr)) {
		} else {
			if (dateStr.contains(" ")) {
				String[] arr = dateStr.split(" ");
				time = showTime(arr[1]); // 时间
			}
		}
		return time;
	}

	/**
	 * 格式化日期
	 * 
	 * 显示今天，昨天，如3-09等，不要时分秒
	 * 
	 * @param date
	 * @return
	 */
	public static String getFormatDate(String dateStr) {
		if (TextUtils.isEmpty(dateStr)) {
			return "";
		}
		String result = dateStr;
		String date = dateStr;
		if (date.length() > 10) {
			date = date.substring(0, 10);
		}
		if (date.equals(getTodayDate())) {
			result = "今天 ";
		} else if (compareDate(date, getTodayDate())) { // 如果是今天以前的日期
			result = getYestoday(date);
		} else {
			result = showDate(date);
		}
		return result;
	}

	/**
	 * 获得当天日期 yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		Date date = new Date(); // 获取当前时间
		String p = "yyyy-MM-dd"; // 自定义时间格式
		SimpleDateFormat sdf = new SimpleDateFormat(p);
		String str = sdf.format(date);// 格式化
		return str;
	}

	/**
	 * 时间显示到分
	 * 
	 * @param time
	 * @return
	 */
	private static String showTime(String time) {
		String result = time;
		if (time.contains(":") && time.length() > 5) {
			result = time.substring(0, 5);
		}
		return result;
	}

	/**
	 * 如果是本年则不显示年
	 * 
	 * @param time
	 * @return
	 */
	private static String showDate(String time) {
		String result = time;
		String yearOfToday = getTodayDate().substring(0, 5);
		if (time.contains(yearOfToday)) {
			result = time.replace(yearOfToday, "");
		}
		return result;
	}

	/**
	 * 如果null,转为“”
	 * 
	 * @param str
	 * @return
	 */
	public static String formatEmpty(String str) {
		if (str == null) {
			str = "";
		}
		return str;
	}
}