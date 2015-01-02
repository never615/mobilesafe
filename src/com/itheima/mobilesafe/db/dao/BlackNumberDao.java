package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

/**
 * ���������ݿ���ɾ�Ĳ鹤����
 * 
 * @author rong
 * 
 */
public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * ��Ӻ���������
	 * 
	 * @param number
	 *            ����
	 * @param mode
	 *            ģʽ
	 * @return �Ƿ���ӳɹ�
	 */
	public boolean add(String number, String mode) {
		// ��ȡ��д���ݿ�
		SQLiteDatabase db = helper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		long rowid = db.insert("blackinfo", null, values);
		if (rowid == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ɾ������������
	 * 
	 * @param number
	 *            ����
	 * @return �Ƿ�ɾ���ɹ�
	 */
	public boolean delete(String number) {
		// ��ȡ��д���ݿ�
		SQLiteDatabase db = helper.getWritableDatabase();
		int rownumber = db.delete("blackinfo", "number=?",
				new String[] { number });
		if (rownumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * �޸ĺ����������ģʽ
	 * 
	 * @param number
	 *            ����
	 * @param newmode
	 *            �µ�����ģʽ
	 * @return �ǹ��޸�
	 */
	public boolean changeBlockMode(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		int rownumber = db.update("blackinfo", values, "number=?",
				new String[] { number });
		if (rownumber == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ����һ����������������ģʽ
	 * 
	 * @param number
	 *            Ҫ��ѯ�ĺ���������
	 * @return 0���Ǻ��������벻���� 1ȫ������ 2�������� 3�绰����
	 */
	public String findBlockMode(String number) {
		String mode = "0";
		// ��ȡ���ɶ������ݿ�
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("blackinfo", new String[] { "mode" },
				"number=?", new String[] { number }, null, null, null);
		if (cursor.moveToNext()) {
			mode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return mode;
	}

	/**
	 * ��ѯȫ���ĺ���������
	 */
	public List<BlackNumberInfo> findAll() {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.query("blackinfo", new String[] { "number", "mode" }, null,
						null, null, null, null);
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return blackNumberInfos;
	}

	/**
	 * ��ҳ��ѯ���ݿ�ļ�¼
	 * 
	 * @param currentpagenumber
	 *            ��ǰҳ�룬��0��ʼ
	 * @param pagesize
	 *            ҳ��Ĵ�С
	 */
	public List<BlackNumberInfo> findPart(int currentPageNumber, int pageSize) {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blackinfo limit ? offset ?",
				new String[] { String.valueOf(pageSize),
						String.valueOf(pageSize * currentPageNumber) });
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return blackNumberInfos;
	}
	/**
	 * ������ѯ���ݿ�ļ�¼
	 * 
	 * @param startIndex
	 *            ��ʵ��Ŀλ��
	 * @param maxCount
	 *            ���ص�������С
	 */
	public List<BlackNumberInfo> findPart2(int startIndex, int maxCount) {
		// �õ��ɶ������ݿ�
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select number,mode from blackinfo order by _id desc limit ? offset ?",
				new String[] { String.valueOf(maxCount),
						String.valueOf(startIndex) });
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			blackNumberInfos.add(info);
		}
		cursor.close();
		db.close();
		SystemClock.sleep(30);
		return blackNumberInfos;
	}

	/**
	 * ��ȡ���ݿ������Ŀ��
	 * 
	 * @return
	 */
	public int getTotalNumber() {
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from blackinfo", null);
		cursor.moveToNext();
		/**
		 * ����ΪʲôҪд moveToNext() �� 
		 * ��Ϊ����ѯ�õ���cursor��ָ���һ����¼֮ǰ�ģ�
		 * ��˲�ѯ�õ�cursor���һ�ε���moveToFirst��moveToNext�����Խ�cursor�ƶ�����һ����¼�ϡ�
		 * Դ������ЩmoveXXX��ʵ����ͨ��moveToPosition��ʵ�ֵ�
		 * ������¼position����һ�����ͱ���mPos����moveXXXX����false��ʱ��
		 * ��mPos�ᱻ��Ϊ-1��Ҳ���ǻص��˳�ʼ״̬��ָ���һ����¼֮ǰ��
		 */
		int count = cursor.getInt(0);
		cursor.close();
		db.close();
		return count;
	}

}
