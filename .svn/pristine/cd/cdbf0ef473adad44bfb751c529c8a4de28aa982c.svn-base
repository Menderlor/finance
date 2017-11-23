package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * 通讯录的客户联系人
 * 
 * @author KJX
 * 
 */
public class 联系人 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true, unique = true)
	public int Id;

	@DatabaseField
	public String Name;

	@DatabaseField
	public int Categray;

	@DatabaseField
	public String CategrayName;

	/** 座机 */
	@DatabaseField
	public String Phone;

	/** 手机 */
	@DatabaseField
	public String MobilePhone;

	@DatabaseField
	public String Address;

	@DatabaseField
	public String Email;

	public int get_Id() {
		return Id;
	}

	public void set_Id(int Id) {
		this.Id = Id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int Id) {
		this.Id = Id;
	}

	public String getName() {
		return Name;
	}

	public int getCategray() {
		return Categray;
	}

	public String getCategrayName() {
		return CategrayName;
	}

	public String getPhone() {
		return Phone;
	}

	public String getMobilePhone() {
		return MobilePhone;
	}

	public String getAddress() {
		return Address;
	}

	public String getEmail() {
		return Email;
	}
}
