package com.cedarhd.models.crm;

/***
 * Crm模块用 [合同]实体
 * 
 * @author K
 * 
 */
public class 合同 {
	public int 编号;
	public int 客户;
	public int 项目;
	public int 项目类型;
	public String 单号;
	public String 名称;
	public int 制单人;
	public String 制单时间;
	public String 附件;
	public int 类型;
	public int 业务员;
	public String 甲方;
	public String 乙方;
	public String 其他方;
	public String 摘要;
	public String 备注;
	public String 截止时间;
	public String 合同期限;
	public String 生效时间;
	public String 签订时间;
	public double 金额;
	public int 流程分类表;
	public String 表单名称;
	public int 数据编号;
	public int 销售机会;
	public int 售前类型;

	/** 流程分类表对应表的“编号” */
	public int 售前;
	public double 收入;

	public double 支出;
}