package com.cedarhd.models;

import java.io.Serializable;

public class 钻石积分 implements Serializable{
	public int 接收人;

	public String 接收人姓名;

	public int 钻石数量;

	public int 赞数量;
	public int 赞总计;

	public 钻石积分() {
		// TODO Auto-generated constructor stub
	}

	public 钻石积分(int 接收人, String 接收人姓名, int 钻石数量, int 赞数量, int 赞总计) {
		super();
		this.接收人 = 接收人;
		this.接收人姓名 = 接收人姓名;
		this.钻石数量 = 钻石数量;
		this.赞数量 = 赞数量;
		this.赞总计 = 赞总计;
	}

}
