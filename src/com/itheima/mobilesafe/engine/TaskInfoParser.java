package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.TaskInfo;

/**
 * ������Ϣ&���̽�����
 * 
 * @author rong
 * 
 */
public class TaskInfoParser {

	/***
	 * ��ȡ�����������еĽ�����Ϣ
	 * 
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getRunningTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		TaskInfo taskInfo;
		List<RunningAppProcessInfo> runningAppProcessInfos = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runnningAppProcessInfo : runningAppProcessInfos) {
			taskInfo = new TaskInfo();
			String packName = runnningAppProcessInfo.processName;
			MemoryInfo[] memoryInfo = am
					.getProcessMemoryInfo(new int[] { runnningAppProcessInfo.pid });
			long memSize = memoryInfo[0].getTotalPrivateDirty() * 1024;

			taskInfo.setPackname(packName);
			taskInfo.setMemsize(memSize);
			try {
				PackageInfo packInfo = pm.getPackageInfo(packName, 0);
				String appName = packInfo.applicationInfo.loadLabel(pm)
						.toString();
				Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);

				taskInfo.setIcon(appIcon);
				taskInfo.setAppname(appName);

				if ((ApplicationInfo.FLAG_SYSTEM & packInfo.applicationInfo.flags) != 0) {
					// ϵͳ����
					taskInfo.setUsertask(false);
				} else {
					// �û�����
					taskInfo.setUsertask(true);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				// ��һЩ������û��appName��ͼ��ģ����Ծ���ʾ������Ĭ��ͼ��
				taskInfo.setAppname(packName);
				taskInfo.setIcon(context.getResources().getDrawable(
						R.drawable.ic_launcher));
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
