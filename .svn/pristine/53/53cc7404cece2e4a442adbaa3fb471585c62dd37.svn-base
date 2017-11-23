package com.cedarhd.control;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/***
 * 自定义滑动页卡，可控制是否允许滑动
 * 
 * 
 */
public class BoeryunViewpager extends ViewPager {

	private boolean enabled;

	public BoeryunViewpager(Context context) {
		super(context);
	}

	public BoeryunViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/***
	 * 设置是否可滑动
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// 触摸没有反应就可以了
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
