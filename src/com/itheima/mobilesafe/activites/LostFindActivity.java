package com.itheima.mobilesafe.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;



public class LostFindActivity extends Activity {
	private static final String TAG = "LostFindActivity";
	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 得到系统配置
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		
		// 判断是否设置过向导，有直接进入手机防盗界面，没有就进入向导界面设置
		if (isFinishSetup()) {
			// 设置过，直接进入主界面
			setContentView(R.layout.activity_lost_find);
			Log.i(TAG, "完成过向导设置，直接进入主界面。");
			
			tv_lostfind_number=(TextView) findViewById(R.id.tv_lostfind_number);
			iv_lostfind_status=(ImageView) findViewById(R.id.iv_lostfind_status);
			
			//回显
			//安全号码
			String safenumber=sp.getString("safenumber","");
			//Toast.makeText(this, safenumber, 0).show();
			tv_lostfind_number.setText(safenumber);
			
			//是否绑定sim卡
			boolean protecting=sp.getBoolean("protecting", false);
			if(protecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
			
			
			
		} else {
			// 没有，进入向导设置界面
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			// 关闭当前的手机防盗界面
			finish();
			Log.i(TAG, "进入向导设置界面");
		}
	}

	//重新进入向导设置界面
	public void reEntrySetup(View view){
		Intent intent=new Intent(this,Setup1Activity.class);
		startActivity(intent);
		//关闭当前的手机防盗界面
		finish();
	}
	
	/**
	 * 判断是否设置完成过向导
	 */
	public boolean isFinishSetup() {
		return sp.getBoolean("finishsetup", false);
	}
	
	/**
	 * 菜单设置
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lost_find_menu, menu);
		return true;
	}
	
	//当菜单被点击的时候调用
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		
		return super.onMenuOpened(featureId, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(R.id.item_change_name==item.getItemId()){
			//弹出修改名称对话框
			AlertDialog.Builder builder=new Builder(this);
			builder.setTitle("请输入手机防盗的新名称");
			final EditText et=new EditText(this);
			builder.setView(et);
			builder.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newname=et.getText().toString().trim();
					SharedPreferences sp=getSharedPreferences("config", MODE_PRIVATE);
					Editor editor=sp.edit();
					editor.putString("newname", newname);
					editor.commit();
				}
			});
			builder.show();
		}
		return super.onOptionsItemSelected(item);
	}
}
