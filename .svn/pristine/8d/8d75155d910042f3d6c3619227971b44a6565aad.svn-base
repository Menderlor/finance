package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.comparator.CheckBoxListViewItemComparator;
import com.cedarhd.control.CheckBoxListViewItem;
import com.cedarhd.helpers.GB2Alpha;
import com.cedarhd.helpers.GB2Alpha.LetterType;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.PictureUtils.HandlerPicture;
import com.cedarhd.utils.LogUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class User_Select_LetterListViewAdapter extends BaseAdapter {

	View.OnClickListener myAdapterCBListener;
	private List<CheckBoxListViewItem> mList;
	private Context mContext;
	int mlistviewlayoutId;

	/**
	 * 存放存在的汉语拼音首字母和与之对应的列表位置
	 */
	public HashMap<String, Integer> alphaIndexer;
	private static final String NAME = "name", NUMBER = "number",
			SORT_KEY = "sort_key";
	private PictureUtils handlerUtils = new PictureUtils();;

	private HandlerPicture handler;

	/**
	 * 存放存在的汉语拼音首字母
	 */
	public String[] sections;
	GB2Alpha obj1 = new GB2Alpha();

	/**
	 * 
	 * @param pContext
	 * @param listviewlayoutId
	 *            子填充项itemId
	 * @param pList
	 * @param listener
	 */
	public User_Select_LetterListViewAdapter(Context pContext,
			int listviewlayoutId, List<CheckBoxListViewItem> pList,
			OnClickListener listener) {
		this.mContext = pContext;
		this.mlistviewlayoutId = listviewlayoutId;
		this.mList = pList;
		this.myAdapterCBListener = listener;

		LogUtils.i("count", "count" + mList.size());
		initSort();
	}

	/**
	 * 初始化拼音派讯
	 */
	private void initSort() {
		// 创建比较器对象
		CheckBoxListViewItemComparator comp = new CheckBoxListViewItemComparator();
		// 调用排序方法
		Collections.sort(mList, comp);
		// for (CheckBoxListViewItem item : mList) {
		// LogUtils.i("itemName", item.Name);
		// }
		LogUtils.i("count", "after sort:" + mList.size());
		alphaIndexer = new HashMap<String, Integer>();
		sections = new String[mList.size()];

		for (int i = 0; i < mList.size(); i++) {
			// 当前汉语拼音首字母
			String currentStr = getAlpha(obj1.String2AlphaFirst(
					mList.get(i).Name, LetterType.Uppercase));
			// 上一个汉语拼音首字母，如果不存在为“ ”
			String previewStr = (i - 1) >= 0 ? getAlpha(obj1.String2AlphaFirst(
					mList.get(i - 1).Name, LetterType.Uppercase)) : " ";
			if (!previewStr.equals(currentStr)) {
				String name = getAlpha(obj1.String2AlphaFirst(
						mList.get(i).Name, LetterType.Uppercase));
				alphaIndexer.put(name, i);
				sections[i] = name;
			}
		}
		LogUtils.i("count", "after sort2:" + mList.size());
	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	@Override
	public int getCount() {
		LogUtils.d("count", "getCount:" + mList.size());
		return mList.size();
	}

	@Override
	public CheckBoxListViewItem getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = convertView;
		if (view == null || view.getTag() == null) {
			view = View.inflate(mContext, mlistviewlayoutId, null);
			ViewHolder holder = new ViewHolder();
			holder = new ViewHolder();
			holder.alpha = (TextView) view.findViewById(R.id.alpha);
			holder.Name = (TextView) view.findViewById(R.id.name_userSelect);
			holder.imageView1 = (ImageView) view
					.findViewById(R.id.imageView1_userSelect);
			holder.ivPhoto = (ImageView) view
					.findViewById(R.id.iv_photo_userSelect);
			view.setTag(holder);
		}

		if (myAdapterCBListener != null) {
			// holder.cb.setOnClickListener(myAdapterCBListener);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		CheckBoxListViewItem item = mList.get(position);

		LogUtils.i("leo3", item.Name + "-->" + item.pic_url);
		holder.Name.setText(item.Name);
		holder.ivPhoto.setTag(position);
		if (!TextUtils.isEmpty(item.pic_url)) {
			handlerUtils.setAvatar(item.pic_url, holder.ivPhoto, position);
		} else {
			holder.ivPhoto.setImageResource(R.drawable.tx);
		}
		if (item.IsChecked) {
			holder.imageView1.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.color.mail_unread_bg);
		} else {
			holder.imageView1.setVisibility(View.INVISIBLE);
			view.setBackgroundResource(R.color.mail_read_bg);
		}

		// 导航 字母
		String currentStr = getAlpha(obj1.String2AlphaFirst(
				mList.get(position).Name, LetterType.Uppercase));
		String previewStr = (position - 1) >= 0 ? getAlpha(obj1
				.String2AlphaFirst(mList.get(position - 1).Name,
						LetterType.Uppercase)) : " ";

		// holder.alpha.setVisibility(View.VISIBLE);
		// holder.alpha.setText(currentStr);

		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return view;
	}

	private class ViewHolder {
		TextView alpha;
		ImageView imageView1;
		ImageView ivPhoto;
		TextView Name;
	}

	public List<CheckBoxListViewItem> getmList() {
		return mList;
	}

	/**
	 * 设置内容源
	 * 
	 * @param mList
	 */
	public void setmList(List<CheckBoxListViewItem> mList) {
		this.mList = mList;
		initSort();
	}

	// /**
	// * 处理头像handler
	// *
	// * @author bohr
	// *
	// */
	// public class HandlerPicture extends Handler {
	// // 标示固定
	// public final int SHOW_IMAGE_SUCCESS = 3;
	// public final int SHOW_IMAGE_FAILUREE = 4;
	// private ImageView ivAvatars;
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// int what = msg.what;
	// if (what == SHOW_IMAGE_SUCCESS) {
	// int pos = msg.arg1;
	// int postion = msg.arg2;
	// ivAvatars = (ImageView) msg.obj;
	// Bitmap avatar = (Bitmap) ivAvatars.getTag();
	// if (pos == postion) {
	// ivAvatars.setImageBitmap(avatar);
	// }
	// } else if (what == SHOW_IMAGE_FAILUREE) {
	// ivAvatars.setImageResource(R.drawable.tx);
	// }
	// }
	// }
	//
	// /**
	// * 设置头像 k
	// *
	// * @param avataUrl
	// * 头像地址（没有加服务器地址）
	// * @param ivAvatar
	// * 显示头像的控件
	// * @param postion
	// * 位置
	// */
	// private void setAvatar(final String avataUrl, final ImageView ivAvatar,
	// int postion) {
	// // 如果本地数据库中存有用户头像信息
	// if (!TextUtils.isEmpty(avataUrl) && avataUrl.contains("\\")) {
	// int index = avataUrl.lastIndexOf("\\");
	// String avatarName = avataUrl
	// .substring(index + 1, avataUrl.length()); // 图片名
	// // 本地图片文件
	// if (!TextUtils.isEmpty(avatarName)) {
	// File file = new File(new File(avatarPath), avatarName);
	// if (file.exists()) { // 本地有图直接使用
	// Bitmap avatar = BitmapFactory.decodeFile(file.toString());
	// int pos = (Integer) ivAvatar.getTag();
	// if (pos == postion) {
	// ivAvatar.setImageBitmap(avatar);
	// }
	// } else {
	// // 开启线程从网络下载
	// new DownloadThread(postion, avataUrl, ivAvatar, handler)
	// .start();
	// }
	// } else {
	// // 开启线程从网络下载
	// new DownloadThread(postion, avataUrl, ivAvatar, handler)
	// .start();
	// }
	// }
	// }
	//
	// class DownloadThread extends Thread {
	// private int pos;
	// private String avataUrl;
	// private ImageView ivAvatar;
	// public HandlerPicture handler;
	// private HttpUtils httpUtils;
	//
	// public DownloadThread(int pos, String avataUrl, ImageView ivAvatar,
	// HandlerPicture handler) {
	// super();
	// this.pos = pos;
	// this.avataUrl = avataUrl;
	// this.ivAvatar = ivAvatar;
	// this.handler = handler;
	// httpUtils = new HttpUtils();
	// }
	//
	// @Override
	// public void run() {
	// httpUtils.downloadData(avataUrl, handler, ivAvatar, pos);
	// }
	// }
}
