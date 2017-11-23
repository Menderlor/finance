package com.cedarhd.listener;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.cedarhd.control.listview.PullToRefreshListView;

/***
 * 文本框变化监听
 * 
 * @author K
 * 
 */
public class TextWatcher_Search implements TextWatcher {

	// 缓存上一次文本框内是否为空
	private boolean mIsNull = true;

	private EditText mSearchView;

	private Drawable mIconSearchDefault; // 搜索文本框默认图标
	private Drawable mIconSearchClear; // 搜索文本框清除文本内容图标

	private TextWatcher_SearchListener mTextWatcher_SearchListener;

	/***
	 * 
	 * @param searchView
	 * @param iconSearchDefault
	 * @param iconSearchClear
	 */
	public TextWatcher_Search(EditText searchView, Drawable iconSearchDefault,
			Drawable iconSearchClear) {
		this.mSearchView = searchView;
		this.mIconSearchDefault = iconSearchDefault;
		this.mIconSearchClear = iconSearchClear;
	}

	public void setTextWatcher_SearchListener(
			TextWatcher_SearchListener textWatcher_SearchListener) {
		mTextWatcher_SearchListener = textWatcher_SearchListener;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (TextUtils.isEmpty(s)) {
			if (!mIsNull) {
				mSearchView.setCompoundDrawablesWithIntrinsicBounds(null, null,
						mIconSearchDefault, null);
				mIsNull = true;
			}
		} else {
			if (mIsNull) {
				mSearchView.setCompoundDrawablesWithIntrinsicBounds(null, null,
						mIconSearchClear, null);
				mIsNull = false;

			}
		}

		if (mTextWatcher_SearchListener != null) {
			mTextWatcher_SearchListener.onSearch(mSearchView.getText()
					.toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	/**
	 * 随着文本框内容改变动态改变列表内容
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public interface TextWatcher_SearchListener {
		/**
		 * Called when the list should be refreshed.
		 * <p>
		 * A call to {@link PullToRefreshListView #onRefreshComplete()} is
		 * expected to indicate that the refresh has completed.
		 */
		public void onSearch(String str);
	}
}
