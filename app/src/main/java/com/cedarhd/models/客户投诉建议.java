package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 客户投诉建议 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8267907344445604047L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public int ClientId;
	@DatabaseField
	public Date UpdateTime;
	@DatabaseField
	public Date Time;
	@DatabaseField
	public String Content;
	@DatabaseField
	public String Attachment;
	@DatabaseField
	public String ClientName;
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
	public int getClientId() {
		return ClientId;
	}
	public void setClientId(int clientId) {
		ClientId = clientId;
	}
	public Date getUpdateTime() {
		return UpdateTime;
	}
	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getAttachment() {
		return Attachment;
	}
	public void setAttachment(String attachment) {
		Attachment = attachment;
	}
	
	public Date getTime() {
		return Time;
	}
	public void setTime(Date time) {
		Time = time;
	}
	public String getClientName() {
		return ClientName;
	}
	public void setClientName(String clientName) {
		ClientName = clientName;
	}
	@Override
	public String toString() {
		return "客户投诉建议 [_Id=" + _Id + ", Id=" + Id + ", ClientId=" + ClientId
				+ ", UpdateTime=" + UpdateTime + ", Time=" + Time
				+ ", Content=" + Content + ", Attachment=" + Attachment
				+ ", ClientName=" + ClientName + "]";
	}
	
}
