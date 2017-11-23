package com.cedarhd.models;

import java.io.Serializable;
import java.util.Date;

public class 客户 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 207654601452224823L;

	public int 编号;

	public String 生日;

	public String 名称;
	public String 联系人;

	public String 电话;

	public String 地址;
	public int 省;

	public int 市;
	public int 县;
	public String 省名; // 省名
	public String 市名; // 市
	public String 县名;
	public int 行业;
	public int 来源;
	public int 销售经理;
	public int 业务员;
	public String 登记时间;
	public String 计划联系时间;
	public String 最后联系时间;
	public int 分类;
	public double 余额;

	public int 价格组;

	public String 附件;

	public String 文本1;
	public String 文本2;
	public String 文本3;
	public String 文本4;
	public String 文本5;
	public String 文本6;
	public String 文本7;
	public String 文本8;
	public String 文本9;
	public String 文本10;

	public String 文本11;
	public String 文本12;
	public String 文本13;
	public String 文本14;
	public String 文本15;
	public String 文本16;
	public String 文本17;
	public String 文本18;
	public String 文本19;
	public String 文本20;

	public String 文本21;
	public String 文本22;
	public String 文本23;
	public String 文本24;
	public String 文本25;
	public String 文本26;
	public String 文本27;
	public String 文本28;
	public String 文本29;
	public String 文本30;

	public double 数值1;
	public double 数值2;
	public double 数值3;
	public double 数值4;
	public double 数值5;

	public String 日期1;
	public String 日期2;
	public String 日期3;
	public String 日期4;
	public String 日期5;

	public String 时间1;
	public String 时间2;
	public int 单选1;
	public int 单选2;
	public int 单选3;
	public int 单选4;
	public int 单选5;
	public int 单选6;
	public int 单选7;
	public int 单选8;
	public int 单选9;
	public int 单选10;
	public int 自动完成1;
	public int 自动完成2;
	public int 自动完成3;
	public int 自动完成4;
	public int 自动完成5;
	public String 多选1;
	public String 多选2;
	public String 多选3;
	public String 多选4;
	public String 多选5;
	public boolean 选中;
	public int SQL客户关联;
	public Date 最后更新;
	public int 创建人;
	public String 手机;
	public int 状态;
	public int 客户状态;
	public int 二级来源;
	public int 市场专员;
	public String 关键字;
	public String 微信;
	public String QQ;
	public String 邮箱;
	public String 网址;
	public String 旺旺;
	public String 提示;

	@Override
	public String toString() {
		return "客户2 [编号=" + 编号 + ", 生日=" + 生日 + ", 名称=" + 名称 + ", 电话=" + 电话
				+ ", 地址=" + 地址 + ", 省=" + 省 + ", 市=" + 市 + ", 行业=" + 行业
				+ ", 来源=" + 来源 + ", 销售经理=" + 销售经理 + ", 业务员=" + 业务员 + ", 登记时间="
				+ 登记时间 + ", 计划联系时间=" + 计划联系时间 + ", 最后联系时间=" + 最后联系时间 + ", 分类="
				+ 分类 + ", 余额=" + 余额 + ", 价格组=" + 价格组 + ", 附件=" + 附件 + ", 文本1="
				+ 文本1 + ", 文本2=" + 文本2 + ", 文本3=" + 文本3 + ", 文本4=" + 文本4
				+ ", 文本5=" + 文本5 + ", 文本6=" + 文本6 + ", 文本7=" + 文本7 + ", 文本8="
				+ 文本8 + ", 文本9=" + 文本9 + ", 文本10=" + 文本10 + ", 文本11=" + 文本11
				+ ", 文本12=" + 文本12 + ", 文本13=" + 文本13 + ", 文本14=" + 文本14
				+ ", 文本15=" + 文本15 + ", 文本16=" + 文本16 + ", 文本17=" + 文本17
				+ ", 文本18=" + 文本18 + ", 文本19=" + 文本19 + ", 文本20=" + 文本20
				+ ", 文本21=" + 文本21 + ", 文本22=" + 文本22 + ", 文本23=" + 文本23
				+ ", 文本24=" + 文本24 + ", 文本25=" + 文本25 + ", 文本26=" + 文本26
				+ ", 文本27=" + 文本27 + ", 文本28=" + 文本28 + ", 文本29=" + 文本29
				+ ", 文本30=" + 文本30 + ", 数值1=" + 数值1 + ", 数值2=" + 数值2 + ", 数值3="
				+ 数值3 + ", 数值4=" + 数值4 + ", 数值5=" + 数值5 + ", 日期1=" + 日期1
				+ ", 日期2=" + 日期2 + ", 日期3=" + 日期3 + ", 日期4=" + 日期4 + ", 日期5="
				+ 日期5 + ", 时间1=" + 时间1 + ", 时间2=" + 时间2 + ", 单选1=" + 单选1
				+ ", 单选2=" + 单选2 + ", 单选3=" + 单选3 + ", 单选4=" + 单选4 + ", 单选5="
				+ 单选5 + ", 自动完成1=" + 自动完成1 + ", 自动完成2=" + 自动完成2 + ", 自动完成3="
				+ 自动完成3 + ", 自动完成4=" + 自动完成4 + ", 自动完成5=" + 自动完成5 + ", 多选1="
				+ 多选1 + ", 多选2=" + 多选2 + ", 多选3=" + 多选3 + ", 多选4=" + 多选4
				+ ", 多选5=" + 多选5 + ", 选中=" + 选中 + ", SQL客户关联=" + SQL客户关联
				+ ", 最后更新=" + 最后更新 + ", 创建人=" + 创建人 + ", 手机=" + 手机 + ", 状态="
				+ 状态 + ", 二级来源=" + 二级来源 + ", 市场专员=" + 市场专员 + "]";
	}
}
