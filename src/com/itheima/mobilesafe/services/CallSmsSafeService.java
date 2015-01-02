package com.itheima.mobilesafe.services;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {
	private InnerSmsReceiver receiver;
	private BlackNumberDao dao;
	private TelephonyManager tm;
	private MyPhoneListener listener; // 系统提供的电话管理器，提供电话服务s

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();

		// 短信拦截
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE); // 最高优先级，如果manifest和代码中配置的广播接收者的优先级一样的话，代码优先
		registerReceiver(receiver, filter);

		// 电话拦截
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				String mode = dao.findBlockMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					Log.i("MyPhoneListener", "挂断电话");
					endCall();

					// 挂断电话之后删除呼叫记录,注册内容观察者监测呼叫记录，一发现要删除的号码，就删除，然后反注册内容观察者
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,
							new CallLogObserver(new Handler(), incomingNumber));

				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通状态
				break;
			}
		}
	}
	/**
	 * 通话记录的内容观察者
	 * @author rong
	 *
	 */
	private class CallLogObserver extends ContentObserver{

		private String incomingNumber;
		public CallLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber=incomingNumber;
		}
		//观察到数据库内容发生变化调用的方法
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("CallLogObserver", "呼叫记录发生变化了！");
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver); // 回收广播接收者
		receiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener=null;
	}

	/**
	 * 删除通话记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		Uri uri=Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
	}

	/**
	 * 利用反射挂断电话
	 */
	public void endCall() {
		try {
			Class clazz = getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
			itelephony.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class InnerSmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("InnerSmsReceiver", "短信来了！！！");
			// 判断短信发送人是否在黑名单中
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findBlockMode(sender);

				if ("1".equals(mode) || "2".equals(mode)) {
					Log.i("InnerSmsReceiver", "黑名单短信被拦截！！");
					abortBroadcast(); // 终止短信广播，短信就会被拦截
				}
				// 只能拦截
				String body = smsMessage.getMessageBody();
				if (body.contains("发票")) {
					Log.i("InnerSmsReceiver", "拦截到拦击发票短信。。");
					abortBroadcast();
				}
			}
		}
	}

}
