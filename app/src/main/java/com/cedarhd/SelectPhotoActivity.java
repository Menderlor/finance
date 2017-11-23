package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickRightListener;
import com.cedarhd.models.ImageFloder;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.widget.ListImageDirPopupWindow;
import com.cedarhd.widget.ListImageDirPopupWindow.OnImageDirSelected;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/***
 * 选择图片
 * 
 * @author K
 * 
 */
public class SelectPhotoActivity extends BaseActivity {

	private Context mContext;
	/** 选中文件路径列表 */
	public static final String PHOTO_LIST = "photo_list";
	private ProgressDialog mProgressDialog;
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private SelectedPhotoAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private BoeryunHeaderView headerView;
	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;

	// private TextView tvSubmit;
	int totalCount = 0;

	private int mScreenHeight;

	/** 可选择图片的最大数量 */
	private int mMaxCount = 9;

	public static String MAX_PHOTO_COUNT = "max_photo_count";

	// private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_photo);

		initData();

		initView();
		getImages();
		initEvent();

	}

	private void initData() {
		mContext = SelectPhotoActivity.this;
		mMaxCount = getIntent().getIntExtra(MAX_PHOTO_COUNT, mMaxCount);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
	}

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "抱歉，一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new SelectedPhotoAdapter(getApplicationContext(), mImgs,
				R.layout.item_photoselect_grid, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText("共" + totalCount + "张");
	};

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				ViewGroup.LayoutParams.MATCH_PARENT,
				(int) (mScreenHeight * 0.7), mImageFloders, LayoutInflater
						.from(getApplicationContext()).inflate(
								R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow
				.setOnImageDirSelected(new OnImageDirSelected() {
					@Override
					public void selected(ImageFloder floder) {
						mImgDir = new File(floder.getDir());
						mImgs = Arrays.asList(mImgDir
								.list(new FilenameFilter() {
									@Override
									public boolean accept(File dir,
											String filename) {
										if (filename.endsWith(".jpg")
												|| filename.endsWith(".png")
												|| filename.endsWith(".jpeg"))
											return true;
										return false;
									}
								}));
						/**
						 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
						 */
						mAdapter = new SelectedPhotoAdapter(
								getApplicationContext(), mImgs,
								R.layout.item_photoselect_grid, mImgDir
										.getAbsolutePath());
						mGirdView.setAdapter(mAdapter);
						// mAdapter.notifyDataSetChanged();
						mImageCount.setText(floder.getCount() + "张");
						mChooseDir.setText(floder.getName());
						mListImageDirPopupWindow.dismiss();
					}
				});
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = SelectPhotoActivity.this
						.getContentResolver();
				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver
						.query(mImageUri, null,
								MediaStore.Images.Media.MIME_TYPE + "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=?", new String[] { "image/jpeg",
										"image/png", "image/gif" },
								MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					Log.e("path", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					Log.i("dirPath", dirPath);
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					// 统计一个文件夹下照片的数量
					String[] pathArrs = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					});

					if (pathArrs == null)
						continue;

					int picSize = pathArrs.length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_select_photo);
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		// tvSubmit = (TextView) findViewById(R.id.tv_ok_select_photo);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		// tvSubmit.setText(getString(R.string.select_photo_no, 0, mMaxCount));

		headerView.setRightTitle(getString(R.string.select_photo_no, 0,
				mMaxCount));
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mListImageDirPopupWindow
				// .setAnimationStyle(R.style.a);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.3f;
				getWindow().setAttributes(lp);
			}
		});
		headerView.setOnButtonClickListener(new OnButtonClickRightListener() {

			@Override
			public void onClickBack() {
				finish();
			}

			@Override
			public void onClickFilter() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickSaveOrAdd() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRightTextClick() {
				ArrayList<String> list = mSelectedImagePath;
				// 选择确定，返回选中图片路径列表
				if (list.size() > 0) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList(PHOTO_LIST, list);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public ArrayList<String> mSelectedImagePath = new ArrayList<String>();

	class SelectedPhotoAdapter extends CommanAdapter<String> {

		/**
		 * 文件夹路径
		 */
		private String mDirPath;

		public SelectedPhotoAdapter(Context context, List<String> mDatas,
				int itemLayoutId, String dirPath) {
			super(mDatas, context, itemLayoutId);
			this.mDirPath = dirPath;
		}

		@SuppressLint("NewApi")
		@Override
		public void convert(int position, final String item,
				BoeryunViewHolder helper) {
			final ImageView mImageView = helper.getView(R.id.id_item_image);
			final ImageView mSelect = helper.getView(R.id.id_item_select);
			mImageView.setImageResource(R.drawable.pictures_no);
			mSelect.setImageResource(R.drawable.flag_no);
			// 设置图片
			helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
			LogUtils.i("mDirPath", mDirPath + "/" + item);

			mImageView.setColorFilter(null);
			// 设置ImageView的点击事件
			mImageView.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@SuppressLint("NewApi")
				@Override
				public void onClick(View v) {
					// 已经选择过该图片
					if (mSelectedImagePath.contains(mDirPath + "/" + item)) {
						mSelectedImagePath.remove(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.flag_no);
						mImageView.setColorFilter(null);

						headerView.setRightTitle(getString(
								R.string.select_photo_no,
								mSelectedImagePath.size(), mMaxCount));
					} else {
						if (mSelectedImagePath.size() < mMaxCount) {
							// 未选择该图片
							mSelectedImagePath.add(mDirPath + "/" + item);
							mSelect.setImageResource(R.drawable.flag_ok);
							mImageView.setColorFilter(Color
									.parseColor("#77000000"));
							headerView.setRightTitle(getString(
									R.string.select_photo_no,
									mSelectedImagePath.size(), mMaxCount));
						} else {
							Toast.makeText(mContext,
									"你最多只能选择" + mMaxCount + "张图片",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImagePath.contains(mDirPath + "/" + item)) {
				mSelect.setImageResource(R.drawable.ico_check_green);
				mImageView.setColorFilter(Color.parseColor("#77000000"));
			}

			headerView.setRightTitle(getString(R.string.select_photo_no,
					mSelectedImagePath.size(), mMaxCount));
		}
	}

}
