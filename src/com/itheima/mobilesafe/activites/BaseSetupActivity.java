package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.AvoidXfermode;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * ������������࣬���ĸ��򵼽���̳���
 * 
 * ����һ����һ���ķ���д����� ������sharedpreferences�Ĵ���д�����
 * 
 * 
 * @author rong
 * 
 */
public abstract class BaseSetupActivity extends Activity {
	public SharedPreferences sp;
	// ����һ������ʶ����
	public GestureDetector mGestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		// ��ʼ������ʶ����
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// e1 ������ָ��һ�δ�����Ļ���¼�
					// e2 ������ָ�뿪��Ļһ˲����¼�
					// velocityX ˮƽ������ٶ� ��λ pix/s
					// velocityY ��ֱ������ٶ�
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						if (Math.abs(velocityX) < 150) {
							Toast.makeText(getApplicationContext(),
									"��Ч����������̫��", 0).show();
							return true;
						}
						if ((e1.getX() - e2.getX()) > 150) {
							// �������� ��һ��
							showNext();
							overridePendingTransition(R.anim.next_in,
									R.anim.next_out);
						}
						if ((e2.getX() - e1.getX()) > 150) {
							// �������һ�����һ��
							showPre();
							overridePendingTransition(R.anim.pre_in,
									R.anim.pre_out);
							
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});

	}

	// �����¼���������ʶ����ȥʶ��
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void next(View view) {
		showNext();
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}

	public abstract void showNext();

	public void pre(View view) {
		showPre();
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}

	public abstract void showPre();

	public void startActivityAndFinishSelf(Class<?> clas) {
		Intent intent = new Intent(this, clas);
		startActivity(intent);
		finish();
	}

}
