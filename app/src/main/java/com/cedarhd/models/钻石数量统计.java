package com.cedarhd.models;

import java.io.Serializable;

public class 钻石数量统计  implements Serializable{
	public int 发放人;
	public String 发放人姓名;
	public int 接收人;
	public String 接收人姓名;
	public int 数据编号;
	public int 数据类型;
	public int 数量;
	public 钻石数量统计() {
		// TODO Auto-generated constructor stub
	}
	public 钻石数量统计(int 发放人, String 发放人姓名, int 接收人, String 接收人姓名, int 数据编号,
			int 数据类型, int 数量) {
		super();
		this.发放人 = 发放人;
		this.发放人姓名 = 发放人姓名;
		this.接收人 = 接收人;
		this.接收人姓名 = 接收人姓名;
		this.数据编号 = 数据编号;
		this.数据类型 = 数据类型;
		this.数量 = 数量;
	}
	
}
