package com.cedarhd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.helpers.CityPicker;
import com.cedarhd.helpers.CityPicker.OnCheckedListener;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Dict;
import com.cedarhd.models.SelectedProvince;
import com.cedarhd.models.crm.销售机会Crm;
import com.cedarhd.models.字典;
import com.cedarhd.models.客户;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/***
 * 新建客户
 * 
 * @author K
 * 
 */
public class CRMAddNewClientFragment extends Fragment {

	/** 依附于activity点击保存事件 */
	private ImageView ivSave_parent;

	/** 客户信息 */
	private EditText etName;
	private EditText etContacts;
	private EditText etKeyWord;
	private EditText etPhone;
	private EditText etIndustry;
	private EditText etWechat;
	private EditText etQQ;
	private EditText etWangwang;
	private EditText etEmail;
	private EditText etUrl;

	/** 销售机会的信息 */
	private EditText etProduct;
	private EditText etPrice;
	private EditText etContent;

	private LinearLayout llSlectCity;
	private TextView tvProvince;
	private TextView tvCity;
	private TextView tvCoutry;

	private 客户 mClient;
	private 销售机会Crm mSaleChance;
	private SelectedProvince mSelectedProvince;
	private DictionaryQueryDialogHelper dictionaryQueryDialogHelper;
	private CityPicker mCityPicker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_new_client, null);
		initViews(view);

		setOnEvent();
		return view;
	}

	private void initData() {
		dictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(getActivity());
		mClient = new 客户();
		mSaleChance = new 销售机会Crm();

		mCityPicker = CityPicker.getInstance(getActivity());
	}

	private void initViews(View view) {
		ivSave_parent = (ImageView) getActivity().findViewById(
				R.id.iv_save_add_salechance);
		etName = (EditText) view.findViewById(R.id.et_name_add_newclient);
		etContacts = (EditText) view
				.findViewById(R.id.et_contact_add_newclient);
		etKeyWord = (EditText) view.findViewById(R.id.et_keyword_add_newclient);
		etPhone = (EditText) view.findViewById(R.id.et_phone_add_newclient);
		etIndustry = (EditText) view
				.findViewById(R.id.et_industry_add_newclient);
		etWechat = (EditText) view.findViewById(R.id.et_wechart_add_newclient);
		etQQ = (EditText) view.findViewById(R.id.et_qq_add_newclient);
		etWangwang = (EditText) view
				.findViewById(R.id.et_wangwang_add_newclient);
		etEmail = (EditText) view.findViewById(R.id.et_email_add_newclient);
		etUrl = (EditText) view.findViewById(R.id.et_url_add_newclient);

		/** 销售机会信息，保存客户成功后，获取客户编号，保存销售机会 */
		etProduct = (EditText) view.findViewById(R.id.et_product_add_newclient);
		etPrice = (EditText) view.findViewById(R.id.et_price_add_newclient);
		etContent = (EditText) view.findViewById(R.id.et_content_add_newclient);

		llSlectCity = (LinearLayout) view
				.findViewById(R.id.ll_select_city_add_newclient);
		tvProvince = (TextView) view
				.findViewById(R.id.tv_province_add_newclient);
		tvCity = (TextView) view.findViewById(R.id.tv_city_add_newclient);
		tvCoutry = (TextView) view.findViewById(R.id.tv_country_add_newclient);
	}

	private void setOnEvent() {
		etProduct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictionaryQueryDialogHelper.show(etProduct, "销售机会_意向产品");
				dictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								etProduct.setText(dict.getName());
								etProduct.setTag(dict);
							}
						});
			}
		});

		etIndustry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dictionaryQueryDialogHelper.show(etProduct, "行业");
				dictionaryQueryDialogHelper
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(字典 dict) {
								etIndustry.setText(dict.getName());
								etIndustry.setTag(dict);
							}
						});
			}
		});

		/** 保存销售机会 */
		ivSave_parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecked()) {
					InputSoftHelper.hiddenSoftInput(getActivity(), etContent);
					ProgressDialogHelper.show(getActivity(), "保存中..");
					setClientValue();
					setSaleChanceValue();
					saveClient();
				}
			}
		});

		llSlectCity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCityPicker.show();
				mCityPicker.setOnCheckedListener(new OnCheckedListener() {
					@Override
					public void onChecked(SelectedProvince selectedCity) {
						mSelectedProvince = selectedCity;

						tvProvince.setText(selectedCity.省.名称);
						tvCity.setText(selectedCity.市.名称);
						tvCoutry.setText(selectedCity.县.名称);
					}
				});
			}
		});
	}

	/** 空值校验 */
	private boolean isChecked() {
		String clientName = etName.getText().toString();
		if (TextUtils.isEmpty(clientName)) {
			Toast.makeText(getActivity(), "请填写客户名称", 0).show();
			return false;
		} else {
			mClient.名称 = clientName;
		}

		String contacts = etContacts.getText().toString();
		if (TextUtils.isEmpty(clientName)) {
			Toast.makeText(getActivity(), "请填写联系人姓名", 0).show();
			return false;
		} else {
			mClient.联系人 = contacts;
		}

		/** 销售机会部分校验 */
		字典 dict = (字典) etProduct.getTag();
		if (dict == null) {
			Toast.makeText(getActivity(), "请选择意向产品", 0).show();
			return false;
		} else {
			mSaleChance.意向产品 = dict.getId();
		}

		String priceStr = etPrice.getText().toString();
		if (TextUtils.isEmpty(priceStr)) {
			Toast.makeText(getActivity(), "请填写报价", 0).show();
			return false;
		} else {
			try {
				double prices = Double.parseDouble(priceStr);
				mSaleChance.报价 = prices;
			} catch (Exception e) {
				Toast.makeText(getActivity(), "", 0).show();
				return false;
			}
		}

		if (mSelectedProvince != null) {
			mSaleChance.省 = mSelectedProvince.省.编号;
			mSaleChance.市 = mSelectedProvince.市.编号;
			mSaleChance.县 = mSelectedProvince.县.编号;
		}
		return true;
	}

	/***
	 * 为client设置 editText填写的值
	 */
	private void setClientValue() {
		mClient.关键字 = etKeyWord.getText().toString();
		mClient.电话 = etPhone.getText().toString();
		// mClient.行业=etIndustry.getText().toString();
		mClient.微信 = etWechat.getText().toString();
		mClient.QQ = etQQ.getText().toString();
		mClient.旺旺 = etWangwang.getText().toString();
		mClient.邮箱 = etEmail.getText().toString();
		mClient.网址 = etUrl.getText().toString();

		// 行业
		Dict industryDict = (Dict) etIndustry.getTag();
		if (industryDict != null) {
			try {
				int industry = industryDict.编号;
				mClient.行业 = industry;
			} catch (Exception e) {
				LogUtils.e("erro", e + "");
			}
		}
	}

	/***
	 * 为销售机会设置 editText填写的值
	 */
	private void setSaleChanceValue() {
		mSaleChance.内容 = etContent.getText().toString();
	}

	private void saveSaleChance() {
		String url = Global.BASE_URL + "SaleSummary/saveSaleChance";
		StringRequest.postAsyn(url, mSaleChance, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "保存失败", 0).show();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "销售机会保存成功", 0).show();
				getActivity().finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "保存失败", 0).show();
			}
		});
	}

	/**
	 * 保存客户到服务器
	 */
	private void saveClient() {
		String url = Global.BASE_URL + "SaleSummary/saveCustomer";
		StringRequest.postAsyn(url, mClient, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "新客户保存失败", 0).show();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "新客户保存成功", 0).show();
				LogUtils.e("response", response);
				// getActivity().finish();

				try {
					String clientStr = JsonUtils.getStringValue(response,
							"Data");
					LogUtils.i("clientId", "clientId=" + clientStr);
					clientStr = StrUtils.removeRex(clientStr);
					mSaleChance.客户 = Integer.parseInt(clientStr);
					saveSaleChance();
				} catch (Exception e) {
					LogUtils.e("ERRO", "" + e);
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "新客户保存失败", 0).show();
			}
		});
	}

}
