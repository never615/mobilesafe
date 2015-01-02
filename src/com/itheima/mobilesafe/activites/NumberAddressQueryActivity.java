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

	// �ֻ��𶯷���
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_numberadress);

		ViewUtils.inject(this);
		// �õ�ϵͳ���𶯷���
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		// �Զ�����ı��仯���������������ʾ
		et_phone_number.addTextChangedListener(new TextWatcher() {
			// �ı��仯ǰ
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			// �ı��仯ʱ
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			// �ı��仯��
			@Override
			public void afterTextChanged(Editable s) {
				queryNumberAdress();
			}
		});
	}

	// ��ѯ��ť����¼�
	public void query(View view) {
		queryNumberAdress();
	}

	/**
	 * ��ѯ���������
	 */
	private void queryNumberAdress() {
		String phonenumber = et_phone_number.getText().toString().trim();
		// �ж�û����������ʱ��������
		if (TextUtils.isEmpty(phonenumber)) {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			// �Զ���Ķ���������Ч��
			/*
			 * shake.setInterpolator(new Interpolator() {
			 * 
			 * @Override public float getInterpolation(float x) { float y=x*x;
			 * return y; } });
			 */
			et_phone_number.startAnimation(shake);

			// ���ֻ��𶯵�Ч��
			// ��һ�������м������������ʱ�䣬˫������Ϣʱ�䣬�ڶ�������-1���ظ�����-1Ϊ��pattern��ָ���±꿪ʼ�ظ�
			vibrator.vibrate(new long[] { 200, 100, 200, 100 }, -1);
			Toast.makeText(this, "����Ϊ�գ�", 0).show();
		}
		String location = addressDao.getLocation(phonenumber);
		tv_address_info.setText("�����أ�" + location);
	}
}
