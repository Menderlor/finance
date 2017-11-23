package com.cedarhd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 工作日志，周总结，月总结的内容适配器
 * 
 * @author kjx
 * 
 */
public class WorkLogViewPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> listFragment;

	public WorkLogViewPagerAdapter(FragmentManager fm,
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
