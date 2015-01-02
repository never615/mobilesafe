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
	 * ϵͳ����
	 */
	@ViewInject(R.id.sv_setting_update)
	private SettingView sv_setting_update;

	/**
	 * ������
	 */
	@ViewInject(R.id.sv_setting_callsmssafe)
	private SettingView sv_setting_callsmssafe;
	private Intent callSmsSafeIntent;
	
	/**
	 * ���������
	 */
	@ViewInject(R.id.sv_setting_location)
	private SettingView sv_setting_location;
	private Intent showLocationIntent;
	
	/**
	 * ������
	 */
	@ViewInject(R.id.sv_setting_lockapp)
	private SettingView sv_setting_lockapp;
	private Intent watchDogIntent;

	private SharedPreferences sp;
	/**
	 * ��������ʾ
	 */
	@ViewInject(R.id.tv_title_style)
	private TextView tv_title_style;
	private static final String[] items ={"��͸��","������","��ʿ��","������","ƻ����"};

	

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

		// �����Ƿ����Զ����µļ����¼�
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
		 * ������
		 */
		// �����Ƿ������������صļ����¼�
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		sv_setting_callsmssafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sv_setting_callsmssafe.isChecked()) {
					sv_setting_callsmssafe.setChecked(false);
					// �رպ��������ط���
					stopService(callSmsSafeIntent);
				} else {
					sv_setting_callsmssafe.setChecked(true);
					// �������������ط���
					startService(callSmsSafeIntent);
				}
			}
		});
		
		/**
		 *������ 
		 */
		watchDogIntent=new Intent(this,WatchDogService.class);
		sv_setting_lockapp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sv_setting_lockapp.isChecked()) {
					sv_setting_lockapp.setChecked(false);
					// �رտ��Ź�����
					stopService(watchDogIntent);
				} else {
					sv_setting_lockapp.setChecked(true);
					// �������Ź�����
					startService(watchDogIntent);
				}
			}
		});
		
		
		/**
		 * ������
		 */
		showLocationIntent = new Intent(this, ShowLocationService.class);
		sv_setting_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (sv_setting_location.isChecked()) {
					sv_setting_location.setChecked(false);
					// �رշ���
					stopService(showLocationIntent);
				} else {
					sv_setting_location.setChecked(true);
					// ��������
					startService(showLocationIntent);
				}
			}
		});
	}
	@Override
	protected void onStart() {
		super.onStart();
		
		/**
		 * ������������״̬����
		 */
		boolean running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.CallSmsSafeService");
		if(running){
			sv_setting_callsmssafe.setChecked(true);
		}else{
			sv_setting_callsmssafe.setChecked(false);
		}
		
		/**
		 * ���Ź��������
		 */
		running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.WatchDogService");
		if(running){
			sv_setting_lockapp.setChecked(true);
		}else{
			sv_setting_lockapp.setChecked(false);
		}
		
		/**
		 * �����ط�����״̬����
		 */
		running=SystemInfoUtils.isServiceRunning(this, "com.itheima.mobilesafe.services.ShowLocationService");
		if(running){
			sv_setting_location.setChecked(true);
		}else{
			sv_setting_location.setChecked(false);
		}
		
		/**
		 * ��������ʾ������û���
		 */
		tv_title_style.setText(items[sp.getInt("which", 0)]);
	}
	
	/**
	 * �ı������ ���
	 * @param view
	 */
	public void changeBgStyle(View view){
		AlertDialog.Builder builder=new Builder(this);
		builder.setIcon(R.drawable.main_icon_36);
		builder.setSingleChoiceItems(items, sp.getInt("which",0),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//����ѡ��sp��
				Editor editor=sp.edit();
				editor.putInt("which", which);
				editor.commit();
				tv_title_style.setText(items[which]);
				dialog.dismiss();
			}
		});
		builder.setTitle("��������ʾ���");
		builder.show();
	}
	
	/**
	 * ���²������ݿ⡣
	 * @param view
	 */
	public void updateVirusDB(View view){
		//1.��ȡ���ز������ݿ�İ汾��
		//��ȡsubcnt
		
		//2.������ȡ��������������Ϣ��
		//serversubcnt
		
		//3.�ȶ� serversubcnt �� ����subcnt�Ĵ�С��
		
		//4.�����������serversubcnt �� ����subcntҪ�� ˵�����µĲ�����Ϣ��Ҫ���¡�
		
		
		//5.���㱾�ذ汾�ͷ������汾�Ĳ����С�� �������5���汾�� 
		
		//6.��������汾�Ų����������� ��ȡ������Ϣ
		//ѹ�����sql��䡣
		
		//7.��ѹsql��䣬������ݵ����ݵ��������ݿ⡣ 
	}
}
