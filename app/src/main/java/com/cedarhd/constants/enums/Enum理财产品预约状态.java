package com.cedarhd.constants.enums;

import com.cedarhd.models.Dict;

import java.util.ArrayList;
import java.util.List;

public enum Enum理财产品预约状态 {

    // 枚举成员变量，默认是静态

    未提交(0, "未提交"), 待审核(1, "待审核"), 预约成功(2, "预约成功 "), 已打款(3, "已打款"), 已到账(4, "已到账"), 排队失败(
            5, "预约失败 "), 合同作废(6, "合同作废"), 已撤销(7, "已撤销 ");

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
    Enum理财产品预约状态(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * 根据枚举编号获取枚举名称
     */
    public static String getStatusNameById(int id) {
        for (Enum理财产品预约状态 item : Enum理财产品预约状态.values()) {
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
        for (Enum理财产品预约状态 item : Enum理财产品预约状态.values()) {
            dicts.add(new Dict(item.getValue(), item.getName()));
        }
        return dicts;
    }
}
