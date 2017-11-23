package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.adapter.TaskClassifyListViewAdapter;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.AddImageHelper;
import com.cedarhd.control.AvartarView;
import com.cedarhd.control.AvartarViewHelper;
import com.cedarhd.control.BoeryunCommentItemView;
import com.cedarhd.control.HorizontalScrollViewAddImage;
import com.cedarhd.helpers.BoeryunTypeMapper;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.DictPickedDialog;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.任务;
import com.cedarhd.models.任务分类;
import com.cedarhd.models.动态;
import com.cedarhd.models.字典;
import com.cedarhd.models.日志评论;
import com.cedarhd.models.评论;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MediaUtils;
import com.cedarhd.utils.MessageUtil;
import com.cedarhd.utils.ShareUtils;
import com.cedarhd.widget.BoeryunDialog;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务详情
 * 
 * @author k
 * 
 */
public class TaskInfoActivity extends BaseActivity implements OnClickListener,
		OnFocusChangeListener {
	private final int SELECT_REQUEST_CODE = 3;
	public static final int RESULT_CODE_SUCCESS = 4;
	public static final int RESULT_CODE_FAILED = 5;
	public static final int SELECT_CLIENT_CODE = 6; // 选择客户名称

	/** 选择多个员工 */
	private static final int CODE_SELECT_USER = 11;
	public static final int EDIT_CONTENT_CODE = 9; // 编辑任务内容

	/** 选择多个员工,复制任务 */
	private static final int CODE_SELECT_USER_SHARE = 14;
	private final int SELECT_SIGNAL_REQUEST_CODE = 31; // 单选

	List<字典> mListStatus = new ArrayList<字典>();

	private int clientId;
	ZLServiceHelper mDataHelper = new ZLServiceHelper();
	private DictionaryHelper dictionaryHelper;
	private DateAndTimePicker dateAndTimePicker;
	private Context context;
	private AddImageHelper addImageHelper;
	private LinearLayout llAttach;// 显示图片附件区域
	private EditText etContent;
	private EditText etClient;
	private TextView tvStartDate;
	private TextView tvPublisher;
	private TextView tvEndDate;
	private TextView tvStatus;

	private LinearLayout llImgPaticipant; // 参与人头像区
	public String mUserSingleSelectId = "0";
	private ImageView btnCancel;
	private ImageView btnSubmit;
	private HorizontalScrollViewAddImage llLoAddImage;

	private boolean isStart; // true表示开始，false表示结束
	// private boolean isEdit; // 默认不可编辑
	public String mUserSelectId = "";
	public String mUserSelectName = "";
	public int states = 1;
	private 任务 mTask;

	private AvartarView avExecutor;// 执行者头像、姓名
	private TextView tvExecutor;
	List<评论> listDiscuss = new ArrayList<评论>();
	private ZLServiceHelper zlServiceHelper = new ZLServiceHelper();
	private HandlerNewContact mHandler = new HandlerNewContact();
	public static final String TAG = "TaskInfoActivity";
	private LinearLayout ll_classify;
	private LinearLayout ll_client;
	private TextView iv_classify;
	private TextView iv_client;
	private TextView tv_classify;
	private TextView tv_client;
	List<任务分类> listClassify = new ArrayList<任务分类>();
	private TaskClassifyListViewAdapter adapter;
	private EditText etParticipant;
	private int classifyid;
	private String permissions = "";

	/** 热门评论评论区标签 */
	private LinearLayout ll_hot_comment;//
	/** 评论内容区域 */
	private LinearLayout ll_discuss_root;// 评论区
	/** 评论文本框 */
	private EditText etDiscuss; //
	/** 发表评论区域 */
	private LinearLayout llPublishDiscuss_root;

	/** 发表评论按钮 */
	private LinearLayout llPublishDiscuss;

	/** 点赞发钻石布局 */
	private LinearLayout ll_support_layout;

	/** 发钻石布局 */
	private LinearLayout ll_zs;
	/** 发钻 */
	private ImageView iv_zs;
	/** 钻石数量 */
	private TextView tv_zs_count;

	/** 发钻石布局 */
	private LinearLayout ll_support;
	/** 点赞 */
	private ImageView iv_support;
	/** 点赞数量 */
	private TextView tv_support_count;

	/** 转发布局按钮 */
	private LinearLayout ll_copy;
	/** 任务转发 */
	private ImageView iv_copy;

	/** 分享布局按钮 */
	private LinearLayout ll_share;
	/** 任务分享 */
	private ImageView iv_share;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_info_new);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findViews();
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			Object obj = bundle.get(TAG);
			if (obj != null) {
				if (obj instanceof 任务) {
					mTask = (任务) obj;
					initViews(mTask);
					initDiamondStyle(mTask.MyDiamondCount);
					initSupportStyle(mTask.MySupportCount);
					setOnclickListener();
				}
			}
		}
		getAddClientPermission();
	}

	/**
	 * 获取添加用户的权限
	 */
	private void getAddClientPermission() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String data = zlServiceHelper.GetPermissions();
					Message msg = mHandler.obtainMessage();
					msg.obj = data;
					msg.what = mHandler.GETPERMISSION;
					mHandler.sendMessage(msg);
				} catch (Exception e) {
					Log.e(TAG, "获取权限异常:" + e.toString());
				}
			}
		}).start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTask != null && mTask.CommentCount > 0) {
			// 加载评论列表
			getDiscussList();
		}

		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(context);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// 加载动态对象
						动态 dynamic = zlServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.Task != null) {
							zlServiceHelper.ReadTask(dynamic.Task, context);

							Message msg = mHandler.obtainMessage();
							msg.obj = dynamic;
							msg.what = mHandler.GET_DYNAMIC_SUCCESS;
							mHandler.sendMessage(msg);
						} else {
							mHandler.sendEmptyMessage(mHandler.GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("erro", e.toString());
				mHandler.sendEmptyMessage(mHandler.GET_DISCUSS_FAILED);
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CODE_FAILED);
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == addImageHelper.CAMERA_TAKE_HELPER
					|| requestCode == addImageHelper.PICKED_PHOTO_WITH_DATA) {
				addImageHelper.refresh(requestCode, data);
			}
			if (requestCode == CODE_SELECT_USER) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				mUserSelectName = bundle.getString("UserSelectName");
				// 选择参与人
				etParticipant.setText(mUserSelectName);

				etParticipant.setTag(false);
				// caculateHeight(llReceiver, mEditTextReceiverName,
				// mUserSelectName);
				LogUtils.i("testKeno", mUserSelectId + "======"
						+ mUserSelectName);
			}

			// 选择要复制的人
			if (requestCode == CODE_SELECT_USER_SHARE) {
				// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				mUserSelectName = bundle.getString("UserSelectName");
				if (TextUtils.isEmpty(mUserSelectId)) {
					return;
				}
				LogUtils.i("testKeno", mUserSelectId + "======"
						+ mUserSelectName);

				String title = "是否转发";
				String content = "该任务将转发给：" + mUserSelectName;
				final BoeryunDialog dialog = new BoeryunDialog(context, false,
						title, content, "取消", "确定");
				dialog.setBoeryunDialogClickListener(
						new BoeryunDialog.OnBoeryunDialogClickListner() {
							@Override
							public void onClick() {
								dialog.dismiss();
							}
						}, new BoeryunDialog.OnBoeryunDialogClickListner() {
							@Override
							public void onClick() {
								// Toast.makeText(
								// context,
								// mUserSelectId + "======"
								// + mUserSelectName,
								// Toast.LENGTH_LONG).show();
								mTask.ExecutorList = mUserSelectId;
								AddTaskToExecutors(mTask);
								dialog.dismiss();
							}
						});
				dialog.show();
			}
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				etContent
						.setText(bundle.getString(TaskContentActivity.Content));
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
			if (requestCode == SELECT_SIGNAL_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
				mUserSingleSelectId = bundle.getString("UserSelectId");
				mUserSingleSelectId = mUserSingleSelectId.replace("'", "")
						.replace(";", "");
				LogUtils.i("kjx21", "sigal------>" + mUserSingleSelectId);
				// setAvatar(mUserSingleSelectId, ivExecutor);
				new AvartarViewHelper(context, mUserSingleSelectId, avExecutor,
						true);
				tvExecutor.setText(dictionaryHelper
						.getUserNameById(mUserSingleSelectId) + "");
			}
			if (requestCode == SELECT_REQUEST_CODE) {// 选择员工姓名
														// 取出字符串
				Bundle bundle = data.getExtras();
				mUserSelectId = bundle.getString("UserSelectId");
				LogUtils.i("kjx21", "------>" + mUserSelectId);
				mUserSelectName = bundle.getString("UserSelectName");
			}
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();

			}
		}
	}

	private void findViews() {
		context = TaskInfoActivity.this;
		dictionaryHelper = new DictionaryHelper(context);
		dateAndTimePicker = new DateAndTimePicker(context);
		tvStartDate = (TextView) findViewById(R.id.tvStartTime_taskinfo2);
		etContent = (EditText) findViewById(R.id.etContent_taskinfo2);
		etClient = (EditText) findViewById(R.id.et_client_name_taskinfo);
		tvEndDate = (TextView) findViewById(R.id.tvEndTime_taskinfo2);
		btnSubmit = (ImageView) findViewById(R.id.ivSubmit_taskinfo2);
		btnCancel = (ImageView) findViewById(R.id.ivCancel_taskinfo2);
		tvStatus = (TextView) findViewById(R.id.tv_state_taskinfo2);
		tvPublisher = (TextView) findViewById(R.id.tv_publiser_taskinfo2);
		llImgPaticipant = (LinearLayout) findViewById(R.id.llImgParticipant_taskinfo2);
		llLoAddImage = (HorizontalScrollViewAddImage) findViewById(R.id.addImg_taskinfo2);
		llAttach = (LinearLayout) findViewById(R.id.ll_attachImg_taskinfo);
		avExecutor = (AvartarView) findViewById(R.id.Executor_taskinfo_new2);
		tvExecutor = (TextView) findViewById(R.id.tv_executor_taskinfo_new2);
		ll_classify = (LinearLayout) findViewById(R.id.ll_classify_task_info);
		ll_client = (LinearLayout) findViewById(R.id.ll_client);
		iv_classify = (TextView) findViewById(R.id.iv_task_info_classify);
		iv_client = (TextView) findViewById(R.id.iv_task_info_client);
		tv_classify = (TextView) findViewById(R.id.tv_task_info_classify);
		etParticipant = (EditText) findViewById(R.id.et_Participant_taskinfo);

		ll_discuss_root = (LinearLayout) findViewById(R.id.ll_root_comment_task_info);
		ll_hot_comment = (LinearLayout) findViewById(R.id.ll_hot_comment_task_info);

		etDiscuss = (EditText) findViewById(R.id.et_task_discuss);
		llPublishDiscuss_root = (LinearLayout) findViewById(R.id.rl_root_publish_discuss_task_info);
		llPublishDiscuss = (LinearLayout) findViewById(R.id.ll_publish_discuss_task_info);
		// 显示发钻 点赞功能
		ll_support_layout = (LinearLayout) findViewById(R.id.ll_support_task_info);
		ll_support_layout.setVisibility(View.VISIBLE);
		ll_zs = (LinearLayout) findViewById(R.id.ll_zs_taskinfo);
		ll_support = (LinearLayout) findViewById(R.id.ll_support_task_info);
		ll_copy = (LinearLayout) findViewById(R.id.ll_copy_taskinfo);
		ll_share = (LinearLayout) findViewById(R.id.ll_share_taskinfo);
		iv_zs = (ImageView) findViewById(R.id.iv_zs_taskinfo);
		iv_support = (ImageView) findViewById(R.id.iv_support_taskinfo);
		tv_zs_count = (TextView) findViewById(R.id.tv_zs_count_taskinfo);
		tv_support_count = (TextView) findViewById(R.id.tv_support_count_taskinfo);
		iv_copy = (ImageView) findViewById(R.id.iv_share_taskinfo);
		iv_share = (ImageView) findViewById(R.id.iv_share_taskinfo);

	}

	public void initViews(任务 obj) {
		clientId = obj.ClientId;
		mUserSelectId = obj.Participant; // 参与人
		mUserSingleSelectId = obj.Executor + "";

		/** 显示评论布局 */
		showCommentLayout();

		if (TextUtils.isEmpty(obj.Attachment)) {
			// 不管有无都要显示，参与人也可添加附件
			// llAttach.setVisibility(View.GONE);
		}

		etContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskInfoActivity.this,
						TaskContentActivity.class);
				intent.putExtra(TaskContentActivity.EDITECONTENT, true);
				intent.putExtra(TaskContentActivity.Content, etContent
						.getText().toString() + "");
				startActivityForResult(intent, EDIT_CONTENT_CODE);
			}
		});

		addImageHelper = new AddImageHelper(this, context, llLoAddImage,
				obj.Attachment, true);
		addImageHelper.reload(obj.Attachment);
		// TODO 设置头像
		LogUtils.i("keno11", "-->" + mUserSingleSelectId);
		tvStatus.setText(obj.getStatusName() + "");

		new AvartarViewHelper(context, mUserSingleSelectId, avExecutor, true);
		tvExecutor.setText(dictionaryHelper
				.getUserNameById(mUserSingleSelectId) + "");
		tvStartDate.setText(obj.AssignTime);// 发布时间
		LogUtils.i("pytime", "-->" + obj.AssignTime);
		tvEndDate.setText(obj.AssignTime); // 任务完成时间
		// tvStatuts.setText(obj.StatusName);
		etContent.setText(obj.Content);
		etClient.setText(dictionaryHelper.getClientNameById(obj.getClientId()));
		tv_support_count.setText("" + obj.SupportCount);
		tv_zs_count.setText("" + obj.DiamondCount);

		btnCancel.setOnClickListener(this);
		// btnSubmit.setVisibility(View.GONE);
		btnSubmit.setOnClickListener(this);
		avExecutor.setOnClickListener(this);
		tvStatus.setOnClickListener(this);

		// TODO 弹框的形式
		// etDiscuss.setFocusable(false);
		// etDiscuss.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// Intent intent = new Intent(TaskInfoActivity.this,
		// DiscussActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable(TAG, item);
		// intent.putExtras(bundle);
		// startActivity(intent);
		// }
		// });

		tv_classify.setText(obj.CategroyName);
		// LogUtils.i("pycate", obj.CategroyName);
		tv_client = (TextView) findViewById(R.id.tv_task_info_client);
		tv_client
				.setText(dictionaryHelper.getClientNameById(obj.getClientId()));
		ll_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectClientName();
			}
		});
		iv_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectClientName();
			}
		});
		tv_client.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				selectClientName();
			}
		});

		ll_support.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int userId = Integer.parseInt(Global.mUser.Id);
				if (userId == mTask.Executor) {
					Toast.makeText(context, "不能给自己点赞哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					publishSupport();
				}

			}
		});

		ll_zs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int userId = Integer.parseInt(Global.mUser.Id);
					if (userId == mTask.Executor) {
						Toast.makeText(context, "不能给自己发钻石哦！",
								Toast.LENGTH_SHORT).show();
					} else {
						giveDiamond();
					}
				} catch (Exception e) {

				}

			}
		});

		ll_copy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 复制任务
				selectMutilUser(CODE_SELECT_USER_SHARE);
			}
		});

		// 分享到qq微信朋友圈
		ll_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = "#波尔云#任务：" + mTask.Content;
				ShareUtils.share(TaskInfoActivity.this, content);
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.getTaskClassify(mHandler);
					List<字典> statusList = zlServiceHelper.getDictList("任务状态");
					if (statusList != null && statusList.size() > 0) {
						Message msg = mHandler.obtainMessage();
						msg.what = mHandler.GET_STATE_SUCCESS;
						msg.obj = statusList;
						mHandler.sendMessage(msg);
					} else {
						mHandler.sendEmptyMessage(mHandler.GET_STATE_FAILED);
					}
				} catch (Exception e) {
					Log.e(TAG, "获取任务分类异常" + e);
				}
			}
		}).start();
		ll_classify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater = LayoutInflater
						.from(TaskInfoActivity.this);// 渲染器
				View dialog = inflater.inflate(R.layout.taskclassifydialog,
						null);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TaskInfoActivity.this);
				builder.setTitle("请选择项目分类");
				builder.setView(dialog);
				final AlertDialog alertdialog = builder.create();
				ListView listView = (ListView) dialog
						.findViewById(R.id.lv_classify);
				adapter = new TaskClassifyListViewAdapter(
						TaskInfoActivity.this, listClassify, null);
				listView.setAdapter(adapter);
				listView.setItemsCanFocus(false);
				listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						任务分类 item = listClassify.get(arg2);
						// item.Id;
						classifyid = item.Id;
						tv_classify.setText(item.getName());
						alertdialog.dismiss();
					}
				});
				alertdialog.show();
			}
		});
		etParticipant.setText(dictionaryHelper
				.getUserNamesById(obj.Participant));

		tvPublisher.setText(dictionaryHelper.getUserNameById(obj.Publisher));
		if ((!Global.mUser.Id.equals(mTask.Publisher + ""))
				&& Global.mUser.Id.equals(mTask.Executor)) {
			// Toast.makeText(context, "只能修改自己发布的任务", Toast.LENGTH_LONG).show();
			btnSubmit.setVisibility(View.GONE);
			requestFocus(false);
		} else {
			// 只有执行人或发布人是自己才可以编辑
			requestFocus(true);
			// etState.setFocusable(false);
			// etContent.setFocusable(false);
		}
	}

	private void showCommentLayout() {
		// 如果显示评论区域
		llPublishDiscuss_root.setVisibility(View.VISIBLE);
		ll_discuss_root.setVisibility(View.VISIBLE);
		ll_hot_comment.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置焦点
	 * 
	 * @param isFocus
	 *            是否获得焦点
	 */
	private void requestFocus(boolean isFocus) {
		tvStatus.setVisibility(View.VISIBLE);
		// viewState.setVisibility(View.VISIBLE);
		tvStatus.setEnabled(isFocus);
		tvStartDate.setEnabled(isFocus);
		tvEndDate.setEnabled(isFocus);
		// ivExecutor.setEnabled(isFocus);
		llImgPaticipant.setEnabled(isFocus);
		// ivParticipant.setEnabled(isFocus);
		etClient.setEnabled(isFocus);
		etContent.setEnabled(isFocus);
		// avAddParticipant.setEnabled(isFocus);
		avExecutor.setEnabled(isFocus);
		ll_classify.setEnabled(isFocus);
		iv_classify.setEnabled(isFocus);
		tv_classify.setEnabled(isFocus);
		ll_client.setEnabled(isFocus);
		iv_client.setEnabled(isFocus);
		tv_client.setEnabled(isFocus);
		etParticipant.setEnabled(isFocus);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ivCancel_taskinfo2: // 取消
			finish();
			break;
		case R.id.Executor_taskinfo_new2: // 选择执行人
			addExecutor();
			break;
		case R.id.llImgParticipant_taskinfo2: // 选择参与人
			// case R.id.Participant_taskinfo_new2:
			// addParticipant();
			// break;
		case R.id.tvStartTime_taskinfo2:
			dateAndTimePicker.showDateWheel(tvStartDate);
			break;
		case R.id.tvEndTime_taskinfo2:
			isStart = false;
			dateAndTimePicker.showDateWheel(tvStartDate);
			break;
		case R.id.et_client_name_taskinfo:
			selectClientName();
			break;
		case R.id.ivSubmit_taskinfo2: // 提交
			submit();
			break;
		case R.id.tv_state_taskinfo2: // 状态
			new DictPickedDialog(context, mListStatus, tvStatus)
					.showDicDialog();
			break;
		}
	}

	/**
	 * 提交
	 */
	private void submit() {
		if (checkValid()) {
			LogUtils.i("tag", "isCheckValid");
			ORMDataHelper helper = ORMDataHelper.getInstance(this);
			// TODO 没有修改任务的接口
			// String participant = etParticipant.getText().toString();
			String startDate = tvStartDate.getText().toString();
			String endDate = tvEndDate.getText().toString();
			String content = etContent.getText().toString();

			mTask.Participant = mUserSelectId;
			mTask.Executor = Integer.parseInt(mUserSingleSelectId);
			mTask.Content = content;
			mTask.Time = startDate;
			mTask.AssignTime = endDate;
			mTask.ClientId = clientId;
			mTask.Categroy = classifyid;
			try {
				mTask.Status = Integer.parseInt(tvStatus.getTag().toString());
			} catch (Exception e) {
				mTask.Status = 1;
			}
			LogUtils.i("update", mTask.toString());

			ProgressDialogHelper.show(context);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						List<String> photoPathList = addImageHelper
								.getPhotoList();
						if (photoPathList != null && photoPathList.size() > 0) {
							String attachMent = zlServiceHelper
									.uploadAttachPhotos(photoPathList, null);
							LogUtils.i("Attachment", photoPathList.get(0));
							LogUtils.i("Attachment", attachMent);
							mTask.Attachment += "," + attachMent;
						}
						boolean isSuccessed = mDataHelper.EditTask(mTask);
						if (isSuccessed) {
							mHandler.sendEmptyMessage(HandlerNewContact.UPDATE_TASK_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(HandlerNewContact.UPDATE_TASK_FAILED);
						}
					} catch (Exception e) {
						mHandler.sendEmptyMessage(HandlerNewContact.UPDATE_TASK_FAILED);
					}
				}
			}).start();

		}
	}

	/**
	 * 绑定监听事件
	 */
	private void setOnclickListener() {
		llImgPaticipant.setOnClickListener(this);
		// ivExecutor.setOnClickListener(this);
		// ivParticipant.setOnClickListener(this);
		tvStartDate.setOnClickListener(this);
		tvEndDate.setOnClickListener(this);
		etClient.setOnClickListener(this);
		// tvStatuts.setOnClickListener(this);

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
		etParticipant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectMutilUser(CODE_SELECT_USER);
			}
		});
		etParticipant.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					selectMutilUser(CODE_SELECT_USER);
				}
			}
		});

		llPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String discussContent = etDiscuss.getText().toString();
				if (!TextUtils.isEmpty(discussContent)) {
					publishDisscuss(discussContent);
				} else {
					Toast.makeText(context, "请输入评论内容", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

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
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		// bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_SIGNAL_REQUEST_CODE);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		if (hasFocus) {
			switch (id) {
			case R.id.etStartTime_addtask:
				// isStart = true;
				// showDialog(SHOW_STARTDATA);
				// setDateTime();
				dateAndTimePicker.showDateWheel(tvStartDate);
				break;
			case R.id.etEndTime_addtask:
				isStart = false;
				dateAndTimePicker.showDateWheel(tvStartDate);
				break;
			// case R.id.tvTitle_taskinfo: // 标题
			// new SpeechDialogHelper(context, this, etTitle, false);
			// break;
			default:
				break;
			}
		}
	}

	// 从客户列表选择客户名称
	private void selectClientName() {
		Intent intent = new Intent(TaskInfoActivity.this,
				ClientListActivity.class);
		intent.putExtra(ClientListActivity.SELECT_CLIENT, true);
		startActivityForResult(intent, SELECT_CLIENT_CODE);
	}

	/**
	 * 提交校验
	 * 
	 * @return
	 */
	private boolean checkValid() {
		if (etContent.getText() == null
				|| etContent.getText().toString().replaceAll(" ", "").length() <= 0) {
			MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败", "内容不能为空！");
			return false;
		} else if (etContent.getText().toString().trim().length() > 1000) {
			MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败",
					"内容不能多于1000个字！");
			return false;
		}

		// 判断接收人是否为空
		if (TextUtils.isEmpty(mUserSelectId)) {
			// MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败",
			// "参与人不能为空！");
			// return false;
		}

		if (tvStartDate.getText().toString().contains("时间")
				|| tvStartDate.getText().toString().replaceAll(" ", "")
						.length() <= 0) {
			MessageUtil.AlertMessage(TaskInfoActivity.this, "保存失败", "请选择开始时间！");
			return false;
		}
		return true;
	}

	public void onClick_RandomFace1(View view) {
		int randomId = 1;
		try {
			// 从R.drawable类中获得相应资源ID（静态变量）的Field对象
			Field field = R.drawable.class.getDeclaredField("face" + randomId);
			// 获得资源ID的值，也就是静态变量的值
			int resourceId = Integer.parseInt(field.get(null).toString());
			// 根据资源ID获得资源图像的Bitmap对象
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					resourceId);
			// 根据Bitmap对象创建ImageSpan对象
			ImageSpan imageSpan = new ImageSpan(this, bitmap);
			// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
			SpannableString spannableString = new SpannableString("face");
			// 用ImageSpan对象替换face
			spannableString.setSpan(imageSpan, 0, 4,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// 将随机获得的图像追加到EditText控件的最后
			// etDiscussContent.append(spannableString);
		} catch (Exception e) {
		}
	}

	public void onClick_RandomFace2(View view) {
		int randomId = 2;
		try {
			// 从R.drawable类中获得相应资源ID（静态变量）的Field对象
			Field field = R.drawable.class.getDeclaredField("face" + randomId);
			// 获得资源ID的值，也就是静态变量的值
			int resourceId = Integer.parseInt(field.get(null).toString());
			// 根据资源ID获得资源图像的Bitmap对象
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					resourceId);
			// 根据Bitmap对象创建ImageSpan对象
			ImageSpan imageSpan = new ImageSpan(this, bitmap);
			// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
			SpannableString spannableString = new SpannableString("face");
			// 用ImageSpan对象替换face
			spannableString.setSpan(imageSpan, 0, 4,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			// 将随机获得的图像追加到EditText控件的最后
			// etDiscussContent.append(spannableString);
		} catch (Exception e) {
		}
	}

	/** 获取任务评论列表 */
	private void getDiscussList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = mHandler.obtainMessage();
				try {
					List<评论> commentList = zlServiceHelper.getTaskDiscuss(mTask
							.getId() + "");
					if (commentList != null && commentList.size() > 0) {
						msg.obj = commentList;
						msg.what = mHandler.GET_DISCUSS_SUCCESS;
						mHandler.sendMessage(msg);
					} else {
						mHandler.sendEmptyMessage(mHandler.GET_DISCUSS_FAILED);
					}
				} catch (Exception e) {
					Log.e(TAG, "获取评论信息异常:" + e);
					mHandler.sendEmptyMessage(mHandler.GET_DISCUSS_FAILED);
				}
			}
		}).start();
	}

	/** 发表评论 */
	private void publishDisscuss(final String discussContent) {
		if (TextUtils.isEmpty(discussContent)) {
			Toast.makeText(context, "评论内容不允许为空！", Toast.LENGTH_SHORT).show();
		} else {
			ProgressDialogHelper.show(context);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						boolean isResult = zlServiceHelper.publishTaskDiscuss(
								mTask.Id + "", discussContent);
						if (isResult) {
							mHandler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_FAILED);
						}
					} catch (Exception e) {
						mHandler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_FAILED);
					}
				}
			}).start();
		}
	}

	/** 点赞 */
	private void publishSupport() {
		if (mTask.MySupportCount > 0) {
			// Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
			mTask.MySupportCount--;
			mTask.SupportCount--;
			tv_support_count.setText("" + mTask.SupportCount);
			initSupportStyle(mTask.MySupportCount);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSucceed = zlServiceHelper.RemoveSupport(
								formUserId, 3, mTask.Id);
						if (isSucceed) {
							// setRetrunResult();
						} else {

						}
					} catch (Exception e) {
						// handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		} else {
			ProgressDialogHelper.show(context, "点了一次赞...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						boolean isResult = zlServiceHelper.giveSupport(
								Global.mUser.Id, mTask.getExecutor() + "", 3,
								mTask.Id);
						if (isResult) {
							mHandler.sendEmptyMessage(HandlerNewContact.SUPPORT_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(HandlerNewContact.SUPPORT_FAILED);
						}
					} catch (Exception e) {
						mHandler.sendEmptyMessage(HandlerNewContact.SUPPORT_FAILED);
					}
				}
			}).start();

		}
	}

	/**
	 * 给指定任务发砖石
	 */
	private void giveDiamond() {
		if (mTask.MyDiamondCount > 0) {
			mTask.MyDiamondCount--;
			mTask.DiamondCount--;
			tv_zs_count.setText("" + mTask.DiamondCount);
			initDiamondStyle(mTask.MyDiamondCount);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSucceed = zlServiceHelper.removeDiamond(
								formUserId, 3, mTask.Id);
						if (isSucceed) {
							// setRetrunResult();
						} else {

						}
					} catch (Exception e) {
						// handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSuccessed = zlServiceHelper.giveDiamond(1,
								formUserId, mTask.Executor, 3, mTask.Id);
						if (isSuccessed) {
							mHandler.sendEmptyMessage(HandlerNewContact.GIVE_DIAMOND_SUCCESS);
						} else {
							mHandler.sendEmptyMessage(HandlerNewContact.GIVE_DIAMOND_FAILED);
						}
					} catch (Exception e) {
						mHandler.sendEmptyMessage(HandlerNewContact.GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		}

	}

	/***
	 * 钻石数量加1
	 */
	private void addCount(TextView tv_count) {
		try {
			String countStr = tv_count.getText().toString();
			int count = Integer.parseInt(countStr);
			count++;
			tv_count.setText("" + count);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "" + e);
		}
	}

	/***
	 * 根据样我的发钻石数量显示 字体图标对应的颜色
	 * 
	 * @param count
	 */
	private void initDiamondStyle(int count) {
		if (count == 0) {
			iv_zs.setImageResource(R.drawable.ico_zs_big);
			tv_zs_count
					.setTextColor(getResources().getColor(R.color.text_info));
		} else {
			iv_zs.setImageResource(R.drawable.ico_zs_big2);
			tv_zs_count
					.setTextColor(getResources().getColor(R.color.theme_new));
		}
	}

	/***
	 * 根据样我的赞数量显示 字体图标对应的颜色
	 * 
	 * @param count
	 */
	private void initSupportStyle(int count) {
		if (count == 0) {
			iv_support.setImageResource(R.drawable.ico_support_normal);
			tv_support_count.setTextColor(getResources().getColor(
					R.color.text_info));
		} else {
			iv_support.setImageResource(R.drawable.ico_support2);
			tv_support_count.setTextColor(getResources().getColor(
					R.color.theme_new));
		}
	}

	/***
	 * 选择多个员工
	 * 
	 * @param requestCode
	 *            请求码：区分不同请求
	 */
	private void selectMutilUser(int requestCode) {
		Intent intent = new Intent(TaskInfoActivity.this,
				User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, requestCode);
	}

	/***
	 * 转发任务给多个执行人
	 * 
	 * @param task
	 */
	private void AddTaskToExecutors(final 任务 task) {
		ProgressDialogHelper.show(context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isSucceed = false;
				try {
					isSucceed = zlServiceHelper.AddTaskToExecutors(task);
				} catch (Exception e) {
					e.printStackTrace();
					isSucceed = false;
				}
				if (isSucceed) {
					mHandler.sendEmptyMessage(HandlerNewContact.ADD_EXECUTORS_SUCCESS);
				} else {
					mHandler.sendEmptyMessage(HandlerNewContact.ADD_EXECUTORS_FAILED);
				}
			}
		}).start();
	}

	/**
	 * 新评论
	 * 
	 * @author BOHR
	 * 
	 */
	public class HandlerNewContact extends Handler {
		public static final int UPDATE_TASK_SUCCESS = 2;
		public static final int UPDATE_TASK_FAILED = 3;
		public static final int GETPERMISSION = 4; // 获取客户权限

		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败
		private final int GET_CLASSIFY_SUCCESS = 7; // 获得任务分类列表成功
		private final int GET_CLASSIFY_FAILED = 8; // 获得任务分类列表失败

		public final int GET_DYNAMIC_SUCCESS = 9; // 点击动态，获取日志成功
		public final int GET_DYNAMIC_FAILED = 10; // 点击动态，获取日志失败

		private final int GET_STATE_SUCCESS = 17; // 获得任务分类列表成功
		private final int GET_STATE_FAILED = 18; // 获得任务分类列表失败

		/** 发表评论 */
		public static final int PUBLISH_DISCUSS_SUCCESS = 21;
		public static final int PUBLISH_DISCUSS_FAILED = 22;

		/** 发钻石成功 */
		public static final int GIVE_DIAMOND_SUCCESS = 23;
		/** 发钻失败 */
		public static final int GIVE_DIAMOND_FAILED = 34;

		/** 点赞成功 */
		public static final int SUPPORT_SUCCESS = 25;
		/** 点赞失败 */
		public static final int SUPPORT_FAILED = 26;

		/** 转发多个执行人成功 */
		public static final int ADD_EXECUTORS_SUCCESS = 27;
		/** 转发多个执行人失败 */
		public static final int ADD_EXECUTORS_FAILED = 28;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GETPERMISSION:
				permissions = (String) msg.obj;
				if (permissions != null) {
					if (permissions.contains("14")) {
						// TODO 隐藏客户模块
						// ll_client.setVisibility(View.VISIBLE);
					}
				}
				break;
			case UPDATE_TASK_SUCCESS: // 保存成功
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "保存成功！");
				setResult(RESULT_OK);
				finish();
				break;
			case UPDATE_TASK_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "保存失败！");
				break;
			case GET_DISCUSS_SUCCESS:// 获得评论列表成功
				listDiscuss = (List<评论>) msg.obj;
				List<日志评论> commentList = BoeryunTypeMapper
						.MapperTo评论中文(listDiscuss);
				new BoeryunCommentItemView(context, commentList,
						ll_discuss_root).createCommentView();
				break;
			case GET_DISCUSS_FAILED:// 获得评论列表失败
				break;
			case GET_CLASSIFY_SUCCESS: // 获取任务分类列表成功
				// tv_classify.setText("");
				listClassify = (List<任务分类>) msg.obj;
				LogUtils.i("pytaskinfolist", listClassify.toString());
				break;
			case GET_CLASSIFY_FAILED:// 获得任务分类列表失败

				break;

			case GET_DYNAMIC_SUCCESS:
				ProgressDialogHelper.dismiss();
				动态 dynamic = (动态) msg.obj;
				mTask = (任务) dynamic.Task;
				initViews(mTask);
				setOnclickListener();
				break;
			case GET_DYNAMIC_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载任务失败，请稍后重试", Toast.LENGTH_LONG)
						.show();
				break;
			case GET_STATE_SUCCESS:// 获取任务状态列表成功
				mListStatus = (List<字典>) msg.obj;
				break;
			case GET_STATE_FAILED:// 获取任务状态列表失败
				break;
			case PUBLISH_DISCUSS_SUCCESS:
				ProgressDialogHelper.dismiss();
				// 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				etDiscuss.setText("");
				getDiscussList();
				InputSoftHelper.hiddenSoftInput(context, etDiscuss);
				break;
			case PUBLISH_DISCUSS_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "评论失败！");
				break;
			case GIVE_DIAMOND_SUCCESS:
				ProgressDialogHelper.dismiss();
				addCount(tv_zs_count);
				MediaUtils.startMusic(context, R.raw.collect_diamonds_02);
				mTask.DiamondCount += 1;
				mTask.MyDiamondCount += 1;
				initDiamondStyle(mTask.MyDiamondCount);
				// setRetrunResult();
				break;
			case GIVE_DIAMOND_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "发钻失败！");
				break;
			case SUPPORT_SUCCESS:
				ProgressDialogHelper.dismiss();
				addCount(tv_support_count);
				mTask.SupportCount += 1;
				mTask.MySupportCount += 1;
				initSupportStyle(mTask.MySupportCount);
				// setRetrunResult();
				break;
			case SUPPORT_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "点赞失败！");
				break;
			case ADD_EXECUTORS_SUCCESS:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "转发成功！");
				break;
			case ADD_EXECUTORS_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "转发失败！");
				break;
			default:
				break;
			}
		}
	}

}
