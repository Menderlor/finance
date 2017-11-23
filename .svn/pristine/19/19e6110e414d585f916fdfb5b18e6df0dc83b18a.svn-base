package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class AlarmTask implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = -9034509516399130020L;
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;
	@DatabaseField
	public int Id;
	@DatabaseField
	public int Publisher; // 发布人
	@DatabaseField
	public int Executor; // 执行人id

	@DatabaseField
	public String Title;

	/**
	 * 任务内容
	 */
	@DatabaseField
	public String Content;

	/**
	 * 执行时间（开始时间） 闹钟提醒时间
	 */
	@DatabaseField
	public String AssignTime;

}