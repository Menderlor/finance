package com.cedarhd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.models.考勤信息;

public class SignActivity extends BaseActivity {

	考勤信息 mSign;
	TextView mTextViewDateValue;

	TextView mTextViewSignInTime;
	TextView mTextViewSignInState;
	TextView mTextViewSignInAddress;
	TextView mTextViewLateLabel;
	EditText mEditTextLateReason;
	Button mButtonSubmitLateReason;

	TextView mTextViewSignOutTime;
	TextView mTextViewSignOutState;
	TextView mTextViewSignOutAddress;
	TextView mTextViewEarlyLabel;
	EditText mEditTextEarlyReason;
	Button mButtonSubmitEarlyReason;

	SendReasonHandler mHanlder = new SendReasonHandler();

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.sign);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_sign_detail);

		Bundle bundle = this.getIntent().getExtras();
		mSign = (考勤信息) bundle.getSerializable("Sign");

		findViews();
		setOnClickListener();
		// Init();
	}

	// void Init() {
	// if (mSign != null) {
	// mTextViewDateValue.setText(DateTimeUtil.ConvertDateToString(mSign.get考勤日期()));

	// if (mSign.get签到时间() != null) {
	// mTextViewSignInState.setVisibility(View.VISIBLE);
	// mTextViewSignInAddress.setVisibility(View.VISIBLE);
	// mTextViewSignInTime.setText(DateTimeUtil.ConvertLongDateToString(mSign.get签到时间()));
	// if (mSign.is是否迟到()) {
	// mTextViewSignInState.setText("迟到");
	// mTextViewSignInState.setTextColor(Color.RED);
	//
	// mTextViewLateLabel.setVisibility(View.VISIBLE);
	// mEditTextLateReason.setVisibility(View.VISIBLE);
	// if (mSign.get迟到原因() != null) {
	// mEditTextLateReason.setText(mSign.get迟到原因());
	// mEditTextLateReason.setEnabled(false);
	// mButtonSubmitLateReason.setVisibility(View.GONE);
	// } else {
	// mEditTextLateReason.setEnabled(true);
	// mButtonSubmitLateReason.setVisibility(View.VISIBLE);
	// }
	//
	// } else {
	// mTextViewSignInState.setText("正常");
	// mTextViewSignInState.setTextColor(Color.GREEN);
	//
	// mTextViewLateLabel.setVisibility(View.GONE);
	// mEditTextLateReason.setVisibility(View.GONE);
	// mButtonSubmitLateReason.setVisibility(View.GONE);
	// }
	// if (mSign.get地理位置_签到() != null) {
	// mTextViewSignInAddress.setText(mSign.get地理位置_签到());
	// } else {
	// mTextViewSignInAddress.setVisibility(View.GONE);
	// }
	// } else {
	// mTextViewSignInTime.setText("无");
	// mTextViewSignInState.setVisibility(View.INVISIBLE);
	// mTextViewSignInAddress.setVisibility(View.GONE);
	//
	// mTextViewLateLabel.setVisibility(View.GONE);
	// mEditTextLateReason.setVisibility(View.GONE);
	// mButtonSubmitLateReason.setVisibility(View.GONE);
	// }

	// if (mSign.get签退时间() != null) {
	// mTextViewSignOutState.setVisibility(View.VISIBLE);
	// mTextViewSignOutAddress.setVisibility(View.VISIBLE);
	// mTextViewSignOutTime.setText(DateTimeUtil.ConvertLongDateToString(mSign.get签退时间()));
	// if (mSign.is是否早退()) {
	// mTextViewSignOutState.setText("早退");
	// mTextViewSignOutState.setTextColor(Color.RED);
	//
	// mTextViewEarlyLabel.setVisibility(View.VISIBLE);
	// mEditTextEarlyReason.setVisibility(View.VISIBLE);
	//
	// if (mSign.get早退原因() != null) {
	// mEditTextEarlyReason.setEnabled(false);
	// mEditTextEarlyReason.setText(mSign.get早退原因());
	// mButtonSubmitEarlyReason.setVisibility(View.GONE);
	// } else {
	// mEditTextEarlyReason.setEnabled(true);
	// mButtonSubmitEarlyReason.setVisibility(View.VISIBLE);
	// }
	// } else {
	// mTextViewSignOutState.setText("正常");
	// mTextViewSignOutState.setTextColor(Color.GREEN);
	//
	// mTextViewEarlyLabel.setVisibility(View.GONE);
	// mEditTextEarlyReason.setVisibility(View.GONE);
	// mButtonSubmitEarlyReason.setVisibility(View.GONE);
	// }
	// if (mSign.get地理位置_签退() != null) {
	// mTextViewSignOutAddress.setText(mSign.get地理位置_签退());
	// } else {
	// mTextViewSignOutAddress.setVisibility(View.GONE);
	// }
	// } else {
	// mTextViewSignOutTime.setText("无");
	// mTextViewSignOutState.setVisibility(View.INVISIBLE);
	// mTextViewSignOutAddress.setVisibility(View.GONE);
	//
	// mTextViewEarlyLabel.setVisibility(View.GONE);
	// mEditTextEarlyReason.setVisibility(View.GONE);
	// mButtonSubmitEarlyReason.setVisibility(View.GONE);
	// }
	// }
	// }

	public void setOnClickListener() {
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mButtonSubmitLateReason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String lateReason = mEditTextLateReason.getText()
						.toString();

				if (lateReason == null || lateReason.equals("")) {
					Toast.makeText(SignActivity.this, "请填写迟到原因！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// ZLServiceHelper dh = new ZLServiceHelper();
						// dh.sendLateReason(mSign.get编号() + "", lateReason,
						// mHanlder);
					}
				}).start();
			}
		});

		mButtonSubmitEarlyReason.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String earlyReason = mEditTextEarlyReason.getText()
						.toString();

				if (earlyReason == null || earlyReason.equals("")) {
					Toast.makeText(SignActivity.this, "请填写早退原因！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// ZLServiceHelper dh = new ZLServiceHelper();
						// dh.sendEarlyReason(mSign.get编号() + "", earlyReason,
						// mHanlder);
					}
				}).start();
			}
		});
	}

	public void findViews() {
		mTextViewDateValue = (TextView) findViewById(R.id.textViewDateValue);

		mTextViewSignInTime = (TextView) findViewById(R.id.textViewSignInTime);
		mTextViewSignInState = (TextView) findViewById(R.id.textViewSignInState);
		mTextViewSignInAddress = (TextView) findViewById(R.id.textViewSignInAddress);
		mTextViewLateLabel = (TextView) findViewById(R.id.textViewLateLabel);
		mEditTextLateReason = (EditText) findViewById(R.id.editTextLateReason);
		mButtonSubmitLateReason = (Button) findViewById(R.id.buttonsSubmitLateReason);

		mTextViewSignOutTime = (TextView) findViewById(R.id.textViewSignOutTime);
		mTextViewSignOutState = (TextView) findViewById(R.id.textViewSignOutState);
		mTextViewSignOutAddress = (TextView) findViewById(R.id.textViewSignOutAddress);
		mTextViewEarlyLabel = (TextView) findViewById(R.id.textViewEarlyLabel);
		mEditTextEarlyReason = (EditText) findViewById(R.id.editTextEarlyReason);
		mButtonSubmitEarlyReason = (Button) findViewById(R.id.buttonSubmitEarlyReason);
	}

	public class SendReasonHandler extends Handler {

		public static final int SEND_LATE_REASON_SUCCESS = 0;
		public static final int SEND_LATE_REASON_FAILED = 1;
		public static final int SEND_EARLY_REASON_SUCCESS = 2;
		public static final int SEND_EARLY_REASON_FAILED = 3;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int whatMsg = msg.what;
			switch (whatMsg) {
			case SEND_LATE_REASON_SUCCESS:
				Toast.makeText(SignActivity.this, "提交迟到原因成功!",
						Toast.LENGTH_SHORT).show();

				mEditTextLateReason.setEnabled(false);
				mButtonSubmitLateReason.setVisibility(View.GONE);
				break;
			case SEND_LATE_REASON_FAILED:
				Toast.makeText(SignActivity.this, "提交迟到原因失败!",
						Toast.LENGTH_SHORT).show();
				break;
			case SEND_EARLY_REASON_SUCCESS:
				Toast.makeText(SignActivity.this, "提交早退原因成功!",
						Toast.LENGTH_SHORT).show();

				mEditTextEarlyReason.setEnabled(false);
				mButtonSubmitEarlyReason.setVisibility(View.GONE);
				break;
			case SEND_EARLY_REASON_FAILED:
				Toast.makeText(SignActivity.this, "提交早退原因失败!",
						Toast.LENGTH_SHORT).show();
				break;
			}

			super.handleMessage(msg);
		}

	}
}
