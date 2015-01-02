package com.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用程序业务bean
 * 
 * @author rong
 * 
 * 
 * 
 */
public class AppInfo {

	// 图标 名称 路径 大小 位置(手机内还是外部存储中) 是否用户程序 包名
	/**
	 * 应用程序的图标
	 */
	private Drawable icon;
	/**
    * 应用程序的名称
    */
	private String apkName;
	/**
	 * 应用程序的路径
	 */
	private String apkPath;
	/**
	 * 应用程序的大小
	 */
	private long appSize;
	/**
	 * 应用程序的位置，手机内存中或者外部存储中
	 */
	private boolean inrom;
	/**
	 * 是否是用户程序，true是用户程序，false为系统程序
	 */
	private boolean userApp;
	/**
	 * 应用程序的包名
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
