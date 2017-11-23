package com.cedarhd.models.crm;

import java.io.Serializable;

public class 线索 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4301487496956315923L;
	public int 编号;
	public int 来源;
	public int 分类;
	public int 创建人;
	public int 业务员;
	public int 状态;

	public String 联系人;
	public String 联系电话;
	public String 公司名称;
	public int 意向产品;
	public String 创建时间;
	public String 分配给业务员时间;
	public String 预计采购时间;

	public String 最后更新;

	public double 预计金额;

	public int 客户;
	public int 销售机会;
	public int 省;
	public int 市;
	public int 县;
	public String 地址;
	public String 微信;
	public String QQ;
	public String 邮箱;
	public String 网址;
	public String 旺旺;
	public String 描述;
	public String 关键字;
}