package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

public class 已提醒 {
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;

	@DatabaseField
	public int Id;

	@DatabaseField
	public int Classifi;
}
