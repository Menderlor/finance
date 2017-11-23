package com.cedarhd.models;

/**
 * 服务器 info
 * 
 */
public class UpdataBean {
	private int version; // 版本号
	private String versionName;
	private String desc; // apk描述
	private String apkUrl; // apk下载地址

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getApkUrl() {
		return apkUrl;
	}

	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}

}