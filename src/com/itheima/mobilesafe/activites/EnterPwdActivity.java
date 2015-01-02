package com.itheima.mobilesafe.activites;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EnterPwdActivity extends Activity {

	@ViewInject(R.id.et_password)
	private EditText et_password;
	@ViewInject(R.id.iv_lock_appicon)
	private ImageView iv_lock_appicon;
	@ViewInject(R.id.tv_lock_appname)
	private TextView tv_lock_appname;

	private String packname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_enter_pwd);
		ViewUtils.inject(this);
		packname = getIntent().getStringExtra("packname");
		

		PackageManager pm = getPackageManager();
		try {
			iv_lock_appicon.setImageDrawable(pm.getApplicationInfo(packname, 0)
					.loadIcon(pm));
			tv_lock_appname.setText(pm.getApplicationInfo(packname, 0)
					.loadLabel(pm));

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ����Ӧ�õ���¼�
	 * 
	 * @param view
	 */
	public void enter_password(View view) {
		String pwd = et_password.getText().toString().trim();
		if ("123".equals(pwd)) {
			System.out.println("����Ӧ��_packname:"+packname);
			// ������ȷ����Ӧ�ý���
			// ֪ͨ���Ź���ͣ������ͨ�������Զ���Ĺ㲥��Ϣ������
			Intent intent=new Intent();
			intent.setAction("com.itheima.mobilesafe.stopprotect");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);
			
			finish();
			

		} else {
			// ���벻��ȷ
			Toast.makeText(this, "���벻��ȷ", 0).show();
			Animation aa = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_password.startAnimation(aa);
		}
	}

	/**
	 * ������ذ�ť �ص�����
	 */
	@Override
	public void onBackPressed() {
		//super.onBackPressed();  ���̳и����Լ�ʵ��
		// �����档
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		finish();// �رյ���������Ľ��档��Ȼ�Ļ��п��ܻ���ֺü��������������
	}

}
