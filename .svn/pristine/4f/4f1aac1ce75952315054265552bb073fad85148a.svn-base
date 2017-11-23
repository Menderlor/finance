package com.cedarhd.utils;

import org.springframework.util.Base64Utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 *
 */
public class DESUtil {
    public final static String KEY_SIGN = "!@#$09&*";
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    public static byte iv1[] = {0x12, 0x34, 0x56, 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};

    public static void main(String[] args) {
        // 密码，长度要是8的倍数
        String password = "12345678";
        // LogUtils.i("Des", password);
        String str = "北京市海淀区人力资源和社会保障局海淀区西四环北路65号";
        String result = encode(password, str);
        System.out.println("加密前：" + new String(str));
        System.out.println("加密后：" + new String(result));
        String result2 = decode(password, result);

        System.out.println("解密后：" + new String(result2));
    }

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws InvalidAlgorithmParameterException
     * @throws Exception
     */
    public static String encode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
            byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));
            return byte2String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static String decode(String key, String data) {
        if (data == null)
            return null;
        try {
            byte[] bytes = Base64Utils.decode(data);
            DESKeySpec dks = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec(iv1);
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    private static String byte2String(byte[] b) {
        // StringBuilder hs = new StringBuilder();
        // String stmp;
        // for (int n = 0; b != null && n < b.length; n++) {
        // stmp = Integer.toHexString(b[n] & 0XFF);
        // if (stmp.length() == 1)
        // hs.append('0');
        // hs.append(stmp);
        // }

        // return hs.toString().toUpperCase(Locale.CHINA);
        return Base64Utils.encodeToString(b);
    }

    private static String byteToString(byte[] b) {
        // StringBuilder hs = new StringBuilder();
        // String stmp;
        // for (int n = 0; b != null && n < b.length; n++) {
        // stmp = Integer.toHexString(b[n] & 0XFF);
        // if (stmp.length() == 1)
        // hs.append('0');
        // hs.append(stmp);
        // }

        // return hs.toString().toUpperCase(Locale.CHINA);
        return Base64Utils.decode(b).toString();
    }

    /**
     * 二进制转化成16进制
     *
     * @param b
     * @return
     */
    private static byte[] byte2hex(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

}