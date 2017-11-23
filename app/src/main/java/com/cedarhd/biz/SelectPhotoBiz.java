package com.cedarhd.biz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import com.cedarhd.SelectPhotoActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.helpers.SharedPreferencesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SelectPhotoBiz {
	public static final String THUMB_PHOTO_PATH = "thumb_photo_path";

	public static final int REQUESTCODE_TAKE_PHOTO = 0X901;
	public static final int REQUESTCODE_SELECT_PHOTO = 0X902;

	/**
	 * 拍照获取图片
	 * 
	 */
	public static void doTakePhoto(Context context) {
		doTakePhoto(context, REQUESTCODE_TAKE_PHOTO);
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	public static void doTakePhoto(Context context, int requestCode) {
		Intent intent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context,
				PreferencesConfig.APP_USER_INFO);

		String photoPath = FilePathConfig.getAvatarDirPath() + File.separator
				+ getPhotoFileName();
		spHelper.putValue(THUMB_PHOTO_PATH, photoPath);

		File file = new File(photoPath);
		Uri imageUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//
		Log.i("RequestCode_take", requestCode + "");
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/***
	 * 选择图片
	 * 
	 * @param context
	 * @param requestCode
	 *            请求码
	 * @param maxPhotoCount
	 *            最大图片数量，默认9张
	 */
	public static void selectPhoto(Context context) {
		selectPhoto(context, REQUESTCODE_SELECT_PHOTO, 9);
	}

	/***
	 * 选择图片
	 * 
	 * @param context
	 * @param maxPhotoCount
	 *            最大图片数量，默认9张
	 */
	public static void selectPhoto(Context context, int maxPhotoCount) {
		selectPhoto(context, REQUESTCODE_SELECT_PHOTO, maxPhotoCount);
	}

	/***
	 * 选择图片
	 * 
	 * @param context
	 * @param requestCode
	 *            请求码
	 * @param maxPhotoCount
	 *            最大图片数量，默认9张
	 */
	public static void selectPhoto(Context context, int requestCode,
			int maxPhotoCount) {
		Intent intent = new Intent(context, SelectPhotoActivity.class);
		intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, maxPhotoCount);
		((Activity) context).startActivityForResult(intent, requestCode);
		Log.i("RequestCode_select", requestCode + "");
	}

	/***
	 * 获取拍照图片路径
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhotoPath(Context context) {
		return new SharedPreferencesHelper(context,
				PreferencesConfig.APP_USER_INFO).getValue(THUMB_PHOTO_PATH);
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

	/***
	 * 获取选择多图片路径
	 * 
	 * @param data
	 * @return
	 */
	public static List<String> getSelectPathListOnActivityForResult(Intent data) {
		ArrayList<String> list = null;
		if (data != null) {
			list = data.getExtras().getStringArrayList(
					SelectPhotoActivity.PHOTO_LIST);
		}
		return list;
	}

}
