package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.cedarhd.utils.LogUtils;
import com.tencent.android.tpush.XGPushConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 屏幕显示帮助类
 *
 * @author bohr
 */
public class ViewHelper {
    public static final String FORMAT_STR_DATE_AND_TIME = "yyyy-MM-dd kk:mm:ss";
    public static final String FORMAT_STR_DATE = "yyyy-MM-dd";

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return widthPixels
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度 heightPixels
     *
     * @param context
     * @return heightPixels
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Rect rect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top; // 状态栏高度
        Log.i("statusBarHeight=", "statusBarHeight=" + statusBarHeight);
        return statusBarHeight;
    }

    /**
     * 把px 转化为dip
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;// 密度
        int dip = (int) (px / density + 0.5f);
        return dip;
    }

    /**
     * 把dip转化为px
     *
     * @param context
     * @param dp
     * @return
     */
    public static float dip2px(Context context, float dp) {
        if (context == null) {

        }
        float density = context.getResources().getDisplayMetrics().density;// 密度
        float px = dp * density + 0.5f;
        return px;
    }

    /**
     * 把sp转化为px
     *
     * @param context
     * @param sp
     * @return
     */
    public static float sp2px(Context context, float sp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                context.getResources().getDisplayMetrics());
        return px;
    }

    /**
     * 把px转化为sp
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2sp(Context context, int px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity + 0.5);
    }

    /**
     * 获取当前时间的字符串格式yyyy-MM-dd kk:mm:ss 24小时制
     *
     * @return
     */
    public static String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取今天日期的字符串格式yyyy-MM-dd
     *
     * @return
     */
    public static String getDateToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * 格式化日期为 字符串格式yyyy-MM-dd kk:mm:ss
     *
     * @return
     */
    public static String formatDateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        return sdf.format(date);
    }

    /***
     * 格式化日期为 字符串格式
     *
     * @param date
     *            时间
     * @param format
     *            日期格式化公式，如果非法则采用默认yyyy-MM-dd kk:mm:ss
     * @return
     */
    public static String formatDateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = "";
        try {
            dateStr = sdf.format(date);
        } catch (Exception e) {
            dateStr = new SimpleDateFormat(FORMAT_STR_DATE_AND_TIME)
                    .format(date);
        }
        return dateStr;
    }

    /**
     * 将字符串yyyy-MM-dd转为日期
     *
     * @return 转换失败返回当前日期
     */
    public static Date formatStrToDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date result = new Date();
        if (TextUtils.isEmpty(dateStr)) {
            return result;
        }
        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取今天年月的字符串格式 yyyy-MM
     *
     * @return
     */
    public static String getDateMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(new Date());
    }

    /**
     * 获得本周的日期列表yyyy-MM-dd
     *
     * @return
     */
    public static List<String> getDateThisWeeks() {
        List<String> list = new ArrayList<String>();
        List<String> returnList = new ArrayList<String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        int weekday = date.getDay();// 获得星期中的第几天
        for (; weekday > 0; weekday--) {
            list.add(sdf.format(date));
            date = getYestody(date);
        }
        // 从小到达依次排序，存入returnList
        for (int i = list.size() - 1; i >= 0; i--) {
            returnList.add(list.get(i));
        }

        Date now = new Date();
        // 获得星期中的第几天
        weekday = now.getDay();
        // 多加了一天 2014-09-25 00:00:00
        int count = 8 - weekday;
        for (int i = 0; i < count; i++) {
            now = getTomorrow(now);
            returnList.add(sdf.format(now));
        }
        return returnList;
    }

    /**
     * 根据当前日期获得这一周的日期列表
     *
     * @return
     */
    public static List<String> getWeeks(final Date nowDate) {
        List<String> list = new ArrayList<String>();
        List<String> returnList = new ArrayList<String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = nowDate;
        // Date引用对象， 在字符串存时间值
        String dateValue = sdf.format(nowDate);
        int weekday = date.getDay();// 获得星期中的第几天
        int weekday2 = date.getDay();
        for (; weekday > 0; weekday--) {
            list.add(sdf.format(date));
            date = getYestody(date);
        }
        // 从小到达依次排序，存入returnList
        for (int i = list.size() - 1; i >= 0; i--) {
            returnList.add(list.get(i));
        }

        // 多加了一天 2014-09-25 00:00:00
        int count = 8 - weekday2;
        Date date2 = null;
        try {
            date2 = sdf.parse(dateValue);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < count; i++) {
            date2 = getTomorrow(date2);
            returnList.add(sdf.format(date2));
        }
        return returnList;
    }

    /**
     * 获取昨天日期的字符串格式yyyy-MM-dd
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getDateYestoday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        date = getYestody(date);
        return sdf.format(date);
    }

    /**
     * 获得当前日期的其一天
     *
     * @param date
     * @return
     */
    public static Date getYestody(Date date) {
        int day = date.getDate();
        if (day > 1) {
            date.setDate(day - 1);
        } else { // 如果是本月第一天
            int month = date.getMonth();
            if (month > 1) {
                date.setMonth(month - 1);
                date.setDate(getDateNum(date.getYear(), date.getMonth()));
            } else if (month == 1) { // 1月1号,前一天为上一年的12月31
                date.setYear(date.getYear() - 1);
                date.setMonth(12);
                date.setDate(31);
            }
        }
        return date;
    }

    /**
     * 获得当前日期的后一天
     *
     * @param date
     * @return
     */
    public static Date getTomorrow(Date date) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDate();
        // 获得本月最后一天
        int lastDay = getDateNum(year, month);
        if (day < lastDay) {
            date.setDate(day + 1);
        } else if (day == lastDay) { // 如果是本月最后一天
            if (month < 12) {
                date.setMonth(month + 1);
                date.setDate(1); // 下月1号
            } else if (month == 12) { // 12月31
                date.setYear(date.getYear() + 1);
                date.setMonth(1);
                date.setDate(1);
            }
        }
        LogUtils.d("weekdate", ViewHelper.getDateString(date));
        return date;
    }

    /**
     * 获得当前日期上一周的日期
     *
     * @param date
     * @return
     */
    public static Date getBeforWeekDate(Date date) {
        for (int i = 0; i < 7; i++) {
            date = getYestody(date);
        }
        return date;
    }

    /**
     * 获得当前日期下一周的日期
     *
     * @param date
     * @return
     */
    public static Date getAfterWeekDate(Date date) {
        for (int i = 0; i < 7; i++) {
            date = getTomorrow(date);
        }
        return date;
    }

    /**
     * 计算某年某月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    private static int getDateNum(int year, int month) {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.YEAR, year + 1900);
        time.set(Calendar.MONTH, month);
        return time.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时间的字符串格式yyyy-MM-dd
     *
     * @return
     */
    public static String getDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }

    /**
     * 获取当前时间的字符串格式yyyy-MM-dd
     *
     * @return
     */
    public static String getDateString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date == null) {
            return "";
        }
        return sdf.format(date);
    }

    /**
     * 格式化时间的字符为 yyyy-MM-dd hh:mm:ss
     *
     * @return
     */
    public static String getDateString(String dataStr) {
        // // 将时间字段中的T去除
        Log.i("keno21", "--->Data:" + dataStr);
        if (dataStr.contains(".")) {
            int dotindex = dataStr.indexOf(".");
            dataStr = dataStr.substring(0, dotindex);
        }
        return dataStr;
    }

    /***
     * 格式化日期字符串格式 为指定format格式
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static String convertStrToFormatDateStr(String dateStr, String format) {
        try {
            if (TextUtils.isEmpty(dateStr)) {
                return "";
            }
            Date date = formatStrToDate(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取版本号
     *
     * @param context 应用程序的上下文
     * @return 应用程序的版本号
     */
    public static String getVersionName(Context context) {
        String version = null;
        // 获得包管理器
        PackageManager pm = context.getPackageManager();
        try {
            // 封装了关于该应用程序的所有的功能清单中的数据
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName; // 版本号 1.01，给客户看
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取版本号
     *
     * @param context 应用程序的上下文
     * @return 应用程序的版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        // 获得包管理器
        PackageManager pm = context.getPackageManager();
        try {
            // 封装了关于该应用程序的所有的功能清单中的数据
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode; // 版本号 1.01，给程序看
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取设备的IMEI,唯一的设备标志
     *
     * @param context 应用程序的上下文
     * @return 获取设备的IMEI
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取经过信鸽加密处理的Token
     *
     * @param context 应用程序的上下文
     * @return 获取设备的IMEI
     */
    public static String getDeviceToken(Context context) {
        // 获取设备唯一通行证
        final String token = XGPushConfig.getToken(context);
        return token;
    }

    /**
     * 获取本周一第一天的日期
     */
    public static String getFirstDateStrOfThisWeek() {
        return formatDateToStr(getFirstDateOfThisWeek(), FORMAT_STR_DATE);
    }

    /**
     * 获取本周一最后一天的日期
     */
    public static String getLastDateStrOfThisWeek() {
        return formatDateToStr(getLastDateOfThisWeek(), FORMAT_STR_DATE);
    }

    /**
     * 获取本周一第一天的日期
     */
    public static Date getFirstDateOfThisWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // 获取当前时间 是本周中的第几天 星期一为1
        int dayofWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DATE, -(dayofWeek - 1));
        System.out.println("周一:" + calendar.get(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * 获取本周一最后一天的日期
     */
    public static Date getLastDateOfThisWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getFirstDateOfThisWeek());
        // // 获取当前时间 是本周中的第几天 星期一为1
        // int dayofWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        // System.out.println(dayofWeek + "---" + (dayofWeek + 6));
        // calendar.add(Calendar.DATE, -(dayofWeek - 1));
        // System.out.println("周一:" + calendar.get(Calendar.DATE));
        calendar.add(Calendar.DATE, 6);
        System.out.println("周日:" + calendar.get(Calendar.DATE));

        return calendar.getTime();
    }

    public static Date getLastDateOfThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // calendar.set(Calendar.DAY_OF_MONTH, 1); // 本月第一天
        // System.out.println("月初:" + calendar.get(Calendar.DATE));
        // 本月最后一天
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getMaximum(Calendar.DAY_OF_MONTH));
        // System.out.println("y:" + calendar.get(Calendar.DATE));
        return calendar.getTime();
    }

    public static Date getFirstDateOfThisMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1); // 本月第一天
        System.out.println("月初:" + calendar.get(Calendar.DATE));
        return calendar.getTime();
    }

    public static String getLastDateStrOfThisMonth() {
        return formatDateToStr(getLastDateOfThisMonth(), FORMAT_STR_DATE);
    }

    public static String getFirstDateStrOfThisMonth() {
        return formatDateToStr(getFirstDateOfThisMonth(), FORMAT_STR_DATE);
    }

    /**
     * 转为日期
     *
     * @return 转换失败返回当前日期
     */
    @SuppressLint("SimpleDateFormat")
    public static Date formatStrToDateAndTime(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date result = new Date();
        if (TextUtils.isEmpty(dateStr)) {
            return result;
        }
        try {
            result = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
