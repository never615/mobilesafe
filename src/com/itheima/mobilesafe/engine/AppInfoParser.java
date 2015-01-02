package com.itheima.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.itheima.mobilesafe.domain.AppInfo;

public class AppInfoParser {

	/**
	 * 获取手机里面所有的应用程序
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			AppInfo appInfo = new AppInfo();

			// 包名
			String packName = packageInfo.packageName;
			// 图标
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			// 应用名字
			String apkNmae = packageInfo.applicationInfo.loadLabel(pm)
					.toString();
			// apkPath
			String apkPath = packageInfo.applicationInfo.sourceDir;
			// 应用大小
			long appSize = new File(apkPath).length();

			// System.out.println("packName___"+packName);
			// System.out.println("appNmae____"+appNmae);
			// System.out.println("apkPath____"+apkPath);

			appInfo.setPackName(packName);
			appInfo.setApkName(apkNmae);
			appInfo.setApkPath(apkPath);
			appInfo.setAppSize(appSize);
			appInfo.setIcon(icon);

			// 应用程序的安装位置 通过flag来判断
			int flags = packageInfo.applicationInfo.flags; // 二进制映射
			if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
				// 外部存储
				appInfo.setInrom(false);
			} else {
				// 内部存储
				appInfo.setInrom(true);
			}
			if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
				// 系统应用
				appInfo.setUserApp(false);
			} else {
				// 用户应用
				appInfo.setUserApp(true);
			}

			// 系统程序还是用户程序，可以用flag，也可以用apkPath来判断
			/*
			 * if(apkPath.startsWith("/system/")){ //系统程序
			 * System.out.println("根据目录判断为系统应用"); }else{
			 * System.out.println("根据目录判断为用户应用"); //用户程序 }
			 */

			appInfos.add(appInfo);
			appInfo = null;
		}
		return appInfos;
	}
}
