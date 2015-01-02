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
		tm=(TelephonyManager) getSystemService(TELEPHONY_SERVICE);   //初始化电话管理服务
		iv_setup_status=(ImageView) findViewById(R.id.iv_setup2_status);
		
		//初始化的时候要读取sim信息，判断是够绑定过，进行回显
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){
			iv_setup_status.setImageResource(R.drawable.unlock);
		}else{
			iv_setup_status.setImageResource(R.drawable.lock);
		}
	}

	//绑定解绑sim卡，点击事件    记得加权限，读取手机状态信息
	public void bindUnbindSim(View view){
		//判断是否绑定过
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){   	//没有绑定过，进行绑定，存储信息， 改变图片
			String simserial=tm.getSimSerialNumber();  //得到手机的系列号
			Editor editor=sp.edit();
			editor.putString("sim", simserial);
			editor.commit();
			
			Toast.makeText(this, "sim卡绑定成功！", 0).show();
			iv_setup_status.setImageResource(R.drawable.lock);
		}else{			//绑定过，点击进行解绑，存储信息改为null，改变图片为解锁
			Editor editor=sp.edit();
			editor.putString("sim", null);
			editor.commit();
			
			Toast.makeText(this, "sim卡解绑成功！", 0).show();
			iv_setup_status.setImageResource(R.drawable.unlock);
		}
	}
	
	@Override
	public void showNext() {
		//点击下一步的时候要进行一个判断，要是没有设置sim卡绑定，提醒没有设置
		//所以要做判断
		String saveSim=sp.getString("sim", null);
		if(TextUtils.isEmpty(saveSim)){
			Toast.makeText(this, "没有绑定sim卡，请先绑定！", 0).show();
			return;
		}
		startActivityAndFinishSelf(Setup3Activity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(Setup1Activity.class);
	}
}
