package com.cedarhd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.SalesChanceDetailListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.销售机会;
import com.cedarhd.utils.DateTimeUtil;

import java.util.ArrayList;

public class SalesChanceDetailActivity extends BaseActivity {

	// DataHelper mDataHelper = new DataHelper();
	// ListView mListViewFunction;
	SalesChanceDetailListViewAdapter mFunctionAdapter;
	ArrayList<String> mListFunctionName;
	销售机会 mSalesChance;

	// TextView mClassificationNameValue;
	TextView mCustomerNameValue;
	TextView mContactsValue;
	TextView mTradeValue;
	TextView mSalesmanValue;
	TextView mRegisterDateValue;
	// TextView mLastContactDateVale;
	// TextView mPlanContactDateValue;
	// TextView mAttachmentValue;
	// TextView mToContactValue;
	TextView mProvinceValue;
	TextView mCityValue;
	TextView mPhoneValue;
	TextView mAddressValue;
	TextView mContentValue;

	private static final int CONTACT_HISTORY = 0;
	private static final int CREATE_CONTACT_HISTORY = 1;
	private static final int CREATE_CUSTOMER = 2;

	// private Handler mHanlder = new Handler();

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// / MyApplication.getInstance().addActivity(this);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.sales_chance_detail);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_sales_chance_detail);

		Bundle bundle = this.getIntent().getExtras();
		mSalesChance = (销售机会) bundle.getSerializable("SalesChance");

		findViews();
		setOnClickListener();
		Init();

		// mHanlder.post(new Runnable() {
		// public void run() {
		// // 判断是否已读
		// if (!mNotice.get("Read").toString()
		// .contains("'" + Global.mUser.Id + "'")) {
		// mDataHelper.ReadNotice(mNotice.get("Id").toString());
		// }
		// }
		// });
	}

	void Init() {
		// mTextViewTitle.setText(mNotice.get("Title").toString());
		//
		// mTextViewTime.setText(mNotice.get("ReleaseTime").toString());
		//
		// mTextViewPublisherName.setText("发送人:" +
		// mNotice.get("PublisherName").toString());
		// mTextViewReceiverName.setText("接收人:" +
		// mNotice.get("PersonnelName").toString());
		// mTextViewContent.setText(mNotice.get("Content").toString());
		// mFunctionAdapter = new ArrayAdapter<String>(this,
		// R.layout.customer_deatil_function);
		// mFunctionAdapter.add("联系拜访记录");
		// mFunctionAdapter.add("销售机会");

		// mClassificationNameValue.setText(mSalesChance.get("ClassificationName").toString());
		mCustomerNameValue
				.setText(mSalesChance.getCustomerName() != null ? mSalesChance
						.getCustomerName() : "");
		mContactsValue
				.setText(mSalesChance.getContacts() != null ? mSalesChance
						.getContacts() : "");
		mTradeValue.setText(mSalesChance.getTradeName() != null ? mSalesChance
				.getTradeName() : "");
		mSalesmanValue
				.setText(mSalesChance.getSalesmanName() != null ? mSalesChance
						.getSalesmanName() : "");

		mRegisterDateValue
				.setText(DateTimeUtil.ConvertDateToString(mSalesChance
						.getRegisterTime() != null ? mSalesChance
						.getRegisterTime().toString() : ""));
		// mLastContactDateVale.setText(mSalesChance.get("LastContactTime").toString());
		// mPlanContactDateValue.setText(mSalesChance.get("PlanContactTime").toString());
		// mAttachmentValue.setText(mSalesChance.get("Attachment").toString());
		// mToContactValue.setText(mSalesChance.get("ContactState").toString());
		mProvinceValue
				.setText(mSalesChance.getProvinceName() != null ? mSalesChance
						.getProvinceName() : "");
		mCityValue.setText(mSalesChance.getCityName() != null ? mSalesChance
				.getCityName() : "");
		mContentValue.setText(mSalesChance.getContent() != null ? mSalesChance
				.getContent() : "");
		mPhoneValue.setText(mSalesChance.getPhone() != null ? mSalesChance
				.getPhone() : "");
		mAddressValue.setText(mSalesChance.getAddress() != null ? mSalesChance
				.getAddress() : "");

		// mListFunctionName = new ArrayList<String>();
		// mListFunctionName.add(CONTACT_HISTORY, "联系拜访记录");
		// mListFunctionName.add(CREATE_CONTACT_HISTORY, "新建联系拜访记录");
		// if (!isCustomerExists(mSalesChance.getCustomerName())) {
		// mListFunctionName.add(CREATE_CUSTOMER, "生成客户");
		// }
		//
		// mFunctionAdapter = new SalesChanceDetailListViewAdapter(this,
		// R.layout.sales_chance_deatil_function, mListFunctionName);
		// mListViewFunction.setAdapter(mFunctionAdapter);
	}

	private boolean isCustomerExists(String customerName) {
		if (customerName != null) {
			return false;
		}
		boolean exists = false;
		ZLServiceHelper dataHelper = new ZLServiceHelper();
		exists = dataHelper.isCustomerExist(customerName);
		return exists;
	}

	public void setOnClickListener() {
		// ImageView imageViewCancel = (ImageView)
		// findViewById(R.id.imageViewCancel);
		// imageViewCancel.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Intent intent = new Intent(SalesChanceActivity.this,
		// // SalesChanceListActivity.class);
		// // startActivity(intent);
		// finish();
		// }
		// });

		// ImageView imageViewDelete = (ImageView)
		// findViewById(R.id.imageViewDelete);
		// imageViewDelete.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
		// mListViewFunction.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// // TODO Auto-generated method stub
		// switch (arg2) {
		// case CONTACT_HISTORY:
		// Intent intentContactHistory = new Intent(SalesChanceActivity.this,
		// ContactHistoryListActivity.class);
		// Bundle bundleContactHistory = new Bundle();
		// bundleContactHistory.putString("SalesChanceId", mSalesChance.getId()
		// + "");
		// intentContactHistory.putExtras(bundleContactHistory);
		// startActivity(intentContactHistory);
		// break;
		// case CREATE_CONTACT_HISTORY:
		// break;
		// case CREATE_CUSTOMER:
		// new Thread(new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// DataHelper dataHelper = new DataHelper();
		// boolean returnFlag =
		// dataHelper.NewCustomer(mCustomerNameValue.getText().toString(),
		// mTradeValue.getText().toString(),
		// mAddressValue.getText().toString(),
		// mProvinceValue.getText().toString(),
		// mCityValue.getText().toString(),
		// mPhoneValue.getText().toString());
		// if (returnFlag) {
		// handler.sendEmptyMessage(1);
		// } else {
		// handler.sendEmptyMessage(0);
		// }
		// }
		//
		// }).start();
		//
		// break;
		// }
		// }
		// });
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // 这里重写返回键
	// Intent intent = new Intent(SalesChanceActivity.this,
	// SalesChanceListActivity.class);
	// startActivity(intent);
	// return true;
	// }
	// return false;
	// }

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(SalesChanceDetailActivity.this, "新建客户失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(SalesChanceDetailActivity.this, "新建客户成功！",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			finish();
		}

	};

	public void findViews() {
		// mClassificationNameValue =
		// (TextView)findViewById(R.id.textViewClassificationNameValue);
		mCustomerNameValue = (TextView) findViewById(R.id.textViewCustomerNameValue);
		mContactsValue = (TextView) findViewById(R.id.textViewContactsValue);
		mTradeValue = (TextView) findViewById(R.id.textViewTradeValue);
		mSalesmanValue = (TextView) findViewById(R.id.textViewSalemanValue);
		mRegisterDateValue = (TextView) findViewById(R.id.textViewRegisterDateValue);
		// mLastContactDateVale =
		// (TextView)findViewById(R.id.textViewLastContactDateValue);
		// mPlanContactDateValue =
		// (TextView)findViewById(R.id.textViewPlanContactDateValue);
		// mAttachmentValue =
		// (TextView)findViewById(R.id.textViewAttachmentValue);
		// mToContactValue =
		// (TextView)findViewById(R.id.textViewToContactValue);
		mProvinceValue = (TextView) findViewById(R.id.textViewProvinceValue);
		mCityValue = (TextView) findViewById(R.id.textViewCityValue);
		mPhoneValue = (TextView) findViewById(R.id.textViewPhoneValue);
		mAddressValue = (TextView) findViewById(R.id.textViewAddressValue);
		mContentValue = (TextView) findViewById(R.id.textViewContentValue);

		// mListViewFunction = (ListView)findViewById(R.id.listViewFunction);
	}
}