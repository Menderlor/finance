package com.cedarhd.models.rad;

import java.util.List;

/// <summary>
/// 产品简略，用于手机端返回数据
/// </summary>
public class Rad商品型号 {
	public int 编号;
	public String 名称;

	public int 分类;

	public String 代码;

	public String 图片;

	public double 销售单价;

	public double 销售原价;

	public double 采购单价;

	public double 最低库存;

	public double 最高库存;

	public boolean 停用;

	public List<Rad商品> 商品列表s;
}