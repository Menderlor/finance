package com.cedarhd.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.cedarhd.R;
import com.cedarhd.utils.LogUtils;

import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/***
 * 字典列表选择滑轮
 * 
 * @author K 2015/10/01 10:10
 */
public class DictWheelPicker {

	private Context mContext;
	private OnSelectedListener mListener;

	/** 选中字典列表序号 */
	private int mSelectedIndex;

	/**
	 * @param mContext
	 */
	public DictWheelPicker(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 弹出日期滚轮
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	@SuppressWarnings("deprecation")
	public void showDateWheel(int mainLayoutId, String[] datas) {
		View parentView = ((Activity) mContext).findViewById(mainLayoutId);
		showDateWheel(parentView, datas);
	}

	/**
	 * 弹出日期滚轮
	 * 
	 * @param mainLayoutId
	 *            layout文件的根节点id
	 * @param datas
	 *            数据源
	 */
	public void showDateWheel(int mainLayoutId, List<String> datas) {
		View parentView = ((Activity) mContext).findViewById(mainLayoutId);
		showDateWheel(parentView, datas);
	}

	/**
	 * 弹出日期滚轮
	 * 
	 * @param tvStartDate
	 */
	@SuppressWarnings("deprecation")
	public void showDateWheel(View parentView, String[] datas) {
		int visbleItems = initVisbleItems(datas);
		int currentItem = visbleItems / 2;
		mSelectedIndex = currentItem;

		View view = View.inflate(mContext, R.layout.pop_dict_picker, null);
		final PopupWindow popupWindow = new PopupWindow(view,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		initViews(datas, visbleItems, currentItem, view, popupWindow);
		initPopupWindow(parentView, popupWindow);
	}

	private void initPopupWindow(View parentView, final PopupWindow popupWindow) {
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBackgroundAlpha(1);
			}
		});
		popupWindow.setAnimationStyle(R.style.AnimationFadeBottom);
		setBackgroundAlpha(0.5f);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.LEFT,
				0, 0);
	}

	private void initViews(String[] datas, int visbleItems, int currentItem,
			View view, final PopupWindow popupWindow) {
		Button btnSure = (Button) view.findViewById(R.id.btn_sure_dict_picker);
		final WheelView wheel = (WheelView) view
				.findViewById(R.id.wheel_dict_picker);
		wheel.setVisibleItems(visbleItems); // Number of items
		wheel.setWheelBackground(R.color.transparent); // 背景色
		wheel.setWheelForeground(R.color.theme_half_press); // 分割线颜色
		wheel.setShadowColor(0xFFFFFFF, 0xFFFFFFF, 0xFFFFFFF);
		wheel.setViewAdapter(new DictAdapter(mContext, datas));
		wheel.setCurrentItem(currentItem);
		wheel.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				LogUtils.i("PopDict", oldValue + "--" + newValue);
				mSelectedIndex = newValue;
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onSelected(mSelectedIndex);
					popupWindow.dismiss();
				}
			}
		});
	}

	/**
	 * 初始化计算可见item个数
	 * 
	 * @param datas
	 *            数据集合
	 * @return
	 */
	private int initVisbleItems(String[] datas) {
		int visbleItems = 0;
		if (datas != null) {
			if (datas.length >= 10) {
				visbleItems = 7; // 字典总数大于10，显示6个
			} else if (datas.length >= 5) {
				visbleItems = 5;// 字典总数大于4小于10，显示4个
			} else {
				visbleItems = datas.length;
			}
		}
		return visbleItems;
	}

	/**
	 * 弹出日期滚轮
	 * 
	 * @param tvStartDate
	 */
	@SuppressWarnings("deprecation")
	public void showDateWheel(View parentView, List<String> datas) {
		showDateWheel(parentView, datas.toArray(new String[datas.size()]));
	}

	/**
	 * Adapter for countries
	 */
	private class DictAdapter extends AbstractWheelTextAdapter {
		// City names
		final String mDatas[];

		/**
		 * Constructor
		 */
		protected DictAdapter(Context context, String[] datas) {
			super(context, R.layout.item_dict_whell, NO_RESOURCE);
			mDatas = datas;
			setItemTextResource(R.id.tv_name_dict_wheel);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return mDatas.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return mDatas[index];
		}
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void setBackgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
				.getAttributes();
		lp.alpha = bgAlpha;// 0.0-1.0
		((Activity) mContext).getWindow().setAttributes(lp);
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.mListener = onSelectedListener;
	}

	public interface OnSelectedListener {
		/** 监听选中字典集合的序号 */
		void onSelected(int index);
	}
}
