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
 * 创建这个抽象类，让四个向导界面继承它
 * 
 * 把上一步下一步的方法写到这里； 把声明sharedpreferences的代码写到这里；
 * 
 * 
 * @author rong
 * 
 */
public abstract class BaseSetupActivity extends Activity {
	public SharedPreferences sp;
	// 定义一个手势识别器
	public GestureDetector mGestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 初始化手势识别器
		mGestureDetector = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					// e1 代表手指第一次触摸屏幕的事件
					// e2 代表手指离开屏幕一瞬间的事件
					// velocityX 水平方向的速度 单位 pix/s
					// velocityY 竖直方向的速度
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						if (Math.abs(velocityX) < 150) {
							Toast.makeText(getApplicationContext(),
									"无效动作，移速太慢", 0).show();
							return true;
						}
						if ((e1.getX() - e2.getX()) > 150) {
							// 从右往左滑 下一步
							showNext();
							overridePendingTransition(R.anim.next_in,
									R.anim.next_out);
						}
						if ((e2.getX() - e1.getX()) > 150) {
							// 从左往右滑，上一步
							showPre();
							overridePendingTransition(R.anim.pre_in,
									R.anim.pre_out);
							
						}
						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});

	}

	// 触摸事件。用手势识别器去识别
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
