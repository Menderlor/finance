package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.BaiduLocator;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.PhotoHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.UploadHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.BaiduPlace;
import com.cedarhd.models.User;
import com.cedarhd.utils.EarthMapUtils;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 考勤登记（签到签退）
 */
public class TagActivity extends BaseActivity implements BDLocationListener {
	static ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	public final int CAMERA_TAKE_HELPER = 1001;
	private static String TAG = "TagActivity";
	public LocationClient mLocationClient = null;
	TextView mTextViewTime;
	// TextView mtag_salary;
	LinearLayout llTagList;
	/** 选择定位列表 */
	LinearLayout llPickLocation;
	TextView mTvAddress;

	ImageView mImageViewCamera;
	Button mButtonTag; // 签到
	Button mButtonQuit;// 签退
	ProgressBar mProgressBar;
	ProgressBar progressBar_showupload;
	/** 查看地图所在位置 */
	private LinearLayout llMap;
	Bitmap bitmap = null;
	ORMDataHelper oDataHelper = null;
	String mLocation = null;
	// private String mProvince;
	private String mCity;
	private String mCountry;

	/** 上传到服务器的经纬度 */
	double mLatitude;
	double mLongitude;

	/** 记录初次定位的经纬度 */
	double mLat;
	double mLog;
	private int radiusMap = 150;// 默认区域半径

	private boolean tagFlag;
	private String latterReason = ""; // 迟到原因
	private String earlyReason = "";// 早退原因

	private String writeToPath = ""; // 文件路径
	private String spPath = ""; // 压缩后的文件路径
	private String mPictureFile = "";

	String attachId;
	public static final int UPLOAD_DATA_END = 3;// 上传数据结束
	public static final int UPLOAD_DATA_START = 4;// 开始上传数据
	public static final int ATTANCE_FAILURE = 5;// 登记失败
	public static final int ATTANCE_SUCCESS = 6;// 登记成功
	public static final int ATTANCE_IS_LATER = 7;// 迟到
	public static final int ATTANCE_IS_EARLY = 8;// 早退
	public static final int ATTANCE_IS_NORMAL = 9;// 签到正常
	private int REQUEST_CODE_ISEARLY = 11;
	private int REQUEST_CODE_ISLATER = 12;

	/** 请求选择地址列表 */
	public static int REQUEST_CODE_LOCATION_LIST = 103;
	private Handler upLoadDataHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPLOAD_DATA_START:
				progressBar_showupload.setVisibility(View.VISIBLE);
				break;
			case ATTANCE_SUCCESS:
				Toast.makeText(TagActivity.this, "登记成功", Toast.LENGTH_SHORT)
						.show();
				ProgressDialogHelper.dismiss();
				// mButtonTag.setEnabled(true);
				// mButtonQuit.setEnabled(true);
				LogUtils.i("attanceIn", "登记成功");
				break;
			case ATTANCE_FAILURE:
				ProgressDialogHelper.dismiss();
				// mButtonTag.setEnabled(true);
				// mButtonQuit.setEnabled(true);
				Toast.makeText(TagActivity.this, "登记失败", Toast.LENGTH_LONG)
						.show();
				break;
			case UPLOAD_DATA_END:// 图片上传成功，签到
				attendance(tagFlag);
				break;
			case ATTANCE_IS_LATER: // 迟到
				ProgressDialogHelper.dismiss();
				// TODO 暂时不用输入迟到原因，下面代码勿删
				Toast.makeText(TagActivity.this, "您迟到了，请输入迟到原因！", 0).show();
				Intent intent = new Intent(TagActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				startActivityForResult(intent, REQUEST_CODE_ISLATER);
				break;
			case ATTANCE_IS_EARLY: // 早退
				ProgressDialogHelper.dismiss();
				// TODO 暂时不用输入迟到原因，下面代码勿删
				Toast.makeText(TagActivity.this, "您早退了，请输入早退原因！", 0).show();
				Intent intent_early = new Intent(TagActivity.this,
						TaskContentActivity.class);
				intent_early.putExtra(TaskContentActivity.EDITECONTENT, true);
				startActivityForResult(intent_early, REQUEST_CODE_ISEARLY);
				break;
			case ATTANCE_IS_NORMAL: // 正常签到
				ProgressDialogHelper.dismiss();
				break;

			default:
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oDataHelper = ORMDataHelper.getInstance(TagActivity.this);
		setContentView(R.layout.tag);
		LogUtils.i("life", "onCreate");
		findViews();
		setOnClickListener();
		try {
			requestLocating();
		} catch (Exception e) {
			LogUtils.e("locating", "failed.");
		}
	}

	protected void onResume() {
		super.onResume();
		LogUtils.i("life", "onResume");

	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtils.i("life", "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtils.i("life", "onRestart");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.i("life", "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.i("life", "onStop");
	}

	@Override
	protected void onDestroy() {
		LogUtils.i("life", "onDestroy");
		if (BaiduLocator.startedOrNot()) {
			BaiduLocator.stop();
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("globalTag", Global.mUser);
		LogUtils.i("lifeState", "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (Global.mUser == null) {
			Global.mUser = (User) savedInstanceState
					.getSerializable("globalTag");
		}
		LogUtils.i("lifeState", "onRestoreInstanceState");
		LogUtils.i("lifeState", Global.mUser.Passport);
	}

	public void setOnClickListener() {
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mImageViewCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
				intent.addCategory(Intent.CATEGORY_DEFAULT); //
				mPictureFile = DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA))
						+ ".jpg";
				LogUtils.i("onactivity", "mPictureFile：" + mPictureFile);
				Uri imageUri = Uri.fromFile(new File(PhotoHelper.PATH,
						mPictureFile));
				SharedPreferences sp = getSharedPreferences("config",
						Context.MODE_PRIVATE);
				sp.edit().putString("path", mPictureFile).commit();
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				// 把照片保存在sd卡中指定位置
				startActivityForResult(intent, CAMERA_TAKE_HELPER);
				LogUtils.i("onactivity", "mPictureFile：" + mPictureFile);
			}
		});

		// 登记考勤
		// 签到
		mButtonTag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sign(true);
			}
		});

		// 签退
		mButtonQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mButtonQuit.setEnabled(false);
				sign(false);
			}
		});
		// 考勤详情按钮
		llTagList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TagActivity.this,
						TagSalaryListActivity.class);
				startActivity(intent);
			}
		});

		llPickLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TagActivity.this,
						LocationListActivity.class);
				if (mLongitude != 0 && mLatitude != 0) {
					intent.putExtra(LocationListActivity.LATITUDE, mLat);
					intent.putExtra(LocationListActivity.LONGITUDE, mLog);
				}
				startActivityForResult(intent, REQUEST_CODE_LOCATION_LIST);
			}
		});

		llMap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startMapPionter(mLocation, mLat + "", mLog + "");
			}
		});
	}

	/**
	 * 签到，签退
	 * 
	 * @param flag
	 *            标志位 true为签到，false为签退
	 */
	private void sign(final boolean tagFlag) {
		this.tagFlag = tagFlag;
		// 没有使用模拟定位
		if (isNotMockLocation()) {
			// 不做处理
		} else {
			initMockLocation();
			return;
		}

		if (TextUtils.isEmpty(mLocation)) {
			Toast.makeText(TagActivity.this, "未获取位置信息,请打开网络连接",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			// if (tagFlag == true) {
			// // 签到时先判断是否有当天的签到记录
			// // if (isExistTodayData()) {
			// // Toast.makeText(this, "已经签到成功，不能重复签到", Toast.LENGTH_LONG)
			// // .show();
			// // return;
			// // }
			// }
		}

		try {
			String title = tagFlag ? "正在签到" : "正在签退";
			ProgressDialogHelper.show(TagActivity.this, title);

			uploadTagPhoto(spPath);
		} catch (Exception e) {
			LogUtils.e(TAG, "--->" + e);
			Toast.makeText(TagActivity.this, "签到失败", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/**
	 * 上传考勤照片
	 */
	private void uploadTagPhoto(String spPath) {
		final File file = new File(spPath);
		LogUtils.i("uploadTagPhoto", spPath);
		if (file.exists()) {
			LogUtils.i("uploadTagPhoto", "file.exists()");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						attachId = UploadHelper.uploadFileGetAttachId(file);
						if (!TextUtils.isEmpty(attachId)) {
							TagActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(TagActivity.this, "图片上传成功",
											Toast.LENGTH_SHORT).show();
								}
							});
							LogUtils.i(TAG, "上传图片，获得附件号:" + attachId);
						} else {
							attachId = "";
							TagActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(TagActivity.this, "图片上传失败",
											Toast.LENGTH_SHORT).show();
								}
							});
						}
						upLoadDataHandler.sendEmptyMessage(UPLOAD_DATA_END);
					} catch (Exception e) {
						Toast.makeText(TagActivity.this, "上传照片异常", 0).show();
					}
				}
			}).start();
		} else {
			attachId = "";
			upLoadDataHandler.sendEmptyMessage(UPLOAD_DATA_END);
		}
	}

	/**
	 * 是否早退
	 */
	@Deprecated
	private void isEarlyAttence() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + "Attendance/SignIsEarly";
				HttpUtils httpUtils = new HttpUtils();
				String result = httpUtils.httpGet(url);
				if (result.contains("true")) {
					upLoadDataHandler.sendEmptyMessage(ATTANCE_IS_EARLY);
				} else {
					upLoadDataHandler.sendEmptyMessage(ATTANCE_IS_NORMAL);
				}
			}
		}).start();
	}

	/**
	 * 是否迟到
	 */
	@Deprecated
	private void isLaterAttence() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + "Attendance/SignIsLate";
				HttpUtils httpUtils = new HttpUtils();
				String result = httpUtils.httpGet(url);
				if (result.contains("true")) {
					upLoadDataHandler.sendEmptyMessage(ATTANCE_IS_LATER);
				} else {
					upLoadDataHandler.sendEmptyMessage(ATTANCE_IS_NORMAL);
				}
			}
		}).start();
	}

	private void WriteLateReason(final String reason) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + "Attendance/WriteLateReason/"
						+ reason;
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.httpGet(url);
				upLoadDataHandler.sendEmptyMessage(ATTANCE_SUCCESS);
			}
		}).start();
	}

	private void WriteEarlyReason(final String reason) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = Global.BASE_URL + "Attendance/WriteEarlyReason/"
						+ reason;
				HttpUtils httpUtils = new HttpUtils();
				httpUtils.httpGet(url);
				upLoadDataHandler.sendEmptyMessage(ATTANCE_SUCCESS);
			}
		}).start();
	}

	/**
	 * 打考勤
	 * 
	 * @param tagFlag
	 */
	private void attendance(final boolean tagFlag) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (tagFlag) // 签到
					{
						// 上传图片附件号等数据到服务器
						mZLServiceHelper.Attendance1("" + mLat, "" + mLog,
								mLocation, attachId,
								ViewHelper.getDeviceToken(TagActivity.this),
								upLoadDataHandler);
					} else { // 签退
						// 上传图片附件号等数据到服务器
						mZLServiceHelper.Attendance2("" + mLat, "" + mLog,
								mLocation, attachId,
								ViewHelper.getDeviceToken(TagActivity.this),
								upLoadDataHandler);
					}
				} catch (Exception e) {
					Toast.makeText(TagActivity.this, "考勤登记异常", 0).show();
				}
			}
		}).start();

	}

	/**
	 * 开启地图显示定位图标页面
	 * 
	 * @param address
	 *            定位地址
	 * @param lat
	 *            定位纬度
	 * @param lon
	 *            定位经度
	 */
	private void startMapPionter(String address, String lat, String lon) {
		Intent intent = new Intent(TagActivity.this, TagMapActivity.class);
		intent.putExtra(TagMapActivity.ADDRESS, address);
		intent.putExtra(TagMapActivity.LATITUDE, lat + "");
		intent.putExtra(TagMapActivity.LONTITUDE, lon + "");
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i("onActivityResult", "onActivityResult");
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_ISEARLY) {
				// 早退
				// 取出任务内容
				Bundle bundle = data.getExtras();
				earlyReason = bundle.getString(TaskContentActivity.Content);
				LogUtils.i("signReason", earlyReason);
				WriteEarlyReason(earlyReason);
			} else if (requestCode == REQUEST_CODE_ISLATER) {
				// 迟到
				Bundle bundle = data.getExtras();
				latterReason = bundle.getString(TaskContentActivity.Content);
				LogUtils.i("signReason2", latterReason);
				WriteLateReason(latterReason);
			} else if (requestCode == REQUEST_CODE_LOCATION_LIST) {
				Bundle bundle = data.getExtras();
				BaiduPlace place = (BaiduPlace) bundle
						.getSerializable(LocationListActivity.RESULT);
				mLocation = getLocationAddress(mCountry, mCity, place);
				mTvAddress.setText(mLocation);
				mLatitude = place.location.lat;
				mLongitude = place.location.lng;
			} else if (requestCode == CAMERA_TAKE_HELPER) {
				if (data == null) {
					LogUtils.i("onActivityResult",
							"onActivityResult data isnull");
					// Toast.makeText(TagActivity.this, "调用系统相机异常",
					// Toast.LENGTH_SHORT).show();
				} else {
					// Uri uri = data.getData();
					// LogUtils.i("onActivityResult",
					// "onActivityResult---" + uri.toString());
					// mImageViewCamera.setImageURI(uri);
				}
				SharedPreferences sp = getSharedPreferences("config",
						Context.MODE_PRIVATE);
				mPictureFile = sp.getString("path", "");
				if (!TextUtils.isEmpty(mPictureFile)) {
					writeToPath = PhotoHelper.PATH + "/" + mPictureFile;
					LogUtils.i("onActivityResult", "writeToPath " + writeToPath);
					// 采样图片（缩小）
					Bitmap uploadPhoto = BitmapHelper
							.decodeSampleBitmapFromFile(writeToPath, 300, 300);
					// 更改路径:使用采样后的路径
					spPath = PhotoHelper.PATH + "/sf_" + mPictureFile;
					BitmapHelper.createThumBitmap(spPath, uploadPhoto);
					// mImageViewCamera.setImageBitmap(uploadPhoto);
					mImageViewCamera.setImageBitmap(PhotoHelper
							.disposeBitmapForListView(spPath));
					LogUtils.i("onActivityResult", "path---------" + spPath);
				} else {
					Toast.makeText(TagActivity.this, "调用系统相机异常",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void findViews() {
		mTextViewTime = (TextView) findViewById(R.id.textViewTime);
		// mtag_salary = (TextView) findViewById(R.id.tag_salary);
		llTagList = (LinearLayout) findViewById(R.id.ll_bottom_tag);
		String today = (new java.util.Date()).toLocaleString();
		mTextViewTime.setText(today);
		mTvAddress = (TextView) findViewById(R.id.tv_location_tag);
		mImageViewCamera = (ImageView) findViewById(R.id.AddImage_imageViewCamera);
		mButtonTag = (Button) findViewById(R.id.buttonTag);
		mButtonQuit = (Button) findViewById(R.id.buttonTag2); // 签退
		// addImageHelper = new AddImageHelper(this, this, mImageViewCamera,
		// photoSerialNo, true);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar_showupload = (ProgressBar) findViewById(R.id.progressBar_showupload);
		// mProgressBar.setVisibility(View.GONE);

		llPickLocation = (LinearLayout) findViewById(R.id.ll_location_pick);
		llMap = (LinearLayout) findViewById(R.id.ll_map_location_tag);
	}

	private void requestLocating() throws Exception {
		BaiduLocator.requestLocation(getApplicationContext(), this);
	}

	/**
	 * 是否使用模拟位置
	 */
	public Boolean isNotMockLocation() {
		// 0为没有使用
		boolean isNotMock = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
		return isNotMock;
	}

	/**
	 * 设置模拟位置
	 */
	private void initMockLocation() {
		// 弹出对话框
		new AlertDialog.Builder(TagActivity.this).setTitle("关闭模拟位置")
				.setMessage(" 手机打开了模拟定位，不能登记考勤").setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							// 开发者选项
							Intent intent = new Intent(
									Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
							startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
						} catch (Exception e) {
							Intent intent = new Intent(
									Settings.ACTION_APPLICATION_SETTINGS);
							startActivity(intent);
						}
					}
				}).show();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		LogUtils.i("BDLocationListener", "onReceiveLocation is running");
		if (location == null) {
			LogUtils.i("BDLocationListener", "onReceiveLocation is null");
		} else {
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			mLatitude = location.getLatitude();
			mLat = location.getLatitude();
			// mLatitude = 42.478988;
			// mLat = 42.478988;
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			mLongitude = location.getLongitude();
			mLog = location.getLongitude();
			// mLongitude = 99.820494;
			// mLog = 99.820494;
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			Log.v("BDLocationListener", sb.toString());
			if (location.getLocType() == BDLocation.TypeNetWorkException) {
				Context context = getApplicationContext();
				CharSequence text = "需要连接到3G或者wifi因特网！";
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				BaiduLocator.stop();
			}

			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// LinearLayout.LayoutParams.MATCH_PARENT,
			// LinearLayout.LayoutParams.WRAP_CONTENT);
			// // params.gravity = Gravity.LEFT;
			// mTvAddress.setLayoutParams(params);
			mLocation = location.getAddrStr();
			// mProvince = location.getProvince();
			mCity = location.getCity();
			mCountry = location.getCountry();
			mTvAddress.setText(mLocation);
			if (TextUtils.isEmpty(mLocation)) {
				mTvAddress.setHint("点击可调整定位地址..");
			}

			LogUtils.e("BDLocationListener", sb.toString());
			BaiduLocator.stop();

			// 定位成功，查看地图按钮可见
			llMap.setVisibility(View.VISIBLE);

			// mLat = 30.54340547308984;
			// mLog = 114.3290092883183;

			String locationRect = EarthMapUtils.getLocationRect(mLat, mLog,
					radiusMap);
			String url = "http://api.map.baidu.com/place/v2/search?query=楼$酒店$大厦$公司$小区$中心$公交$银行$学校$街道$路&bounds="
					+ locationRect
					+ "&output=json&ak=60d1e3a7095dd79b5cf2b58a5a1aaaa8";
			getLocationList(url, mCountry, mCity);
		}
	}

	private void getLocationList(final String url, final String country,
			final String city) {
		// ProgressDialogHelper.show(TagActivity.this, "定位中..");
		LogUtils.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpUtils httpUtils = new HttpUtils();
				String result = httpUtils.httpGet(url);
				LogUtils.i(TAG, result);
				try {
					JSONObject jo = new JSONObject(result);
					int status = jo.getInt("status");
					String message = jo.getString("message");
					String results = jo.getString("results");
					if (status == 0 && "ok".equals(message)) {
						List<BaiduPlace> list = JsonUtils.pareseJsonToList(
								results, BaiduPlace.class);
						LogUtils.i(TAG, "地址个数" + list.size());
						if (list.size() > 0) {
							if (list.size() > 1) {
								for (int i = 0; i < list.size(); i++) {
									BaiduPlace temp = list.get(i);
									LogUtils.d("distance2", temp.name
											+ temp.address + "---"
											+ temp.location.lat + ","
											+ temp.location.lng);
								}

								sortByDistance(list);

								// 根据距离由近到远排序
								for (int i = 0; i < list.size(); i++) {
									BaiduPlace temp = list.get(i);
									LogUtils.i("distance", temp.name
											+ temp.address + "---"
											+ temp.location.lat + ","
											+ temp.location.lng);
								}
							}

							BaiduPlace place = list.get(0);

							mLatitude = place.location.lat;
							mLongitude = place.location.lng;

							mLocation = getLocationAddress(country, city, place);

							TagActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mTvAddress.setGravity(Gravity.LEFT
											| Gravity.CENTER_VERTICAL);
									mTvAddress.setText(mLocation);
								}
							});
						}
					}
				} catch (Exception e) {
					LogUtils.e(TAG, "" + e.getMessage());
				}
			}
		}).start();
	}

	/** 根据距离对POI集合由近到远排序 */
	private void sortByDistance(List<BaiduPlace> list) {
		// 计算个点和初次定位的距离，然后保存到集合
		for (int i = 0; i < list.size(); i++) {
			// 如果个数大于1，计算距离最近的一个
			BaiduPlace item = list.get(i);
			// 计算和初次定位距离
			double distance = Math.sqrt(Math.pow(item.location.lat - mLat, 2)
					+ Math.pow(item.location.lng - mLog, 2));
			list.get(i).setDistance(distance);
		}

		// 根据距离由近到远排序
		for (int i = 0; i < list.size(); i++) {
			// 计算和初次定位距离
			double distance1 = list.get(i).getDistance();

			for (int j = 0; j < list.size(); j++) {
				double distance2 = list.get(j).getDistance();
				if (distance2 > distance1) {
					BaiduPlace temp = list.get(j);
					list.set(j, list.get(i));
					list.set(i, temp);
				}
			}
		}
	}

	/***
	 * 获取定位地址
	 * 
	 * @param country
	 *            县
	 * @param city
	 *            市
	 * @param place
	 * @return
	 */
	private String getLocationAddress(final String country, final String city,
			BaiduPlace place) {
		String address = place.name + " (" + place.address + ")";
		if (!TextUtils.isEmpty(city) && !address.contains(city)) {
			address = city + " " + address;
		}
		return address;
	}
}