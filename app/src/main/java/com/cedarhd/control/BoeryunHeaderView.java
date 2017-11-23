package com.cedarhd.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cedarhd.R;

/***
 * 标题栏
 * 
 * @author K
 * 
 */
public class BoeryunHeaderView extends RelativeLayout {
	private boolean mIsVisbleBackIco;
	private boolean mIsVisbleFilterIco;
	private boolean mIsVisbleSaveIco;
	private boolean mIsVisbleAddIco;
	private boolean mIsVisbleRightText;

	private Bitmap mFilterBitmap;
	private Bitmap mSaveBitmap;

	private String mTitle = "标题";
	private String mRightTitle = "标题";

	public ImageView ivBack;
	public ImageView ivFilter;
	public ImageView ivSave;
	private TextView tvTitle;
	private TextView tvRightTitle;

	public BoeryunHeaderView(Context context) {
		this(context, null, 0);
	}

	public BoeryunHeaderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BoeryunHeaderView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray typedArray = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.BoeryunHeaderView, defStyleAttr, 0);
		int n = typedArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int index = typedArray.getIndex(i);
			switch (index) {
			case R.styleable.BoeryunHeaderView_titleText:
				mTitle = typedArray.getString(index);
				break;
			case R.styleable.BoeryunHeaderView_titleRightText:
				mRightTitle = typedArray.getString(index);
				break;
			case R.styleable.BoeryunHeaderView_isVisbleBackIco:
				mIsVisbleBackIco = typedArray.getBoolean(index, false);
				break;
			case R.styleable.BoeryunHeaderView_isVisbleFilterIco:
				mIsVisbleFilterIco = typedArray.getBoolean(index, false);
				break;
			case R.styleable.BoeryunHeaderView_isVisbleSaveIco:
				mIsVisbleSaveIco = typedArray.getBoolean(index, false);
				break;
			case R.styleable.BoeryunHeaderView_isVisbleAddIco:
				mIsVisbleAddIco = typedArray.getBoolean(index, false);
				break;
			case R.styleable.BoeryunHeaderView_isVisbleRightText:
				mIsVisbleRightText = typedArray.getBoolean(index, false);
				break;
			case R.styleable.BoeryunHeaderView_filterImgSrc:
				mFilterBitmap = BitmapFactory.decodeResource(getResources(),
						typedArray.getResourceId(index, 0));
				break;
			case R.styleable.BoeryunHeaderView_addImgSrc:
				mSaveBitmap = BitmapFactory.decodeResource(getResources(),
						typedArray.getResourceId(index, 0));
				break;
			default:
				break;
			}
		}

		initViews(context);
		setOnEvent();
	}

	private void initViews(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.headerview_boeryun, this, true);
		ivBack = (ImageView) view.findViewById(R.id.iv_back_headerview);
		ivFilter = (ImageView) view.findViewById(R.id.iv_filter_headerview);
		ivSave = (ImageView) view.findViewById(R.id.iv_add_headerview);
		tvTitle = (TextView) view.findViewById(R.id.tv_title_headerview);
		tvRightTitle = (TextView) view
				.findViewById(R.id.tv_right_title_headerview);

		tvTitle.setText(mTitle + "");
		if (mIsVisbleBackIco) {
			ivBack.setVisibility(VISIBLE);
		}

		if (mIsVisbleFilterIco) {
			ivFilter.setVisibility(VISIBLE);
			if (mFilterBitmap != null) {
				ivFilter.setImageBitmap(mFilterBitmap);
			}
		}

		if (mIsVisbleRightText) {
			tvRightTitle.setVisibility(VISIBLE);
			tvRightTitle.setText(mRightTitle);
		}

		if (mIsVisbleAddIco || mIsVisbleSaveIco) {
			ivSave.setVisibility(VISIBLE);
			if (mIsVisbleSaveIco) {
				// 如果是保存 显示对勾图标
				ivSave.setImageResource(R.drawable.ic_check);
			} else if (mIsVisbleAddIco) {
				// 如果是新建 显示加号图标
				ivSave.setImageResource(R.drawable.ic_add_menu);
			}

			if (mSaveBitmap != null) {
				ivSave.setImageBitmap(mSaveBitmap);
			}
		}
	}

	private void setOnEvent() {
		if (mIsVisbleBackIco) {
			ivBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mButtonClickListener != null) {
						mButtonClickListener.onClickBack();
					}
				}
			});
		}

		if (mIsVisbleFilterIco) {
			ivFilter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mButtonClickListener != null) {
						mButtonClickListener.onClickFilter();
					}
				}
			});
		}

		if (mIsVisbleAddIco || mIsVisbleSaveIco) {
			ivSave.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mButtonClickListener != null) {
						mButtonClickListener.onClickSaveOrAdd();
					}
				}
			});
		}

		if (mIsVisbleRightText) {
			tvRightTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mButtonClickListener != null
							&& mButtonClickListener instanceof OnButtonClickRightListener) {
						((OnButtonClickRightListener) mButtonClickListener)
								.onRightTextClick();
					}
				}
			});
		}
	}

	/** 设置顶部标题 */
	public void setTitle(String title) {
		this.mTitle = title;
		tvTitle.setText(mTitle);
	}

	public void setRightTitle(String title) {
		this.mRightTitle = title;
		tvRightTitle.setText(title);
	}

	/** 设置右上角文本按钮可见 */
	public void setRightTitleVisible(boolean isVisible) {
		int visible = isVisible ? View.VISIBLE : View.GONE;
		tvRightTitle.setVisibility(visible);
	}

	private OnButtonClickListener mButtonClickListener;

	public interface OnButtonClickListener {
		void onClickBack();

		void onClickFilter();

		void onClickSaveOrAdd();
	}

	public interface OnButtonClickRightListener extends OnButtonClickListener {
		void onRightTextClick();
	}

	/** 监听顶部按钮点击事件 */
	public void setOnButtonClickListener(
			OnButtonClickListener buttonClickListener) {
		this.mButtonClickListener = buttonClickListener;
	}

	/***
	 * 包括右侧文字内容点击事件监听
	 * 
	 * @param buttonClickRightListener
	 */
	public void setmButtonClickRightListener(
			OnButtonClickRightListener buttonClickRightListener) {
		this.mButtonClickListener = buttonClickRightListener;
	}

}
