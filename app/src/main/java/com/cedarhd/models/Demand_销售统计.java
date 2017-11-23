package com.cedarhd.models;

import com.cedarhd.constants.enums.EnumDateType;
import com.cedarhd.constants.enums.EnumSaleType;

/// <summary>
/// 手机调用接口传递的查询条件
/// </summary>
public class Demand_销售统计 {
	/** 日期类型 使用枚举类型 {@link EnumDateType} */
	public int dateType;

	/**
	 * 销售类型 使用枚举类型 {@link EnumSaleType}
	 */
	public int saleType;

	public int userId;

	public int deptId;

	public String startTime;

	public String endTime;

	public String defaultTime;

	/***
	 * 偏移量
	 */
	public int pageIndex;

	/** 每页数量 */
	public int pageSize;

	/** 页码 */
	public int Offset;
}