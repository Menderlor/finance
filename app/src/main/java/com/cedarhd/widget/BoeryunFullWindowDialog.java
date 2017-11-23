package com.cedarhd.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

/***
 * 自定义全屏对话框
 * 
 * @author K
 * 
 */
public class BoeryunFullWindowDialog extends AlertDialog {

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public BoeryunFullWindowDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		this(context, 0);
	}

	/**
	 * @param context
	 * @param theme
	 */
	public BoeryunFullWindowDialog(Context context, int theme) {
		super(context, android.R.style.Theme);
		setOwnerActivity((Activity) context);
	}

	/**
	 * @param context
	 */
	public BoeryunFullWindowDialog(Context context) {
		this(context, 0);
	}

}
