package com.itheima.mobilesafe.activites;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;

import com.itheima.mobilesafe.R;

public class TrafficManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获取手机所有的网络端口 ， wifi ， 2g/3g/4g(手机卡产生的流量）
		TrafficStats.getTotalRxBytes(); // r -->receive接收，获取全部的接受的byte （下载）
		TrafficStats.getTotalTxBytes(); // t -->translate发送，获取全部的发送的byte （上传）

		TrafficStats.getMobileRxBytes();// 手机卡下载产生的流量 从开机开始到现在产出的流量
		TrafficStats.getMobileTxBytes();// 手机卡上传产生的流量

		// 在Android系统里面 ，给每一个应用程序都分配了一个用户id
		// pid:进程id uid:用户id
		TrafficStats.getUidRxBytes(10014);
		TrafficStats.getUidTxBytes(10014);

		// 分别列出来3g/wifi产生了多少的流量
		// 市面上的应用通过计时器不断检查网络状态。
		//通过以下方法检查网络状态
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		cm.getActiveNetworkInfo().getType();

		// 自动校验流量 ， 偷偷后台给运营商发送短信。
		// 10010 10086 llcx

		// 联网禁用
		// linux平台的防火墙软件。必须手机要有root权限，修改iptable

		setContentView(R.layout.activity_traffic_manager);

	}
}
