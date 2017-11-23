package com.cedarhd.models;

import java.io.Serializable;

/**
 * 新的部门实体类
 * @author zmy
 *
 */
public class NewDepartMent implements Serializable{
	public int 编号;
	public String 名称;
	public int 上级;
	public String 代码;
	public String 电话;
	public String 地址;
	public boolean 停用;
	public int 负责人;
	public int 排序;
	public int 额度;
	public String 分管负责人;
	public String 分管负责人名称;
	public String 负责人名称;
	public NewDepartMent() {
		// TODO Auto-generated constructor stub
	}
	public NewDepartMent(int 编号, String 名称, int 上级, String 代码, String 电话,
			String 地址, boolean 停用, int 负责人, int 排序, int 额度, String 分管负责人,
			String 分管负责人名称, String 负责人名称) {
		super();
		this.编号 = 编号;
		this.名称 = 名称;
		this.上级 = 上级;
		this.代码 = 代码;
		this.电话 = 电话;
		this.地址 = 地址;
		this.停用 = 停用;
		this.负责人 = 负责人;
		this.排序 = 排序;
		this.额度 = 额度;
		this.分管负责人 = 分管负责人;
		this.负责人名称 = 负责人名称;
		this.分管负责人名称 = 分管负责人名称;
	}
	
}
