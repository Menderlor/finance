package com.cedarhd.models;

import java.io.Serializable;

public class 流程过程表 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4656382682483156446L;

	public int id;

	public int userId;

	public String opinion;

	public String result;

	public String UpdateTime;

	public String signure;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.UpdateTime = updateTime;
	}

	public String getSignure() {
		return signure;
	}

	public void setSignure(String signure) {
		this.signure = signure;
	}

}
