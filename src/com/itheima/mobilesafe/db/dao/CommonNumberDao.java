package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 常用号码的数据库操作方法类
 * @author rong
 *
 */
public class CommonNumberDao {

	/**
	 * 获取数据库一共有多少个分组
	 * @param db
	 */
	public static int getGroupCount(SQLiteDatabase db){
		Cursor cursor=db.rawQuery("select count(*) from classlist", null);
		cursor.moveToNext();
		int groupCount=cursor.getInt(0);
		cursor.close();
		return groupCount;
	}
	/**
	 * 获取数据库某个分组有多少个孩子
	 * @param db
	 * @param groupPosition
	 * @return
	 */
	public static int getChildCount(SQLiteDatabase db,int groupPosition){
		//因为数据库中第一组名字是一，所以加一
		Cursor cursor=db.rawQuery("select count(*) from table"+(groupPosition+1), null);
		cursor.moveToNext();
		int childCount=cursor.getInt(0);
		cursor.close();
		return childCount;
	}
	/**
	 * 获取数据库某一个分组的名字
	 * @param db
	 */
	public static String getGroupName(SQLiteDatabase db,int groupPosition){
		//因为数据库中第一组名字是一，所以加一
		Cursor cursor=db.rawQuery("select name from classlist where idx=?", new String[]{String.valueOf(groupPosition+1)});
		cursor.moveToNext();
		String groupName=cursor.getString(0);
		cursor.close();
		return groupName;
	}
	/**
	 * 获取数据库某一个分组中某一个孩子的名字和电话
	 * @param db
	 */
	public static String getChildNameAndNumber(SQLiteDatabase db,int groupPosition,int childPosition){
		//因为数据库中第一组名字是一，所以加一
		Cursor cursor=db.rawQuery("select name,number from table"+(groupPosition+1)+" where _id=?", new String[]{String.valueOf(childPosition+1)});
		cursor.moveToNext();       
		String childName=cursor.getString(0);
		String childNumber=cursor.getString(1);
		cursor.close();
		return childName+"#"+childNumber;
	}
	
	
}
