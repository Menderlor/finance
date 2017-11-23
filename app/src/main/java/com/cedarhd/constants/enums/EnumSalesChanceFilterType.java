package com.cedarhd.constants.enums;

/***
 * 销售机会联系记录分类枚举
 * 
 * @author K
 * 
 */
public enum EnumSalesChanceFilterType {
	最近联系(1), 计划提醒(2), 关注客户(3), 最新客户(4), 关注销售机会(5), 最新销售机会(6), 一周未联系(7), 半月未联系(8), 一月未联系(
			9), 三月未联系(10), 半年已上未联系(11), N天以上未联系(12);

	// 私有成员变量，保存名称
	private int value;

	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	EnumSalesChanceFilterType(int value) {
		this.value = value;
	}
}