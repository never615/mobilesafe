package com.itheima.mobilesafe.activites;

import android.os.Bundle;
import android.view.View;

import com.itheima.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity1_setup);
	}

	@Override
	public void showNext() {
		startActivityAndFinishSelf(Setup2Activity.class);
	}

	@Override
	public void showPre() {
	}


}
