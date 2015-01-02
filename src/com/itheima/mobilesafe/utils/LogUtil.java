package com.itheima.mobilesafe.utils;

import android.util.Log;

public class LogUtil {

	public static final boolean mode = true;
	
	public static void d(Object obj,String msg){
		if(mode){
			Log.d(obj.getClass().getSimpleName(), msg);
		}
	}
}
