package com.cedarhd.models;

import java.util.List;

public class 联系人列表 {
	private String Status;

	private List<联系人> Data;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public List<联系人> getData() {
		return Data;
	}

	public void setData(List<联系人> data) {
		Data = data;
	}

}
