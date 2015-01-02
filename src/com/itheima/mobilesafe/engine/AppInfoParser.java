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
	 * ��ȡ�ֻ��������е�Ӧ�ó���
	 * 
	 * @param context
	 *            ������
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			AppInfo appInfo = new AppInfo();

			// ����
			String packName = packageInfo.packageName;
			// ͼ��
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			// Ӧ������
			String apkNmae = packageInfo.applicationInfo.loadLabel(pm)
					.toString();
			// apkPath
			String apkPath = packageInfo.applicationInfo.sourceDir;
			// Ӧ�ô�С
			long appSize = new File(apkPath).length();

			// System.out.println("packName___"+packName);
			// System.out.println("appNmae____"+appNmae);
			// System.out.println("apkPath____"+apkPath);

			appInfo.setPackName(packName);
			appInfo.setApkName(apkNmae);
			appInfo.setApkPath(apkPath);
			appInfo.setAppSize(appSize);
			appInfo.setIcon(icon);

			// Ӧ�ó���İ�װλ�� ͨ��flag���ж�
			int flags = packageInfo.applicationInfo.flags; // ������ӳ��
			if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & flags) != 0) {
				// �ⲿ�洢
				appInfo.setInrom(false);
			} else {
				// �ڲ��洢
				appInfo.setInrom(true);
			}
			if ((ApplicationInfo.FLAG_SYSTEM & flags) != 0) {
				// ϵͳӦ��
				appInfo.setUserApp(false);
			} else {
				// �û�Ӧ��
				appInfo.setUserApp(true);
			}

			// ϵͳ�������û����򣬿�����flag��Ҳ������apkPath���ж�
			/*
			 * if(apkPath.startsWith("/system/")){ //ϵͳ����
			 * System.out.println("����Ŀ¼�ж�ΪϵͳӦ��"); }else{
			 * System.out.println("����Ŀ¼�ж�Ϊ�û�Ӧ��"); //�û����� }
			 */

			appInfos.add(appInfo);
			appInfo = null;
		}
		return appInfos;
	}
}
