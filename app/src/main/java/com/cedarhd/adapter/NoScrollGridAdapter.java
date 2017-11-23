package com.cedarhd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cedarhd.R;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.utils.StrUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NoScrollGridAdapter extends BaseAdapter {

	/** 上下文 */
	private Context ctx;
	/** 图片Url集合 */
	private List<String> imageUrls;

	/*** 默认图片宽度 75Dp */
	private int mPicWidth = 75;

	/***
	 * 
	 * 
	 * @param ctx
	 *            当前上下文
	 * @param urls
	 *            图片下载url
	 */
	public NoScrollGridAdapter(Context ctx, List<String> urls) {
		this.ctx = ctx;
		this.imageUrls = urls;
	}

	/***
	 * 通过主机名和文件路径
	 * 
	 * @param ctx
	 *            当前上下文
	 * @param host
	 *            主机名如 http:www.boeryun.com:8076
	 * @param urls
	 *            文件路径
	 */
	public NoScrollGridAdapter(Context ctx, String host, List<String> urls) {
		this.ctx = ctx;
		imageUrls = new ArrayList<String>();

		for (int i = 0; i < urls.size(); i++) {
			imageUrls.add(host + urls.get(i));
		}
	}

	/***
	 * 通过主机名和文件路径
	 * 
	 * @param ctx
	 *            当前上下文
	 * @param imgWidth
	 *            图片宽高（DP）
	 * @param host
	 *            主机名如 http:www.boeryun.com:8076
	 * @param urls
	 *            文件路径
	 */
	public NoScrollGridAdapter(Context ctx, int imgWidth, String host,
			List<String> urls) {
		this.ctx = ctx;
		this.mPicWidth = imgWidth;
		imageUrls = new ArrayList<String>();

		for (int i = 0; i < urls.size(); i++) {
			imageUrls.add(host + urls.get(i));
		}
	}

	@Override
	public int getCount() {
		return imageUrls == null ? 0 : imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return imageUrls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = View.inflate(ctx, R.layout.item_photo_gridview, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_photo_item);
		if (mPicWidth != 75) {
			int picWidth = (int) ViewHelper.dip2px(ctx, mPicWidth);
			// int picWidth = mPicWidth;
			RelativeLayout.LayoutParams params = new LayoutParams(picWidth,
					picWidth);
			imageView.setLayoutParams(params);
		}
		ImageLoader.getInstance().displayImage(StrUtils.convertUTF(imageUrls.get(position)),
				imageView, Global.getPassport());
		return view;
	}

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

}
