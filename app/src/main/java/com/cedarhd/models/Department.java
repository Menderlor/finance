package com.cedarhd.models;

public class Department {

	public int 编号;

	public String 名称;

	public int 上级;

	public String 代码;

	public Boolean 停用;

	public int 负责人;
	public String 最后更新;

	public int get编号() {
		return 编号;
	}

	public void set编号(int 编号) {
		this.编号 = 编号;
	}

	public String get名称() {
		return 名称;
	}

	public void set名称(String 名称) {
		this.名称 = 名称;
	}

	public int get上级() {
		return 上级;
	}

	public void set上级(int 上级) {
		this.上级 = 上级;
	}

	public String get代码() {
		return 代码;
	}

	public void set代码(String 代码) {
		this.代码 = 代码;
	}

	public Boolean get停用() {
		return 停用;
	}

	public void set停用(Boolean 停用) {
		this.停用 = 停用;
	}

	public int get负责人() {
		return 负责人;
	}

	public void set负责人(int 负责人) {
		this.负责人 = 负责人;
	}

	public String get最后更新() {
		return 最后更新;
	}

	public void set最后更新(String 最后更新) {
		this.最后更新 = 最后更新;
	}

	@Override
	public String toString() {
		return "Department [编号=" + 编号 + ", 名称=" + 名称 + ", 上级=" + 上级 + ", 代码="
				+ 代码 + ", 停用=" + 停用 + ", 负责人=" + 负责人 + ", 最后更新=" + 最后更新 + "]";
	}

}
