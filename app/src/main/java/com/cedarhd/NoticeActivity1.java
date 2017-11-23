package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AttchmentListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.通知;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知详情
 * 
 * @author BOHR
 * 
 */
public class NoticeActivity1 extends BaseActivity {
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	DictionaryHelper dictionaryHelper;
	ListView mListView;
	HorizontalScrollViewAddImage addImg_noticeinfo;
	通知 mNotice;

	EditText mEditTextTitle;
	EditText mEditTextContent;
	// EditText mEditTextReceiverName; //不显示接收人
	EditText mEditTextPubliser;
	LinearLayout llTitle;
	LinearLayout llReceiver;
	LinearLayout llContent;
	private File file;
	private String fileName;
	// private ImageButton showAttchment;
	private File file1 = new File(FilePathConfig.getAvatarDirPath()
			+ File.separator + "weather.txt");

	private EditText mEditTextExpirationTime;
	private LinearLayout llAttchment; // 附件内容显示区
	private ListView lvAttchment; // 附件列表
	private AttchmentListHelper attchmentListHelper;
	Context context = NoticeActivity1.this;
	List<Attach> listAttach = new ArrayList<Attach>();
	public final static int SUCESS_ATTCHMENT_DOWNLOAD = 3;
	public final static int FAILURE_ATTCHMENT_DOWNLOAD = 4;
	List<Attach> attachs;
	private Handler mHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == SUCESS_ATTCHMENT_DOWNLOAD) {
				LogUtils.i("py:filepath-->", "handler");
				file = new File(FilePathConfig.getAvatarDirPath()
						+ File.separator + fileName);
				String files = file.getPath();
				LogUtils.i("py:filepath-->", files);
				if (listAttach.size() == 0) {
					// showAttchment.setEnabled(true);
					listAttach = attachs;
					attchmentListHelper = new AttchmentListHelper(context,
							listAttach, lvAttchment, llAttchment);
					if (listAttach.size() == 0) {
						llAttchment.setVisibility(View.GONE);
					}
					attchmentListHelper.setmList(listAttach);
					for (Attach attach : listAttach) {
						LogUtils.i("pyattachname", attach.Name);
					}
				}
			}
			if (msg.what == FAILURE_ATTCHMENT_DOWNLOAD) {
				llAttchment.setVisibility(View.GONE);
			}
			if (msg.what == 5) {
				listAttach = attachs;
				attchmentListHelper = new AttchmentListHelper(context,
						listAttach, lvAttchment, llAttchment);
				attchmentListHelper.setmList(listAttach);
			}
		}
	};
	private AddImageHelper addImageHelper;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_info);
		Bundle bundle = this.getIntent().getExtras();
		mNotice = (通知) bundle.getSerializable("Notice");
		findViews();
		setOnClickListener();
		Init();
	}

	void Init() {
		dictionaryHelper = new DictionaryHelper(getApplicationContext());
		mEditTextTitle.setText(mNotice.Title);
		mEditTextExpirationTime.setText(DateTimeUtil
				.ConvertLongDateToString(mNotice.ReleaseTime));
		mEditTextPubliser.setText(dictionaryHelper
				.getUserNameById(mNotice.Publisher));
		// String personnelName = dictionaryHelper
		// .getUserNamesById(mNotice.Personnel);
		// mEditTextReceiverName.setText(personnelName);
		mEditTextContent.setText(mNotice.Content);

		mEditTextTitle.setTag(false);
		mEditTextContent.setTag(false);
		caculateHeight(llTitle, mEditTextTitle, mNotice.getTitle());
		caculateHeight(llContent, mEditTextContent, mNotice.Content);

		// mEditTextReceiverName.setTag(false);
		// caculateHeight(llReceiver, mEditTextReceiverName, personnelName);
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
		ImageView imageViewDone = (ImageView) findViewById(R.id.imageViewDone2);
		imageViewDone.setVisibility(View.GONE);
		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel2);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// showAttchment.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// // LogUtils.i("openfilepath", file.getPath());
		// LogUtils.i("openfilepath", "onClick");
		// if(file!=null&&file.exists())
		// {
		// LogUtils.i("openfilepath", file.getPath());
		// open(file);
		// }else
		// {
		// LogUtils.i("openfilepath", "openfilepath");
		// }
		// }
		// });
		lvAttchment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				// lvAttchment.getItemAtPosition(arg2);
				// File file = new
				// File(FilePathConfig.getAvatarDirPath() + File.separator
				// +mNotice.Attachment.toString());
				// open(file);
				Attach item = listAttach.get(arg2);
				// String attachpath = FilePathConfig.getAvatarDirPath() +
				// File.separator +item.Name;
				// File file = new File(attachpath);
				open(item.Name);
			}
		});
	}

	public void findViews() {
		findViewById(R.id.ll_speech2).setVisibility(View.GONE);

		ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView_content_notice2);
		scrollView.getParent().requestDisallowInterceptTouchEvent(true);
		// tvContent = (TextView) findViewById(R.id.tv_content_notice);
		llTitle = (LinearLayout) findViewById(R.id.ll_title_notice_info2);
		llReceiver = (LinearLayout) findViewById(R.id.ll_receiver_notice_info2);
		llReceiver.setVisibility(View.GONE);
		findViewById(R.id.view_personel_notic_info_new2).setVisibility(
				View.GONE);

		llContent = (LinearLayout) findViewById(R.id.ll_content_notice_info2);

		mEditTextTitle = (EditText) findViewById(R.id.editTextTitle2);
		mEditTextContent = (EditText) findViewById(R.id.editTextContent2);
		// mEditTextReceiverName = (EditText)
		// findViewById(R.id.editTextReceiverName);
		mEditTextPubliser = (EditText) findViewById(R.id.editTextPublisher2);
		mEditTextExpirationTime = (EditText) findViewById(R.id.showdateExpirationTime2);
		addImg_noticeinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_noticeinfo2);
		// tvContent.setGravity(Gravity.TOP);
		// mEditTextContent.setGravity(Gravity.TOP);
		// showAttchment = (ImageButton) findViewById(R.id.show_attchment);
		// showAttchment.setEnabled(false);
		llAttchment = (LinearLayout) findViewById(R.id.ll_attchment);
		lvAttchment = (ListView) findViewById(R.id.lv_attchment);
		if (TextUtils.isEmpty(mNotice.Attachment)) {
			addImg_noticeinfo.setVisibility(View.GONE);
		} else {
			// addImageHelper = new AddImageHelper(this,
			// this.getApplicationContext(), addImg_noticeinfo,
			// mNotice.Attachment, false);
			addImg_noticeinfo.setVisibility(View.GONE);
			// 显示附件名称
			llAttchment.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						attachs = mZLServiceHelper.getAttachmentAddr(
								NoticeActivity1.this, mNotice.Attachment);
						// LogUtils.i("py:filename", "--->"+filepath);
						// int index = filepath.lastIndexOf("\\");
						// fileName = filepath.substring(index + 1,
						// filepath.length());
						// HttpUtils httpUtils = new HttpUtils();
						// httpUtils.downloadData(filepath,mHanlder);
						for (int i = 0; i < attachs.size(); i++) {
							Attach attach = (Attach) attachs.get(i);
							LogUtils.i("py:filename", "--->" + attach.Name);
							String attachaddr = FilePathConfig
									.getAvatarDirPath()
									+ File.separator
									+ attach.Name;
							File attachfile = new File(attachaddr);
							if (!attachfile.exists()) {
								HttpUtils httpUtils = new HttpUtils();
								httpUtils
										.downloadData(attach.Address, mHanlder);
							} else {
								Message msg = mHanlder.obtainMessage();
								msg.what = 5;
								mHanlder.sendMessage(msg);
							}
							attchmentListHelper = new AttchmentListHelper(
									context, listAttach, lvAttchment,
									llAttchment);
							listAttach = attachs;
						}
					} catch (Exception e) {
						Toast.makeText(context, "初始化附件异常", Toast.LENGTH_SHORT)
								.show();
					}

				}
			}).start();
		}

		// mEditTextReceiverName.setCursorVisible(false);
		// mEditTextReceiverName.setFocusable(false);
		mEditTextTitle.setEnabled(false);
		mEditTextContent.setEnabled(false);
		mEditTextPubliser.setEnabled(false);
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
	public void open(String name) {
		String fileName = FilePathConfig.getAvatarDirPath() + File.separator
				+ name;
		File currentPath = new File(fileName);
		if (currentPath != null && currentPath.isFile()) {
			// String fileName = currentPath.toString();
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
			}
		} else {
			Toast.makeText(this, "对不起，这不是文件！", Toast.LENGTH_LONG).show();
			// showMessage("对不起，这不是文件！");
		}
	}
}
