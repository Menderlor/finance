package com.cedarhd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class AskForMoreFormFragmentAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragmentList;

	public AskForMoreFormFragmentAdapter(FragmentManager fm,
			List<Fragment> fragmentList) {
		super(fm);
		this.fragmentList = fragmentList;
	}

	@Override
	public Fragment getItem(int pos) {
		return fragmentList.get(pos);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

}
