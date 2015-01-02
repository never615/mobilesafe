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
				Toast.makeText(getApplicationContext(), "�����:" + MSG_URL_ERROR,
						0).show();
				enterHome();
				break;
			case MSG_IO_ERROR:
				Toast.makeText(getApplicationContext(), "�����:" + MSG_IO_ERROR,
						0).show();
				enterHome();
				break;
			case MSG_JSON_ERROR:
				Toast.makeText(getApplicationContext(),
						"�����:" + MSG_JSON_ERROR, 0).show();
				enterHome();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(getApplicationContext(),
						"�����:" + MSG_SERVER_ERROR, 0).show();
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
		// ��ע��ķ�ʽfindViewById()
		ViewUtils.inject(this); // �����д�ע��Ŀռ�ȫ�� findViewById()
		
		

		// ���ð汾�ŵ�������ʾ
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
		
		//�����ʲ�Ŀ¼�µ����ݿ�
		copyDB("address.db");
		copyDB("commonnum.db");
		copyDB("antivirus.db");
		
		//������ݷ�ʽ
		createShortCut();
		
	}
	

	/**
	 * ����Ӧ�ó���Ŀ��ͼ��
	 */
	private void createShortCut() {
		Intent intent=new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//���ÿ�ݷ�ʽ������  �򿪰�ȫ��ʿ����Ϊ�����ݷ�ʽ������ģ����Բ�������ʾ��ͼ��Ҫ����ʽ��ͼ��
		Intent shortcutIntent=new Intent();
		shortcutIntent.setAction("ooo.aaa.bbb");
		shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
		/**
		 * ���ô�绰�Ķ���
		 * shortcutIntent.setAction(Intent.ACTION_CALL);
		 * shortcutIntent.setData(Uri.parse("tel:110"));
		 */
		
		
		//Ϊ���û����飬����ֻ����һ����ݷ�ʽ  1.
		intent.putExtra("duplicate", false);
		//���֣�ͼ�꣬��ʲô
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "������");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		
		sendBroadcast(intent);
	}


	/**
	 * �����ʲ�Ŀ¼�µ����ݿ��ļ�
	 * 
	 * @param dbname  ���ݿ���ļ���
	 */
	private void copyDB(final String dbname) {
		new Thread(){
			public void run() {
				//��һ���жϣ�����Ѿ��������ˣ��Ͳ����ٿ�����
				File file=new File(getFilesDir(),dbname);
				if(file.exists()&&file.length()>0){
					Log.i("SplashActivity", "���ݿ��Ǵ��ڵģ����追����");
					return;
				}
				try {
					//�õ��ʲ��������������ݿ��ļ�����������
					InputStream is = getAssets().open(dbname);  
					//�����ļ������,����ļ���fileĿ¼��
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
	 * ���������Ի��򣬽��в���
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("�����Ի���");
		builder.setIcon(R.drawable.ic_launcher);
		// builder.setCancelable(false);// ���ò����˳�
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
			}

		});
		builder.setMessage(des);

		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// �����µİ汾 ��xUtils���߰����д���
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
								// ��װ
								installAPK();
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								// ����ʧ�ܣ�������һ������
								Toast.makeText(getApplicationContext(),
										"����ʧ�ܣ���", 0).show();
								enterHome();
							}
						});
			}
		});
		builder.setNegativeButton("�˳�", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		builder.show();
	}

	/**
	 * ��װapk <activity android:name=".PackageInstallerActivity"
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
		startActivityForResult(intent, 0); // ����ϵͳ�İ�װ����Ժ󣬻ص�ָ���ķ��������ã����û�����ϵͳ�İ�װ����֮����˳��ˣ��Ϳ���ֱ�ӽ���Ӧ�õ��������ˣ����û�еĻ����ͻ�ͣ����SplashActivity��

	}

	/**
	 * ����ϵͳ��װ����Ļص�����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("requestCode:" + requestCode);
		// ����������
		enterHome();
	}

	/**
	 * ����������
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish(); // �˳���ǰ����
	}

	/**
	 * ������ȡ������Ϣ��json���� 1.��ȡurl���� new URL("xxx");
	 * 2.�������ӣ����HttpURLConnection����openConnection(); 3.��������ʽ���ͳ�ʱ 4.�õ�������
	 * getResponseCode(); 5.���ݷ��������������
	 */
	public void checkVersion() {
		new Thread() {
			// ����splash��ʾʱ�䣬�������룬��������
			long startTime = System.currentTimeMillis();

			public void run() {
				Message msg = Message.obtain(); // ����Ϣ������Ϣ
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
									"����ţ�60��jsonΪ��");
							msg.what = MSG_ENTER_HOME;
						} else {
							// ����json�������󣬿�ʼ����
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
					// �����쳣��������һ������
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
	 * ��̬��ȡ�汾��
	 */
	private String getVersionName() {
		// �õ���������
		PackageManager pManager = getPackageManager();
		try {
			// �õ���ǰ������Ϣ,getPackageName()���ص�ǰ�İ���
			PackageInfo packageInfo = pManager.getPackageInfo(getPackageName(),
					0);
			String versionName = packageInfo.versionName;
			clientVersionCode = packageInfo.versionCode;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// can't reach �������޷��ﵽ��
			return "";
		}
	}

}
