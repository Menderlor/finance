package com.cedarhd;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AttchmentListHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.客户投诉建议;
import com.cedarhd.utils.DateTimeUtil;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户投诉建议详情
 * 
 * @author py 2014.8.14
 * 
 */
public class SuggestActivity extends BaseActivity {
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	private LinearLayout llAttchment; // 附件内容显示区
	private ListView lvAttchment; // 附件列表
	private AttchmentListHelper attchmentListHelper;
	// 存放非图片格式的附件信息
	private AddImageHelper addImageHelper;
	List<Attach> listAttach = new ArrayList<Attach>();
	private AlertDialog dialog;
	private int pos;
	public final static int SUCESS_SHOW_ATTCHMENT = 2;
	public final static int SUCESS_DOWNLOAD_ATTACH = 3;
	public final static int FAILURE_SHOW_ATTCHMENT = 4;
	private ImageView ivcancel;
	private ImageView ivNew;
	private EditText etclient;
	private EditText etContent;
	private EditText etTime;
	private HorizontalScrollViewAddImage addImag_suggestinfo;
	private 客户投诉建议 m客户投诉建议;
	DictionaryHelper dictionaryHelper;
	private LinearLayout ll_speech;
	private int clientId;
	public static final int SELECT_CLIENT_CODE = 5; // 选择客户名称
	private Context context;
	String mContent = "";
	String mReleaseTime = "";
	String Attachment = "";
	private int id;// 客户投诉建议id
	public static final int UPDATA_FAILED = 2;
	public static final int UPDATA_SUCCESED = 3;
	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int EDIT_CONTENT_CODE = 7;
	public static final String TAG = "SuggestActivity";
	private Handler upDataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_FAILED:
				// 提示信息显示
				MessageUtil.ToastMessage(SuggestActivity.this, "修改失败！");
				break;
			case UPDATA_SUCCESED:
				// 提示信息显示
				MessageUtil.ToastMessage(SuggestActivity.this, "修改成功！");
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
		Bundle bundle = this.getIntent().getExtras();
		m客户投诉建议 = (客户投诉建议) bundle.getSerializable("Suggest");
		findviews();
		init();
		setonclick();

	}

	private void init() {
		// TODO Auto-generated method stub
		dictionaryHelper = new DictionaryHelper(getApplicationContext());
		if (m客户投诉建议.ClientName == null) {
			etclient.setText("未知客户");
		} else {
			etclient.setText(m客户投诉建议.ClientName);
		}
		etContent.setText(m客户投诉建议.Content);
		// etTime.setText(m客户投诉建议.UpdateTime.toString());
		etTime.setText(DateTimeUtil.ConvertLongDateToString(m客户投诉建议.Time));
		clientId = m客户投诉建议.ClientId;
	}

	private void findviews() {
		// TODO Auto-generated method stub
		context = SuggestActivity.this;
		ivcancel = (ImageView) findViewById(R.id.imageViewCancel);
		ivNew = (ImageView) findViewById(R.id.imageViewDone);
		etclient = (EditText) findViewById(R.id.et_client_suggest);
		etContent = (EditText) findViewById(R.id.et_content_suggest);
		etContent.setFocusable(false);
		etTime = (EditText) findViewById(R.id.et_time_suggest);
		etTime.setEnabled(false);
		lvAttchment = (ListView) findViewById(R.id.lv_attchment_suggest);
		llAttchment = (LinearLayout) findViewById(R.id.ll_attchment_suggest);
		addImag_suggestinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_suggest);
		ll_speech = (LinearLayout) findViewById(R.id.ll_speech_suggest);
		ll_speech.setVisibility(View.GONE);
		addImag_suggestinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_suggest);
		if (TextUtils.isEmpty(m客户投诉建议.Attachment)) {
			addImag_suggestinfo.setVisibility(View.GONE);
			llAttchment.setVisibility(View.GONE);
		} else {
			addImageHelper = new AddImageHelper(this,
					this.getApplicationContext(), addImag_suggestinfo,
					m客户投诉建议.Attachment, false);
			initAttachs();
		}
		id = m客户投诉建议.getId();
		Attachment = m客户投诉建议.Attachment;
	}

	private void setonclick() {
		// TODO Auto-generated method stub
		ivcancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lvAttchment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pos = position;
				Attach attach = (Attach) parent.getItemAtPosition(position);
				LogUtils.i("suggestAtta", attach.Name + "-------"
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
		ivNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				submit();
			}
		});
		etclient.setOnClickListener(new View.OnClickListener() {

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
		etContent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SuggestActivity.this,
						SuggestContentActivity.class);
				// intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				Bundle bundle = new Bundle();
				bundle.putSerializable(SuggestActivity.TAG, etContent.getText()
						.toString());
				intent.putExtras(bundle);
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == SUCESS_SHOW_ATTCHMENT) {
				if (listAttach != null && listAttach.size() != 0) {
					attchmentListHelper = new AttchmentListHelper(
							SuggestActivity.this, listAttach, lvAttchment,
							llAttchment);
					int height = (int) ViewHelper.dip2px(SuggestActivity.this,
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
				Toast.makeText(SuggestActivity.this, "文件下载完毕",
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

	private void showDownLoadDialog(final Attach attach) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SuggestActivity.this);
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
							Toast.makeText(context, "下载数据异常", 0).show();
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

	// 定义用于检查要打开的附件文件的后缀是否在遍历后缀数组中
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	private void initAttachs() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					String attachIds = m客户投诉建议.Attachment.replace("'", "")
							.replace("\"", "");
					LogUtils.i("noticeAttach", attachIds);
					List<Attach> attachs = mZLServiceHelper.getAttachmentAddr(
							SuggestActivity.this, attachIds);
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
					Toast.makeText(context, "加载附件信息异常", 0).show();
				}
			}
		}).start();
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkValid() {
		if (TextUtils.isEmpty(etclient.getText().toString())) {
			MessageUtil.AlertMessage(SuggestActivity.this, "保存失败", "客户不能为空！");
			return false;
		}
		if (etContent.getText() == null
				|| etContent.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(SuggestActivity.this, "保存失败", "内容不能为空！");
			return false;
		} else if (etContent.getText().toString().trim().length() > 1000) {
			MessageUtil.AlertMessage(SuggestActivity.this, "保存失败",
					"内容不能多于1000个字！");
			return false;
		}
		if (TextUtils.isEmpty(etclient.getText().toString())) {
			MessageUtil.AlertMessage(SuggestActivity.this, "保存失败", "客户不能为空！");
			return false;
		}

		return true;
	}

	private void submit() {
		// TODO Auto-generated method stub
		if (checkValid()) {
			mContent = etContent.getText().toString();
			mReleaseTime = etTime.getText().toString();

			LogUtils.i("pymsg", clientId + "");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 修改投诉建议
						mZLServiceHelper.EditSuggest(id, clientId, mContent,
								mReleaseTime, upDataHandler, Attachment);
					} catch (Exception e) {
						Toast.makeText(context, "修改投诉建议异常", 0).show();
					}
				}
			}).start();
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(context, ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
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
}
