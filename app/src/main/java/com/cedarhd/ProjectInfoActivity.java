package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.NoScrollGridAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunNoScrollGridView;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Attach;
import com.cedarhd.models.项目简略;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.widget.BoeryunDialog;
import com.cedarhd.widget.BoeryunDialog.OnBoeryunDialogClickListner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** 项目详情 **/
public class ProjectInfoActivity extends BaseActivity {

	private final int CODE_SELECT_PHOTO = 1;
	private Context context;
	private 项目简略 mProject;
	private DictionaryHelper mDictionaryHelper;
	private ZLServiceHelper mZlServiceHelper;
	private HttpUtils mHttpUtils;

	private ImageView ivBack;
	private ImageView ivSave;
	private EditText etName;
	private EditText etUserName;
	private TextView tvCount;
	private LinearLayout llAttach;
	private ProgressBar pbar;
	private ProgressBar pbarUpload;
	private ImageView ivAddAttach;

	private BoeryunNoScrollGridView gridview_img;

	/** 附件数量 */
	private int attachCount;

	/***
	 * 图片附件的相对路径集合
	 */
	private ArrayList<String> mImgUrlList;

	/** 图片以外的附件编号 */
	private List<Attach> mOtherAtachList;

	StringBuilder mSbAttachIds;

	private final int SUCCESS_INIT_ATTACH = 11;

	private final int SUCCEED_SAVE_PROJECT = 21;
	private final int FAILURE_SAVE_PROJECT = 22;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_INIT_ATTACH:
				if (mImgUrlList.size() > 0) {
					gridview_img.setAdapter(new NoScrollGridAdapter(context,
							100, Global.BASE_URL, mImgUrlList));
				}
				tvCount.setText("图片：" + mImgUrlList.size() + "   其他："
						+ mOtherAtachList.size());
				break;
			case SUCCEED_SAVE_PROJECT:
				Toast.makeText(context, "附件上传成功", Toast.LENGTH_SHORT).show();
				pbarUpload.setVisibility(View.GONE);
				break;
			case FAILURE_SAVE_PROJECT:
				Toast.makeText(context, "项目保存失败成功", Toast.LENGTH_SHORT).show();
				pbarUpload.setVisibility(View.GONE);
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project_info);
		initData();
		initViews();
		initEvent();

		if (mProject != null && !TextUtils.isEmpty(mProject.附件)) {
			getAttachList(mProject.附件);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CODE_SELECT_PHOTO:
				if (data != null) {
					Bundle bundle = data.getExtras();
					final ArrayList<String> pathList = bundle
							.getStringArrayList(SelectPhotoActivity.PHOTO_LIST);
					if (pathList != null && pathList.size() > 0) {
						uploadAttach(pathList);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	/** 上传附件 */
	private void uploadAttach(final ArrayList<String> pathList) {
		final BoeryunDialog dialog = new BoeryunDialog(context, true, "是否上传",
				"您选中了" + pathList.size() + "张图片", "取消", "确定");
		dialog.setBoeryunDialogClickListener(new OnBoeryunDialogClickListner() {
			@Override
			public void onClick() {
				dialog.dismiss();
			}
		}, new OnBoeryunDialogClickListner() {
			@Override
			public void onClick() {
				Toast.makeText(context, "开始上传：" + pathList.size() + "张图片",
						Toast.LENGTH_SHORT).show();

				pbarUpload.setVisibility(View.VISIBLE);
				pbarUpload.setMax(pathList.size());
				pbarUpload.setProgress(0);
				new Thread(new Runnable() {
					@Override
					public void run() {
						String attachIds = mZlServiceHelper.uploadAttachPhotos(
								pathList, pbarUpload);
						LogUtils.i(TAG, "附件号:" + attachIds);
						// 更新用户附件列表
						getAttachList(attachIds);

						mProject.附件 = attachIds + "," + mProject.附件;
						saveProject();
					}
				}).start();

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void initData() {
		context = this;
		mDictionaryHelper = new DictionaryHelper(context);
		mZlServiceHelper = new ZLServiceHelper();
		mHttpUtils = new HttpUtils();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mProject = (项目简略) bundle
					.getSerializable(ProjectListActivity.PROJECT_INFO);
		}

		mImgUrlList = new ArrayList<String>();
		mSbAttachIds = new StringBuilder();
		mOtherAtachList = new ArrayList<Attach>();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_project_inofo);
		ivSave = (ImageView) findViewById(R.id.iv_save_project_inofo);
		etName = (EditText) findViewById(R.id.et_name_project_ifno);
		etUserName = (EditText) findViewById(R.id.et_paticipant_project_info);
		tvCount = (TextView) findViewById(R.id.tv_attach_count_project_info);
		pbar = (ProgressBar) findViewById(R.id.pbar_project_info);
		pbarUpload = (ProgressBar) findViewById(R.id.pbar_upload_attach_project_info);
		llAttach = (LinearLayout) findViewById(R.id.ll_attchment_project_info);
		ivAddAttach = (ImageView) findViewById(R.id.iv_add_attach_project_info);
		gridview_img = (BoeryunNoScrollGridView) findViewById(R.id.gv_project_info_item);
		if (mProject != null) {
			etName.setText(mProject.名称);
			etUserName.setText(mDictionaryHelper.getUserNamesById(mProject.参与人)
					+ "");
			if (!TextUtils.isEmpty(mProject.附件)) {
				attachCount = mProject.附件.split(",").length;
				tvCount.setText("" + attachCount);
			}
		}
	}

	private void initEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		llAttach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mSbAttachIds.toString())) {
					Toast.makeText(context, "还没有添加其他附件哦", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intent = new Intent(context,
							AttachListActivity.class);
					intent.putExtra(AttachListActivity.ATTACH_IDS,
							mSbAttachIds.toString());
					startActivity(intent);
				}
			}
		});

		pbar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mProject != null) {
					Intent intent = new Intent(context,
							TaskTabListActivity.class);
					intent.putExtra(TaskTabListActivity.PROJECT_ID, mProject.编号
							+ "");
					intent.putExtra(TaskTabListActivity.PROJECT_NAME,
							mProject.名称 + "");
					startActivity(intent);
				}
			}
		});

		gridview_img.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startImageBrower(position, mImgUrlList);
			}
		});

		/** 添加附件 */
		ivAddAttach.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SelectPhotoActivity.class);
				startActivityForResult(intent, CODE_SELECT_PHOTO);
			}
		});
	}

	private void saveProject() {
		final String url = Global.BASE_URL + "Project/SaveProject";
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo;
				try {
					jo = JsonUtils.initJsonObj(mProject);
					String result = mHttpUtils.postSubmit(url, jo);

					String status = JsonUtils.parseStatus(result);
					if ("1".equals(status)) {
						handler.sendEmptyMessage(SUCCEED_SAVE_PROJECT);
					} else {
						handler.sendEmptyMessage(FAILURE_SAVE_PROJECT);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(FAILURE_SAVE_PROJECT);
				}
			}
		}).start();
	}

	/***
	 * 获取附件列表
	 * 
	 * @param ids
	 */
	private void getAttachList(final String ids) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Attach> attachList = mZlServiceHelper.getAttachmentAddr(
						context, ids);
				initAttachList(attachList);
				handler.sendEmptyMessage(SUCCESS_INIT_ATTACH);
			}
		}).start();
	}

	private void initAttachList(List<Attach> attachList) {
		if (attachList != null && attachList.size() > 0) {
			String suffix = "";
			for (Attach attach : attachList) {
				suffix = (attach.Suffix + "").toLowerCase();
				if (!TextUtils.isEmpty(suffix)
						&& (suffix.endsWith("png") || suffix.endsWith("jpg")
								|| suffix.endsWith("png") || suffix
									.endsWith("jpeg"))) {
					mImgUrlList.add(attach.Address);
				} else {
					mSbAttachIds.append(attach.Id + ",");
					mOtherAtachList.add(attach);
				}
			}
		}
	}

	/**
	 * 打开可滑动的图片查看器
	 * 
	 * @param position
	 * @param urls2
	 */
	protected void startImageBrower(int position, ArrayList<String> urls2) {
		Intent intent = new Intent(context, ImagePagerActivity.class);
		ArrayList<String> urlList = new ArrayList<String>();
		for (int i = 0; i < urls2.size(); i++) {
			urlList.add(Global.BASE_URL + urls2.get(i));
		}
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urlList);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
}
