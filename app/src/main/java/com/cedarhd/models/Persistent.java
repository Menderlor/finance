package com.cedarhd.models;

import android.content.Context;

public class Persistent {
	// 通知检查间隔时间
	public static Context mContext = null;
	public static int POLLING_INTERVAL = 1000 * 10;
	public static User mUser;
	// 是否是新登录用户
	public static boolean IsNewUser = false;
	public static int mWidthPixels;
	public static int mHeightPixels;
	public static int SECTION_LIST_ADAPTER = 0;
	public static int SECTION_LIST_ADAPTER_CONTACT_HISTORY = 1;
	public static boolean DEBUG_MODE = true;
	public static final String EMAIL_READ = "1";
	public static final String EMAIL_UNREAD = "0";

	// 新服务器地址
	public static final String IP = "www.boeryun.com";
	public static final String BASE_URL = "http://" + IP + ":8076/";
	public static final String BASE_URL_PROCESS = "http://" + IP + ":8076/";

	public static final String EXTENSION = "";
	public static final String PROCESS_URL_HEADER = "http://www.boeryun.com/流程表单/VSheet/sheet/Form?";

	// id是流程表的编号 新建则为0
	// http://www.boeryun.com/流程表单/VSheet/Form?name=订货单&id=1&uname=张少磊&coid=127&pwd=bohrsoft
	public static final int REMIND_NOTICE = 0;
	public static final int REMIND_EMAIL = 1;
	public static final int REMIND_TASK = 2;
	public static final int REMIND_ORDER = 3;
	public static final int REMIND_APPROVAL = 6;
}
