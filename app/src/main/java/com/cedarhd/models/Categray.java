package com.cedarhd.models;

/**
 * 分类
 * 
 * @author kjx
 * 
 */
public class Categray {
	public int Id;

	public String Name;

	public int parentId;

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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

}
