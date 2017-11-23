package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 论坛回帖 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;
	@DatabaseField
	public int Id;// 编号
	@DatabaseField
	public int 编号;
	@DatabaseField
	public int 论坛发帖;// 论坛发帖
	@DatabaseField
	public String 内容;
	@DatabaseField
	public Date 回帖时间;
	@DatabaseField
	public int 回帖人;
	@DatabaseField
	public int 楼号;
	public String 回帖人名称;

	public 论坛回帖() {
	}

	public 论坛回帖(String 内容, String 回帖人名称) {
		super();
		this.内容 = 内容;
		this.回帖人名称 = 回帖人名称;
	}

	public int get编号() {
		return 编号;
	}

	public void set编号(int 编号) {
		this.编号 = 编号;
	}

	public String get回帖人名称() {
		return 回帖人名称;
	}

	public void set回帖人名称(String 回帖人名称) {
		this.回帖人名称 = 回帖人名称;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int get论坛发帖() {
		return 论坛发帖;
	}

	public void set论坛发帖(int 论坛发帖) {
		this.论坛发帖 = 论坛发帖;
	}

	public String get内容() {
		return 内容;
	}

	public void set内容(String 内容) {
		this.内容 = 内容;
	}

	public Date get回帖时间() {
		return 回帖时间;
	}

	public void set回帖时间(Date 回帖时间) {
		this.回帖时间 = 回帖时间;
	}

	public int get回帖人() {
		return 回帖人;
	}

	public void set回帖人(int 回帖人) {
		this.回帖人 = 回帖人;
	}

	public int get楼号() {
		return 楼号;
	}

	public void set楼号(int 楼号) {
		this.楼号 = 楼号;
	}

	@Override
	public String toString() {
		return "lt论坛回帖 [Id=" + Id + ", 论坛发帖=" + 论坛发帖 + ", 内容=" + 内容 + ", 回帖时间="
				+ 回帖时间 + ", 回帖人=" + 回帖人 + ", 楼号=" + 楼号 + "]";
	}

}
