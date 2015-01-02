package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.NumberAddressDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {

	@ViewInject(R.id.et_phone_number)
	private EditText et_phone_number;
	@ViewInject(R.id.tv_address_info)
	private TextView tv_address_info;

	private NumberAddressDao addressDao;

	// 手机震动服务
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numberadress);

		ViewUtils.inject(this);
		// 得到系统的震动服务
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// 自动监测文本变化，做号码归属地显示
		et_phone_number.addTextChangedListener(new TextWatcher() {
			// 文本变化前
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			// 文本变化时
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			// 文本变化后
			@Override
			public void afterTextChanged(Editable s) {
				queryNumberAdress();
			}
		});
	}

	// 查询按钮点击事件
	public void query(View view) {
		queryNumberAdress();
	}

	/**
	 * 查询号码归属地
	 */
	private void queryNumberAdress() {
		String phonenumber = et_phone_number.getText().toString().trim();
		// 判断没有输入号码的时候做提醒
		if (TextUtils.isEmpty(phonenumber)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			// 自定义的动画插入器效果
			/*
			 * shake.setInterpolator(new Interpolator() {
			 * 
			 * @Override public float getInterpolation(float x) { float y=x*x;
			 * return y; } });
			 */
			et_phone_number.startAnimation(shake);

			// 做手机震动的效果
			// 第一个参数中间隔，单数是震动时间，双数是休息时间，第二个参数-1不重复，非-1为从pattern的指定下标开始重复
			vibrator.vibrate(new long[] { 200, 100, 200, 100 }, -1);
			Toast.makeText(this, "号码为空！", 0).show();
		}
		String location = addressDao.getLocation(phonenumber);
		tv_address_info.setText("归属地：" + location);
	}
}
