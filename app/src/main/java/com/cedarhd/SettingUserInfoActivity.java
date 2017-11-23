package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;

/**
 * 显示用户信息
 * 
 * @author BOHR
 * 
 */
public class SettingUserInfoActivity extends BaseActivity {

	private TextView company;
	private TextView user;
	private ImageView ivCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_userinfo);
		findViews();
	}

	private void findViews() {
		ivCancel = (ImageView) findViewById(R.id.iv_cancel_setting_userinfo);
		company = (TextView) findViewById(R.id.tv_userinfo_setting_company);
		user = (TextView) findViewById(R.id.tv_useinfo_setting_user);

		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		user.setText(Global.mUser.UserName);
		company.setText(Global.mUser.CorpName);
	}
}
