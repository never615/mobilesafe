package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 自定义的textview，用来展示跑马灯效果
 * @author rong
 *
 */
public class FocusTextView extends TextView {

	//在代码中设置用
	public FocusTextView(Context context) {
		super(context);
	}
	//指定了样式
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//在布局文件中使用
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//欺骗系统，已经获得了焦点
	@Override
	public boolean isFocused() {
		return true;
	}
	

}
