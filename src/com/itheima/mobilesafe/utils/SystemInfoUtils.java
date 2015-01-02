package com.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SystemInfoUtils {
	/**
	 * �ж�һ�������Ƿ�������״̬
	 * 
	 */
	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(200); // �õ��������еķ��񣬲����������õ������ֵ
		for (RunningServiceInfo info : infos) {
			String serviceClassName = info.service.getClassName(); // �����õ��ķ����õ������Ӧ������
			if (serviceClassName.equals(className)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �õ��������е��ڴ�����
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context){
		ActivityManager am=(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfo=am.getRunningAppProcesses();
		int count=runningAppProcessInfo.size();
		return count;
	}
	
	/**
	 * �õ����õ�ram
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context){
		ActivityManager am=(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo outInfo=new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		long availMem=outInfo.availMem;
		return availMem;
	}
	
	/**
	 * �õ�ϵͳ�����ڴ�
	 * @return
	 */
	public static long getTotalMem(){
		
		try {
			BufferedReader bufr=new BufferedReader(new FileReader("/proc/meminfo"));
			String totalInfo=bufr.readLine();
			StringBuffer sb=new StringBuffer();
			for(char c:totalInfo.toCharArray()){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			long byteSize=Long.parseLong(sb.toString())*1024;
			return byteSize;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
