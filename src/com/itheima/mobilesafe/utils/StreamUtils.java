package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * ����һ���������Ĺ��߰����ṩһЩ����
 * @author rong
 */
public class StreamUtils {
	/**
	 * ���ֽ���ת�����ַ�
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public static String parseInputStream(InputStream is) throws IOException{
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader bufr=new BufferedReader(isr);
		//�ڴ�����д���ȡ������
		StringWriter sw=new StringWriter();
		String line=null;
		while((line=bufr.readLine())!=null){
			sw.write(line);
		}
		sw.close();
		bufr.close();
		return sw.toString();
	}
}
