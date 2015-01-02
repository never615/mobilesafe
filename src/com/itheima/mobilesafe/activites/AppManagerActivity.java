package com.itheima.mobilesafe.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoParser;
import com.itheima.mobilesafe.utils.DensityUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stericson.RootTools.RootTools;

public class AppManagerActivity extends Activity implements OnClickListener {
	private static final String TAG = "AppManagerActivity";
	@ViewInject(R.id.tv_avail_rom)
	private TextView tv_avail_rom;
	@ViewInject(R.id.tv_avail_sd)
	private TextView tv_avail_sd;
	@ViewInject(R.id.ll_appmanager_loading)
	private LinearLayout ll_appmanager_loading;
	@ViewInject(R.id.lv_appmanager)
	private ListView lv_appmanager;
	@ViewInject(R.id.tv_appmanager_number)
	private TextView tv_appmanager_number;

	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;

	private AppInfo clickAppInfo;// 点击条目对应的appInfo
	private PopupWindow popup; // 悬浮窗体
	
	private UninstallReceiver receiver;

	/**
	 * 消息处理器
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_appmanager_loading.setVisibility(View.INVISIBLE);

			lv_appmanager.setAdapter(new AppManagerAdapter());

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_appmanager);
		ViewUtils.inject(this);

		// 显示可用空间
		showAvailSpace();
		// 填充数据的业务方法
		fillDate();

		initUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
		
		unregisterReceiver(receiver);
		receiver=null;
	}

	/**
	 * 初始化UI逻辑模块
	 */
	private void initUI() {

		// 设置滚轮的监听事件 为了控制tv_appmanager_number中显示内容的改变（用户程序或者系统程序数量）
		lv_appmanager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// 当listview滚动的时候调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null & systemAppInfos != null) { // 因为填充数据在子线程中完成的，所以一开始运行的时候可能还是空的，要做判断
					if (firstVisibleItem >= (userAppInfos.size() + 1)) {
						tv_appmanager_number.setText("系统程序："
								+ systemAppInfos.size() + "个");
					} else {
						tv_appmanager_number.setText("用户程序："
								+ userAppInfos.size() + "个");
					}
				}
			}
		});

		// 设置listview 条目的点击事件 实现气泡弹框效果
		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object obj = lv_appmanager.getItemAtPosition(position); // 调用的返回值就是适配器中的getItem

				if (obj != null & obj instanceof AppInfo) {
					clickAppInfo = (AppInfo) obj;
					dismissPopupWindow();
					View contentView = View.inflate(getApplicationContext(),
							R.layout.popup_item, null);
					contentView.findViewById(R.id.ll_uninstall)
							.setOnClickListener(AppManagerActivity.this);
					contentView.findViewById(R.id.ll_share).setOnClickListener(
							AppManagerActivity.this);
					contentView.findViewById(R.id.ll_start).setOnClickListener(
							AppManagerActivity.this);
					contentView.findViewById(R.id.ll_setting)
							.setOnClickListener(AppManagerActivity.this);

					popup = new PopupWindow(contentView, -2, -2);
					// 动画播放有一个前提条件： 窗体必须要有背景资源。 如果窗体没有背景，动画就播放不出来。
					popup.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));


					// 拿到点击的条目在窗体中的位置 x ，y ，通过int数组接受
					int[] location = new int[2];
					view.getLocationInWindow(location);
					// 设置悬浮窗体的显示位置
					
					int dp=50;
					
					int px=DensityUtil.dip2px(getApplicationContext(), dp);
					
					popup.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
							px, location[1]);

					// 气泡弹出时的动画
					ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f,
							1.0f, Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0.5f);
					sa.setDuration(200);
					AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
					aa.setDuration(200);
					AnimationSet set = new AnimationSet(false);
					set.addAnimation(aa);
					set.addAnimation(sa);
					contentView.startAnimation(set);

				}
			}
		});
		
		/**
		 * 卸载应用的广播接收者 为了应用程序列表界面在发生卸载之后及时刷新
		 */
		receiver=new UninstallReceiver();
		IntentFilter filter=new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
		
	}

	/**
	 * 关闭悬浮窗体的方法
	 */
	private void dismissPopupWindow() {
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
			popup = null;
		}
	}

	/**
	 * 填充数据的业务方法
	 */
	private void fillDate() {
		ll_appmanager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoParser.getAppInfos(getApplicationContext());

				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
				}

				handler.sendEmptyMessage(0);
			};
		}.start();

	}

	/**
	 * listview的适配器
	 * 
	 * @author rong
	 * 
	 */
	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			AppInfo appInfo;

			// 两个textView，用来显示程序数量
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("用户程序：" + userAppInfos.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}

			if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("系统程序：" + systemAppInfos.size() + "个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}

			// 得到程序信息
			if (position < userAppInfos.size() + 1) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos
						.get(position - userAppInfos.size() - 2);
			}

			// listview 效率优化处理
			if (convertView != null & convertView instanceof LinearLayout) {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.item_app_manager, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				viewHolder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);
				viewHolder.tv_app_size = (TextView) view
						.findViewById(R.id.tv_app_size);
				viewHolder.tv_app_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				view.setTag(viewHolder);
			}

			viewHolder.iv_app_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_app_name.setText(appInfo.getApkName());
			viewHolder.tv_app_size.setText(Formatter.formatFileSize(
					getApplicationContext(), appInfo.getAppSize()));
			if (appInfo.isInrom()) {
				viewHolder.tv_app_location.setText("手机内存");
			} else {
				viewHolder.tv_app_location.setText("外部存储");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			}
			if (position == userAppInfos.size() + 1) {
				return null;
			}
			if (position < userAppInfos.size() + 1) {
				return userAppInfos.get(position - 1);
			} else {
				return systemAppInfos.get(position - 2 - userAppInfos.size());
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	/**
	 * listview返回条目的家庭组容器，用来提高效率
	 * 
	 * @author rong
	 * 
	 */
	private class ViewHolder {
		ImageView iv_app_icon;
		TextView tv_app_name;
		TextView tv_app_size;
		TextView tv_app_location;
	}

	/**
	 * 在页面上显示出剩余可用空间
	 */
	private void showAvailSpace() {
		long romFree = Environment.getDataDirectory().getFreeSpace();
		long sdFree = Environment.getExternalStorageDirectory().getFreeSpace();

		String str_romFree = Formatter.formatFileSize(this, romFree); // 格式化方法，把long数据变成使用MB,KB,GB为单位的形式
		String str_sdFree = Formatter.formatFileSize(this, sdFree);

		tv_avail_rom.setText("内部剩余空间：" + str_romFree);
		tv_avail_sd.setText("sd卡剩余空间：" + str_sdFree);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_setting:
			viewAppDetail();
			Log.i(TAG, "设置：" + clickAppInfo.getPackName());
			break;
		case R.id.ll_share:
			shareApplication();
			break;
		case R.id.ll_start:
			startAppication();
			break;
		case R.id.ll_uninstall:
			uninstallApplication();
			break;
		}

		dismissPopupWindow();
	}

	/**
	 * 分享应用
	 */
	private void shareApplication() {
		/**
		 * 一种带内容跳转的发短信界面的方法
		 */
		/**
		 * String android.intent.action.VIEW
		 * 
		 * 用于显示用户的数据。比较通用，会根据用户的数据类型打开相应的Activity。 比如
		 * tel:13400010001打开拨号程序，http://www.g.cn则会打开浏览器等。
		 */
		/*
		 * Intent it = new Intent(Intent. ACTION_VIEW); it.putExtra("sms_body",
		 * "推荐您使用一款软件，名称叫：" + clickAppInfo.getApkName() +
		 * "下载路径：https://play.google.com/store/apps/details?id=" +
		 * clickAppInfo.getPackName()); it.setType("vnd.android-dir/mms-sms");
		 * startActivity(it);
		 */

		// 另一种
		Intent intent = new Intent("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"推荐您使用一款软件，名称叫：" + clickAppInfo.getApkName()
						+ "下载路径：https://play.google.com/store/apps/details?id="
						+ clickAppInfo.getPackName());
		startActivity(intent);
	}

	/**
	 * 开启应用程序
	 */
	private void startAppication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm
				.getLaunchIntentForPackage(clickAppInfo.getPackName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "该应用没有启动界面", 0).show();
		}
	}

	/**
	 * 卸载应用
	 */
	private void uninstallApplication() {
		if (clickAppInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:" + clickAppInfo.getPackName()));
			startActivity(intent);
		} else {
			// 系统应用 卸载 需要 root权限 利用linux命令删除文件
			if (RootTools.isRootAvailable()) {
				Toast.makeText(this, "卸载系统应用，必须要root权限", 0).show();
				return;
			}
			try {
				if (RootTools.isAccessGiven()) {
					Toast.makeText(this, "卸载系统应用需要同意root权限", 0).show();
					return;
				}
				RootTools.sendShell("mount -o remount , rw /system", 3000);
				RootTools.sendShell("rm -r" + clickAppInfo.getApkPath(), 30000);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跳转到应用的详细页面
	 */
	private void viewAppDetail() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		// intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		// intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setData(Uri.parse("package:" + clickAppInfo.getPackName()));
		startActivity(intent);
	}
	
	/**
	 * 卸载应用的广播接收者
	 * @author rong
	 *
	 */
	private class UninstallReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String info=intent.getDataString();
			System.out.println("info:"+info);
			fillDate();
		}
	}
}
