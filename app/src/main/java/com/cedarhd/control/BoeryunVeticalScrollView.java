package com.cedarhd.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

/***
 * 纵向滑动的ScrollView，不处理横向滑动事件
 * 
 * @author K
 * 
 */
public class BoeryunVeticalScrollView extends ScrollView {

	private GestureDetector mGestureDetector;

	public BoeryunVeticalScrollView(Context context) {
		super(context);
		init();
	}

	public BoeryunVeticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoeryunVeticalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		mGestureDetector = new GestureDetector(getContext(),
				new YScrollDetector());
		setFadingEdgeLength(0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev)
				&& mGestureDetector.onTouchEvent(ev);
	}

	private class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// scrollView只处理纵向滑动
			if (Math.abs(distanceY) >= Math.abs(distanceX)) {
				return true;
			}
			return false;
		}
	}
}
