package com.itheima.mobilesafe.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.AppLockDao;
import com.itheima.mobilesafe.domain.AppInfo;
import com.itheima.mobilesafe.engine.AppInfoParser;

public class LockFragment extends Fragment {

	private TextView tv_status;
	private ListView lv_locked;

	private List<AppInfo> lockappInfos;

	private lockAdapter adapter;
	private AppLockDao dao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_locked, null);
		tv_status = (TextView) view.findViewById(R.id.tv_status);
		lv_locked = (ListView) view.findViewById(R.id.lv_locked);
		return view;

	}

	@Override
	public void onStart() {
		super.onStart();
		dao = new AppLockDao(getActivity());
		lockappInfos = new ArrayList<AppInfo>();
		List<AppInfo> infos = AppInfoParser.getAppInfos(getActivity());
		for (AppInfo info : infos) {
			if (dao.find(info.getPackName())) {
				// 查到了，说明是加锁应用，添加进来
				lockappInfos.add(info);
			} else {
				// 没查到，安静的待着
			}
		}
		adapter = new lockAdapter();
		lv_locked.setAdapter(adapter);

	}

	private class lockAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			tv_status.setText("已加锁(" + lockappInfos.size() + ")个");
			return lockappInfos.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getActivity(), R.layout.item_locked, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.iv_unlock = (ImageView) view
						.findViewById(R.id.iv_app_unlock);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(lockappInfos.get(position)
					.getIcon());
			holder.tv_name.setText(lockappInfos.get(position).getApkName());
			holder.iv_unlock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					TranslateAnimation ta = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, -1.0f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					ta.setDuration(300);
					view.startAnimation(ta);

					new Thread(new Runnable() {

						@Override
						public void run() {
							SystemClock.sleep(300);
							dao.delete(lockappInfos.get(position).getPackName());
							lockappInfos.remove(position);
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									adapter.notifyDataSetChanged();
								}
							});
						}
					}).start();

				}
			});

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

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_unlock;
	}
}
