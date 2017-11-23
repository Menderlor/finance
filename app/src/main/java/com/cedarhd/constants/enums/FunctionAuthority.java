package com.cedarhd.constants.enums;

/** 权限枚举功能点 */
public enum FunctionAuthority {
	部门客户管理("11"), 全体客户管理("12"), 全体员工管理("70"), 部门员工管理("71"), 删除客户("99"), 导出客户(
			"100"), 部门管理("97"), 审批流程("61"), 流程节点配置("62"), 删除工作流("63"), 查看员工工资(
			"198"), 销售机会("209"), 联系记录("210"), 工作计划("211"), 附件("212"), 理疗记录(
			"213"), 投诉建议("214"), 预约记录("215"), 渠道维护记录("216"), 客户消费记录("217"), 客户拜访登记表(
			"219"), 查看全部橱柜订单("180"), 客户回访记录("285");

	// 私有成员变量，保存名称
	private String value;

	public String getValue() {
		return value;
	}

	// 带参构造函数
	FunctionAuthority(String value) {
		this.value = value;
	}
}
