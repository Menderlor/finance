package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class 任务 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034509516399130020L;
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public int ProjectId;
	@DatabaseField
	public int ClientId; // 客户Id
	@DatabaseField
	public int CorpId;
	@DatabaseField
	public int UserId;
	@DatabaseField
	public int Publisher; // 发布人
	@DatabaseField
	public String PublisherName; // 发布人姓名
	@DatabaseField
	public int Executor; // 执行人id

	/** 执行人集合，用于保存多个执行人 */
	public String ExecutorList;

	@DatabaseField
	public String ExecutorName; // 执行人姓名
	@DatabaseField
	public String Participant; // 参与人
	@DatabaseField
	public String Title;

	/**
	 * 任务内容
	 */
	@DatabaseField
	public String Content;

	@DatabaseField
	public int Deadline; // 期限

	/**
	 * 附件
	 */
	@DatabaseField
	public String Attachment;

	@DatabaseField
	public String Evaluate;
	@DatabaseField
	public double Score;
	@DatabaseField
	public int Status;
	@DatabaseField
	public String StatusName;

	/**
	 * 发布时间（创建时间）
	 */
	@DatabaseField
	public String Time;

	/**
	 * 执行时间（开始时间）
	 */
	@DatabaseField
	public String AssignTime;

	/**
	 * 任务完成时间
	 */
	@DatabaseField
	public String CompletionTime;
	/**
	 * 分类
	 */
	@DatabaseField
	public int Categroy;

	@DatabaseField
	public String CategroyName;

	/**
	 * 已读未读[已读时间] 如果值为空则为已读，否则未读
	 */
	@DatabaseField
	public String ReadTime;

	/** 评论数量 */
	@DatabaseField
	public int CommentCount;

	/** 赞数量 */
	@DatabaseField
	public int SupportCount;

	/** 钻石数量 */
	@DatabaseField
	public int DiamondCount;

	/** 我的赞数量 */
	public int MySupportCount;

	/** 我发的钻石数量 */
	public int MyDiamondCount;

	public String getStatusName() {
		return StatusName;
	}

	public void setStatusName(String statusName) {
		StatusName = statusName;
	}

	public String getAssignTime() {
		return AssignTime;
	}

	public void setAssignTime(String assignTime) {
		AssignTime = assignTime;
	}

	@DatabaseField
	public Date UpdateTime;

	/**
	 * 照片序列号
	 */
	@DatabaseField
	public String photoNo;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
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
		this.ClientId = clientId;
	}

	public int getCorpId() {
		return CorpId;
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

	public int getExecutor() {
		return Executor;
	}

	public void setExecutor(int executor) {
		Executor = executor;
	}

	public String getExecutorName() {
		return ExecutorName;
	}

	public void setExecutorName(String executorName) {
		ExecutorName = executorName;
	}

	public String getParticipant() {
		return Participant;
	}

	public void setParticipant(String participant) {
		Participant = participant;
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

	public int getDeadline() {
		return Deadline;
	}

	public void setDeadline(int deadline) {
		Deadline = deadline;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public String getEvaluate() {
		return Evaluate;
	}

	public void setEvaluate(String evaluate) {
		Evaluate = evaluate;
	}

	public double getScore() {
		return Score;
	}

	public void setScore(double score) {
		Score = score;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getCompletionTime() {
		return CompletionTime;
	}

	public void setCompletionTime(String completionTime) {
		CompletionTime = completionTime;
	}

	public int getCategroy() {
		return Categroy;
	}

	public void setCategroy(int categroy) {
		Categroy = categroy;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public String getPhotoNo() {
		return photoNo;
	}

	public void setPhotoNo(String photoNo) {
		this.photoNo = photoNo;
	}

	public String getReadTime() {
		return ReadTime;
	}

	public void setReadTime(String readTime) {
		ReadTime = readTime;
	}

	public int getDiamondCount() {
		return DiamondCount;
	}

	public void setDiamondCount(int diamondCount) {
		DiamondCount = diamondCount;
	}

	@Override
	public String toString() {
		return "任务 [Id=" + Id + ", ProjectId=" + ProjectId + ", ClientId="
				+ ClientId + ", CorpId=" + CorpId + ", UserId=" + UserId
				+ ", Publisher=" + Publisher + ", PublisherName="
				+ PublisherName + ", Executor=" + Executor + ", ExecutorName="
				+ ExecutorName + ", Participant=" + Participant + ", Title="
				+ Title + ", Content=" + Content + ", Deadline=" + Deadline
				+ ", Attachment=" + Attachment + ", Evaluate=" + Evaluate
				+ ", Score=" + Score + ", Status=" + Status + ", StatusName="
				+ StatusName + ", Time=" + Time + ", AssignTime=" + AssignTime
				+ ", CompletionTime=" + CompletionTime + ", Categroy="
				+ Categroy + ", UpdateTime=" + UpdateTime + ", photoNo="
				+ photoNo + "]";
	}

}
