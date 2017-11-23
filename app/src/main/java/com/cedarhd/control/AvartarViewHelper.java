package com.cedarhd.control;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.User;
import com.cedarhd.utils.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 自定义员工头像控件
 * 
 * @author bohr
 * 
 */
public class AvartarViewHelper {
	private Context context;
	private LinearLayout view; // 添加头像控件布局
	/** 头像图片 */
	private ImageView ivAvatar;
	/** 未读红色圆点 */
	private ImageView ivReadDot; //
	private TextView tvName; // 头像人的姓名

	/**
	 * 头像控件
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 */
	public AvartarViewHelper(Context context, String id, AvartarView view) {
		super();
		this.context = context;
		this.view = view;
		initViews();
		init(id);
	}

	/**
	 * 头像控件
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 * @param isShowName
	 *            是否在图片底部显示姓名
	 */
	public AvartarViewHelper(Context context, String id, AvartarView view,
			boolean isShowName) {
		super();
		this.context = context;
		this.view = view;
		initViews(isShowName);
		init(id);
	}

	/**
	 * 头像控件
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 */
	public AvartarViewHelper(Context context, int id, AvartarView view) {
		super();
		this.context = context;
		this.view = view;
		initViews();
		init(id);
	}

	/**
	 * 头像控件
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 * @param postion
	 *            图片在图片列表中的位置
	 */
	public AvartarViewHelper(Context context, String id, AvartarView view,
			int postion) {
		super();
		this.context = context;
		this.view = view;
		initViews();
		init(id, postion);
	}

	/**
	 * 头像控件
	 * 
	 * @param context
	 *            上下文
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 * @param postion
	 *            图片在图片列表中的位置
	 */
	public AvartarViewHelper(Context context, int id, AvartarView view,
			int postion) {
		super();
		this.context = context;
		this.view = view;
		initViews();
		init(id, postion);
	}

	/**
	 * * @param context 上下文
	 * 
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 * @param postion
	 *            图片在图片列表中的位置
	 * @param width
	 *            图片宽度 dp
	 * @param height
	 *            图片高度 dp
	 * @param isShowName
	 *            是否在图片底部显示姓名
	 */
	public AvartarViewHelper(Context context, int id, AvartarView view,
			int postion, int width, int height, boolean isShowName) {
		super();
		this.context = context;
		this.view = view;
		initViews(width, height, isShowName);
		init(id, postion);
	}

	/**
	 * * @param context 上下文
	 * 
	 * @param id
	 *            头像人的id
	 * @param view
	 *            头像控件
	 * @param width
	 *            图片宽度 dp
	 * @param height
	 *            图片高度 dp
	 * @param isShowName
	 *            是否在图片底部显示姓名
	 */
	public AvartarViewHelper(Context context, int id, AvartarView view,
			int width, int height, boolean isShowName) {
		super();
		this.context = context;
		this.view = view;
		initViews(width, height, isShowName);
		init(id);
	}

	private void initViews() {
		ivAvatar = (ImageView) view.findViewById(R.id.iv_pic_avatar);
		ivReadDot = (ImageView) view.findViewById(R.id.iv_dot_avatar);
		tvName = (TextView) view.findViewById(R.id.tv_name_avatar);
	}

	private void initViews(int width, int height, boolean isShowName) {
		ivAvatar = (ImageView) view.findViewById(R.id.iv_pic_avatar);
		ivReadDot = (ImageView) view.findViewById(R.id.iv_dot_avatar);
		tvName = (TextView) view.findViewById(R.id.tv_name_avatar);
		int w = (int) ViewHelper.dip2px(context, width);
		int h = (int) ViewHelper.dip2px(context, height);
		LogUtils.i("avatarWH", w + "--" + h);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w,
				h);
		params.topMargin = (int) ViewHelper.dip2px(context, 3);
		// ivAvatar.setLayoutParams(new LinearLayout.LayoutParams(w, h));
		ivAvatar.setLayoutParams(params);
		// ivAvatarLayer.setLayoutParams(params);
		if (!isShowName) {
			tvName.setVisibility(View.GONE);
		}
	}

	private void initViews(boolean isShowName) {
		ivAvatar = (ImageView) view.findViewById(R.id.iv_pic_avatar);
		// ivAvatarLayer = (ImageView) view.findViewById(R.id.iv_layer_avatar);
		tvName = (TextView) view.findViewById(R.id.tv_name_avatar);
		if (!isShowName) {
			tvName.setVisibility(View.GONE);
		}
	}

	private void init(String id) {
		DictionaryHelper dictionaryHelper = new DictionaryHelper(context);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
				.showImageForEmptyUri(R.drawable.tx) //
				.showImageOnFail(R.drawable.tx).build();//
		String name = dictionaryHelper.getUserNameById(id);
		String avatarUrl = dictionaryHelper.getUserPhoto(id);
		if (!TextUtils.isEmpty(avatarUrl)) {
			// pictureUtils.setAvatar(avatarUrl, ivAvatar);
			ImageLoader.getInstance().displayImage(Global.BASE_URL + avatarUrl,
					ivAvatar, defaultOptions, Global.mUser.Passport);
		} else {
			ivAvatar.setImageResource(R.drawable.tx);
		}

		LogUtils.i("name", "--->" + name);
		tvName.setText(name);
	}

	/**
	 * 初始化
	 * 
	 * @param id
	 * @param postion
	 */
	private void init(String id, int postion) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
				.showImageForEmptyUri(R.drawable.tx) //
				.showImageOnFail(R.drawable.tx).build();//
		DictionaryHelper dictionaryHelper = new DictionaryHelper(context);
		// String name = dictionaryHelper.getUserNameById(id);
		// String avatarUrl = dictionaryHelper.getUserPhoto(id);
		User user = dictionaryHelper.getUser(id);
		String name = user.getUserName();
		String avatarUrl = user.getAvatarURI();
		int pos = (Integer) view.getTag();
		tvName.setText(name);
		if (!TextUtils.isEmpty(avatarUrl)) {
			ivAvatar.setTag(pos); // 标识位置
			// pictureUtils.setAvatar(avatarUrl, ivAvatar, postion);
			ImageLoader.getInstance().displayImage(Global.BASE_URL + avatarUrl,
					ivAvatar, defaultOptions, Global.mUser.Passport);
		} else {
			ivAvatar.setImageResource(R.drawable.tx);
		}
	}

	private void init(int id) {
		String userId = id + "";
		init(userId);
	}

	private void init(int id, int postion) {
		String userId = id + "";
		init(userId, postion);
	}

	/***
	 * 设置已读未读
	 * 
	 * @param isRead
	 */
	public void setRead(boolean isRead) {
		if (isRead) {
			ivReadDot.setVisibility(View.GONE);
		} else {
			ivReadDot.setVisibility(View.VISIBLE);
		}
	}
}
