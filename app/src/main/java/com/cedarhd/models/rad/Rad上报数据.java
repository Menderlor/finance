package com.cedarhd.models.rad;

import java.io.Serializable;

public class Rad上报数据 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5478559401259462358L;

	public int 编号;

	public int 上报人;

	public int 上报店;

	public String 上报时间;

	public double 咨询人数;

	public double 成交人数;

	public double 成交金额;

	public double 成交件数;

	public double 进店人数;

	public double 试用人数;

	public double 试用件数;

	public double 办会员卡数;
}