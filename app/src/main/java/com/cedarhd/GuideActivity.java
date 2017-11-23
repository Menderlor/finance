package com.cedarhd;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.cedarhd.adapter.MyPagerAdapter;
import com.cedarhd.control.MyBaseActivity;

import java.util.ArrayList;
import java.util.List;

/***
 * 引导页面
 * 
 * 
 */
public class GuideActivity extends MyBaseActivity {
	private ViewPager viewPager;
	private View view1, view2, view3, view4;
	public LayoutInflater inflater;
	private List<View> list;
	public boolean isCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		isCount = load();
		if (isCount) {
			skip(NavActivity.class);
			finish();
		} else {
			inflater = LayoutInflater.from(this);
			view1 = inflater.inflate(R.layout.guide_one, null);
			view2 = inflater.inflate(R.layout.guide_two, null);
			view3 = inflater.inflate(R.layout.guide_three, null);
			view4 = inflater.inflate(R.layout.guide_four, null);
			list = new ArrayList<View>();
			list.add(view1);
			list.add(view2);
			list.add(view3);
			list.add(view4);
			viewPager = (ViewPager) findViewById(R.id.vp_guide_view);
			MyPagerAdapter adapter = new MyPagerAdapter(this, list);
			viewPager.setAdapter(adapter);
			view4.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					skip(LoginActivity.class);
					finish();
					isCount = true;
					save();
				}
			});

		}
	}

	/** 记录是第几次进来 */
	private void save() {
		SharedPreferences preferences = this.getSharedPreferences("Count",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isCount", isCount);
		editor.commit();
	}

	private boolean load() {
		SharedPreferences preferences = this.getSharedPreferences("Count",
				Context.MODE_PRIVATE);
		boolean is = preferences.getBoolean("isCount", false);
		return is;
	}
}
