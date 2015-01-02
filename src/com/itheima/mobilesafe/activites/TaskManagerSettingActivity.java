package com.itheima.mobilesafe.activites;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.services.AutoKillService;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TaskManagerSettingActivity extends Activity{
	@ViewInject(R.id.cb_lock_autokill)
	private CheckBox cb_lock_autokill;
	@ViewInject(R.id.cb_show_system)
	private CheckBox cb_show_system;
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager_setting);
		ViewUtils.inject(this);
		
		init();
	}

	private void init() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));
		
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("showsystem", isChecked);
				editor.commit();
			}
		});
		
		final Intent intent=new Intent();
		intent.setClass(this, AutoKillService.class);
		
		cb_lock_autokill.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(cb_lock_autokill.isChecked()){
					startService(intent);
				}else{
					stopService(intent);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if(SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.AutoKillService")){
			cb_lock_autokill .setChecked(true);
		}else{
			cb_lock_autokill .setChecked(false);
		}
	}
	
}
