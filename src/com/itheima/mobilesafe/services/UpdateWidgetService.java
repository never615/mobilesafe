package com.itheima.mobilesafe.services;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.activites.AppManagerActivity;
import com.itheima.mobilesafe.receives.MyWidget;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

/**
 * 定时更新桌面插件
 * 
 * @author rong
 * 
 */
public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	protected String tag="UpdateWidgetService";

	@Override
	public void onCreate() {
		super.onCreate();
		awm = AppWidgetManager.getInstance(getApplicationContext());
		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				Log.i(tag, "更新widget");
				// 让桌面更新widget，由 另外一个进程更新UI
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				// 远程的view的描述信息，并不是一个真实的view对象，由远程的桌面应用根据描述信息把view对象创建出来
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"运行中的进程："
								+ SystemInfoUtils
										.getRunningProcessCount(getApplicationContext()));
				String availsize = Formatter.formatFileSize(
						getApplicationContext(),
						SystemInfoUtils.getAvailMem(getApplicationContext()));
				views.setTextViewText(R.id.process_memory, "可用内存：" + availsize);

				// 由另一个进程执行的动作，由桌面发出一个广播
				Intent intent = new Intent(); // 自定义广播
				intent.setAction("com.itheima.killall");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 5000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
