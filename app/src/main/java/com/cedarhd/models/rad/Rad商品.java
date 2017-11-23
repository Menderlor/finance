package com.cedarhd.models.rad;

import java.io.Serializable;
import java.util.HashMap;

public class Rad商品 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6541572090011918742L;
	// / <summary>
	// / 分类使用 商品分类 表
	// / </summary>
	public int 编号;
	public int 型号;

	public String 编码;

	public String 条码;

	public int 选项1;
	public int 选项2;
	public int 选项3;
	public int 选项4;
	public int 选项5;
	public int 选项6;
	public int 选项7;
	public int 选项8;
	public int 选项9;
	public int 选项10;

	public String 文本1;
	public String 文本2;
	public String 文本3;
	public String 文本4;
	public String 文本5;

	public int 数值1;
	public int 数值2;
	public int 数值3;
	public int 数值4;
	public int 数值5;

	public String 时间1;
	public String 时间2;
	public String 时间3;
	public String 时间4;
	public String 时间5;

	public String 多选1;
	public String 多选2;
	public String 多选3;
	public String 多选4;
	public String 多选5;

	public double 销售单价;

	public double 销售原价;

	public double 采购单价;

	public boolean 停用;
	public String 图片;
	public String 描述;

	public double 最低库存;

	public double 最高库存;

	// / <summary>
	// / 保存 字典项的文本值 如 ：{选项1：蓝色}
	// / </summary>
	public HashMap<String, String> 字典值;

}
