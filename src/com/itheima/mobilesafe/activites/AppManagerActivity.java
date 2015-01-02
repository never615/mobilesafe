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

	private AppInfo clickAppInfo;// �����Ŀ��Ӧ��appInfo
	private PopupWindow popup; // ��������
	
	private UninstallReceiver receiver;

	/**
	 * ��Ϣ������
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

		// ��ʾ���ÿռ�
		showAvailSpace();
		// ������ݵ�ҵ�񷽷�
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
	 * ��ʼ��UI�߼�ģ��
	 */
	private void initUI() {

		// ���ù��ֵļ����¼� Ϊ�˿���tv_appmanager_number����ʾ���ݵĸı䣨�û��������ϵͳ����������
		lv_appmanager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// ��listview������ʱ����õķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (userAppInfos != null & systemAppInfos != null) { // ��Ϊ������������߳�����ɵģ�����һ��ʼ���е�ʱ����ܻ��ǿյģ�Ҫ���ж�
					if (firstVisibleItem >= (userAppInfos.size() + 1)) {
						tv_appmanager_number.setText("ϵͳ����"
								+ systemAppInfos.size() + "��");
					} else {
						tv_appmanager_number.setText("�û�����"
								+ userAppInfos.size() + "��");
					}
				}
			}
		});

		// ����listview ��Ŀ�ĵ���¼� ʵ�����ݵ���Ч��
		lv_appmanager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object obj = lv_appmanager.getItemAtPosition(position); // ���õķ���ֵ�����������е�getItem

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
					// ����������һ��ǰ�������� �������Ҫ�б�����Դ�� �������û�б����������Ͳ��Ų�������
					popup.setBackgroundDrawable(new ColorDrawable(
							Color.TRANSPARENT));


					// �õ��������Ŀ�ڴ����е�λ�� x ��y ��ͨ��int�������
					int[] location = new int[2];
					view.getLocationInWindow(location);
					// ���������������ʾλ��
					
					int dp=50;
					
					int px=DensityUtil.dip2px(getApplicationContext(), dp);
					
					popup.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
							px, location[1]);

					// ���ݵ���ʱ�Ķ���
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
		 * ж��Ӧ�õĹ㲥������ Ϊ��Ӧ�ó����б�����ڷ���ж��֮��ʱˢ��
		 */
		receiver=new UninstallReceiver();
		IntentFilter filter=new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
		
	}

	/**
	 * �ر���������ķ���
	 */
	private void dismissPopupWindow() {
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
			popup = null;
		}
	}

	/**
	 * ������ݵ�ҵ�񷽷�
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
	 * listview��������
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

			// ����textView��������ʾ��������
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("�û�����" + userAppInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}

			if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setText("ϵͳ����" + systemAppInfos.size() + "��");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}

			// �õ�������Ϣ
			if (position < userAppInfos.size() + 1) {
				appInfo = userAppInfos.get(position - 1);
			} else {
				appInfo = systemAppInfos
						.get(position - userAppInfos.size() - 2);
			}

			// listview Ч���Ż�����
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
				viewHolder.tv_app_location.setText("�ֻ��ڴ�");
			} else {
				viewHolder.tv_app_location.setText("�ⲿ�洢");
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
	 * listview������Ŀ�ļ�ͥ���������������Ч��
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
	 * ��ҳ������ʾ��ʣ����ÿռ�
	 */
	private void showAvailSpace() {
		long romFree = Environment.getDataDirectory().getFreeSpace();
		long sdFree = Environment.getExternalStorageDirectory().getFreeSpace();

		String str_romFree = Formatter.formatFileSize(this, romFree); // ��ʽ����������long���ݱ��ʹ��MB,KB,GBΪ��λ����ʽ
		String str_sdFree = Formatter.formatFileSize(this, sdFree);

		tv_avail_rom.setText("�ڲ�ʣ��ռ䣺" + str_romFree);
		tv_avail_sd.setText("sd��ʣ��ռ䣺" + str_sdFree);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_setting:
			viewAppDetail();
			Log.i(TAG, "���ã�" + clickAppInfo.getPackName());
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
	 * ����Ӧ��
	 */
	private void shareApplication() {
		/**
		 * һ�ִ�������ת�ķ����Ž���ķ���
		 */
		/**
		 * String android.intent.action.VIEW
		 * 
		 * ������ʾ�û������ݡ��Ƚ�ͨ�ã�������û����������ʹ���Ӧ��Activity�� ����
		 * tel:13400010001�򿪲��ų���http://www.g.cn����������ȡ�
		 */
		/*
		 * Intent it = new Intent(Intent. ACTION_VIEW); it.putExtra("sms_body",
		 * "�Ƽ���ʹ��һ����������ƽУ�" + clickAppInfo.getApkName() +
		 * "����·����https://play.google.com/store/apps/details?id=" +
		 * clickAppInfo.getPackName()); it.setType("vnd.android-dir/mms-sms");
		 * startActivity(it);
		 */

		// ��һ��
		Intent intent = new Intent("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"�Ƽ���ʹ��һ����������ƽУ�" + clickAppInfo.getApkName()
						+ "����·����https://play.google.com/store/apps/details?id="
						+ clickAppInfo.getPackName());
		startActivity(intent);
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void startAppication() {
		PackageManager pm = getPackageManager();
		Intent intent = pm
				.getLaunchIntentForPackage(clickAppInfo.getPackName());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, "��Ӧ��û����������", 0).show();
		}
	}

	/**
	 * ж��Ӧ��
	 */
	private void uninstallApplication() {
		if (clickAppInfo.isUserApp()) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:" + clickAppInfo.getPackName()));
			startActivity(intent);
		} else {
			// ϵͳӦ�� ж�� ��Ҫ rootȨ�� ����linux����ɾ���ļ�
			if (RootTools.isRootAvailable()) {
				Toast.makeText(this, "ж��ϵͳӦ�ã�����ҪrootȨ��", 0).show();
				return;
			}
			try {
				if (RootTools.isAccessGiven()) {
					Toast.makeText(this, "ж��ϵͳӦ����Ҫͬ��rootȨ��", 0).show();
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
	 * ��ת��Ӧ�õ���ϸҳ��
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
	 * ж��Ӧ�õĹ㲥������
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
