package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_setup4_protect;
	private TextView tv_setup4_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity4_setup);
		cb_setup4_protect = (CheckBox) findViewById(R.id.cb_setup4_protect);
		tv_setup4_status = (TextView) findViewById(R.id.tv_setup4_status);

		// дһ������ѡ���״̬�ı���¼�
		cb_setup4_protect
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							tv_setup4_status.setText("���������Ѿ�����");
						} else {
							tv_setup4_status.setText("��������û�п���");
						}
						Editor editor = sp.edit();
						editor.putBoolean("protecting", isChecked);
						editor.commit();
					}
				});

		// �Ƿ���������״̬����
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			cb_setup4_protect.setChecked(true);
			tv_setup4_status.setText("���������Ѿ�����");
		} else {
			cb_setup4_protect.setChecked(false);
			tv_setup4_status.setText("��������û�п���");
		}
	}

	@Override
	public void showNext() {
		// дһ��������Ϣ
		Editor editor = sp.edit();
		editor.putBoolean("finishsetup", true);
		editor.commit();
		startActivityAndFinishSelf(LostFindActivity.class);
	}

	@Override
	public void showPre() {
		startActivityAndFinishSelf(Setup3Activity.class);
	}
}
