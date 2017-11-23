package com.cedarhd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;

import java.util.List;

/**
 * Created by K on 2016/1/26.
 */
public class Indicator extends LinearLayout {

	private ViewPager mViewPager;
	private int mColor;

	private List<String> mTitles;
	private OnTitleClickListener mOnTitleClickListener;

	private Paint mPaint;
	private int mMarginTop; // 距离顶部距离
	private int mMarginLeft;// 左侧距离
	private int mWidth; // 滑动下标的宽度
	private int mHeight = 5;// 滑动下标的高度,默认10px
	private int mChildCount = 4; // Tab个数

	/**
	 * 标题正常时的颜色
	 */
	private static final int COLOR_TEXT_NORMAL = 0XFF8c8c8d;

	/**
	 * 标题选中时的颜色
	 */
	private static int color_text_highlight = 0xFF0099ff;

	public Indicator(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.Indicator);
		mColor = typedArray.getColor(R.styleable.Indicator_color_indicator,
				R.color.theme_new);
		typedArray.recycle();

		color_text_highlight = getResources().getColor(R.color.theme_new);

		mPaint = new Paint();
		mPaint.setColor(mColor);
		// 设置画笔抗锯齿
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMarginTop = getMeasuredHeight(); // 默认Tab高度
		int width = getMeasuredWidth();// 计算控件的宽度
		int height = mMarginTop + mHeight;

		mWidth = width / mChildCount;

		// 重新计算控件宽高
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Rect rect = new Rect(mMarginLeft, mMarginTop, mMarginLeft + mWidth,
				mMarginTop + mHeight);
		canvas.drawRect(rect, mPaint);
		super.onDraw(canvas);
	}

	/***
	 * 
	 * @param pos
	 * @param offset
	 */
	public void scroll(int pos, float offset) {
		mMarginLeft = (int) ((pos + offset) * mWidth);
		Log.i("scroll", mMarginLeft + "-" + mMarginTop + "-"
				+ (mMarginLeft + mWidth) + "-" + (mMarginTop + mHeight));
		invalidate();// 强制要求重绘
	}

	/** 翻页 */
	public void onPageChanged(int pos) {
		resetTextViewColor();
		highLightTextView(pos);
	}

	/***
	 * 设置相关联的ViewPager
	 * 
	 * @param mViewPager
	 */
	public void setRelateViewPager(ViewPager mViewPager) {
		this.mViewPager = mViewPager;

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pos) {
				onPageChanged(pos);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				scroll(position, positionOffset);
			}

			@Override
			public void onPageScrollStateChanged(int pos) {

			}
		});
	}

	/**
	 * 设置tab的标题内容 可选，可以自己在布局文件中写死
	 * 
	 * @param datas
	 */
	public void setTabItemTitles(List<String> datas) {
		// 如果传入的list有值，则移除布局文件中设置的view
		if (datas != null && datas.size() > 0) {
			this.removeAllViews();
			this.mTitles = datas;
			this.mChildCount = datas.size();

			for (int i = 0; i < mTitles.size(); i++) {
				final int pos = i;
				final TextView tv = generateTextView(mTitles.get(i));
				// 点击tab滑动viewpage
				tv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						if (mOnTitleClickListener != null) {
							mOnTitleClickListener.onTitleClick(pos);
						}

						if (mViewPager != null) {
							if (pos < mViewPager.getChildCount()) {
								mViewPager.setCurrentItem(pos);
								onPageChanged(pos);
							}
						}
					}
				});
				// 添加view
				addView(tv);
			}

			onPageChanged(0);
			invalidate();

			// 设置item的click事件
			// setItemClickEvent();
		}

	}

	/**
	 * 根据标题生成我们的TextView
	 * 
	 * @param text
	 * @return
	 */
	private TextView generateTextView(String text) {
		TextView tv = new TextView(getContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.width = getScreenWidth() / mChildCount;
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(COLOR_TEXT_NORMAL);
		tv.setText(text);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setLayoutParams(lp);
		return tv;
	}

	/**
	 * 高亮文本
	 * 
	 * @param position
	 */
	protected void highLightTextView(int position) {
		View view = getChildAt(position);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(color_text_highlight);
		}

	}

	/**
	 * 重置文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}

	/**
	 * 获得屏幕的宽度
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/***
	 * 设置绑定顶部标签的监听事件
	 * 
	 * @param onClickListener
	 */
	public void setOnTitleClickListener(OnTitleClickListener onClickListener) {
		mOnTitleClickListener = onClickListener;
	}

	public interface OnTitleClickListener {
		void onTitleClick(int pos);
	}
}
