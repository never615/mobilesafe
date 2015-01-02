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
	// Called when the application is starting当应用程序被开启的时候调用
	// before any other application objects have been created
	@Override
	public void onCreate() {
		super.onCreate();
		// 老母子 基地
		// 重写系统的异常处理
		Thread.currentThread().setUncaughtExceptionHandler(
				new MyExceptionHandler());
	}

	private class MyExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			try {
				// 留下遗嘱 留下遗嘱时间
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
			
			//专注自杀的方法，早死早超生，
			android.os.Process.killProcess(android.os.Process.myPid());

		}

	}
}
