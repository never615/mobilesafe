package com.itheima.mobilesafe.activites;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.StreamUtils;
import com.itheima.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SplashActivity extends Activity {
	protected static final int MSG_UPDATE = 00;
	protected static final int MSG_ENTER_HOME = 10;
	protected static final int MSG_URL_ERROR = 20;
	protected static final int MSG_IO_ERROR = 30;
	protected static final int MSG_JSON_ERROR = 40;
	protected static final int MSG_SERVER_ERROR = 50;
	@ViewInject(R.id.tv_splash_version)
	private TextView tv_splash_version;
	@ViewInject(R.id.tv_splash_process)
	private TextView tv_splash_process;
	private SharedPreferences sp;

	private int clientVersionCode;

	private String des;
	private String apkurl;

	/**
	 * handler
	 */
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_UPDATE:
				showUpdateDialog();
				break;
			case MSG_ENTER_HOME:
				enterHome();
				break;
			case MSG_URL_ERROR:
				Toast.makeText(getApplicationContext(), "错误号:" + MSG_URL_ERROR,
						0).show();
				enterHome();
				break;
			case MSG_IO_ERROR:
				Toast.makeText(getApplicationContext(), "错误号:" + MSG_IO_ERROR,
						0).show();
				enterHome();
				break;
			case MSG_JSON_ERROR:
				Toast.makeText(getApplicationContext(),
						"错误号:" + MSG_JSON_ERROR, 0).show();
				enterHome();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(getApplicationContext(),
						"错误号:" + MSG_SERVER_ERROR, 0).show();
				enterHome();
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		// tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		// 用注解的方式findViewById()
		ViewUtils.inject(this); // 把所有带注解的空间全部 findViewById()
		
		

		// 设置版本号到界面显示
		tv_splash_version.setText(getVersionName());
		if (sp.getBoolean("update", true)) {
			checkVersion();
		} else {
			new Thread(){
				public void run() {
					SystemClock.sleep(2000);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							enterHome();
						}
					});
				};
			}.start();
		}
		
		//拷贝资产目录下的数据库
		copyDB("address.db");
		copyDB("commonnum.db");
		copyDB("antivirus.db");
		
		//创建快捷方式
		createShortCut();
		
	}
	

	/**
	 * 创建应用程序的快捷图标
	 */
	private void createShortCut() {
		Intent intent=new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//设置快捷方式的内容  打开安全卫士（因为这个快捷方式是桌面的，所以不能用显示意图，要用隐式意图）
		Intent shortcutIntent=new Intent();
		shortcutIntent.setAction("ooo.aaa.bbb");
		shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
		/**
		 * 设置打电话的动作
		 * shortcutIntent.setAction(Intent.ACTION_CALL);
		 * shortcutIntent.setData(Uri.parse("tel:110"));
		 */
		
		
		//为了用户体验，设置只创建一个快捷方式  1.
		intent.putExtra("duplicate", false);
		//名字，图标，干什么
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马快捷");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		
		sendBroadcast(intent);
	}


	/**
	 * 拷贝资产目录下的数据库文件
	 * 
	 * @param dbname  数据库的文件名
	 */
	private void copyDB(final String dbname) {
		new Thread(){
			public void run() {
				//做一个判断，如果已经拷贝过了，就不用再拷贝了
				File file=new File(getFilesDir(),dbname);
				if(file.exists()&&file.length()>0){
					Log.i("SplashActivity", "数据库是存在的，无需拷贝！");
					return;
				}
				try {
					//拿到资产管理器，打开数据库文件返回输入流
					InputStream is = getAssets().open(dbname);  
					//创建文件输出流,输出文件到file目录下
					FileOutputStream fos = openFileOutput(dbname, MODE_PRIVATE);
					byte[] buffer=new byte[1024];
					int len=0;
					while((len=is.read(buffer))!=-1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void copyDB() {
		
	}

	/**
	 * 弹出升级对话框，进行操作
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("升级对话框");
		builder.setIcon(R.drawable.ic_launcher);
		// builder.setCancelable(false);// 设置不能退出
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}

		});
		builder.setMessage(des);

		builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载新的版本 用xUtils工具包进行处理
				HttpUtils http = new HttpUtils();
				http.download(apkurl, Environment.getExternalStorageDirectory()
						.toString() + "/mobilesafe2.0.apk",
						new RequestCallBack<File>() {
							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
								tv_splash_process.setVisibility(View.VISIBLE);
								tv_splash_process
										.setText(current + "/" + total);
								super.onLoading(total, current, isUploading);
							}

							@Override
							public void onSuccess(ResponseInfo<File> arg0) {
								// 安装
								installAPK();
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								// 下载失败，进入下一个界面
								Toast.makeText(getApplicationContext(),
										"下载失败！！", 0).show();
								enterHome();
							}
						});
			}
		});
		builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	/**
	 * 安装apk <activity android:name=".PackageInstallerActivity"
	 * android:configChanges="orientation|keyboardHidden|screenSize"
	 * android:excludeFromRecents="true"> <intent-filter> <action
	 * android:name="android.intent.action.VIEW" /> <action
	 * android:name="android.intent.action.INSTALL_PACKAGE" /> <category
	 * android:name="android.intent.category.DEFAULT" /> <data
	 * android:scheme="file" /> <data
	 * android:mimeType="application/vnd.android.package-archive" />
	 * </intent-filter> <intent-filter> <action
	 * android:name="android.intent.action.INSTALL_PACKAGE" /> <category
	 * android:name="android.intent.category.DEFAULT" /> <data
	 * android:scheme="file" /> <data android:scheme="package" />
	 * </intent-filter> </activity>
	 */
	private void installAPK() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(
				Uri.fromFile(new File(Environment.getExternalStorageDirectory()
						+ "/mobilesafe2.0.apk")),
				"application/vnd.android.package-archive");
		// startActivity(intent);
		startActivityForResult(intent, 0); // 调用系统的安装完成以后，回调指定的方法，作用：当用户进入系统的安装界面之后点退出了，就可以直接进入应用的主界面了，如果没有的话，就会停留在SplashActivity上

	}

	/**
	 * 调用系统安装程序的回调方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("requestCode:" + requestCode);
		// 进入主界面
		enterHome();
	}

	/**
	 * 进入主界面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish(); // 退出当前界面
	}

	/**
	 * 联网获取升级信息，json解析 1.获取url对象 new URL("xxx");
	 * 2.建立连接，获得HttpURLConnection对象openConnection(); 3.设置请求方式，和超时 4.拿到返回码
	 * getResponseCode(); 5.根据返回码做具体操作
	 */
	public void checkVersion() {
		new Thread() {
			// 设置splash显示时间，不足两秒，补足两秒
			long startTime = System.currentTimeMillis();

			public void run() {
				Message msg = Message.obtain(); // 在消息池拿消息
				try {
					URL url = new URL(
							"http://192.168.1.100:8080/updateinfo.json");
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(5000);
					int requestCode = conn.getResponseCode();
					if (requestCode == 200) {
						InputStream is = conn.getInputStream();
						String json = StreamUtils.parseInputStream(is);
						if (TextUtils.isEmpty(json)) {
							UIUtils.showToast(SplashActivity.this,
									"错误号：60，json为空");
							msg.what = MSG_ENTER_HOME;
						} else {
							// 创建json解析对象，开始解析
							JSONObject jsonObject = new JSONObject(json);
							int serverVersionCode = jsonObject.getInt("code");
							des = jsonObject.getString("des");
							apkurl = jsonObject.getString("apkurl");

							if (serverVersionCode == clientVersionCode) {
								msg.what = MSG_ENTER_HOME;
							} else {
								msg.what = MSG_UPDATE;
							}
						}
					} else {
						msg.what = MSG_SERVER_ERROR;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					// 处理异常，进入下一个界面
					msg.what = MSG_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = MSG_IO_ERROR;
				} catch (JSONException e) {
					msg.what = MSG_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();
					long time = endTime - startTime;
					if (time - 2000 < 0) {
						SystemClock.sleep(2000 - time);
					}
					handler.sendMessage(msg);
				}
			}
		}.start();

	}

	/**
	 * 动态获取版本号
	 */
	private String getVersionName() {
		// 拿到包管理器
		PackageManager pManager = getPackageManager();
		try {
			// 拿到当前包的信息,getPackageName()返回当前的包名
			PackageInfo packageInfo = pManager.getPackageInfo(getPackageName(),
					0);
			String versionName = packageInfo.versionName;
			clientVersionCode = packageInfo.versionCode;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// can't reach 这里是无法达到的
			return "";
		}
	}

}
