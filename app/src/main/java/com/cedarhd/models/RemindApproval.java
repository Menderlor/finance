package com.cedarhd.models;

/**
 * 申请提醒
 * 
 * @author BOHR
 * 
 */
public class RemindApproval {
	public int Id;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	@Override
	public String toString() {
		return "RemindApproval [Id=" + Id + "]";
	}
}
