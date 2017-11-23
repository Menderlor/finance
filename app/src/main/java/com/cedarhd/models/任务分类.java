package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 任务分类 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -888236795093459646L;

	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;

	@DatabaseField
	public String Name;

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	
}
