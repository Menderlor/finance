package com.cedarhd.constants.enums;

import com.cedarhd.models.Dict;

import java.util.ArrayList;
import java.util.List;

/**
 * 附件类型
 * 
 * @author K
 * 
 */
public enum EnumAttachType {

	// 枚举成员变量，默认是静态

	附件号(0, "附件号"), 附件URL(1, "附件URL"), 本地路径(2, "本地路径"), 新建(3, "新建");

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
	EnumAttachType(int value, String name) {
		this.value = value;
		this.name = name;
	}

	/** 根据枚举编号获取枚举名称 */
	public static String getStatusNameById(int id) {
		for (Enum理财产品预约状态 item : Enum理财产品预约状态.values()) {
			if (id == item.getValue()) {
				return item.getName();
			}
		}
		return "";
	}

	/** 根据枚举编号获取枚举名称 */
	public static List<Dict> getDicts(int id) {
		ArrayList<Dict> dicts = new ArrayList<Dict>();
		for (Enum理财产品预约状态 item : Enum理财产品预约状态.values()) {
			dicts.add(new Dict(item.getValue(), item.getName()));
		}
		return dicts;
	}

}
