package com.cedarhd.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Email implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String Id;

	public String Title;

	public String Content;

	public String Sender;

	public String SenderName;

	public String Receiver;

	public String ReceiverName;

	public Date SendTime;

	public String Reply;

	public String Attachment;

	public String Read;

	public String GetSendTime() {
		java.text.SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		return format.format(SendTime);
	}

	public void SetSendTime(String date) throws ParseException {
		java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SendTime = format.parse(date);
	}
}
