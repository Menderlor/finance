package com.cedarhd.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

public class StrUtils {
	/** 判断字符串是否为空 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null || str.replaceAll(" ", "").equals("")) {
			return true;
		}
		return false;
	}

	/** 去除标点符号 */
	public static String deleteSign(String str) {
		if (!TextUtils.isEmpty(str)) {
			str = str.trim();
			if (str.contains(";")) {
				str = str.replaceAll(";", "");
			}
			if (str.contains("'")) {
				str = str.replaceAll("'", "");
			}
			if (str.contains(",")) {
				str = str.replaceAll(",", "");
			}
		}
		return str;
	}

	/** 移除开头结尾的[] */
	public static String removeRex(String json) {
		if (!TextUtils.isEmpty(json) && json.startsWith("[")
				&& json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }
        return json;
    }

    /**
     * 移除开头结尾的[]
     */
    public static String removeRexYinHao(String json) {
        if (!TextUtils.isEmpty(json) && json.startsWith("\"")
                && json.endsWith("\"")) {
            json = json.substring(1, json.length() - 1);
		}
		return json;
	}

	/**
	 * 移除开头结尾的指定字符
	 * 
	 * @param json
	 *            目标字符串
	 * @param removeStartAndEndChar
	 *            要移除开头结尾的指定字符
	 * @return
	 */
	public static String removeRex(String json, String removeStartAndEndChar) {
		if (!TextUtils.isEmpty(json)) {
			if (json.endsWith(removeStartAndEndChar)) {
				json = json.substring(0, json.length() - 1);
			}

			if (json.startsWith(removeStartAndEndChar)) {
				json = json.substring(1, json.length());
			}
		}
		return json;
	}

	/** 如果字符串为空，则转为""的形式,避免出现null */
	public static String pareseNull(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		return str;
	}
	
	
	/**
	 * 将字符串转为UTF-8格式
	 * @param str
	 * @return
	 */
	public static String convertUTF(String str) {
		try {
			return new String(str.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
}
