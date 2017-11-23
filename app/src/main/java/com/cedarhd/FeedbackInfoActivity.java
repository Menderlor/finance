package com.cedarhd;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
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
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.BitmapHelper;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.问题反馈;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.iflytek.cloud.SpeechRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * 问题反馈详情
 *
 * @author py
 *
 */
public class FeedbackInfoActivity extends BaseActivity implements
		OnClickListener, OnFocusChangeListener {
	private static final int SHOW_STARTDATA = 0;
	private static final int SHOW_ENDDATE = 1;
	private final int CAMERA_TAKE_REQUEST_CODE = 2;
	private final int SELECT_REQUEST_CODE = 3;
	public static final int RESULT_CODE_SUCCESS = 4;
	public static final int RESULT_CODE_FAILED = 5;
	public static final int SELECT_CLIENT_CODE = 6; // 选择客户名称
	private final int SELECT_SIGNAL_REQUEST_CODE = 31; // 单选
	private static final String[] arrs = { "启动", "暂停", "完成", "搁置", "提交", "重启" };
	private List<String> status = new ArrayList<String>();
	private String photoSerialNo; // 照片序列号
	private String mPictureFile; // 照片文件名
	private static final String PATH = Environment
			.getExternalStorageDirectory() + "/DCIM";
	// 头像存放路径
	private String avatarPath = FilePathConfig.getAvatarDirPath();

	private int clientId;
	private SpeechRecognizer mIat;
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private DictionaryHelper dictionaryHelper;
	private HandlerAvatar handler;
	private DateAndTimePicker dateAndTimePicker;
	private Context context;
	HashMap<String, Object> mTask;
	private ORMDataHelper helper;
	private AddImageHelper addImageHelper;
	private EditText etTitle;
	private EditText etContent;
	private EditText etClient;
	private TextView tvStartDate;
	private TextView tvEndDate;
	private LinearLayout llState;
	private EditText etState;
	private View viewState;
	// private TextView tvStatuts;
	// private ImageView ivExecutor; // 任务执行人头像
	// private ImageView ivParticipant; // 任务参与人头像

	// private LinearLayout llImgPaticipant; // 参与人头像区
	public String mUserSingleSelectId = "0";
	private ImageView ivState;
	private ImageView btnCancel;
	private ImageView btnSubmit;
	private TextView tvtitle;
	private HorizontalScrollViewAddImage llLoAddImage;

	// private Button btnSpeek; // 说话按钮
	// ImageView ivSpeek; // 喇叭按钮，选择说话
	// ImageView ivKeybord;// 键盘输入
	/** 用于输入备注信息 */
	private Button btnSpeek2; // 说话按钮
	ImageView ivKeybord2;// 键盘输入
	private boolean isSpeek1 = true;// 标志位 用来区分两个不同的说话按钮

	private boolean hasMessured; // 計算高度
	private boolean isStart; // true表示开始，false表示结束
	// private boolean isEdit; // 默认不可编辑
	public String mUserSelectId = "";
	public String mUserSelectName = "";
	public int states = 1;
	private 问题反馈 item;

	private int mYear;
	private int mMonth;
	private int mDay;
	private AvartarView avExecutor;// 执行者头像、姓名
	// private AvartarView avAddParticipant;//参与者头像、姓名
	private EditText etParticipant;
	private static final int CODE_SELECT_USER = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_info_new);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Bundle data = getIntent().getExtras();
		item = (问题反馈) data.get(FeedbackListActivity.TAG);
		initViews(item);
		init();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CODE_SELECT_USER) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				mUserSelectName = bundle.getString("UserSelectName");
				// 选择参与人
				etParticipant.setText(mUserSelectName);

				etParticipant.setTag(false);
				// caculateHeight(llReceiver, mEditTextReceiverName,
				// mUserSelectName);
				LogUtils.i("testKeno", mUserSelectId + "======"
						+ mUserSelectName);
			}
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
			if (requestCode == SELECT_CLIENT_CODE) {
				// 取出客户名称字符串
				Bundle bundle = data.getExtras();
				clientId = bundle.getInt(ClientListActivity.ClientId);
				LogUtils.i("kjxi", "clientId:" + clientId);
				if (clientId != 0) {
					etClient.setText(dictionaryHelper
							.getClientNameById(clientId));
				}
			}
			if (requestCode == SELECT_SIGNAL_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
				mUserSingleSelectId = bundle.getString("UserSelectId");
				mUserSingleSelectId = mUserSingleSelectId.replace("'", "")
						.replace(";", "");
				LogUtils.i("kjx21", "sigal------>" + mUserSingleSelectId);
				// setAvatar(mUserSingleSelectId, ivExecutor);
				new AvartarViewHelper(context, mUserSingleSelectId, avExecutor,
						true);
			}
			// if (requestCode == SELECT_REQUEST_CODE) {// 选择员工姓名
			// // 取出字符串
			// Bundle bundle = data.getExtras();
			// mUserSelectId = bundle.getString("UserSelectId");
			// LogUtils.i("kjx21", "------>" + mUserSelectId);
			// mUserSelectName = bundle.getString("UserSelectName");
			// // TODO 显示执行人头像
			// // ivAddParticipant.
			// if (!TextUtils.isEmpty(mUserSelectId)) {
			// String[] arr = dictionaryHelper
			// .getUserIdArray(mUserSelectId);
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// Math.round(ViewHelper.dip2px(context, 60)),
			// Math.round(ViewHelper.dip2px(context, 60)));
			// params.rightMargin = Math.round(ViewHelper.dip2px(context,
			// 3));
			// llImgPaticipant.removeAllViews(); // 移除之前所有控件
			// if (arr.length == 0) {
			// AvartarView img = new AvartarView(context);
			// img.setLayoutParams(params);
			// new AvartarViewHelper(context, mUserSelectId, avAddParticipant,
			// true);
			// llImgPaticipant.addView(img);
			// }
			// for (int i = 0; i < arr.length; i++) {
			// AvartarView img = new AvartarView(context);
			// img.setLayoutParams(params);
			// // setAvatar(arr[i], img);
			// new AvartarViewHelper(context, arr[i], img, true);
			// llImgPaticipant.addView(img);
			// }
			// for (String id : arr) {
			// LogUtils.i("kjx21", "ID:" + id);
			// }
			// }
			// }
		}
	}

	public void initViews(问题反馈 obj) {
		clientId = obj.ClientId;
		mUserSelectId = obj.Participant; // 参与人
		mUserSingleSelectId = obj.Executor + "";
		photoSerialNo = obj.Attachment;
		context = FeedbackInfoActivity.this;
		helper = ORMDataHelper.getInstance(FeedbackInfoActivity.this);
		dictionaryHelper = new DictionaryHelper(context);
		dateAndTimePicker = new DateAndTimePicker(context);

		handler = new HandlerAvatar();
		for (String item : arrs) {
			status.add(item);
		}
		LinearLayout ll_title_info = (LinearLayout) findViewById(R.id.ll_title_feedbackinfo);
		etTitle = (EditText) findViewById(R.id.tvTitle_feedbackinfo);
		// ivExecutor = (ImageView) findViewById(R.id.ivExecutor_taskinfo2);
		// ivParticipant = (ImageView)
		// findViewById(R.id.ivAddParticipant_taskinfo2);
		tvStartDate = (TextView) findViewById(R.id.tvStartTime_feedbackinfo);
		etContent = (EditText) findViewById(R.id.etContent_feedbackinfo);
		etClient = (EditText) findViewById(R.id.et_client_name_feedbackinfo);
		tvEndDate = (TextView) findViewById(R.id.tvEndTime_feedbackinfo);
		btnSubmit = (ImageView) findViewById(R.id.ivSubmit_feedbackinfo);
		btnCancel = (ImageView) findViewById(R.id.ivCancel_feedbackinfo);
		llState = (LinearLayout) findViewById(R.id.ll_state_feedbackinfo);
		etState = (EditText) findViewById(R.id.tv_state_feedbackinfo);
		viewState = findViewById(R.id.view_state_feedbackinfo);

		etState.setText(dictionaryHelper.getStateName(obj.getStatus()));
		// llImgPaticipant = (LinearLayout)
		// findViewById(R.id.llImgParticipant_feedbackinfo);
		llLoAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_feedbackinfo);

		btnSpeek2 = (Button) findViewById(R.id.btn_speek2_feedbackinfo); // 语音
		// 说话
		ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_feedbackinfo);// 键盘
		btnSpeek2.setOnClickListener(this);
		ivKeybord2.setOnClickListener(this);

		addImageHelper = new AddImageHelper(this, this.getApplicationContext(),
				llLoAddImage, obj.Attachment, false);
		etParticipant = (EditText) findViewById(R.id.et_Participant_feedbackinfo);
		etParticipant.setText(dictionaryHelper
				.getUserNamesById(obj.Participant));
		LogUtils.i("pypy", dictionaryHelper.getUserNamesById(obj.Participant));
		etTitle.setText(obj.Title);
		// caculateHeight(ll_title_info, etTitle, obj.Title);
		// TODO 设置头像
		LogUtils.i("keno11", "-->" + mUserSingleSelectId);
		avExecutor = (AvartarView) findViewById(R.id.Executor_feedbackinfo);
		new AvartarViewHelper(context, mUserSingleSelectId, avExecutor, true);
		// setAvatar(obj.Executor + "", ivExecutor);
		tvStartDate.setText(obj.AssignTime);// 发布时间
		tvEndDate.setText(obj.AssignTime); // 问题反馈完成时间
		// tvStatuts.setText(obj.StatusName);
		etContent.setText(obj.Content);
		etClient.setText(dictionaryHelper.getClientNameById(obj.getClientId()));

		btnCancel.setOnClickListener(this);
		// btnSubmit.setVisibility(View.GONE);
		btnSubmit.setOnClickListener(this);
		avExecutor.setOnClickListener(this);
		// avAddParticipant = (AvartarView)
		// findViewById(R.id.Participant_feedbackinfo);
		// avAddParticipant.setOnClickListener(this);
		// 如果不是自己发布的问题反馈不能修改
		if (!Global.mUser.Id.equals(item.Publisher + "")) {
			// Toast.makeText(context, "只能修改自己发布的问题反馈",
			// Toast.LENGTH_LONG).show();
			btnSubmit.setVisibility(View.GONE);
			// ivSpeek.setVisibility(View.GONE);
			btnSpeek2.setVisibility(View.GONE);
			// ivKeybord.setVisibility(View.GONE);
			ivKeybord2.setVisibility(View.GONE);
			requestFocus(false);
		} else {
			requestFocus(true);
			etTitle.setFocusable(false);

			// etState.setFocusable(false);
			// etContent.setFocusable(false);
		}

		// if (!TextUtils.isEmpty(mUserSelectId)) { // 参与人
		// String[] arr = dictionaryHelper.getUserIdArray(mUserSelectId);
		//
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		// Math.round(ViewHelper.dip2px(context, 60)),
		// Math.round(ViewHelper.dip2px(context, 60)));
		// params.rightMargin = Math.round(ViewHelper.dip2px(context, 3));
		// llImgPaticipant.removeAllViews(); // 移除之前所有控件
		// if (arr.length == 0) {
		// AvartarView img = new AvartarView(context);
		// img.setLayoutParams(params);
		// // img.setImageResource(R.drawable.tx);
		// llImgPaticipant.addView(img);
		// }
		// for (int i = 0; i < arr.length; i++) {
		// AvartarView img = new AvartarView(context);
		// img.setLayoutParams(params);
		// // img.setImageResource(R.drawable.tx);
		// // setAvatar(arr[i], img);
		// new AvartarViewHelper(context, arr[i], img, true);
		// llImgPaticipant.addView(img);
		// }
		// for (String id : arr) {
		// LogUtils.i("kjx21", "ID:" + id);
		// }
		// }
	}

	/**
	 * 设置焦点
	 *
	 * @param isFocus
	 *            是否获得焦点
	 */
	private void requestFocus(boolean isFocus) {
		llState.setVisibility(View.VISIBLE);
		etState.setVisibility(View.VISIBLE);
		viewState.setVisibility(View.VISIBLE);
		etTitle.setEnabled(isFocus);
		etState.setEnabled(isFocus);
		tvStartDate.setEnabled(isFocus);
		tvEndDate.setEnabled(isFocus);
		// ivExecutor.setEnabled(isFocus);
		// llImgPaticipant.setEnabled(isFocus);
		// ivParticipant.setEnabled(isFocus);
		etClient.setEnabled(isFocus);
		etContent.setEnabled(isFocus);
		// avAddParticipant.setEnabled(isFocus);
		avExecutor.setEnabled(isFocus);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ivCancel_feedbackinfo: // 取消
				finish();
				break;
			case R.id.Executor_feedbackinfo: // 选择执行人
				addExecutor();
				break;
			case R.id.llImgParticipant_feedbackinfo: // 选择参与人
				// case R.id.Participant_feedbackinfo:
				// addParticipant();
				// break;
			case R.id.tvStartTime_feedbackinfo:
				// isStart = true;
				// showDialog(SHOW_STARTDATA);
				// setDateTime();
				dateAndTimePicker.showDateWheel(tvStartDate);
				break;
			case R.id.tvEndTime_feedbackinfo:
				isStart = false;
				showDialog(SHOW_ENDDATE);
				setDateTime();
				break;
			case R.id.et_client_name_feedbackinfo:
				selectClientName();
				break;
			case R.id.ivSubmit_feedbackinfo: // 提交
				submit();
				break;
			case R.id.btn_speek2_feedbackinfo: // 按住说话
				isSpeek1 = false;
				new SpeechDialogHelper(context, this, etContent, true);
				break;
			case R.id.iv_keybord2_feedbackinfo:
				etContent.requestFocus();
				// 弹出软键盘
				InputMethodManager m2 = (InputMethodManager) etTitle.getContext()
						.getSystemService(INPUT_METHOD_SERVICE);
				m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				break;
			case R.id.tvTitle_feedbackinfo: // 标题
				new SpeechDialogHelper(context, this, etTitle, false);
				break;
		}
	}

	/**
	 * 提交
	 */
	private void submit() {
		// if (!isEdit) {
		// isEdit = true;
		// btnSubmit.setImageResource(R.drawable.check);
		// init();
		// return;
		// }
		// 提交空校验
		if (checkValid()) {
			LogUtils.i("tag", "isCheckValid");
			ORMDataHelper helper = ORMDataHelper.getInstance(this);
			String title = etTitle.getText().toString();
			// TODO 没有修改任务的接口
			// String participant = etParticipant.getText().toString();
			String startDate = tvStartDate.getText().toString();
			String endDate = tvEndDate.getText().toString();
			String content = etContent.getText().toString();
			item.Participant = mUserSelectId;
			item.Executor = Integer.parseInt(mUserSingleSelectId);
			item.Title = title;
			item.Content = content;
			item.Time = startDate;
			item.AssignTime = endDate;
			item.ClientId = clientId;
			LogUtils.i("update", item.toString());
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mDataHelper.EditFeedback(item, handler);
					} catch (Exception e) {
						Toast.makeText(context, "编辑问题反馈异常", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}).start();

		}
	}

	private void init() {
		// llImgPaticipant.setOnClickListener(this);
		// ivExecutor.setOnClickListener(this);
		// ivParticipant.setOnClickListener(this);
		tvStartDate.setOnClickListener(this);
		tvEndDate.setOnClickListener(this);
		etClient.setOnClickListener(this);
		// tvStatuts.setOnClickListener(this);

		btnCancel.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		etTitle.setOnClickListener(this);
		etTitle.setOnFocusChangeListener(this);
		etClient.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					selectClientName();
				}
			}
		});
		etParticipant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FeedbackInfoActivity.this,
						User_SelectActivityNew_zmy.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mUserSelectId);
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_USER);
			}
		});
		etParticipant.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Intent intent = new Intent(FeedbackInfoActivity.this,
							User_SelectActivityNew_zmy.class);
					Bundle bundle = new Bundle();
					bundle.putString("UserSelectId", mUserSelectId);
					intent.putExtras(bundle);
					startActivityForResult(intent, CODE_SELECT_USER);
				}
			}
		});
	}

	/**
	 * 添加参与人
	 */
	private void addParticipant() {
		Intent intent = new Intent(this, User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_REQUEST_CODE);
	}

	/**
	 * 添加执行人
	 */
	private void addExecutor() {
		Intent intent = new Intent(this, User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		// bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_SIGNAL_REQUEST_CODE);
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

	private void updateDateFromDisplay() {
		TextView tvText = isStart ? tvStartDate : tvEndDate;
		// tvText.setBackgroundColor(0xffffff);
		tvText.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case SHOW_STARTDATA:
				return new DatePickerDialog(this, mDateExpirationTimeSetListener,
						mYear, mMonth, mDay);
			case SHOW_ENDDATE:
				return new DatePickerDialog(this, mDateExpirationTimeSetListener,
						mYear, mMonth, mDay);
			default:
				break;
		}
		return null;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (hasFocus) {
			switch (id) {
				case R.id.etStartTime_addtask:
					// isStart = true;
					// showDialog(SHOW_STARTDATA);
					// setDateTime();
					dateAndTimePicker.showDateWheel(tvStartDate);
					break;
				case R.id.etEndTime_addtask:
					isStart = false;
					showDialog(SHOW_ENDDATE);
					setDateTime();
					break;
				// case R.id.tvTitle_taskinfo: // 标题
				// new SpeechDialogHelper(context, this, etTitle, false);
				// break;
				default:
					break;
			}
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(FeedbackInfoActivity.this,
				ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/**
	 * 提交校验
	 *
	 * @return
	 */
	private boolean checkValid() {
		if (etTitle.getText() == null
				|| etTitle.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(FeedbackInfoActivity.this, "保存失败",
					"标题不能为空！");
			return false;
		} else if (etTitle.getText().toString().trim().length() > 50) {
			MessageUtil.AlertMessage(FeedbackInfoActivity.this, "保存失败",
					"标题不能多于50个字！");
			return false;
		}

		// 判断接收人是否为空
		if (TextUtils.isEmpty(mUserSelectId)) {
			// MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败",
			// "参与人不能为空！");
			// return false;
		}

		if (tvStartDate.getText().toString().contains("时间")
				|| tvStartDate.getText().toString().replaceAll(" ", "")
				.length() <= 0) {
			MessageUtil.AlertMessage(FeedbackInfoActivity.this, "保存失败",
					"请选择开始时间！");
			return false;
		}
		// if (tvEndDate.getText() == null
		// || tvEndDate.getText().toString().replaceAll(" ", "").length() <= 0)
		// {
		// MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败", "请选择结束时间！");
		// return false;
		// }
		return true;
	}

	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 处理头像handler
	 *
	 * @author bohr
	 *
	 */
	public class HandlerAvatar extends Handler {
		// 标示固定
		public final int SHOW_IMAGE_SUCCESS = 3;
		public final int SHOW_IMAGE_FAILUREE = 4;
		public final static int UPLOAD_PHOTO_SUCCESS = 5; // 上传图片成功
		public final static int UPLOAD_PHOTO_FAILURE = 6; // 上传图片失败
		// private ImageView ivAvatars;
		private AvartarView avAvatarts;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == SHOW_IMAGE_SUCCESS) {
				// TODO Bitmap保存在Tag中
				// ivAvatars = (ImageView) msg.obj;
				// Bitmap avatar = (Bitmap) ivAvatars.getTag();
				// ivAvatars.setImageBitmap(avatar);
				avAvatarts = (AvartarView) msg.obj;
				// String avatarName = (String) ivAvatars.getTag();
				// showLocalImg(ivAvatars, avatarName);

			} else if (what == SHOW_IMAGE_FAILUREE) {
				// ivAvatars.setImageResource(R.drawable.tx);
			} else if (what == UPLOAD_PHOTO_SUCCESS) {
				// 提示信息显示
				MessageUtil.ToastMessage(context, "修改成功！");
				TaskListActivityNew.isResume = true;
				finish();
			} else if (what == UPLOAD_PHOTO_FAILURE) {
				// pBar.setVisibility(View.GONE);
				TaskListActivityNew.isResume = false;
				MessageUtil.ToastMessage(context, "修改失败！");
			}
		}
	}

	/**
	 * 显示本地图片
	 *
	 * @param ivAvatar
	 *            图片控件
	 * @param avatarName
	 *            图片名
	 */
	private void showLocalImg(final ImageView ivAvatar, String avatarName) {
		if (!TextUtils.isEmpty(avatarName)) {
			File file = new File(new File(avatarPath), avatarName);
			if (file.exists()) { // 本地有图直接使用
				Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
				Bitmap avatar = BitmapHelper.zoomBitmap(bitmap,
						ViewHelper.dip2px(context, 75),
						ViewHelper.dip2px(context, 75));
				ivAvatar.setImageBitmap(avatar);
			}
		}
	}

	/**
	 * 设置头像
	 */
	private void setAvatar(String id, final ImageView ivAvatar) {
		DictionaryHelper dictionaryHelper = new DictionaryHelper(context);
		final String avataUrl = dictionaryHelper.getUserPhoto(id); // 头像地址
		// 如果本地数据库中存有用户头像信息
		if (!TextUtils.isEmpty(avataUrl) && avataUrl.contains("\\")) {
			int index = avataUrl.lastIndexOf("\\");
			String avatarName = avataUrl
					.substring(index + 1, avataUrl.length()); // 图片名
			// 本地图片文件
			if (!TextUtils.isEmpty(avatarName)) {
				File file = new File(new File(avatarPath), avatarName);
				if (file.exists()) { // 本地有图直接使用
					Bitmap avatar = BitmapFactory.decodeFile(file.toString());
					ivAvatar.setImageBitmap(avatar);
				} else {
					// 开启线程从网络下载
					final HttpUtils httpUtils = new HttpUtils();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								httpUtils.downloadData(avataUrl, handler,
										ivAvatar);
							} catch (Exception e) {
								Toast.makeText(context, "下载网络数据异常",
										Toast.LENGTH_SHORT).show();
							}
						}
					}).start();
				}
			} else {
				// 开启线程从网络下载
				final HttpUtils httpUtils = new HttpUtils();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							httpUtils.downloadData(avataUrl, handler, ivAvatar);
						} catch (Exception e) {
							Toast.makeText(context, "下载网络数据异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		}
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
	private void caculateHeight(final LinearLayout linearLayout,
								final EditText editText, final String contents) {
		// 监听控件绘制
		ViewTreeObserver vto = editText.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (hasMessured == false) {
					hasMessured = true;
					int width = editText.getWidth(); // 控件宽度
					int height = editText.getHeight(); // 控件高度
					if (width != 0 && height != 0) {
						int len = contents.length(); // 字数
						float px = editText.getTextSize(); // 得到字体像素
						double length = Math.floor(width / px); // 能容纳字母个数
						if (len > length) {
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
