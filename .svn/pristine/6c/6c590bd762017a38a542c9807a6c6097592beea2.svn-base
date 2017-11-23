package com.cedarhd;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.utils.ByteUtil;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

public class SetPasswordActivity extends BaseActivity {

	private Context mContext;
	private BoeryunHeaderView header;
	private EditText etOld;
	private EditText etNew;
	private EditText etNewAgain;
	private Button btnSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		initData();
		initViews();
		setOnEvent();
	}

	private void initData() {
		mContext = this;
	}

	private void initViews() {
		header = (BoeryunHeaderView) findViewById(R.id.header_set_pwd);
		etOld = (EditText) findViewById(R.id.et_old_pwd);
		etNew = (EditText) findViewById(R.id.et_new_pwd);
		etNewAgain = (EditText) findViewById(R.id.et_new_pwd2);
		btnSet = (Button) findViewById(R.id.btn_set_pwd);
	}

	private void setOnEvent() {
		header.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {

			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		btnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String oldPwd = etOld.getText().toString();
				String newPwd = etNew.getText().toString();
				String newPwdAgain = etNewAgain.getText().toString();
				if (TextUtils.isEmpty(oldPwd)) {
					showShortToast("请输入原密码");
					return;
				}

				if (TextUtils.isEmpty(newPwd)) {
					showShortToast("请输入新密码");
					return;
				}

				if (newPwd.length() < 6) {
					showShortToast("密码长度至少为6位");
					return;
				}

				if (!newPwd.equals(newPwdAgain)) {
					showShortToast("两次密码不一致，请重新输入");
					return;
				}

				resetPassword(ByteUtil.md5One(oldPwd), ByteUtil.md5One(newPwd));
			}
		});
	}

	private void resetPassword(String oldPwd, String newPwd) {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "account/ResetPassword/" + oldPwd + "/"
				+ newPwd + "";

		StringRequest.getAsyn(url, new StringResponseCallBack() {

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
//				reLogin();
				showShortToast("修改成功");
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast(INFO_ERRO_SERVER);
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponseCodeErro(String result) {
				
			}
		});
	}

	/**
	 * 下线通知
	 * 
	 * @param context
	 * @param title
	 *            通知标题
	 * @param content
	 *            通知内容
	 */
	private void reLogin() {
		Builder builder = new Builder(mContext);
		builder.setTitle("重新登录")
				.setMessage("修改密码成功")
				.setPositiveButton("重新登录",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 关闭现有所有打开的activity
								ExistApplication.getInstance().exit(false);
								Intent intent = new Intent(mContext,
										LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								mContext.startActivity(intent);
							}
						});
		AlertDialog alert = builder.create();
		alert.setCancelable(false);
		// 设置为全局对话框
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.show();
	}

}
