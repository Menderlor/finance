package com.cedarhd.models;

import java.io.Serializable;

public class 客户工作计划 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7817108700321080950L;
	public int 编号;
	public int 客户;

	public int 业务员;

	public String 时间;

	public String 地点;
	public int 工作类型;
	public String 内容;
	public String 困难问题;
	public int 是否完成;
	public String 完成时间;
	public String 追踪内容;
	public int 追踪方式;
	public int 部门经理;
	public int 部门;
	public String 创建时间;

	public int sectionType;
}