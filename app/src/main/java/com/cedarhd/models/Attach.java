package com.cedarhd.models;

import java.io.Serializable;

/**
 * 附件模型类
 * 
 * @author kjx
 * @since 2014/07/17 14:50
 */
public class Attach implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 437025599744651141L;

	/**
	 * 附件编号
	 */
	public int Id;

	/**
	 * 附件名 1405607561.png
	 */
	public String Name;
	/**
	 * 附件地址：Upload\\Upload2014\\127\\85\\7\\17\\1405607561.png
	 */
	public String Address;
	/**
	 * 附件后缀名
	 */
	public String Suffix;

	/** 是否已经下载 */
	public boolean isDownloaded;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getSuffix() {
		return Suffix;
	}

	public void setSuffix(String suffix) {
		Suffix = suffix;
	}

	public boolean isDownloaded() {
		return isDownloaded;
	}

	public void setDownloaded(boolean isDownloaded) {
		this.isDownloaded = isDownloaded;
	}

	@Override
	public String toString() {
		return "Attach [Id=" + Id + ", Name=" + Name + ", Address=" + Address
				+ ", Suffix=" + Suffix + "]";
	}

	// /以下三个字段用于下载时使用
	/** 已下载大小 */
	public int downloadSize;

	/** 文件大小 */
	public int totalSize;

	/** 下载状态 */
	public int downloadState;
}
