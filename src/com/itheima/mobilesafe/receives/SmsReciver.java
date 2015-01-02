package com.itheima.mobilesafe.receives;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.services.LocationService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore.Audio.Media;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReciver extends BroadcastReceiver {

	private static final String TAG = "SmsReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "短信来了");
		
		Object[] objs=(Object[]) intent.getExtras().get("pdus");
		
		//获取超级管理员
		DevicePolicyManager dpm=(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		for(Object obj:objs){
			SmsMessage smsMessage= SmsMessage.createFromPdu((byte[])obj);
			String sender=smsMessage.getOriginatingAddress();
			String body=smsMessage.getMessageBody();
			
			if("#*location*#".equals(body)){
				Log.i(TAG, "返回位置信息");
				//返回位置应该写在服务中  因为广播接收者在十秒内没有运行完，会被认为无响应，所以不能进行耗时操作
				Intent service=new Intent(context, LocationService.class);
				context.startService(service);
				abortBroadcast();
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"播放报警音乐.");
				MediaPlayer player=	MediaPlayer.create(context,R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){
				Log.i(TAG,"远程清除数据.");
				dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"远程锁屏.");
				dpm.resetPassword("123", 0);
				dpm.lockNow();
				abortBroadcast();
			}
		}
	}

}
