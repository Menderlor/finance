package com.cedarhd.control.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/***
 * 解决ListView嵌套显示不全而使用自定义ListView
 * 
 * @author K
 * 
 */
public class BoeryunNoScrollListView extends ListView {

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BoeryunNoScrollListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public BoeryunNoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 */
	public BoeryunNoScrollListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
