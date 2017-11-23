package com.cedarhd.constants.enums;

import com.cedarhd.models.Dict;

import java.util.ArrayList;
import java.util.List;

public enum Enum理财产品产品状态 {

    // 枚举成员变量，默认是静态

    未上线(1, "未上线"), 在售(2, "在售 "), 待售(3, "待售"), 已下线(4, "已下线"), 上线审批中(5, "上线审批中");

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
    Enum理财产品产品状态(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据枚举编号获取枚举名称
     */
    public static String getStatusNameById(int id) {
        for (Enum理财产品产品状态 item : Enum理财产品产品状态.values()) {
            if (id == item.getValue()) {
                return item.getName();
            }
        }
        return "";
    }

    /**
     * 根据枚举编号获取枚举名称
     */
    public static List<Dict> getAllDicts() {
        ArrayList<Dict> dicts = new ArrayList<Dict>();
        for (Enum理财产品产品状态 item : Enum理财产品产品状态.values()) {
            dicts.add(new Dict(item.getValue(), item.getName()));
        }
        return dicts;
    }
}
