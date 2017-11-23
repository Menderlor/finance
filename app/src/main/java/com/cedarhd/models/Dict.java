package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * 字典项
 * 
 * @author K
 * 
 */
public class Dict implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2448497598284609694L;
	@DatabaseField(generatedId = true, unique = true)
	int _Id;

	@DatabaseField
	public int 编号;

	@DatabaseField
	public String 名称;

	@DatabaseField
	public String 拼音;

	@DatabaseField
	public int 上级字典;

	public Dict() {
		super();
	}

	public Dict(int 编号, String 名称) {
		super();
		this.编号 = 编号;
		this.名称 = 名称;
	}

}
