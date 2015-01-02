package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 程序锁数据库初始化
	 * 数据库创建的构造方法
	 * 
	 * @param context
	 */
	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);
	}
	
	

	//在数据库被创建出来后Android系统会自动调用此方法，第一次创建数据库时调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applockinfo(_id integer primary key autoincrement,packname varchar(20))");
	}

	//当数据库被更新时(数据库的版本号改变时)调用该方法,版本便小时并不调用
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}




	
}
