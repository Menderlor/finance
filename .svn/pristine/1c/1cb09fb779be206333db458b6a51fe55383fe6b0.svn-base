package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 动态 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2591036614543576577L;

	@DatabaseField(generatedId = true, unique = true)
	public int _id;

	@DatabaseField
	public int Id;

	@DatabaseField
	public String Content;

	// / <summary>
	// / html内容提示
	// / </summary>
	public String HtmlContent;

	@DatabaseField
	public String UserId;

	@DatabaseField
	public String UserName;

	/***
	 * 判断动态类型：日志/任务/客户
	 */
	@DatabaseField
	public String Type;
	/***
	 * 判断动态数据类型类型，键值
	 */
	public int DataType;

	@DatabaseField
	public Date Time;

	@DatabaseField
	// 数据编号：日志，任务的编号
	public String DataId;

	// 有时间值为已读，否则未读
	public String Read;

	public 任务 Task;

	public 日志 Log;

	public 通知 Notice;

	public Client Client;

	public 客户联系记录 Contacts;

	public 流程 WorkFlow;

	public 销售机会 SaleChance;

	public 帖子 Tiezi;

	public int get_Id() {
		return _id;
	}

	public void set_Id(int _id) {
		this._id = _id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public Date getTime() {
		return Time;
	}

	public void setTime(Date time) {
		Time = time;
	}

	public String getDataId() {
		return DataId;
	}

	public void setDataId(String dataId) {
		DataId = dataId;
	}

	public 任务 getTask() {
		return Task;
	}

	public void setTask(任务 task) {
		Task = task;
	}

	public 日志 getLog() {
		return Log;
	}

	public void setLog(日志 log) {
		Log = log;
	}

	public 通知 getNotice() {
		return Notice;
	}

	public void setNotice(通知 notice) {
		Notice = notice;
	}

	public 客户联系记录 getContacts() {
		return Contacts;
	}

	public void setContacts(客户联系记录 contacts) {
		Contacts = contacts;
	}

}
