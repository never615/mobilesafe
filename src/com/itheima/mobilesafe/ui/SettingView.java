package com.itheima.mobilesafe.ui;

import com.itheima.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {

	private TextView tv_setting_title;
	private TextView tv_setting_content;
	private CheckBox cb_setting;
	private String title;
	private String des_on;
	private String des_off;

	public SettingView(Context context) {
		super(context);
		init();
	}

	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		title = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.itheima.mobilesafe",
				"title");
		des_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.itheima.mobilesafe",
				"des_on");
		des_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.itheima.mobilesafe",
				"des_off");
		setTitle(title);
		if (!cb_setting.isChecked()) {
			setContent(des_off);
		} else {
			setContent(des_on);
		}
	}

	private void init() {
		//把资源文件转换成view对象，显示在自己身上
		View.inflate(getContext(), R.layout.setting_view, this);// 创建布局直接告诉它爹是谁
		// 找到布局文件中的各个空间，提供修改他们内容的方法，以便上层代码调用
		tv_setting_title = (TextView) findViewById(R.id.tv_setting_title);
		tv_setting_content = (TextView) findViewById(R.id.tv_setting_content);
		cb_setting = (CheckBox) findViewById(R.id.cb_setting);
		this.setBackgroundResource(R.drawable.list_selector);
	}

	/**
	 * 修改标题
	 * 
	 * @param text_title
	 */
	public void setTitle(String text_title) {
		tv_setting_title.setText(text_title);
	}

	/**
	 * 修改描述信息
	 * 
	 * @param text_content
	 */
	public void setContent(String text_content) {
		tv_setting_content.setText(text_content);
	}

	/**
	 * 修改选中状态
	 * 
	 * @param b
	 */
	public void setChecked(boolean b) {
		cb_setting.setChecked(b);
		
		if(b){
			setContent(des_on);
		}else{
			setContent(des_off);
		}
	}

	/**
	 * 是否被选中
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return cb_setting.isChecked();
	}

}
