package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * 操作号码归属地数据库的dao类
 * 
 * @author rong
 * 
 */
public class NumberAddressDao {
	/**
	 * 返回号码的归属地
	 * 
	 * @param phonenumber
	 *            电话号码
	 * @return 归属地
	 */
	public static String getLocation(String phonenumber) {
		String location = phonenumber;

		// 先拿到数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"data/data/com.itheima.mobilesafe/files/address.db", null,
				SQLiteDatabase.OPEN_READONLY);

		// 号码查询
		if (phonenumber.matches("^1[34578]\\d{9}$")) {
			// 手机号码的查询s
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { phonenumber.substring(0, 7) });

			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}
			db.close();
		} else {
			// 其他电话
			switch (phonenumber.length()) {
			case 3:
				if ("110".equals(phonenumber)) {
					location = "匪警";
				} else if ("120".equals(phonenumber)) {
					location = "急救";
				} else {
					location = "其他报警号码";
				}
				break;
			case 4:
				location = "模拟器";
				break;
			case 5:
				location = "客服电话";
				break;
			case 7:
				location = "本地电话";
				break;
			case 8:
				location = "本地电话";
				break;
			default:
				// 其他长途电话
				if (phonenumber.length() >= 9 && phonenumber.startsWith("0")) {
					String address = null;
					//查询三位区号的
					Cursor cursor = db.rawQuery(
							"select location from data2 where area = ?",
							new String[] { location.substring(1, 3) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();

					//查询四位区号
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
