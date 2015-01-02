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
	private MyPhoneListener listener; // ϵͳ�ṩ�ĵ绰���������ṩ�绰����s

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

		// ��������
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE); // ������ȼ������manifest�ʹ��������õĹ㲥�����ߵ����ȼ�һ���Ļ�����������
		registerReceiver(receiver, filter);

		// �绰����
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // ����״̬
				break;
			case TelephonyManager.CALL_STATE_RINGING: // ����״̬
				String mode = dao.findBlockMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					Log.i("MyPhoneListener", "�Ҷϵ绰");
					endCall();

					// �Ҷϵ绰֮��ɾ�����м�¼,ע�����ݹ۲��߼����м�¼��һ����Ҫɾ���ĺ��룬��ɾ����Ȼ��ע�����ݹ۲���
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true,
							new CallLogObserver(new Handler(), incomingNumber));

				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // ��ͨ״̬
				break;
			}
		}
	}
	/**
	 * ͨ����¼�����ݹ۲���
	 * @author rong
	 *
	 */
	private class CallLogObserver extends ContentObserver{

		private String incomingNumber;
		public CallLogObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber=incomingNumber;
		}
		//�۲쵽���ݿ����ݷ����仯���õķ���
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Log.i("CallLogObserver", "���м�¼�����仯�ˣ�");
			getContentResolver().unregisterContentObserver(this);
			deleteCallLog(incomingNumber);
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver); // ���չ㲥������
		receiver = null;
		
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener=null;
	}

	/**
	 * ɾ��ͨ����¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		Uri uri=Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
	}

	/**
	 * ���÷���Ҷϵ绰
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
			Log.i("InnerSmsReceiver", "�������ˣ�����");
			// �ж϶��ŷ������Ƿ��ں�������
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.findBlockMode(sender);

				if ("1".equals(mode) || "2".equals(mode)) {
					Log.i("InnerSmsReceiver", "���������ű����أ���");
					abortBroadcast(); // ��ֹ���Ź㲥�����žͻᱻ����
				}
				// ֻ������
				String body = smsMessage.getMessageBody();
				if (body.contains("��Ʊ")) {
					Log.i("InnerSmsReceiver", "���ص�������Ʊ���š���");
					abortBroadcast();
				}
			}
		}
	}

}
