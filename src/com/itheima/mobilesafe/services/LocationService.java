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
		criteria.setAccuracy(Criteria.ACCURACY_FINE);   //精确
		criteria.setCostAllowed(true);					//是够允许网络开销
		String localProvider=lm.getBestProvider(criteria, true);  //criteria 查询条件  true只返回可用的位置提供者
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

		//当位置改变的时候调用此方法
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
			
			//拿到位置以后发送给安全号码
			SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
			//发短信
		//	Log.i("safenumber", sp.getString("safenumber", ""));
			SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""), null, result, null, null);
			stopSelf();
		}
		//当位置提供者 状态发生改变的时候调用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		//当位置提供者 可用的时候调用此方法
		@Override
		public void onProviderEnabled(String provider) {
		}

		//当位置提供者 不可用的时候调用此方法
		@Override
		public void onProviderDisabled(String provider) {
		}
	}

}
