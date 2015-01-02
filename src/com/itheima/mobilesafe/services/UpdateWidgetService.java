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
 * ��ʱ����������
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
				Log.i(tag, "����widget");
				// ���������widget���� ����һ�����̸���UI
				ComponentName provider = new ComponentName(
						getApplicationContext(), MyWidget.class);
				// Զ�̵�view��������Ϣ��������һ����ʵ��view������Զ�̵�����Ӧ�ø���������Ϣ��view���󴴽�����
				RemoteViews views = new RemoteViews(getPackageName(),
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"�����еĽ��̣�"
								+ SystemInfoUtils
										.getRunningProcessCount(getApplicationContext()));
				String availsize = Formatter.formatFileSize(
						getApplicationContext(),
						SystemInfoUtils.getAvailMem(getApplicationContext()));
				views.setTextViewText(R.id.process_memory, "�����ڴ棺" + availsize);

				// ����һ������ִ�еĶ����������淢��һ���㲥
				Intent intent = new Intent(); // �Զ���㲥
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
