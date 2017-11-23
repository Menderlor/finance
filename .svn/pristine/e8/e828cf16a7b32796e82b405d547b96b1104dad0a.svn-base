package com.cedarhd.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;

public class MyProgressBar extends LinearLayout {
	private Context context;
	private TextView textView;

	public MyProgressBar(Context context) {
		super(context);
	}

	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(
				R.layout.myprogress, this, true);
	}
	
	public void setInfo(CharSequence tilte)
	{
		textView.setText(tilte);
	}
	
}
