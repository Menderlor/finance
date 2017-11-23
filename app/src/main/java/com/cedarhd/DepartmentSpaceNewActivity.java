package com.cedarhd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.lt论坛版块;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.Calendar;
import java.util.List;

/*
 * @author py 2014.8.25
 */
public class DepartmentSpaceNewActivity extends BaseActivity implements
		OnClickListener {
	private ImageView ivcancel;
	private ImageView ivnew;
	private ProgressBar pBar;
	private EditText etclassify;
	// EditText mEditTextReceiverName;
	EditText mEditTextTitle;
	EditText mEditTextContent;
	private lt论坛版块 ltitems;
	private String[] classList = { "所有版块", "公司文化", "规章制度", "图书馆", "新闻", "人事档案",
			"合同档案", "散热器实施图集", "波尔云技术文档", "安装", "web开发技术", "散热器ERP", "售后技术问题",
			"操作视频", "订单模块", "生产管理模块", "会议纪要", "销售部", "研发部", "晨会记要", "周会",
			"销售部文档", "产品报价单", "散热器营销核算", "公司制度单据模版", "手机开发技术", "部门版块", "测试",
			"新建模块1", "培训资料", "新建模块", "新建模", "采集器使用文档", "消防培训", "佛罗伦萨" };
	private int classIndex;
	private int board = 0;
	LinearLayout llReceiver;
	private HorizontalScrollViewAddImage addImg_departmentspaceinfo;
	private AddImageHelper addImageHelper;
	private List<String> photoPathList; // 要上传照片路径列表
	private Button btnSpeek2; // 说话按钮
	ImageView ivKeybord2;// 键盘输入
	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int RESULT_CODE_FAILED = 1;
	public static final int UPDATA_FAILED = 2;
	public static final int UPDATA_SUCCESED = 3;
	String mTitle = "";
	String mContent = "";
	String mReleaseTime = "";
	String mClassify = "";

	// public String mUserSelectId = "";
	// public String mUserSelectName = "";

	private static final int SHOW_DATAPICKExpirationTime = 0;
	private static final int SHOW_WriteCompanySpace = -1;
	private static final int CODE_SELECT_USER = 11;

	private int mYear;
	private int mMonth;
	private int mDay;
	private Context context;
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private Handler upDataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_FAILED:
				// 提示信息显示
				pBar.setVisibility(View.GONE);
				MessageUtil.ToastMessage(DepartmentSpaceNewActivity.this,
						"发布失败！");
				break;
			case UPDATA_SUCCESED:
				// 提示信息显示
				MessageUtil.ToastMessage(DepartmentSpaceNewActivity.this,
						"发布成功！");
				setResult(RESULT_CODE_SUCCESS);
				CompanySpaceListActivity.isResume = true;
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentspace_info_new);
		findviews();
		setonclick();
		Init();
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

	private void findviews() {
		// 初始化控件
		context = DepartmentSpaceNewActivity.this;
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel);
		ivnew = (ImageView) findViewById(R.id.imageViewDone);
		LinearLayout ll_publisher = (LinearLayout) findViewById(R.id.ll_publiser);
		findViewById(R.id.view_line_publiser).setVisibility(View.GONE);
		findViewById(R.id.ll_publish_time_departmentspace_info_new)
				.setVisibility(View.GONE);
		findViewById(R.id.view_publish_time_departmentspace_info_new)
				.setVisibility(View.GONE);
		llReceiver = (LinearLayout) findViewById(R.id.ll_receiver_departmentspace_info);

		ll_publisher.setVisibility(View.GONE); // 默认发布人为当前用户，隐藏
		((TextView) findViewById(R.id.tv_departmentspace_info_title))
				.setText("新建帖子");
		pBar = (ProgressBar) findViewById(R.id.progressbar_adddepartmentspace);
		mEditTextTitle = (EditText) findViewById(R.id.editTextTitle);
		mEditTextTitle.setOnFocusChangeListener(onFocusAutoClearListener);
		mEditTextContent = (EditText) findViewById(R.id.editTextContent);
		mEditTextContent.setOnFocusChangeListener(onFocusAutoClearListener);
		// mEditTextReceiverName = (EditText)
		// findViewById(R.id.editTextReceiverName);
		// mEditTextReceiverName
		// .setOnFocusChangeListener(onFocusAutoClearListener);
		addImg_departmentspaceinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_companyspace);
		addImageHelper = new AddImageHelper(this,
				DepartmentSpaceNewActivity.this, addImg_departmentspaceinfo,
				null, true);
		btnSpeek2 = (Button) findViewById(R.id.btn_speek2_departmentspace); // 语音
																			// 说话
		ivKeybord2 = (ImageView) findViewById(R.id.iv_keybord2_departmentspace);// 键盘
		btnSpeek2.setOnClickListener(this);
		ivKeybord2.setOnClickListener(this);
		mEditTextContent.setHeight(100);
		mEditTextContent.setHint("请输入内容..");

		// mEditTextReceiverName.setCursorVisible(false);
		// mEditTextReceiverName.setFocusable(false);
		// mEditTextReceiverName.setFocusableInTouchMode(false);
		etclassify = (EditText) findViewById(R.id.et_classify);
		mReleaseTime = ViewHelper.getDateString();// 当前时间
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	private void setonclick() {
		// 添加监听事件
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		ivnew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!Verification_E()) {
					return;
				}
				showDialog(SHOW_WriteCompanySpace);
			}
		});
		mEditTextTitle.setOnClickListener(this);
		mEditTextTitle
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							new SpeechDialogHelper(context,
									DepartmentSpaceNewActivity.this,
									mEditTextTitle, false);
						}
					}
				});

		// mEditTextReceiverName.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(CompanySpaceNewActivity.this,
		// User_SelectActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putString("UserSelectId", mUserSelectId);
		// intent.putExtras(bundle);
		// startActivityForResult(intent, CODE_SELECT_USER);
		// }
		// });
		etclassify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				classsify();
			}
		});
		etclassify.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					classsify();
				}

			}
		});
	}

	private void classsify() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(DepartmentSpaceNewActivity.this)
				.setItems(classList, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						switch (arg1) {
						case 0:
							etclassify.setText(classList[0]);
							classIndex = 0;
							board = 1;
							break;
						case 1:
							etclassify.setText(classList[1]);
							classIndex = 1;
							board = 2;
							break;
						case 2:
							etclassify.setText(classList[2]);
							classIndex = 2;
							board = 3;
							break;
						case 3:
							etclassify.setText(classList[3]);
							classIndex = 3;
							board = 4;
							break;
						case 4:
							etclassify.setText(classList[4]);
							classIndex = 4;
							board = 5;
							break;
						case 5:
							etclassify.setText(classList[5]);
							classIndex = 5;
							board = 6;
							break;
						case 6:
							etclassify.setText(classList[6]);
							classIndex = 6;
							board = 7;
							break;
						case 7:
							etclassify.setText(classList[7]);
							classIndex = 7;
							board = 22;
							break;
						case 8:
							etclassify.setText(classList[8]);
							classIndex = 8;
							board = 23;
							break;
						case 9:
							etclassify.setText(classList[9]);
							classIndex = 9;
							board = 24;
							break;
						case 10:
							etclassify.setText(classList[10]);
							classIndex = 10;
							board = 25;
							break;
						case 11:
							etclassify.setText(classList[11]);
							classIndex = 11;
							board = 26;
							break;
						case 12:
							etclassify.setText(classList[12]);
							classIndex = 12;
							board = 27;
							break;
						case 13:
							etclassify.setText(classList[13]);
							classIndex = 13;
							board = 28;
							break;
						case 14:
							etclassify.setText(classList[14]);
							classIndex = 14;
							board = 29;
							break;
						case 15:
							etclassify.setText(classList[15]);
							classIndex = 15;
							board = 30;
							break;
						case 16:
							etclassify.setText(classList[16]);
							classIndex = 16;
							board = 31;
							break;
						case 17:
							etclassify.setText(classList[17]);
							classIndex = 17;
							board = 32;
							break;
						case 18:
							etclassify.setText(classList[18]);
							classIndex = 18;
							board = 33;
							break;
						case 19:
							etclassify.setText(classList[19]);
							classIndex = 19;
							board = 34;
							break;
						case 20:
							etclassify.setText(classList[20]);
							classIndex = 20;
							board = 35;
							break;
						case 21:
							etclassify.setText(classList[21]);
							classIndex = 21;
							board = 37;
							break;
						case 22:
							etclassify.setText(classList[22]);
							classIndex = 22;
							board = 38;
							break;
						case 23:
							etclassify.setText(classList[23]);
							classIndex = 23;
							board = 39;
							break;
						case 24:
							etclassify.setText(classList[24]);
							classIndex = 24;
							board = 42;
							break;
						case 25:
							etclassify.setText(classList[25]);
							classIndex = 25;
							board = 43;
							break;
						case 26:
							etclassify.setText(classList[26]);
							classIndex = 26;
							board = 44;
							break;
						case 27:
							etclassify.setText(classList[27]);
							classIndex = 27;
							board = 46;
							break;
						case 28:
							etclassify.setText(classList[28]);
							classIndex = 28;
							board = 53;
							break;
						case 29:
							etclassify.setText(classList[29]);
							classIndex = 29;
							board = 54;
							break;
						case 30:
							etclassify.setText(classList[30]);
							classIndex = 30;
							board = 82;
							break;
						case 31:
							etclassify.setText(classList[31]);
							classIndex = 31;
							board = 84;
							break;
						case 32:
							etclassify.setText(classList[32]);
							classIndex = 32;
							board = 85;
							break;
						case 33:
							etclassify.setText(classList[33]);
							classIndex = 33;
							board = 86;
							break;
						case 34:
							etclassify.setText(classList[34]);
							classIndex = 34;
							board = 90;
							break;
						default:
							break;
						}
					}

				}).create().show();
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
		case R.id.editTextTitle:
			new SpeechDialogHelper(context, this, mEditTextTitle, false);
			break;
		case R.id.btn_speek2_departmentspace:
			new SpeechDialogHelper(context, this, mEditTextContent, true);
			break;
		case R.id.iv_keybord2_departmentspace:
			// ivKeybord2.setVisibility(View.GONE);
			// btnSpeek2.setVisibility(View.GONE);
			// mEditTextContent.setVisibility(View.VISIBLE);
			// ivSpeek2.setVisibility(View.VISIBLE);
			mEditTextContent.requestFocus();
			// 弹出软键盘
			InputMethodManager m2 = (InputMethodManager) mEditTextTitle
					.getContext().getSystemService(INPUT_METHOD_SERVICE);
			m2.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		default:
			break;
		}
	}

	boolean Verification_E() {
		if (mEditTextTitle.getText() == null
				|| mEditTextTitle.getText().toString().replaceAll(" ", "")
						.length() <= 0) {
			MessageUtil.AlertMessage(DepartmentSpaceNewActivity.this, "保存失败",
					"标题不能为空！");
			return false;
		} else if (mEditTextTitle.getText().toString().trim().length() > 50) {
			MessageUtil.AlertMessage(DepartmentSpaceNewActivity.this, "保存失败",
					"标题不能多于50个字！");
			return false;
		} else {
			mTitle = mEditTextTitle.getText().toString().replaceAll(" ", "");
		}

		if (mEditTextContent.getText() == null
				|| mEditTextContent.getText().toString().replaceAll(" ", "")
						.length() <= 0) {
			MessageUtil.AlertMessage(DepartmentSpaceNewActivity.this, "保存失败",
					"通知内容不能为空！");
			return false;
		} else {
			mContent = mEditTextContent.getText().toString()
					.replaceAll(" ", "");
		}
		if (etclassify.getText() == null
				|| etclassify.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(DepartmentSpaceNewActivity.this, "保存失败",
					"通知内容不能为空！");
			return false;
		} else {
			mClassify = etclassify.getText().toString().replaceAll(" ", "");
		}

		// if (mUserSelectId.replaceAll(" ", "").length() <= 0) {
		// MessageUtil.AlertMessage(CompanySpaceNewActivity.this, "保存失败",
		// "接收人不能为空！");
		// return false;
		// }

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SHOW_WriteCompanySpace:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("确认发布帖子吗?")
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
												mDataHelper.WriteCompanySpace(
														mTitle, board,
														mContent, mReleaseTime,
														photoPathList,
														upDataHandler, pBar);
											} catch (Exception e) {
												Toast.makeText(context, "发帖异常",
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == CODE_SELECT_USER) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				// mUserSelectId = bundle.getString("UserSelectId");
				// mUserSelectName = bundle.getString("UserSelectName");
				// 选择接收人
				// mEditTextReceiverName.setText(mUserSelectName);
				//
				// mEditTextReceiverName.setTag(false);
				// caculateHeight(llReceiver, mEditTextReceiverName,
				// mUserSelectName);
				// LogUtils.i("testKeno", mUserSelectId + "======" +
				// mUserSelectName);
			} else {
				if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
						|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
					addImageHelper.refresh(requestCode, data);
				}
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
				Boolean hasMessured = (Boolean) editText.getTag();
				if (hasMessured == false) {
					editText.setTag(true);
					int width = editText.getWidth(); // 控件宽度
					int height = editText.getHeight(); // 控件高度
					if (width != 0 && height != 0) {
						if (!TextUtils.isEmpty(contents)) {
							// 显示文字个数字数
							int len = contents.length();
							// 得到字体像素
							float px = editText.getTextSize();
							LogUtils.i("time", "字体像素：" + px + "，控件宽度：" + width);
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
				}
				return true;
			}
		});
	}
}
