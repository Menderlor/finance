package com.cedarhd.models;

import java.util.List;

public class Node {
	/**
	 * 节点编号
	 */
	public int id;

	/**
	 * 节点信息的描述
	 */
	public String name;

	/**
	 * 该节点是否展开
	 */
	public boolean isExpand;

	/**
	 * 父节点
	 */
	public Node parentNode;

	/**
	 * 父节点编号
	 */
	public int parentId;

	/**
	 * 子节点
	 */
	public List<Node> childList;

	/**
	 * 该节点到屏幕左侧间距
	 */
	public int leftPadding;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public List<Node> getChildList() {
		return childList;
	}

	public void setChildList(List<Node> childList) {
		this.childList = childList;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public Node() {
		super();
	}

	/**
	 * 
	 * @param id
	 *            分类编号
	 * @param name
	 *            分类名称
	 * @param parentId
	 *            上级 分类的编号
	 */
	public Node(int id, String name, int parentId) {
		super();
		this.id = id;
		this.name = name;
		this.parentId = parentId;
	}

}
