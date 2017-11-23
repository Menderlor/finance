package com.cedarhd.models;

import com.cedarhd.constants.enums.EnumFunctionPoint;

/** 首页导航菜单 子项 */
public class MenuChildItem {

	/** 图标 */
	public int ico;

	/** 标题 */
	public String title;

	/** 未读 数量 */
	public int count;

	/** 模块功能点 */
	public EnumFunctionPoint ponit;

	/***
	 * 未读数量默认为0
	 * 
	 * @param ico
	 *            图标
	 * @param title
	 *            标题
	 * @param ponit
	 *            功能点
	 */
	public MenuChildItem(int ico, String title, EnumFunctionPoint ponit) {
		super();
		this.ico = ico;
		this.title = title;
		this.count = 0;
		this.ponit = ponit;
	}

	/***
	 * 
	 * @param ico
	 *            图标
	 * @param title
	 *            标题
	 * @param count
	 *            未读数量
	 * @param ponit
	 *            功能点
	 */
	public MenuChildItem(int ico, String title, int count,
			EnumFunctionPoint ponit) {
		super();
		this.ico = ico;
		this.title = title;
		this.count = count;
		this.ponit = ponit;
	}

}
