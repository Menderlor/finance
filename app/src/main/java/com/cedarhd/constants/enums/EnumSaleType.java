package com.cedarhd.constants.enums;

/***
 * 销售类型枚举
 * 
 * @author K
 * 
 */
public enum EnumSaleType {
	签单(0), 回款(1);

	// 私有成员变量，保存名称
	private int value;

	/** 获取枚举值 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	EnumSaleType(int value) {
		this.value = value;
	}

}
