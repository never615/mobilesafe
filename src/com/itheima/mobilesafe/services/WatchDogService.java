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

		//ע��һ�����ݹ۲��ߣ������ݿ�仯��ʱ�����»�ȡ�������İ���
		oberver=new AppLockObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://com.itheima.mobilesafe.applock"), true, oberver);
		
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// ֻ���ڷ����һ�δ�����ʱ�� �Ż��ȡ���ݡ� �����ڷ����������У�������ĳ�������������Ϣ ��Ч�ˡ�
		dao = new AppLockDao(getApplicationContext());
		lockedPackNames = dao.findAll();
		intent = new Intent(WatchDogService.this, EnterPwdActivity.class);
		tempStopProtectPacknames = new ArrayList<String>();

		// ע��һ���Զ���Ĺ㲥������
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
	 * ���ݹ۲��� �����������ݷ����仯��ʱ�򴥷�
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
			Log.i("ApplockObserver","���ݿ�����ݱ仯�ˣ����»�ȡ �������İ���");
			//��⵽���ݿ����ݷ����仯,�����»�ȡ �������İ���
			lockedPackNames = dao.findAll();
		}
	}
	
	/**
	 * ���Ź��Ĺ㲥�����ߣ�ʵ����ͣ������������֮������ͣ�ı���
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
				// ֹͣ���Ź�
				flag = false;
			} else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
				// �������Ź�
				if (flag == false) {
					startWatchDog();
				}
			}
		}
	}

	/**
	 * ��ͣ��ˢ�¼�������ջ
	 */
	private void startWatchDog() {

		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					// ��������ջ����������ʹ�õĴ򿪵�����ջ�ڼ��ϵ���ǰ��
					infos = am.getRunningTasks(1);
					// ������õ�����ջ
					RunningTaskInfo info = infos.get(0);
					String packName = info.topActivity.getPackageName();
					// System.out.println("packName:"+packName);

					// Ȼ���ж����Ӧ���Ƿ񱻱������������Ļ��ͽ��������������
					// if (dao.find(packname)) {//��ѯ���ݿ� Ч�ʵ� ���ڴ濪����
					if (lockedPackNames.contains(packName)) { // ��ѯ�ڴ漯�� �ٶȿ� ����С
						// ��Ҫ����
						// �������������
						if (tempStopProtectPacknames.contains(packName)) {
							// ��ͣ����
						} else {
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packname", packName);
							startActivity(intent);
						}

					} else {
						// ����Ҫ������ʲô������
					}

					// ˯һ�ᣬ��Ϊ̫����
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
