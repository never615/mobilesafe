package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * �Զ����textview������չʾ�����Ч��
 * @author rong
 *
 */
public class FocusTextView extends TextView {

	//�ڴ�����������
	public FocusTextView(Context context) {
		super(context);
	}
	//ָ������ʽ
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//�ڲ����ļ���ʹ��
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//��ƭϵͳ���Ѿ�����˽���
	@Override
	public boolean isFocused() {
		return true;
	}
	

}
