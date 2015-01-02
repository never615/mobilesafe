package com.itheima.mobilesafe.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.TaskInfo;
import com.itheima.mobilesafe.engine.TaskInfoParser;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class TaskManagerActivity extends Activity {

	@ViewInject(R.id.ll_taskmanager_loading)
	private LinearLayout ll_taskmanager_loading;
	@ViewInject(R.id.tv_ram_info)
	private TextView tv_ram_info;
	@ViewInject(R.id.tv_run_app)
	private TextView tv_run_app;
	@ViewInject(R.id.lv_taskmanager)
	private ListView lv_taskmanager;

	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> sysmTaskInfos;

	private TaskManagerAdapter adapter;

	private long availRam;
	private SharedPreferences sp;

	/**
	 * �����еĳ�������
	 */
	private int runningProcessCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		ViewUtils.inject(this);
		init();
		fillDate();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * ���listview������
	 */
	private void fillDate() {

		new Thread() {
			public void run() {
				ll_taskmanager_loading.setVisibility(View.VISIBLE);
				taskInfos = TaskInfoParser
						.getRunningTaskInfos(getApplicationContext());

				userTaskInfos = new ArrayList<TaskInfo>();
				sysmTaskInfos = new ArrayList<TaskInfo>();

				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUsertask()) {
						// ���û�����
						userTaskInfos.add(taskInfo);
					} else {
						// ��ϵͳ����
						sysmTaskInfos.add(taskInfo);
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						ll_taskmanager_loading.setVisibility(View.INVISIBLE);
						adapter = new TaskManagerAdapter();
						lv_taskmanager.setAdapter(adapter);
					}
				});
			};
		}.start();
	}

	private void init() {
		sp=getSharedPreferences("config", MODE_PRIVATE);
		
		String totalSize = Formatter.formatFileSize(this,
				SystemInfoUtils.getTotalMem());
		runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);

		availRam = SystemInfoUtils.getAvailMem(this);
		String str_availRam = Formatter.formatFileSize(this, availRam);
		tv_run_app.setText("�����н��̣�" + runningProcessCount + "��");
		tv_ram_info.setText("����/���ڴ棺" + str_availRam + "/" + totalSize);

		// ������Ŀ�ĵ���¼�
		lv_taskmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_taskmanager.getItemAtPosition(position);
				if (obj != null && obj instanceof TaskInfo) {
					TaskInfo info = (TaskInfo) obj;
					if (info.getPackname().equals(getPackageName())) {
						// ���������Լ��������õ���¼� ֱ�ӷ���
						return;
					}

					ViewHolder holder = (ViewHolder) view.getTag();
					if (info.isChecked()) {
						holder.cb_status.setChecked(false);
						info.setChecked(false);
					} else {
						holder.cb_status.setChecked(true);
						info.setChecked(true);
					}
				}
			}
		});

	}

	/**
	 * listview ��������
	 * 
	 * @author rong
	 * 
	 */
	private class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if(sp.getBoolean("showsystem", false)){
				return userTaskInfos.size() + 1 + sysmTaskInfos.size() + 1;
			}else{
				return userTaskInfos.size() + 1;
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			ViewHolder holder;
			TaskInfo info = new TaskInfo();

			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("�û����̣�" + userTaskInfos.size() + "��");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("ϵͳ���̣�" + sysmTaskInfos.size() + "��");
				return tv;
			} else if (position <= userTaskInfos.size()) {
				info = userTaskInfos.get(position - 1);
			} else {
				info = sysmTaskInfos.get(position - userTaskInfos.size() - 2);
			}

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_task_manager, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.tv_size = (TextView) view
						.findViewById(R.id.tv_task_size);
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_task_icon);
				holder.cb_status = (CheckBox) view
						.findViewById(R.id.cb_task_status);
				view.setTag(holder);
			}

			holder.tv_name.setText(info.getAppname());
			holder.tv_size.setText("ռ���ڴ棺"
					+ Formatter.formatFileSize(getApplicationContext(),
							info.getMemsize()));
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.cb_status.setChecked(info.isChecked());

			// ���ص�ʱ��������Լ�������checkbox���ɼ�
			if (info.getPackname().equals(getPackageName())) {
				// ���������Լ���
				holder.cb_status.setVisibility(View.INVISIBLE);
				//TODO  ����listview ���ض��� view ������
				// ����˵Ӧ�����ó�false�Ŷԣ��������true���Ǹ���Ŀ�Ͳ��ܵ���ˣ�������ܷ��ص�view������Ŀ���ӿؼ�
				view.setClickable(true);
			} else {
				holder.cb_status.setVisibility(View.VISIBLE);
				view.setClickable(false);
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			TaskInfo info = new TaskInfo();
			if (position == 0) {
				return null;
			} else if (position == (userTaskInfos.size() + 1)) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				info = userTaskInfos.get(position - 1);
			} else {
				info = sysmTaskInfos.get(position - userTaskInfos.size() - 2);
			}
			return info;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	/**
	 * ��ͥ������
	 * 
	 * @author rong
	 * 
	 */
	private static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_size;
		CheckBox cb_status;
	}

	/**
	 * �������
	 * 
	 * @param view
	 */
	public void killProcess(View view) {

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long mem = 0;

		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		// �ڱ������ϵ�ʱ���ܸı伯�ϵĴ�С�����Ա������ϴ浽һ���¼��ϣ�Ȼ�� ����������ϴӽ��漯����remove��
		for (TaskInfo info : userTaskInfos) {
			if (info.isChecked()) {
				count++;
				mem += info.getMemsize();
				am.killBackgroundProcesses(info.getPackname());
				killedTaskInfos.add(info);
			}
		}
		for (TaskInfo info : sysmTaskInfos) {
			if(info.isChecked()){
				count++;
				mem += info.getMemsize();
				am.killBackgroundProcesses(info.getPackname());
				killedTaskInfos.add(info);
			}
		}
		for (TaskInfo info : killedTaskInfos) {
			if (info.isUsertask()) {
				userTaskInfos.remove(info);
			} else {
				sysmTaskInfos.remove(info);
			}
		}

		runningProcessCount -= count;
		availRam += mem;
		// �ı�������
		tv_run_app.setText("�����еĽ��̣�"+runningProcessCount+"��");
		tv_ram_info.setText("����/���ڴ棺"+Formatter.formatFileSize(this, availRam)+"/"+Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem()));
		
		Toast.makeText(
				this,
				"ɱ����" + count + "������,�ͷ���"
						+ Formatter.formatFileSize(this, mem) + "���ڴ�", 1)
				.show();
		
		adapter.notifyDataSetChanged();
	}

	/**
	 * ȫѡ
	 * 
	 * @param view
	 */
	public void selectAll(View view) {

		for (TaskInfo info : userTaskInfos) {
			if(info.getPackname().equals(getPackageName())){
				continue;
			}
			info.setChecked(true);
		}
		for (TaskInfo info : sysmTaskInfos) {
			info.setChecked(true);
		}
		
		// Ȼ��֪ͨ����ˢ��
		adapter.notifyDataSetChanged();
	}

	/**
	 * ��ѡ
	 * 
	 * @param view
	 */
	public void selectOpposite(View view) {
		for (TaskInfo info : userTaskInfos) {
			if (info.getPackname().equals(getPackageName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		for (TaskInfo info : sysmTaskInfos) {
			info.setChecked(!info.isChecked());
		}
		// ֪ͨ�������
		adapter.notifyDataSetChanged();
	}

	/**
	 * ����
	 * @param view
	 */
	public void openSetting(View view) {
		Intent intent=new Intent(this,TaskManagerSettingActivity.class);
		startActivity(intent);
	}
}
