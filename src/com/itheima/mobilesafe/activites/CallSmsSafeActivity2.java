package com.itheima.mobilesafe.activites;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CallSmsSafeActivity2 extends Activity {
	@ViewInject(R.id.lv_callsms_safe)
	private ListView lv_callsms_safe;
	@ViewInject(R.id.tv_add_number_tips)
	private TextView tv_add_number_tips;
	@ViewInject(R.id.ll_loading_tips)
	private LinearLayout ll_loading_tips;
	@ViewInject(R.id.et_callsms_pagenumber)
	private EditText et_callsms_pagenumber;
	@ViewInject(R.id.tv_callsms_pageinfo)
	private TextView tv_callsms_pageinfo;

	private BlackNumberDao dao;
	List<BlackNumberInfo> infos;

	/**
	 * ��ǰ��ҳ��
	 */
	private int currentPageNumber = 0;
	/**
	 * ҳ�����Ŀ��
	 */
	private static final int pageSize = 20;
	/**
	 * ��ҳ��
	 */
	private int totalPageNumber = 0;

	/**
	 * ��Ϣ������
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading_tips.setVisibility(View.INVISIBLE);
			if (infos.size() == 0) {
				// û�����ݣ�����tv_add_number_tips������
				tv_add_number_tips.setVisibility(View.VISIBLE);
			} else {
				lv_callsms_safe.setAdapter(new CallSmsSafeAdapter());
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUI();
		init();
		fillData();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		// ͨ�����ݿ��������������
		dao = new BlackNumberDao(this);
		// �����ҳ����
		int temp=0;
		if((dao.getTotalNumber() %pageSize>0)||(dao.getTotalNumber()==0)){
			temp=1;
		}
		totalPageNumber = dao.getTotalNumber() / pageSize+temp;
		//System.out.println("totalPageNumber:"+totalPageNumber);
		//System.out.println("dao.getTotalNumber():"+dao.getTotalNumber());
	}

	/**
	 * �������
	 */
	private void fillData() {

		// ����ҳ��
		tv_callsms_pageinfo.setText(currentPageNumber + "/"
				+ (totalPageNumber - 1));

		// ��Ϊ��ȡ���ݿ��ʱ�����ݶ�Ļ�����ʱ̫�ã����ԣ������߳��н���
		new Thread() {
			@Override
			public void run() {
				super.run();
				// infos = dao.findAll();
				infos = dao.findPart(currentPageNumber, pageSize);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * ��ʼ��UI�߼�ģ��
	 */
	private void initUI() {
		setContentView(R.layout.activity_callsms_safe);

		ViewUtils.inject(this);// ͨ��ע��ķ�ʽע��ȫ���Ŀؼ�
	}

	/**
	 * listView��������
	 * 
	 * @author rong
	 */
	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			// �����Ѿ����ڵ�view����
			if (convertView == null) {
				view = View.inflate(CallSmsSafeActivity2.this,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // Ϊ�˼��ٺ��Ӳ�ѯ�Ĵ���
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);

				// �Ѻ���id�����ã�����holder���棬���ø�����view
				view.setTag(holder);
			} else {
				view = convertView; // ʹ����ʷ�����view���󣬼���view�������Ĵ���
				holder = (ViewHolder) view.getTag();
			}

			BlackNumberInfo info = infos.get(position);
			holder.tv_phone.setText(info.getNumber());

			// 1ȫ2��3�绰
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("ȫ������");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("�绰����");
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

	/**
	 * ��ͥ�� View���������
	 * 
	 * @author rong
	 */
	class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
	}

	// ҳ�밴ť����

	/**
	 * ��һҳ
	 * 
	 * @param view
	 */
	public void prePage(View view) {
		if (currentPageNumber <= 0) {
			Toast.makeText(this, "�Ѿ��ǵ�һҳ��", 0).show();
			return;
		}
		currentPageNumber--;
		fillData();
	}

	/**
	 * ��һҳ
	 * 
	 * @param view
	 */
	public void nextPage(View view) {
		if (currentPageNumber >= (totalPageNumber - 1)) {
			Toast.makeText(this, "�Ѿ������һҳ��", 0).show();
			return;
		}
		currentPageNumber++;
		fillData();
	}

	/**
	 * ��ת
	 * 
	 * @param view
	 */
	public void jump(View view) {
		/**
		 * Integer.parseInt()��String ��ת��ΪInt�ͣ�
		 * 
		 * Integer.valueOf()��String ��ת��ΪInteger����
		 */
		String str_pagenumber = et_callsms_pagenumber.getText().toString()
				.trim();
		if (TextUtils.isEmpty(str_pagenumber)) {
			Toast.makeText(this, "������ҳ���", 0).show();
		} else {
			int number = Integer.parseInt(str_pagenumber);
			if (number >= 0 && number < totalPageNumber) {
				currentPageNumber = number;
				fillData();
			} else {
				Toast.makeText(this, "��������ȷ��ҳ���", 0).show();
			}
		}
	}

}
