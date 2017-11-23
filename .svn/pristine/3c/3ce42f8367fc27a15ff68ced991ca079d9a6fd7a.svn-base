package com.cedarhd.control;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;

/***
 * 进度对话框
 * 
 * @author K
 * 
 */
@SuppressLint("NewApi")
public class BoeryunProgressDialog extends ProgressDialog {

	private ImageView ivLoading;
	private TextView tvContent;

	private String mContent;

	private Context mContext;

	public BoeryunProgressDialog(Context context) {
		super(context);
		this.mContext = context;
		this.mContent = "正在加载中...";
	}

	/**
	 * @param context
	 * @param mContent
	 *            加载问题提示，建议不多于8字
	 */
	public BoeryunProgressDialog(Context context, String mContent) {
		super(context);
		this.mContext = context;
		this.mContent = mContent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		// initRolateAnimation();
		initFramAnimation();
	}

	private void initView() {
		setContentView(R.layout.dialog_progress);
		ivLoading = (ImageView) findViewById(R.id.iv_pbar_loading);
		tvContent = (TextView) findViewById(R.id.tv_content_pbar_loading);
		tvContent.setText(mContent);
	}

	/***
	 * 旋转动画
	 */
	private void initRolateAnimation() {
		final RotateAnimation animation = new RotateAnimation(0, 359,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setRepeatCount(-1);
		animation.setDuration(500);
		ivLoading.startAnimation(animation);
		ivLoading.post(new Runnable() {
			@Override
			public void run() {
				animation.start();
			}
		});
	}

	/** 初始化帧动画 */
	private void initFramAnimation() {
		Resources resources = mContext.getResources();
		AnimationDrawable animDrawable = new AnimationDrawable();
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_1), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_2), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_3), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_4), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_5), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_6), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_7), 120);
		animDrawable.addFrame(
				resources.getDrawable(R.drawable.loading_animation_big_8), 120);
		// ivLoading.setBackground(animDrawable);
		animDrawable.setOneShot(false);
		ivLoading.setImageDrawable(animDrawable);
		animDrawable.start();

	}

	/***
	 * 设置文字显示内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		tvContent.setText("" + content);
	}

	/***
	 * 设置文字内容是否可见
	 * 
	 * @param visbled
	 */
	public void setContentVisble(boolean visbled) {
		if (visbled) {
			tvContent.setVisibility(View.VISIBLE);
		} else {
			tvContent.setVisibility(View.GONE);
		}
	}
}
