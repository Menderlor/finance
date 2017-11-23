package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.adapter.TaskClassifyListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper;
import com.cedarhd.helpers.DictionaryQueryDialogHelper.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.models.任务;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;
import com.iflytek.cloud.SpeechRecognizer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 新建任务
 * 
 * @author bohr
 * 
 */
public class TaskNewActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {
	private static final int SHOW_STARTDATA = 0;
	// private static final int SHOW_ENDDATE = 1;// 完成时间
	private final int CAMERA_TAKE_REQUEST_CODE = 2;
	private final int SELECT_REQUEST_CODE = 3;
	private final int SELECT_SIGNAL_REQUEST_CODE = 31; // 单选
	public static final int RESULT_CODE_SUCCESS = 4;
	public static final int RESULT_CODE_FAILED = 5;
	public static final int SELECT_CLIENT_CODE = 6; // 选择客户名称
	public static final int EDIT_CONTENT_CODE = 7; // 编辑任务内容
	private HandlerAvatar handler = new HandlerAvatar();

	private DateAndTimePicker dateAndTimePicker;

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
	DictionaryQueryDialogHelper dictQueryHelper;
	HashMap<String, Object> mTask;
	private ORMDataHelper helper;
	private AddImageHelper addImageHelper;
	private ProgressBar pBar;
	private EditText etContent;
	private EditText etClient;
	private TextView tvStartDate;
	private ImageView btnCancel;
	private ImageView btnSubmit;
	private LinearLayout llImgPaticipant; // 参与人头像区
	private ImageView ivClearPaticipant; // 清空参与人
	private HorizontalScrollViewAddImage llAddImage;
	/** 点赞发钻石布局 */
	private LinearLayout ll_support_layout;

	/** 用于输入备注信息 */
	private boolean isStart; // true表示开始，false表示结束
	public String mUserSelectId = "";

	/** 执行人编号 */
	public String mExecuteId = "0";
	public String mExecuteName = "0";

	/** 参与人姓名 */
	public String mPaticipantName = "";
	// private TextView tvExecutor;
	// private TextView tvAddParticipant;
	private AvartarView avExecutor;// 执行者头像、姓名
	private TextView tvExecutor;
	// private AvartarView avAddParticipant;// 参与者头像、姓名
	private LinearLayout ll_classify;
	private LinearLayout ll_client;
	private TextView iv_classify;
	private TextView iv_client;
	private TextView tv_classify;
	private TextView tv_client;
	private TextView tvStatus; // 任务状态
	private LinearLayout rlDiscuss; // 评论按钮功能区
	private EditText etParticipant;// 参与者
	private static final int CODE_SELECT_USER = 11;
	private TaskClassifyListViewAdapter adapter;

	private HandlerNewContact mHandlerNewContact = new HandlerNewContact();
	private ZLServiceHelper zlServiceHelper;
	private int classifyid;
	private String permissions = "";

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		// public static final int GET_LOG_NOW_SUCCESS = 0;
		// public static final int GET_LOG_NOW_FAILED = 1;
		public static final int UPDATE_TASK_SUCCESS = 2;
		public static final int UPDATE_TASK_FAILED = 3;
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败
		public static final int GET_LOAD_PERMISSION = 9; // 获得权限

		private final int GET_STATE_SUCCESS = 17; // 获得任务分类列表成功
		private final int GET_STATE_FAILED = 18; // 获得任务分类列表失败

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GET_LOAD_PERMISSION:// 获取任务分类列表成功
				permissions = (String) msg.obj;
				if (permissions != null) {
					if (permissions.contains("14")) {
						// TODO
						// ll_client.setVisibility(View.VISIBLE);
					}
				}
				break;
			case GET_STATE_FAILED:// 获取任务状态列表失败
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_info_new);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		initViews();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			if (clientId != -1) {
				etClient.setText(dictionaryHelper.getClientNameById(clientId));
				etClient.setEnabled(false);
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String data = zlServiceHelper.GetPermissions();
					Message msg = handler.obtainMessage();
					msg.obj = data;
					msg.what = HandlerAvatar.SUCESS_GET_PERMISSION;
					handler.sendMessage(msg);
				} catch (Exception e) {
					LogUtils.e("NoticeList", "" + e);
				}
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	public void initViews() {
		context = TaskNewActivity.this;
		zlServiceHelper = new ZLServiceHelper();

		dictQueryHelper = DictionaryQueryDialogHelper.getInstance(context);
		File file = new File(avatarPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// 照片序列号，对应多张照片
		photoSerialNo = UUID.randomUUID().toString();
		sbAttachNos = new StringBuilder();
		helper = ORMDataHelper.getInstance(TaskNewActivity.this);
		dictionaryHelper = new DictionaryHelper(context);
		dateAndTimePicker = new DateAndTimePicker(context);
		pBar = (ProgressBar) findViewById(R.id.progressbar_addtask2);
		tvStatus = (TextView) findViewById(R.id.tv_state_taskinfo2);
		TextView tvTitle = (TextView) findViewById(R.id.tv_title_taskinfo2);
		tvTitle.setText("新建任务");
		// ivExecutor = (ImageView) findViewById(R.id.ivExecutor_taskinfo2);
		// ivAddParticipant = (ImageView)
		// findViewById(R.id.ivAddParticipant_taskinfo2);
		// 新建时隐藏发钻 点赞功能
		ll_support_layout = (LinearLayout) findViewById(R.id.ll_support_task_info);
		ll_support_layout.setVisibility(View.GONE);

		ivClearPaticipant = (ImageView) findViewById(R.id.iv_clear_paticipant_task_new);
		ivClearPaticipant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivClearPaticipant.setVisibility(View.GONE);
				// 清空参与人
				mUserSelectId = "";
				mPaticipantName = "";
				// 选择接收人
				etParticipant.setText(mPaticipantName);
			}
		});

		tvStartDate = (TextView) findViewById(R.id.tvStartTime_taskinfo2);
		etContent = (EditText) findViewById(R.id.etContent_taskinfo2);
		etContent.setHint("请输入");
		etContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String content = etContent.getText().toString();
				Intent intent = new Intent(TaskNewActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				intent.putExtra(TaskContentActivity.Content, content + "");
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});
		etClient = (EditText) findViewById(R.id.et_client_name_taskinfo);
		TextView tvPublisher = (TextView) findViewById(R.id.tv_publiser_taskinfo2);
		tvPublisher.setText(Global.mUser.UserName + "");
		// tvEndDate = (TextView) findViewById(R.id.tvEndTime_taskinfo);
		btnSubmit = (ImageView) findViewById(R.id.ivSubmit_taskinfo2);
		btnCancel = (ImageView) findViewById(R.id.ivCancel_taskinfo2);
		llImgPaticipant = (LinearLayout) findViewById(R.id.llImgParticipant_taskinfo2);
		llAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_taskinfo2);
		addImageHelper = new AddImageHelper(this, TaskNewActivity.this,
				llAddImage, null, true);

		tvStartDate.setText(ViewHelper.getDateString());// 默认起始时间为当前
		// tvStartDate.setText(DateDeserializer.getFormatTime(ViewHelper.getDateString()));
		// etContent.setHint("请输入任务内容...");
		// etClient.setHint("请选择相关客户...");

		tvStatus.setOnClickListener(this);
		// btnSpeek2 = (Button) findViewById(R.id.btn_speek2_taskinfo2); // 语音
		// 说话
		// ivSpeek2 = (ImageView) findViewById(R.id.iv_speek2_taskinfo); // 喇叭
		// ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_taskinfo2);//
		// 键盘
		// btnSpeek2.setOnClickListener(this);
		// ivKeybord2.setOnClickListener(this);
		// mUserSingleSelectId = Global.mUser.Id; // 默认执行人为登录用户
		// setAvatar(Global.mUser.Id, ivExecutor);// 初始化头像
		llImgPaticipant.setOnClickListener(this);
		// ivExecutor.setOnClickListener(this);
		// ivAddParticipant.setOnClickListener(this);
		tvStartDate.setOnClickListener(this);
		// tvEndDate.setOnClickListener(this);
		etClient.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		tvStatus.setOnClickListener(this);
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
		avExecutor = (AvartarView) findViewById(R.id.Executor_taskinfo_new2);
		tvExecutor = (TextView) findViewById(R.id.tv_executor_taskinfo_new2);
		avExecutor.setOnClickListener(this);
		tvExecutor.setOnClickListener(this);
		// 初始化执行人为当前登录用户
		mExecuteId = Global.mUser.Id;
		// new AvartarViewHelper(context, mExecuteId, avExecutor, true);
		avExecutor.setVisibility(View.GONE);
		tvExecutor.setText(dictionaryHelper.getUserNameById(mExecuteId) + "");

		// avAddParticipant = (AvartarView)
		// findViewById(R.id.Participant_taskinfo_new2);
		// avAddParticipant.setOnClickListener(this);
		ll_classify = (LinearLayout) findViewById(R.id.ll_classify_task_info);
		ll_client = (LinearLayout) findViewById(R.id.ll_client);
		iv_classify = (TextView) findViewById(R.id.iv_task_info_classify);
		iv_client = (TextView) findViewById(R.id.iv_task_info_client);
		tv_classify = (TextView) findViewById(R.id.tv_task_info_classify);
		tv_client = (TextView) findViewById(R.id.tv_task_info_client);
		ll_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectClientName();
			}
		});
		iv_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectClientName();
			}
		});
		tv_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectClientName();
			}
		});
		rlDiscuss = (LinearLayout) findViewById(R.id.rl_root_publish_discuss_task_info);
		rlDiscuss.setVisibility(View.GONE);
		etParticipant = (EditText) findViewById(R.id.et_Participant_taskinfo);
		etParticipant.setInputType(InputType.TYPE_NULL);
		etParticipant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TaskNewActivity.this,
						User_SelectActivityNew_zmy.class);
				Bundle bundle = new Bundle();
				bundle.putString("UserSelectId", mUserSelectId);
				intent.putExtras(bundle);
				startActivityForResult(intent, CODE_SELECT_USER);
			}
		});
		etParticipant.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					Intent intent = new Intent(TaskNewActivity.this,
							User_SelectActivityNew_zmy.class);
					Bundle bundle = new Bundle();
					bundle.putString("UserSelectId", mUserSelectId);
					intent.putExtras(bundle);
					startActivityForResult(intent, CODE_SELECT_USER);
				}
			}
		});

		ll_classify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dictQueryHelper.show("项目管理");
				dictQueryHelper.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(字典 dict) {
						tv_classify.setText(dict.getName() + "");
						tv_classify.setTag(dict.getId());
					}
				});
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("globalTag", Global.mUser);
		LogUtils.i("lifeState", "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (Global.mUser == null) {
			Global.mUser = (User) savedInstanceState
					.getSerializable("globalTag");
		}
		LogUtils.i("lifeState", "onRestoreInstanceState");
		LogUtils.i("lifeState", Global.mUser.Passport);
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
		public static final int SUCESS_GET_PERMISSION = 7; // 获得模块权限
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
				ProgressDialogHelper.dismiss();
				// 提示信息显示
				MessageUtil.ToastMessage(context, "发布成功！");
				// TaskListActivityNew.isResume = true;
				setResult(RESULT_OK);
				finish();
			} else if (what == UPLOAD_PHOTO_FAILURE) {
				ProgressDialogHelper.dismiss();
				pBar.setVisibility(View.GONE);
				TaskListActivityNew.isResume = false;
				MessageUtil.ToastMessage(context, "发布失败！");
			} else if (msg.what == SUCESS_GET_PERMISSION) {
				permissions = (String) msg.obj;
				if (permissions != null) {
					if (permissions.contains("14")) {
						// TODO
						// ll_client.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkValid() {
		if (etContent.getText() == null
				|| etContent.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(TaskNewActivity.this, "保存失败", "内容不能为空！");
			return false;
		} else if (etContent.getText().toString().trim().length() > 250) {
			MessageUtil.AlertMessage(TaskNewActivity.this, "保存失败",
					"内容不能多于250个字！");
			return false;
		}
		// 判断接收人是否为空
		if (TextUtils.isEmpty(mUserSelectId)) {
		}

		if (tvStartDate.getText().toString().contains("时间")
				|| tvStartDate.getText().toString().replaceAll(" ", "")
						.length() <= 0) {
			MessageUtil.AlertMessage(TaskNewActivity.this, "保存失败", "请选择开始时间！");
			return false;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivCancel_taskinfo2: // 取消
			finish();
			break;
		case R.id.Executor_taskinfo_new2: // 选择执行人
		case R.id.tv_executor_taskinfo_new2:
			addExecutor();
			break;
		case R.id.llImgParticipant_taskinfo2: // 选择参与人
			// case R.id.Participant_taskinfo_new2:
			// addParticipant();
			// break;
		case R.id.tvStartTime_taskinfo2:
			// isStart = true;
			// showDialog(SHOW_STARTDATA);
			// setDateTime();

			// dateAndTimePicker.showDateAndTimeDialog(tvStartDate);

			dateAndTimePicker.showDateWheel(tvStartDate);

			break;
		// case R.id.tvEndTime_taskinfo:
		// isStart = false;
		// showDialog(SHOW_ENDDATE);
		// setDateTime();
		// break;
		case R.id.et_client_name_taskinfo:
			selectClientName();
			break;
		case R.id.ivSubmit_taskinfo2:
			// 提交
			if (checkValid()) {
				submit();
			}
			break;
		case R.id.tv_state_taskinfo2: // 状态
			dictQueryHelper.show("任务状态");
			dictQueryHelper.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(字典 dict) {
					tvStatus.setText(dict.getName() + "");
					tvStatus.setTag(dict.getId());
				}
			});
			break;
		default:
			break;
		}
	}

	private void submit() {
		LogUtils.i("tag", "isCheckValid");
		// String participant = etParticipant.getText().toString();
		String startDate = tvStartDate.getText().toString();
		// String endDate = tvEndDate.getText().toString();
		String content = etContent.getText().toString();
		final 任务 item = new 任务();
		item.Participant = mUserSelectId;
		if (!TextUtils.isEmpty(mExecuteId) && mExecuteId.contains(",")) {
			item.ExecutorList = mExecuteId;
		} else {
			item.Executor = Integer.parseInt(mExecuteId);
		}
		item.Content = content;
		item.Time = startDate;
		try {
			item.Status = Integer.parseInt(tvStatus.getTag().toString());
		} catch (Exception e) {
			item.Status = 1;
		}
		try {
			item.Categroy = Integer.parseInt(tv_classify.getTag().toString());
		} catch (Exception e) {
		}
		item.CategroyName = tv_classify.getText().toString();
		// item.Categroy = classifyid;
		LogUtils.i("pyid", classifyid + "");
		item.AssignTime = startDate;
		LogUtils.i("pynewtime", item.AssignTime);
		item.ClientId = clientId;
		item.Status = 1;
		LogUtils.i("attach", "--->" + addImageHelper.sbAttachIds);
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认发布任务吗?")
					.setCancelable(false)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									photoPathList = addImageHelper
											.getPhotoList();
									for (String path : photoPathList) {
										LogUtils.i("attachPath", path);
									}
									if (photoPathList.size() > 0) {
										pBar.setVisibility(View.VISIBLE);
										pBar.setMax(photoPathList.size());
									}
									dialog.dismiss();

									ProgressDialogHelper.show(context);
									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												mDataHelper.PublishTask(item,
														photoPathList, handler,
														pBar);
											} catch (Exception e) {
												handler.sendEmptyMessage(handler.UPLOAD_PHOTO_FAILURE);
											}
										}
									}).start();
								}
							})
					.setNegativeButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加执行人
	 */
	private void addExecutor() {
		Intent intent = new Intent(this, User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putString("UserSelectId", mExecuteId);
		// bundle.putString("UserSelectId", mUserSelectId);
		// bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true);
		// // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_SIGNAL_REQUEST_CODE);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (hasFocus) {
			switch (id) {
			case R.id.etStartTime_addtask:
				dateAndTimePicker.showDateWheel(tvStartDate);
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
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				etContent
						.setText(bundle.getString(TaskContentActivity.Content));

			}
			if (requestCode == SELECT_SIGNAL_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
				// mUserSingleSelectExecuteId =
				// bundle.getString("UserSelectId");
				// mUserSingleSelectExecuteId = mUserSingleSelectExecuteId
				// .replace("'", "").replace(";", "");
				// LogUtils.i("kjx21", "sigal------>" +
				// mUserSingleSelectExecuteId);
				// // setAvatar(mUserSingleSelectId, ivExecutor);
				// new AvartarViewHelper(context, mUserSingleSelectExecuteId,
				// avExecutor, true);
				// tvExecutor.setText(dictionaryHelper
				// .getUserNameById(mUserSingleSelectExecuteId) + "");

				mExecuteId = bundle.getString("UserSelectId").replace("'", "")
						.replace(";", ",");
				mExecuteName = bundle.getString("UserSelectName");
				LogUtils.i("testKeno执行人", mUserSelectId + "======"
						+ mPaticipantName);
				if (!TextUtils.isEmpty(mExecuteId)) {
					// 选择执行人
					tvExecutor.setText(mExecuteName);
					tvExecutor.setTag(false);
					LogUtils.i("testKeno执行人", mExecuteId + "======"
							+ mPaticipantName);
				}
			}
			if (requestCode == SELECT_CLIENT_CODE) {
				// 取出客户名称字符串
				Bundle bundle = data.getExtras();
				clientId = bundle.getInt(ClientListActivity.ClientId);
				LogUtils.i("kjxi", "clientId:" + clientId);
				if (clientId != 0) {
					tv_client.setText(dictionaryHelper
							.getClientNameById(clientId));
				}
			}
			if (requestCode == SELECT_REQUEST_CODE) {// 选择员工姓名
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				LogUtils.i("kjx21", "------>" + mUserSelectId);
				mPaticipantName = bundle.getString("UserSelectName");
			}

			if (requestCode == CODE_SELECT_USER) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				mPaticipantName = bundle.getString("UserSelectName");
				if (!TextUtils.isEmpty(mUserSelectId)) {
					// 选择接收人
					etParticipant.setText(mPaticipantName);
					etParticipant.setTag(false);
					ivClearPaticipant.setVisibility(View.VISIBLE);
					LogUtils.i("testKeno", mUserSelectId + "======"
							+ mPaticipantName);
				}
			}
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(TaskNewActivity.this,
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
