package com.cedarhd.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/***
 * 浏览器相关工具类
 * 
 * @author K
 * 
 */
public class BrowserUtils {
	/**
	 * 打开浏览器
	 */
	public static void openBrowser(Context context, String url) {
		// 输入网站地址
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		// intent.setClassName("com.android.browser",
		// "com.android.browser.BrowserActivity");
		context.startActivity(intent);
	}
}
