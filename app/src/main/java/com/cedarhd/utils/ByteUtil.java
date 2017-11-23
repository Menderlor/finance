package com.cedarhd.utils;

import com.cedarhd.helpers.Global;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteUtil {

	public static String md5One() {
		if (Global.mUser == null) {
			return "0";
		}
		String uname = Global.mUser.UserName;

		MessageDigest md_uname = null;
		try {
			md_uname = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		md_uname.update(uname.getBytes());
		return byteArrayToHexString(md_uname.digest()) + Global.mUser.PassWord;
	}

	/**
	 * Md5加密工具
	 * 
	 * @param s
	 * @return
	 */
	public static String md5One(String s) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
		md.update(s.getBytes());
		// 之前的登录密码
		// return byteArrayToHexString(md.digest());
		return byteArrayToHexString(md.digest()).toUpperCase();
	}

	private static String[] HexCode = { "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "a", "b", "c", "d", "e", "f" };

	public static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return HexCode[d1] + HexCode[d2];
	}

	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result = result + byteToHexString(b[i]);
		}
		return result;
	}

}
