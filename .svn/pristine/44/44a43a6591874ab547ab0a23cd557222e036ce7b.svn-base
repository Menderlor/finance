package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工
 * 
 * @author bohr
 * 
 */
public class User implements Serializable {
	private static final long serialVersionUID = -5710064635665839589L;
	@DatabaseField(generatedId = true, unique = true)
	public int _id;
	@DatabaseField
	public String Id;
	@DatabaseField
	public int Department;// 员工所属部门的编号
	@DatabaseField
	public String UserName;
	@DatabaseField
	public String PassWord;
	@DatabaseField
	public int UserLevel;
	@DatabaseField
	public String CorpName;
	@DatabaseField
	public String Admin; // 是否管理员true/false
	@DatabaseField
	public int CorpId;
	@DatabaseField
	public String Passport;

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

	@DatabaseField
	public long updateTime;// 最后修改时间 员工

	@DatabaseField
	public Date lastUpdateDate;// 最后修改时间 员工

	/**
	 * 职位
	 */
	@DatabaseField
	public int Position;

	/** 冗余字段 用于保存本地 */
	private String userIds;

	private String userNames;

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getDptId() {
		return Department;
	}

	public void setDptId(int Department) {
		this.Department = Department;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassWord() {
		return PassWord;
	}

	public void setPassWord(String passWord) {
		PassWord = passWord;
	}

	public int getUserLevel() {
		return UserLevel;
	}

	public void setUserLevel(int userLevel) {
		UserLevel = userLevel;
	}

	public String getCorpName() {
		return CorpName;
	}

	public void setCorpName(String corpName) {
		CorpName = corpName;
	}

	public int getCorpId() {
		return CorpId;
	}

	public void setCorpId(int corpId) {
		CorpId = corpId;
	}

	public String getPassport() {
		return Passport;
	}

	public void setPassport(String passport) {
		Passport = passport;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public String getAdmin() {
		return Admin;
	}

	public void setAdmin(String admin) {
		Admin = admin;
	}

	public String getAvatarURI() {
		return AvatarURI;
	}

	public void setAvatarURI(String avatarURI) {
		AvatarURI = avatarURI;
	}

	/** 获取多个员工编号 如 1,2,3,4 */
	public String getUserIds() {
		return userIds;
	}

	/** 设置多个员工编号 如 1,2,3,4 */
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	@Override
	public String toString() {
		return "User [_id=" + _id + ", Id=" + Id + ", dptId=" + Department
				+ ", UserName=" + UserName + ", PassWord=" + PassWord
				+ ", UserLevel=" + UserLevel + ", CorpName=" + CorpName
				+ ", Admin=" + Admin + ", CorpId=" + CorpId + ", Passport="
				+ Passport + ", Avatar=" + Avatar + ", AvatarURI=" + AvatarURI
				+ ", updateTime=" + updateTime + "]";
	}

}
