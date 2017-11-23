package com.cedarhd.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cedarhd.R;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 显示图片工具类
 * 
 * 
 * @author Administrator
 * 
 */
public class PictureUtils {
	// 头像存放路径
	private String avatarPath = FilePathConfig.getAvatarDirPath();
	private HandlerPicture handler;
	private Context context;

	// 构造函数
	public PictureUtils() {
		handler = new HandlerPicture();
	}

	// 构造函数
	public PictureUtils(Context context) {
		this.context = context;
		handler = new HandlerPicture();
	}

	/**
	 * 处理头像handler
	 * 
	 * @author bohr
	 * 
	 */
	public class HandlerPicture extends Handler {
		// 标示固定
		public final int SHOW_IMAGE_SUCCESS = 3;
		public final int SHOW_IMAGE_FAILUREE = 4;
		public final int GET_PIC_URL = 5;
		public final int GET_PIC_URL_SUCCESS = 6;

		private ImageView ivAvatars;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == SHOW_IMAGE_SUCCESS) {
				int pos = msg.arg1;
				int postion = msg.arg2;
				LogUtils.i("picUrl", pos + "--" + postion);
				ivAvatars = (ImageView) msg.obj;
				Object tag = ivAvatars.getTag();
				if (tag instanceof Bitmap) {
					Bitmap avatar = (Bitmap) ivAvatars.getTag();
					Bitmap zoomAvatar = BitmapHelper.zoomBitmap(avatar, 75, 75);
					if (pos == postion) {
						ivAvatars.setImageBitmap(zoomAvatar);
					}
				} else if (tag instanceof String) {
					String avatarName = (String) ivAvatars.getTag();
					showLocalImg(ivAvatars, avatarName);
				}

			} else if (what == SHOW_IMAGE_FAILUREE) {
				ivAvatars = (ImageView) msg.obj;
				ivAvatars.setImageResource(R.drawable.tx);
			} else if (what == GET_PIC_URL) {
				ImageView ivPic = (ImageView) msg.obj;
				String picUrl = (String) ivPic.getTag();
				setAvatar(picUrl, ivPic); // 设置头像，无图显示默认头像
			} else if (what == GET_PIC_URL_SUCCESS) {
				ImageView ivPic = (ImageView) msg.obj;
				String picUrl = (String) ivPic.getTag();
				showPic(ivPic, picUrl); // 显示图片，功能同setAvatar
			}
		}
	}

	/**
	 * 设置头像 (头像列表中使用)
	 * 
	 * @author k
	 * 
	 * @param avataUrl
	 *            头像地址（没有加服务器地址）
	 * @param ivAvatar
	 *            显示头像的控件
	 * @param postion
	 *            位置
	 */
	public void setAvatar(final String avataUrl, final ImageView ivAvatar,
			int postion) {
		// LogUtils.i("keno98", avataUrl);
		// 如果本地数据库中存有用户头像信息
		if (!TextUtils.isEmpty(avataUrl) && avataUrl.contains("\\")) {
			int index = 0;
			if (avataUrl.contains("\\")) {
				index = avataUrl.lastIndexOf("\\");
			} else if (avataUrl.contains("/")) {
				index = avataUrl.lastIndexOf("/");
			}
			String avatarName = avataUrl
					.substring(index + 1, avataUrl.length()); // 图片名
			// 本地图片文件
			if (!TextUtils.isEmpty(avatarName)) {
				File file = new File(new File(avatarPath), avatarName);
				String path = avatarPath + "/" + avatarName;
				if (file.exists()) { // 本地有图直接使用
					// Bitmap avatar =
					// BitmapFactory.decodeFile(file.toString());
					Bitmap avatar = BitmapHelper.decodeSampleBitmapFromFile(
							path, 120, 120);
					int pos = (Integer) ivAvatar.getTag();
					if (pos == postion) {
						ivAvatar.setImageBitmap(avatar);
					}
				} else {
					// 开启线程从网络下载
					new DownloadThread(postion, avataUrl, ivAvatar, handler)
							.start();
				}
			} else {
				// 开启线程从网络下载
				new DownloadThread(postion, avataUrl, ivAvatar, handler)
						.start();
			}
		} else {
			ivAvatar.setImageResource(R.drawable.tx);
		}
	}

	/**
	 * 设置头像 k
	 * 
	 * @param avataUrl
	 *            头像地址（没有加服务器地址）
	 * @param ivAvatar
	 *            显示头像的控件
	 */
	public void setAvatar(final String avataUrl, final ImageView ivAvatar) {
		// 如果本地数据库中存有用户头像信息
		if (!TextUtils.isEmpty(avataUrl)) {
			int index = 0;
			if (avataUrl.contains("\\")) {
				index = avataUrl.lastIndexOf("\\");
			} else if (avataUrl.contains("/")) {
				index = avataUrl.lastIndexOf("/");
			}
			String avatarName = avataUrl
					.substring(index + 1, avataUrl.length()); // 图片名
			// 本地图片文件
			if (!TextUtils.isEmpty(avatarName)) {
				File file = new File(new File(avatarPath), avatarName);
				if (file.exists()) { // 本地有图直接使用
					LogUtils.i("picUrl1", avataUrl);
					Bitmap avatar = BitmapFactory.decodeFile(file.toString());
					ivAvatar.setImageBitmap(avatar);
				} else {
					// 开启线程从网络下载
					LogUtils.i("picUrl1", "开启线程从网络下载" + avataUrl);
					new DownloadThread(-1, avataUrl, ivAvatar, handler).start();
				}
			} else {
				// 开启线程从网络下载
				new DownloadThread(-1, avataUrl, ivAvatar, handler).start();
			}
		}
	}

	/**
	 * 根据附件号 显示图片,已设置为运行在子线程，直接调用
	 * 
	 * @param context
	 * @param photoId
	 *            附件号
	 * @param imageView
	 *            图片控件
	 */
	public void showPicture(final Context context, final String photoId,
			final ImageView imageView) {
		final ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String photoUrl = mZLServiceHelper.getPhotoAddr(context,
						photoId);
				LogUtils.i("showPicture", photoId + "----" + photoUrl);
				imageView.setTag(photoUrl);
				Message msg = handler.obtainMessage();
				msg.obj = imageView;
				msg.what = handler.GET_PIC_URL_SUCCESS;
				handler.sendMessage(msg);
				// showPic(imageView, photoUrl);
			}
		}).start();
	}

	private void showPic(final ImageView imageView, String photoUrl) {
		try {
			JSONArray jsonArray = new JSONArray(photoUrl);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			String address = (String) jsonObject.get("Address");
			address = address.replace("\\", "/");
			photoUrl = Global.BASE_URL + Global.EXTENSION + address;
			Message msg = handler.obtainMessage();
			imageView.setTag(photoUrl);
			msg.obj = imageView;
			msg.what = handler.GET_PIC_URL;
			handler.sendMessage(msg);
			// photoUrl = address;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	// ///////////////////处理图片拍照，相册选择 ///////////////////

	class DownloadThread extends Thread {
		private int pos;
		private String avataUrl;
		private ImageView ivAvatar;
		public HandlerPicture handler;
		private HttpUtils httpUtils;

		public DownloadThread(int pos, String avataUrl, ImageView ivAvatar,
				HandlerPicture handler) {
			super();
			this.pos = pos;
			this.avataUrl = avataUrl;
			this.ivAvatar = ivAvatar;
			this.handler = handler;
			httpUtils = new HttpUtils();
		}

		@Override
		public void run() {
			if (pos == -1) {
				httpUtils.downloadData(avataUrl, handler, ivAvatar);
			} else {
				httpUtils.downloadData(avataUrl, handler, ivAvatar, pos);
			}
		}
	}

	/**
	 * 显示本地图片
	 * 
	 * @param ivAvatar
	 *            图片控件
	 * @param avatarName
	 *            图片名
	 */
	private void showLocalImg(final ImageView ivAvatar, String avatarName) {
		if (!TextUtils.isEmpty(avatarName)) {
			File file = new File(new File(avatarPath), avatarName);
			if (file.exists()) { // 本地有图直接使用
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				Bitmap avatar = BitmapHelper.zoomBitmap(bitmap,
						ViewHelper.dip2px(context, 75),
						ViewHelper.dip2px(context, 75));
				ivAvatar.setImageBitmap(avatar);
			}
		}
	}
}
