package com.cedarhd.control;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.utils.LogUtils;

/**
 * @author yangyu 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class TitlePopup extends PopupWindow {

	private static final String TAG = TitlePopup.class.getSimpleName();

	private TextView priase;
	private TextView comment;

	private Context mContext;

	// 列表弹窗的间隔
	protected final int LIST_PADDING = 10;

	// 实例化一个矩形
	private Rect mRect = new Rect();

	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];

	// 判断是否需要添加或更新列表子类项
	private boolean mIsDirty;

	public TitlePopup(Context context) {
		// 设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public TitlePopup(Context context, int width, int height) {
		this.mContext = context;

		// 设置可以获得焦点
		setFocusable(true);
		// 设置弹窗内可点击
		setTouchable(true);
		// 设置弹窗外可点击
		setOutsideTouchable(true);

		// 设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

		setBackgroundDrawable(new BitmapDrawable());

		// 设置弹窗的布局界面
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup, null);
		setContentView(view);
		LogUtils.i(TAG, " -> " + view.getHeight() + "    " + view.getWidth());

	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(final View c) {
		// 获得点击屏幕的位置坐标
		c.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + c.getWidth(),
				mLocation[1] + c.getHeight());
		// priase.setText(mActionItems.get(0).mTitle);

		// 判断是否需要添加或更新列表子类项
		if (mIsDirty) {
			// populateActions();
		}
		LogUtils.i(TAG, "this.height ->  " + this.getHeight());// 50
		LogUtils.i(TAG, "height ->  " + c.getHeight());// 96
		LogUtils.i(TAG, "this.width -> " + this.getWidth());

		LogUtils.i(TAG, "mLocation[1] -> " + (mLocation[1]));

		// 显示弹窗的位置
		// showAtLocation(view, popupGravity, mScreenWidth - LIST_PADDING
		// - (getWidth() / 2), mRect.bottom);
		showAtLocation(c, Gravity.NO_GRAVITY, mLocation[0] - this.getWidth()
				- 10, mLocation[1] - ((this.getHeight() - c.getHeight()) / 2));
	}

	/**
	 * 功能描述：弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener {
		public void onItemClick(ActionItem item, int position);
	}
}
