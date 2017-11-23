package com.cedarhd.utils;

import android.os.Environment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class MemoryUtils {
	private static final String LOG_TAG = "MemoryUtils";
	private static final String KEY_OutOfMemoryError = "OutOfMemoryError";
	private static final String KEY_NullPointerException = "NullPointerException";

	private static final UncaughtExceptionHandler mUncaughtExceptionHandler = new UncaughtExceptionHandler();
	private static final ThreadLocal<HashMap<String, Boolean>> mThreadLocal = new ThreadLocal<HashMap<String, Boolean>>();

	public static void makeDumpHprofData() {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/currentdump"
					+ df.format(new Date())
					+ ".hprof";
			android.os.Debug.dumpHprofData(path);
			LogUtils.i(LOG_TAG, "生成内存快照：" + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拦截所有线程的所有异常。作该操作的主要原因：如果想在某个特定的时刻对某些线程进行拦截，很有可能无法在设置完handler的时刻立即拦截到。
	 * 因此通常进行全局的拦截
	 **/
	public static void startCaughtAllException() {
		Thread.setDefaultUncaughtExceptionHandler(mUncaughtExceptionHandler);
	}

	public static class CatchOutOfMemoryError {
		public static void start() {
			if (mThreadLocal.get() == null)
				mThreadLocal.set(new HashMap<String, Boolean>());
			mThreadLocal.get().put(KEY_OutOfMemoryError, true);
			Thread.currentThread().setUncaughtExceptionHandler(
					mUncaughtExceptionHandler);
		}
	}

	public static class CatchNullPointerException {
		public static void start() {
			if (mThreadLocal.get() == null)
				mThreadLocal.set(new HashMap<String, Boolean>());
			mThreadLocal.get().put(KEY_NullPointerException, true);
			Thread.currentThread().setUncaughtExceptionHandler(
					mUncaughtExceptionHandler);
		}
	}

	private static class UncaughtExceptionHandler implements
			Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable e) {
			Class clazz = e.getClass();
			LogUtils.e(LOG_TAG,
					"拦截异常：" + clazz.getName() + "，线程：" + thread.getName() + e);
			if (mThreadLocal.get() != null
					&& mThreadLocal.get().get(KEY_OutOfMemoryError)
					&& clazz.equals(OutOfMemoryError.class)) {
				try {
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/outofmemorydump"
							+ df.format(new Date()) + ".hprof";
					android.os.Debug.dumpHprofData(path);
					LogUtils.i(LOG_TAG, "生成内存快照：" + path);
				} catch (IOException o) {
					o.printStackTrace();
				}
			} else if (mThreadLocal.get() != null
					&& mThreadLocal.get().get(KEY_NullPointerException)
					&& clazz.equals(NullPointerException.class)) {

			}
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
