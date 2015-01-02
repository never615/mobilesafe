package com.itheima.mobilesafe.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作病毒数据库的操作类
 * @author rong
 *
 */
public class AntivirusDao {
	/**
	 * 查询病毒数据库
	 * @param md5  应用程序的特征码
	 * @return   不为空为病毒描述，为null则不是病毒
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
