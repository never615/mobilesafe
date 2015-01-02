package com.itheima.mobilesafe.activites;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.SmsUtils;
import com.itheima.mobilesafe.utils.SmsUtils.BackupSmsCallBack;
import com.itheima.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AtoolsActivity extends Activity {
	
	@ViewInject(R.id.pb_backupsms)
	private ProgressBar pb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);
	}

	public void numberAddressQuery(View view) {
		Intent intent=new Intent(this,NumberAddressQueryActivity.class);
		startActivity(intent);
	}

	/**
	 * ���ű���
	 * @param view
	 */
	public void smsBackup(View view){
		//������̫��ʱ�����ݻ�ԭ���������������Ҫ�����߳��н���
		new Thread(){
			public void run() {
				try {
					boolean result=SmsUtils.backUpSms(AtoolsActivity.this, new BackupSmsCallBack() {
						
						@Override
						public void onSmsBackup(int progress) {
							pb.setProgress(progress);
						}
						
						@Override
						public void beforeSmsBackup(int size) {
							pb.setMax(size);
						}
					});
					
					if(result){
						UIUtils.showToast(AtoolsActivity.this, "�������");
					}
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					UIUtils.showToast(AtoolsActivity.this, "�ļ�����ʧ��");
				} catch (IllegalStateException e) {
					e.printStackTrace();
					UIUtils.showToast(AtoolsActivity.this, "SD�����ú󴢴�ռ䲻��");
				} catch (IOException e) {
					e.printStackTrace();
					UIUtils.showToast(AtoolsActivity.this, "��д����");
				}
				
			};
		}.start();
		
		
	}
	/**
	 * ���Ż�ԭ
	 * @param view
	 */
	public void smsRestore(View view){
		
	}
	
	/**
	 * �������������
	 * @param view
	 */
	public void appLock(View view){
		Intent intent =new Intent(this,AppLockActivity.class);
		startActivity(intent);
	}
	
	/**
	 *���ú����ѯ
	 * @param view
	 */
	public void commonNumberQuery(View view){
		Intent intent=new Intent(this,CommonNumberActivity.class);
		startActivity(intent);
	}
}
