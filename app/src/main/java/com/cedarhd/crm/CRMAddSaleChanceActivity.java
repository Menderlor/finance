package com.cedarhd.crm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.fragment.CRMAddNewClientFragment;
import com.cedarhd.fragment.CRMAddSaleChanceFragment;
import com.cedarhd.utils.LogUtils;

/***
 * 销售统计版 新建销售机会
 * 
 * 分为新客户和老客户两种选择
 * 
 * @author K
 * 
 */
public class CRMAddSaleChanceActivity extends BaseActivity {

	/** 客户类型 0：老客户，1:为新客户 */
	public final static String TYPE_CLIENT = "client_type";

	/** 客户类型 0：老客户，1:为新客户 */
	private int clientType;

	private Context mContext;
	private FragmentManager mFm;

	private ImageView ivBack;
	private ImageView ivSave;
	private RadioButton rbNew;
	private RadioButton rbOld;
	private RadioGroup rGroup;
	private LinearLayout llRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crm_add_salechance);
		initData();
		initViews();
		initFragment();
		setEventListener();
	}

	private void initData() {
		mContext = this;
		mFm = getSupportFragmentManager();
		clientType = getIntent().getIntExtra(TYPE_CLIENT, 0);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_add_salechance);
		ivSave = (ImageView) findViewById(R.id.iv_save_add_salechance);
		rbNew = (RadioButton) findViewById(R.id.rb_new_client);
		rbOld = (RadioButton) findViewById(R.id.rb_old_client);
		rGroup = (RadioGroup) findViewById(R.id.rgroup_add_salechance);
	}

	@SuppressLint("Recycle")
	private void initFragment() {
		FragmentTransaction tran = mFm.beginTransaction();
		if (clientType == 0) {
			tran.add(R.id.ll_root_add_salechance,
					new CRMAddNewClientFragment(), "新建客户");
		} else {
			tran.add(R.id.ll_root_add_salechance,
					new CRMAddSaleChanceFragment(), "销售机会");
		}
		tran.commit();
	}

	private void setEventListener() {
		//TODO 注释掉 无用
		rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				FragmentTransaction tran = mFm.beginTransaction();
				switch (checkedId) {
				case R.id.rb_new_client:
					tran.replace(R.id.ll_root_add_salechance,
							new CRMAddNewClientFragment(), "新建客户");
					break;
				case R.id.rb_old_client:
					tran.replace(R.id.ll_root_add_salechance,
							new CRMAddSaleChanceFragment(), "销售机会");
					break;
				}
				tran.commit();
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

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); // 必须调用父类方法
		LogUtils.i(TAG, "requestCode:" + requestCode);
	}
}
