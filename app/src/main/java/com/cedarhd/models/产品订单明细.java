package com.cedarhd.models;

import java.util.Date;

public class 产品订单明细 {
	public int 编号;
	public String 产品名称;

	public int jy订单;

	public int 制单人;
	public int 产品;
	public double 单价;
	public int 数量;
	public double 小计;
	public Date 制单时间;

	/** 是否选中 */
	public boolean isCheced;

	/**
	 * @param 型号
	 * @param 单价
	 * @param 数量
	 * @param 小计金额
	 */
	public 产品订单明细(int 产品, double 单价, int 数量, double 小计金额) {
		this.产品 = 产品;
		this.单价 = 单价;
		this.数量 = 数量;
		this.小计 = 小计金额;
	}

	public 产品订单明细() {

	}

	public boolean isCheced() {
		return isCheced;
	}

	public void setCheced(boolean isCheced) {
		this.isCheced = isCheced;
	}

}
