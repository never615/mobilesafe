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
	 * 当前的页码
	 */
	private int currentPageNumber = 0;
	/**
	 * 页面的条目数
	 */
	private static final int pageSize = 20;
	/**
	 * 总页码
	 */
	private int totalPageNumber = 0;

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
	 * 初始化
	 */
	private void init() {
		// 通过数据库操作工具拿数据
		dao = new BlackNumberDao(this);
		// 获得总页码数
		int temp=0;
		if((dao.getTotalNumber() %pageSize>0)||(dao.getTotalNumber()==0)){
			temp=1;
		}
		totalPageNumber = dao.getTotalNumber() / pageSize+temp;
		//System.out.println("totalPageNumber:"+totalPageNumber);
		//System.out.println("dao.getTotalNumber():"+dao.getTotalNumber());
	}

	/**
	 * 填充数据
	 */
	private void fillData() {

		// 更新页码
		tv_callsms_pageinfo.setText(currentPageNumber + "/"
				+ (totalPageNumber - 1));

		// 因为读取数据库的时候，数据多的话，耗时太久，所以，在子线程中进行
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
	 * 初始化UI逻辑模块
	 */
	private void initUI() {
		setContentView(R.layout.activity_callsms_safe);

		ViewUtils.inject(this);// 通过注解的方式注册全部的控件
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
				view = View.inflate(CallSmsSafeActivity2.this,
						R.layout.item_callsms, null);
				holder = new ViewHolder(); // 为了减少孩子查询的次数
				holder.tv_phone = (TextView) view
						.findViewById(R.id.tv_item_phone);
				holder.tv_mode = (TextView) view
						.findViewById(R.id.tv_item_mode);

				// 把孩子id的引用，放在holder里面，设置给父亲view
				view.setTag(holder);
			} else {
				view = convertView; // 使用历史缓存的view对象，减少view被创建的次数
				holder = (ViewHolder) view.getTag();
			}

			BlackNumberInfo info = infos.get(position);
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
	}

	// 页码按钮操作

	/**
	 * 上一页
	 * 
	 * @param view
	 */
	public void prePage(View view) {
		if (currentPageNumber <= 0) {
			Toast.makeText(this, "已经是第一页了", 0).show();
			return;
		}
		currentPageNumber--;
		fillData();
	}

	/**
	 * 下一页
	 * 
	 * @param view
	 */
	public void nextPage(View view) {
		if (currentPageNumber >= (totalPageNumber - 1)) {
			Toast.makeText(this, "已经是最后一页了", 0).show();
			return;
		}
		currentPageNumber++;
		fillData();
	}

	/**
	 * 跳转
	 * 
	 * @param view
	 */
	public void jump(View view) {
		/**
		 * Integer.parseInt()把String 型转换为Int型，
		 * 
		 * Integer.valueOf()把String 型转换为Integer对象。
		 */
		String str_pagenumber = et_callsms_pagenumber.getText().toString()
				.trim();
		if (TextUtils.isEmpty(str_pagenumber)) {
			Toast.makeText(this, "请输入页面号", 0).show();
		} else {
			int number = Integer.parseInt(str_pagenumber);
			if (number >= 0 && number < totalPageNumber) {
				currentPageNumber = number;
				fillData();
			} else {
				Toast.makeText(this, "请输入正确的页面号", 0).show();
			}
		}
	}

}
