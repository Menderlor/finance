package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * 
 * 
 "Id": 6, "UserName": "钟斌", "CorpId": 0, "Department": 2, "IsUse": false,
 * "Admin": false, "EleSignature": null, "Passport": null, "LoginTip": null,
 * "lastUpdateDate": "2014-02-13T09:50:01"
 */
public class Data {

	private int Id;// 员工id
	private String UserName;// 用户名
	private int CorpId;
	private int Department;// 部门编号
	private boolean IsUse;
	private String EleSignature;
	private String Passport;
	private String LoginTip;
	private String lastUpdateDate;// 最后更新时间

	public String Admin; // 是否管理员true/false
	/**
	 * 头像附件编号
	 */
	@DatabaseField
	public String Avatar;

	/**
	 * 头像的服务器地址
	 */
	@DatabaseField
	public String AvatarURI;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public int getCorpId() {
		return CorpId;
	}

	public void setCorpId(int corpId) {
		CorpId = corpId;
	}

	public int getDepartment() {
		return Department;
	}

	public void setDepartment(int department) {
		Department = department;
	}

	public boolean isIsUse() {
		return IsUse;
	}

	public void setIsUse(boolean isUse) {
		IsUse = isUse;
	}

	public String isAdmin() {
		return Admin;
	}

	public void setAdmin(String admin) {
		Admin = admin;
	}

	public String getEleSignature() {
		return EleSignature;
	}

	public void setEleSignature(String eleSignature) {
		EleSignature = eleSignature;
	}

	public String getPassport() {
		return Passport;
	}

	public void setPassport(String passport) {
		Passport = passport;
	}

	public String getLoginTip() {
		return LoginTip;
	}

	public void setLoginTip(String loginTip) {
		LoginTip = loginTip;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public String getAvatarURI() {
		return AvatarURI;
	}

	public void setAvatarURI(String avatarURI) {
		AvatarURI = avatarURI;
	}

	public String getAdmin() {
		return Admin;
	}

	public Data(int id, String userName, int corpId, int department,
			boolean isUse, String admin, String eleSignature, String passport,
			String loginTip, String lastUpdateDate) {
		super();
		Id = id;
		UserName = userName;
		CorpId = corpId;
		Department = department;
		IsUse = isUse;
		Admin = admin;
		EleSignature = eleSignature;
		Passport = passport;
		LoginTip = loginTip;
		this.lastUpdateDate = lastUpdateDate;
	}

	public Data() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Data [Id=" + Id + ", UserName=" + UserName + ", CorpId="
				+ CorpId + ", Department=" + Department + ", IsUse=" + IsUse
				+ ", Admin=" + Admin + ", EleSignature=" + EleSignature
				+ ", Passport=" + Passport + ", LoginTip=" + LoginTip
				+ ", lastUpdateDate=" + lastUpdateDate + "]";
	}

}
