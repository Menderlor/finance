package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 日志 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7872504240638522244L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public String Time;
	@DatabaseField
	public String Content;

	@DatabaseField
	public String PlanContent;

	@DatabaseField
	public int Personnel; // 写日志的人id
	@DatabaseField
	public String PersonnelName;
	@DatabaseField
	public int Client;
	@DatabaseField
	public int Suppliers;
	@DatabaseField
	public int Project;
	@DatabaseField
	public int ClientRecord;
	@DatabaseField
	public Date UpdateTime;

	@Deprecated
	@DatabaseField
	public String Readed; // 已读员工

	@DatabaseField
	public String isTemp;

	@DatabaseField
	public int DiscussCount;

	/** 赞数量 */
	public int SupportCount;

	/** 钻石数量 */
	public int DiamondCount;

	/** 我的赞数量 */
	public int MySupportCount;

	/** 我发的钻石数量 */
	public int MyDiamondCount;

	/** 已读时间 */
	public String ReadTime;

	public int getDiscussCount() {
		return DiscussCount;
	}

	public void setDiscussCount(int _DiscussCount) {
		this.DiscussCount = _DiscussCount;
	}

	public String getReaded() {
		return Readed;
	}

	public void setReaded(String readed) {
		Readed = readed;
	}

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

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public int getPersonnel() {
		return Personnel;
	}

	public void setPersonnel(int personnel) {
		Personnel = personnel;
	}

	public String getPersonnelName() {
		return PersonnelName;
	}

	public void setPersonnelName(String personnelName) {
		PersonnelName = personnelName;
	}

	public int getClient() {
		return Client;
	}

	public void setClient(int client) {
		Client = client;
	}

	public int getSuppliers() {
		return Suppliers;
	}

	public void setSuppliers(int suppliers) {
		Suppliers = suppliers;
	}

	public int getProject() {
		return Project;
	}

	public void setProject(int project) {
		Project = project;
	}

	public int getClientRecord() {
		return ClientRecord;
	}

	public void setClientRecord(int clientRecord) {
		ClientRecord = clientRecord;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public String getIsTemp() {
		return isTemp;
	}

	public void setIsTemp(String isTemp) {
		this.isTemp = isTemp;
	}

	@Override
	public String toString() {
		return "日志 [Id=" + Id + ", Time=" + Time + ", Content=" + Content
				+ ", Personnel=" + Personnel + ", PersonnelName="
				+ PersonnelName + ", Client=" + Client + ", Suppliers="
				+ Suppliers + ", Project=" + Project + ", ClientRecord="
				+ ClientRecord + ", UpdateTime=" + UpdateTime + "]";
	}
}
