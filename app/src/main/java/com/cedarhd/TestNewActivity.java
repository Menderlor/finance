package com.cedarhd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.测量信息;
import com.cedarhd.models.订单;
import com.cedarhd.utils.LogUtils;
import com.iflytek.cloud.SpeechRecognizer;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/**
 * 新建测试详情
 * 
 * @author Administrator
 * 
 */
public class TestNewActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {
	public static final String TAG = "TestNewActivity";
	private final int SELECT_DESINER_REQUEST_CODE = 31; // 单选设计师
	private final int SELECT_STAFF_REQUEST_CODE = 32; // 单选 制单人
	private static final int SHOW_DATE_PICKER = 101;
	private SpeechRecognizer mIat;
	private List<String> photoPathList; // 要上传照片路径列表
	public String mUserSingleSelectId = "0"; // 选择用户id
	private int orderId = -1;
	private String desinerId; // 设计师Id
	private String staffId; // 制单人Id
	private int desiner; // 设计师Id
	private int staff; // 制单人Id

	private Context context;
	private DictionaryHelper dictionaryHelper;
	private ZLServiceHelper zlServiceHelper;
	private AddImageHelper addImageHelper;

	private 测量信息 item;

	private int mYear;
	private int mMonth;
	private int mDay;

	private ImageView ivCancel;
	private ImageView ivDone;
	private EditText etDate;
	private EditText etAddress;
	private EditText etDesigner;
	private ProgressBar pBar;
	// private EditText etType;
	private EditText etStaff;
	private EditText etRemark;
	private HorizontalScrollViewAddImage llAddImage;
	private LinearLayout llSpeek;
	private ImageView ivKeyword;
	private Button btnSpeek;

	private Handler handler = new Handler() {
		private static final int UPDATE_SUCCESS = 3;
		private static final int UPDATE_FAILURE = 4;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_SUCCESS:
				Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case UPDATE_FAILURE:
				Toast.makeText(context, "提交失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_info);
		findViews();
		// orderId = getIntent().getIntExtra(TAG, -1);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			orderId = bundle.getInt(TAG, -1);
		}
		LogUtils.d("testList", "orderId:" + orderId);
		init();
	}

	private void findViews() {
		// 默认制单人和设计师都是自己
		staffId = Global.mUser.Id;
		desinerId = Global.mUser.Id;
		pBar = (ProgressBar) findViewById(R.id.pbar_test_info);
		ivCancel = (ImageView) findViewById(R.id.iv_cancel_testinfo);
		ivDone = (ImageView) findViewById(R.id.iv_submit_testinfo);
		etDate = (EditText) findViewById(R.id.tv_time_testinfo);
		etAddress = (EditText) findViewById(R.id.tv_address_testinfo);
		etDesigner = (EditText) findViewById(R.id.tv_designer_testinfo);
		etStaff = (EditText) findViewById(R.id.et_Staff_name_testinfo);
		etRemark = (EditText) findViewById(R.id.et_remark_testinfo);
		llAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_testinfo);
		llSpeek = (LinearLayout) findViewById(R.id.ll_speek_test_new);
		ivKeyword = (ImageView) findViewById(R.id.iv_speek_test_new);
		btnSpeek = (Button) findViewById(R.id.btn_speek_test_new);

		ivCancel.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		etDate.setOnClickListener(this);
		etDesigner.setOnClickListener(this);
		etStaff.setOnClickListener(this);
		ivKeyword.setOnClickListener(this);
		btnSpeek.setOnClickListener(this);

		etDate.setOnFocusChangeListener(this);
		etDesigner.setOnFocusChangeListener(this);
		etStaff.setOnFocusChangeListener(this);
		etRemark.setOnFocusChangeListener(this);
	}

	private void init() {
		etDate.setHint("请选择测量时间..");
		etAddress.setHint("请输入测量地址..");
		etDesigner.setHint("请选择设计师..");
		// etType.setHint("请选择测量类型..");
		etStaff.setHint("请选择制单人..");
		etRemark.setHint("请输入备注信息..");

		context = TestNewActivity.this;
		dictionaryHelper = new DictionaryHelper(context);
		zlServiceHelper = new ZLServiceHelper();
		addImageHelper = new AddImageHelper(this, TestNewActivity.this,
				llAddImage, null, true);
		staffId = Global.mUser.Id;
		etDate.setText(ViewHelper.getDateString());
		etDesigner.setText(dictionaryHelper.getUserNameById(staffId));
		etStaff.setText(dictionaryHelper.getUserNameById(staffId));
		if (orderId != -1) {
			etAddress.setText(getOrderAddress());
		}
		setDateTime();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.iv_cancel_testinfo: // 回退
			finish();
			break;
		case R.id.iv_submit_testinfo: // 提交
			submit();
			break;
		case R.id.tv_time_testinfo:
			showDialog(SHOW_DATE_PICKER);
			break;
		case R.id.tv_designer_testinfo:
			selectUser(SELECT_DESINER_REQUEST_CODE);
			break;
		case R.id.et_Staff_name_testinfo:
			selectUser(SELECT_STAFF_REQUEST_CODE);
			break;
		case R.id.iv_speek_test_new:
			etRemark.requestFocus();
			// 弹出软键盘
			InputMethodManager m2 = (InputMethodManager) etRemark.getContext()
					.getSystemService(INPUT_METHOD_SERVICE);
			m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.btn_speek_test_new:
			// 设置参数
			new SpeechDialogHelper(context, this, etRemark, true);
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
			case R.id.tv_time_testinfo:
				showDialog(SHOW_DATE_PICKER);
				setDateTime();
				break;
			case R.id.tv_designer_testinfo:
				selectUser(SELECT_DESINER_REQUEST_CODE);
				break;
			case R.id.et_Staff_name_testinfo:
				selectUser(SELECT_STAFF_REQUEST_CODE);
				break;
			case R.id.et_remark_testinfo:
				// if (hasFocus) {
				// llSpeek.setVisibility(View.VISIBLE);
				// } else {
				// llSpeek.setVisibility(View.GONE);
				// }
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
			if (requestCode == SELECT_DESINER_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
				mUserSingleSelectId = bundle.getString("UserSelectId");
				mUserSingleSelectId = mUserSingleSelectId.replace("'", "")
						.replace(";", "");
				desinerId = mUserSingleSelectId;
				etDesigner.setText(dictionaryHelper
						.getUserNameById(mUserSingleSelectId));
			}
			if (requestCode == SELECT_STAFF_REQUEST_CODE) { // 制单人
				Bundle bundle = data.getExtras();
				mUserSingleSelectId = bundle.getString("UserSelectId");
				mUserSingleSelectId = mUserSingleSelectId.replace("'", "")
						.replace(";", "");
				staffId = mUserSingleSelectId;
				etStaff.setText(dictionaryHelper
						.getUserNameById(mUserSingleSelectId));
			}
		}
	}

	private String getOrderAddress() {
		String address = "";
		ORMDataHelper helper = ORMDataHelper.getInstance(context);
		try {
			Dao<订单, Integer> dao = helper.getDao(订单.class);
			address = dao.queryForEq("Id", orderId).get(0).Address;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * 添加设计师
	 */
	private void selectUser(int requestCode) {
		Intent intent = new Intent(this, User_SelectActivityNew.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(User_SelectActivityNew.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 设置时间
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 时间选择器
	 */
	private DatePickerDialog.OnDateSetListener mDateExpirationTimeSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateFromDisplay();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_DATE_PICKER:
			return new DatePickerDialog(this, mDateExpirationTimeSetListener,
					mYear, mMonth, mDay);
		}
		return null;
	}

	private void updateDateFromDisplay() {
		etDate.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	private void submit() {
		if (isCheck()) {
			try {
				desiner = Integer.parseInt(desinerId);
				staff = Integer.parseInt(staffId);
			} catch (Exception e) {
			}

			photoPathList = addImageHelper.getPhotoList();
			for (String path : photoPathList) {
				Log.i("attachPath", path);
			}
			if (photoPathList.size() > 0) {
				pBar.setVisibility(View.VISIBLE);
				pBar.setMax(photoPathList.size());
			}
			item = new 测量信息();
			item.setOrder(orderId);
			item.setDate(etDate.getText().toString());
			item.setAddress(etAddress.getText().toString());
			item.setDesigner(desiner);
			item.setStaff(staff);
			item.setRemark(etRemark.getText().toString());
			// TODO 图片上传
			new Thread(new Runnable() {
				@Override
				public void run() {
					zlServiceHelper.submitTestInfo(handler, item,
							photoPathList, pBar);
				}
			}).start();
		}
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean isCheck() {
		if (TextUtils.isEmpty(etDate.getText().toString())) {
			Toast.makeText(context, "请选择测量时间..", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etAddress.getText().toString())) {
			Toast.makeText(context, "请输入测量地址..", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etDesigner.getText().toString())) {
			Toast.makeText(context, "请选择设计师..", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(etStaff.getText().toString())) {
			Toast.makeText(context, "请选择制单人..", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
