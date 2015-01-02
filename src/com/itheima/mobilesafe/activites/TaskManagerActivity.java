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
	 * 运行中的程序数量
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
	 * 填充listview的数据
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
						// 是用户进程
						userTaskInfos.add(taskInfo);
					} else {
						// 是系统进程
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
		tv_run_app.setText("运行中进程：" + runningProcessCount + "个");
		tv_ram_info.setText("可用/总内存：" + str_availRam + "/" + totalSize);

		// 设置条目的点击事件
		lv_taskmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object obj = lv_taskmanager.getItemAtPosition(position);
				if (obj != null && obj instanceof TaskInfo) {
					TaskInfo info = (TaskInfo) obj;
					if (info.getPackname().equals(getPackageName())) {
						// 就是我们自己，不设置点击事件 直接返回
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
	 * listview 的适配器
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
				tv.setText("用户进程：" + userTaskInfos.size() + "个");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统进程：" + sysmTaskInfos.size() + "个");
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
			holder.tv_size.setText("占用内存："
					+ Formatter.formatFileSize(getApplicationContext(),
							info.getMemsize()));
			holder.iv_icon.setImageDrawable(info.getIcon());
			holder.cb_status.setChecked(info.isChecked());

			// 返回的时候如果是自己，设置checkbox不可见
			if (info.getPackname().equals(getPackageName())) {
				// 就是我们自己。
				holder.cb_status.setVisibility(View.INVISIBLE);
				//TODO  关于listview 返回对象 view 的问题
				// 按理说应该设置成false才对，但是设成true，那个条目就不能点击了，猜想可能返回的view是子条目的子控件
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
	 * 家庭组容器
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
	 * 清理进程
	 * 
	 * @param view
	 */
	public void killProcess(View view) {

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long mem = 0;

		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		// 在遍历集合的时候不能改变集合的大小，所以遍历集合存到一个新集合，然后 遍历这个集合从界面集合中remove掉
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
		// 改标题内容
		tv_run_app.setText("运行中的进程："+runningProcessCount+"个");
		tv_ram_info.setText("可用/总内存："+Formatter.formatFileSize(this, availRam)+"/"+Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem()));
		
		Toast.makeText(
				this,
				"杀死了" + count + "个进程,释放了"
						+ Formatter.formatFileSize(this, mem) + "的内存", 1)
				.show();
		
		adapter.notifyDataSetChanged();
	}

	/**
	 * 全选
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
		
		// 然后通知界面刷新
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选
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
		// 通知界面更新
		adapter.notifyDataSetChanged();
	}

	/**
	 * 设置
	 * @param view
	 */
	public void openSetting(View view) {
		Intent intent=new Intent(this,TaskManagerSettingActivity.class);
		startActivity(intent);
	}
}
