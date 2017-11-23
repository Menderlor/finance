package com.cedarhd.models;

import java.io.Serializable;

public class 字段描述 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4764436316530871650L;
	// [Key]
	// // [DbIdentityField("编号")]
	public int 编号;
	// // [DbTextField("表名", 40)]
	public String 表名;
	// // [DbTextField("字段名", 40)]
	public String 字段名;
	// // [DbTextField("字段显示名", 40)]
	public String 字段显示名;
	// // [DbTextField("宽度", 40)]
	public int 宽度;
	// / Text = 0, Integer = 1, Double = 2, CheckBox = 3,
	// / Date = 4, Time = 5, ComboBox = 6, AutoComplete = 7,
	// / CheckListBox = 8,
	// / 输入类型：0: 文本 1：数值 2：日期 3：时间 4：bool 5：下拉选择, 7：AutoCompleteBox
	public int 输入类型;

	public int 基本字段;
	public int 排序;
	public int 隐藏;
	public int 只读;
	public int 字段长度;
	public int 主键;
	public String SQL类型;
	public String 字典;

	public String 公式;

	public int 计算顺序;

	public boolean 显示到列表;
	public String 显示名公式;
	public Boolean Required;
	public Boolean UniqueField;

	// / 如果两个字段的UniqueGroup相同，则数据不能重复
	public int UniqueGroup;

	public String RegEx;

	public String Format;

	public boolean 创建索引;

	public String 分类;

	public String 颜色;

	public String 上级字段;
	public String 下级字段;
	public String 默认值;
	public boolean 不为空;

}
