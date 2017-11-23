package com.cedarhd.models;

import java.util.List;

/**
 * 评论提醒
 * 
 * @author kjx
 * @since 2014/08/26 15:36
 */
public class Notifications {
	// （提醒：新日志，新任务，新客户...）
	public List<日志> logList;

	public List<任务> taskList;

	public List<Client> clientList;

	public List<客户联系记录> contactsList;

	// / 订单提醒
	public List<订单> orderList;

	// / 申请与审批提醒
	public List<RemindApproval> applyList;

	/**
	 * 日志评论列表
	 */
	public List<日志评论> logCommentList;

	/**
	 * 联系记录评论列表
	 */
	public List<评论> contactsCommentList;
}
