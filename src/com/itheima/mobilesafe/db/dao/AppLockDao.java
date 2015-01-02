package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itheima.mobilesafe.db.AppLockDBOpenHelper;

public class AppLockDao {

	private AppLockDBOpenHelper helper;
	private Context context;

	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * ���һ����applock���ݿ�
	 * 
	 * @param packname
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packname", packname);
		db.insert("applockinfo", null, values);
		db.close();

		// ֪ͨ���ݹ۲���
		context.getContentResolver().notifyChange(
				Uri.parse("content://com.itheima.mobilesafe.applock"), null);
	}

	/**
	 * ɾ��һ�� ��applock���ݿ���
	 * 
	 * @param packname
	 */
	public void delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("applockinfo", "packname=?", new String[] { packname });
		db.close();

		// ֪ͨ���ݹ۲���
		context.getContentResolver().notifyChange(
				Uri.parse("content://com.itheima.mobilesafe.applock"), null);
	}

	/**
	 * ��ѯһ��app�Ƿ���������ݿ�
	 * 
	 * @param packname
	 * @return
	 */
	public boolean find(String packname) {
		boolean flag = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applockinfo", null, "packname=?",
				new String[] { packname }, null, null, null);
		if (cursor.moveToNext()) {
			flag = true;
		}
		cursor.close();
		db.close();
		return flag;
	}

	/**
	 * ��ѯȫ���������İ���
	 * 
	 * @return
	 */
	public List<String> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applockinfo", new String[] { "packname" },
				null, null, null, null, null);
		List<String> packnames = new ArrayList<String>();
		while (cursor.moveToNext()) {
			packnames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packnames;
	}

}
