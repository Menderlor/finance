package com.cedarhd.constants.enums;

/***
 * 时间类型枚举
 * 
 * @author K
 * 
 */
public enum EnumDateType {
	其它(0), 今日(1), 本周(2), 本月(3), 本季度(4), 本年(5);
	// 私有成员变量，保存名称
	private int value;

	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	EnumDateType(int value) {
		this.value = value;
	}

}
