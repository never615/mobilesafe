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
		Log.i(TAG, "��������");
		
		Object[] objs=(Object[]) intent.getExtras().get("pdus");
		
		//��ȡ��������Ա
		DevicePolicyManager dpm=(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		for(Object obj:objs){
			SmsMessage smsMessage= SmsMessage.createFromPdu((byte[])obj);
			String sender=smsMessage.getOriginatingAddress();
			String body=smsMessage.getMessageBody();
			
			if("#*location*#".equals(body)){
				Log.i(TAG, "����λ����Ϣ");
				//����λ��Ӧ��д�ڷ�����  ��Ϊ�㲥��������ʮ����û�������꣬�ᱻ��Ϊ����Ӧ�����Բ��ܽ��к�ʱ����
				Intent service=new Intent(context, LocationService.class);
				context.startService(service);
				abortBroadcast();
			}else if("#*alarm*#".equals(body)){
				Log.i(TAG,"���ű�������.");
				MediaPlayer player=	MediaPlayer.create(context,R.raw.ylzs);
				player.setVolume(1.0f, 1.0f);
				player.start();
				abortBroadcast();
			}else if("#*wipedata*#".equals(body)){
				Log.i(TAG,"Զ���������.");
				dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
				abortBroadcast();
			}else if("#*lockscreen*#".equals(body)){
				Log.i(TAG,"Զ������.");
				dpm.resetPassword("123", 0);
				dpm.lockNow();
				abortBroadcast();
			}
		}
	}

}
