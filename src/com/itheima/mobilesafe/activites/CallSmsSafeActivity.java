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
	 * 查询条目的起始位置
	 */
	private int startIndex = 0;

	/**
	 * 加载条目的数量
	 */
	private int maxCount = 20;
	private int totalNumber;

	/**
	 * 消息处理器
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ll_loading_tips.setVisibility(View.INVISIBLE);
			if (infos.size() == 0) {
				// 没有数据，调用tv_add_number_tips做提醒
				tv_add_number_tips.setVisibility(View.VISIBLE);
			} else {
				tv_add_number_tips.setVisibility(View.INVISIBLE);
				// 在刷新数据的时候，每次都new了一个新的适配器，这样会每次一加载数据就回到最顶，
				// 我们应该调用旧的适配器，然后调用方法刷新里面的数据
				if (adapter == null) {
					adapter = new CallSmsSafeAdapter();
					lv_callsms_safe.setAdapter(adapter);
				} else {// 如果适配器已经存在，调用适配器里面的方法刷新数据
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
	 * 初始化
	 */
	private void init() {
		// 通过数据库操作工具拿数据
		dao = new BlackNumberDao(this);
		totalNumber = dao.getTotalNumber();

	}

	/**
	 * 填充数据
	 */
	private void fillData() {

		// 因为读取数据库的时候，数据多的话，耗时太久，所以，在子线程中进行
		new Thread() {
			@Override
			public void run() {
				super.run();
				// 反复调用fillData()方法，infos集合一直存的是新数据，我们应该把新数据添加到infos集合中；
				// infos = dao.findPart2(startIndex, maxCount);
				if (infos == null) {
					infos = dao.findPart2(startIndex, maxCount);
				} else {
					// infos集合中已经有数据了，我们需要把新数据添加到集合中
					infos.addAll(dao.findPart2(startIndex, maxCount));
				}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	/**
	 * 初始化UI逻辑模块
	 */
	private void initUI() {
		setContentView(R.layout.activity_callsms_safe);

		ViewUtils.inject(this);// 通过注解的方式注册全部的控件

		lv_callsms_safe.setOnScrollListener(new OnScrollListener() {
			// 滚动状态发生变化时调用
			// OnScrollListener.SCROLL_STATE_FLING 惯性滑动
			// OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 触摸滑动
			// OnScrollListener.SCROLL_STATE_IDLE 静止
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 静止状态的时候判断listview是否是最后一个条目
					// 拿到最后一个可见条目的位置
					int lastPosition = lv_callsms_safe.getLastVisiblePosition();
					if (lastPosition == (infos.size() - 1)) {
						if (lastPosition == totalNumber - 1) {
							Toast.makeText(getApplicationContext(), "没有更多的数据了",
									0).show();
							return;
						}
						// 如果现在可见的最后一个条目是集合中最后一个的话，就加载更多的数据
						startIndex += maxCount;
						fillData();
					}
					break;
				}
			}

			// 只要listView发生变化，就会调用下面的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	/**
	 * listView的适配器
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
			// 复用已经存在的view对象
			if (convertView == null) {
				view = View.inflate(CallSmsSafeActivity.this,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // 为了减少孩子查询的次数
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);
				holder.iv_item_delete = (ImageView) view
						.findViewById(R.id.iv_item_delete);

				// 把孩子id的引用，放在holder里面，设置给父亲view
				view.setTag(holder);
			} else {
				view = convertView; // 使用历史缓存的view对象，减少view被创建的次数
				holder = (ViewHolder) view.getTag();
			}

			// 拿到当前条目的信息
			final BlackNumberInfo info = infos.get(position);

			// 删除按钮监听事件
			// TODO 删除太快的话，界面更新跟不上出现问题
			holder.iv_item_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if (result) {
						Toast.makeText(getApplicationContext(), "删除成功", 0)
								.show();
						// 更新界面
						// 从ui显示的集合infos中删除条目
						infos.remove(info);
						// 通知界面更新
						adapter.notifyDataSetChanged();

					} else {
						Toast.makeText(getApplicationContext(), "删除失败", 0)
								.show();
					}
				}
			});

			// 设置每一个条目内容，号码和拦截模式
			holder.tv_phone.setText(info.getNumber());

			// 1全2短3电话
			String mode = info.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("全部拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
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
	 * 家庭组 View对象的容器
	 * 
	 * @author rong
	 */
	class ViewHolder {
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_item_delete;
	}

	/**
	 * 添加黑名单方法
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		final AlertDialog dialog = builder.create(); // 创建对话框好控制，比如调用dimiss()方法。

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
							Toast.makeText(getApplicationContext(), "号码不能为空", 1)
									.show();
							return;
						}
						String mode = "0";
						// 1 全部拦截 2 短信拦截 3 电话拦截
						if (cb_phone.isChecked() && cb_sms.isChecked()) {
							mode = "1";
						} else if (cb_phone.isChecked()) {
							mode = "3";
						} else if (cb_sms.isChecked()) {
							mode = "2";
						} else {
							Toast.makeText(getApplicationContext(), "请选择拦截模式",
									1).show();
							return;
						}
						boolean result = dao.add(number, mode);
						// 刷新界面 把数据加到infos集合中
						if (result) {
							BlackNumberInfo info = new BlackNumberInfo();
							info.setNumber(number);
							info.setMode(mode);
							infos.add(0, info);
							// 通知界面刷新
							handler.sendEmptyMessage(0);
							/*
							 * //下面刷新界面 if (adapter != null) {
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

		dialog.setView(dialogView, 0, 0, 0, 0); // 为了适应低版本的模拟器，让布局完全填充对话框，所以调用此方法设置与边界的距离为0，就没有黑边了
		dialog.show();
	}

}
