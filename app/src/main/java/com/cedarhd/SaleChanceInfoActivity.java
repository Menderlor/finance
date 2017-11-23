package com.cedarhd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.User;
import com.cedarhd.models.动态;
import com.cedarhd.models.销售机会;
import com.cedarhd.utils.LogUtils;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * 销售机会详情/新建
 * 
 * @author bohr
 * 
 */
public class SaleChanceInfoActivity extends BaseActivity implements
		OnClickListener, OnFocusChangeListener {

	public final int SAVE_CHANCES_SUCCESS = 2; // 保存销售机会成功
	private final int SHOW_DATAPICKExpirationTime = 3;
	private final int SHOW_CLINET_NAME = 4; // 客户名称
	public static final int SELECT_CLIENT_CODE = 5;
	private final int SELECT_SALER_CODE = 6; // 选择业务员
	public final int GET_DYNAMIC_SUCCESS = 7; // 点击动态，获取销售机会成功
	public final int GET_DYNAMIC_FAILED = 8; // 点击动态，获取销售机会失败
	private DictionaryHelper dictionaryHelper;
	private ZLServiceHelper zlServiceHelper;
	private TextView tvTitle;
	private EditText etClientName;
	private EditText etContacts;
	private EditText etPlanTime;
	private EditText etSaler;
	private EditText etPhone;
	// private EditText etAddress;
	private EditText etContent;
	private EditText etestimatedamount;
	private EditText etActualAmount;
	private EditText etStage;
	private ImageView ivCancel;
	private ImageView ivDone;
	private LinearLayout llContactslist;// 查看联系记录
	private LinearLayout llContactsNew;// 新建联系
	private LinearLayout llWorkPlanList;// 查看工作计划
	private LinearLayout llWorkPlanNew; // 新建工作计划
	private 销售机会 item;
	private Boolean isNew; // 标识位，是否新建
	private String mUserSelectId = ""; // 选择业务员id
	private String mUserSelectName = "";// 业务员姓名
	private int clientId;
	private String clientName;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Button btnSpeek2; // 说话按钮
	ImageView ivKeybord2;// 键盘输入
	private Context context;
	public static final int EDIT_CONTENT_CODE = 7;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SAVE_CHANCES_SUCCESS:
				String result = msg.obj.toString();
				if ("true".equals(result)) {
					Toast.makeText(SaleChanceInfoActivity.this, "提交成功",
							Toast.LENGTH_LONG).show();
					SaleChanceListActivity.isResume = true;
					finish();
				} else {
					Toast.makeText(SaleChanceInfoActivity.this, "提交失败",
							Toast.LENGTH_LONG).show();
				}
				break;
			case GET_DYNAMIC_SUCCESS: // 点击动态，获取日志成功
				ProgressDialogHelper.dismiss();
				动态 dynamic = (动态) msg.obj;
				item = dynamic.SaleChance;
				if (item != null) {
					isNew = false;
					init(item);
				}
				break;
			case GET_DYNAMIC_FAILED: // 获取销售机会失败
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载销售机会失败，请稍后重试", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.sale_chance_info);
		findviews();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			if (clientId == -1) {
				// 详情页面
				isNew = false;
				Object obj = bundle.getSerializable(SaleChanceListActivity.TAG);
				if (obj instanceof 销售机会) {
					item = (销售机会) obj;
					init(item);
				}
			} else {
				isNew = true;
				// 从客户详情页面新建，业务员和客户不可编辑，显示为默认
				etClientName.setEnabled(false);
				etSaler.setEnabled(false);
				init();
			}
		} else { // 新建页面
			isNew = true;
			init();
		}
		setOnclick();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			// Toast.makeText(this, "来自信鸽：" + clickedResult.toString(),
			// Toast.LENGTH_LONG).show();
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				// 获取动态类型 和 数据编号
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(context, "日志加载中");
				new Thread(new Runnable() {
					@Override
					public void run() {
						动态 dynamic = zlServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.SaleChance != null) {
							// 设置销售机会为已读
							zlServiceHelper.ReadDynamic(dynamic.SaleChance.Id,
									9);

							// 发送到handler中进行处理
							Message msg = handler.obtainMessage();
							msg.obj = dynamic;
							msg.what = GET_DYNAMIC_SUCCESS;
							handler.sendMessage(msg);
						} else {
							handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("erro", e.toString());
				handler.sendEmptyMessage(GET_DYNAMIC_FAILED);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sale_chance_info, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				// etContent = bundle.getInt(ClientListActivity.ClientId);
				etContent
						.setText(bundle.getString(TaskContentActivity.Content));

			}
			if (requestCode == SELECT_SALER_CODE) { // 选择业务员
				// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				mUserSelectName = bundle.getString("UserSelectName");
				etSaler.setText(mUserSelectName);
			} else if (requestCode == SELECT_CLIENT_CODE) {
				// 取出客户名称字符串
				Bundle bundle = data.getExtras();
				clientId = bundle.getInt(ClientListActivity.ClientId);
				clientName = dictionaryHelper.getClientNameById(clientId);
				etClientName.setText(clientName);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.etContactName_salechanceinfo2:
			new SpeechDialogHelper(context, this, etContacts, false);
			break;
		case R.id.ivDone_salechanceinfo2: // 保存
			submit();
			break;
		case R.id.ivCance_salechanceinfo2: // 返回
			finish();
			break;
		case R.id.etClientName_salechanceinfo2: // 选择客户
			selectClientName();
			break;
		case R.id.et_saler_salechanceinfo2: // 业务员
			selectSaler();
			break;
		case R.id.etPlanTime_salechanceinfo2: // 计划联系时间
			showDialog(SHOW_DATAPICKExpirationTime);
			break;
		case R.id.iv_workplan_list_salechance2: // 查看工作计划
			Intent intentSale = new Intent(SaleChanceInfoActivity.this,
					TaskListActivityNew.class);
			Bundle bundleSale = new Bundle();
			bundleSale.putInt("ClientInfoActivity_clientId",
					item.getCustomerId());
			intentSale.putExtras(bundleSale);
			startActivity(intentSale);
			break;
		case R.id.iv_contact_list_salechance2: // 查看联系记录
			Intent intent = new Intent(this, ClientConstactListActivity.class);
			Bundle bundle = new Bundle();
			// Client client = new Client();
			// client.setId(item.getCustomerId());
			bundle.putInt(ClientConstactListActivity.SALE_CHANCE_ID,
					item.getId());
			// bundle.putSerializable(ClientConstactListActivity.TAG, client);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		case R.id.iv_contact_add_salechance2: // 新建联系记录
			Intent intent3 = new Intent(SaleChanceInfoActivity.this,
					ClientConstactNewActivity.class);
			Bundle bundle3 = new Bundle();
			Client ite客户 = new Client();
			ite客户.setId(item.getCustomerId());
			ite客户.setContacts(item.getContacts());
			if (item != null) {
				bundle3.putSerializable(
						ClientConstactNewActivity.SALE_CHANCE_TAG, item);
			}
			bundle3.putSerializable(ClientConstactNewActivity.CLIENTTAG, ite客户);
			intent3.putExtras(bundle3);
			startActivity(intent3);
			break;
		case R.id.iv_workplan_new_salechance2: // 新建计划
			Intent intent2 = new Intent(SaleChanceInfoActivity.this,
					TaskNewActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putInt("ClientInfoActivity_clientId", item.getCustomerId());
			intent2.putExtras(bundle2);
			startActivity(intent2);
			break;
		case R.id.btn_speek2_salechance: // 点击说话
			new SpeechDialogHelper(context, this, etContent, true);
			break;
		case R.id.iv_keybord2_salechance:
			etContent.requestFocus();
			// 弹出软键盘
			InputMethodManager m2 = (InputMethodManager) etContacts
					.getContext().getSystemService(INPUT_METHOD_SERVICE);
			m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (hasFocus) {
			switch (id) {
			case R.id.etClientName_salechanceinfo2: // 选择客户
				selectClientName();
				break;
			case R.id.et_saler_salechanceinfo2: // 业务员
				// selectSaler();
				break;
			case R.id.etPlanTime_salechanceinfo2: // 计划联系时间
				showDialog(SHOW_DATAPICKExpirationTime);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case SHOW_DATAPICKExpirationTime:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_DATAPICKExpirationTime:
			return new DatePickerDialog(this, mDateExpirationTimeSetListener,
					mYear, mMonth, mDay);
		}
		return null;
	}

	private void updateDateFromDisplay() {
		etPlanTime.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	private DatePickerDialog.OnDateSetListener mDateExpirationTimeSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateFromDisplay();
		}
	};

	private void findviews() {
		context = SaleChanceInfoActivity.this;
		dictionaryHelper = new DictionaryHelper(SaleChanceInfoActivity.this);
		zlServiceHelper = new ZLServiceHelper();
		tvTitle = (TextView) findViewById(R.id.tv_title_sale_chance_info2);
		etClientName = (EditText) findViewById(R.id.etClientName_salechanceinfo2);
		etContacts = (EditText) findViewById(R.id.etContactName_salechanceinfo2);
		etPlanTime = (EditText) findViewById(R.id.etPlanTime_salechanceinfo2);
		etSaler = (EditText) findViewById(R.id.et_saler_salechanceinfo2);
		etPhone = (EditText) findViewById(R.id.et_phone_salechanceinfo2);
		// etAddress = (EditText) findViewById(R.id.et_address_salechanceinfo2);
		etContent = (EditText) findViewById(R.id.et_content_salechanceinfo2);
		etContent.setFocusable(false);
		etContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SaleChanceInfoActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});
		etestimatedamount = (EditText) findViewById(R.id.et_estimatedamount_salechanceinfo2);
		etActualAmount = (EditText) findViewById(R.id.et_totalamount_salechanceinfo2);
		ivCancel = (ImageView) findViewById(R.id.ivCance_salechanceinfo2);
		ivDone = (ImageView) findViewById(R.id.ivDone_salechanceinfo2);
		btnSpeek2 = (Button) findViewById(R.id.btn_speek2_salechance); // 语音 说话
		btnSpeek2.setVisibility(View.GONE);
		ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_salechance);// 键盘
		ivKeybord2.setVisibility(View.GONE);
		etStage = (EditText) findViewById(R.id.et_stage_salechanceinfo2);
		btnSpeek2.setOnClickListener(this);
		ivKeybord2.setOnClickListener(this);
		etContacts.setOnClickListener(this);
		// btnContactslist = (Button)
		// findViewById(R.id.btn_contactlist_salechance);
		// btnContactsNew = (Button)
		// findViewById(R.id.btn_contact_new_salechance);
		// btnWorkPlanNew = (Button)
		// findViewById(R.id.btn_workplan_new_salechance);
		// btnWorkPlanList = (Button)
		// findViewById(R.id.btn_workplan_salechance);
		llContactslist = (LinearLayout) findViewById(R.id.iv_contact_list_salechance2);
		llContactsNew = (LinearLayout) findViewById(R.id.iv_contact_add_salechance2);
		llWorkPlanNew = (LinearLayout) findViewById(R.id.iv_workplan_new_salechance2);
		llWorkPlanList = (LinearLayout) findViewById(R.id.iv_workplan_list_salechance2);

	}

	/**
	 * 显示销售机会详情页面
	 * 
	 * @param item
	 */
	private void init(销售机会 item) {
		mUserSelectName = dictionaryHelper.getUserNameById(item.getSalesman());
		// etClientName.setText(item.getCustomerName());
		etContacts.setText(item.getContacts());
		etPlanTime.setText(item.getPlanContactTime());
		etSaler.setText(mUserSelectName);
		etPhone.setText(item.getPhone());
		// etAddress.setText(item.getAddress());
		etContent.setText(item.getContent());
		etestimatedamount.setText(item.getEstimatedAmount());
		etActualAmount.setText(item.getActualAmount());
		etStage.setText(item.getContactState());

		clientId = item.getCustomerId();
		clientName = item.getCustomerName();
		etClientName.setText(clientName);

		// 详情页不可编辑
		ivDone.setVisibility(View.GONE);
		llContactslist.setVisibility(View.VISIBLE);
		llWorkPlanList.setVisibility(View.VISIBLE);
		llContactsNew.setVisibility(View.VISIBLE);
		llWorkPlanNew.setVisibility(View.VISIBLE);
		llContactslist.setOnClickListener(this);
		llWorkPlanList.setOnClickListener(this);
		llContactsNew.setOnClickListener(this);
		llWorkPlanNew.setOnClickListener(this);
		etClientName.setEnabled(false);
		etContacts.setEnabled(false);
		etPlanTime.setEnabled(false);
		etSaler.setEnabled(false);
		etPhone.setEnabled(false);
		// etAddress.setEnabled(false);
		etContent.setEnabled(false);
		etestimatedamount.setEnabled(false);
		etActualAmount.setEnabled(false);
		etStage.setEnabled(false);
	}

	/**
	 * 初始化页面
	 */
	private void init() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		tvTitle.setText("新建销售机会");
		// etClientName.setHint("请输入客户名称..");
		// etContacts.setHint("请输入联系人姓名..");
		// etPlanTime.setHint("请选择计划联系时间..");
		// etSaler.setHint("请选择业务员..");
		// etPhone.setHint("请输入联系电话..");
		// etAddress.setHint("请输入联系地址..");
		// etContent.setHint("请输入内容..");

		// 默认业务员为当前登录用户
		User user = Global.mUser;
		mUserSelectId = user.Id;
		LogUtils.i("keno4", Global.mUser.Id);
		etSaler.setText(dictionaryHelper.getUserNameById(Global.mUser.Id));
		clientName = dictionaryHelper.getClientNameById(clientId);
		etClientName.setText(clientName);
		llContactslist.setVisibility(View.GONE);
		llWorkPlanList.setVisibility(View.GONE);
		llContactsNew.setVisibility(View.GONE);
		llWorkPlanNew.setVisibility(View.GONE);

		etClientName.setOnClickListener(this);
		etPlanTime.setOnClickListener(this);
		etSaler.setOnClickListener(this);
		etClientName.setOnFocusChangeListener(this);
		etPlanTime.setOnFocusChangeListener(this);
		// etSaler.setOnFocusChangeListener(this);
		ivDone.setOnClickListener(this);

		// etClientName.setInputType(InputType.TYPE_NULL);
		etPlanTime.setInputType(InputType.TYPE_NULL);
		etSaler.setInputType(InputType.TYPE_NULL);
	}

	private void setOnclick() {
		ivCancel.setOnClickListener(this);
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(this, ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/**
	 * 选择用户
	 */
	private void selectSaler() {
		Intent intent = new Intent(SaleChanceInfoActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_SALER_CODE);
	}

	// 提交
	private void submit() {
		if (!isCheck()) {
			Toast.makeText(this, "用户名或业务员必选", Toast.LENGTH_LONG).show();
			return;
		} else {
			final 销售机会 item = new 销售机会();
			item.setCustomerId(clientId);
			item.setCustomerName(clientName);
			item.setContacts(etContacts.getText().toString());
			int saler = 0;
			try {
				saler = Integer.parseInt(mUserSelectId);
			} catch (Exception e) {
				saler = 0;
			}
			item.setSalesman(saler);
			item.setPlanContactTime(etPlanTime.getText().toString());
			item.setPhone(etPhone.getText().toString());
			// item.setAddress(etAddress.getText().toString());
			item.setContent(etContent.getText().toString());
			item.setEstimatedAmount(etestimatedamount.getText().toString());
			item.setActualAmount(etActualAmount.getText().toString());
			item.setContactState(etStage.getText().toString());
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 向服务器提交保存，网络操作放在子线程
						zlServiceHelper.submitSalesChance(item, handler);
					} catch (Exception e) {
						Toast.makeText(context, "新建销售机会异常", 0).show();
					}
				}
			}).start();

		}
	}

	private Boolean isCheck() {
		if (TextUtils.isEmpty(clientName) || TextUtils.isEmpty(mUserSelectId)) {
			return false;
		}
		return true;
	}

}
