package com.cedarhd.models.changhui;

import java.io.Serializable;

public class CH客户联系记录 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6939052036748283501L;

	public int 编号;

	// //[DbTextField("业务员", 40)]
	public int 业务员;

	// //[DbTextField("客户", 40)]
	public int 客户;

	// //[DbTextField("销售机会", 40)]
	public int 销售机会;

	// //[DbTextField("合同", 40)]
	public int 合同;

	// //[DbTextField("项目", 40)]
	public int 项目;

	// [DbStringField("时间", 8)]
	public String 时间;

	// //[DbTextField("联系状态", 40)]
	public int 联系状态;

	// //[DbTextField("联系人", 80)]
	public String 联系人;

	// //[DbTextField("内容", 2000)]
	public String 内容;
	// //[DbTextField("客户反馈", 2000)]
	public String 客户反馈;
	// //[DbTextField("联系形式", 40)]
	public int 联系形式;
	// //[DbTextField("意向程度", 40)]
	public int 意向程度;
	// //[DbTextField("备注", 80)]
	public String 备注;

	// [DbStringField("最后处理时间", 8)]
	public String 最后处理时间;

	// //[DbTextField("制单人", 40)]
	public int 制单人;

	// [DbStringField("制单时间", 8)]
	public String 制单时间;

	// //[DbTextField("附件", 200)]
	public String 附件;

	// //[DbTextField("主叫号码", 200)]
	public String 主叫号码;

	// //[DbTextField("被叫号码", 200)]
	public String 被叫号码;

	// //[DbTextField("坐席号", 200)]
	public String 坐席号;
	// //[DbTextField("呼叫类型", 200)]
	public int 呼叫类型;

	// //[DbTextField("地址", 200)]
	public String 地址;
	// //[DbTextField("工作类型", 200)]
	public int 工作类型;
	// //[DbTextField("是否完成", 200)]
	public boolean 是否完成;
	// //[DbTextField("问题困难", 200)]
	public String 问题困难;

	// public List<联系记录评论> 联系记录评论;

	// 联系状态比率(乘以100后)
	public double ContactStatusRate;

	public int 赞数量;

	public int 钻石数;

	public boolean 已读;

	public int 评论数;

	public String 下次联系时间;

	// [DbField("客户名称", 255)]
	public String 客户名称;

	// [DbField("业务员姓名", 255)]
	public String 业务员姓名;

	// [DbField("状态名称", 255)]
	public String 状态名称;

	// [DbField("销售机会内容", 255)]
	public String 销售机会内容;

	public String 联系形式名称;
	public String 意向程度名称;
	public String 工作类型名称;

	// [DbField("已读时间", 8)]
	public String 已读时间;

	// [DbField("评论数量", 8)]
	public int 评论数量;

	// [DbField("最后评论时间", 8)]
	public String 最后评论时间;

	// [DbField("最后更新", 8)]
	public String 最后更新;

	// [DbTextField("所在部门", 200)]
	public int 所在部门;

	// [DbTextField("所在岗位", 200)]
	public int 所在岗位;
}