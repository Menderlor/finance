package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 客户联系记录 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9222437017179085037L;
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id; // 编号

	@DatabaseField
	public int Saler;// 业务员;

	@DatabaseField
	public int Customer; // 客户;

	@DatabaseField
	public int ChanceId;// 销售机会;

	@DatabaseField
	public String ChanceContent;// 销售机会内容;

	@DatabaseField
	public int AgreementId;// 合同;

	@DatabaseField
	public int ProjectId;// 项目;

	/**
	 * 最后更新时间
	 */
	@DatabaseField
	public Date UpdateTime;

	@DatabaseField
	public int Status;// 联系状态;

	@DatabaseField
	public String StatusName;// 联系状态名称;

	@DatabaseField
	public String Contacts;// 联系人;

	@DatabaseField
	public String Content;// 内容;

	@DatabaseField
	public String Remarks;// 备注;

	@DatabaseField
	public String LastProcessTime;

	@DatabaseField
	public int Preparer;// 制单人;

	@DatabaseField
	public String PrepareTime; // 制单时间

	@DatabaseField
	public String Attachments; // 附件;
	@DatabaseField
	public String Readed; // 已读人员id

	/** 已读时间 */
	@DatabaseField
	public String ReadTime;

	@DatabaseField
	public int CommentCount; // 评论数量

	@DatabaseField
	public String ClientName;

	@DatabaseField
	public String SalemanName;

	@DatabaseField
	public String isTemp;

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

	public int getSaler() {
		return Saler;
	}

	public void setSaler(int saler) {
		Saler = saler;
	}

	public int getCustomer() {
		return Customer;
	}

	public void setCustomer(int customer) {
		Customer = customer;
	}

	public int getChanceId() {
		return ChanceId;
	}

	public void setChanceId(int chanceId) {
		ChanceId = chanceId;
	}

	public int getAgreementId() {
		return AgreementId;
	}

	public void setAgreementId(int agreementId) {
		AgreementId = agreementId;
	}

	public int getProjectId() {
		return ProjectId;
	}

	public void setProjectId(int projectId) {
		ProjectId = projectId;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getStatusName() {
		return StatusName;
	}

	public void setStatusName(String statusName) {
		StatusName = statusName;
	}

	public String getContacts() {
		return Contacts;
	}

	public void setContacts(String contacts) {
		Contacts = contacts;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public String getLastProcessTime() {
		return LastProcessTime;
	}

	public void setLastProcessTime(String lastProcessTime) {
		LastProcessTime = lastProcessTime;
	}

	public int getPreparer() {
		return Preparer;
	}

	public void setPreparer(int preparer) {
		Preparer = preparer;
	}

	public String getPrepareTime() {
		return PrepareTime;
	}

	public void setPrepareTime(String prepareTime) {
		PrepareTime = prepareTime;
	}

	public String getAttachments() {
		return Attachments;
	}

	public void setAttachments(String attachments) {
		Attachments = attachments;
	}

	public String getRead() {
		return Readed;
	}

	public void setRead(String read) {
		Readed = read;
	}

	public String getChanceContent() {
		return ChanceContent;
	}

	public void setChanceContent(String chanceContent) {
		ChanceContent = chanceContent;
	}

	public String getReaded() {
		return Readed;
	}

	public void setReaded(String readed) {
		Readed = readed;
	}

	public int getCommentCount() {
		return CommentCount;
	}

	public void setCommentCount(int commentCount) {
		CommentCount = commentCount;
	}

	public String getClientName() {
		return ClientName;
	}

	public void setClientName(String clientName) {
		ClientName = clientName;
	}

	public String getSalemanName() {
		return SalemanName;
	}

	public void setSalemanName(String salemanName) {
		SalemanName = salemanName;
	}

	public String getIsTemp() {
		return isTemp;
	}

	public void setIsTemp(String isTemp) {
		this.isTemp = isTemp;
	}
}
