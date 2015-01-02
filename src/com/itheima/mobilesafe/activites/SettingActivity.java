package com.itheima.mobilesafe.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.services.CallSmsSafeService;
import com.itheima.mobilesafe.services.ShowLocationService;
import com.itheima.mobilesafe.services.WatchDogService;
import com.itheima.mobilesafe.ui.SettingView;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SettingActivity extends Activity {

	/**
	 * 系统更新
	 */
	@ViewInject(R.id.sv_setting_update)
	private SettingView sv_setting_update;

	/**
	 * 黑名单
	 */
	@ViewInject(R.id.sv_setting_callsmssafe)
	private SettingView sv_setting_callsmssafe;
	private Intent callSmsSafeIntent;
	
	/**
	 * 来电归属地
	 */
	@ViewInject(R.id.sv_setting_location)
	private SettingView sv_setting_location;
	private Intent showLocationIntent;
	
	/**
	 * 程序锁
	 */
	@ViewInject(R.id.sv_setting_lockapp)
	private SettingView sv_setting_lockapp;
	private Intent watchDogIntent;

	private SharedPreferences sp;
	/**
	 * 归属地显示
	 */
	@ViewInject(R.id.tv_title_style)
	private TextView tv_title_style;
	private static final String[] items ={"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		if (sp.getBoolean("update", true)) {
			sv_setting_update.setChecked(true);
		} else {
			sv_setting_update.setChecked(false);
		}

		// 设置是否开启自动更新的监听事件
		sv_setting_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();

				if (sv_setting_update.isChecked()) {
					sv_setting_update.setChecked(false);
					editor.putBoolean("update", false);

				} else {
					sv_setting_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});

		
		/**
		 * 黑名单
		 */
		// 设置是否开启黑名单拦截的监听事件
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		sv_setting_callsmssafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sv_setting_callsmssafe.isChecked()) {
					sv_setting_callsmssafe.setChecked(false);
					// 关闭黑名单拦截服务
					stopService(callSmsSafeIntent);
				} else {
					sv_setting_callsmssafe.setChecked(true);
					// 开启黑名单拦截服务
					startService(callSmsSafeIntent);
				}
			}
		});
		
		/**
		 *程序锁 
		 */
		watchDogIntent=new Intent(this,WatchDogService.class);
		sv_setting_lockapp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sv_setting_lockapp.isChecked()) {
					sv_setting_lockapp.setChecked(false);
					// 关闭看门狗服务
					stopService(watchDogIntent);
				} else {
					sv_setting_lockapp.setChecked(true);
					// 开启看门狗服务
					startService(watchDogIntent);
				}
			}
		});
		
		
		/**
		 * 归属地
		 */
		showLocationIntent = new Intent(this, ShowLocationService.class);
		sv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sv_setting_location.isChecked()) {
					sv_setting_location.setChecked(false);
					// 关闭服务
					stopService(showLocationIntent);
				} else {
					sv_setting_location.setChecked(true);
					// 开启服务
					startService(showLocationIntent);
				}
			}
		});
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		/**
		 * 黑名单服务开启状态回显
		 */
		boolean running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.CallSmsSafeService");
		if(running){
			sv_setting_callsmssafe.setChecked(true);
		}else{
			sv_setting_callsmssafe.setChecked(false);
		}
		
		/**
		 * 看门狗服务回显
		 */
		running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.WatchDogService");
		if(running){
			sv_setting_lockapp.setChecked(true);
		}else{
			sv_setting_lockapp.setChecked(false);
		}
		
		/**
		 * 归属地服务开启状态回显
		 */
		running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.ShowLocationService");
		if(running){
			sv_setting_location.setChecked(true);
		}else{
			sv_setting_location.setChecked(false);
		}
		
		/**
		 * 归属地显示风格设置回显
		 */
		tv_title_style.setText(items[sp.getInt("which", 0)]);
	}
	
	/**
	 * 改变归属地 风格
	 * @param view
	 */
	public void changeBgStyle(View view){
		AlertDialog.Builder builder=new Builder(this);
		builder.setIcon(R.drawable.main_icon_36);
		builder.setSingleChoiceItems(items, sp.getInt("which",0),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//保存选择到sp中
				Editor editor=sp.edit();
				editor.putInt("which", which);
				editor.commit();
				tv_title_style.setText(items[which]);
				dialog.dismiss();
			}
		});
		builder.setTitle("归属地提示风格");
		builder.show();
	}
	
	/**
	 * 更新病毒数据库。
	 * @param view
	 */
	public void updateVirusDB(View view){
		//1.获取本地病毒数据库的版本号
		//获取subcnt
		
		//2.联网获取服务器的配置信息。
		//serversubcnt
		
		//3.比对 serversubcnt 和 本地subcnt的大小。
		
		//4.如果服务器端serversubcnt 比 本地subcnt要大 说明有新的病毒信息需要更新。
		
		
		//5.计算本地版本和服务器版本的差异大小， 假设相差5个版本号 
		
		//6.带着这个版本号差别请求服务器 获取更新信息
		//压缩后的sql语句。
		
		//7.解压sql语句，添加数据到数据到本地数据库。 
	}
}
