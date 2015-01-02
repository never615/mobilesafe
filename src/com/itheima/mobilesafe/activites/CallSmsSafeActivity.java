package com.itheima.mobilesafe.activites;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CallSmsSafeActivity extends Activity {
	@ViewInject(R.id.lv_callsms_safe)
	private ListView lv_callsms_safe;
	@ViewInject(R.id.tv_add_number_tips)
	private TextView tv_add_number_tips;
	@ViewInject(R.id.ll_loading_tips)
	private LinearLayout ll_loading_tips;

	private BlackNumberDao dao;
	List<BlackNumberInfo> infos;
	private BaseAdapter adapter;

	/**
	 * ��ѯ��Ŀ����ʼλ��
	 */
	private int startIndex = 0;

	/**
	 * ������Ŀ������
	 */
	private int maxCount = 20;
	private int totalNumber;

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
				tv_add_number_tips.setVisibility(View.INVISIBLE);
				// ��ˢ�����ݵ�ʱ��ÿ�ζ�new��һ���µ���������������ÿ��һ�������ݾͻص����
				// ����Ӧ�õ��þɵ���������Ȼ����÷���ˢ�����������
				if (adapter == null) {
					adapter = new CallSmsSafeAdapter();
					lv_callsms_safe.setAdapter(adapter);
				} else {// ����������Ѿ����ڣ���������������ķ���ˢ������
					adapter.notifyDataSetChanged();
				}
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
		totalNumber = dao.getTotalNumber();

	}

	/**
	 * �������
	 */
	private void fillData() {

		// ��Ϊ��ȡ���ݿ��ʱ�����ݶ�Ļ�����ʱ̫�ã����ԣ������߳��н���
		new Thread() {
			@Override
			public void run() {
				super.run();
				// ��������fillData()������infos����һֱ����������ݣ�����Ӧ�ð���������ӵ�infos�����У�
				// infos = dao.findPart2(startIndex, maxCount);
				if (infos == null) {
					infos = dao.findPart2(startIndex, maxCount);
				} else {
					// infos�������Ѿ��������ˣ�������Ҫ����������ӵ�������
					infos.addAll(dao.findPart2(startIndex, maxCount));
				}
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

		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			// ����״̬�����仯ʱ����
			// OnScrollListener.SCROLL_STATE_FLING ���Ի���
			// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ��������
			// OnScrollListener.SCROLL_STATE_IDLE ��ֹ
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // ��ֹ״̬��ʱ���ж�listview�Ƿ������һ����Ŀ
					// �õ����һ���ɼ���Ŀ��λ��
					int lastPosition = lv_callsms_safe.getLastVisiblePosition();
					if (lastPosition == (infos.size() - 1)) {
						if (lastPosition == totalNumber - 1) {
							Toast.makeText(getApplicationContext(), "û�и����������",
									0).show();
							return;
						}
						// ������ڿɼ������һ����Ŀ�Ǽ��������һ���Ļ����ͼ��ظ��������
						startIndex += maxCount;
						fillData();
					}
					break;
				}
			}

			// ֻҪlistView�����仯���ͻ��������ķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
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
				view = View.inflate(CallSmsSafeActivity.this,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // Ϊ�˼��ٺ��Ӳ�ѯ�Ĵ���
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);
				holder.iv_item_delete = (ImageView) view
						.findViewById(R.id.iv_item_delete);

				// �Ѻ���id�����ã�����holder���棬���ø�����view
				view.setTag(holder);
			} else {
				view = convertView; // ʹ����ʷ�����view���󣬼���view�������Ĵ���
				holder = (ViewHolder) view.getTag();
			}

			// �õ���ǰ��Ŀ����Ϣ
			final BlackNumberInfo info = infos.get(position);

			// ɾ����ť�����¼�
			// TODO ɾ��̫��Ļ���������¸����ϳ�������
			holder.iv_item_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if (result) {
						Toast.makeText(getApplicationContext(), "ɾ���ɹ�", 0)
								.show();
						// ���½���
						// ��ui��ʾ�ļ���infos��ɾ����Ŀ
						infos.remove(info);
						// ֪ͨ�������
						adapter.notifyDataSetChanged();

					} else {
						Toast.makeText(getApplicationContext(), "ɾ��ʧ��", 0)
								.show();
					}
				}
			});

			// ����ÿһ����Ŀ���ݣ����������ģʽ
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
		ImageView iv_item_delete;
	}

	/**
	 * ��Ӻ���������
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create(); // �����Ի���ÿ��ƣ��������dimiss()������

		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);

		final EditText et_blackNumber = (EditText) dialogView
				.findViewById(R.id.et_blackNumber);
		final CheckBox cb_phone = (CheckBox) dialogView
				.findViewById(R.id.cb_addblack_phone);
		final CheckBox cb_sms = (CheckBox) dialogView
				.findViewById(R.id.cb_addblack_sms);

		dialogView.findViewById(R.id.bt_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						String number = et_blackNumber.getText().toString()
								.trim();
						if (TextUtils.isEmpty(number)) {
							Toast.makeText(getApplicationContext(), "���벻��Ϊ��", 1)
									.show();
							return;
						}
						String mode = "0";
						// 1 ȫ������ 2 �������� 3 �绰����
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							mode = "1";
						} else if (cb_phone.isChecked()) {
							mode = "3";
						} else if (cb_sms.isChecked()) {
							mode = "2";
						} else {
							Toast.makeText(getApplicationContext(), "��ѡ������ģʽ",
									1).show();
							return;
						}
						boolean result = dao.add(number, mode);
						// ˢ�½��� �����ݼӵ�infos������
						if (result) {
							BlackNumberInfo info = new BlackNumberInfo();
							info.setNumber(number);
							info.setMode(mode);
							infos.add(0, info);
							// ֪ͨ����ˢ��
							handler.sendEmptyMessage(0);
							/*
							 * //����ˢ�½��� if (adapter != null) {
							 * adapter.notifyDataSetChanged(); } else { adapter
							 * = new CallSmsSafeAdapter();
							 * lv_callsms_safe.setAdapter(adapter); }
							 */
							dialog.dismiss();
						}
					}
				});
		dialogView.findViewById(R.id.bt_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		dialog.setView(dialogView, 0, 0, 0, 0); // Ϊ����Ӧ�Ͱ汾��ģ�������ò�����ȫ���Ի������Ե��ô˷���������߽�ľ���Ϊ0����û�кڱ���
		dialog.show();
	}

}
