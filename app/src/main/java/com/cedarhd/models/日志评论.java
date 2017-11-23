package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 日志评论 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3517969605552358798L;

	public 日志评论() {
		super();
	}

	public 日志评论(int id, String 发表时间, int 发表人, String 日志编号, String 内容) {
		super();
		Id = id;
		this.发表时间 = 发表时间;
		this.发表人 = 发表人;
		this.日志编号 = 日志编号;
		this.内容 = 内容;
	}

	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;// 编号
	@DatabaseField
	public String 发表时间;

	@DatabaseField
	public int 发表人;

	@DatabaseField
	public String 日志编号;

	@DatabaseField
	public String 内容;

	public int getId() {
		return Id;
	}

	public String get发表时间() {
		return 发表时间;
	}

	public int get发表人() {
		return 发表人;
	}

	public String getO日志编号() {
		return 日志编号;
	}

	public String getContent() {
		return 内容;
	}

}
