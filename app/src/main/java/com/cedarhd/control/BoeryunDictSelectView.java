package com.cedarhd.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.models.字典;

/***
 * 字典选择控件,文本控件的形式显示内容，点击弹出字典选择页面，选择完毕将字典名称显示TextView，并把[字典]实体以TAG的形式保存
 * 
 * @author K
 * 
 *         2015/09/29 14:05
 */
public class BoeryunDictSelectView extends TextView {

	private String mDictName;

	private 字典 mDict;
	private Context mContext;
	private DictionaryQueryDialogHelper dictDialogHelper;

	public BoeryunDictSelectView(Context context) {
		this(context, null, 0);
	}

	public BoeryunDictSelectView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BoeryunDictSelectView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// 初始化属性
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.BoeryunDictSelectView, defStyle, defStyle);
		int n = typedArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.BoeryunDictSelectView_dictName:
				// 获取属性中 字典名称
				mDictName = typedArray.getString(attr);
				break;
			default:
				break;
			}
		}

		mContext = context;
		typedArray.recycle();
		// setFocusable(false);
		initData();
		setOnClick();
	}

	private void initData() {
		dictDialogHelper = DictionaryQueryDialogHelper.getInstance(mContext);
	}

	private void setOnClick() {
		this.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDict = null;
				dictDialogHelper.show(BoeryunDictSelectView.this, mDictName);
				dictDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								setText(dict.getName());
								setTag(dict);

								mDict = dict;
							}
						});
			}
		});
	}

	/** 获取选中字典,未选中则为空 */
	public 字典 getSelectDict() {
		return mDict;
	}
}
