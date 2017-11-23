package com.cedarhd;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
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
import java.util.UUID;

/**
 * 新建问题反馈
 * 
 * @author py 2014.8.11
 * 
 */
@SuppressLint("NewApi")
public class FeedbackNewActivity extends BaseActivity implements
		OnClickListener, OnFocusChangeListener {
	private static final int SHOW_STARTDATA = 0;
	// private static final int SHOW_ENDDATE = 1;// 完成时间
	private final int CAMERA_TAKE_REQUEST_CODE = 2;
	private final int SELECT_REQUEST_CODE = 3;
	private final int SELECT_SIGNAL_REQUEST_CODE = 31; // 单选
	public static final int RESULT_CODE_SUCCESS = 4;
	public static final int RESULT_CODE_FAILED = 5;
	public static final int SELECT_CLIENT_CODE = 6; // 选择客户名称
	private static final int CODE_SELECT_USER = 11;

	private DateAndTimePicker dateAndTimePicker;

	private static final String[] arrs = { "启动", "暂停", "完成", "搁置", "提交", "重启" };
	private List<String> status = new ArrayList<String>();
	private String photoSerialNo; // 照片序列号
	private StringBuilder sbAttachNos; // 上传照片附件号
	private List<String> photoPathList; // 要上传照片路径列表
	private String mPictureFile; // 照片文件名
	private static final String PATH = Environment
			.getExternalStorageDirectory() + "/DCIM";

	// 头像存放路径
	private String avatarPath = FilePathConfig.getAvatarDirPath();

	private int clientId;
	private SpeechRecognizer mIat;
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private Context context;
	DictionaryHelper dictionaryHelper;
	HashMap<String, Object> mTask;
	private ORMDataHelper helper;
	private AddImageHelper addImageHelper;
	private ProgressBar pBar;
	private EditText etTitle;
	private EditText etContent;
	private EditText etClient;
	private TextView tvStartDate;
	// private TextView tvEndDate;

	// private ImageView ivExecutor; // 任务执行人头像
	// private ImageView ivAddParticipant; // 参与人
	private ImageView btnCancel;
	private ImageView btnSubmit;
	// private LinearLayout llImgPaticipant; // 参与人头像区
	private HorizontalScrollViewAddImage llAddImage;
	private HandlerAvatar handler;
	// private LinearLayout llSpeek;

	/** 用于输入备注信息 */
	private Button btnSpeek2; // 说话按钮
	ImageView ivKeybord2;// 键盘输入
	// private boolean isSpeek1 = true;// 标志位 用来区分两个不同的说话按钮

	private boolean isStart; // true表示开始，false表示结束
	public String mUserSelectId = "";
	public String mUserSingleSelectId = "0";
	public String mUserSelectName = "";
	// public int states = 1;
	private int mYear;
	private int mMonth;
	private int mDay;
	// private TextView tvExecutor;
	// private TextView tvAddParticipant;
	private AvartarView avExecutor;// 执行者头像、姓名
	// private AvartarView avAddParticipant;//参与者头像、姓名
	private EditText etParticipant;// 参与者

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_info_new);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		initViews();
		setDateTime();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			if (clientId != -1) {
				etClient.setText(dictionaryHelper.getClientNameById(clientId));
				etClient.setEnabled(false);
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	public void initViews() {
		File file = new File(avatarPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		// 照片序列号，对应多张照片
		photoSerialNo = UUID.randomUUID().toString();
		sbAttachNos = new StringBuilder();
		context = FeedbackNewActivity.this;
		helper = ORMDataHelper.getInstance(FeedbackNewActivity.this);
		dictionaryHelper = new DictionaryHelper(context);
		handler = new HandlerAvatar();
		dateAndTimePicker = new DateAndTimePicker(context);

		// 初始化状态
		for (String item : arrs) {
			status.add(item);
		}
		pBar = (ProgressBar) findViewById(R.id.progressbar_addfeedbackinfo);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title_feedbackinfo);
		tvTitle.setText("新建问题反馈");
		etTitle = (EditText) findViewById(R.id.tvTitle_feedbackinfo);
		// ivExecutor = (ImageView) findViewById(R.id.ivExecutor_taskinfo2);
		// ivAddParticipant = (ImageView)
		// findViewById(R.id.ivAddParticipant_taskinfo2);
		tvStartDate = (TextView) findViewById(R.id.tvStartTime_feedbackinfo);
		etContent = (EditText) findViewById(R.id.etContent_feedbackinfo);
		etContent.setOnFocusChangeListener(onFocusAutoClearListener);
		etContent.setHint("请输入");
		etClient = (EditText) findViewById(R.id.et_client_name_feedbackinfo);
		// tvEndDate = (TextView) findViewById(R.id.tvEndTime_taskinfo);
		btnSubmit = (ImageView) findViewById(R.id.ivSubmit_feedbackinfo);
		btnCancel = (ImageView) findViewById(R.id.ivCancel_feedbackinfo);
		// llImgPaticipant = (LinearLayout)
		// findViewById(R.id.llImgParticipant_feedbackinfo);
		llAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_feedbackinfo);
		addImageHelper = new AddImageHelper(this, FeedbackNewActivity.this,
				llAddImage, null, true);
		// ivAddParticipant = (ImageView)
		// findViewById(R.id.ivAddParticipant_taskinfo1);
		etParticipant = (EditText) findViewById(R.id.et_Participant_feedbackinfo);
		// etParticipant.setOnFocusChangeListener(onFocusAutoClearListener);
		// etParticipant.setCursorVisible(false);
		// etParticipant.setFocusable(false);
		// etParticipant.setFocusableInTouchMode(false);
		// etParticipant.setOnClickListener(this);
		etParticipant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FeedbackNewActivity.this,
						User_SelectActivityNew.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mUserSelectId);
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_USER);
			}
		});
		etParticipant.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FeedbackNewActivity.this,
						User_SelectActivityNew.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mUserSelectId);
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_USER);
			}
		});

		tvStartDate.setText(ViewHelper.getDateString());// 默认起始时间为当前
		// etContent.setHint("请输入任务内容...");
		// etClient.setHint("请选择相关客户...");

		etTitle.setOnFocusChangeListener(this);
		etTitle.setOnClickListener(this);
		btnSpeek2 = (Button) findViewById(R.id.btn_speek2_feedbackinfo); // 语音
																			// 说话
		// ivSpeek2 = (ImageView) findViewById(R.id.iv_speek2_taskinfo); // 喇叭
		ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_feedbackinfo);// 键盘
		btnSpeek2.setOnClickListener(this);
		ivKeybord2.setOnClickListener(this);
		// mUserSingleSelectId = Global.mUser.Id; // 默认执行人为登录用户
		// setAvatar(Global.mUser.Id, ivExecutor);// 初始化头像
		// llImgPaticipant.setOnClickListener(this);
		// ivExecutor.setOnClickListener(this);
		// ivAddParticipant.setOnClickListener(this);
		tvStartDate.setOnClickListener(this);
		// tvEndDate.setOnClickListener(this);
		etClient.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		etClient.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					selectClientName();
				}
			}
		});
		// tvExecutor = (TextView) findViewById(R.id.tvExecutor);
		// tvAddParticipant = (TextView) findViewById(R.id.tvParticipant);
		avExecutor = (AvartarView) findViewById(R.id.Executor_feedbackinfo);
		avExecutor.setOnClickListener(this);
		// avAddParticipant = (AvartarView)
		// findViewById(R.id.Participant_feedbackinfo);
		// avAddParticipant.setOnClickListener(this);
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
		private ImageView ivAvatars;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what = msg.what;
			if (what == SHOW_IMAGE_SUCCESS) {
				ivAvatars = (ImageView) msg.obj;
				Bitmap avatar = (Bitmap) ivAvatars.getTag();
				ivAvatars.setImageBitmap(avatar);
			} else if (what == SHOW_IMAGE_FAILUREE) {
				ivAvatars = (ImageView) msg.obj;
				ivAvatars.setImageResource(R.drawable.tx);
			} else if (what == UPLOAD_PHOTO_SUCCESS) {
				// 提示信息显示
				MessageUtil.ToastMessage(context, "发布成功！");
				FeedbackListActivity.isResume = true;
				finish();
			} else if (what == UPLOAD_PHOTO_FAILURE) {
				pBar.setVisibility(View.GONE);
				FeedbackListActivity.isResume = false;
				MessageUtil.ToastMessage(context, "发布失败！");
			}
		}
	}

	/**
	 * 设置头像
	 */
	private void setAvatar(String id, final ImageView ivAvatar) {
		DictionaryHelper dictionaryHelper = new DictionaryHelper(context);
		final String avataUrl = dictionaryHelper.getUserPhoto(id); // 头像地址
		showPicture(ivAvatar, avataUrl);
	}

	/**
	 * 显示图片
	 * 
	 * @param ivAvatar
	 * @param avataUrl
	 */
	private void showPicture(final ImageView ivAvatar, final String avataUrl) {
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

							LogUtils.i("kjx211", "开启线程从网络下载");
							// httpUtils.downloadData(avataUrl, handler,
							// ivAvatar);
						}
					}).start();
				}
			} else {
				// 开启线程从网络下载
				final HttpUtils httpUtils = new HttpUtils();
				new Thread(new Runnable() {
					@Override
					public void run() {

						LogUtils.i("kjx211", "88888888888");

						try {
							httpUtils.downloadData(avataUrl, handler, ivAvatar);
						} catch (Exception e) {
							Toast.makeText(context, "下载网络数据异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		} else {
			ivAvatar.setImageResource(R.drawable.tx);
		}
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
		// TextView tvText = isStart ? tvStartDate : tvEndDate;
		// tvText.setBackgroundColor(0xffffff);
		tvStartDate.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkValid() {
		if (etTitle.getText() == null
				|| etTitle.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(FeedbackNewActivity.this, "保存失败",
					"标题不能为空！");
			return false;
		} else if (etTitle.getText().toString().trim().length() > 50) {
			MessageUtil.AlertMessage(FeedbackNewActivity.this, "保存失败",
					"标题不能多于50个字！");
			return false;
		}
		// 判断接收人是否为空
		if (TextUtils.isEmpty(mUserSelectId)) {

		}

		if (tvStartDate.getText().toString().contains("时间")
				|| tvStartDate.getText().toString().replaceAll(" ", "")
						.length() <= 0) {
			MessageUtil.AlertMessage(FeedbackNewActivity.this, "保存失败",
					"请选择开始时间！");
			return false;
		}

		// if (tvEndDate.getText() == null
		// || tvEndDate.getText().toString().replaceAll(" ", "").length() <= 0)
		// {
		// MessageUtil.AlertMessage(TaskNewActivity.this, "保存失败", "请选择结束时间！");
		// return false;
		// }
		return true;
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
		// case R.id.llImgParticipant_feedbackinfo: // 选择参与人
		// case R.id.et_Participant_feedbackinfo:
		// Intent intent = new Intent(FeedbackNewActivity.this,
		// User_SelectActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("UserSelectId", mUserSelectId);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, CODE_SELECT_USER);
		// break;
		// case R.id.Participant_feedbackinfo:
		// addParticipant();
		// break;
		case R.id.tvStartTime_feedbackinfo:
			// isStart = true;
			// showDialog(SHOW_STARTDATA);
			// setDateTime();
			dateAndTimePicker.showDateWheel(tvStartDate);
			break;
		// case R.id.tvEndTime_taskinfo:
		// isStart = false;
		// showDialog(SHOW_ENDDATE);
		// setDateTime();
		// break;
		case R.id.et_client_name_feedbackinfo:
			selectClientName();
			break;
		case R.id.ivSubmit_feedbackinfo:
			// 提交
			if (checkValid()) {
				submit();
			}
			break;
		case R.id.btn_speek2_feedbackinfo: // 点击说话
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
		default:
			break;
		}
	}

	private void submit() {
		LogUtils.i("tag", "isCheckValid");
		ORMDataHelper helper = ORMDataHelper.getInstance(this);
		String title = etTitle.getText().toString();
		// TODO
		// String participant = etParticipant.getText().toString();
		String startDate = tvStartDate.getText().toString();
		// String endDate = tvEndDate.getText().toString();
		String content = etContent.getText().toString();
		final 问题反馈 item = new 问题反馈();
		item.Participant = mUserSelectId;
		item.Executor = Integer.parseInt(mUserSingleSelectId);
		item.Title = title;
		item.Content = content;
		item.Time = startDate;
		item.AssignTime = startDate;
		item.ClientId = clientId;
		item.Status = 1;
		// final HashMap<String, Object> map = new HashMap<String,
		// Object>();
		// // map.put("Publisher",
		// Global.mUser.IdGlobal.mUser.IdGlobal.mUser.Id //
		// map.put("Participant", mUserSelectId); // 参与人id
		// map.put("Title", title);
		// map.put("Content", content);
		// map.put("Time", startDate); // 发布时间
		// map.put("AssignTime", endDate); // 完成时间
		// map.put("Status", 1); // 状态默认为“启动”
		// map.put("Attachment", addImageHelper.sbAttachIds);
		LogUtils.i("attach", "--->" + addImageHelper.sbAttachIds);
		// map.put(key, value)
		// item.photoNo = photoSerialNo;
		try {
			// final Dao<任务, Integer> dao = helper.getDao(任务.class);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认发布问题反馈吗?")
					.setCancelable(false)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// TODO 上传服务器：问题反馈内容、图片
									// Boolean result = mDataHelper
									// .PublishTask(map);
									// 要上传图片的路径
									photoPathList = addImageHelper
											.getPhotoList();
									for (String path : photoPathList) {
										LogUtils.i("attachPath", path);
									}
									if (photoPathList.size() > 0) {
										pBar.setVisibility(View.VISIBLE);
										pBar.setMax(photoPathList.size());
									}
									// dialog.cancel();
									dialog.dismiss();
									new Thread(new Runnable() {
										@Override
										public void run() {
											mDataHelper.PublishFeedback(item,
													photoPathList, handler,
													pBar);
										}
									}).start();
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		// bundle.putString("UserSelectId", mUserSelectId);
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_SIGNAL_REQUEST_CODE);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_STARTDATA:
			return new DatePickerDialog(this, mDateExpirationTimeSetListener,
					mYear, mMonth, mDay);
			// case SHOW_ENDDATE:
			// return new DatePickerDialog(this, mDateExpirationTimeSetListener,
			// mYear, mMonth, mDay);
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
				// / setDateTime();
				dateAndTimePicker.showDateWheel(tvStartDate);
				break;
			// case R.id.etEndTime_addtask:
			// isStart = false;
			// showDialog(SHOW_ENDDATE);
			// setDateTime();
			// break;
			// case R.id.tvTitle_taskinfo: // 标题
			// new SpeechDialogHelper(context, this, etTitle, false);
			// break;
			default:
				break;
			}
		}
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
				// 选择接收人
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
			if (requestCode == SELECT_REQUEST_CODE) {// 选择员工姓名
														// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				LogUtils.i("kjx21", "------>" + mUserSelectId);
				mUserSelectName = bundle.getString("UserSelectName");
				// // 参与者
				// etParticipant.setText(mUserSelectName);
				// etParticipant.setTag(false);
				// LogUtils.i("testKeno", mUserSelectId + "======" +
				// mUserSelectName);
				// //显示执行人的名字
				// tvExecutor.setText(mUserSelectName);
				// TODO 显示执行人头像
				// ivAddParticipant.
				// if (!TextUtils.isEmpty(mUserSelectId)) {
				// String[] arr = dictionaryHelper
				// .getUserIdArray(mUserSelectId);
				// LinearLayout.LayoutParams params = new
				// LinearLayout.LayoutParams(
				// Math.round(ViewHelper.dip2px(context, 60)),
				// Math.round(ViewHelper.dip2px(context, 70)));
				// params.rightMargin = Math.round(ViewHelper.dip2px(context,
				// 3));
				// llImgPaticipant.removeAllViews(); // 移除之前所有控件
				// if (arr.length == 0) {
				// AvartarView img = new AvartarView(context);
				// img.setLayoutParams(params);
				// new AvartarViewHelper(context, mUserSelectId,
				// avAddParticipant, true);
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
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(FeedbackNewActivity.this,
				ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	public static OnFocusChangeListener onFocusAutoClearListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			EditText textView = (EditText) v;
			String hint;
			if (hasFocus) {
				hint = textView.getHint().toString();
				textView.setTag(hint);
				textView.setHint("");
			} else {
				hint = textView.getTag().toString();
				textView.setHint(hint);
			}
		}
	};

}
