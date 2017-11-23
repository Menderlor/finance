package com.cedarhd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.PhotoHelper;
import com.cedarhd.helpers.PictureUtils;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.考勤信息;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;

import java.io.File;

/**
 * 考勤详情
 */
public class TagSalaryInfoActivity extends BaseActivity {

	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	private PhotoHelper photoHelper;
	private PictureUtils pictureUtils;
	考勤信息 mAttendence;
	String signInfileName = "";// 图片的名字
	String signOutfileName = "";// 签退图片的名字

	TextView mTextViewTitle;
	TextView mTextViewTime; // 签到时间
	TextView mTextViewTimeSignOut;// 签退时间
	TextView mTextViewPublisherName;
	TextView mTextViewReceiverName;
	TextView mTextViewSignInPos;
	TextView mTvSignOutPos;
	TextView mTvLateReason;
	TextView mTvEarlyReason;
	// ImageView mImageViewNew;
	// HorizontalScrollViewAddImage imageView_attachFileName;
	ImageView iv_signIn; // 签到图片
	ImageView iv_signOut; // 签退图片

	// TextView mTextViewLongitude;
	// TextView mTextViewLatitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		photoHelper = new PhotoHelper(TagSalaryInfoActivity.this);
		// setContentView(R.layout.tagsalaryinfo);
		setContentView(R.layout.tagsalaryinfo_new);

		Bundle bundle = this.getIntent().getExtras();
		mAttendence = (考勤信息) bundle.getSerializable("Attendence");

		findViews();
		setOnClickListener();
		Init();
	}

	private void Init() {
		pictureUtils = new PictureUtils(this);
		String signIntime = mAttendence.getSignInTime() != null ? DateDeserializer
				.getFormatTime(mAttendence.getSignInTime()) : "";
		mTextViewTime.setText("" + signIntime);// "登入时间:"+//DateTimeUtil.ConvertLongDateToString(mAttendence.ReleaseTime));

		String signOutTime = mAttendence.getSignOutTime() != null ? DateDeserializer
				.getFormatTime(mAttendence.getSignOutTime()) : "";
		mTextViewTimeSignOut.setText("" + signOutTime);

		// mTextViewReceiverName.setText("接收人:" );
		mTextViewSignInPos.setText(mAttendence.getPositionSignIn());
		mTvSignOutPos.setText(mAttendence.getPositionSignOut());
		// mTextViewLongitude.setText("" + mAttendence.getLongitude());
		// mTextViewLatitude.setText("" + mAttendence.getLatitude());

		final String photoId = mAttendence.PhotoSerialNo;

		if (!TextUtils.isEmpty(photoId)) {
			iv_signIn.setVisibility(View.VISIBLE);
			pictureUtils.showPicture(TagSalaryInfoActivity.this, photoId,
					iv_signIn);
		} else {
			iv_signIn.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(mAttendence.SignInPicURI)) {
			LogUtils.i("signInPIC", mAttendence.SignInPicURI);
			signInfileName = mAttendence.SignInPicURI;
			int index = 0;
			if (signInfileName.contains("\\")) {
				index = signInfileName.lastIndexOf("\\");
			} else if (signInfileName.contains("/")) {
				index = signInfileName.lastIndexOf("/");
			}
			signInfileName = signInfileName.substring(index + 1,
					signInfileName.length());
		}

		if (!TextUtils.isEmpty(mAttendence.SignOutPicURI)) {
			LogUtils.i("signOutPIC", mAttendence.SignOutPicURI);
			signOutfileName = mAttendence.SignOutPicURI;
			int index = 0;
			if (signOutfileName.contains("\\")) {
				index = signOutfileName.lastIndexOf("\\");
			} else if (signOutfileName.contains("/")) {
				index = signOutfileName.lastIndexOf("/");
			}
			signOutfileName = signOutfileName.substring(index + 1,
					signOutfileName.length());
		}
		// 签退图片
		final String photoSignOutId = mAttendence.PhotoSingnOut;
		if (!TextUtils.isEmpty(photoSignOutId)) {
			pictureUtils.showPicture(TagSalaryInfoActivity.this,
					photoSignOutId, iv_signOut);
		} else {
			iv_signOut.setVisibility(View.GONE);
		}

		if (mAttendence.IsLater) {
			mTvLateReason.setText(StrUtils.pareseNull(mAttendence.LaterReason));
		}

		if (mAttendence.isIsEarly()) {
			mTvEarlyReason
					.setText(StrUtils.pareseNull(mAttendence.EarlyReason));
		}
	}

	public void setOnClickListener() {
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		iv_signIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);// 调用系统的图片查看器
				Uri mUri = Uri.fromFile(new File(PhotoHelper.PATH,
						signInfileName));// 图片的路径
				LogUtils.i("signInPIC", "iv_signIn:" + signInfileName);
				intent.setDataAndType(mUri, "image/*");// 设置数据和格式
				startActivity(intent);
			}
		});

		iv_signOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW);// 调用系统的图片查看器
				Uri mUri = Uri.fromFile(new File(PhotoHelper.PATH,
						signOutfileName));// 图片的路径

				LogUtils.i("signOutPIC", "iv_signOut:" + signOutfileName);
				intent.setDataAndType(mUri, "image/*");// 设置数据和格式
				startActivity(intent);
			}
		});

		mTextViewSignInPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mAttendence.PositionSignIn)) {
					// // 查看签到地址，如果存在签退地址，只传地址（经纬度被签退占用）
					// if (TextUtils.isEmpty(mAttendence.PositionSignOut)) {
					// startMapPionter(mAttendence.PositionSignOut,
					// mAttendence.Latitude + "",
					// mAttendence.Longitude + "");
					// }

					startMapPionter(mAttendence.PositionSignIn,
							mAttendence.Longitude + "", mAttendence.Latitude
									+ "");
				}
			}
		});

		/***
		 * 查看签退
		 */
		mTvSignOutPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mAttendence.PositionSignOut)) {
					startMapPionter(mAttendence.PositionSignOut,
							mAttendence.OutLatitude + "",
							mAttendence.OutLongitude + "");
				}
			}
		});
	}

	public void findViews() {
		mTextViewTitle = (TextView) findViewById(R.id.textViewTitle);
		mTextViewTime = (TextView) findViewById(R.id.textViewTime);
		mTextViewTimeSignOut = (TextView) findViewById(R.id.textViewTime_signOut);
		mTextViewPublisherName = (TextView) findViewById(R.id.textViewPublisherName);
		mTextViewReceiverName = (TextView) findViewById(R.id.textViewReceiverName);
		mTextViewSignInPos = (TextView) findViewById(R.id.textViewContent);
		mTvSignOutPos = (TextView) findViewById(R.id.tv_signOut_address_info);
		// mTextViewLongitude = (TextView) findViewById(R.id.textViewLongitude);
		// mTextViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
		iv_signIn = (ImageView) findViewById(R.id.addImageView_tagsalary);
		iv_signOut = (ImageView) findViewById(R.id.addImageView_tagsalary_signout);
		mTvLateReason = (TextView) findViewById(R.id.tv_reason_late_tag_info);
		mTvEarlyReason = (TextView) findViewById(R.id.tv_reason_earlyLeave_tag_info);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 处理照相机返回的数据
		if (resultCode == RESULT_OK) {
			// if (requestCode == addImageHelper.CAMERA_TAKE_HELPER) {
			// addImageHelper.refresh();
			// }
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		Intent intent = new Intent(TagSalaryInfoActivity.this,
				TagMapActivity.class);
		intent.putExtra(TagMapActivity.ADDRESS, address);
		intent.putExtra(TagMapActivity.LATITUDE, lat + "");
		intent.putExtra(TagMapActivity.LONTITUDE, lon + "");
		startActivity(intent);
	}

}