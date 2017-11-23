package com.cedarhd.models;

public class 推送设置 {

	public int Id;

	// "员工"
	public int UserId;

	/***
	 * DynamicType 动态类型 1: 公告通知 2:员工日志 3:任务 4:申请审批 5:邮件 6:通讯录 7:客户 8:客户联系记录
	 * 9:销售机会
	 */
	public int DynamicType;

	// / 0代表不提醒，1或空默认代表提醒
	// "是否提醒"
	public int IsNotification;
}