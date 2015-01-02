package com.itheima.mobilesafe.services;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.itheima.mobilesafe.activites.EnterPwdActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;

public class WatchDogService extends Service {

	private boolean flag;

	private ActivityManager am;

	private AppLockDao dao;

	private List<String> lockedPackNames;

	private Intent intent;
	private WatchDogReceiver receiver;
	private AppLockObserver oberver;

	private List<String> tempStopProtectPacknames;
	private List<RunningTaskInfo> infos;

	@Override
	public void onCreate() {
		super.onCreate();

		//注册一个内容观察者，在数据库变化的时候，重新获取被锁定的包名
		oberver=new AppLockObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://com.itheima.mobilesafe.applock"), true, oberver);
		
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 只有在服务第一次创建的时候 才会获取数据。 所有在服务开启过程中，如果更改程序锁的配置信息 无效了。
		dao = new AppLockDao(getApplicationContext());
		lockedPackNames = dao.findAll();
		intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
		tempStopProtectPacknames = new ArrayList<String>();

		// 注册一个自定义的广播接受者
		receiver = new WatchDogReceiver();
		IntentFilter filter = new IntentFilter(
				"com.itheima.mobilesafe.stopprotect");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(receiver, filter);

		startWatchDog();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
	}

	/**
	 * 内容观察者 当被监视内容发生变化的时候触发
	 * @author rong
	 *
	 */
	private class AppLockObserver extends ContentObserver{

		public AppLockObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("ApplockObserver","数据库的内容变化了，重新获取 被锁定的包名");
			//监测到数据库内容发生变化,就重新获取 被锁定的包名
			lockedPackNames = dao.findAll();
		}
	}
	
	/**
	 * 看门狗的广播接收者，实现暂停保护，和锁定之后开启暂停的保护
	 * 
	 * @author rong
	 * 
	 */
	private class WatchDogReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.itheima.mobilesafe.stopprotect".equals(intent.getAction())) {
				tempStopProtectPacknames.add(intent.getStringExtra("packname"));
				// System.out.println("tempStopProtectPackname"+tempStopProtectPackname);
			} else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
				tempStopProtectPacknames = null;
				tempStopProtectPacknames=new ArrayList<String>();
				// 停止看门狗
				flag = false;
			} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				// 开启看门狗
				if (flag == false) {
					startWatchDog();
				}
			}
		}
	}

	/**
	 * 不停的刷新监视任务栈
	 */
	private void startWatchDog() {

		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					// 监视任务栈的情况，最近使用的打开的任务栈在集合的最前面
					infos = am.getRunningTasks(1);
					// 最近是用的任务栈
					RunningTaskInfo info = infos.get(0);
					String packName = info.topActivity.getPackageName();
					// System.out.println("packName:"+packName);

					// 然后判断这个应用是否被保护，被保护的话就进入输入密码界面
					// if (dao.find(packname)) {//查询数据库 效率低 ，内存开销大
					if (lockedPackNames.contains(packName)) { // 查询内存集合 速度快 开销小
						// 需要保护
						// 打开输入密码界面
						if (tempStopProtectPacknames.contains(packName)) {
							// 暂停保护
						} else {
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packname", packName);
							startActivity(intent);
						}

					} else {
						// 不需要保护，什么都不做
					}

					// 睡一会，因为太快了
					SystemClock.sleep(50);

				}
			};
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
