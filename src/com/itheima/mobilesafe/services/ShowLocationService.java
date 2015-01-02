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
	private WindowManager windowManager; // ���ڹ�����
	private View view; // �Զ�����˾�ϵ�view����
	private WindowManager.LayoutParams mParams; // �Զ�����˾�ϵ�view�Ĳ���
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
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE); // �õ����ڹ������
		listener = new MyPhoneListener();
		dao = new NumberAddressDao();
		receiver = new OutCallReiver();

		// �绰���������������磬��ʾ������
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// �Ⲧ�绰ͨ���㲥������ʵ��
		// ͨ������ע��㲥������
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL));

	}

	/**
	 * �Ⲧ�绰�㲥������
	 * 
	 * @author rong
	 * 
	 */
	private class OutCallReiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// �õ��Ⲧ����
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
			case TelephonyManager.CALL_STATE_IDLE: // ����״̬
				// ����״̬�°ѹ�������ʾ��view�����Ƴ�
				if (view != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING: // ����״̬
				String address = dao.getLocation(incomingNumber);
				// Toast.makeText(getApplicationContext(), location, 0).show();
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ͨ״̬
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
	 * �Զ������˾ �ѹ�������ʾ��ͨ��������
	 * 
	 * @param address
	 */
	public void showMyToast(String address) {
		// �����Զ�����˾�Ĳ����ļ�
		view = View.inflate(this, R.layout.toast_showaddress, null);
		// "��͸��","������","��ʿ��","������","ƻ����"
		view.setBackgroundResource(bgs[sp.getInt("which", 0)]);

		// ���ù����ص���˾��
		TextView tv_toast_address = (TextView) view
				.findViewById(R.id.tv_toast_address);
		tv_toast_address.setText(address);

		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// ��ʼ��view��λ��
		mParams.gravity = Gravity.LEFT + Gravity.TOP; // ����view��Ϊ���϶��룬������������0���λ��
		mParams.x = sp.getInt("lastX", 0);
		mParams.y = sp.getInt("lastY", 0);

		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// //Ҫ���ֶ��ı�view��λ�ã���Ҫ���Դ���
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		// mParams.type = WindowManager.LayoutParams.TYPE_TOAST; //��˾������������Ӧ�����¼�
		mParams.type=WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		
		// ���view���󵽴�����
		windowManager.addView(view, mParams);

		// ��view����һ�������¼�
		view.setOnTouchListener(new OnTouchListener() {
			int startX=0;
			int startY=0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.i(tag, "��ָ�����˿ؼ���");
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					Log.i(tag,"��ʼλ�ã�"+startX+","+startY);
					break;
				case MotionEvent.ACTION_MOVE:
					Log.i(tag, "��ָ�ڿؼ����ƶ�");
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					Log.i(tag,"�µ�λ�ã�"+newX+","+newY);

					// �仯��
					int dX = newX - startX;
					int dY = newY - startY;

					Log.i(tag,"ƫ������"+dX+","+dY);
					Log.i(tag,"���¿ؼ�����Ļ�ϵ�λ��");
					// �ı��������ʾ���λ��
					mParams.x += dX;
					mParams.y += dY;
					
					// �߽����
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
					
					//��������ʾ��λ�øı�֮�����»�ȡ��ָ�ĳ�ʼλ��
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					

					break;
				case MotionEvent.ACTION_UP:
					Log.i(tag, "��ָ�뿪�˿ؼ�");
					// ��ס��ָ����ʼ�ؼ���λ�ã�Ϊ�´ε���ʱ�ĳ�ʼλ��
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
