package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 邮件 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5292838755938125670L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public String Title;
	@DatabaseField
	public String Content;
	@DatabaseField
	public int Sender;
	@DatabaseField
	public String SenderName;
	@DatabaseField
	public String Receiver;
	@DatabaseField
	public String ReceiverName;
	@DatabaseField
	public Date SendTime;
	@DatabaseField
	public String Reply;
	@DatabaseField
	public String Attachment;
	@DatabaseField
	public String Read;
	@DatabaseField
	Date UpdateTime;

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

	public int getSender() {
		return Sender;
	}

	public void setSender(int sender) {
		Sender = sender;
	}

	public String getSenderName() {
		return SenderName;
	}

	public void setSenderName(String senderName) {
		SenderName = senderName;
	}

	public String getReceiver() {
		return Receiver;
	}

	public void setReceiver(String receiver) {
		Receiver = receiver;
	}

	public String getReceiverName() {
		return ReceiverName;
	}

	public void setReceiverName(String receiverName) {
		ReceiverName = receiverName;
	}

	public Date getSendTime() {
		return SendTime;
	}

	public void setSendTime(Date sendTime) {
		SendTime = sendTime;
	}

	public String getReply() {
		return Reply;
	}

	public void setReply(String reply) {
		Reply = reply;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

}
