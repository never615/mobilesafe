package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.NumberAddressDao;

public class ShowLocationService extends Service {

	private TelephonyManager tm;
	private MyPhoneListener listener;
	private NumberAddressDao dao;
	private OutCallReiver receiver;
	private WindowManager windowManager; // 窗口管理器
	private View view; // 自定义吐司上的view对象
	private WindowManager.LayoutParams mParams; // 自定义吐司上的view的参数
	private SharedPreferences sp;
	protected String tag = "ShowLocationService";
	private static final int[] bgs = { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green };

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE); // 拿到窗口管理服务
		listener = new MyPhoneListener();
		dao = new NumberAddressDao();
		receiver = new OutCallReiver();

		// 电话监听器，监听来电，显示归属地
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 外拨电话通过广播接收者实现
		// 通过代码注册广播接收者
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL));

	}

	/**
	 * 外拨电话广播接收者
	 * 
	 * @author rong
	 * 
	 */
	private class OutCallReiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到外拨号码
			String number = getResultData();
			String address = dao.getLocation(number);
			// Toast.makeText(getApplicationContext(), address, 0).show();
			showMyToast(address);
		}
	}

	private class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
				// 空闲状态下把归属地显示的view对象移除
				if (view != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				String address = dao.getLocation(incomingNumber);
				// Toast.makeText(getApplicationContext(), location, 0).show();
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通状态
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;

		unregisterReceiver(receiver);
		receiver = null;
	}

	/**
	 * 自定义的吐司 把归属地显示到通话界面上
	 * 
	 * @param address
	 */
	public void showMyToast(String address) {
		// 加载自定义吐司的布局文件
		view = View.inflate(this, R.layout.toast_showaddress, null);
		// "半透明","活力橙","卫士蓝","金属灰","苹果绿"
		view.setBackgroundResource(bgs[sp.getInt("which", 0)]);

		// 设置归属地到吐司上
		TextView tv_toast_address = (TextView) view
				.findViewById(R.id.tv_toast_address);
		tv_toast_address.setText(address);

		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// 初始化view的位置
		mParams.gravity = Gravity.LEFT + Gravity.TOP; // 设置view的为左上对齐，就是设置坐标0点的位置
		mParams.x = sp.getInt("lastX", 0);
		mParams.y = sp.getInt("lastY", 0);

		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// //要能手动改变view的位置，需要可以触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		// mParams.type = WindowManager.LayoutParams.TYPE_TOAST; //吐司类型天生不响应触摸事件
		mParams.type=WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		
		// 添加view对象到窗口上
		windowManager.addView(view, mParams);

		// 给view设置一个触摸事件
		view.setOnTouchListener(new OnTouchListener() {
			int startX=0;
			int startY=0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.i(tag, "手指按到了控件上");
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(tag,"开始位置："+startX+","+startY);
					break;
				case MotionEvent.ACTION_MOVE:
					Log.i(tag, "手指在控件上移动");
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(tag,"新的位置："+newX+","+newY);

					// 变化量
					int dX = newX - startX;
					int dY = newY - startY;

					Log.i(tag,"偏移量："+dX+","+dY);
					Log.i(tag,"更新控件在屏幕上的位置");
					// 改变归属地提示框的位置
					mParams.x += dX;
					mParams.y += dY;
					
					// 边界控制
					if(mParams.x<0){
						mParams.x=0;
					}
					if(mParams.y<0){
						mParams.y=0;
					}
					if(mParams.x>(windowManager.getDefaultDisplay().getWidth()-view.getWidth())){
						mParams.x=windowManager.getDefaultDisplay().getWidth()-view.getWidth();
					}
					if(mParams.y>(windowManager.getDefaultDisplay().getHeight()-view.getHeight())){
						mParams.y=windowManager.getDefaultDisplay().getHeight()-view.getHeight();
					}
					
					windowManager.updateViewLayout(view, mParams); 
					
					//归属地提示框位置改变之后，重新获取手指的初始位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					

					break;
				case MotionEvent.ACTION_UP:
					Log.i(tag, "手指离开了控件");
					// 记住手指来开始控件的位置，为下次弹出时的初始位置
					Editor editor = sp.edit();
					editor.putInt("lastX", mParams.x);
					editor.putInt("lastY", mParams.y);
					editor.commit();
					break;
				}

				return true;
			}
		});

	}

}
