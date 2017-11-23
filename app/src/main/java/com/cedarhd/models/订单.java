package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单信息
 * 
 * @author bohr
 * 
 */
public class 订单 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3292810467841626900L;
	/*
	 * 单号，客户名称，金额 ， 下单时间，财务审核时间，测量时间， 状态（文本类型）
	 * 
	 * * OrderNO, ClientName, Total, OrderTime,
	 * AccountApproveTime,MeasureTime,state
	 */
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;

	@DatabaseField
	public int ProjectId;

	@DatabaseField
	public int ClientId;

	@DatabaseField
	public int CorpId;

	@DatabaseField
	public int UserId;

	@DatabaseField
	public Date UpdateTime; // 咨询时间,系统ListView封装使用UpdateTime,

	@DatabaseField
	public String OrderNo;

	@DatabaseField
	public String ClientName; // 联系人

	@DatabaseField
	public String Total;
	@DatabaseField
	public String OrderTime;

	@DatabaseField
	public String AccountApproveTime;

	@DatabaseField
	public String MeasureTime;

	@DatabaseField
	public String State; // 状态

	@DatabaseField
	public String CustomProject; // 定做项目

	@DatabaseField
	public String Attachment; // 附件编号
	@DatabaseField
	public int Designer; // 设计师
	@DatabaseField
	public int Stage2; // 阶段
	@DatabaseField
	public String StageName; // 阶段名称
	@DatabaseField
	public String Address; // 地址
	@DatabaseField
	public String Phone; // 电话
	@DatabaseField
	public int SalesPerson; // 业务员
	@DatabaseField
	public String Readed; // 已读未读

	public int get_Id() {
		return _Id;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
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

	public int getProjectId() {
		return ProjectId;
	}

	public void setProjectId(int projectId) {
		ProjectId = projectId;
	}

	public int getClientId() {
		return ClientId;
	}

	public void setClientId(int clientId) {
		ClientId = clientId;
	}

	public int getCorpId() {
		return CorpId;
	}

	public String getCustomProject() {
		return CustomProject;
	}

	public void setCustomProject(String customProject) {
		CustomProject = customProject;
	}

	public int getDesigner() {
		return Designer;
	}

	public void setDesigner(int designer) {
		Designer = designer;
	}

	public int getStage2() {
		return Stage2;
	}

	public void setStage2(int stage2) {
		Stage2 = stage2;
	}

	public String getStageName() {
		return StageName;
	}

	public void setStageName(String stageName) {
		StageName = stageName;
	}

	public void setCorpId(int corpId) {
		CorpId = corpId;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public String getOrderNO() {
		return OrderNo;
	}

	public void setOrderNO(String orderNO) {
		OrderNo = orderNO;
	}

	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}

	public String getTotal() {
		return Total;
	}

	public void setTotal(String total) {
		Total = total;
	}

	public String getOrderTime() {
		return OrderTime;
	}

	public void setOrderTime(String orderTime) {
		OrderTime = orderTime;
	}

	public String getAccountApproveTime() {
		return AccountApproveTime;
	}

	public void setAccountApproveTime(String accountApproveTime) {
		AccountApproveTime = accountApproveTime;
	}

	public String getMeasureTime() {
		return MeasureTime;
	}

	public void setMeasureTime(String measureTime) {
		this.MeasureTime = measureTime;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public int getSalesPerson() {
		return SalesPerson;
	}

	public void setSalesPerson(int salesPerson) {
		SalesPerson = salesPerson;
	}

	public String getReaded() {
		return Readed;
	}

	public void setReaded(String readed) {
		Readed = readed;
	}

	// @DatabaseField(generatedId = true, unique = true)
	// int _Id;
	// @DatabaseField
	// public int 编号;
	// @DatabaseField
	// public int 项目编号;
	// @DatabaseField
	// public int 订单编号;
	// @DatabaseField
	// public int 客户编号;
	// @DatabaseField
	// public String 最后更新;
	// @DatabaseField
	// public String 单号;
	// @DatabaseField
	// public String 客户名称;
	// @DatabaseField
	// public String 金额;
	// @DatabaseField
	// public String 下单时间;
	// @DatabaseField
	// public String 财务审核时间;
	// @DatabaseField
	// public String 测量时间;
	// @DatabaseField
	// public String 状态;

}
