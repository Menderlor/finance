package com.cedarhd.models;

import java.util.HashMap;
import java.util.List;

/**
 * 数据库查询条件
 * 
 * @author 阚健雄
 * 
 */
public class QueryDemand {

	/**
	 * 字段名,用来传递对应服务器数据库表中排序字段：最后更新，最后处理时间，考勤日期等
	 */
	public String fildName;

	/**
	 * 本地数据库中（取最后更新的最值 ）的字段：UpdateTime,AttendanceDate,
	 */
	public String sortFildName;

	/**
	 * 本地数据库排序字段名 :Time
	 */
	public String localFildName;

	/**
	 * 模糊查询条件
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, String> likeDemand = new HashMap<String, String>();

	/**
	 * 模糊查询条件,一个字段对应多个子选项
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, List<String>> likeListDemand = new HashMap<String, List<String>>();

	/**
	 * 精确查询条件 等于
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, String> eqDemand = new HashMap<String, String>();

	/**
	 * 一个字段名对应多个值 or
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, List<String>> eqListOrDemand = new HashMap<String, List<String>>();

	/**
	 * 精确查询条件 大于
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, String> gtDemand = new HashMap<String, String>();

	/**
	 * 精确查询条件 小于
	 * 
	 * key:字段名
	 * 
	 * value:字段值
	 */
	public HashMap<String, String> ltDemand = new HashMap<String, String>();

	/**
	 * 清空所有查询条件
	 */
	public void clearAll() {
		likeDemand.clear();
		likeListDemand.clear();
		eqDemand.clear();
		eqListOrDemand.clear();
		gtDemand.clear();
		ltDemand.clear();
	}

	public QueryDemand() {
		super();
	}

	/**
	 * @param sortFildName
	 *            本地数据库中（取最后更新的最值 ）的字段名：UpdateTime,AttendanceDate
	 */
	public QueryDemand(String sortFildName) {
		super();
		this.sortFildName = sortFildName;
		this.fildName = sortFildName;
	}

	public String getSortFildName() {
		return sortFildName;
	}

	public void setSortFildName(String sortFildName) {
		this.sortFildName = sortFildName;
	}

}
