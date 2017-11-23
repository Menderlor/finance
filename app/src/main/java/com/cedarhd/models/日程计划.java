package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 日程计划 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7969700094934192519L;

	@DatabaseField(generatedId = true, unique = true)
	int _Id;

	@DatabaseField
	int Id;

	@DatabaseField
	int Staff;

	@DatabaseField
	String Title;

	@DatabaseField
	String DateTimePlanBegin;

	@DatabaseField
	String DateTimePlanEnd;

	@DatabaseField
	String Content;

	@DatabaseField
	int State;

	@DatabaseField
	String StateName;

	@DatabaseField
	String Remark;

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getStaff() {
		return Staff;
	}

	public void setStaff(int staff) {
		Staff = staff;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDateTimePlanBegin() {
		return DateTimePlanBegin;
	}

	public void setDateTimePlanBegin(String dateTimePlanBegin) {
		DateTimePlanBegin = dateTimePlanBegin;
	}

	public String getDateTimePlanEnd() {
		return DateTimePlanEnd;
	}

	public void setDateTimePlanEnd(String dateTimePlanEnd) {
		DateTimePlanEnd = dateTimePlanEnd;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	public String getStateName() {
		return StateName;
	}

	public void setStateName(String stateName) {
		StateName = stateName;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}
}
