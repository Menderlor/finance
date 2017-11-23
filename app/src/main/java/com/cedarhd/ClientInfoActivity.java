package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.utils.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 客户详情（可编辑）/新建客户
 *
 * @author k
 *
 */
@Deprecated
public class ClientInfoActivity extends BaseActivity {
	public static final String TAG = "ClientInfoActivity";
	private boolean isNew; // 是否新建
	private DictionaryHelper dictionaryHelper;
	private Client item;
	private Context context;
	ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	DateAndTimePicker dateAndTimePicker;

	private String mUserSelectId = ""; // 业务员id
	private String mUserSelectName = "";

	private boolean hasMessured; // 計算高度
	private EditText etName;
	private EditText etContactName;
	private EditText etRegisterTime;
	private EditText etSaleMan;
	private EditText etPhone;
	private EditText etAdress;
	private EditText etLastContact; // 最后联系时间

	public static final int SUCCESS_UPDATE = 101;
	public static final int FAILURE_UPDATE = 102;
	public static final int SUCCESS_NEW = 103;
	public static final int FAILURE_NEW = 104;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
				case SUCCESS_UPDATE:
					Toast.makeText(context, "修改客户信息成功", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case FAILURE_UPDATE:
					Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
					break;
				case SUCCESS_NEW:
					Toast.makeText(context, "新建客户成功", Toast.LENGTH_SHORT).show();
					ClientListActivity.isResume = true;
					finish();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_info_activity_new);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		findviews();
		if (getIntent() != null) {
			isNew = getIntent().getBooleanExtra(ClientInfoActivity.TAG, false);
			if (!isNew) {
				item = (Client) getIntent().getExtras().getSerializable(TAG);
				init();
			} else {
				findViewById(R.id.ll_bottom_list_client_info).setVisibility(
						View.GONE);
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 0) {
			// 取出字符串
			Bundle bundle = data.getExtras();
			mUserSelectId = bundle.getString("UserSelectId");
			LogUtils.i("keno110", mUserSelectId);
			mUserSelectName = bundle.getString("UserSelectName");
			etSaleMan.setText(mUserSelectName);
		}
	}

	private void findviews() {
		context = ClientInfoActivity.this;
		dictionaryHelper = new DictionaryHelper(context);
		dateAndTimePicker = new DateAndTimePicker(context);
		mUserSelectId = Global.mUser.Id;
		etName = (EditText) findViewById(R.id.et_name_clientInfo);
		etContactName = (EditText) findViewById(R.id.et_contanctName_clientInfo);
		etRegisterTime = (EditText) findViewById(R.id.et_clientInfo_registerTime);
		etSaleMan = (EditText) findViewById(R.id.et_clientInfo_saleman);
		etPhone = (EditText) findViewById(R.id.et_clientInfo_phone);
		etAdress = (EditText) findViewById(R.id.et_clientInfo_adress);
		etLastContact = (EditText) findViewById(R.id.et_clientInfo_lastContactDate);
		etSaleMan.setText(dictionaryHelper.getUserNameById(Global.mUser.Id));
		ImageView ivCancel = (ImageView) findViewById(R.id.imageViewCancel_clientInfo);
		ImageView ivDone = (ImageView) findViewById(R.id.imageViewDone_clientInfo);
		ivCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheck()) {
					// TODO 保存客户
					if (!TextUtils.isEmpty(mUserSelectId)
							&& mUserSelectId.contains(";")) {
						LogUtils.i("keno110", mUserSelectId);
						String str = mUserSelectId.split(";")[0];
						mUserSelectId = str.replace("'", "");
					}
					LogUtils.i("keno110", mUserSelectId);
					int salerId = Integer.parseInt(mUserSelectId);
					LogUtils.i("keno110", "int---->" + mUserSelectId);
					LogUtils.i("keno110", "salerId---->" + salerId);
					String contactsName = etContactName.getText().toString();
					String address = etAdress.getText().toString();
					String phone = etPhone.getText().toString();
					String lastContactDate = etLastContact.getText().toString();
					if (isNew) {
						item = new Client();
						item.setCustomerName(etName.getText().toString());
					}
					item.setContacts(contactsName);
					item.setSalesman(salerId);
					item.setAddress(address);
					item.setPhone(phone);
					item.setLastContactDate(lastContactDate);
					LogUtils.i("keno110",
							"getSalesman---->" + item.getSalesman());
					// item.setUpdateTime(ViewHelper.getDateString());
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								zlServiceHelper.updateCustomer(item, handler);
							} catch (Exception e) {
								Toast.makeText(context, "保存客户异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				}
			}
		});

		etSaleMan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectSaler();
			}
		});
		etSaleMan.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					selectSaler();
				}
			}
		});

		etLastContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dateAndTimePicker.showDateWheel(etLastContact);
			}
		});

		etLastContact.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					dateAndTimePicker.showDateWheel(etLastContact);
				}
			}
		});

	}

	private void init() {
		mUserSelectId = String.valueOf(item.getSalesman());
		String customerName = item.getCustomerName() == null ? "" : item
				.getCustomerName();
		etName.setText(customerName);
		String contactName = TextUtils.isEmpty(item.getContacts()) ? "" : item
				.getContacts();
		etContactName.setText(contactName);
		etRegisterTime.setText(formatDate(item.getRegisterTime()));
		etSaleMan.setText(dictionaryHelper.getUserNameById(item.getSalesman()));
		etPhone.setText(item.getPhone());
		etAdress.setText(item.getAddress());
		etLastContact.setText(formatDate(item.getLastContactDate()));
		etLastContact.setEnabled(false);
		// etUpdate.setText(formatDate(item.getUpdateTime()));

		// 计算客户名称高度重绘
		etName.setTag(false);
		caculateHeight(etName, item.getCustomerName());
		// 计算地址高度重绘
		etAdress.setTag(false);
		caculateHeight(etAdress, item.getAddress());

		LinearLayout llContactList = (LinearLayout) findViewById(R.id.iv_contact_list_client_info);
		LinearLayout llNewContact = (LinearLayout) findViewById(R.id.iv_contact_add_client_info);
		LinearLayout llWorkPlanList = (LinearLayout) findViewById(R.id.iv_workplan_list_client_info);
		LinearLayout llWorkPlanNew = (LinearLayout) findViewById(R.id.iv_workplan_new_client_info);
		LinearLayout llSaleChanceList = (LinearLayout) findViewById(R.id.iv_salechance_list_client_info);
		LinearLayout llSaleChanceNew = (LinearLayout) findViewById(R.id.iv_salechance_new_client_info);

		// 查看本客户的联系记录
		llContactList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						ClientConstactListActivity.class);
				// Bundle bundle = new Bundle();
				// bundle.putSerializable(ClientConstactListActivity.TAG, item);
				// intent.putExtras(bundle);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 新建联系记录
		llNewContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						ClientConstactNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		llWorkPlanList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						TaskListActivityNew.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// 投诉建议列表
		llWorkPlanList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						SuggestListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// llWorkPlanNew.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(ClientInfoActivity.this,
		// TaskNewActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putInt("ClientInfoActivity_clientId", item.getId());
		// intent.putExtras(bundle);
		// startActivity(intent);
		// }
		// });
		// 新建投诉建议
		llWorkPlanNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						SuggestNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		/**
		 * 查看销售机会列表
		 */
		llSaleChanceList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						SaleChanceListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		/**
		 * 新建销售机会
		 */
		llSaleChanceNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClientInfoActivity.this,
						SaleChanceInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("ClientInfoActivity_clientId", item.getId());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	// private void selectSaler() {
	// Intent intent = new Intent(ClientInfoActivity.this,
	// User_SelectActivity.class);
	// Bundle bundle = new Bundle();
	// bundle.putString("UserSelectId", mUserSelectId);
	// intent.putExtras(bundle);
	// startActivityForResult(intent, 0);
	// }

	/**
	 * 选择用户
	 */
	private void selectSaler() {
		Intent intent = new Intent(ClientInfoActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		// bundle.putString("UserSelectId", mUserSelectId);
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, 0);
	}

	private String formatDate(String dataString) {
		if (dataString == null) {
			return "";
		}
		// 将时间字段中的T去除
		String regEx = "[0-9]{2}T[0-9]{2}"; // 表示11T11这样的数据
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(dataString);
		while (mat.find()) {
			String temp = dataString.substring(mat.start(), mat.end());
			dataString = dataString.replaceAll(temp, temp.replace("T", " "));//
		}
		return dataString;
	}

	/**
	 * 提交校验
	 *
	 */
	private boolean isCheck() {
		String name = etName.getText().toString();
		String contactName = etContactName.getText().toString();
		String registerTime = etRegisterTime.getText().toString();
		String saleMan = etSaleMan.getText().toString();
		String Phone = etPhone.getText().toString();
		String address = etAdress.getText().toString();
		// String lastcontact = etLastContact.getText().toString();
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(contactName)
				|| TextUtils.isEmpty(Phone) || TextUtils.isEmpty(saleMan)
				|| TextUtils.isEmpty(address)) {
			if (TextUtils.isEmpty(name)) {
				Toast.makeText(getApplicationContext(), "未选择客户名称",
						Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(contactName)) {
				Toast.makeText(getApplicationContext(), "未填写联系人",
						Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(Phone)) {
				Toast.makeText(getApplicationContext(), "未填写电话号码",
						Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(saleMan)) {
				Toast.makeText(getApplicationContext(), "未选择业务员",
						Toast.LENGTH_LONG).show();
			} else if (TextUtils.isEmpty(address)) {
				Toast.makeText(getApplicationContext(), "未填写联系地址",
						Toast.LENGTH_LONG).show();
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * 根据文字内容计算重绘EditText的高度
	 *
	 * @param linearLayout
	 *            父控件
	 * @param editText
	 *            文本控件
	 * @param contents
	 *            文字内容
	 */
	private void caculateHeight(final EditText editText, final String contents) {
		// 监听控件绘制
		ViewTreeObserver vto = editText.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				Object editTag = editText.getTag();
				boolean hasMessured = (editTag == null) ? false
						: (Boolean) editTag;
				if (hasMessured == false) {
					// hasMessured = true;
					editText.setTag(true);
					int width = editText.getWidth(); // 控件宽度
					int height = editText.getHeight(); // 控件高度
					if (width != 0 && height != 0) {
						int len = 0;
						if (!TextUtils.isEmpty(contents)) {
							len = contents.length(); // 字数
						}
						float px = editText.getTextSize(); // 得到字体像素
						double length = Math.floor(width / px); // 能容纳字母个数
						if (len > length) {
							// int llWidth =
							// linearLayout.getLayoutParams().width;
							LinearLayout linearLayout = (LinearLayout) editText
									.getParent();
							int llWidth = linearLayout.getLayoutParams().width;
							int offset = (int) (len / length); // 计算出需要行数
							linearLayout
									.setLayoutParams(new LinearLayout.LayoutParams(
											llWidth, (int) (height + px
											* offset)));
						}
					}
				}
				return true;
			}
		});
	}
}
