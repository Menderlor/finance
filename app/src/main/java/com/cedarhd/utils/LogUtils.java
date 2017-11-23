package com.cedarhd.utils;

import android.util.Log;

/***
 * 自定义打印日志，当发布时隐藏除了错误信息之外的所有日志
 *
 * @author K 2015/06/26 09:39
 */
public class LogUtils {

    // 打印级别
    private final static int LEVEL = 0;

    /***
     * 调试模式，打印日志信息
     */
    public final static int DEBUG_MODE = 0;

    /***
     * 发布模式
     */
    public final static int RELEASE_MODE = 1;

    /***
     * 打印信息
     *
     * @param tag
     * @param msg
     *            信息内容
     */
    public static void i(String tag, String msg) {
        if (LEVEL == DEBUG_MODE) {
            Log.i(tag, StrUtils.pareseNull(msg));
        }
    }

    /***
     * 打印信息
     *
     * @param tag
     * @param msg
     *            信息内容
     */
    public static void d(String tag, String msg) {
        if (LEVEL == DEBUG_MODE) {
            Log.d(tag, StrUtils.pareseNull(msg));
        }
    }

    /***
     * 打印信息
     *
     * @param tag
     * @param msg
     *            信息内容
     */
    public static void v(String tag, String msg) {
        if (LEVEL == DEBUG_MODE) {
            Log.v(tag, StrUtils.pareseNull(msg));
        }
    }

    /***
     * 打印信息
     *
     * @param tag
     * @param msg
     *            信息内容
     */
    public static void e(String tag, String msg) {
        if (LEVEL == DEBUG_MODE) {
            Log.e(tag, StrUtils.pareseNull(msg));
        }
    }
}
