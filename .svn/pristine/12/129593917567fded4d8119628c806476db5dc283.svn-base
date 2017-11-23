package com.cedarhd.helpers;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/***
 * 键盘/输入法帮助类
 * 
 * @author K
 * 
 */
public class InputSoftHelper {

	/***
	 * 隐藏输入法软键盘
	 * 
	 * @param context
	 *            当前页面
	 * @param eText
	 *            请求弹出软件盘的文本框
	 */
	public static void hiddenSoftInput(Context context, EditText eText) {
		((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(eText.getWindowToken(), 0);
	}
}
