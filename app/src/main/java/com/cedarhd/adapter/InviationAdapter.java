package com.cedarhd.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.MyBaseAdapter;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.LoadImage;
import com.cedarhd.models.GroupModel;
import com.cedarhd.utils.LogUtils;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;

public class InviationAdapter extends MyBaseAdapter<GroupModel> {
	public boolean isclick;
	public GridView gridView;
	public LoadImage loadImage;

	public InviationAdapter(Context context, GridView gridView) {
		super(context);
		this.gridView = gridView;
		loadImage = new LoadImage(context, listener);
	}

	@Override
	public View getView2(int position, View convertView, ViewGroup parent) {
		HolderView holderView = null;

		if (holderView == null) {
			convertView = inflater.inflate(R.layout.griditem, null);
			holderView = new HolderView(convertView);
			convertView.setTag(holderView);
		}
		holderView = (HolderView) convertView.getTag();
		if (position < list.size() - 1) {
			if (list.get(position).isdepart) {
				holderView.textView.setText(list.get(position).名称);
				holderView.imageView
						.setImageResource(R.drawable.new_wenjianjia);
			} else {
				holderView.textView.setText(list.get(position).userName);
				holderView.imageView.setTag(list.get(position).AvatarURI);
				String url = Global.BASE_URL + Global.EXTENSION
						+ list.get(position).AvatarURI;
				Bitmap bitmap = loadImage.geBitmap(url);
				LogUtils.i("out", "图片地址：" + url);
				if (bitmap != null) {
					holderView.imageView.setImageBitmap(bitmap);
				} else {
					holderView.imageView.setImageResource(R.drawable.tx);
				}
			}
		} else {
			holderView.textView.setVisibility(View.GONE);
			holderView.imageView.setImageResource(R.drawable.invitexs);
		}
		return convertView;
	}

	class HolderView {
		PorterShapeImageView imageView;
		TextView textView;

		public HolderView(View convertView) {
			imageView = (PorterShapeImageView) convertView
					.findViewById(R.id.show_grid_item);
			textView = (TextView) convertView
					.findViewById(R.id.text_show_grname);
		}
	}

	private LoadImage.ImageLoadListener listener = new LoadImage.ImageLoadListener() {
		@Override
		public void imageLoadOk(Bitmap bitmap, String url) {
			ImageView iv = (ImageView) gridView.findViewWithTag(url);
			if (iv != null)
				iv.setImageBitmap(bitmap);
		}
	};
}
