package com.cedarhd.models;

import java.io.Serializable;

public class 产品型号 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2738049902574853660L;

	public int 编号;

	public String 名称;

	public int 分类;

	public String 描述;

	/***
	 * 图片路径
	 */
	public String 图片;

	public double 单价;

	public String 编码;

	public String 条码;

	private boolean isCheck;

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
}