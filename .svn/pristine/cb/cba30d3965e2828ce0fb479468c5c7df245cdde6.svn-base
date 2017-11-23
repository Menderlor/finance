package com.cedarhd.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.PhotoInfo;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PhotoHelper {
	private static String TAG = "PhotoHelper";
	public static ZLServiceHelper mZLServiceHelper;
	public static final String PATH = FilePathConfig.getAvatarDirPath();
	public static final String CACHEPATH = Environment
			.getExternalStorageDirectory() + "/DCIM/CACHE";
	public ORMDataHelper helper;

	public PhotoHelper(Context context) {
		helper = ORMDataHelper.getInstance(context);
		mZLServiceHelper = new ZLServiceHelper();
	}

	/**
	 * 查找图片的缓存
	 * 
	 * @param photoNo
	 *            图片的ID号,如1123
	 * @return 图片的缓存地址
	 */
	public String searchPhotoCache(String photoNo) {
		PhotoInfo info = new PhotoInfo();
		info.setPhotoSerialNo(photoNo);
		Dao<PhotoInfo, Integer> daoPhotoInfo;
		try {
			daoPhotoInfo = helper.getDao(PhotoInfo.class);
			List<PhotoInfo> photoInfos = daoPhotoInfo.queryForMatching(info);
			for (PhotoInfo photoInfo : photoInfos) {
				return photoInfo.getCachePath();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param picW
	 *            图片的宽
	 * @param picH
	 *            图片的高
	 * @param reqW
	 *            需求的宽
	 * @param reqH
	 *            需求的高
	 * @return 缩放比例
	 */
	public static int calcSampleSize(int picW, int picH, int reqW, int reqH) {
		int n = 1;
		float picRate = (picW * 1.0f) / (picH * 1.0f);
		float reqRate = (reqW * 1.0f) / (reqH * 1.0f);
		// 将图片缩小到仅比需求尺寸大一点
		if (picRate >= reqRate) {
			// 按照宽度进行缩放
			while (picW / n >= reqW) {
				n *= 2;
			}
			return n / 2;
		} else {
			// 按照高度进行缩放
			while (picH / n >= reqH) {
				n *= 2;
			}
			return n / 2;
		}
	}

	/**
	 * 更新和插入本地数据库的PhotoInfo信息
	 * 
	 * @param photoNo
	 *            原PhotoNO号
	 * @param newPhotoNO
	 *            新的PhotoNO号
	 * @param fileName
	 *            文件名
	 * @param cachePath
	 *            缓存路径
	 */
	public void updataAndCreatePhotoInfo(String photoNo, String newPhotoNO,
			String fileName, String cachePath) {
		// 更新本地数据库的PhotoInfo信息
		PhotoInfo info = new PhotoInfo();
		info.setPhotoSerialNo(photoNo);
		Dao<PhotoInfo, Integer> daoPhotoInfo;
		try {
			daoPhotoInfo = helper.getDao(PhotoInfo.class);
			List<PhotoInfo> photoInfos = daoPhotoInfo.queryForMatching(info);
			// 如果有PhotoInfo的信息就更新，没有就插入
			if (photoInfos.size() == 0) {
				PhotoInfo photoInfo = new PhotoInfo();
				photoInfo.setPhotoSerialNo(newPhotoNO);
				photoInfo.setName(fileName);
				photoInfo.setCachePath(cachePath);
				photoInfo.setAddress(PATH + File.separator + fileName);
				// photoInfo.setPhotoTime(mPictureFile.substring(0,
				// mPictureFile.length() - 4));
				try {
					daoPhotoInfo.create(photoInfo);
				} catch (SQLException e) {
					LogUtils.i("out", "==插入本地PhotoInfo数据库==失败");
					e.printStackTrace();
				}
			} else {
				for (PhotoInfo photoInfo : photoInfos) {
					photoInfo.setPhotoSerialNo(newPhotoNO);
					photoInfo.setName(fileName);
					photoInfo.setCachePath(cachePath);
					photoInfo.setAddress(PATH + File.separator + fileName);
					// photoInfo.setPhotoTime(mPictureFile.substring(0,
					// mPictureFile.length() - 4));
					try {
						daoPhotoInfo.update(photoInfo);
					} catch (SQLException e) {
						LogUtils.i("out", "==更新本地PhotoInfo数据库==失败");
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 处理采样大位图,并展示在ListView中
	 * 
	 * @param filePath
	 *            缓存路径
	 * @return Bitmap 大位图
	 */
	public static Bitmap disposeBitmapForListView(String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		int picWidth = opts.outWidth;
		int picHeight = opts.outHeight;

		int sampleSize = calcSampleSize(picWidth, picHeight, 100, 100);
		opts.inSampleSize = sampleSize;
		opts.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(filePath, opts);
	}

	/**
	 * 做一个缩略图,处理大位图,将数据写入本地CACHE目录
	 * 
	 * @param readForPath
	 *            读取路径
	 * @param writeToPath
	 *            写入的路径
	 */
	public static void disposeBitmap(String readForPath, String writeToPath) {

		File file = new File(readForPath);
		if (file.exists()) {
			// 对返回的结果，大位图处理
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(readForPath, opts);
			int picWidth = opts.outWidth;
			int picHeight = opts.outHeight;

			int sampleCacheSize = calcSampleSize(picWidth, picHeight, 200, 200);
			opts.inSampleSize = sampleCacheSize;
			opts.inJustDecodeBounds = false;

			// 将数据写入本地CACHE目录
			writeImageTOCache(readForPath, writeToPath, opts);
		}
	}

	/**
	 * @param readPath
	 *            读取路径
	 * @param writePath
	 *            写入路径
	 * @param opts
	 *            设置缩放的变量
	 */
	public static void writeImageTOCache(String readPath, String writePath,
			BitmapFactory.Options opts) {
		FileOutputStream out = null;
		Bitmap back = BitmapFactory.decodeFile(readPath, opts);
		File target = new File(writePath);
		if (!target.getParentFile().isDirectory()) {
			target.getParentFile().mkdirs();
		}
		try {
			out = new FileOutputStream(target);
			back.compress(CompressFormat.JPEG, 100, out);
			if (back != null) {
				back.recycle();
				back = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 通过返回的图片ID,拿到图片的地址
	 * 
	 * @param context
	 *            上下文对象
	 * @param photoId
	 *            返回的图片ID
	 * @return 图片的url地址
	 */
	public static String getNetworkPhotoPath(Context context, String photoId) {
		String photoUrl = "";
		photoUrl = mZLServiceHelper.getPhotoAddr(context, photoId);
		if (photoUrl.equals("") && photoUrl.equals("网络错误")) {
			LogUtils.i(TAG, "===photoUrl==" + photoUrl);
		}
		try {
			JSONArray jsonArray = new JSONArray(photoUrl);
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			String address = (String) jsonObject.get("Address");
			// Upload2014\\3\\21\\1395396664.jpg
			// http://www.tt-sms.net:8076/Upload2014/3/21/1395396664.jpg
			address = address.replace("\\", "/");
			photoUrl = Global.BASE_URL + Global.EXTENSION + address;
			// photoUrl = address;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return photoUrl;
	}

	/**
	 * 处理缓存,生成缩略图并更新PhotoInfo
	 * 
	 * @param photoUrl
	 *            图片的URL地址
	 * @param readToPath
	 *            图片的本地路径
	 * @param oldPhotoNo
	 *            原PhotoNo
	 * @param newPhotoNo
	 *            新PhotoNo
	 * @return 图片的完整地址
	 */
	public String disposeCacheAndUpdataPhotoInfo(String photoUrl,
			String readToPath, String oldPhotoNo, String newPhotoNo) {
		int index = photoUrl.lastIndexOf("/");
		String photoCacheName = photoUrl
				.substring(index + 1, photoUrl.length());// 图片的缓存名
		String photoCachePath = CACHEPATH + File.separator + photoCacheName;// 图片的缓存地址
		String photoCompletePath = PATH + File.separator + photoCacheName;// 图片的完整路径
		File filephotoCachePath = new File(photoCachePath);

		if (!filephotoCachePath.exists()) {
			disposeBitmap(readToPath, photoCachePath);// 将图片写入本地缓存
		}

		File file = new File(readToPath);
		file.renameTo(new File(photoCompletePath));// 将本地的图片的名字重命名为网络返回的名字
		// readToPath=photoCompletePath;
		// 更新或者创建本地数据库PhotoInfo数据
		updataAndCreatePhotoInfo(oldPhotoNo, newPhotoNo, photoCacheName,
				photoCachePath);
		return photoCompletePath;
	}

	/**
	 * 处理缓存,生成缩略图
	 * 
	 * @param photoUrl
	 *            图片的URL地址
	 * @param writeToPath
	 *            图片的本地路径
	 */
	public void disposeCache(String photoUrl, String readToPath) {
		int index = photoUrl.lastIndexOf("/");
		String photoCacheName = photoUrl
				.substring(index + 1, photoUrl.length());// 图片的缓存名
		String photoCachePath = CACHEPATH + File.separator + photoCacheName;// 图片的缓存地址
		String photoCompletePath = PATH + File.separator + photoCacheName;// 图片的完整路径
		File filephotoCachePath = new File(photoCachePath);

		if (!filephotoCachePath.exists()) {
			disposeBitmap(readToPath, photoCachePath);// 将图片写入本地缓存
		}

		File file = new File(readToPath);
		file.renameTo(new File(photoCompletePath));// 将本地的图片的名字重命名为网络返回的名字
		readToPath = photoCompletePath;
	}

	/**
	 * 获取到要上传的文件的输入流信息，通过ByteArrayOutputStream流转成byte[]
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件的byte数组
	 */
	@SuppressWarnings("resource")
	public static byte[] changeFileToByte(String filePath) {
		BufferedInputStream bis = null;
		byte[] body_data = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c = 0;
		byte[] buffer = new byte[8 * 1024];
		try {
			while ((c = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			body_data = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body_data;
	}

}
