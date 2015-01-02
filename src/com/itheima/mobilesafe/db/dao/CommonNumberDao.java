package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * ���ú�������ݿ����������
 * @author rong
 *
 */
public class CommonNumberDao {

	/**
	 * ��ȡ���ݿ�һ���ж��ٸ�����
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
	 * ��ȡ���ݿ�ĳ�������ж��ٸ�����
	 * @param db
	 * @param groupPosition
	 * @return
	 */
	public static int getChildCount(SQLiteDatabase db,int groupPosition){
		//��Ϊ���ݿ��е�һ��������һ�����Լ�һ
		Cursor cursor=db.rawQuery("select count(*) from table"+(groupPosition+1), null);
		cursor.moveToNext();
		int childCount=cursor.getInt(0);
		cursor.close();
		return childCount;
	}
	/**
	 * ��ȡ���ݿ�ĳһ�����������
	 * @param db
	 */
	public static String getGroupName(SQLiteDatabase db,int groupPosition){
		//��Ϊ���ݿ��е�һ��������һ�����Լ�һ
		Cursor cursor=db.rawQuery("select name from classlist where idx=?", new String[]{String.valueOf(groupPosition+1)});
		cursor.moveToNext();
		String groupName=cursor.getString(0);
		cursor.close();
		return groupName;
	}
	/**
	 * ��ȡ���ݿ�ĳһ��������ĳһ�����ӵ����ֺ͵绰
	 * @param db
	 */
	public static String getChildNameAndNumber(SQLiteDatabase db,int groupPosition,int childPosition){
		//��Ϊ���ݿ��е�һ��������һ�����Լ�һ
		Cursor cursor=db.rawQuery("select name,number from table"+(groupPosition+1)+" where _id=?", new String[]{String.valueOf(childPosition+1)});
		cursor.moveToNext();       
		String childName=cursor.getString(0);
		String childNumber=cursor.getString(1);
		cursor.close();
		return childName+"#"+childNumber;
	}
	
	
}
