package com.cedarhd.models;

import java.io.Serializable;

public class GroupModel implements Serializable{
	/**
	 * 部门编号
	 */
	public int 编号;
	/**
	 * 部门名称
	 */
	public String 名称;
	/**
	 * 员工编号
	 */
	public int Id;
	/**
	 * 员工头像地址
	 */
	public String Admin;
	public String userName;
	public String AvatarURI;
	public boolean isdepart;
	public GroupModel() {
		// TODO Auto-generated constructor stub
	}
	public GroupModel(int 编号, String 名称, int id, String avatarURI,boolean isdepart,String Admin,String userName) {
		super();
		this.编号 = 编号;
		this.名称 = 名称;
		this.Id = id;
		this.AvatarURI = avatarURI;
		this.isdepart=isdepart;
		this.Admin=Admin;
		this.userName=userName;
	}
	
}
