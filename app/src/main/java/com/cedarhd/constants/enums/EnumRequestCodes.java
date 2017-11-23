package com.cedarhd.constants.enums;

/***
 * 请求码枚举
 * 
 * @author K
 * 
 */
public enum EnumRequestCodes {
	客户单选(101), 客户多选(102), 员工单选(301), 员工多选(202);

	private int value;

	/** 获取枚举对应整数型数值 */
	public int getValue() {
		return value;
	}

	// 带参构造函数
	EnumRequestCodes(int value) {
		this.value = value;
	}
}
