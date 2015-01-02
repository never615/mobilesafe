package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Setup2Activity extends BaseSetupActivity {
	private TelephonyManager tm;
	private ImageView iv_setup_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity2_setup);
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);   //��ʼ���绰�������
		iv_setup_status=(ImageView) findViewById(R.id.iv_setup2_status);
		
		//��ʼ����ʱ��Ҫ��ȡsim��Ϣ���ж��ǹ��󶨹������л���
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){
			iv_setup_status.setImageResource(R.drawable.unlock);
		}else{
			iv_setup_status.setImageResource(R.drawable.lock);
		}
	}

	//�󶨽��sim��������¼�    �ǵü�Ȩ�ޣ���ȡ�ֻ�״̬��Ϣ
	public void bindUnbindSim(View view){
		//�ж��Ƿ�󶨹�
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){   	//û�а󶨹������а󶨣��洢��Ϣ�� �ı�ͼƬ
			String simserial=tm.getSimSerialNumber();  //�õ��ֻ���ϵ�к�
			Editor editor=sp.edit();
			editor.putString("sim", simserial);
			editor.commit();
			
			Toast.makeText(this, "sim���󶨳ɹ���", 0).show();
			iv_setup_status.setImageResource(R.drawable.lock);
		}else{			//�󶨹���������н�󣬴洢��Ϣ��Ϊnull���ı�ͼƬΪ����
			Editor editor=sp.edit();
			editor.putString("sim", null);
			editor.commit();
			
			Toast.makeText(this, "sim�����ɹ���", 0).show();
			iv_setup_status.setImageResource(R.drawable.unlock);
		}
	}
	
	@Override
	public void showNext() {
		//�����һ����ʱ��Ҫ����һ���жϣ�Ҫ��û������sim���󶨣�����û������
		//����Ҫ���ж�
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){
			Toast.makeText(this, "û�а�sim�������Ȱ󶨣�", 0).show();
			return;
		}
		startActivityAndFinishSelf(Setup3Activity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(Setup1Activity.class);
	}
}
