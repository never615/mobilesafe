package com.itheima.mobilesafe.activites;

import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.AntivirusDao;
import com.itheima.mobilesafe.utils.MD5Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AntivirusActivity extends Activity {

	private static final int ISVIRUS = 0;
	private static final int NOVIRUS = 1;
	private static final int FINISH = 2;
	@ViewInject(R.id.iv_scan)
	private ImageView iv_scan;
	@ViewInject(R.id.tv_scan_status)
	private TextView tv_scan_status;
	@ViewInject(R.id.pb_scan)
	private ProgressBar pb_scan;
	@ViewInject(R.id.ll_container)
	private LinearLayout ll_container;

	private PackageManager pm;
	private List<PackageInfo> infos;
	private AntivirusDao dao;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ISVIRUS:
				tv_scan_status.setText("正在扫描");
				PackageInfo packinfo = (PackageInfo) msg.obj;
				TextView tv1 = new TextView(getApplicationContext());
				tv1.setTextColor(Color.RED);
				tv1.setText("发现病毒：" + packinfo.applicationInfo.loadLabel(pm));
				ll_container.addView(tv1, 0);
				break;
			case NOVIRUS:
				tv_scan_status.setText("正在扫描");
				PackageInfo packinfo2 = (PackageInfo) msg.obj;
				TextView tv2 = new TextView(getApplicationContext());
				tv2.setTextColor(Color.GREEN);
				tv2.setText("扫描安全：" + packinfo2.applicationInfo.loadLabel(pm));
				ll_container.addView(tv2, 0);
				break;
			case FINISH:
				iv_scan.clearAnimation();
				tv_scan_status.setText("扫描完毕");
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();
		dao = new AntivirusDao();
		pm=getPackageManager();
		scan();
	}

	/**
	 * 扫描病毒
	 */
	private void scan() {
		new Thread() {
			public void run() {
				// 获取手机上所有应用 通过添加flag，把那些没卸载干净的，还保留数据的全部找到
				infos = pm
						.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				pb_scan.setMax(infos.size());
				int process = 0;
				for (PackageInfo info : infos) {
					String MD5 = MD5Utils
							.getMD5(info.applicationInfo.sourceDir);
					String desc = dao.find(MD5, AntivirusActivity.this);
					Message msg = Message.obtain();
					if (desc != null) {
						// 是病毒
						msg.what = ISVIRUS;
					} else {
						// 不是病毒
						msg.what = NOVIRUS;
					}
					msg.obj = info;
					handler.sendMessage(msg);

					process++;
					pb_scan.setProgress(process);

					SystemClock.sleep(30);
				}
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 初始化 UI界面
	 */
	private void initUI() {
		setContentView(R.layout.activity_antivirus);
		ViewUtils.inject(this);

		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		ra.setRepeatCount(Animation.INFINITE);
		ra.setDuration(2000);
		ra.setInterpolator(new Interpolator() {

			@Override
			public float getInterpolation(float input) {

				return input;
			}
		});
		iv_scan.setAnimation(ra);
	}
}
