package com.cedarhd.models.rad;

import com.cedarhd.models.字典;
import com.j256.ormlite.field.DatabaseField;

public class Rad产品字典 extends 字典 {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7643129377171075937L;

	@DatabaseField
	public String DictTableName;

	public Rad产品字典() {
		super();
	}

	public Rad产品字典(String 字典表名) {
		super();
		this.DictTableName = 字典表名;
	}

	public Rad产品字典(int 编号, String 名称, String 字典表名) {
		super();
		this.Id = 编号;
		this.Name = 名称;
		this.DictTableName = 字典表名;
	}

}
