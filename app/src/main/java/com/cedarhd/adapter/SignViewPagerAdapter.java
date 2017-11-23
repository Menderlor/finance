package com.cedarhd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cedarhd.fragment.AskMeFrament;

public class SignViewPagerAdapter extends FragmentPagerAdapter {

	private String[] methodNames = new String[] { "",
			"", "" ,""};

	public SignViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int pos) {
		AskMeFrament frament = new AskMeFrament();
		frament.methodName = methodNames[pos];
		return frament;
	}

	@Override
	public int getCount() {
		return 4;
	}

}
