package com.cedarhd.models;

import java.util.List;

public class 评论列表 {

	private String Status;

	private List<评论> Data;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public List<评论> getData() {
		return Data;
	}

	public void setData(List<评论> data) {
		Data = data;
	}

}
