package com.cedarhd.models;

import java.util.Date;

public class Process_E {

	public int 编号;

	public int 流程分类编号;

	public String 流程分类名称;

	public int 表单数据编号;

	public String 名称;

	public int 创建人;

	public String 创建人名称;

	public int 下个步骤审核人;

	public Date 创建时间;

	public String 当前状态;

	public int 下个步骤编号;

	public String 下个步骤;

	public Date 上个步骤完成时间;

	public String 可编写单元格;

	public String 隐藏单元格;

	public String 工作流标识;

	public String 表单名称;

	public Boolean 完成;

	public int get编号() {
		return 编号;
	}

	public int get流程分类编号() {
		return 流程分类编号;
	}

	public int get表单数据编号() {
		return 表单数据编号;
	}

	public String get名称() {
		return 名称;
	}

	public int get创建人() {
		return 创建人;
	}

	public String get创建人名称() {
		return 创建人名称;
	}

	public int get下个步骤审核人() {
		return 下个步骤审核人;
	}

	public Date get创建时间() {
		return 创建时间;
	}

	public String get当前状态() {
		return 当前状态;
	}

	public int get下个步骤编号() {
		return 下个步骤编号;
	}

	public String get下个步骤() {
		return 下个步骤;
	}

	public Date get上个步骤完成时间() {
		return 上个步骤完成时间;
	}

	public String get可编写单元格() {
		return 可编写单元格;
	}

	public String get隐藏单元格() {
		return 隐藏单元格;
	}

	public String get工作流标识() {
		return 工作流标识;
	}

	public String get表单名称() {
		return 表单名称;
	}

	public Boolean get完成() {
		return 完成;
	}
}
