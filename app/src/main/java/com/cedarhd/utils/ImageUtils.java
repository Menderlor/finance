package com.cedarhd.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.cedarhd.ImagePagerActivity;
import com.cedarhd.helpers.Global;

import java.util.ArrayList;

public class ImageUtils {

	/***
	 * 获取附件文件下载的通用地址
	 * 
	 * @return
	 */
	public static String getDownloadUrlByAddress(String address) {
		return Global.BASE_URL + address;
	}

	/***
	 * 通过附件
	 * 
	 * @return
	 */
	public static String getDownloadUrlById(String attachId) {
		return Global.BASE_URL + "FileUpDownLoad/downloadAttach/" + attachId;
	}

	/***
	 * 根据文件后缀判断该文件是否是图片
	 * 
	 * @param suffix
	 * @return
	 */
	public static boolean isImage(String suffix) {
		if (TextUtils.isEmpty(suffix)) {
			return false;
		}
		if (suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("jpg")
				|| suffix.equalsIgnoreCase("jpeg")
				|| suffix.equalsIgnoreCase("bmp")) {
			return true;
		}
		return false;
	}

	/**
	 * 打开可滑动的图片查看器
	 * 
	 * @param position
	 *            位置
	 * @param urls
	 *            图片路径相对路径集合
	 */
	public static void startImageBrower(Context context, int position,
			ArrayList<String> urls) {

		Intent intent = new Intent(context, ImagePagerActivity.class);
		ArrayList<String> urlList = new ArrayList<String>();
		for (int i = 0; i < urls.size(); i++) {
			urlList.add(Global.BASE_URL + urls.get(i));
		}
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		context.startActivity(intent);
	}

	/**
	 * 打开单张的图片查看器
	 * 
	 * @param url
	 *            图片路径相对路径
	 */
	public static void startSingleImageBrower(Context context, String url) {
		ArrayList<String> urls = new ArrayList<String>();
		urls.add(url);
		startImageBrower(context, 0, urls);
	}
}
