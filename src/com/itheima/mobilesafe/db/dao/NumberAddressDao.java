package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * ����������������ݿ��dao��
 * 
 * @author rong
 * 
 */
public class NumberAddressDao {
	/**
	 * ���غ���Ĺ�����
	 * 
	 * @param phonenumber
	 *            �绰����
	 * @return ������
	 */
	public static String getLocation(String phonenumber) {
		String location = phonenumber;

		// ���õ����ݿ�
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"data/data/com.itheima.mobilesafe/files/address.db", null,
				SQLiteDatabase.OPEN_READONLY);

		// �����ѯ
		if (phonenumber.matches("^1[34578]\\d{9}$")) {
			// �ֻ�����Ĳ�ѯs
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phonenumber.substring(0, 7) });

			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			db.close();
		} else {
			// �����绰
			switch (phonenumber.length()) {
			case 3:
				if ("110".equals(phonenumber)) {
					location = "�˾�";
				} else if ("120".equals(phonenumber)) {
					location = "����";
				} else {
					location = "������������";
				}
				break;
			case 4:
				location = "ģ����";
				break;
			case 5:
				location = "�ͷ��绰";
				break;
			case 7:
				location = "���ص绰";
				break;
			case 8:
				location = "���ص绰";
				break;
			default:
				// ������;�绰
				if (phonenumber.length() >= 9 && phonenumber.startsWith("0")) {
					String address = null;
					//��ѯ��λ���ŵ�
					Cursor cursor = db.rawQuery(
							"select location from data2 where area = ?",
							new String[] { location.substring(1, 3) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();

					//��ѯ��λ����
					cursor = db.rawQuery(
							"select location from data2 where area = ?",
							new String[] { location.substring(1, 4) });
					if(cursor.moveToNext()){
						address=cursor.getString(0);
					}
					cursor.close();
					if(!TextUtils.isEmpty(address)){
						location=address.substring(0,address.length()-2);
					}
				}
				break;
			}
		}
		return location;
	}
}
