package com.cedarhd.crm;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.DictionaryBiz;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Dict;
import com.cedarhd.models.crm.线索;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

/***
 * 新建线索CRM
 * 
 * @author K 2015-9-29
 */
public class CRMAddXiansuoActivity extends BaseActivity {

	/** 线索实体 key */
	public static final String CLEW = "clew";

	/** 大字典集合 key */
	public static final String DICTIONARYS = "dictionarys";

	private Context mContext;
	private 线索 mClew;
	private HashMap<String, List<Dict>> mDictionarys;

	private ImageView ivBack;
	private ImageView ivSave;
	/** 点击展开按钮 */
	private LinearLayout llMore;
	/** 展开内容区域 */
	private LinearLayout llInfo;

	private BoeryunDictSelectView et_source_add_xiansuo;
	private BoeryunDictSelectView et_category_add_xiansuo;
	private BoeryunDictSelectView et_product_add_xiansuo;
	private BoeryunDateSelectView et_timecaigou_add_xiansuo;
	private EditText et_total_add_xiansuo;
	private EditText et_company_add_xiansuo;
	private EditText et_contacts_add_xiansuo;
	private EditText et_phone_add_xiansuo;
	private EditText et_keyword_add_xiansuo;
	private EditText et_address_add_xiansuo;
	private EditText et_info_add_xiansuo;
	private EditText et_wechart_add_xiansuo;
	private EditText et_qq_add_xiansuo;
	private EditText et_wangwang_add_xiansuo;
	private EditText et_email_add_xiansuo;
	private EditText et_url_add_xiansuo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_xiansuo_crm);
		initData();
		initViews();
		setOnEvent();

		initClew();
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		mContext = CRMAddXiansuoActivity.this;
		mClew = new 线索();
		mDictionarys = new HashMap<String, List<Dict>>();

	}

	private void initClew() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mClew = (线索) bundle.getSerializable(CLEW);
			mDictionarys = (HashMap<String, List<Dict>>) bundle
					.getSerializable(DICTIONARYS);

			if (mClew != null) {
				et_source_add_xiansuo.setText(DictionaryBiz.getDictName(
						mDictionarys, "来源", mClew.来源) + "");
				et_category_add_xiansuo.setText(DictionaryBiz.getDictName(
						mDictionarys, "分类", mClew.分类) + "");
				et_product_add_xiansuo.setText(DictionaryBiz.getDictName(
						mDictionarys, "意向产品", mClew.意向产品) + "");
				et_total_add_xiansuo.setText(mClew.预计金额 + "");
				et_timecaigou_add_xiansuo.setText(mClew.预计采购时间 + "");
				et_company_add_xiansuo.setText(mClew.公司名称 + "");
				et_contacts_add_xiansuo.setText(mClew.联系人 + "");
				et_phone_add_xiansuo.setText(mClew.联系电话 + "");
				et_keyword_add_xiansuo.setText(mClew.关键字 + "");
				et_address_add_xiansuo.setText(mClew.地址 + "");
				et_info_add_xiansuo.setText(mClew.描述 + "");
				et_wechart_add_xiansuo.setText(mClew.微信 + "");
				et_qq_add_xiansuo.setText(mClew.QQ + "");
				et_wangwang_add_xiansuo.setText(mClew.旺旺 + "");
				et_email_add_xiansuo.setText(mClew.邮箱 + "");
				et_url_add_xiansuo.setText(mClew.网址 + "");
			}
		}
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_add_xiansuo);
		ivSave = (ImageView) findViewById(R.id.iv_save_add_xiansuo);
		llMore = (LinearLayout) findViewById(R.id.ll_more_add_xiansuo);
		llInfo = (LinearLayout) findViewById(R.id.ll_info_more_add_xiansuo);

		et_source_add_xiansuo = (BoeryunDictSelectView) findViewById(R.id.et_source_add_xiansuo);
		et_category_add_xiansuo = (BoeryunDictSelectView) findViewById(R.id.et_category_add_xiansuo);
		et_product_add_xiansuo = (BoeryunDictSelectView) findViewById(R.id.et_product_add_xiansuo);
		et_total_add_xiansuo = (EditText) findViewById(R.id.et_total_add_xiansuo);
		et_timecaigou_add_xiansuo = (BoeryunDateSelectView) findViewById(R.id.et_timecaigou_add_xiansuo);
		et_company_add_xiansuo = (EditText) findViewById(R.id.et_company_add_xiansuo);
		et_contacts_add_xiansuo = (EditText) findViewById(R.id.et_contacts_add_xiansuo);
		et_phone_add_xiansuo = (EditText) findViewById(R.id.et_phone_add_xiansuo);
		et_keyword_add_xiansuo = (EditText) findViewById(R.id.et_keyword_add_xiansuo);
		et_address_add_xiansuo = (EditText) findViewById(R.id.et_address_add_xiansuo);
		et_info_add_xiansuo = (EditText) findViewById(R.id.et_info_add_xiansuo);
		et_wechart_add_xiansuo = (EditText) findViewById(R.id.et_wechart_add_xiansuo);
		et_qq_add_xiansuo = (EditText) findViewById(R.id.et_qq_add_xiansuo);
		et_wangwang_add_xiansuo = (EditText) findViewById(R.id.et_wangwang_add_xiansuo);
		et_email_add_xiansuo = (EditText) findViewById(R.id.et_email_add_xiansuo);
		et_url_add_xiansuo = (EditText) findViewById(R.id.et_url_add_xiansuo);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheck()) {
					saveClew();
				}
			}
		});

		llMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				llMore.setVisibility(View.GONE);
				llInfo.setVisibility(View.VISIBLE);
			}
		});
	}

	/***
	 * 提交校验
	 * 
	 * @return 如果必填项为空则返回false
	 */
	private boolean isCheck() {
		字典 source字典 = (字典) et_source_add_xiansuo.getTag();
		字典 product字典 = (字典) et_product_add_xiansuo.getTag();
		String clientName = et_company_add_xiansuo.getText().toString();
		String contact = et_contacts_add_xiansuo.getText().toString();

		if (mClew.来源 != 0) {
			if (source字典 != null) {
				mClew.来源 = source字典.getId();
			}
		} else {
			showShortToast("请选择来源");
			return false;
		}

		if (mClew.意向产品 != 0) {
			if (product字典 != null) {
				mClew.意向产品 = product字典.getId();
			}
		} else {
			showShortToast("请选择意向产品");
			return false;
		}

		/** 非必填项赋值 */
		字典 category字典 = (字典) et_category_add_xiansuo.getTag();
		if (mClew.分类 != 0) {
			if (category字典 != null) {
				mClew.分类 = category字典.getId();
			}
		}

		String totalStr = et_total_add_xiansuo.getText().toString();
		if (!TextUtils.isEmpty(totalStr)) {
			try {
				mClew.预计金额 = Integer.parseInt(totalStr);
			} catch (Exception e) {
				LogUtils.e(TAG, "预计金额：" + e);
			}
		}

		if (TextUtils.isEmpty(clientName)) {
			showShortToast("请填写公司名称");
			return false;
		} else {
			mClew.公司名称 = clientName;
		}

		if (TextUtils.isEmpty(contact)) {
			showShortToast("请填写联系人");
			return false;
		} else {
			mClew.联系人 = contact;
		}

		mClew.预计采购时间 = et_timecaigou_add_xiansuo.getText().toString();
		mClew.联系电话 = et_phone_add_xiansuo.getText().toString();

		mClew.描述 = et_info_add_xiansuo.getText().toString();
		mClew.关键字 = et_keyword_add_xiansuo.getText().toString();
		mClew.微信 = et_wechart_add_xiansuo.getText().toString();
		mClew.QQ = et_qq_add_xiansuo.getText().toString();
		mClew.旺旺 = et_wangwang_add_xiansuo.getText().toString();
		mClew.邮箱 = et_email_add_xiansuo.getText().toString();
		mClew.地址 = et_address_add_xiansuo.getText().toString();
		mClew.网址 = et_url_add_xiansuo.getText().toString();
		return true;
	}

	private void saveClew() {
		ProgressDialogHelper.show(mContext, "正在提交..", false);
		String url = Global.BASE_URL + "SaleSummary/saveClew";
		StringRequest.postAsyn(url, mClew, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败！");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功！");
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("服务器访问异常！");

			}
		});
	}
}
