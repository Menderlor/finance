package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Latest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2660198998803786741L;
	@DatabaseField(generatedId = true, unique = true)
	private int _id;

	@DatabaseField
	private int id;// 用户的id
	@DatabaseField
	private String userName;// 用户名
	@DatabaseField
	private String pic_url;// 用户头像的网址（也可以是存储用户本地头像的路径）
	@DatabaseField
	private long date;// 最后更新时间

	public int getId() {
		return id;
	}

	public long getDate() {
		return date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	@Override
	public String toString() {
		return "Latest [ id=" + id + ", userName=" + userName + ", pic_url="
				+ pic_url + ", date=" + date + "]";
	}

	public Latest(int id, String userName, String pic_url, long date) {
		super();
		this.id = id;
		this.userName = userName;
		this.pic_url = pic_url;
		this.date = date;
	}

	public Latest() {
		super();
		// TODO Auto-generated constructor stub
	}

}
