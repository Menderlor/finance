package com.cedarhd;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.fragment.DynamicAllFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态提醒页面
 * 
 * @author kjx
 */
public class DynamicNewsActivity extends BaseActivity {
	private List<Fragment> listFragment = new ArrayList<Fragment>();
	private Resources resource;
	private FragmentManager fm;
	private int currentPage = 0;
	private ViewPager vPager;
	private DynamicViewPagerAdapter adapter;
	private Context context;
	private TextView tvNoRead;// 日志
	private TextView tvAll;// 周报
	private View viewNoRead;
	private View viewAll;

	private List<TextView> tvList = new ArrayList<TextView>();
	private List<View> viewList = new ArrayList<View>();
	// private DynamicNoReadFragment noReadFragmnet;
	private DynamicAllFragment allFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_dynamic_news);
		initViews();
	}

	private void initViews() {
		resource = getResources();
		fm = getSupportFragmentManager();
		context = DynamicNewsActivity.this;
		// noReadFragmnet = new DynamicNoReadFragment();
		// listFragment.add(noReadFragmnet);
		allFragment = new DynamicAllFragment();
		listFragment.add(allFragment);
		adapter = new DynamicViewPagerAdapter(fm, listFragment);

		tvNoRead = (TextView) findViewById(R.id.tv_dynamic_no_read_news);
		tvAll = (TextView) findViewById(R.id.tv_dynamic_all_news);
		viewNoRead = findViewById(R.id.view_dynamic_no_read__news);
		viewAll = findViewById(R.id.view_dynamic_all_news);
		tvList.add(tvNoRead);
		tvList.add(tvAll);
		viewList.add(viewNoRead);
		viewList.add(viewAll);

		vPager = (ViewPager) findViewById(R.id.vp_dynamic_news);
		vPager.setAdapter(adapter);

		tvNoRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				currentPage = 0;
				setCurrentPager(0);
			}
		});
		tvAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTab();
				currentPage = 1;
				setCurrentPager(1);
			}
		});
		vPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				setTab();
				currentPage = pos;
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

	/**
	 * 设置顶部页标签切换颜色
	 */
	private void setTab() {
		tvNoRead.setTextColor(getResources().getColor(R.color.gray));
		tvAll.setTextColor(getResources().getColor(R.color.gray));
		viewNoRead.setVisibility(View.INVISIBLE);
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

	class DynamicViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> listFragment;

		public DynamicViewPagerAdapter(FragmentManager fm,
				List<Fragment> listFragment) {
			super(fm);
			this.listFragment = listFragment;
		}

		@Override
		public Fragment getItem(int pos) {
			return listFragment.get(pos);
		}

		@Override
		public int getCount() {
			return listFragment.size();
		}

	}

	private long lastClickTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - lastClickTime > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				lastClickTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
