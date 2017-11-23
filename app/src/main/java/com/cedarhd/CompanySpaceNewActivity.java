package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.User;
import com.cedarhd.models.帖子;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.List;

/***
 * 新建帖子-分享
 * 
 * @author Administrator
 * 
 */
public class CompanySpaceNewActivity extends BaseActivity {

	public static final int RESULT_CODE_SUCCESS = 0;
	public static final int RESULT_CODE_FAILED = 1;
	public static final int UPDATA_FAILED = 2;
	public static final int UPDATA_SUCCESED = 3;
	public static final int EDIT_CONTENT_CODE = 7;
	private static final int SHOW_WriteCompanySpace = -1;
	private static final int CODE_SELECT_USER = 11;

	/** 可见区域类型 */
	final String[] mAreaTypeArrs = new String[] { "公开", "部分可见", "不给谁看" };

	private Context mContext;
	private ImageView ivBack;
	private ImageView ivSave;
	private ProgressBar pBar;
	private EditText mEditTextContent;

	/** 可见区域 */
	private LinearLayout llArea;
	private TextView tvArea;
	private ImageView ivArea;

	private int board = 0;

	/** 可见范围类型 */
	private int mSelectAreaType = 0;

	private HorizontalScrollViewAddImage addImg_companyspaceinfo;
	private DictIosPickerBottomDialog mDictIosPickerBottomDialog;
	private AddImageHelper addImageHelper;
	private List<String> photoPathList; // 要上传照片路径列表
	String mContent = "";
	String mReleaseTime = "";
	String mClassify = "";
	// 指定查看人员编号
	String mAreaUsersId;

	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private Handler upDataHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATA_FAILED:
				ProgressDialogHelper.dismiss();
				// 提示信息显示
				pBar.setVisibility(View.GONE);
				MessageUtil.ToastMessage(CompanySpaceNewActivity.this, "发布失败！");
				break;
			case UPDATA_SUCCESED:
				ProgressDialogHelper.dismiss();
				// 提示信息显示
				MessageUtil.ToastMessage(CompanySpaceNewActivity.this, "发布成功！");
				setResult(RESULT_CODE_SUCCESS);
				CompanySpaceListActivity.isResume = true;
				finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_new);
		findviews();
		setonclick();
	}

	private void findviews() {
		// 初始化控件
		mContext = CompanySpaceNewActivity.this;
		ivBack = (ImageView) findViewById(R.id.iv_back_share);
		ivSave = (ImageView) findViewById(R.id.iv_save_share);
		pBar = (ProgressBar) findViewById(R.id.pbar_share_new);
		mEditTextContent = (EditText) findViewById(R.id.et_content_share);
		tvArea = (TextView) findViewById(R.id.tv_area_share_new);
		llArea = (LinearLayout) findViewById(R.id.ll_select_area_share_new);
		ivArea = (ImageView) findViewById(R.id.iv_area_share_new);
		mEditTextContent.setFocusable(false);
		addImg_companyspaceinfo = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_share);
		addImageHelper = new AddImageHelper(this, CompanySpaceNewActivity.this,
				addImg_companyspaceinfo, null, true);
		mEditTextContent.setHeight(100);
		mEditTextContent.setHint("请输入内容..");
		mReleaseTime = ViewHelper.getDateString();// 当前时间
		mDictIosPickerBottomDialog = new DictIosPickerBottomDialog(mContext);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	private void setonclick() {
		// 添加监听事件
		ivBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		ivSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isNullContent()) {
					showShortToast("请填写分享内容");
					return;
				}

				mDictIosPickerBottomDialog.show("确认发布");
				mDictIosPickerBottomDialog
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								LogUtils.i("attach", "--->"
										+ addImageHelper.sbAttachIds);
								photoPathList = addImageHelper.getPhotoList();
								for (String path : photoPathList) {
									LogUtils.i("attachPath", path);
								}
								if (photoPathList.size() > 0) {
									pBar.setVisibility(View.VISIBLE);
									pBar.setMax(photoPathList.size());
								}

								ProgressDialogHelper.show(mContext);

								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											final String attachIds = mDataHelper
													.uploadAttachPhotos(
															photoPathList, pBar);

											帖子 tiezi = new 帖子();
											// tiezi.setTitle(mContent);
											tiezi.setBoard(board);
											tiezi.setContent(mContent);
											tiezi.setAttachment(attachIds);
											if (mSelectAreaType == 1) {
												tiezi.其他可看人 = mAreaUsersId;
											} else if (mSelectAreaType == 2) {
												tiezi.不可看人 = mAreaUsersId;
											}
											mDataHelper.addCompanySpace(tiezi,
													upDataHandler);
										} catch (Exception e) {
											LogUtils.e("out", "发帖异常" + e);
										}
									}
								}).start();

							}
						});
			}
		});

		mEditTextContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CompanySpaceNewActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});

		llArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDictIosPickerBottomDialog.show(mAreaTypeArrs);
				mDictIosPickerBottomDialog
						.setOnSelectedListener(new OnSelectedListener() {
							@Override
							public void onSelected(int index) {
								mSelectAreaType = index;
								switch (index) {
								case 0:
									ivArea.setImageResource(R.drawable.ico_share_are);
									tvArea.setText(mAreaTypeArrs[index]);
									break;
								case 1:
									ivArea.setImageResource(R.drawable.ico_share_area_pressed);
									UserBiz.selectMultiUser(mContext, "");
									break;
								case 2:
									ivArea.setImageResource(R.drawable.ico_share_area_pressed);
									UserBiz.selectMultiUser(mContext, "");
									break;
								default:
									break;
								}
							}
						});
			}
		});

	}

	private boolean isNullContent() {
		mContent = mEditTextContent.getText().toString().replaceAll(" ", "");
		return TextUtils.isEmpty(mContent);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				// etContent = bundle.getInt(ClientListActivity.ClientId);
				mEditTextContent.setText(bundle
						.getString(TaskContentActivity.Content));

			}
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
			} else if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			} else if (requestCode == UserBiz.SELECT_MULTI_USER_REQUEST_CODE) {
				User selectUser = UserBiz.onActivityMultiUserSelected(
						requestCode, resultCode, data);
				if (selectUser != null
						&& !TextUtils.isEmpty(selectUser.getUserIds())) {
					mAreaUsersId = selectUser.getUserIds();
					tvArea.setText(mAreaTypeArrs[mSelectAreaType] + "["
							+ selectUser.getUserNames() + "]");
				}
			}
		}
	}

}
