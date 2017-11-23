package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @author py 2014.8.12
 */
public class 帖子 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;
	@DatabaseField
	public int Id;// 编号
	@DatabaseField
	public String Title;// 标题
	@DatabaseField
	public String Content;// 内容
	@DatabaseField
	public int Poster;// 发帖人
	@DatabaseField
	public int board;// 论坛版块
	@DatabaseField
	public String PostTime;// 发帖时间
	@DatabaseField
	public int ReplyCount;// 回复次数
	@DatabaseField
	public Date UpdateTime;// 更新时间
	@DatabaseField
	public String Attachment;// 附件
	@DatabaseField
	public int Sort;// 类型
	@DatabaseField
	public String Name;// 名称
	public List<论坛回帖> ReplyList;// 回帖列表

	/** 图片以外的其他格式附件编号 */
	public String otherAttachIds; //

	public String 其他可看人;

	public String 不可看人;

	/***
	 * 附件图片列表
	 */
	public ArrayList<String> AttachImageList;

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

	public int getPoster() {
		return Poster;
	}

	public void setPoster(int poster) {
		Poster = poster;
	}

	public int getBoard() {
		return board;
	}

	public void setBoard(int board) {
		this.board = board;
	}

	public String getPostTime() {
		return PostTime;
	}

	public void setPostTime(String postTime) {
		PostTime = postTime;
	}

	public int getReplyCount() {
		return ReplyCount;
	}

	public void setReplyCount(int replyCount) {
		ReplyCount = replyCount;
	}

	public Date getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Date updateTime) {
		UpdateTime = updateTime;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public int getSort() {
		return Sort;
	}

	public void setSort(int sort) {
		Sort = sort;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public List<论坛回帖> getReplyList() {
		return ReplyList;
	}

	public void setReplyList(List<论坛回帖> replyList) {
		ReplyList = replyList;
	}

	@Override
	public String toString() {
		return "lt帖子 [Id=" + Id + ", Title=" + Title + ", Content=" + Content
				+ ", Poster=" + Poster + ", board=" + board + ", PostTime="
				+ PostTime + ", ReplyCount=" + ReplyCount + ", UpdateTime="
				+ UpdateTime + ", Attachment=" + Attachment + ", Sort=" + Sort
				+ ", Name=" + Name + "]";
	}

}
