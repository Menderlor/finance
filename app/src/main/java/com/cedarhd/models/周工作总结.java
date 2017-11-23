package com.cedarhd.models;

import java.io.Serializable;
import java.util.Date;

public class 周工作总结 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2191645476321112168L;

	public int 编号;

	public int 制单人;

	public String 制单人名称;

	public Date 制单时间;

	public int 创建人;

	public String 创建人名称;

	public String 负责区域;

	public String 开始时间;

	public String 结束时间;

	public String 本周已完成工作;

	public String 下周计划完成工作;

	public String 本周未完成工作;

	public String 自我总结;
}