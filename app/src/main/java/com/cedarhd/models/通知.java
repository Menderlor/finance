package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 通知 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 226696040949934365L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public int Publisher;
	@DatabaseField
	public String PublisherName;
	@DatabaseField
	public Date ReleaseTime;
	@DatabaseField
	public String Title;
	@DatabaseField
	public String Content;
	@DatabaseField
	public Date ExpirationTime;
	@DatabaseField
	public String Personnel; // 接收人
	@DatabaseField
	public String PersonnelName;
	@DatabaseField
	public String Read;

	@DatabaseField
	public String ReadTime; // 已读时间

	@DatabaseField
	public String Attachment;
	@DatabaseField
	public Date UpdateTime;

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

	public int getPublisher() {
		return Publisher;
	}

	public void setPublisher(int publisher) {
		Publisher = publisher;
	}

	public String getPublisherName() {
		return PublisherName;
	}

	public void setPublisherName(String publisherName) {
		PublisherName = publisherName;
	}

	public Date getReleaseTime() {
		return ReleaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		ReleaseTime = releaseTime;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public Date getExpirationTime() {
		return ExpirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		ExpirationTime = expirationTime;
	}

	public String getPersonnel() {
		return Personnel;
	}

	public void setPersonnel(String personnel) {
		Personnel = personnel;
	}

	public String getPersonnelName() {
		return PersonnelName;
	}

	public void setPersonnelName(String personnelName) {
		PersonnelName = personnelName;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.UpdateTime = updateTime;
	}
}
