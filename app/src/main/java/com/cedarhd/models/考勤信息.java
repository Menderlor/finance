package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 考勤信息 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8504135717138346769L;

	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;// 编号

	@DatabaseField
	public int UserId;// 员工编号

	@DatabaseField
	public int CorpId;// 企业编号

	@DatabaseField
	public String PublisherName;// 发布人

	@DatabaseField
	public String CachePath;// 缓存路径

	// @DatabaseField
	// public String Participant;//接受人

	@DatabaseField
	public String AttendanceDate;// 考勤日期

	@DatabaseField
	public String EarlyReason;// 早退原因

	@DatabaseField
	public int Employee;// 员工

	@DatabaseField
	public String EmployeeName;// 员工姓名

	@DatabaseField
	public String PositionSignIn;// 地理位置_签到

	@DatabaseField
	public String PositionSignOut;// 地理位置_签退

	@DatabaseField
	public String SignInTime;// 签到时间

	@DatabaseField
	public String SignOutTime;// 签退时间

	@DatabaseField
	public String LaterReason;// 迟到原因

	// @DatabaseField
	// public String LeaveEarlyCause;//早退原因

	/** 签到纬度（ 历史遗留问题：搞反了，英文是经度，实际该属性表示的是纬度） */
	@DatabaseField
	public double Longitude;// 经度

	/** 签到经度（ 历史遗留问题：搞反了，英文是纬度，实际该属性表示的是经度） */
	@DatabaseField
	public double Latitude;

	@DatabaseField
	public boolean IsLater;// 是否迟到

	@DatabaseField
	public boolean IsEarly;// 是否早退

	@DatabaseField
	public String AttachFileName;// 图片名

	@DatabaseField
	public Date UpdateTime;// 最后更新

	@DatabaseField
	public String Read;// 是否读过

	@DatabaseField
	public String PhotoSerialNo;// 签到图片

	@DatabaseField
	public String PhotoSingnOut;// 签退图片

	// 签退图片url
	@DatabaseField
	public String SignInPicURI;

	// 签退图片url
	@DatabaseField
	public String SignOutPicURI;

	/**
	 * 签退_经度
	 */
	public double OutLongitude;

	/***
	 * 签退_纬度
	 */
	public double OutLatitude;

	/**
	 * 是否临时数据,如果是 则为 true,否则为 null
	 */
	@DatabaseField
	public String isTemp;

	public String getPhotoSerialNo() {
		return PhotoSerialNo;
	}

	public String getRead() {
		return Read;
	}

	public void setRead(String read) {
		Read = read;
	}

	public String getCachePath() {
		return CachePath;
	}

	public void setCachePath(String cachePath) {
		CachePath = cachePath;
	}

	public String getSignInTime() {
		return SignInTime;
	}

	public void setSignInTime(String signInTime) {
		SignInTime = signInTime;
	}

	public String getSignOutTime() {
		return SignOutTime;
	}

	public void setSignOutTime(String signOutTime) {
		SignOutTime = signOutTime;
	}

	public String getPositionSignIn() {
		return PositionSignIn;
	}

	public void setPositionSignIn(String positionSignIn) {
		PositionSignIn = positionSignIn;
	}

	public String getPositionSignOut() {
		return PositionSignOut;
	}

	public void setPositionSignOut(String positionSignOut) {
		PositionSignOut = positionSignOut;
	}

	public String getPublisherName() {
		return PublisherName;
	}

	public void setPublisherName(String publisherName) {
		PublisherName = publisherName;
	}

	public String getAttendanceDate() {
		return AttendanceDate;
	}

	public void setAttendanceDate(String attendanceDate) {
		AttendanceDate = attendanceDate;
	}

	public String getEarlyReason() {
		return EarlyReason;
	}

	public void setEarlyReason(String earlyReason) {
		EarlyReason = earlyReason;
	}

	public int getEmployee() {
		return Employee;
	}

	public void setEmployee(int employee) {
		Employee = employee;
	}

	public String getEmployeeName() {
		return EmployeeName;
	}

	public void setEmployeeName(String employeeName) {
		EmployeeName = employeeName;
	}

	public String getLaterReason() {
		return LaterReason;
	}

	public void setLaterReason(String laterReason) {
		LaterReason = laterReason;
	}

	public boolean isIsLater() {
		return IsLater;
	}

	public void setIsLater(boolean isLater) {
		IsLater = isLater;
	}

	public boolean isIsEarly() {
		return IsEarly;
	}

	public void setIsEarly(boolean isEarly) {
		IsEarly = isEarly;
	}

	public void setPhotoSerialNo(String photoSerialNo) {
		PhotoSerialNo = photoSerialNo;
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

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public int getCorpId() {
		return CorpId;
	}

	public void setCorpId(int corpId) {
		CorpId = corpId;
	}

	public String getAttachFileName() {
		return AttachFileName;
	}

	public void setAttachFileName(String attachFileName) {
		AttachFileName = attachFileName;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPhotoSingnOut() {
		return PhotoSingnOut;
	}

	public void setPhotoSingnOut(String photoSingnOut) {
		PhotoSingnOut = photoSingnOut;
	}

	@Override
	public String toString() {
		return "考勤信息 [_Id=" + _Id + ", Id=" + Id + ", UserId=" + UserId
				+ ", CorpId=" + CorpId + ", PublisherName=" + PublisherName
				+ ", CachePath=" + CachePath + ", AttendanceDate="
				+ AttendanceDate + ", EarlyReason=" + EarlyReason
				+ ", Employee=" + Employee + ", EmployeeName=" + EmployeeName
				+ ", PositionSignIn=" + PositionSignIn + ", PositionSignOut="
				+ PositionSignOut + ", SignInTime=" + SignInTime
				+ ", SignOutTime=" + SignOutTime + ", LaterReason="
				+ LaterReason + ", Longitude=" + Longitude + ", Latitude="
				+ Latitude + ", IsLater=" + IsLater + ", IsEarly=" + IsEarly
				+ ", AttachFileName=" + AttachFileName + ", UpdateTime="
				+ UpdateTime + ", Read=" + Read + ", PhotoSerialNo="
				+ PhotoSerialNo + "]";
	}

}
