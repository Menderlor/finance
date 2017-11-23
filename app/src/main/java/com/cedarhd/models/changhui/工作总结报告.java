package com.cedarhd.models.changhui;

import java.io.Serializable;

public class 工作总结报告 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8341507251310993807L;
	public int 编号;
	public int 制单人;
	public int 员工;
	public int 负责区域;
	public String 制单时间;
	public String 开始时间;
	public String 结束时间;
	public String 已完成工作;
	public String 未完成工作;
	public String 计划完成工作;
	public String 自我总结;
	public String 附件;
	/***
	 * 日报=1,周报=2,月报 =3
	 */
	public int 类型;

	public String 已读时间;
}
