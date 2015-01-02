package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	/**
	 * 获取某个路径文件的MD5值
	 * 
	 * @param path
	 * @return
	 */
	public static String getMD5(String path) {
		/**
		 * 对于方法digest.digest(byte[]); 等价于 digest.update(byte[]);
		 * digest.digest();
		 */
		try {
			// 获取一个数字摘要器
			MessageDigest digest = MessageDigest.getInstance("MD5");
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			// 循环的读取文件的每一个byte，获取每个byte的数字签名
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			fis.close();
			StringBuffer sb = new StringBuffer();
			byte[] result = digest.digest();
			for (byte b : result) {
				// 获取低八位
				int number = b & 0xff; // byte转int，高位置零。
				String hex = Integer.toHexString(number);
				if (hex.length() == 1) {
					sb.append("0" + hex);
				} else {
					sb.append(hex);
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * MD5加密算法
	 * 
	 * @param text
	 * @return
	 */
	public static String encode(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(text.getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				int number = b & 0xff; // byte转int，高位置零。
				String hex = Integer.toHexString(number);
				if (hex.length() == 1) {
					sb.append("0" + hex);
				} else {
					sb.append(hex);
				}
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			// can't reach;
			return "";
		}
	}
}
