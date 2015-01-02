package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * 这是一个操作流的工具包，提供一些方法
 * @author rong
 */
public class StreamUtils {
	/**
	 * 把字节流转换成字符
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	public static String parseInputStream(InputStream is) throws IOException{
		InputStreamReader isr=new InputStreamReader(is);
		BufferedReader bufr=new BufferedReader(isr);
		//内存流，写入读取的数据
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
