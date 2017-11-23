package com.cedarhd.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import com.cedarhd.control.BoeryunProgressDialog;

/** 显示进度条对话框 */
public class ProgressDialogHelper {
	private static boolean isShow;
	private static BoeryunProgressDialog dialog;

	/**
	 * 显示进度对话框,请稍后(进度框点击返回键是否消失，默认false)
	 */
	public static ProgressDialog show(Context context) {
		return show(context, "请稍候...");
	}

	/**
	 * 显示进度对话框,请稍后(进度框点击返回键是否消失，默认false)
	 */
	public static ProgressDialog show(Context context, boolean isCancelable) {
		return show(context, "请稍候...", isCancelable);
	}

	/**
	 * 显示进度对话框
	 * 
	 * @param context
	 * @param content
	 *            提示文字内容，建议不多于8字
	 * @param isCancelable
	 *            进度框点击返回键是否消失，默认false
	 * @return
	 */
	public static ProgressDialog show(Context context, String content) {
		return show(context, content, false);
	}

	/**
	 * 显示进度对话框
	 * 
	 * @param context
	 * @param content
	 *            提示文字内容，建议不多于8字
	 * @param isCancelable
	 *            进度框点击返回键是否消失，默认false
	 * @return
	 */
	public static ProgressDialog show(Context context, String content,
			boolean isCancelable) {
		if (isShow && dialog != null) {
			return dialog;
		}
		dialog = new BoeryunProgressDialog(context, content);
		dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
		dialog.setCancelable(isCancelable);
		// dialog.setMessage(content + "");
		dialog.show();
		isShow = true;
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				isShow = false;
			}
		});
		return dialog;
	}

	/**
	 * 隐藏进度对话框
	 */
	public static void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}