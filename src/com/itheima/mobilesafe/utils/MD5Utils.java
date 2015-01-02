package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	/**
	 * ��ȡĳ��·���ļ���MD5ֵ
	 * 
	 * @param path
	 * @return
	 */
	public static String getMD5(String path) {
		/**
		 * ���ڷ���digest.digest(byte[]); �ȼ��� digest.update(byte[]);
		 * digest.digest();
		 */
		try {
			// ��ȡһ������ժҪ��
			MessageDigest digest = MessageDigest.getInstance("MD5");
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			// ѭ���Ķ�ȡ�ļ���ÿһ��byte����ȡÿ��byte������ǩ��
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			fis.close();
			StringBuffer sb = new StringBuffer();
			byte[] result = digest.digest();
			for (byte b : result) {
				// ��ȡ�Ͱ�λ
				int number = b & 0xff; // byteתint����λ���㡣
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
	 * MD5�����㷨
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
				int number = b & 0xff; // byteתint����λ���㡣
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
