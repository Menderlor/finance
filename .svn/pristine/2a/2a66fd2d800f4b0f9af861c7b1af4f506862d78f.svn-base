package com.cedarhd.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;

/**
 * 选择数量控件
 * 
 * @author Administrator
 * 
 */
public class SelectConut_BoeryunControl extends LinearLayout {
	private View view;
	private EditText etCount;
	private int countNum = 0;

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SelectConut_BoeryunControl(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		view = LayoutInflater.from(context).inflate(
				R.layout.control_select_count, this, true);
		setOnclickListener(view);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public SelectConut_BoeryunControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		view = LayoutInflater.from(context).inflate(
				R.layout.control_select_count, this, true);
		setOnclickListener(view);
	}

	/**
	 * @param context
	 */
	public SelectConut_BoeryunControl(Context context) {
		super(context);
		view = LayoutInflater.from(context).inflate(
				R.layout.control_select_count, this, true);
		setOnclickListener(view);
	}

	/**
	 * 对外暴露方法，取得数量
	 * 
	 * @return
	 */
	public int getCountNum() {
		try {
			countNum = Integer.parseInt(etCount.getText().toString());
		} catch (Exception e) {
		}
		return countNum;
	}

	private void setOnclickListener(View view) {
		TextView tvSub = (TextView) view
				.findViewById(R.id.tv_sub_select_control);
		TextView tvAdd = (TextView) view
				.findViewById(R.id.tv_add_select_control);
		etCount = (EditText) view.findViewById(R.id.et_count_select_control);

		// 减数量
		tvSub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					countNum = Integer.parseInt(etCount.getText().toString());
				} catch (Exception e) {
				}
				if (countNum > 0) {
					countNum--;
					etCount.setText("" + countNum);
				}
			}
		});

		// 加数量
		tvAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					countNum = Integer.parseInt(etCount.getText().toString());
				} catch (Exception e) {
				}
				countNum++;
				etCount.setText("" + countNum);
			}
		});

		etCount.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					try {
						countNum = Integer.parseInt(etCount.getText()
								.toString());
					} catch (Exception e) {
					}
				}
			}
		});
	}

}
