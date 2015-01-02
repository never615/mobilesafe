package com.itheima.mobilesafe.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.itheima.mobilesafe.services.CallSmsSafeService;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "BootCompleteReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "手机启动完毕了");
		
		SharedPreferences sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		//获取防盗保护状态，开启的话就检查手机sim卡是否发生了变化
		boolean protecting=sp.getBoolean("protecting", false);
		if(protecting){
			//拿到手机里面存的sim卡序列号
			String bindsim=sp.getString("sim", "");
			
			//拿到现在手机里面的sim卡序列号
			TelephonyManager tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String realsim=tm.getSimSerialNumber()+"aaa";
			
			//判断是否发生了变化，发生了变化就发短信给安全号码
			if(bindsim.equals(realsim)){
				Log.i(TAG, "sim卡没变，一切正常");
			}else{
				//sim变化，发送短信给安全号码
				Log.i(TAG, "sim卡发生变化");
				String safenumber=sp.getString("safenumber", "");
				SmsManager smsManager=SmsManager.getDefault();
				smsManager.sendTextMessage(safenumber, null, "sim changed", null, null);
			}
			
		}else{
			Log.i(TAG, "防盗保护没有开启");
		}
		
		//开机启动黑名单拦截
		Intent callSmsSafeIntent = new Intent(context, CallSmsSafeService.class);
		context.startService(callSmsSafeIntent);
	}

}
