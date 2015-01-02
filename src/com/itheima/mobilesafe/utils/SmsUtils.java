package com.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Xml;

/**
 * ���Ų����࣬�ṩ���ŵı��ݺͻ�ԭ��API
 * 
 * @author rong
 * 
 */
public class SmsUtils {

	/**
	 * ������ű��ݵĻص��ӿ�
	 * 
	 * @author rong
	 * 
	 */
	public interface BackupSmsCallBack {
		/**
		 * �ڶ��ű���֮ǰ�ص��ķ���
		 * 
		 * @param size
		 *            ���ŵ�������
		 */
		public void beforeSmsBackup(int size);

		/**
		 * ���ű����еĻص�����
		 * 
		 * @param progress
		 *            ��ǰ�Ľ���
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * ���ݶ���
	 * 
	 * @param context
	 *            ������
	 * @param callback
	 *            �ӿ�
	 * @return
	 * 
	 *         1.�������ṩ�߲�ѯ�������� 2.��xml���л��������������
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws FileNotFoundException
	 */
	public static boolean backUpSms(Context context, BackupSmsCallBack callback)
			throws FileNotFoundException, IllegalStateException, IOException {
		// �õ����л���
		XmlSerializer serializer = Xml.newSerializer();
		// ���ô洢·��
		File sdDir = Environment.getExternalStorageDirectory();
		// �õ�ָ��·���Ŀ��ÿռ�
		long freeSize = sdDir.getFreeSpace();

		// �ж����sd���ɶ���д����ʣ��ռ����1M����ִ�б���
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& freeSize > 1024 * 1024) {
			// �������ݵ��ļ�
			File file = new File(Environment.getExternalStorageDirectory(),
					"backupSms.xml");
			FileOutputStream os = new FileOutputStream(file);

			// ��ʼ�����л���
			serializer.setOutput(os, "utf-8");
			// Ȼ��Ϳ�ʼдxml�ļ���

			// xml��ͷ
			serializer.startDocument("utf-8", true);

			// ������Ҫ�ö��ŵ�����̫�ṩ�߲�ѯ������������
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"body", "type", "date" }, null, null, null);
			// �õ���ѯ���Ķ��ŵ�������
			int size = cursor.getCount();
			callback.beforeSmsBackup(size);

			// �����
			serializer.startTag(null, "smss");
			serializer.attribute(null, "size", String.valueOf(size)); // ����startTag֮����������

			int progress=0;
			while (cursor.moveToNext()) {
				serializer.startTag(null, "sms");

				serializer.startTag(null, "body");
				// ���ܳ����������⣬�������������ܻᵼ�±���ʧ��
				try {
					String bodyencpyt = Crypto.encrypt("123",
							cursor.getString(1));
					serializer.text(bodyencpyt);
				} catch (Exception e) {
					e.printStackTrace();
					serializer.text("���Ŷ�ȡʧ�ܣ�");
				}
				serializer.endTag(null, "body");

				serializer.startTag(null, "address");
				serializer.text(cursor.getString(0));
				serializer.endTag(null, "address");

				serializer.startTag(null, "type");
				serializer.text(cursor.getString(2));
				serializer.endTag(null, "type");

				serializer.startTag(null, "date");
				serializer.text(cursor.getString(3));
				serializer.endTag(null, "date");

				serializer.endTag(null, "sms");

				// ģ�����ݺܶ�����������˯һ��
				SystemClock.sleep(1000);
				progress++;
				callback.onSmsBackup(progress);

			}
			cursor.close();
			serializer.endTag(null, "smss");
			serializer.endDocument();
			os.flush();
			os.close();
			return true;
		} else {
			throw new IllegalStateException("SD���ռ䲻����߲����á�");
		}
	}

	/**
	 * ���Ż�ԭ�Ļص���������������ʹ��
	 * 
	 * @author rong
	 * 
	 */
	public interface RestoreSmsCallBack {
		public void beforeSmsRestore(int size); // ���ݶ�������

		public void onSmsRestore(int progress); // ���ݻ�ԭ���ŵĽ���
	}

	public static boolean restoreSms(Context context) {
		// TODO ��ɶ��Ż�ԭ�ķ���
		// �жϱ����ļ��Ƿ���ڣ���ȡsd�����ļ�
		// ����xml�ļ�
		// 1.ʹ��android���õ�XmlResourceParser������xml�ļ�
		// 2.����xml�ļ�while���ĵ�ĩβ��
		// {
		// ��ȡ����size�����ýӿڷ��� beforeSmsRestore()
		// ÿ��ȡ��һ�����ţ��ͽ���body��Ҫ���ܣ���address��type��date
		// ���������ṩ�� resolver.insert(Uri.parse("content://sms/"),contentValue);
		// contentValueά��һ��map����
		// ÿ��ԭһ�����ŵ���onSmsRestore��count������ count++
		// }

		return false;
	}

}
