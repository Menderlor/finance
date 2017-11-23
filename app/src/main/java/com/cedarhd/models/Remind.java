package com.cedarhd.models;

public class Remind {

	public String Notice;

	public String Task;

	public String Email;

	public String Order;

	public String Log;

	public String Client;// 客户

	public String Contacts;// 客户联系记录

	/** 销售机会 */
	public int SaleChance;

	/** 申请审批 */
	public int Apply;

	/** 通讯录 */
	public int Communication;

	/** 未读消息总数量 */
	public int Total;

	@Override
	public String toString() {
		return "Remind [Notice=" + Notice + ", Task=" + Task + ", Email="
				+ Email + ", Order=" + Order + ", Log=" + Log + ", Client="
				+ Client + ", Contacts=" + Contacts + ", SaleChance="
				+ SaleChance + ", Apply=" + Apply + ", Communication="
				+ Communication + ", Total=" + Total + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
