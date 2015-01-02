package com.itheima.mobilesafe.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;

public class MobileSafeApplication extends Application {
	// Called when the application is starting��Ӧ�ó��򱻿�����ʱ�����
	// before any other application objects have been created
	@Override
	public void onCreate() {
		super.onCreate();
		// ��ĸ�� ����
		// ��дϵͳ���쳣����
		Thread.currentThread().setUncaughtExceptionHandler(
				new MyExceptionHandler());
	}

	private class MyExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			try {
				// �������� ��������ʱ��
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				File file = new File(Environment.getExternalStorageDirectory(),
						"error.log");
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(sw.toString().getBytes());
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//רע��ɱ�ķ����������糬����
			android.os.Process.killProcess(android.os.Process.myPid());

		}

	}
}
