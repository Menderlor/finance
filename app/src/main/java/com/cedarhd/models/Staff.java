package com.cedarhd.models;

import java.util.List;

//员工的封装类（解析从服务器那边获取的json数据）
public class Staff {
	int Status;
	List<Data> datas;

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public Staff() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<Data> getDatas() {
		return datas;
	}

	public void setDatas(List<Data> datas) {
		this.datas = datas;
	}

	@Override
	public String toString() {
		return "Staff [Status=" + Status + ", datas=" + datas + "]";
	}

	public Staff(int status, List<Data> datas) {
		super();
		Status = status;
		this.datas = datas;
	}

}
