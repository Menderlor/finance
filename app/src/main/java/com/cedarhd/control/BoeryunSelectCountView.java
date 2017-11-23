package com.cedarhd.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;

/***
 * 自定义数量选择控件
 * 
 * @author K
 * 
 */
@SuppressLint("NewApi")
public class BoeryunSelectCountView extends LinearLayout {
	private int mNum = 1;

	/** 是否可编辑，如果为false只能查看不能编辑 */
	private boolean mIsEditble = true;

	TextView tvDecrease;
	TextView tvIncrease;
	TextView tvNum;

	private OnNumChanged mOnNumChanged;

	public BoeryunSelectCountView(Context context) {
		super(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.select_count_view, this, true);
		initViews(view);
	}

	public BoeryunSelectCountView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(
				R.layout.select_count_view, this, true);
		initViews(view);
	}

	public BoeryunSelectCountView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		View view = LayoutInflater.from(context).inflate(
				R.layout.select_count_view, this, true);
		initViews(view);
	}

	private void initViews(View view) {
		tvDecrease = (TextView) findViewById(R.id.tv_decrease_select_count);
		tvIncrease = (TextView) findViewById(R.id.tv_increate_select_count);
		tvNum = (TextView) findViewById(R.id.tv_num_select_count);

		tvDecrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mNum > 1) {
					mNum--;
					setNum(mNum);
				}
			}
		});

		tvIncrease.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mNum < 200) {
					mNum++;
					setNum(mNum);
				}
			}
		});
	}

	/***
	 * 获取选择数量控件值的大小
	 * 
	 * @return
	 */
	public int getNum() {
		return mNum;
	}

	public void setNum(int mNum) {
		this.mNum = mNum;
		tvNum.setText("" + mNum);

		if (mOnNumChanged != null) {
			mOnNumChanged.onchange(mNum);
		}
	}

	/** 是否可编辑，如果为false只能查看不能编辑 */
	public void setEnabled(boolean isEnble) {
		this.mIsEditble = isEnble;

		if (!mIsEditble) {
			tvDecrease.setEnabled(false);
			tvIncrease.setEnabled(false);
		}
	}

	/** 设置数值变化监听 */
	public void setOnNumChangedeListener(OnNumChanged onNumChanged) {
		this.mOnNumChanged = onNumChanged;
	}

	/***
	 * 数值变化监听
	 * 
	 * @author K
	 * 
	 */
	public interface OnNumChanged {
		public abstract void onchange(int value);
	}
}
