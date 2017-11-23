package com.cedarhd.constants.enums;

/**
 * 手机端枚举功能点
 * 
 * @author kjx
 * 
 */
public enum EnumFunctionPoint {

	ATTANCE(0), LOG(1), TASK(2), NOTICE(3), APPLY(4), CLIENT(5), SALECHANCE(6), SALESUMARY(
			7), CONTACTS(8), PROJECT(9), ORDER(10), ADVERTISEMENT(11), COMMUNICATION(
			13), APPLY_INBOX(14), APPLY_OUTBOX(15), PRODUCT(16), RANKING(17), CLEW(
			18), CONPACT(19), RECEIPET(20), EXPENSE(21), RAD_PRODUCTLIST(31), RAD_ORDER(
			32), RAD_REPORT(33), RAD_CACULATE(34), RAD_SCAN_CODE(35), SLT_PRODUCTLIST(
			41), SLT_ORDER(42), SLT_REPORT(44), SLT_CACULATE(45), SLT_SHOPCAR_LIST(
			46), SLT_SCAN_CODE(47), SLT_SALE_TARGET(48), SLT_APPROVE_ORDER(49), CHANGHUI_WORK_PLAN(
			60), CHANGHUI_PRODUCT_LIST(61), CHANGHUI_CONTACT_LIST(64), CHANGHUI_BESPOKE_LIST(
			66), CHANGHUI_CLIENT_LIST(68), XUESONG_NULL(100),COMPANY_SPACE(101),PROFIT_CALCULATOR(102);

	// 私有成员变量，保存名称
	private int value;

	public int getValue() {
		return value;
	}

	// 带参构造函数
	EnumFunctionPoint(int value) {
		this.value = value;
	}
}
