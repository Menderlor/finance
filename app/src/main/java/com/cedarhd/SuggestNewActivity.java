package com.cedarhd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.Calendar;
import java.util.List;

/*
 * 新建投诉建议
 * @author py
 * 2014.8.14
 */

public class SuggestNewActivity extends BaseActivity implements OnClickListener {
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private DictionaryHelper dictionaryHelper;
	private Context context;
	private ProgressBar pBar;
	private ImageView ivcancel;
	private ImageView ivNew;
	private EditText etclient;
	private EditText etContent;
	private EditText etTime;
	private HorizontalScrollViewAddImage addImag_suggestinfo;
	private Button btnSpeek_suggest;
	private ImageView ivKeybord_suggest;
	String mContent = "";
	String mClient = "";
	String mReleaseTime = "";
	private static final int SHOW_WriteSuggest = -1;
	private int mYear;
	private int mMonth;
	private int mDay;
	private AddImageHelper addImageHelper;
	private List<String> photoPathList; // 要上传照片路径列表
	private static final int SHOW_DATAPICKExpirationTime = 0;
	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int RESULT_CODE_FAILED = 1;
	public static final int UPDATA_FAILED = 2;
	public static final int UPDATA_SUCCESED = 3;
	public static final int SELECT_CLIENT_CODE = 5; // 选择客户名称
	public static final int EDIT_CONTENT_CODE = 7;
	private int clientId;
	private LinearLayout ll_time_suggest;
	private DateAndTimePicker dateAndTimePicker;
	private Handler upDataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_FAILED:
				// 提示信息显示
				pBar.setVisibility(View.GONE);
				MessageUtil.ToastMessage(SuggestNewActivity.this, "发布失败！");
				break;
			case UPDATA_SUCCESED:
				// 提示信息显示
				MessageUtil.ToastMessage(SuggestNewActivity.this, "发布成功！");
				setResult(RESULT_CODE_SUCCESS);
				NoticeListActivity.isResume = true;
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_info_new);
		findviews();
		setonclick();
		Init();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			clientId = bundle.getInt("ClientInfoActivity_clientId", -1);
			if (clientId != -1) {
				etclient.setText(dictionaryHelper.getClientNameById(clientId));
				etclient.setEnabled(false);
			}
		}
	}

	void Init() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		setDateTime();
	}

	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_WriteSuggest:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认发布投诉建议吗?")
					.setCancelable(false)
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									LogUtils.i("attach", "--->"
											+ addImageHelper.sbAttachIds);
									photoPathList = addImageHelper
											.getPhotoList();
									for (String path : photoPathList) {
										LogUtils.i("attachPath", path);
									}
									if (photoPathList.size() > 0) {
										pBar.setVisibility(View.VISIBLE);
										pBar.setMax(photoPathList.size());
									}

									new Thread(new Runnable() {
										@Override
										public void run() {
											try {
												// 发布投诉建议
												mDataHelper.WriteSuggest(
														clientId, mContent,
														mReleaseTime,
														photoPathList,
														upDataHandler, pBar);
											} catch (Exception e) {
												Toast.makeText(context,
														"发布投诉建议异常",
														Toast.LENGTH_SHORT)
														.show();
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
			return alert;
		}

		return null;
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
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	private void findviews() {
		// TODO Auto-generated method stub
		context = SuggestNewActivity.this;
		dictionaryHelper = new DictionaryHelper(context);
		pBar = (ProgressBar) findViewById(R.id.progressbar_addsuggest);
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel);
		ivNew = (ImageView) findViewById(R.id.imageViewDone);
		etclient = (EditText) findViewById(R.id.et_client_suggest);
		etContent = (EditText) findViewById(R.id.et_content_suggest);
		etContent.setFocusable(false);
		etContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SuggestNewActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});
		etTime = (EditText) findViewById(R.id.et_time_suggest);
		etTime.setText(ViewHelper.getDateString());
		addImag_suggestinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_suggest);
		btnSpeek_suggest = (Button) findViewById(R.id.btn_speek_suggest);
		btnSpeek_suggest.setVisibility(View.GONE);
		btnSpeek_suggest.setOnClickListener(this);
		ivKeybord_suggest = (ImageView) findViewById(R.id.iv_keybord_suggest);
		ivKeybord_suggest.setVisibility(View.GONE);
		ivKeybord_suggest.setOnClickListener(this);
		addImageHelper = new AddImageHelper(this, SuggestNewActivity.this,
				addImag_suggestinfo, null, true);
		dateAndTimePicker = new DateAndTimePicker(context);
		// ll_time_suggest = (LinearLayout) findViewById(R.id.ll_time_suggest);
		// ll_time_suggest.setVisibility(View.GONE);
		// findViewById(R.id.view_time_suggest).setVisibility(View.GONE);
	}

	private void setonclick() {
		// TODO Auto-generated method stub
		ivcancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		ivNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!Verification_E()) {
					return;
				}
				showDialog(SHOW_WriteSuggest);
			}
		});
		etclient.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectClientName();
			}
		});
		etclient.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					selectClientName();
				}
			}
		});
		etTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// showDialog(SHOW_DATAPICKExpirationTime);
				dateAndTimePicker.showDateWheel(etTime);
			}
		});
		etTime.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// showDialog(SHOW_DATAPICKExpirationTime);
					dateAndTimePicker.showDateWheel(etTime);
				}
			}
		});
	}

	boolean Verification_E() {

		if (etclient.getText() == null
				|| etclient.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil
					.AlertMessage(SuggestNewActivity.this, "保存失败", "客户不能为空！");
			return false;
		} else {
			mClient = etclient.getText().toString().replaceAll(" ", "");
		}
		if (etTime.getText() == null
				|| etTime.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil
					.AlertMessage(SuggestNewActivity.this, "保存失败", "时间不能为空！");
			return false;
		} else {
			mReleaseTime = etTime.getText().toString();
		}
		if (etContent.getText() == null
				|| etContent.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(SuggestNewActivity.this, "保存失败",
					"投诉建议内容不能为空！");
			return false;
		} else {
			mContent = etContent.getText().toString().replaceAll(" ", "");
		}

		return true;
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch (id) {
		case R.id.btn_speek_suggest:
			new SpeechDialogHelper(context, this, etContent, true);
			break;
		case R.id.iv_keybord_suggest:
			etContent.requestFocus();
			// 弹出软键盘
			InputMethodManager m2 = (InputMethodManager) etContent.getContext()
					.getSystemService(INPUT_METHOD_SERVICE);
			m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		default:
			break;
		}
	}

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
					etclient.setText(dictionaryHelper
							.getClientNameById(clientId));
				}
			}
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				// etContent = bundle.getInt(ClientListActivity.ClientId);
				etContent
						.setText(bundle.getString(TaskContentActivity.Content));

			}
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(context, ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}
}
