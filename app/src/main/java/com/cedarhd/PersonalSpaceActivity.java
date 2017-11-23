package com.cedarhd;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AttchmentListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.control.LunTanDiscussListHelper;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.帖子;
import com.cedarhd.models.论坛回帖;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * @author py 2014.8.25
 */
public class PersonalSpaceActivity extends BaseActivity {
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	DictionaryHelper dictionaryHelper;
	ListView mListView;
	HorizontalScrollViewAddImage addImg_personalspaceinfo;
	帖子 m帖子;
	private EditText etclassify;
	EditText mEditTextTitle;
	EditText mEditTextContent;
	EditText mEditTextPubliser;
	LinearLayout llTitle;
	LinearLayout llReceiver;
	LinearLayout llContent;
	private EditText mEditTextExpirationTime;
	public final static int SUCESS_SHOW_ATTCHMENT = 2;
	public final static int SUCESS_DOWNLOAD_ATTACH = 3;
	public final static int FAILURE_SHOW_ATTCHMENT = 4;
	private AddImageHelper addImageHelper;
	private LinearLayout llAttchment; // 附件内容显示区
	private ListView lvAttchment; // 附件列表
	private AttchmentListHelper attchmentListHelper;
	// 存放非图片格式的附件信息
	List<Attach> listAttach = new ArrayList<Attach>();
	private AlertDialog dialog;
	private int pos;
	// private RelativeLayout rlDiscuss; // 评论按钮功能区
	// private Button btnDiscuss; // 评论功能按钮
	private EditText etDiscuss;
	private RelativeLayout rlPublishDiscuss; // 发表评论区域
	// private Button btnPublishDiscuss;// 发表评论按钮
	private EditText etDiscussContent;// 评论内容输入区
	private LinearLayout rlDiscussContent; // 评论内容显示区
	private ListView lvDiscuss; // 评论列表
	List<论坛回帖> listDiscuss = new ArrayList<论坛回帖>();
	private ImageView ivQuitDiscuss; // 取消评论
	private ImageView ivPublishDiscuss;// 发表评论按钮
	private Context context;
	private ZLServiceHelper zlServiceHelper;
	private HandlerNewContact handlerNewContact;
	private LunTanDiscussListHelper discussListHelper;
	private final int ID_TV_MORE = 101;// 查看更多的id
	private LinearLayout llDiscuss;// 评论区
	private LinearLayout ll_Discuss;// 评论功能按钮区
	private Button btnDiscusscount;
	private String time = "";
	private String id = "";
	private LinearLayout ll_classify;
	private LinearLayout ll_speech;

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		// public static final int GET_LOG_NOW_SUCCESS = 0;
		// public static final int GET_LOG_NOW_FAILED = 1;
		public static final int UPDATE_Reply_SUCCESS = 2;
		public static final int UPDATE_Reply_FAILED = 3;
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == UPDATE_Reply_SUCCESS) { // 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				// finish();
				// 重新加载评论列表
				// discussListHelper.setmList(listDiscuss);
				llDiscuss.setVisibility(View.VISIBLE);
				// btnDiscuss.setVisibility(View.VISIBLE);
				// addHeader();
				if (discussListHelper == null) {
					discussListHelper = new LunTanDiscussListHelper(context,
							listDiscuss, lvDiscuss, rlDiscussContent);
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						// zlServiceHelper.getReply(m帖子.getId(),
						// handlerNewContact);
					}
				}).start();
				// zlServiceHelper.getReply(m帖子.getId(), handlerNewContact);
			}
			if (msg.what == UPDATE_Reply_FAILED) {
				MessageUtil.ToastMessage(context, "修改失败！");
			}
			if (msg.what == GET_DISCUSS_SUCCESS) {// 获得评论列表成功
				etDiscussContent.setText("");
				listDiscuss = (List<论坛回帖>) msg.obj;
				// 显示评论内容
				rlDiscussContent.setVisibility(View.VISIBLE);
				if (listDiscuss.size() == 0) {
					rlDiscussContent.setVisibility(View.GONE);
				}
				discussListHelper.setmList(listDiscuss);
				btnDiscusscount.setText("回复" + listDiscuss.size() + "");
			}
			if (msg.what == GET_DISCUSS_FAILED) {// 获得评论列表失败
				rlDiscussContent.setVisibility(View.GONE);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCESS_SHOW_ATTCHMENT) {
				if (listAttach != null && listAttach.size() != 0) {
					attchmentListHelper = new AttchmentListHelper(
							PersonalSpaceActivity.this, listAttach,
							lvAttchment, llAttchment);
					int height = (int) ViewHelper.dip2px(
							PersonalSpaceActivity.this,
							attchmentListHelper.getHeight());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, height);
					lvAttchment.setLayoutParams(params);
				} else {
					llAttchment.setVisibility(View.GONE);
				}
			} else if (msg.what == FAILURE_SHOW_ATTCHMENT) {
				llAttchment.setVisibility(View.GONE);
			} else if (msg.what == SUCESS_DOWNLOAD_ATTACH) {
				Toast.makeText(PersonalSpaceActivity.this, "文件下载完毕",
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
				Attach attach = listAttach.get(pos);
				// 下载附件成功
				int index = attach.Address.lastIndexOf("\\");
				String picName = attach.Address.substring(index + 1,
						attach.Address.length()); // 图片名
				String attachaddr = FilePathConfig.getAvatarDirPath()
						+ File.separator + picName;
				File attachfile = new File(attachaddr);
				if (attachfile.exists()) {
					open(attachfile);
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalspace_info_new);
		Bundle bundle = this.getIntent().getExtras();
		m帖子 = (帖子) bundle.getSerializable("帖子");
		findViews();
		setOnClickListener();
		Init();
	}

	void Init() {
		dictionaryHelper = new DictionaryHelper(getApplicationContext());
		mEditTextTitle.setText(m帖子.Title);
		// mEditTextExpirationTime.setText(DateTimeUtil
		// .ConvertLongDateToString(m帖子.PostTime));
		mEditTextExpirationTime.setText(m帖子.PostTime);
		mEditTextPubliser.setText(dictionaryHelper.getUserNameById(m帖子.Poster));
		// String personnelName = dictionaryHelper
		// .getUserNamesById(mNotice.Personnel);
		// mEditTextReceiverName.setText(personnelName);
		mEditTextContent.setText(m帖子.Content);

		mEditTextTitle.setTag(false);
		mEditTextContent.setTag(false);
		caculateHeight(llTitle, mEditTextTitle, m帖子.getTitle());
		// caculateHeight(llContent, mEditTextContent, mNotice.Content);

		// mEditTextReceiverName.setTag(false);
		// caculateHeight(llReceiver, mEditTextReceiverName, personnelName);
		llDiscuss.setVisibility(View.VISIBLE);
		// btnDiscuss.setVisibility(View.VISIBLE);
		addHeader();
		if (discussListHelper == null) {
			discussListHelper = new LunTanDiscussListHelper(context,
					listDiscuss, lvDiscuss, rlDiscussContent);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// zlServiceHelper.getReply(m帖子.getId(), handlerNewContact);
			}
		}).start();
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

	public void setOnClickListener() {
		ImageView imageViewDone = (ImageView) findViewById(R.id.imageViewDone);
		imageViewDone.setVisibility(View.GONE);
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lvAttchment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos = position;
				Attach attach = (Attach) parent.getItemAtPosition(position);
				LogUtils.i("noticeAtta", attach.Name + "-------"
						+ attach.Address);

				int index = attach.Address.lastIndexOf("\\");
				String picName = attach.Address.substring(index + 1,
						attach.Address.length()); // 图片名
				String attachaddr = FilePathConfig.getAvatarDirPath()
						+ File.separator + picName;
				File attachfile = new File(attachaddr);
				if (attachfile.exists()) {
					open(attachfile);
				} else {
					showDownLoadDialog(attach);
				}
			}
		});

		etDiscuss.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				rlPublishDiscuss.setVisibility(View.VISIBLE);
				// InputMethodManager imm = (InputMethodManager) etDiscuss
				// .getContext().getSystemService(INPUT_METHOD_SERVICE);
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				// rlDiscuss.setVisibility(View.GONE);
			}
		});
		ivQuitDiscuss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rlPublishDiscuss.setVisibility(View.GONE);
				// rlDiscuss.setVisibility(View.VISIBLE);
			}
		});
		// 发表评论btnPublishDiscuss
		ivPublishDiscuss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// rlDiscuss.setVisibility(View.VISIBLE);
				final String content = etDiscussContent.getText().toString();
				if (!TextUtils.isEmpty(content)) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// zlServiceHelper.publishRepaly(m帖子.Id, content,
							// time, id, handlerNewContact);
						}
					}).start();
				} else {
					Toast.makeText(PersonalSpaceActivity.this, "回复内容不能为空",
							Toast.LENGTH_LONG).show();
				}
				rlPublishDiscuss.setVisibility(View.GONE);
				// rlDiscuss.setVisibility(View.VISIBLE);
			}
		});
	}

	public void findViews() {
		findViewById(R.id.ll_speech).setVisibility(View.GONE);
		etclassify = (EditText) findViewById(R.id.et_classify);
		etclassify.setVisibility(View.GONE);
		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_content_personalspace);
		scrollView.getParent().requestDisallowInterceptTouchEvent(true);
		// tvContent = (TextView) findViewById(R.id.tv_content_notice);
		llTitle = (LinearLayout) findViewById(R.id.ll_title_info);
		llReceiver = (LinearLayout) findViewById(R.id.ll_receiver_personalspace_info);
		llReceiver.setVisibility(View.GONE);
		// findViewById(R.id.view_publish_time_companyspace_info_new)
		// .setVisibility(View.GONE);
		llContent = (LinearLayout) findViewById(R.id.ll_content_personalspace_info);

		mEditTextTitle = (EditText) findViewById(R.id.editTextTitle);
		mEditTextContent = (EditText) findViewById(R.id.editTextContent);
		// mEditTextReceiverName = (EditText)
		// findViewById(R.id.editTextReceiverName);
		mEditTextPubliser = (EditText) findViewById(R.id.editTextPublisher);
		mEditTextExpirationTime = (EditText) findViewById(R.id.showdateExpirationTime);
		addImg_personalspaceinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_personalspace);

		llAttchment = (LinearLayout) findViewById(R.id.ll_attchment_personalspace_info);
		lvAttchment = (ListView) findViewById(R.id.lv_attchment_personalspace_info);

		if (TextUtils.isEmpty(m帖子.Attachment)) {
			addImg_personalspaceinfo.setVisibility(View.GONE);
			llAttchment.setVisibility(View.GONE);
		} else {
			addImageHelper = new AddImageHelper(this,
					this.getApplicationContext(), addImg_personalspaceinfo,
					m帖子.Attachment, false);
			initAttachs();
		}
		mEditTextTitle.setEnabled(false);
		mEditTextContent.setEnabled(false);
		// mEditTextReceiverName.setEnabled(false);
		mEditTextPubliser.setEnabled(false);
		mEditTextExpirationTime.setEnabled(false);
		// btnDiscuss = (Button) findViewById(R.id.et_companyspace_discuss);
		etDiscuss = (EditText) findViewById(R.id.et_personalspace_discuss);
		etDiscuss.setFocusable(false);
		rlPublishDiscuss = (RelativeLayout) findViewById(R.id.rl_publich_discuss_personalspace_info);
		// btnPublishDiscuss = (Button)
		// findViewById(R.id.btn_publich_discuss_companyspace_info);
		etDiscussContent = (EditText) findViewById(R.id.et_discuss_content_personalspace_info);
		// etDiscussContent.setOnFocusChangeListener(onFocusAutoClearListener);
		// rlDiscuss = (RelativeLayout)
		// findViewById(R.id.rl_discuss_personalspace_new);
		rlDiscussContent = (LinearLayout) findViewById(R.id.rl_discuss_content_info_personalspace_new);
		lvDiscuss = (ListView) findViewById(R.id.lv_discuss_personalspace_new);
		context = getApplicationContext();
		handlerNewContact = new HandlerNewContact();
		zlServiceHelper = new ZLServiceHelper();
		ivQuitDiscuss = (ImageView) findViewById(R.id.iv_discuss_quit_personalspace_info);// 退出评论
		ivPublishDiscuss = (ImageView) findViewById(R.id.iv_discuss_submit_personalspace_info); // 发表评论
		// mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		// mProgressBar.setVisibility(View.GONE);
		llDiscuss = (LinearLayout) findViewById(R.id.ll_discuss_personalspace_new);
		ll_Discuss = (LinearLayout) findViewById(R.id.ll_personalspace_discuss);
		ll_Discuss.setVisibility(View.VISIBLE);
		btnDiscusscount = (Button) findViewById(R.id.btn_personalspace_discuss);
		time = ViewHelper.getDateString();
		// id = Global.mUser.Id;
		ll_classify = (LinearLayout) findViewById(R.id.ll_title_class);
		ll_classify.setVisibility(View.GONE);
		View view = findViewById(R.id.view_line_id_classify);
		view.setVisibility(View.GONE);
		ll_speech = (LinearLayout) findViewById(R.id.ll_speech);
		ll_speech.setVisibility(View.GONE);
	}

	private void initAttachs() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String attachIds = m帖子.Attachment.replace("'", "").replace(
							"\"", "");
					LogUtils.i("noticeAttach", attachIds);
					List<Attach> attachs = mZLServiceHelper.getAttachmentAddr(
							PersonalSpaceActivity.this, attachIds);
					String suffix = "";
					if (attachs != null && attachs.size() > 0) {
						for (Attach attach : attachs) {
							if (attach != null) {
								suffix = attach.Suffix.toLowerCase();
								if (!TextUtils.isEmpty(suffix)
										&& !suffix.contains("png")
										&& !suffix.contains("jpg")
										&& !suffix.contains("gif")) {
									listAttach.add(attach);
								}
							}
						}
						handler.sendEmptyMessage(SUCESS_SHOW_ATTCHMENT);
					} else {
						handler.sendEmptyMessage(FAILURE_SHOW_ATTCHMENT);
					}

				} catch (Exception e) {
					Toast.makeText(context, "初始化附件信息异常", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}).start();
	}

	// 定义用于检查要打开的附件文件的后缀是否在遍历后缀数组中
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	// 打开附件文件的方法
	private void open(File currentPath) {
		if (currentPath != null && currentPath.isFile()) {
			String fileName = currentPath.toString();
			LogUtils.i("pathname", "-->" + fileName);
			Intent intent;
			if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingImage))) {
				intent = OpenFilesIntent.getImageFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingWebText))) {
				intent = OpenFilesIntent.getHtmlFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPackage))) {
				intent = OpenFilesIntent.getApkFileIntent(currentPath);
				startActivity(intent);

			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingAudio))) {
				intent = OpenFilesIntent.getAudioFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingVideo))) {
				intent = OpenFilesIntent.getVideoFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingText))) {
				intent = OpenFilesIntent.getTextFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPdf))) {
				intent = OpenFilesIntent.getPdfFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingWord))) {
				intent = OpenFilesIntent.getWordFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingExcel))) {
				intent = OpenFilesIntent.getExcelFileIntent(currentPath);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPPT))) {
				intent = OpenFilesIntent.getPPTFileIntent(currentPath);
				startActivity(intent);
			} else {
				Toast.makeText(this, "无法打开，请安装相应的软件！", Toast.LENGTH_LONG)
						.show();
				// showMessage("无法打开，请安装相应的软件！");
			}
		} else {
			Toast.makeText(this, "对不起，这不是文件！", Toast.LENGTH_LONG).show();
			// showMessage("对不起，这不是文件！");
		}
	}

	private void showDownLoadDialog(final Attach attach) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PersonalSpaceActivity.this);
		// LayoutInflater inflater = LayoutInflater.from(NoticeActivity.this);
		// View view = inflater.inflate(R.layout.dialog_download, null);
		// TextView tv = (TextView) view.findViewById(R.id.tv_file_sumary);
		// tv.setText(attach.Name);
		// builder.setView(view);
		// Button btnDownload = (Button) view
		// .findViewById(R.id.btn_dwonload_dialog);
		// Button btnCancel = (Button)
		// view.findViewById(R.id.btn_cancel_dialog);
		// btnDownload.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// HttpUtils httpUtils = new HttpUtils();
		// httpUtils.downloadData(attach.Address, handler);
		// }
		// });
		//
		// btnCancel.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// dialog.dismiss();
		// }
		// });

		builder.setTitle("下载");
		builder.setMessage("是否下载文件： " + attach.Name);
		builder.setPositiveButton("立即下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final HttpUtils httpUtils = new HttpUtils();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							httpUtils.downloadData(attach.Address, handler);
						} catch (Exception e) {
							Toast.makeText(context, "下载附件数据异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}).start();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();

	}

	private void addHeader() {
		TextView tvHeader = new TextView(context);
		tvHeader.setId(ID_TV_MORE);
		tvHeader.setTextColor(0xFF28B69B);
		tvHeader.setTextSize(14);
		tvHeader.setBackgroundColor(0xFFEEEEEE);
		tvHeader.setClickable(true);
		tvHeader.setText("查看更多回复");
		AbsListView.LayoutParams tvparams = new AbsListView.LayoutParams(
				Global.mWidthPixels, (int) ViewHelper.dip2px(context, 35));
		tvHeader.setPadding(20, 20, 0, 0);
		tvHeader.setLayoutParams(tvparams);
		lvDiscuss.addHeaderView(tvHeader);
		tvHeader.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int height = discussListHelper.getHeight();
				LogUtils.i("height", "height:" + height);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, height);
				rlDiscussContent.setLayoutParams(params);
			}
		});

	}

}
