package com.cedarhd.utils;

import android.text.TextUtils;

public class DoubleUtils {

    /**
     * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
     *
     * @param value
     * @return Sting
     */
    public static String formatFloatNumber(double value) {
        if (value != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat(",###,###.00");
            return df.format(value);
        } else {
            return "0.00";
        }

    }

    public static String formatFloatNumber(Double value) {
        if (value != null) {
            if (value.doubleValue() != 0.00) {
                java.text.DecimalFormat df = new java.text.DecimalFormat(",###,###.00");
                return df.format(value.doubleValue());
            } else {
                return "0.00";
            }
        }
        return "";
    }

    /**
     * 格式化double类型的数据
     *
     * @param value
     * @return 整数字符串
     */
    public static String formatFloatNumberString(double value) {
        if (value != 0.00) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("########");
            return df.format(value);
        } else {
            return "0";
        }

    }


    /**
     * 去掉数字字符串中的逗号
     *
     * @param str
     * @return
     */
    public static String formatDoubleString(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.contains(",")) {
                str = str.replaceAll(",", "");
            }
        }
        return str;
    }
}
