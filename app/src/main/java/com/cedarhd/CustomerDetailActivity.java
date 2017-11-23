package com.cedarhd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.CustomerDetailListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.models.Client;

import java.util.ArrayList;

public class CustomerDetailActivity extends BaseActivity {

	// DataHelper mDataHelper = new DataHelper();
	// ListView mListViewFunction;
	CustomerDetailListViewAdapter mFunctionAdapter;
	ArrayList<String> mListFunctionName;
	Client mCustomer;

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

	private static final int CONTACT_HISTORY = 0;
	private static final int CREATE_CONTACT_HISTORY = 1;
	private static final int SALES_CHANCE = 2;
	private static final int NEW_SALES_CHANCE = 3;

	// private Handler mHanlder = new Handler();

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// MyApplication.getInstance().addActivity(this);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.customer_detail);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.title_customer_detail);

		Bundle bundle = this.getIntent().getExtras();
		mCustomer = (Client) bundle.getSerializable("Customer");

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

		// mClassificationNameValue.setText(mCustomer.get("ClassificationName").toString());
		mCustomerNameValue
				.setText(mCustomer.getCustomerName() != null ? mCustomer
						.getCustomerName() : "");
		mContactsValue.setText(mCustomer.getContacts() != null ? mCustomer
				.getContacts() : "");
		mTradeValue.setText(mCustomer.getTradeName() != null ? mCustomer
				.getTradeName() : "");
		mSalesmanValue.setText(mCustomer.getSalesmanName() != null ? mCustomer
				.getSalesmanName() : "");
		String date = mCustomer.getRegisterTime();
		mRegisterDateValue.setText(date != null ? date : "");
		// mLastContactDateVale.setText(mCustomer.get("LastContactTime").toString());
		// mPlanContactDateValue.setText(mCustomer.get("PlanContactTime").toString());
		// mAttachmentValue.setText(mCustomer.get("Attachment").toString());
		// mToContactValue.setText(mCustomer.get("ContactState").toString());
		mProvinceValue.setText(mCustomer.get省名() != null ? mCustomer.get省名()
				: "");
		mCityValue.setText(mCustomer.get市名() != null ? mCustomer.get市名() : "");
		mPhoneValue.setText(mCustomer.getPhone() != null ? mCustomer.getPhone()
				: "");
		mAddressValue.setText(mCustomer.getAddress() != null ? mCustomer
				.getAddress() : "");

		// mListFunctionName = new ArrayList<String>();
		// mListFunctionName.add(CONTACT_HISTORY,"联系拜访记录");
		// mListFunctionName.add(CREATE_CONTACT_HISTORY, "新建联系拜访记录");
		// mListFunctionName.add(SALES_CHANCE,"销售机会");
		// mListFunctionName.add(NEW_SALES_CHANCE,"新建销售机会");
		// mFunctionAdapter = new CustomerDetailListViewAdapter(this,
		// R.layout.customer_deatil_function, mListFunctionName);
		// mListViewFunction.setAdapter(mFunctionAdapter);
	}

	public void setOnClickListener() {
		// ImageView imageViewCancel = (ImageView)
		// findViewById(R.id.imageViewCancel);
		// imageViewCancel.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
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
		// Intent intentContactHistory = new Intent(CustomerActivity.this,
		// ContactHistoryListActivity.class);
		// Bundle bundleContactHistory = new Bundle();
		// bundleContactHistory.putString("CustomerId", mCustomer.getId() + "");
		// intentContactHistory.putExtras(bundleContactHistory);
		// startActivity(intentContactHistory);
		// break;
		// case CREATE_CONTACT_HISTORY:
		// break;
		// case SALES_CHANCE:
		// Intent intent = new Intent(CustomerActivity.this,
		// SalesChanceListActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("CustomerName", mCustomer.getCustomerName());
		// intent.putExtras(bundle);
		//
		// startActivity(intent);
		// break;
		// case NEW_SALES_CHANCE:
		// LayoutInflater inflater = LayoutInflater.from(CustomerActivity.this);
		// final View view = inflater.inflate(R.layout.new_sales_chance_dialog,
		// null);
		// final EditText editTextContent =
		// (EditText)view.findViewById(R.id.editTextDialogContent);
		//
		// new AlertDialog.Builder(CustomerActivity.this)
		// .setTitle("请输入销售机会内容：")
		// .setIcon(android.R.drawable.ic_dialog_info)
		// .setView(view)
		// .setPositiveButton("确定", new DialogInterface.OnClickListener(){
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// final String editTextContentValue =
		// editTextContent.getText().toString();
		// if (!editTextContentValue.equals("")) {
		//
		// new Thread(new Runnable(){
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// DataHelper dataHelper = new DataHelper();
		//
		// boolean returnFlag =
		// dataHelper.NewSalesChance(mCustomerNameValue.getText().toString(),
		// mTradeValue.getText().toString(),
		// mAddressValue.getText().toString(),
		// mProvinceValue.getText().toString(),
		// mCityValue.getText().toString(),
		// mPhoneValue.getText().toString(),
		// editTextContentValue);
		// if (returnFlag) {
		// handler.sendEmptyMessage(1);
		// } else {
		// handler.sendEmptyMessage(0);
		// }
		// }
		//
		// }).start();
		//
		// } else {
		// Toast.makeText(CustomerActivity.this, "内容不能为空！",
		// Toast.LENGTH_SHORT).show();
		// }
		//
		//
		// dialog.dismiss();
		// }
		//
		// })
		// .setNegativeButton("取消", new DialogInterface.OnClickListener(){
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// dialog.dismiss();
		// }
		//
		// })
		// .show();
		// break;
		// default:
		// break;
		// }
		// }
		// });
	}

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// // 这里重写返回键
	// Intent intent = new Intent(CustomerActivity.this,
	// CustomerListActivity.class);
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
				Toast.makeText(CustomerDetailActivity.this, "新建销售机会失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(CustomerDetailActivity.this, "新建销售机会成功！",
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

		// mListViewFunction = (ListView)findViewById(R.id.listViewFunction);
	}
}