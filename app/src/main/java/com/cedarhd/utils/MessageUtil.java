package com.cedarhd.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

public class MessageUtil {

	// 提示信息显示
	public static void ToastMessage(Context context, String msg) {
		CharSequence text = msg;
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void AlertMessage(Context context, String msg) {
		new AlertDialog.Builder(context).setTitle("提示").setMessage(msg)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	public static void AlertMessage(Context context, String title, String msg) {
		try {
			new AlertDialog.Builder(context)
					.setTitle(title)
					.setMessage(msg)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} catch (Exception ex) {

		}
	}

}
