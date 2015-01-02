package com.itheima.mobilesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AutoKillService extends Service {

	//private Timer timer;
	//private TimerTask task;
	private LockScreenReceiver receiver;
	private class LockScreenReceiver  extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ActivityManager am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
			for(RunningAppProcessInfo info:am.getRunningAppProcesses()){
				String packName=info.processName;
				am.killBackgroundProcesses(packName);
			}
		}
		
	}
	@Override
	public void onCreate() {
		super.onCreate();
		receiver=new LockScreenReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		/*timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				//自动清理内存
			}
		};
		timer.schedule(task, 0, 1000*60*60*2);
		
		CountDownTimer countDownTimer =  new CountDownTimer(1000*60*60*2, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				
			}
			
			@Override
			public void onFinish() {
//				countDownTimer.start();
			}
		};
		countDownTimer.start();*/
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	   unregisterReceiver(receiver);
	   receiver=null;
	}

}
