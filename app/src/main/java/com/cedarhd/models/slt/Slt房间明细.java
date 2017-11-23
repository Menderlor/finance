package com.cedarhd.models.slt;

import java.io.Serializable;

public class Slt房间明细 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2283480538070257272L;

	public int 编号;

	public String 名称;

	public double 长;

	public double 宽;

	public double 面积;

	public String 朝向;

	public int 订单明细;

	public Slt订单明细 订单明细详情;

	public Slt房间明细() {

	}

	public Slt房间明细(String 名称, double 长, double 宽, double 面积, String 朝向) {
		this.名称 = 名称;
		this.长 = 长;
		this.宽 = 宽;
		this.面积 = 面积;
		this.朝向 = 朝向;
	}

}