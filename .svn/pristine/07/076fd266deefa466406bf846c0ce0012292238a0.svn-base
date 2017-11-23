package com.cedarhd.base;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.helpers.PhotoImageLoader;
import com.cedarhd.helpers.PhotoImageLoader.Type;
import com.cedarhd.utils.StrUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 通用的ViewHolder
 */
public class BoeryunViewHolder {
	private SparseArray<View> mViews;
	private View mConvertView;

	private BoeryunViewHolder(int position, ViewGroup parent, Context context,
			int layoutId) {
		mViews = new SparseArray<View>();
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);
	}

	/**
	 * 获得持有convertView和其中各个控件 的实体类ViewHolder
	 * 
	 * @param position
	 *            数据源的序号（位置）
	 * @param convertView
	 *            由xml加载的view
	 * @param parent
	 *            父控件
	 * @param context
	 * @param layoutId
	 *            item的xml资源ID
	 * @return
	 */
	public static BoeryunViewHolder getInstance(int position, View convertView,
			ViewGroup parent, Context context, int layoutId) {
		if (convertView == null) {
			return new BoeryunViewHolder(position, parent, context, layoutId);
		} else {
			return (BoeryunViewHolder) convertView.getTag();
		}
	}

	/***
	 * 获得Item中指定编号的一个View
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/***
	 * 设置指定id的textView显示指定内容
	 * 
	 * @param viewId
	 *            控件布局编号
	 * @param content
	 *            显示内容
	 * @return
	 */
	public void setTextValue(int viewId, String content) {
		TextView textView = getView(viewId);
		if (textView != null
				&& (textView instanceof TextView || textView instanceof EditText)) {
			textView.setText(StrUtils.pareseNull(content));
		}
	}

	private DisplayImageOptions options;

	/***
	 * 获得图片参数
	 * 
	 * @return
	 */
	private DisplayImageOptions getImageOptions() {
		if (options == null) {
			options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.showImageForEmptyUri(R.drawable.ic_launcher) //
					.showImageOnFail(R.drawable.img_load_failed) //
					.cacheOnDisk(true)//
					.bitmapConfig(Config.RGB_565)//
					.build();
		}
		return options;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public BoeryunViewHolder setImageByUrl(int viewId, String url) {
		PhotoImageLoader.getInstance(3, Type.LIFO).loadImage(url,
				(ImageView) getView(viewId));
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param resourceId
	 *            图片资源编号
	 * @return
	 */
	public BoeryunViewHolder setImageResoure(int viewId, int resourceId) {
		ImageView imageView = getView(viewId);
		if (imageView != null && (imageView instanceof ImageView)) {
			imageView.setImageResource(resourceId);
		}
		return this;
	}

	public View getConvertView() {
		return mConvertView;
	}

}
