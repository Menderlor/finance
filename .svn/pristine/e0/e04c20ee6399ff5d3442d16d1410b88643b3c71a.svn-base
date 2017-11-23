package com.cedarhd.constants.enums;

/**
 * Created by 王安民 on 2017/9/30.
 */

public enum Enum理财产品预约类型 {

    新增(0, "新增"), 追加(6, "追加");

    private String name;

    private int value;

    public String getName() {
        return name;
    }

    /**
     * 根据枚举编号获取枚举名称
     */
    public static String getStatusNameById(int id) {
        for (Enum理财产品预约类型 item : Enum理财产品预约类型.values()) {
            if (id == item.getValue()) {
                return item.getName();
            }
        }
        return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    Enum理财产品预约类型(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
