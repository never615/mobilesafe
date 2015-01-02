package com.itheima.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * �����������ݿ�Ĳ�����
 * @author rong
 *
 */
public class AntivirusDao {
	/**
	 * ��ѯ�������ݿ�
	 * @param md5  Ӧ�ó����������
	 * @return   ��Ϊ��Ϊ����������Ϊnull���ǲ���
	 */
	public static String find(String md5,Context context){
		File file=new File(context.getFilesDir(),"antivirus.db");
		String desc=null;
		SQLiteDatabase db=SQLiteDatabase.openDatabase(file.toString(), null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor=db.rawQuery("select desc from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()){
			desc=cursor.getString(0);
		}
		return desc;
	}
}
