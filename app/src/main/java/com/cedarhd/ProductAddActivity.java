package com.cedarhd;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.产品型号;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/***
 * 新建产品列表
 * 
 * @author K
 * 
 */
public class ProductAddActivity extends BaseActivity {

	private String mFileName; // 文件名
	private String absoluteFileName; // 文件全路径
	private Bitmap photo; // 头像照片
	private Context mContext;
	private HttpUtils mHttpUtils;

	/** 提交校验信息 */
	private String mNullCheckMsg;
	/** 产品单价 */
	private double price = 0;
	private String productName;
	private String uploadFilePath;// 上传文件的返回路径
	private ImageView ivPhoto;
	private ImageView ivBack;
	private ImageView ivSave;
	private EditText etName;
	private EditText etPrice;

	/* 用来标识请求gallery的activity */
	private static final int PICKED_PHOTO_WITH_DATA = 1;

	private static final int CODE_SELECT_PHOTO = 2;

	private static final int SUCCESS_UPLOAD = 11;
	private static final int FAILURE_UPLOAD = 12;
	private static final int SUCCESS_SUBMIT = 13;
	private static final int FAILURE_SUBMIT = 14;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_UPLOAD:
				submitProduct();
				break;
			case FAILURE_UPLOAD:
				Toast.makeText(mContext, "上传图片失败！", Toast.LENGTH_SHORT).show();
				ProgressDialogHelper.dismiss();
				break;
			case SUCCESS_SUBMIT:
				Toast.makeText(mContext, "保存成功！", Toast.LENGTH_SHORT).show();
				ProgressDialogHelper.dismiss();
				break;
			case FAILURE_SUBMIT:
				Toast.makeText(mContext, "上传失败！", Toast.LENGTH_SHORT).show();
				ProgressDialogHelper.dismiss();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_product);
		initData();
		initView();
		setOnClickListener();
	}

	private void initData() {
		mContext = ProductAddActivity.this;
		mHttpUtils = new HttpUtils();
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_back_product_add);
		ivSave = (ImageView) findViewById(R.id.iv_save_product_add);
		ivPhoto = (ImageView) findViewById(R.id.iv_photo_product_add);
		etName = (EditText) findViewById(R.id.et_name_product_new);
		etPrice = (EditText) findViewById(R.id.et_price_product_new);
	}

	private void setOnClickListener() {
		ivPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SelectPhotoActivity.class);
				intent.putExtra(SelectPhotoActivity.MAX_PHOTO_COUNT, 1);
				startActivityForResult(intent, CODE_SELECT_PHOTO);
			}
		});

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadPhoto();
			}
		});
	}

	private void uploadPhoto() {
		if (isCheckPass()) {

			ProgressDialogHelper.show(mContext);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Attach attach = UploadHelper
								.uploadFileByHttpGetAttach(new File(
										absoluteFileName));

						if (attach != null
								&& !TextUtils.isEmpty(attach.Address)) {
							uploadFilePath = attach.Address;
							LogUtils.i("ATTACH", attach.Name + "\t"
									+ attach.Address);
							handler.sendEmptyMessage(SUCCESS_UPLOAD);
						} else {
							handler.sendEmptyMessage(FAILURE_UPLOAD);
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(FAILURE_UPLOAD);
					}

				}
			}).start();

		} else {
			Toast.makeText(mContext, mNullCheckMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private void submitProduct() {
		产品型号 item = new 产品型号();
		item.单价 = price;
		item.名称 = productName;
		item.图片 = uploadFilePath;
		try {
			final JSONObject jo2 = JsonUtils.initJsonObj(item);
			// final String url = Global.BASE_URL + "SaleStore/saveProduct/";
			final String url = Global.BASE_URL + "SaleStore/saveSimpleProduct/";
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String jsonResult = mHttpUtils.postSubmit(url, jo2);
						LogUtils.i("jsonResult", jsonResult);
						String status = JsonUtils.parseStatus(jsonResult);
						if ("1".equals(status)) {
							handler.sendEmptyMessage(SUCCESS_SUBMIT);
						} else {
							handler.sendEmptyMessage(FAILURE_SUBMIT);
						}
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(FAILURE_SUBMIT);
					}
				}
			}).start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean isCheckPass() {
		productName = etName.getText().toString();
		String priceStr = etPrice.getText().toString();
		if (TextUtils.isEmpty(absoluteFileName)) {
			mNullCheckMsg = "请选择上传图片";
			return false;
		}
		if (TextUtils.isEmpty(productName)) {
			mNullCheckMsg = "名称不能为空";
			return false;
		}
		if (TextUtils.isEmpty(priceStr)) {
			mNullCheckMsg = "单价不能为空";
			return false;
		}
		try {
			price = Double.parseDouble(priceStr);
		} catch (Exception e) {
			mNullCheckMsg = "非法的数值类型";
			return false;
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case CODE_SELECT_PHOTO:
			if (RESULT_OK == resultCode) {
				if (data == null) {
					Toast.makeText(mContext, "图片系统异常..", Toast.LENGTH_SHORT)
							.show();
				} else {
					Bundle bundle = data.getExtras();
					List<String> photoList = bundle
							.getStringArrayList(SelectPhotoActivity.PHOTO_LIST);
					if (photoList.size() > 0) {
						absoluteFileName = photoList.get(0);

						photo = BitmapHelper.decodeSampleBitmapFromFile(
								absoluteFileName, 480, 480);
						ivPhoto.setImageBitmap(photo);
					}
				}
			}
			break;
		case PICKED_PHOTO_WITH_DATA: {// 调用Gallery返回的
			mFileName = getPhotoFileName();
			if (data == null) {
				Toast.makeText(mContext, "图片系统异常..", Toast.LENGTH_SHORT).show();
			} else {
				photo = data.getParcelableExtra("data");
				if (photo == null) {
					Toast.makeText(mContext, "获取图片异常,请尝试选择相机",
							Toast.LENGTH_SHORT).show();
				} else {
					// ivPhoto.setImageBitmap(photo);
					// 下面就是显示照片了
					absoluteFileName = FilePathConfig.getThumbDirPath()
							+ File.separator + mFileName;
					// 保存头像缩略图
					BitmapHelper.createThumBitmap(absoluteFileName, photo);

					photo = BitmapHelper.decodeSampleBitmapFromFile(
							absoluteFileName, 480, 480);
					ivPhoto.setImageBitmap(photo);
				}
			}
			break;
		}
		}
	}

	/*** 请求Gallery程序,打开相册 */
	protected void doPickPhotoFromGallery() {
		try {
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PICKED_PHOTO_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "失败", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		return "IMG"
				+ DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA)) + ".jpg";
	}

	// 封装请求Gallery的intent
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "false");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 480);
		intent.putExtra("outputY", 480);
		intent.putExtra("return-data", true);
		return intent;
	}
}
