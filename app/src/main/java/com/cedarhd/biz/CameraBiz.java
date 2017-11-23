package com.cedarhd.biz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.cedarhd.constants.FilePathConfig;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class CameraBiz {
	/** 拍照 */
	public static final int CAMERA_TAKE_PHOTO = 300;

	private String mPictureName;

	/**
	 * 拍照获取图片
	 * 
	 */
	public void takePhoto(Context context) {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			mPictureName = getPhotoFileName();
			File file = new File(FilePathConfig.getAvatarDirPath(),
					mPictureName);
			Uri imageUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//
			// 把照片保存在sd卡中指定位置。
			((Activity) context).startActivityForResult(intent,
					CAMERA_TAKE_PHOTO);
		} else {
			Toast.makeText(context, "没有SD卡", 1).show();
		}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private static String getPhotoFileName() {
		return "IMG"
				+ DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
	}

	/**
	 * 拍照后重新加载
	 * 
	 * 1.先显示本地图片
	 */
	public String getFilePath(int requesCode, Intent data) {
		if (requesCode == CAMERA_TAKE_PHOTO && !TextUtils.isEmpty(mPictureName)) {
			return FilePathConfig.getAvatarDirPath() + File.separator
					+ mPictureName;
		}
		return "";
	}
}
