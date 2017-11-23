package com.cedarhd.crm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.BDLocationHelper;
import com.cedarhd.helpers.server.BDLocationHelper.OnReceivedLocationListerner;
import com.cedarhd.models.Client;
import com.cedarhd.models.crm.客户联系记录Crm;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/***
 * 销售统计版 新建客户联系记录
 * 
 * @author K 2015/09/23 14:26
 */
public class CrmAddClientContactsActivity extends BaseActivity {

	private 客户联系记录Crm mContact;
	private Context mContext;
	private DateAndTimePicker mTimePicker;
	private BDLocationHelper mBdLocationHelper;

	private ImageView ivBack;
	private ImageView ivSave;
	private EditText etContent;
	private EditText etOpinion;
	private EditText etClient;
	private EditText etSaleChance;
	private TextView tvAddress;
	private BoeryunDateSelectView dateContactTime;
	private BoeryunDateSelectView datePlanTime;
	private BoeryunDictSelectView selectDictType;
	private BoeryunDictSelectView selectDictStage;
	private BoeryunDictSelectView selectDictIntention;
	private DictionaryQueryDialogHelper mDictionaryQueryDialogHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_client_contacts_crm);
		initData();
		initViews();
		setOnEvent();
	}

	private void initData() {
		mContext = this;
		mContact = new 客户联系记录Crm();
		mTimePicker = new DateAndTimePicker(mContext);
		mDictionaryQueryDialogHelper = DictionaryQueryDialogHelper
				.getInstance(mContext);
		mBdLocationHelper = new BDLocationHelper(mContext);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("RESULT", "RESULT" + requestCode);
		if (resultCode == Activity.RESULT_OK) {
			Client client = ClientBiz.onActivityGetClient(mContext,
					requestCode, data);
			if (client != null) {
				etClient.setText(client.getCustomerName());
				etClient.setTag(client.getId());
			}
		}
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_add_contacts);
		ivSave = (ImageView) findViewById(R.id.iv_save_add_contacts);
		etContent = (EditText) findViewById(R.id.et_content_add_contacts);
		etOpinion = (EditText) findViewById(R.id.et_oppinion_add_contacts);
		etClient = (EditText) findViewById(R.id.et_client_add_contacts);
		etSaleChance = (EditText) findViewById(R.id.et_salechance_add_contacts);
		tvAddress = (TextView) findViewById(R.id.tv_address_add_contacts);
		dateContactTime = (BoeryunDateSelectView) findViewById(R.id.date_contact_time_add_contacts);
		datePlanTime = (BoeryunDateSelectView) findViewById(R.id.date_plan_time_add_contacts);
		selectDictStage = (BoeryunDictSelectView) findViewById(R.id.dict_stage_add_contacts);
		selectDictType = (BoeryunDictSelectView) findViewById(R.id.dict_type_add_contacts);
		selectDictIntention = (BoeryunDictSelectView) findViewById(R.id.dict_intention_add_contacts);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				// startActivity(new Intent(mContext,
				// SelectCityActivity.class));
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheckNAN()) {
					saveContacts();
				}
			}
		});

		etClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClientBiz.selectClient(mContext);
			}
		});

		dateContactTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTimePicker.showDateWheel(dateContactTime);
			}
		});

		etSaleChance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (etClient.getTag() == null) {
					showShortToast("请先选择客户");
				} else {
					try {
						int clientId = Integer.parseInt(etClient.getTag()
								.toString());
						mDictionaryQueryDialogHelper.show("销售机会", "内容", "客户="
								+ clientId);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						showShortToast("非法客户");
					}
				}

			}
		});

		mBdLocationHelper
				.setOnReceivedLocationListener(new OnReceivedLocationListerner() {
					@Override
					public void onReceived(String mLoc, double mLong,
							double mLati) {
						tvAddress.setText(mLoc + "");
						mContact.地址 = mLoc;
						mContact.经度 = mLong;
						mContact.纬度 = mLati;
					}
				});
	}

	/***
	 * 提交前的非空校验
	 * 
	 * @return
	 */
	private boolean isCheckNAN() {
		String content = etContent.getText().toString();
		String contactTime = dateContactTime.getText().toString();

		if (TextUtils.isEmpty(content)) {
			Toast.makeText(mContext, "请填写联系内容", 0).show();
			return false;
		}

		if (TextUtils.isEmpty(contactTime)) {
			Toast.makeText(mContext, "请选择联系时间", 0).show();
			return false;
		}

		mContact.内容 = content;
		mContact.时间 = contactTime;
		mContact.客户反馈 = etOpinion.getText().toString();

		if (etClient.getTag() != null) {
			mContact.客户 = (Integer) etClient.getTag();
		}

		if (etSaleChance.getTag() != null) {
			mContact.销售机会 = (Integer) etSaleChance.getTag();
		}

		if (selectDictStage.getTag() != null) {
			字典 dict = (字典) selectDictStage.getTag();
			mContact.联系状态 = dict.getId();
		}

		if (selectDictType.getTag() != null) {
			字典 dict = (字典) selectDictType.getTag();
			mContact.联系形式 = dict.getId();
		}

		if (selectDictIntention.getTag() != null) {
			字典 dict = (字典) selectDictIntention.getTag();
			mContact.意向程度 = dict.getId();
		}

		return true;
	}

	/***
	 * 保存到服务器
	 */
	private void saveContacts() {
		String url = Global.BASE_URL + "SaleSummary/saveContactRecord";
		StringRequest.postAsyn(url, mContact, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				// 保存失败
				Toast.makeText(mContext, "保存失败", 0).show();
			}

			@Override
			public void onResponse(String response) {
				// 保存成功
				Toast.makeText(mContext, "保存成功", 0).show();
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				// 保存失败
				Toast.makeText(mContext, "保存失败", 0).show();
			}
		});
	}
}
