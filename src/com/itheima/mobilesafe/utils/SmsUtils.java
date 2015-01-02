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
 * 短信操作类，提供短信的备份和还原的API
 * 
 * @author rong
 * 
 */
public class SmsUtils {

	/**
	 * 定义短信备份的回调接口
	 * 
	 * @author rong
	 * 
	 */
	public interface BackupSmsCallBack {
		/**
		 * 在短信备份之前回调的方法
		 * 
		 * @param size
		 *            短信的总条数
		 */
		public void beforeSmsBackup(int size);

		/**
		 * 短信备份中的回调方法
		 * 
		 * @param progress
		 *            当前的进度
		 */
		public void onSmsBackup(int progress);
	}

	/**
	 * 备份短信
	 * 
	 * @param context
	 *            上下文
	 * @param callback
	 *            接口
	 * @return
	 * 
	 *         1.用内容提供者查询短信数据 2.用xml序列化器保存短信内容
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws FileNotFoundException
	 */
	public static boolean backUpSms(Context context, BackupSmsCallBack callback)
			throws FileNotFoundException, IllegalStateException, IOException {
		// 拿到序列化器
		XmlSerializer serializer = Xml.newSerializer();
		// 设置存储路径
		File sdDir = Environment.getExternalStorageDirectory();
		// 拿到指定路径的可用空间
		long freeSize = sdDir.getFreeSpace();

		// 判断如果sd卡可读可写，且剩余空间大于1M，就执行备份
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& freeSize > 1024 * 1024) {
			// 创建备份的文件
			File file = new File(Environment.getExternalStorageDirectory(),
					"backupSms.xml");
			FileOutputStream os = new FileOutputStream(file);

			// 初始化序列化器
			serializer.setOutput(os, "utf-8");
			// 然后就开始写xml文件了

			// xml的头
			serializer.startDocument("utf-8", true);

			// 这里需要拿短信的内容太提供者查询出短信数据了
			ContentResolver resolver = context.getContentResolver();
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = resolver.query(uri, new String[] { "address",
					"body", "type", "date" }, null, null, null);
			// 拿到查询出的短信的总数量
			int size = cursor.getCount();
			callback.beforeSmsBackup(size);

			// 根结点
			serializer.startTag(null, "smss");
			serializer.attribute(null, "size", String.valueOf(size)); // 跟在startTag之后，设置属性

			int progress=0;
			while (cursor.moveToNext()) {
				serializer.startTag(null, "sms");

				serializer.startTag(null, "body");
				// 可能出现乱码问题，如果出现乱码可能会导致备份失败
				try {
					String bodyencpyt = Crypto.encrypt("123",
							cursor.getString(1));
					serializer.text(bodyencpyt);
				} catch (Exception e) {
					e.printStackTrace();
					serializer.text("短信读取失败！");
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

				// 模仿数据很多的情况，所以睡一下
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
			throw new IllegalStateException("SD卡空间不足或者不可用。");
		}
	}

	/**
	 * 短信还原的回调方法，给调用者使用
	 * 
	 * @author rong
	 * 
	 */
	public interface RestoreSmsCallBack {
		public void beforeSmsRestore(int size); // 传递短信总数

		public void onSmsRestore(int progress); // 传递还原短信的进度
	}

	public static boolean restoreSms(Context context) {
		// TODO 完成短信还原的方法
		// 判断备份文件是否存在，读取sd卡的文件
		// 解析xml文件
		// 1.使用android内置的XmlResourceParser来解析xml文件
		// 2.解析xml文件while（文档末尾）
		// {
		// 读取属性size，调用接口方法 beforeSmsRestore()
		// 每读取到一条短信，就解析body（要解密），address，type，date
		// 利用内容提供者 resolver.insert(Uri.parse("content://sms/"),contentValue);
		// contentValue维持一个map集合
		// 每还原一条短信调用onSmsRestore（count）方法 count++
		// }

		return false;
	}

}
