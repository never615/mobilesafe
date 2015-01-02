package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_setup3_phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity3_setup);
		et_setup3_phone = (EditText) findViewById(R.id.et_setup_phone);
		
		//���԰�ȫ����
		String safenumber=sp.getString("safenumber", null);
		et_setup3_phone.setText(safenumber);
		

	}

	// ��ת����ϵ��ѡ�����
	public void selectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, 0);
	}

	// �õ���ϵ��ѡ�����Ľ��
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra("phone");
			et_setup3_phone.setText(phone);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void showNext() {
		// ����һ��֮ǰҪ���ж�  ���氲ȫ����
		String phone = et_setup3_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "�������ð�ȫ���룡", 0).show();
			return;
		}
		Editor editor=sp.edit();
		editor.putString("safenumber", phone);
		editor.commit();
		startActivityAndFinishSelf(Setup4Activity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(Setup2Activity.class);
	}

}
