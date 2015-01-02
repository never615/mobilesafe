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
		Log.i(TAG, "�ֻ����������");
		
		SharedPreferences sp=context.getSharedPreferences("config", Context.MODE_PRIVATE);
		//��ȡ��������״̬�������Ļ��ͼ���ֻ�sim���Ƿ����˱仯
		boolean protecting=sp.getBoolean("protecting", false);
		if(protecting){
			//�õ��ֻ�������sim�����к�
			String bindsim=sp.getString("sim", "");
			
			//�õ������ֻ������sim�����к�
			TelephonyManager tm=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String realsim=tm.getSimSerialNumber()+"aaa";
			
			//�ж��Ƿ����˱仯�������˱仯�ͷ����Ÿ���ȫ����
			if(bindsim.equals(realsim)){
				Log.i(TAG, "sim��û�䣬һ������");
			}else{
				//sim�仯�����Ͷ��Ÿ���ȫ����
				Log.i(TAG, "sim�������仯");
				String safenumber=sp.getString("safenumber", "");
				SmsManager smsManager=SmsManager.getDefault();
				smsManager.sendTextMessage(safenumber, null, "sim changed", null, null);
			}
			
		}else{
			Log.i(TAG, "��������û�п���");
		}
		
		//������������������
		Intent callSmsSafeIntent = new Intent(context, CallSmsSafeService.class);
		context.startService(callSmsSafeIntent);
	}

}
