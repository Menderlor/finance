package com.cedarhd.constants.enums;

import com.cedarhd.models.Dict;

import java.util.ArrayList;
import java.util.List;

public enum Enum合格投资者认证状态 {

    // 枚举成员变量，默认是静态

    未认证(1, "未认证"), 认证中(2, "认证中 "), 未通过(3, "未通过"), 已过期(4, "已过期"), 已认证(
            5, "已认证 ");

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
    Enum合格投资者认证状态(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据枚举编号获取枚举名称
     */
    public static String getStatusNameById(int id) {
        for (Enum合格投资者认证状态 item : Enum合格投资者认证状态.values()) {
            if (id == item.getValue()) {
                return item.getName();
            }
        }
        return "";
    }


    /**
     * 根据枚举编号获取枚举名称
     */
    public static List<Dict> getDicts(int id) {
        ArrayList<Dict> dicts = new ArrayList<Dict>();
        for (Enum合格投资者认证状态 item : Enum合格投资者认证状态.values()) {
            dicts.add(new Dict(item.getValue(), item.getName()));
        }
        return dicts;
    }
}
