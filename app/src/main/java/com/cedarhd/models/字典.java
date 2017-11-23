package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

public class 字典 {
	@DatabaseField(generatedId = true, unique = true)
	int _Id;

	@DatabaseField
	public int Id;

	@DatabaseField
	public String Name;

	@DatabaseField
	public String ParentNode;

	/**
	 * 
	 */
	public 字典() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public 字典(int Id, String Name) {
		this.Id = Id;
		this.Name = Name;
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

	public String getParentNode() {
		return ParentNode;
	}

	public void setParentNode(String parentNode) {
		ParentNode = parentNode;
	}

}
