package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

public class 动态已提醒 {
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;

	/**
	 * @param _Id
	 * @param id
	 */
	public 动态已提醒(int id) {
		this();
		Id = id;
	}

	/**
	 * @param _Id
	 * @param id
	 */
	public 动态已提醒() {
		super();
	}

}