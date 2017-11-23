package com.cedarhd.helpers;

import android.os.Handler;
import android.os.Message;

import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.models.DownloadFile;
import com.cedarhd.utils.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/***
 * 下载文件帮助类
 * 
 * @author K
 * 
 */
public class BoeryunDownloadManager {

	// 下载状态：正常，暂停，下载中，已下载，排队中
	public static final int DOWNLOAD_STATE_NORMAL = 0x00;
	public static final int DOWNLOAD_STATE_PAUSE = 0x01;
	public static final int DOWNLOAD_STATE_DOWNLOADING = 0x02;
	public static final int DOWNLOAD_STATE_FINISH = 0x03;
	public static final int DOWNLOAD_STATE_WAITING = 0x04;

	/***
	 * 下载成功
	 */
	public final static int SUCCEDD_DOWNLOAD = 107;

	private static BoeryunDownloadManager mdDownloadHelper;

	/** 下载文件名称集合，避免添加重复 */
	private HashSet<String> mNameSet = new HashSet<String>();

	private ExecutorService mThreadPool = Executors.newFixedThreadPool(2);

	private Handler mHandler;

	private BoeryunDownloadManager() {

	}

	public static BoeryunDownloadManager getInstance() {
		if (mdDownloadHelper == null) {
			mdDownloadHelper = new BoeryunDownloadManager();
		}
		return mdDownloadHelper;
	}

	public void download(DownloadFile downloadFile) {
		if (mNameSet.contains(downloadFile.attachName)) {
			return;
		} else {
			mNameSet.add(downloadFile.attachName);
			mThreadPool.execute(new DownloadRunnable(downloadFile));
		}
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	/***
	 * 下载文件线程
	 * 
	 */
	class DownloadRunnable implements Runnable {
		private DownloadFile mDownloadFile;

		/** 是否正在下载 */
		private boolean isWorking;

		/**
		 * @param url
		 */
		public DownloadRunnable(DownloadFile downloadFile) {
			this.mDownloadFile = downloadFile;
			// isWorking = true;
		}

		@Override
		public void run() {
			mDownloadFile.downloadState = DOWNLOAD_STATE_DOWNLOADING; // 开始下载状态
			downloadData(mDownloadFile);
		}
	}

	// 更新listview中对应的item
	public void update(DownloadFile downloadFile) {
		Message msg = mHandler.obtainMessage();
		if (downloadFile.totalSize == downloadFile.downloadSize) {
			downloadFile.downloadState = DOWNLOAD_STATE_FINISH;
		}
		msg.obj = downloadFile;
		msg.sendToTarget();
	}

	/**
	 * 访问网络,下载图片数据,并存储到本地DCIM目录
	 * 
	 * @param url
	 *            url地址
	 */
	private void downloadData(DownloadFile downloadFile) {
		// 文件存放路径
		String filePath = FilePathConfig.getCacheDirPath() + File.separator
				+ downloadFile.attachName;
		LogUtils.i("filePath", filePath);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			String url = downloadFile.url.replace("\\", "/");
			LogUtils.d("url", url);
			HttpGet httpGet = new HttpGet(url);
			HttpConnectionParams.setConnectionTimeout(httpGet.getParams(),
					10 * 1000);
			HttpConnectionParams.setSoTimeout(httpGet.getParams(), 10 * 1000);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = null;
			if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				httpEntity = httpResponse.getEntity();
			}
			if (httpEntity != null) {
				InputStream is = httpEntity.getContent();
				// 文件总大小
				int contentLen = (int) httpEntity.getContentLength();
				downloadFile.totalSize = contentLen;

				File file = new File(filePath);
				FileOutputStream fos = new FileOutputStream(file);
				int len = -1;
				byte[] b = new byte[1024];
				while ((len = is.read(b)) != -1) {
					fos.write(b, 0, len);
					downloadFile.downloadSize += len;
					update(downloadFile);
				}

				fos.close();
				is.close();

				downloadFile.downloadState = DOWNLOAD_STATE_FINISH;
				update(downloadFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DownloadListener mDownloadListener;

	public void setOnDownloadListener(DownloadListener downloadListener) {
		this.mDownloadListener = downloadListener;
	}

	public interface DownloadListener {
		void complete();
	}
}
