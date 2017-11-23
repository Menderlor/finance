package com.cedarhd.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * viewpage内容适配器，一下几个方法必须复写
 * 
 */
public class PicViewPagerAdapter extends PagerAdapter {

	private List<View> pagerList;

	/**
	 * 首页viewpage内容适配器
	 */
	public PicViewPagerAdapter(List<View> pagerList) {
		super();
		this.pagerList = pagerList;
	}

	@Override
	public int getCount() {
		// 返回填充子项个数
		return pagerList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// super.destroyItem(container, position, object);
		// 删除ViewPager的子选项
		container.removeView(pagerList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 初始化ViewPager的子选项
		container.addView(pagerList.get(position));
		return pagerList.get(position);
	}

}