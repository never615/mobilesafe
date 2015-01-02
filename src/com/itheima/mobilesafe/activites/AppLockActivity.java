package com.itheima.mobilesafe.activites;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.fragments.LockFragment;
import com.itheima.mobilesafe.fragments.UnLockFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AppLockActivity extends FragmentActivity implements
		OnClickListener {

	@ViewInject(R.id.tv_unlock)
	private TextView tv_unlock;
	@ViewInject(R.id.tv_locked)
	private TextView tv_locked;

	private LockFragment lockFragment;
	private UnLockFragment unLockFragment;

	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_app_lock);
		ViewUtils.inject(this);

		// 设置点击事件
		tv_locked.setOnClickListener(this);
		tv_unlock.setOnClickListener(this);

		// 初始化帧管理器
		fm = getSupportFragmentManager();
		lockFragment = new LockFragment();
		unLockFragment = new UnLockFragment();

		// 开启界面变换的事务
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fl_container, unLockFragment);
		ft.commit();

	}

	@Override
	public void onClick(View v) {
		
		// 开启界面变换的事务
		FragmentTransaction ft = fm.beginTransaction();
		System.out.println("AppLockActivity 的点击事件触发·················");

		switch (v.getId()) {
		case R.id.tv_locked:
			tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			ft.replace(R.id.fl_container, lockFragment);
			break;
		case R.id.tv_unlock:
			tv_locked.setBackgroundResource(R.drawable.tab_right_default);
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			ft.replace(R.id.fl_container, unLockFragment);
			break;
		}
		ft.commit();
	}

}
