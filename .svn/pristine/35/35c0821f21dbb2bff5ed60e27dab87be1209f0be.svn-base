package com.cedarhd.models;

import com.cedarhd.utils.StrUtils;
import com.j256.ormlite.field.DatabaseField;

public class 部门 {

	public 部门() {
		super();
		// TODO Auto-generated constructor stub
	}

	@DatabaseField(generatedId = true, unique = true)
	int _Id;

	@DatabaseField
	int 编号;

	@DatabaseField
	String 名称;

	@DatabaseField
	int 上级;

	@DatabaseField
	String 代码;

	@DatabaseField
	boolean 停用;

	@DatabaseField
	int 负责人;

	@DatabaseField
	public String 最后更新;

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public int get编号() {
		return 编号;
	}

	public void set编号(int 编号) {
		this.编号 = 编号;
	}

	public String get名称() {
		return StrUtils.pareseNull(名称);
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

	public boolean is停用() {
		return 停用;
	}

	public void set停用(boolean 停用) {
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

	public void setUpdateTime(String 最后更新) {
		this.最后更新 = 最后更新;
	}

	public 部门(int _Id, int 编号, String 名称, int 上级, String 代码, boolean 停用,
			int 负责人, String 最后更新) {
		super();
		this._Id = _Id;
		this.编号 = 编号;
		this.名称 = 名称;
		this.上级 = 上级;
		this.代码 = 代码;
		this.停用 = 停用;
		this.负责人 = 负责人;
		this.最后更新 = 最后更新;
	}

	@Override
	public String toString() {
		return "部门 [_Id=" + _Id + ", 编号=" + 编号 + ", 名称=" + 名称 + ", 上级=" + 上级
				+ ", 代码=" + 代码 + ", 停用=" + 停用 + ", 负责人=" + 负责人 + ", =最后更新="
				+ 最后更新 + "]";
	}

}
