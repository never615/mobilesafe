package com.itheima.mobilesafe.activites;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CleanCacheActivity extends Activity {

	protected static final int SCANNING = 1;
	public static final int ADDVIEW = 2;
	protected static final int FINISHED = 3;
	private PackageManager pm;
	@ViewInject(R.id.pb_scan)
	private ProgressBar pb_scan;
	@ViewInject(R.id.tv_scan_result)
	private TextView tv_scan_result;
	@ViewInject(R.id.ll_container)
	private LinearLayout ll_container;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SCANNING:
				PackageInfo info = (PackageInfo) msg.obj;
				tv_scan_result.setText(info.applicationInfo.loadLabel(pm));
				break;
			case FINISHED:
				tv_scan_result.setText("ɨ�����");
				break;
			case ADDVIEW:
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;

				View view = View.inflate(getApplicationContext(),
						R.layout.item_cache_info, null);
				TextView tv_cacheinfo_name = (TextView) view
						.findViewById(R.id.tv_cacheinfo_name);
				TextView tv_cacheinfo_size = (TextView) view
						.findViewById(R.id.tv_cacheinfo_size);
				ImageView iv_cacheinfo_icon = (ImageView) view
						.findViewById(R.id.iv_cacheinfo_icon);

				tv_cacheinfo_name.setText(cacheInfo.getAppName());
				tv_cacheinfo_size.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheInfo.getCacheSize()));
				iv_cacheinfo_icon.setImageDrawable(cacheInfo.getIcon());

				ll_container.addView(view, 0);
				// ����ÿ����Ŀ�ĵ���¼�
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// �����Ŀ������ �������û��rootȨ�� ����ϵͳ������������ֱ������ֻ����ת��Ӧ�ý��棬�ֶ�ɾ��
						Uri packageURI = Uri.parse("package:"
								+ cacheInfo.getPackName());
						Intent intent = new Intent();
						intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
						intent.setData(packageURI);
						startActivity(intent);

						// ϵͳ����Ӧ�ÿ���ֱ��ɾ��Ӧ�û��棬��ϵͳӦ���и�Ȩ�޲������

						// pm.deleteApplicationCacheFiles(packageName,mClearCacheObserver);  �������

					}
				});
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_clean_cache);
		ViewUtils.inject(this);

		pm = getPackageManager();

		scan();

	}

	/**
	 * ɨ�����е�Ӧ��
	 */
	private void scan() {

		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb_scan.setMax(infos.size());
				int process = 0;
				Message msg;
				SystemClock.sleep(500);
				for (PackageInfo info : infos) {
					getCacheInfo(info);
					process++;
					pb_scan.setProgress(process);

					// ������Ϣ���������֣�������
					msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = info;
					handler.sendMessage(msg);
					SystemClock.sleep(100);
				}
				msg = Message.obtain();
				msg.what = FINISHED;
				handler.sendMessage(msg);

			};
		}.start();

	}

	/**
	 * ɨ�����еİ���������ǵĻ�����Ϣ
	 */
	private void getCacheInfo(PackageInfo info) {

		Class clazz = PackageManager.class;
		try {
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			method.invoke(pm, info.packageName, new myOberver(info));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class myOberver extends
			android.content.pm.IPackageStatsObserver.Stub {

		PackageInfo packinfo;
		CacheInfo cacheInfo = new CacheInfo();

		public myOberver(PackageInfo packinfo) {
			super();
			this.packinfo = packinfo;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException { // �����������ֱ�����������߳��У����Բ���ֱ�Ӹ���UI
			long size = pStats.cacheSize;
			/*
			 * System.out.println(packinfo.applicationInfo.loadLabel(pm) +
			 * ",�����СΪ��" + Formatter.formatFileSize(getApplicationContext(),
			 * size));
			 */
			// ��������л��棬�������Ŀ��������ʾ ͼ�� ���� �����С

			// �л���
			if (size > 0) {
				String appName = packinfo.applicationInfo.loadLabel(pm)
						.toString();
				Drawable appIcon = packinfo.applicationInfo.loadIcon(pm);
				String packName = packinfo.packageName;

				cacheInfo.setAppName(appName);
				cacheInfo.setIcon(appIcon);
				cacheInfo.setCacheSize(size);
				cacheInfo.setPackName(packName);

				// Ȼ�����Щ��Ϣ��ʾ������
				Message msg = Message.obtain();
				msg.what = ADDVIEW;
				msg.obj = cacheInfo;
				handler.sendMessage(msg);

			}
		}

	}

	/**
	 * ������Ϣ
	 * 
	 * @author rong
	 * 
	 */
	private class CacheInfo {
		private Drawable icon;
		private String appName;
		private long cacheSize;
		private String packName;

		public String getPackName() {
			return packName;
		}

		public void setPackName(String packName) {
			this.packName = packName;
		}

		public Drawable getIcon() {
			return icon;
		}

		public void setIcon(Drawable icon) {
			this.icon = icon;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public long getCacheSize() {
			return cacheSize;
		}

		public void setCacheSize(long cacheSize) {
			this.cacheSize = cacheSize;
		}

		@Override
		public String toString() {
			return "CacheInfo [appName=" + appName + ", cacheSize=" + cacheSize
					+ "]";
		}

	}

	/**
	 * ����ȫ��
	 * 
	 * @param view
	 */
	
	public void cleanAll(View view) {
		
		try {
			Method method=PackageManager.class.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
			method.invoke(pm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
				
				@Override
				public void onRemoveCompleted(String packageName, boolean succeeded)
						throws RemoteException {
					//�Ƴ���ɻص�����  succeeded�����Ƿ�ɹ�
					System.out.println("result:"+succeeded);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * ����ȫ���Ļ���
	 * @param view
	 */
	/*public void cleanAll(View view){
		Method[]  methods= PackageManager.class.getMethods();
		for(Method method : methods){
			if("freeStorageAndNotify".equals(method.getName())){
				try {
					method.invoke(pm, Integer.MAX_VALUE,new IPackageDataObserver.Stub() {
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded)
								throws RemoteException {
							System.out.println("result:"+succeeded);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
	}*/
}
