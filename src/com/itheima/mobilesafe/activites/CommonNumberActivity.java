package com.itheima.mobilesafe.activites;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.CommonNumberDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CommonNumberActivity extends Activity {

	@ViewInject(R.id.elv_commonnumber)
	private ExpandableListView elv_commonnumber;

	private SQLiteDatabase db;
	private CommonNumberDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_commonnumberquery);
		ViewUtils.inject(this);

		File file = new File(getFilesDir(), "commonnum.db");
		db = SQLiteDatabase.openDatabase(file.toString(), null,
				SQLiteDatabase.OPEN_READONLY);

		dao = new CommonNumberDao();

		filldata();

		init();
	}

	private void init() {
		elv_commonnumber.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String str=dao.getChildNameAndNumber(db, groupPosition, childPosition);
				String number=str.split("#")[1];
				Uri uri = Uri.parse("tel:"+number); 
				Intent it = new Intent(Intent.ACTION_DIAL,uri); 
				startActivity(it); 
				return false;
			}
		});
	}

	private void filldata() {
		elv_commonnumber.setAdapter(new CommnonNumberAdapter());

	}

	private class CommnonNumberAdapter extends BaseExpandableListAdapter {

		// 返回一共有多少组
		@Override
		public int getGroupCount() {
			return dao.getGroupCount(db);
		}

		// 返回一组中孩子的数量
		@Override
		public int getChildrenCount(int groupPosition) {
			return dao.getChildCount(db, groupPosition);
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		// 这个方法判断ID是够有效，有效的话可以通过getGroupId来确定显示那条内容，无效的话，就用item的位置来确定id
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null && convertView instanceof TextView) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(20);
			tv.setTextColor(Color.RED);
			tv.setText("       " + dao.getGroupName(db, groupPosition));
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView != null && convertView instanceof TextView) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(getApplicationContext());
			}
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(15);
			String str = dao.getChildNameAndNumber(db, groupPosition,
					childPosition);
			tv.setText(str.split("#")[0] + "\n" + str.split("#")[1]);
			return tv;
		}

		// 子孩子想要被点击，此方法要返回true
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
