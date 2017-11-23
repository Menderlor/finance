package com.cedarhd.constants.enums;

import com.cedarhd.models.Dict;

import java.util.ArrayList;
import java.util.List;

public enum Enum理财产品购买合同状态 {

	// 枚举成员变量，默认是静态

	已打款(1, "已打款"), 已到账(2, "已到账 "), 已通过(3, "已通过"), 已作废(4, "已作废"), 已转投(5, "已转投 "), 已续投(
			6, "已续投"), 已错时匹配转投(7, " 已错时匹配转投"), 已转让收益权(8, "已转让收益权"), 已提前赎回(9,
			"已提前赎回"),已追加(11,"已追加");

	// 私有成员变量，保存名称
	private int value;
	private String name;

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	// 带参构造函数
	Enum理财产品购买合同状态(int value, String name) {
		this.value = value;
		this.name = name;
	}

	/** 根据枚举编号获取枚举名称 */
	public static String getStatusNameById(int id) {
		for (Enum理财产品购买合同状态 item : Enum理财产品购买合同状态.values()) {
			if (id == item.getValue()) {
				return item.getName();
			}
		}
		return "";
	}

	/** 根据枚举编号获取枚举名称 */
	public static List<Dict> getDicts(int id) {
		ArrayList<Dict> dicts = new ArrayList<Dict>();
		for (Enum理财产品购买合同状态 item : Enum理财产品购买合同状态.values()) {
			dicts.add(new Dict(item.getValue(), item.getName()));
		}
		return dicts;
	}
}
