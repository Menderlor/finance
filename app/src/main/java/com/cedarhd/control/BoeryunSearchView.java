package com.cedarhd.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.utils.LogUtils;

/***
 * 顶部搜索控件
 * 
 * @author kjx
 * 
 */
public class BoeryunSearchView extends RelativeLayout {
	private OnSearchedListener mOnSearchedListener;
	private OnButtonClickListener mOnButtonClickListener;
	private OnButtonSearchClickListener mOnButtonSearchClickListener;
	private String mSearchHintText;
	private EditText eText;
	private TextView tv_hint;

	public BoeryunSearchView(Context context) {
		this(context, null, 0);

	}

	public BoeryunSearchView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BoeryunSearchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		View view = LayoutInflater.from(context).inflate(
				R.layout.control_searchview, this, true);

		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.BoeryunSearchView, defStyle, 0);
		int n = typedArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int index = typedArray.getIndex(i);
			switch (index) {
			case R.styleable.BoeryunSearchView_hintText:
				mSearchHintText = typedArray.getString(index);
				break;
			}
		}
		initView(view);
	}

	private void initView(View view) {
		final RelativeLayout rlIco = (RelativeLayout) view
				.findViewById(R.id.rl_search_ico);
		eText = (EditText) view
				.findViewById(R.id.et_search_text);
		tv_hint = (TextView) view.findViewById(R.id.tv_search_hint_text);
		ImageView ivCancle = (ImageView) findViewById(R.id.iv_cancle_control);
		ImageView ivSearch = (ImageView) findViewById(R.id.iv_search_control);

		if (!TextUtils.isEmpty(mSearchHintText)) {
			eText.setHint(mSearchHintText);
		}
		ivCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击取消按钮，清空搜索文字
				eText.setText("");
				rlIco.setVisibility(View.GONE);

				InputSoftHelper.hiddenSoftInput(getContext(), eText);

				if (mOnButtonClickListener != null) {
					mOnButtonClickListener.OnCancle();
				}
			}
		});

		ivSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchStr = eText.getText().toString();
				InputSoftHelper.hiddenSoftInput(getContext(), eText);
				if (mOnButtonClickListener != null) {
					mOnButtonClickListener.OnSearch(searchStr);
				}
			}
		});

		eText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				LogUtils.i("BoeryunSearchView", s + "");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				LogUtils.i("Editable", s + "");
				if (mOnSearchedListener != null) {
					mOnSearchedListener.OnSearched(s.toString());
				}
			}
		});

		/** 覆盖于搜索框上的按钮点击事件 */
		rlIco.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnButtonSearchClickListener != null) {
					mOnButtonSearchClickListener.OnClick();
				} else {
					rlIco.setVisibility(View.GONE);
					eText.requestFocus();
				}
			}
		});

		eText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rlIco.setVisibility(View.GONE);
				} else {
					rlIco.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	/**
	 * 设置提示文字
	 * @param text
	 */
	public void setHintText(String text) {
		if (!TextUtils.isEmpty(text)) {
			tv_hint.setText(text);
		}
	}

	/***
	 * 搜索框按钮点击事件监听
	 * 
	 * @param onButtonClickListener
	 */
	public void setOnButtonClickListener(
			OnButtonClickListener onButtonClickListener) {
		this.mOnButtonClickListener = onButtonClickListener;
	}

	/***
	 * 设置searchView按钮的点击事件
	 * 
	 * @param mOnButtonSearchClickListener
	 */
	public void setmOnButtonSearchClickListener(
			OnButtonSearchClickListener mOnButtonSearchClickListener) {
		this.mOnButtonSearchClickListener = mOnButtonSearchClickListener;
	}

	/** 监听文字变化 */
	public void setOnSearchedListener(OnSearchedListener onSearchedListener) {
		this.mOnSearchedListener = onSearchedListener;

	}

	public interface OnSearchedListener {
		public abstract void OnSearched(String str);
	}

	public interface OnButtonClickListener {
		public abstract void OnCancle();

		public abstract void OnSearch(String searchStr);
	}

	public interface OnButtonSearchClickListener {
		public abstract void OnClick();
	}
}
