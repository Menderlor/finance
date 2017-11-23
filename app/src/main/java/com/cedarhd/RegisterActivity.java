package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.RegexUtils;

/**
 * 新用户注册页面
 * 
 * @author BOHR
 * 
 */
public class RegisterActivity extends BaseActivity {

	public static final String TAG = "RegisterActivity";
	private Context context;
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private EditText etCompany;
	private EditText etUser;
	private EditText etPwd;
	private EditText etPhone;
	private EditText etConfirmPwd;
	private ImageView reg;
	private ImageView ivCancel;
	private String corpName, pwd, confirmPwd, phone, contacts;

	private final int SUCCESS_REGISTER = 3;
	private final int FAILURE_REGISTER = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCCESS_REGISTER) {// 成功
				ProgressDialogHelper.dismiss();
				Toast.makeText(RegisterActivity.this,
						"企业 " + corpName + " 注册成功", Toast.LENGTH_LONG).show();
				User user = new User();
				user.CorpName = corpName;
				user.UserName = contacts;
				user.PassWord = pwd;
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(TAG, user);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			} else if (msg.what == FAILURE_REGISTER) { // 失败
				ProgressDialogHelper.dismiss();
				String message = msg.obj.toString();
				Toast.makeText(RegisterActivity.this, message,
						Toast.LENGTH_SHORT).show();
				reg.setEnabled(true);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		context = RegisterActivity.this;
		findviews();
		setOnclick();
	}

	private void findviews() {
		// 初始化控件
		etCompany = (EditText) findViewById(R.id.et_register_company);
		etCompany.setOnFocusChangeListener(onFocusAutoClearListener);
		etUser = (EditText) findViewById(R.id.et_register_user);
		etPwd = (EditText) findViewById(R.id.et_register_pwd);
		etPwd.setOnFocusChangeListener(onFocusAutoClearListener);
		reg = (ImageView) findViewById(R.id.btn_register);
		ivCancel = (ImageView) findViewById(R.id.imageViewCancel_register);
		etPhone = (EditText) findViewById(R.id.et_register_phone);
		etPhone.setOnFocusChangeListener(onFocusAutoClearListener);
		etConfirmPwd = (EditText) findViewById(R.id.et_register_confirm_password);
		etConfirmPwd.setOnFocusChangeListener(onFocusAutoClearListener);
	}

	private void setOnclick() {
		// 注册按钮
		reg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (isCheck()) {
					reg.setEnabled(false);
					// pBar.setVisibility(View.VISIBLE);
					ProgressDialogHelper.show(context, "注册中..");
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								boolean isResult = zlServiceHelper
										.RegisterCorp(corpName, pwd, phone,
												contacts);
								if (isResult) {
									handler.sendEmptyMessage(SUCCESS_REGISTER);
								} else {
									handler.sendEmptyMessage(SUCCESS_REGISTER);
								}
							} catch (Exception e) {
								LogUtils.e("erro", "注册失败：" + e);
								handler.sendEmptyMessage(SUCCESS_REGISTER);
							}
						}
					}).start();
				}
			}
		});
		// 返回
		ivCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private boolean isCheck() {
		boolean result = true;
		corpName = etCompany.getText().toString();
		pwd = etPwd.getText().toString();
		phone = etPhone.getText().toString();
		contacts = etUser.getText().toString();
		confirmPwd = etConfirmPwd.getText().toString();

		if (TextUtils.isEmpty(corpName) || TextUtils.isEmpty(pwd)
				|| TextUtils.isEmpty(phone) || TextUtils.isEmpty(contacts)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "注册信息不完整", Toast.LENGTH_SHORT)
					.show();
		} else if (!RegexUtils.isSpecialCharactor(corpName)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "企业名格式不正确",
					Toast.LENGTH_SHORT).show();
		} else if (!RegexUtils.isSpecialCharactor(contacts)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "企业负责格式不正确",
					Toast.LENGTH_SHORT).show();
		} else if (!RegexUtils.isMobile(phone)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "手机号格式不正确",
					Toast.LENGTH_SHORT).show();
		} else if (!RegexUtils.isCharactor(pwd)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "密码只能输入数字或英文字母",
					Toast.LENGTH_SHORT).show();
		} else if (!pwd.equals(confirmPwd)) {
			result = false;
			Toast.makeText(RegisterActivity.this, "两次输入密码不一致",
					Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public static OnFocusChangeListener onFocusAutoClearListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			EditText textView = (EditText) v;
			String hint;
			if (hasFocus) {
				hint = textView.getHint().toString();
				textView.setTag(hint);
				textView.setHint("");
			} else {
				hint = textView.getTag().toString();
				textView.setHint(hint);
			}
		}
	};
}
