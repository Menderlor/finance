package com.cedarhd.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cedarhd.fragment.AskMeFragment;

/** 申请与审批页卡切换适配器 */
public class ApplyViewPagerAdapter extends FragmentPagerAdapter {

	/** 接口名数组：待我审批、我的申请、所有 */
	private String[] methodNames = new String[] { "Flow/GetApprovalFlow/",
			"Flow/GetApplyFlow/", "Flow/GetAllFlow/" };

	public ApplyViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int pos) {
		// AskMeFrament frament = new AskMeFrament();
		AskMeFragment frament = new AskMeFragment();
		frament.methodName = methodNames[pos];
		return frament;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
