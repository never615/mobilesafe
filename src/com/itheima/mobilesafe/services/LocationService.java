package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

public class LocationService extends Service {
	private LocationManager lm;
	private MyListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		
		lm=(LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria=new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);   //��ȷ
		criteria.setCostAllowed(true);					//�ǹ��������翪��
		String localProvider=lm.getBestProvider(criteria, true);  //criteria ��ѯ����  trueֻ���ؿ��õ�λ���ṩ��
		listener=new MyListener();
		lm.requestLocationUpdates(localProvider, 0, 0, listener);
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener=null;
	}
	private class MyListener implements LocationListener{

		//��λ�øı��ʱ����ô˷���
		/* (non-Javadoc)
		 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
		 */
		@Override
		public void onLocationChanged(Location location) {
			StringBuffer sb=new StringBuffer();
			sb.append("Accuracy:"+location.getAccuracy()+"\n");
			sb.append("speed:"+location.getSpeed()+"\n");
			sb.append("latitude:"+location.getLatitude()+"\n");
			sb.append("Longitude:"+location.getLongitude()+"\n");
			sb.append("Altitude:"+location.getAltitude()+"\n");
			String result=sb.toString();
			
			//�õ�λ���Ժ��͸���ȫ����
			SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
			//������
		//	Log.i("safenumber", sp.getString("safenumber", ""));
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, result, null, null);
			stopSelf();
		}
		//��λ���ṩ�� ״̬�����ı��ʱ�����
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		//��λ���ṩ�� ���õ�ʱ����ô˷���
		@Override
		public void onProviderEnabled(String provider) {
		}

		//��λ���ṩ�� �����õ�ʱ����ô˷���
		@Override
		public void onProviderDisabled(String provider) {
		}
	}

}
