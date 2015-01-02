package com.itheima.mobilesafe.activites;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.MD5Utils;
import com.itheima.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.cache.MD5FileNameGenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


 
public class HomeActivity extends Activity{
	private GridView gv_home;
	private SharedPreferences sp;
	
	private EditText et_pwd;
	private EditText et_pwd_confirm;
	private Button bt_ok;
	private Button bt_cancel;
	
	private AlertDialog dialog; //享元模式，关闭对话框
	
	private String[] names = { "手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "手机杀毒",
			"缓存清理", "高级工具", "设置中心" };
	private int[] icons = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
			R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
			R.drawable.settings };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		sp=getSharedPreferences("config", MODE_PRIVATE);
		
		gv_home=(GridView) findViewById(R.id.gv_home);
		
		
		//设置GridView每个条目的监听事件，点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0://手机防盗
					//1.判断是否设置过密码
					if(isSetupPwd()){
						//设置密码，显示输入密码对话框
						showEnterPwdDialog();
					}else{
						//没有设置过密码，显示设置密码对话框
						showSetupPwdDialog();
					}
					break;
					
				case 1: //跳转到通讯卫士界面
					intent=new Intent(getApplicationContext(),CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2: //跳转到软件管家界面
					intent=new Intent(getApplicationContext(),AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3: //跳转到进程管理界面
					intent=new Intent(getApplicationContext(),TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4: //跳转到流量统计界面
					intent=new Intent(getApplicationContext(),TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5: //跳转到手机杀毒界面
					intent=new Intent(getApplicationContext(),AntivirusActivity.class);
					startActivity(intent);
					break;
				case 6: //跳转缓存清理界面
					intent=new Intent(getApplicationContext(),CleanCacheActivity.class);
					startActivity(intent);
					break;
				case 7: //跳转到高级工具界面
					intent=new Intent(getApplicationContext(),AtoolsActivity.class);
					startActivity(intent);
					break;
				case 8: //跳转到设置界面
					intent=new Intent(getApplicationContext(),SettingActivity.class);
					startActivity(intent);
					break;
				
				default:
					break;
				}
			}
		});
		
	}
	
	//解决改名之后刷新新名字的问题，利用生命周期
	@Override
	protected void onStart() {
		super.onStart();
		gv_home.setAdapter(new HomeAdapter());
	}
	
	/**
	 * 设置密码对话框
	 */
	protected void showSetupPwdDialog() {
		AlertDialog.Builder buidler=new Builder(this);
		View view=View.inflate(this, R.layout.dialog_setup_pwd, null);
		
		
		et_pwd = (EditText) view.findViewById(R.id.ed_pwd);
		et_pwd_confirm=(EditText) view.findViewById(R.id.ed_pwd_confirm);
		
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		//确定和取消按钮的事件
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwd=et_pwd.getText().toString().trim();
				String pwd_confirm=et_pwd_confirm.getText().toString().trim();
				
				if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
					UIUtils.showToast(HomeActivity.this, "密码不能为空！！！");
					return;
				}
				if(!pwd.equals(pwd_confirm)){
					UIUtils.showToast(HomeActivity.this, "两次输入的密码不一致！！");
					return;
				}
				//密码没问题，用sharedpreferences,保存。
				Editor editor=sp.edit();
				editor.putString("password", MD5Utils.encode(pwd));
				editor.commit();
				dialog.dismiss();
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		buidler.setView(view);
		dialog=buidler.show();
	}
	/**
	 * 输入密码对话框
	 */
	protected void showEnterPwdDialog() {
		AlertDialog.Builder buidler=new Builder(this);
		View view=View.inflate(this, R.layout.dialog_show_pwd, null);
		
		bt_ok=(Button) view.findViewById(R.id.bt_ok);
		bt_cancel=(Button) view.findViewById(R.id.bt_cancel);
		et_pwd=(EditText) view.findViewById(R.id.et_pwd);
		
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String pwd=MD5Utils.encode(et_pwd.getText().toString().trim());
				String pwd_test=sp.getString("password", null);
				if(TextUtils.isEmpty(pwd)){
					UIUtils.showToast(HomeActivity.this, "输入密码不能为空！！");
					return;
				}
				if(!pwd_test.equals(pwd)){
					UIUtils.showToast(HomeActivity.this, "密码不正确！！");
					return;
				}
				//进入手机防盗主界面
				Intent intent =new Intent(HomeActivity.this, LostFindActivity.class);
				startActivity(intent);
				
				dialog.dismiss();
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		buidler.setView(view);
		dialog=buidler.show();
	}

	/**
	 * 判断是否设置过密码
	 * @return
	 */
	private boolean isSetupPwd(){
		String password=sp.getString("password", null);
		if(TextUtils.isEmpty(password)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * GridView的适配器 设置home界面
	 * @author rong
	 *
	 */
	private class HomeAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return names.length;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if(convertView==null){
				view=View.inflate(getApplicationContext(), R.layout.item_home_grid, null);
			}else{
				view=convertView;
			}
			ImageView iv=(ImageView) view.findViewById(R.id.iv_homeitem_icon);
			TextView tv=(TextView) view.findViewById(R.id.tv_homeitem_name);
			iv.setImageResource(icons[position]);
			tv.setText(names[position]);
			
			//如果用户设置了新名字，显示新的
			if(position==0){
				String newname=sp.getString("newname", "");
				if(!TextUtils.isEmpty(newname)){
					tv.setText(newname);
				}
			}
			return view;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
	}
}
