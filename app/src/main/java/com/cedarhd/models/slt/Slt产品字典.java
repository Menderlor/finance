package com.cedarhd.models.slt;

import com.cedarhd.models.字典;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Slt产品字典 extends 字典 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField
	public String DictTableName;

	public Slt产品字典() {
		super();
	}

	public Slt产品字典(String 字典表名) {
		super();
		this.DictTableName = 字典表名;
	}

	public Slt产品字典(int 编号, String 名称, String 字典表名) {
		super();
		this.Id = 编号;
		this.Name = 名称;
		this.DictTableName = 字典表名;
	}

}