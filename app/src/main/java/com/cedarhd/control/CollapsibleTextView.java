package com.cedarhd.control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.cedarhd.R;

public class CollapsibleTextView extends LinearLayout implements
		OnClickListener {

	/** default text show max lines */
	private static final int DEFAULT_MAX_LINE_COUNT = 3;

	private static final int COLLAPSIBLE_STATE_NONE = 0;

	/*** 状态为收缩 */
	private static final int COLLAPSIBLE_STATE_SHRINKUP = 1;

	/*** 状态为伸展 */
	private static final int COLLAPSIBLE_STATE_SPREAD = 2;

	private TextView desc;
	private TextView descOp;

	private String shrinkup;
	private String spread;
	private int mState;
	private boolean flag;

	public CollapsibleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		shrinkup = context.getString(R.string.desc_shrinkup);
		spread = context.getString(R.string.desc_spread);
		View view = inflate(context, R.layout.collapsible_textview, this);
		view.setPadding(0, -1, 0, 0);
		desc = (TextView) view.findViewById(R.id.desc_tv);
		descOp = (TextView) view.findViewById(R.id.desc_op_tv);
		descOp.setOnClickListener(this);
	}

	public CollapsibleTextView(Context context) {
		this(context, null);
	}

	public final void setDesc(CharSequence charSequence, BufferType bufferType) {
		desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		if (charSequence.length() < 20) {
			desc.setText(charSequence);
			mState = COLLAPSIBLE_STATE_SHRINKUP;
			requestLayout();
		} else {
			desc.setText(charSequence, null);
			mState = COLLAPSIBLE_STATE_SPREAD;
			requestLayout();
		}
	}

	@Override
	public void onClick(View v) {
		flag = false;
		requestLayout();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (!flag) {
			flag = true;
			if (desc.getLineCount() <= DEFAULT_MAX_LINE_COUNT) {
				mState = COLLAPSIBLE_STATE_NONE;
				descOp.setVisibility(View.GONE);
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT + 1);
			} else {
				post(new InnerRunnable());
			}
		}
	}

	class InnerRunnable implements Runnable {
		@Override
		public void run() {
			if (mState == COLLAPSIBLE_STATE_SPREAD) {
				desc.setMaxLines(DEFAULT_MAX_LINE_COUNT);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(spread);
				mState = COLLAPSIBLE_STATE_SHRINKUP;
			} else if (mState == COLLAPSIBLE_STATE_SHRINKUP) {
				desc.setMaxLines(Integer.MAX_VALUE);
				descOp.setVisibility(View.VISIBLE);
				descOp.setText(shrinkup);
				mState = COLLAPSIBLE_STATE_SPREAD;
			}
		}
	}
}
