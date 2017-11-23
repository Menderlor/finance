package com.cedarhd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class SpaceViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> listFragment;

		public SpaceViewPagerAdapter(FragmentManager fm,
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


