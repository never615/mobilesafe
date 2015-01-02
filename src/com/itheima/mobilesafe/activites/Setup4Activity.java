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

		// 写一个监听选择框状态改变的事件
		cb_setup4_protect
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							tv_setup4_status.setText("防盗保护已经开启");
						} else {
							tv_setup4_status.setText("防盗保护没有开启");
						}
						Editor editor = sp.edit();
						editor.putBoolean("protecting", isChecked);
						editor.commit();
					}
				});

		// 是否开启保护的状态回显
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			cb_setup4_protect.setChecked(true);
			tv_setup4_status.setText("防盗保护已经开启");
		} else {
			cb_setup4_protect.setChecked(false);
			tv_setup4_status.setText("防盗保护没有开启");
		}
	}

	@Override
	public void showNext() {
		// 写一个配置信息
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
