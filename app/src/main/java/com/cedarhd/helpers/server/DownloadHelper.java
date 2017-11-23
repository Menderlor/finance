package com.cedarhd.helpers.server;

import android.widget.ProgressBar;

import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.utils.LogUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.ByteArrayOutputStream;
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
public class DownloadHelper {

	/***
	 * 下载成功
	 */
	public final static int SUCCEDD_DOWNLOAD = 107;

	private static DownloadHelper mdDownloadHelper;

	/** 下载文件名称集合，避免添加重复 */
	private HashSet<String> mNameSet = new HashSet<String>();

	private ExecutorService mThreadPool = Executors.newFixedThreadPool(2);

	private DownloadHelper() {

	}

	public static DownloadHelper getInstance() {
		if (mdDownloadHelper == null) {
			mdDownloadHelper = new DownloadHelper();
		}
		return mdDownloadHelper;
	}

	public void download(String url, String fileName, ProgressBar pBar) {
		if (mNameSet.contains(fileName)) {
			return;
		} else {
			mNameSet.add(fileName);
			mThreadPool.execute(new DownloadRunnable(fileName, url, pBar));
		}
	}

	private DownloadListener mDownloadListener;

	public void setOnDownloadListener(DownloadListener downloadListener) {
		this.mDownloadListener = downloadListener;
	}

	public interface DownloadListener {
		void complete();
	}

	/***
	 * 下载文件线程
	 * 
	 */
	class DownloadRunnable implements Runnable {
		private String fileName;
		private String url;
		private ProgressBar mPbar;

		/**
		 * @param url
		 */
		public DownloadRunnable(String fileName, String url, ProgressBar pBar) {
			this.fileName = fileName;
			this.url = url;
			this.mPbar = pBar;
		}

		@Override
		public void run() {
			downloadData(url, fileName, mPbar);
		}
	}

	/**
	 * 访问网络,下载图片数据,并存储到本地DCIM目录
	 * 
	 * @param url
	 *            url地址
	 */
	private void downloadData(String url, String fileName, ProgressBar pBar) {
		// 文件存放路径
		String filePath = FilePathConfig.getCacheDirPath() + File.separator
				+ fileName;
		LogUtils.i("filePath", filePath);
		try {
			HttpClient httpClient = new DefaultHttpClient();
			LogUtils.i("url", url);
			url = url.replace("\\", "/");
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
				int contentLen = (int) httpEntity.getContentLength();
				if (pBar != null) {
					pBar.setMax(contentLen);
				}
				final ByteArrayOutputStream out = new ByteArrayOutputStream(
						contentLen > 0 ? contentLen : 1024);
				File file = new File(filePath);
				FileOutputStream fos = new FileOutputStream(file);
				int len = -1;
				byte[] b = new byte[1024];
				while ((len = is.read(b)) != -1) {
					out.write(b, 0, len);
					fos.write(b, 0, len);

					if (pBar != null) {
						pBar.incrementProgressBy(len);
					}
				}
				byte[] data = out.toByteArray();
				LogUtils.d("leo2", "长度：" + data.length);

				fos.close();
				out.close();
				is.close();

				if (mDownloadListener != null) {
					mDownloadListener.complete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
