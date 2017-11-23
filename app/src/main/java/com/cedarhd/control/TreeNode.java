package com.cedarhd.control;

public class TreeNode {
	private String id;// ID
	private String name;// 显示文本
	private boolean isextends;// 展开状态
	private String parent;// 父级ID
	private boolean IsChecked;// 勾选状态

	public TreeNode() {
	}

	public TreeNode(String id, String name, String parent, boolean isextends) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.isextends = isextends;
	}

	public TreeNode(String id, String name, String parent, boolean isextends,
			boolean IsChecked) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.isextends = isextends;
		this.IsChecked = IsChecked;
	}

	public boolean Getisextends() {
		return isextends;
	}

	public void Setisextends(boolean isextends) {
		this.isextends = isextends;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean GetIsChecked() {
		return IsChecked;
	}

	public void SetIsChecked(boolean IsChecked) {
		this.IsChecked = IsChecked;
	}
}
