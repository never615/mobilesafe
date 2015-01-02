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

		// ��ȡ�ֻ����е�����˿� �� wifi �� 2g/3g/4g(�ֻ���������������
		TrafficStats.getTotalRxBytes(); // r -->receive���գ���ȡȫ���Ľ��ܵ�byte �����أ�
		TrafficStats.getTotalTxBytes(); // t -->translate���ͣ���ȡȫ���ķ��͵�byte ���ϴ���

		TrafficStats.getMobileRxBytes();// �ֻ������ز��������� �ӿ�����ʼ�����ڲ���������
		TrafficStats.getMobileTxBytes();// �ֻ����ϴ�����������

		// ��Androidϵͳ���� ����ÿһ��Ӧ�ó��򶼷�����һ���û�id
		// pid:����id uid:�û�id
		TrafficStats.getUidRxBytes(10014);
		TrafficStats.getUidTxBytes(10014);

		// �ֱ��г���3g/wifi�����˶��ٵ�����
		// �����ϵ�Ӧ��ͨ����ʱ�����ϼ������״̬��
		//ͨ�����·����������״̬
		ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		cm.getActiveNetworkInfo().getType();

		// �Զ�У������ �� ͵͵��̨����Ӫ�̷��Ͷ��š�
		// 10010 10086 llcx

		// ��������
		// linuxƽ̨�ķ���ǽ����������ֻ�Ҫ��rootȨ�ޣ��޸�iptable

		setContentView(R.layout.activity_traffic_manager);

	}
}
