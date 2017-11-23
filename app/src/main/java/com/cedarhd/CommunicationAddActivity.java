package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.联系人;

/**
 * 通讯录-新建联系人
 * 
 * @author KJX
 * @since 2015-03-02
 */
public class CommunicationAddActivity extends BaseActivity {
	public static final String TAG = "CommunicationAddActivity";
	public static final String IS_EDIT = "isEdit";
	private Context context;

	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private 联系人 mContact = new 联系人();

	private ImageView ivBack;
	private ImageView ivSave;
	private EditText etName;
	private EditText etMobile;
	private EditText etPhone;
	private EditText etEmail;
	private EditText etAddress;
	private LinearLayout llCall;

	private static final int SUCCESS_SAVE = 3;
	private static final int FAILURE_SAVE = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_SAVE:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
				CommunicationListActivity.isResume = true;
				finish();
				break;
			case FAILURE_SAVE:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communication_add);
		context = CommunicationAddActivity.this;
		findViews();
		setOnClick();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			联系人 item = (联系人) bundle.getSerializable(TAG);
			boolean isEdit = bundle.getBoolean(IS_EDIT);
			initData(item, isEdit);
		}
	}

	private void findViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_cummunication_add);
		ivSave = (ImageView) findViewById(R.id.iv_save_cummunication_add);
		etName = (EditText) findViewById(R.id.et_name_cummunication_add);
		etMobile = (EditText) findViewById(R.id.et_mobilephone_cummunication_add);
		etPhone = (EditText) findViewById(R.id.et_phone_cummunication_add);
		etEmail = (EditText) findViewById(R.id.et_email_cummunication_add);
		etAddress = (EditText) findViewById(R.id.et_address_cummunication_add);
		llCall = (LinearLayout) findViewById(R.id.ll_call_cummunication_add);
	}

	private void initData(联系人 item, boolean isEdit) {
		if (item != null) {
			etName.setText(item.getName() + "");
			etMobile.setText(item.getMobilePhone() + "");
			etPhone.setText(item.getPhone() + "");
			etEmail.setText(item.getEmail() + "");
			etAddress.setText(item.getAddress() + "");
		}

		if (!isEdit) {
			ivSave.setVisibility(View.GONE);
			etName.setFocusable(false);
			etMobile.setFocusable(false);
			etPhone.setFocusable(false);
			etEmail.setFocusable(false);
			etAddress.setFocusable(false);
		}
	}

	/**
	 * 绑定监听事件
	 */
	private void setOnClick() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecked()) {
					ProgressDialogHelper.show(context);
					new Thread(new Runnable() {
						@Override
						public void run() {
							boolean isResult = zlServiceHelper
									.SaveContactPerson(mContact);
							if (isResult) {
								handler.sendEmptyMessage(SUCCESS_SAVE);
							} else {
								handler.sendEmptyMessage(FAILURE_SAVE);
							}
						}
					}).start();
				}
			}
		});

		// 拨号
		llCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNoStr = etMobile.getText().toString();
				if (!TextUtils.isEmpty(phoneNoStr)) {
					call(phoneNoStr);
				} else {
					Toast.makeText(context, "请先输入手机号码", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	/**
	 * 跳转拨号页面
	 * 
	 * @param number
	 *            电话号码
	 */
	private void call(String number) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:" + number));
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(context, "打开拨号器异常", 0).show();
		}
	}

	/**
	 * 提交校验，必填项不能为空
	 */
	private boolean isChecked() {
		boolean isChecked = false;
		String name = etName.getText().toString();
		String mobile = etMobile.getText().toString();
		String phone = etPhone.getText().toString();
		String email = etEmail.getText().toString();
		String address = etAddress.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Toast.makeText(context, "联系人姓名不能为空", Toast.LENGTH_SHORT).show();
		} else if (TextUtils.isEmpty(mobile)) {
			Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
		} else {
			isChecked = true;
			mContact.Name = name;
			mContact.MobilePhone = mobile;
			if (!TextUtils.isEmpty(phone)) {
				mContact.Phone = phone;
			}
			if (!TextUtils.isEmpty(email)) {
				mContact.Email = email;
			}
			if (!TextUtils.isEmpty(address)) {
				mContact.Address = address;
			}
		}
		return isChecked;
	}
}
