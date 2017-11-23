package com.cedarhd;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.adapter.AskForMoreFormFragmentAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.fragment.AllFormFragment;
import com.cedarhd.fragment.LatestFormFragment;
import com.cedarhd.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版更多表单列表
 * 
 * @author kjx
 * @since 2014/07/23 16:02
 */
public class AskForLeaveMoreFormFragmentActivity extends BaseActivity {
	private Resources resource;
	private ViewPager vPager;
	private AskForMoreFormFragmentAdapter adapter;
	private FragmentManager fm;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();

	private ImageView ivRefresh;
	private TextView tvLatest;// 最近
	private TextView tvAll;// 所有
	private View viewLatest;
	private View viewAll;
	private List<TextView> tvList = new ArrayList<TextView>();
	private List<View> viewList = new ArrayList<View>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.askforlevemorefragmentform);
		initViews();
	}

	private void initViews() {
		resource = getResources();
		initData();

		ivRefresh = (ImageView) findViewById(R.id.iv_refresh_all_form);
		tvLatest = (TextView) findViewById(R.id.tv_latest_askformore);
		tvAll = (TextView) findViewById(R.id.tv_all_askformore);
		viewLatest = findViewById(R.id.view_notice_askformore);
		viewAll = findViewById(R.id.view_work_askformore);
		tvList.add(tvLatest);
		tvList.add(tvAll);
		viewList.add(viewLatest);
		viewList.add(viewAll);

		vPager = (ViewPager) findViewById(R.id.vp_askformore);
		fm = getSupportFragmentManager();
		adapter = new AskForMoreFormFragmentAdapter(fm, fragmentList);
		vPager.setAdapter(adapter);
		vPager.setCurrentItem(1);

		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					AllFormFragment formFragment = (AllFormFragment) fragmentList
							.get(1);
					formFragment.reloadDataFromNet();
				} catch (Exception e) {
					LogUtils.e("erro", e.toString());
				}
			}
		});

		tvLatest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				setCurrentPager(0);
			}
		});
		tvAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				setCurrentPager(1);
			}
		});
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setTab();
				tvList.get(pos).setTextColor(
						resource.getColor(R.color.theme_text));
				viewList.get(pos).setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});
	}

	private void initData() {
		fragmentList.add(new LatestFormFragment());
		fragmentList.add(new AllFormFragment());
	}

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		tvLatest.setTextColor(getResources().getColor(R.color.gray));
		tvAll.setTextColor(getResources().getColor(R.color.gray));
		viewLatest.setVisibility(View.INVISIBLE);
		viewAll.setVisibility(View.INVISIBLE);
	}

	/**
	 * 设置当前显示页面
	 */
	public void setCurrentPager(int pos) {
		vPager.setCurrentItem(pos);
		tvList.get(pos).setTextColor(resource.getColor(R.color.theme_text));
		viewList.get(pos).setVisibility(View.VISIBLE);
	}
}
