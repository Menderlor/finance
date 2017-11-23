package com.cedarhd.services;

import android.util.Xml;

import com.cedarhd.models.UpdataBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新升级Apk的业务逻辑
 * 
 */
public class UpdateInfoService {
	/**
	 * 获取服务上的最新的版本信息
	 * 
	 * @param path
	 *            路径
	 * @return
	 * @throws Exception
	 */
	public UpdataBean getUpdateInfo(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		conn.setConnectTimeout(10000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			UpdataBean updataBean = parserUpdateInfo(is);
			return updataBean;
		}
		return null;
	}

	/**
	 * 解析服务的流信息为UpdateInfo对象
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	private UpdataBean parserUpdateInfo(InputStream is) throws Exception {
		UpdataBean updateInfo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_TAG:
				if ("VERSION".equals(parser.getName())) {
					updateInfo = new UpdataBean();
				} else if ("VERSIONCODE".equals(parser.getName())) {
					String version = parser.nextText();
					int versionCode = 0;
					try {
						versionCode = Integer.parseInt(version);
					} catch (Exception e) {
						// TODO: handle exception
					}
					updateInfo.setVersion(versionCode);
				} else if ("VERSIONNAME".equals(parser.getName())) {
					String versionName = parser.nextText();
					updateInfo.setVersionName(versionName);
				} else if ("LOADURL".equals(parser.getName())) {
					String url = parser.nextText();
					updateInfo.setApkUrl(url);
				} else if ("FILEDESC".equals(parser.getName())) {
					String description = parser.nextText();
					updateInfo.setDesc(description);
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		return updateInfo;
	}
}
