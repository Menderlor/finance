package com.cedarhd;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.server.ZLServiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Deprecated
public class CustomerNewActivity extends BaseActivity {

	EditText mEditTextCustomerName;
	EditText mEditTextContacts;
	Spinner mSpinnerTrade;
	EditText mEditTextAddress;
	Spinner mSpinnerProvince;
	Spinner mSpinnerCity;
	EditText mPhone;
	Button mSave;

	List<HashMap<String, Object>> mCustomerIndustry;
	List<HashMap<String, Object>> mProvince;
	List<HashMap<String, Object>> mCity;

	ArrayList<String> mArrayListIndustry;
	ArrayList<String> mArrayListProvince;
	ArrayList<String> mArrayListCity;

	ArrayAdapter arrayAdapterIndustry;
	ArrayAdapter arrayAdapterProvince;
	ArrayAdapter arrayAdapterCity;

	MessageHandler handler;

	int mSelectProvince;

	public static final int RESULT_CODE_RETURN_SUCCESS = 0;
	public static final int RESULT_CODE_RETURN_FAILED = 1;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.customer_new);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.title_customer_new);

		handler = new MessageHandler();

		findViews();
		setOnClickListener();

		getIndustry();

		mProvince = LoadProvince();
		mArrayListProvince = new ArrayList<String>();
		for (HashMap<String, Object> mPro : mProvince) {
			mArrayListProvince.add(mPro.get("名称").toString());
		}

		arrayAdapterProvince = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mArrayListProvince);
		arrayAdapterProvince
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerProvince.setAdapter(arrayAdapterProvince);
		mSelectProvince = 1;

		mCity = LoadCity();
		mArrayListCity = new ArrayList<String>();
		if (mCity != null) {
			for (HashMap<String, Object> mCi : mCity) {
				mArrayListCity.add(mCi.get("名称").toString());
			}
		}
		arrayAdapterCity = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, mArrayListCity);
		arrayAdapterCity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCity.setAdapter(arrayAdapterCity);

	}

	private void getIndustry() {
	}

	private List<HashMap<String, Object>> LoadProvince() {
		List<HashMap<String, Object>> listProvince = null;
		listProvince = null;//
		// sqlManager.getTableData("省");
		return listProvince;
	}

	private int getProvinceCode(int Id) {
		int code = 110000;
		Cursor cursor = null;
		// sqlManager.Mysqlite.query("省", new String[] { "代码" },
		// "编号 = ?", new String[] { "" + Id }, null, null, null);
		while (cursor.moveToNext()) {
			code = cursor.getInt(0);
		}
		cursor.close();
		return code;
	}

	private List<HashMap<String, Object>> LoadCity() {
		List<HashMap<String, Object>> listCity = new ArrayList<HashMap<String, Object>>();
		int codeProvince = getProvinceCode(mSelectProvince);
		Cursor cursor = null;
		// sqlManager.Mysqlite.query("市", new String[] { "编号",
		// "名称" }, " 市.省  = ?", new String[] { "" + codeProvince }, null,
		// null, null);
		while (cursor.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("编号", cursor.getInt(cursor.getColumnIndex("编号")));
			map.put("名称", cursor.getString(cursor.getColumnIndex("名称")));
			listCity.add(map);
		}
		cursor.close();
		// listCity = sqlManager.getTableData("市");
		return listCity;
	}

	private void setOnClickListener() {

		ImageView ImageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		ImageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mSpinnerProvince
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						// sqlManager.open();
						mSelectProvince = arg2 + 1;

						mCity = LoadCity();
						mArrayListCity.clear();
						for (HashMap<String, Object> mCi : mCity) {
							mArrayListCity.add(mCi.get("名称").toString());
						}
						arrayAdapterCity.notifyDataSetChanged();
						// sqlManager.close();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		// mSpinnerTrade.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// Log.e("guojianwen", "OnTouch GetPosition" +
		// mSpinnerTrade.getSelectedItemPosition());
		//
		// return false;
		// }
		// });

		mSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String mCustomerNameValue = mEditTextCustomerName
						.getText().toString();
				final String mEditTextContactsValue = mEditTextContacts
						.getText().toString();
				final String mCustomerIndustryValue = mArrayListIndustry
						.get(mSpinnerTrade.getSelectedItemPosition());
				final String mCustomerAddressValue = mEditTextAddress.getText()
						.toString();
				final String mCustomerProvinceValue = mArrayListProvince
						.get(mSpinnerProvince.getSelectedItemPosition());
				final String mCustomerCityValue = mArrayListCity
						.get(mSpinnerCity.getSelectedItemPosition());
				final String mCustomerPhoneValue = mPhone.getText().toString();

				// SimpleDateFormat format = new
				// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				// String mRegisterDate = format.format(new
				// Date(System.currentTimeMillis()));
				// long mRegisterDate = System.currentTimeMillis();
				if (mCustomerNameValue.equals("")) {
					Toast.makeText(CustomerNewActivity.this, "客户名不能为空！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (mEditTextContactsValue.equals("")) {
					Toast.makeText(CustomerNewActivity.this, "联系人不能为空！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				final ZLServiceHelper dataHelper = new ZLServiceHelper();
				if (dataHelper.isCustomerExist(mCustomerNameValue)) {
					Toast.makeText(CustomerNewActivity.this, "客户已经存在！",
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							boolean returnFlag = dataHelper.NewCustomer(
									mCustomerNameValue, mEditTextContactsValue,
									mCustomerIndustryValue,
									mCustomerAddressValue,
									mCustomerProvinceValue, mCustomerCityValue,
									mCustomerPhoneValue);
							if (returnFlag) {
								handler.sendEmptyMessage(handler.CREATE_NEW_CUSTOMER_SUCCESS);
							} else {
								handler.sendEmptyMessage(handler.CREATE_NEW_CUSTOMER_FAILED);
							}
						} catch (Exception e) {
							Toast.makeText(CustomerNewActivity.this, "新建客户异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();

			}

		});
	}

	private void findViews() {
		mEditTextCustomerName = (EditText) findViewById(R.id.editTextCustomerName);
		mEditTextContacts = (EditText) findViewById(R.id.editTextContacts);
		mSpinnerTrade = (Spinner) findViewById(R.id.spinnerTrade);
		mEditTextAddress = (EditText) findViewById(R.id.editTextAddress);
		mSpinnerProvince = (Spinner) findViewById(R.id.spinnerProvince);
		mSpinnerCity = (Spinner) findViewById(R.id.spinnerCity);
		mPhone = (EditText) findViewById(R.id.editTextPhone);
		mSave = (Button) findViewById(R.id.buttonSave);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_CODE_RETURN_FAILED);
		super.onBackPressed();
	}

	class MessageHandler extends Handler {

		public static final int CREATE_NEW_CUSTOMER_SUCCESS = 0;
		public static final int CREATE_NEW_CUSTOMER_FAILED = 1;
		public static final int GET_INDUSTRY_SUCCESS = 2;
		public static final int GET_INDUSTRY_FAILED = 3;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case CREATE_NEW_CUSTOMER_FAILED:
				Toast.makeText(CustomerNewActivity.this, "新建客户失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case CREATE_NEW_CUSTOMER_SUCCESS:
				Toast.makeText(CustomerNewActivity.this, "新建客户成功！",
						Toast.LENGTH_SHORT).show();
				setResult(RESULT_CODE_RETURN_SUCCESS);
				finish();
				break;
			case GET_INDUSTRY_SUCCESS:
				mCustomerIndustry = (List<HashMap<String, Object>>) msg.obj;
				mArrayListIndustry = new ArrayList<String>();
				for (HashMap<String, Object> mIndustry : mCustomerIndustry) {
					mArrayListIndustry
							.add(mIndustry.get("Industry").toString());
				}

				arrayAdapterIndustry = new ArrayAdapter(
						CustomerNewActivity.this,
						android.R.layout.simple_spinner_item,
						mArrayListIndustry);
				arrayAdapterIndustry
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mSpinnerTrade.setAdapter(arrayAdapterIndustry);
				break;
			case GET_INDUSTRY_FAILED:
				Toast.makeText(CustomerNewActivity.this, "获取行业信息失败！",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}

	};
}
