package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 评论 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6424699143019690690L;

	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;// 编号
	@DatabaseField
	public String PublishDate;
	/**
	 * 评论人
	 */
	@DatabaseField
	public int userId;

	@DatabaseField
	public String OrderNo;

	@DatabaseField
	public String Content;

	public int getId() {
		return Id;
	}

	public String getPublishDate() {
		return PublishDate;
	}

	public int getUserId() {
		return userId;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public String getContent() {
		return Content;
	}

}
