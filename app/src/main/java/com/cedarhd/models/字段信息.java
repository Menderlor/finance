package com.cedarhd.models;

/**
 * @author Administrator 字段信息
 */
public class 字段信息 {

	// public String fieldName;// 字段名 附件
	public String 字段名;// 字段名 附件

	// public String fieldValue;// 字段值 result
	public String 字段值;// 字段值 result

	// public String fieldType;// 字段类型 String
	public String 字段类型;// 字段类型 String

	public String get字段名() {
		return 字段名;
	}

	public void set字段名(String 字段名) {
		this.字段名 = 字段名;
	}

	public String get字段值() {
		return 字段值;
	}

	public void set字段值(String 字段值) {
		this.字段值 = 字段值;
	}

	public String get字段类型() {
		return 字段类型;
	}

	public void set字段类型(String 字段类型) {
		this.字段类型 = 字段类型;
	}

	/**
	 * @param 字段名
	 * @param 字段值
	 * @param 字段类型
	 */
	public 字段信息() {
		super();
	}

	/**
	 * @param 字段名
	 * @param 字段值
	 * @param 字段类型
	 */
	public 字段信息(String 字段名, String 字段值, String 字段类型) {
		super();
		this.字段名 = 字段名;
		this.字段值 = 字段值;
		this.字段类型 = 字段类型;
	}
}
