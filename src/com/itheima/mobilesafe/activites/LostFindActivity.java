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
		// �õ�ϵͳ����
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		
		// �ж��Ƿ����ù��򵼣���ֱ�ӽ����ֻ��������棬û�оͽ����򵼽�������
		if (isFinishSetup()) {
			// ���ù���ֱ�ӽ���������
			setContentView(R.layout.activity_lost_find);
			Log.i(TAG, "��ɹ������ã�ֱ�ӽ��������档");
			
			tv_lostfind_number=(TextView) findViewById(R.id.tv_lostfind_number);
			iv_lostfind_status=(ImageView) findViewById(R.id.iv_lostfind_status);
			
			//����
			//��ȫ����
			String safenumber=sp.getString("safenumber","");
			//Toast.makeText(this, safenumber, 0).show();
			tv_lostfind_number.setText(safenumber);
			
			//�Ƿ��sim��
			boolean protecting=sp.getBoolean("protecting", false);
			if(protecting){
				iv_lostfind_status.setImageResource(R.drawable.lock);
			}else{
				iv_lostfind_status.setImageResource(R.drawable.unlock);
			}
			
			
			
		} else {
			// û�У����������ý���
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			// �رյ�ǰ���ֻ���������
			finish();
			Log.i(TAG, "���������ý���");
		}
	}

	//���½��������ý���
	public void reEntrySetup(View view){
		Intent intent=new Intent(this,Setup1Activity.class);
		startActivity(intent);
		//�رյ�ǰ���ֻ���������
		finish();
	}
	
	/**
	 * �ж��Ƿ�������ɹ���
	 */
	public boolean isFinishSetup() {
		return sp.getBoolean("finishsetup", false);
	}
	
	/**
	 * �˵�����
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lost_find_menu, menu);
		return true;
	}
	
	//���˵��������ʱ�����
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		
		return super.onMenuOpened(featureId, menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(R.id.item_change_name==item.getItemId()){
			//�����޸����ƶԻ���
			AlertDialog.Builder builder=new Builder(this);
			builder.setTitle("�������ֻ�������������");
			final EditText et=new EditText(this);
			builder.setView(et);
			builder.setPositiveButton("ȷ��", new OnClickListener() {
				
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
