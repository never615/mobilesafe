package com.itheima.mobilesafe.receives;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.itheima.mobilesafe.services.UpdateWidgetService;

/**
 * 特殊的广播接受者
 * 
 * @author Administrator 1.写一个类 继承AppWidgetProvider
 * 
 */
public class MyWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		// 开启服务 定期的更新widget
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onEnabled(context);
	}

	//防止服务被意外结束，定期开启 ，
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
    		int[] appWidgetIds) {
    	super.onUpdate(context, appWidgetManager, appWidgetIds);
    	
    	Intent i = new Intent(context, UpdateWidgetService.class);
		context.startService(i);
		super.onEnabled(context);
    }
	
	@Override
	public void onDisabled(Context context) {
		// 停止服务 不再去更新widget
		Intent i = new Intent(context, UpdateWidgetService.class);
		context.stopService(i);
		super.onDisabled(context);
	}
}
