package com.cedarhd.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 流程表单实体，用于解析xml表单为Json对象
 * 
 * @author kjx 2015/03/10 10:50
 * 
 */
public class VmFormDef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8735580487073735103L;

	public String Content;

	/** 数据库表名 */
	public String TableName;

	/** 明细表名 */
	public String DetailTableName;

	/***
	 * 表单名称
	 */
	public String FormName;

	/** 字典项集合 */
	public HashMap<String, HashMap<Integer, String>> Dictionaries;

	/** 主表字段定义，如果有数据的话，还包含相关的值 */
	public List<FieldInfo> Fields;

	/** 明细表的字段定义 */
	public List<FieldInfo> DetailFields;

	/** 明细表的字段的值，第一层数组为行，第二层每行各个字段的值 */
	public List<List<String>> DetailValues;

	/** 如果该表单已经提交申请，应该会有一个 流程表 的记录。应该设置这个值方便使用 */
	public int FlowId;

	/** 流程过程表：用于显示流程节点 */
	public List<流程过程表> Results;

	/*** 当前节点可编辑单元格 */
	public String CurrentNodeEditableCells;

	/*** 当前节点隐藏单元格 */
	public String HiddenFields;

	public 流程 Workflow;

}