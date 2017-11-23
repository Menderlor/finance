package com.cedarhd.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.cedarhd.ClientListActivity;
import com.cedarhd.R;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.crm.销售机会Crm;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/***
 * 新建销售机会(新建老客户)
 * 
 * @author K
 * 
 */
public class CRMAddSaleChanceFragment extends Fragment {

	private int clientId;
	private 销售机会Crm mSaleChance;
	private DictionaryHelper dictionaryHelper;
	private DictionaryQueryDialogHelper dictionaryQueryDialogHelper;

	private EditText etClient;
	private EditText etProduct;
	private EditText etPrice;
	private EditText etContent;

	/** 依附于activity点击保存事件 */
	private ImageView ivSave_parent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dictionaryHelper = new DictionaryHelper(getActivity());
		dictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(getActivity());
		mSaleChance = new 销售机会Crm();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_sale_chance, null);
		initViews(view);
		setOnEvent();
		return view;
	}

	private void initViews(View view) {
		etClient = (EditText) view.findViewById(R.id.et_client_add_salechance);
		etProduct = (EditText) view
				.findViewById(R.id.et_product_add_salechance);
		etPrice = (EditText) view.findViewById(R.id.et_price_add_salechance);
		etContent = (EditText) view
				.findViewById(R.id.et_content_add_salechance);
		ivSave_parent = (ImageView) getActivity().findViewById(
				R.id.iv_save_add_salechance);
	}

	private void setOnEvent() {
		etClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientBiz.selectClient(CRMAddSaleChanceFragment.this);
				// Intent intent = new Intent(getActivity(),
				// ClientListActivity.class);
				// intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
				// startActivityForResult(intent, 10);
			}
		});

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

		/** 保存销售机会 */
		ivSave_parent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isChecked()) {
					InputSoftHelper.hiddenSoftInput(getActivity(), etContent);
					ProgressDialogHelper.show(getActivity(), "保存中..");
					saveSaleChance();
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("RESULT", "RESULT" + requestCode);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ClientBiz.SELECT_CLIENT_CODE:
				Bundle bundle = data.getExtras();
				clientId = bundle.getInt(ClientListActivity.ClientId);
				LogUtils.i("kjxi", "clientId:" + clientId);
				if (clientId != 0) {
					etClient.setText(dictionaryHelper
							.getClientNameById(clientId));
				}
				break;
			}
		}
	}

	/** 空值校验 */
	private boolean isChecked() {
		if (clientId == 0) {
			Toast.makeText(getActivity(), "请选择客户", 0).show();
			return false;
		} else {
			mSaleChance.客户 = clientId;
		}

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

		mSaleChance.内容 = etContent.getText().toString();
		return true;
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
				Toast.makeText(getActivity(), "保存成功", 0).show();
				getActivity().finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				Toast.makeText(getActivity(), "保存失败", 0).show();
			}
		});
	}
}
