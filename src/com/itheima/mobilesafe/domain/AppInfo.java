package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Ӧ�ó���ҵ��bean
 * 
 * @author rong
 * 
 * 
 * 
 */
public class AppInfo {

	// ͼ�� ���� ·�� ��С λ��(�ֻ��ڻ����ⲿ�洢��) �Ƿ��û����� ����
	/**
	 * Ӧ�ó����ͼ��
	 */
	private Drawable icon;
	/**
    * Ӧ�ó��������
    */
	private String apkName;
	/**
	 * Ӧ�ó����·��
	 */
	private String apkPath;
	/**
	 * Ӧ�ó���Ĵ�С
	 */
	private long appSize;
	/**
	 * Ӧ�ó����λ�ã��ֻ��ڴ��л����ⲿ�洢��
	 */
	private boolean inrom;
	/**
	 * �Ƿ����û�����true���û�����falseΪϵͳ����
	 */
	private boolean userApp;
	/**
	 * Ӧ�ó���İ���
	 */
	private String packName;

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public long getAppSize() {
		return appSize;
	}

	public void setAppSize(long appSize) {
		this.appSize = appSize;
	}

	public boolean isInrom() {
		return inrom;
	}

	public void setInrom(boolean inrom) {
		this.inrom = inrom;
	}

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	@Override
	public String toString() {
		return "AppInfo [apkName=" + apkName + ", apkPath=" + apkPath
				+ ", appSize=" + appSize + ", inrom=" + inrom + ", userApp="
				+ userApp + ", packName=" + packName + "]";
	}

}
