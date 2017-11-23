package com.cedarhd.listener;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.cedarhd.helpers.ViewHelper;

public class OnTouchListener_Search implements OnTouchListener {

	private EditText mSearchView;
	private Context mContext;

	public OnTouchListener_Search(EditText searchView, Context context) {
		this.mSearchView = searchView;
		this.mContext = context;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			int curX = (int) event.getX();
			if (curX > v.getWidth() - ViewHelper.dip2px(mContext, 38)
					&& !TextUtils.isEmpty(mSearchView.getText())) {
				mSearchView.setText("");
				int cacheInputType = mSearchView.getInputType();// backup the
																// input type

				mSearchView.setInputType(InputType.TYPE_NULL);// disable soft
																// input
				mSearchView.onTouchEvent(event);// call native handler
				mSearchView.setInputType(cacheInputType);// restore input type
				return true;// consume touch even
			}
			break;
		}
		return false;
	}
}
