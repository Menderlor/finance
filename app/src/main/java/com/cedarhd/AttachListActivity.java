package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cedarhd.adapter.DownloadAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.FilePathConfig;
import com.cedarhd.helpers.OpenFilesIntent;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.DownloadHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.utils.LogUtils;

import java.io.File;
import java.util.List;

/***
 * 附件列表
 * 
 * @author K
 * 
 */
public class AttachListActivity extends BaseActivity {

	public final static String ATTACH_IDS = "attachIds";

	public final static String ATTACH_LIST = "attachList";

	private static final int SUCCEED_GET_ATTACH = 1;
	private static final int FAILURE_GET_ATTACH = 2;
	private ZLServiceHelper mZlServiceHelper;

	private String mAttachIds;
	private List<Attach> mAttachList;
	private DownloadAdapter mAdapter;

	private Context mContext;

	private ImageView ivBack;
	private ListView lv;
	private LinearLayout llAttchment;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_GET_ATTACH:
				ProgressDialogHelper.dismiss();
				List<Attach> list = (List<Attach>) msg.obj;
				initDataAdapter(list);
				break;
			case FAILURE_GET_ATTACH:
				ProgressDialogHelper.dismiss();
				break;
			case DownloadHelper.SUCCEDD_DOWNLOAD:
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attach_list);
		initData();
		initViews();
		initEvent();

		if (!TextUtils.isEmpty(mAttachIds)) {
			getAttachList();
		} else if (mAttachList != null) {
			initDataAdapter(mAttachList);
		}
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		mContext = AttachListActivity.this;
		mZlServiceHelper = new ZLServiceHelper();
		mAttachIds = getIntent().getStringExtra(ATTACH_IDS);
		mAttachList = (List<Attach>) getIntent().getExtras().getSerializable(
				ATTACH_LIST);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_attach_list);
		lv = (ListView) findViewById(R.id.lv_attach_list);
		llAttchment = (LinearLayout) findViewById(R.id.ll_root_attach_list);
	}

	private void initEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Attach attach = mList.get(position);
		// if (attach.isDownloaded()) {
		// // Toast.makeText(mContext, "打开",
		// // Toast.LENGTH_SHORT).show();
		// open(attach.Name);
		// } else {
		// Toast.makeText(mContext, "开启下载...", Toast.LENGTH_SHORT)
		// .show();
		// mDownloadHelper.download(Global.BASE_URL + attach.Address,
		// attach.getName(), );
		// }
		// }
		// });
	}

	/** 通过网络访问附件列表 */
	private void getAttachList() {
		ProgressDialogHelper.show(mContext);
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Attach> listAttach = mZlServiceHelper.getAttachmentAddr(
						mContext, mAttachIds);
				if (listAttach != null && listAttach.size() > 0) {
					Message msg = handler.obtainMessage();
					msg.obj = listAttach;
					msg.what = SUCCEED_GET_ATTACH;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(FAILURE_GET_ATTACH);
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

	/***
	 * 打开附件文件的方法
	 * 
	 * @param name
	 */
	public void open(String name) {
		String fileName = FilePathConfig.getCacheDirPath() + File.separator
				+ name;
		File currentPath = new File(fileName);
		if (currentPath != null && currentPath.isFile()) {
			LogUtils.i("pathname", "-->" + fileName);
			Intent intent = null;
			if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingImage))) {
				intent = OpenFilesIntent.getImageFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingWebText))) {
				intent = OpenFilesIntent.getHtmlFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPackage))) {
				intent = OpenFilesIntent.getApkFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingAudio))) {
				intent = OpenFilesIntent.getAudioFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingVideo))) {
				intent = OpenFilesIntent.getVideoFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingText))) {
				intent = OpenFilesIntent.getTextFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPdf))) {
				intent = OpenFilesIntent.getPdfFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingWord))) {
				intent = OpenFilesIntent.getWordFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingExcel))) {
				intent = OpenFilesIntent.getExcelFileIntent(currentPath);
			} else if (checkEndsWithInStringArray(fileName, getResources()
					.getStringArray(R.array.fileEndingPPT))) {
				intent = OpenFilesIntent.getPPTFileIntent(currentPath);
			} else {
				Toast.makeText(this, "无法打开，请安装相应的软件！", Toast.LENGTH_LONG)
						.show();
			}
			if (intent != null) {
				try {
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, "对不起，文件打开异常！", Toast.LENGTH_LONG)
							.show();
				}
			}
		} else {
			Toast.makeText(this, "对不起，这不是文件！", Toast.LENGTH_LONG).show();
			// showMessage("对不起，这不是文件！");
		}
	}

	private void initDataAdapter(List<Attach> list) {
		if (list.size() == 0) {
			llAttchment.setVisibility(View.GONE);
		} else {
			mAdapter = new DownloadAdapter(mContext, list, lv);
			lv.setAdapter(mAdapter);
		}
	}

}
