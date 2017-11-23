package com.cedarhd;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;

/**
 * 找回密码
 * 
 * @author BOHR
 * 
 */
public class FindPasswordActivity extends BaseActivity implements
		OnClickListener {
	private EditText etCompany;
	private EditText etUser;
	private EditText etEmail;
	private EditText etCode;
	private ImageView ivCode;
	private ImageView ivCancel;
	private TextView tvCode;
	private Button btnSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findpassword);
		findviews();
		setOnclickListener();
	}

	public void findviews() {
		etCompany = (EditText) findViewById(R.id.editTextcompany);
		etUser = (EditText) findViewById(R.id.editTextuser);
		etEmail = (EditText) findViewById(R.id.editTextemail);
		etCode = (EditText) findViewById(R.id.editTextcode);
		ivCode = (ImageView) findViewById(R.id.iv_code);
		ivCancel = (ImageView) findViewById(R.id.iv_back_findpassword);
		tvCode = (TextView) findViewById(R.id.tv_code);
		btnSubmit = (Button) findViewById(R.id.findpassword_submit);
	}

	public void setOnclickListener() {
		etCompany.setOnClickListener(this);
		etUser.setOnClickListener(this);
		etEmail.setOnClickListener(this);
		etCode.setOnClickListener(this);
		ivCode.setOnClickListener(this);
		ivCancel.setOnClickListener(this);
		tvCode.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.editTextcompany:

			break;
		case R.id.editTextuser:

			break;
		case R.id.editTextemail:

			break;
		case R.id.editTextcode:

			break;
		case R.id.iv_code:

			break;
		case R.id.tv_code:

			break;
		case R.id.findpassword_submit:

			break;
		case R.id.iv_back_findpassword:
			finish();
			break;
		default:
			break;
		}
	}
}
