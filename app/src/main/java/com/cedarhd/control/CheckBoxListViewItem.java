package com.cedarhd.control;

import android.widget.CheckBox;
import android.widget.TextView;

public class CheckBoxListViewItem {
	public String pic_url;// 头像的网址，或者本地存储的路径
	public int dptId;// 员工所属部门的编号
	public String Id;
	public String Name;
	public boolean IsChecked = false;

	public int getDptId() {
		return dptId;
	}

	public void setDptId(int dptId) {
		this.dptId = dptId;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public boolean isIsChecked() {
		return IsChecked;
	}

	public void setIsChecked(boolean isChecked) {
		IsChecked = isChecked;
	}

	/**
	 * 
	 * @param pic_url
	 *            头像地址
	 * @param id
	 *            员工编号
	 * @param name
	 *            员工姓名
	 * @param isChecked
	 *            是否选中
	 * @param dptId
	 *            部门id
	 */
	public CheckBoxListViewItem(String pic_url, String id, String name,
			boolean isChecked, int dptId) {
		super();
		this.pic_url = pic_url;
		Id = id;
		Name = name;
		IsChecked = isChecked;
		this.dptId = dptId;
	}

}

class CheckBoxListViewTag {
	public TextView tv = null;
	public CheckBox cb = null;
}
