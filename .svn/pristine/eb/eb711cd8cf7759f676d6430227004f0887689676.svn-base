package com.cedarhd;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunCommentItemView;
import com.cedarhd.fragment.WorkLogFragment;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.InputSoftHelper;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.任务;
import com.cedarhd.models.动态;
import com.cedarhd.models.日志;
import com.cedarhd.models.日志评论;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MediaUtils;
import com.cedarhd.utils.MessageUtil;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日志详情/新建日志
 * 
 * @author KJX
 * 
 */
@SuppressLint("NewApi")
public class WorkLogActivity extends BaseActivity {
	public static final String TAG = "WorkLogActivity";

	private boolean isCommentSuccessed;
	/** 编辑日志内容 */
	public static final int EDIT_CONTENT_CODE = 7; //
	/** 发钻石成功 */
	public static final int GIVE_DIAMOND_SUCCESS = 21;
	/** 点赞失败 */
	public static final int GIVE_DIAMOND_FAILED = 22;

	/***
	 * 日志内容文本框是否可以编辑
	 */
	private boolean mIsEdit = true;
	private ZLServiceHelper zlServiceHelper;
	private DictionaryHelper dictionaryHelper;
	private 日志 mLog;
	private TextView mTextViewTime;
	private TextView mTextViewPersonnelName;
	private EditText mEditTextContent;
	/** 保存日志按钮 */
	private ImageView ivSave;
	private Context context;
	public static HandlerNewContact handler;

	List<日志评论> listDiscuss = new ArrayList<日志评论>();
	/** 热门评论评论区标签 */
	private LinearLayout ll_hot_comment_worklog;//
	/** 评论内容区域 */
	private LinearLayout ll_discuss_root;// 评论区
	/** 评论文本框 */
	private EditText etDiscuss; //

	/***
	 * 评论点赞区域
	 */
	private LinearLayout ll_supportAndDiamond_layout_worklog;
	/*** 点赞 */
	private ImageView ivSupport;
	/** 点赞数量 */
	private TextView tv_support_count;

	/** 点赞按钮 */
	private LinearLayout llSupport;

	/*** 发钻石 */
	private ImageView ivZs;
	/** 钻石数量 */
	private TextView tv_zs_count;
	/** 发钻石按钮 */
	private LinearLayout llZs;

	/** 发表评论区域 */
	private LinearLayout llPublishDiscuss_root;
	/** 发表评论按钮 */
	private LinearLayout llPublishDiscuss;
	/** 语音输入评论 */
	private ImageView ivSpeechDiscuss;

	/** 明日计划 */
	private LinearLayout llTomorrowPlan;

	/** 明日计划输入框 */
	private EditText etTomorrowPlan;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_log);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		findViews();
		setOnClickListener();
		setOnContentClickListener();
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			// 获取上级页面Intent中携带的 日志实体
			Object obj = bundle.getSerializable("Log");
			if (obj != null) {
				if (obj instanceof 日志) {
					mLog = (日志) obj;
					initLog();
				}
			}
		}

		// 如果日志为空，查看当天日志是否存在，存在则显示
		if (mLog == null) {
			showTodayLog();
		}
	}

	/** 显示当天日志 */
	private void showTodayLog() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mLog = zlServiceHelper.getTodaykLog();
				if (mLog != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							initLog();
						}
					});
				} else {
					mLog = new 日志();
				}
			}
		}).start();
	}

	/** 显示日志信息 */
	private void initLog() {
		LogUtils.i("keno日志", "initLog。。。");
		if (mLog != null && mLog.Id != 0) {
			getLogCommentList();
			tv_support_count.setText(mLog.SupportCount + "");
			tv_zs_count.setText(mLog.DiamondCount + "");
			initDiamondStyle(mLog.MyDiamondCount);
			initSupportStyle(mLog.MySupportCount);
			// 评论点赞区域可见
			ll_supportAndDiamond_layout_worklog.setVisibility(View.VISIBLE);

			if (!TextUtils.isEmpty(mLog.Time)
					&& !DateDeserializer.dateIsBeforoNow(mLog.Time)
					&& Global.mUser.Id.equals(mLog.Personnel + "")) {
				// 如果日志时间为当天，并且日志发布人是自己,可编辑
				// mEditTextContent.setEnabled(true);
				mIsEdit = true;
				llTomorrowPlan.setVisibility(View.VISIBLE);
			} else {
				// mEditTextContent.setEnabled(false);
				// mEditTextContent.setClickable(false);
				mEditTextContent.setFocusable(false);
				mIsEdit = false;
				ivSave.setVisibility(View.GONE);
				llTomorrowPlan.setVisibility(View.GONE);
			}
			setOnContentClickListener();

			mTextViewTime.setText(mLog.getTime() != null ? mLog.getTime()
					: ViewHelper.getDateString());
			if (mLog.getPersonnelName() != null) {
				mTextViewPersonnelName.setText(mLog.getPersonnelName());
			} else {
				// mTextViewPersonnelName.setText(Global.mUser.UserName);
				mTextViewPersonnelName.setText(dictionaryHelper
						.getUserNameById(mLog.Personnel));
			}
			// 显示任务内容
			String content = mLog.getContent();
			if (content != null) {
				LogUtils.i("worklog2", content);
				if (content.contains("\r")) {
					content = content.replaceAll("\\r", "\n\r");
					LogUtils.i("worklog2", content);
				}
				mEditTextContent.setText(content);
			}

			String planContent = mLog.PlanContent;
			if (!TextUtils.isEmpty(planContent)) {
				llTomorrowPlan.setVisibility(View.VISIBLE);
				etTomorrowPlan.setText(planContent);
			}

			// 如果不是新建日志，隐藏发表评论区域
			llPublishDiscuss_root.setVisibility(View.VISIBLE);
			ll_discuss_root.setVisibility(View.VISIBLE);
			ll_hot_comment_worklog.setVisibility(View.VISIBLE);
		} else {
			// 新建：默认当前员工，当前时间
			mTextViewPersonnelName.setText(dictionaryHelper
					.getUserNameById(Global.mUser.Id));
			mTextViewTime.setText(ViewHelper.getDateString());
			// 当天新建日志，可以新建明日计划
			llTomorrowPlan.setVisibility(View.VISIBLE);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 监听信鸽 Notification点击打开的通知
		XGPushClickedResult clickedResult = XGPushManager
				.onActivityStarted(this);
		if (clickedResult != null) {
			// Toast.makeText(this, "来自信鸽：" + clickedResult.toString(),
			// Toast.LENGTH_LONG).show();
			LogUtils.i("clickedResult", clickedResult.toString());
			String customContent = clickedResult.getCustomContent();
			LogUtils.i("CustomContent", customContent);
			try {
				JSONObject jo = new JSONObject(customContent);
				// 获取动态类型 和 数据编号
				final String dynamicType = jo.getString("dynamicType");
				final String dataId = jo.getString("dataId");
				LogUtils.i("dynami", dynamicType + "--" + dataId);
				ProgressDialogHelper.show(context, "日志加载中");
				new Thread(new Runnable() {
					@Override
					public void run() {
						动态 dynamic = zlServiceHelper.LoadDynamicById(
								dynamicType, dataId);
						if (dynamic != null && dynamic.Log != null) {
							// 设置日志为已读
							zlServiceHelper.ReadLog(dynamic.Log, context);

							// 发送到handler中进行处理
							Message msg = handler.obtainMessage();
							msg.obj = dynamic;
							msg.what = handler.GET_DYNAMIC_SUCCESS;
							handler.sendMessage(msg);
						} else {
							handler.sendEmptyMessage(handler.GET_DYNAMIC_FAILED);
						}
					}
				}).start();
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtils.i("erro", e.toString());
				handler.sendEmptyMessage(handler.GET_DYNAMIC_FAILED);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == EDIT_CONTENT_CODE) {
				// 取出任务内容
				Bundle bundle = data.getExtras();
				mEditTextContent.setText(bundle
						.getString(TaskContentActivity.Content));

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 获取日志评论列表 */
	private void getLogCommentList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					zlServiceHelper.getLogDiscuss(mLog.getId() + "", handler);
				} catch (Exception e) {
					Toast.makeText(context, "获取评论信息异常", 0).show();
				}
			}
		}).start();
	}

	public void setOnClickListener() {

		ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
		imageViewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				back();
			}
		});

		ivSave = (ImageView) findViewById(R.id.iv_save_worklog);
		ivSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String logContent = mEditTextContent.getText().toString();
				String planContent = etTomorrowPlan.getText().toString();
				if (mEditTextContent.getText() == null
						|| mEditTextContent.getText().toString()
								.replaceAll(" ", "").length() <= 0) {
					MessageUtil.AlertMessage(context, "保存失败", "日志内容不能为空！");
					return;
				} else {
					logContent = mEditTextContent.getText().toString()
							.replaceAll(" ", "");
					mLog.Content = logContent;
				}
				publishWorkLog(logContent, planContent);
				saveTomorrowPlan();
			}
		});

		// 发表 评论
		llPublishDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publishDisscuss(etDiscuss.getText().toString());
			}
		});

		ivSpeechDiscuss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SpeechDialogHelper(context, etDiscuss, true);
			}
		});

		llSupport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int userId = Integer.parseInt(Global.mUser.Id);
				if (userId != mLog.Personnel) {
					publishSupport();
				} else {
					Toast.makeText(context, "不能给自己点赞哦！", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		llZs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int userId = Integer.parseInt(Global.mUser.Id);
					if (userId != mLog.Personnel) {
						giveDiamond(mLog);
					} else {
						Toast.makeText(context, "不能给自己发钻石哦！",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
	}

	/***
	 * 设置文本点击事件，编辑状态下弹出语音对话框，不可编辑状态下复制内容到剪贴板
	 */
	private void setOnContentClickListener() {
		mEditTextContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsEdit) {
					new SpeechDialogHelper(context, WorkLogActivity.this,
							mEditTextContent, false);
				} else {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("simple text",
							mLog.Content + "");
					clipboard.setPrimaryClip(clip);
					Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
	}

	@SuppressLint("NewApi")
	public void findViews() {
		context = WorkLogActivity.this;
		dictionaryHelper = new DictionaryHelper(this);
		handler = new HandlerNewContact();
		zlServiceHelper = new ZLServiceHelper();
		mTextViewTime = (TextView) findViewById(R.id.textViewTime_log);
		mTextViewPersonnelName = (TextView) findViewById(R.id.textViewPersonnelName_log);
		mEditTextContent = (EditText) findViewById(R.id.editTextContent);
		ivSupport = (ImageView) findViewById(R.id.iv_support_worklog);
		llSupport = (LinearLayout) findViewById(R.id.ll_publish_support_worklog);
		ivZs = (ImageView) findViewById(R.id.iv_zs_worklog);
		llZs = (LinearLayout) findViewById(R.id.ll_publish_zs_worklog);
		ll_hot_comment_worklog = (LinearLayout) findViewById(R.id.ll_hot_comment_worklog);
		ll_discuss_root = (LinearLayout) findViewById(R.id.ll_root_comment_worklog);
		etDiscuss = (EditText) findViewById(R.id.et_work_log_discuss);
		// etDiscuss.setFocusable(false);
		llPublishDiscuss_root = (LinearLayout) findViewById(R.id.rl_root_publish_discuss_worklog);
		llPublishDiscuss = (LinearLayout) findViewById(R.id.ll_publish_discuss);
		ivSpeechDiscuss = (ImageView) findViewById(R.id.iv_talking_log_discuss);
		llPublishDiscuss_root.setBackgroundColor(0xFFEEEEEE);
		llTomorrowPlan = (LinearLayout) findViewById(R.id.ll_tomorrow_plan_notice_info);
		etTomorrowPlan = (EditText) findViewById(R.id.et_tomorrow_plan_worklog);
		tv_zs_count = (TextView) findViewById(R.id.tv_zs_count_worklog);
		tv_support_count = (TextView) findViewById(R.id.tv_support_count_worklog);
		ll_supportAndDiamond_layout_worklog = (LinearLayout) findViewById(R.id.ll_supportAndDiamond_layout_worklog);
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
						boolean isResult = zlServiceHelper.publishLogDiscuss(
								mLog.Id + "", discussContent);
						if (isResult) {
							handler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_SUCCESS);
						} else {
							handler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_FAILED);
						}
					} catch (Exception e) {
						LogUtils.i("erro", "match_parent");
						handler.sendEmptyMessage(HandlerNewContact.PUBLISH_DISCUSS_FAILED);
					}
				}
			}).start();
		}
	}

	/**
	 * 给指定任务发砖石
	 */
	private void giveDiamond(final 日志 log) {
		if (mLog.MyDiamondCount > 0) {
			// Toast.makeText(context, "您已发钻石", Toast.LENGTH_SHORT).show();
			// 取消发钻
			mLog.MyDiamondCount--;
			mLog.DiamondCount--;
			tv_zs_count.setText("" + mLog.DiamondCount);
			initDiamondStyle(mLog.MyDiamondCount);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSucceed = zlServiceHelper.removeDiamond(
								formUserId, 2, log.Id);
						if (isSucceed) {
							setRetrunResult();
						} else {

						}
					} catch (Exception e) {
						// handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		} else {
			ProgressDialogHelper.show(context, "正在发放钻石...");
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSuccessed = zlServiceHelper.giveDiamond(1,
								formUserId, log.Personnel, 2, log.Id);
						if (isSuccessed) {
							handler.sendEmptyMessage(GIVE_DIAMOND_SUCCESS);
						} else {
							handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		}
	}

	/** 点赞 */
	private void publishSupport() {
		if (mLog.MySupportCount > 0) {
			// Toast.makeText(context, "您已赞过", Toast.LENGTH_SHORT).show();
			mLog.MySupportCount--;
			mLog.SupportCount--;
			tv_support_count.setText("" + mLog.SupportCount);
			initSupportStyle(mLog.MySupportCount);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int formUserId = Integer.parseInt(Global.mUser.Id);
						boolean isSucceed = zlServiceHelper.RemoveSupport(
								formUserId, 2, mLog.Id);
						if (isSucceed) {
							setRetrunResult();
						} else {

						}
					} catch (Exception e) {
						// handler.sendEmptyMessage(GIVE_DIAMOND_FAILED);
					}
				}
			}).start();
		} else {
			ProgressDialogHelper.show(context, "点了一次赞...");
			final String discussContent = "[已赞]";
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						boolean isResult = zlServiceHelper.giveSupport(
								Global.mUser.Id, mLog.getPersonnel() + "", 2,
								mLog.Id);
						if (isResult) {
							handler.sendEmptyMessage(HandlerNewContact.SUPPORT_SUCCESS);
						} else {
							handler.sendEmptyMessage(HandlerNewContact.SUPPORT_FAILED);
						}
					} catch (Exception e) {
						handler.sendEmptyMessage(HandlerNewContact.SUPPORT_FAILED);
					}
				}
			}).start();
		}
	}

	public static OnFocusChangeListener onFocusAutoClearListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
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
			LogUtils.i(TAG, "" + e);
		}
	}

	/***
	 * 根据样我的发钻石数量显示 字体图标对应的颜色
	 * 
	 * @param count
	 */
	private void initDiamondStyle(int count) {
		if (count == 0) {
			ivZs.setImageResource(R.drawable.ico_zs_big);
			tv_zs_count
					.setTextColor(getResources().getColor(R.color.text_info));
		} else {
			ivZs.setImageResource(R.drawable.ico_zs_big2);
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
			ivSupport.setImageResource(R.drawable.ico_support_normal);
			tv_support_count.setTextColor(getResources().getColor(
					R.color.text_info));
		} else {
			ivSupport.setImageResource(R.drawable.ico_support2);
			tv_support_count.setTextColor(getResources().getColor(
					R.color.theme_new));
		}
	}

	private void saveTomorrowPlan() {
		final String taskContent = etTomorrowPlan.getText().toString();
		if (TextUtils.isEmpty(taskContent)) {
			// Toast.makeText(context, "明日计划内容还没填写哦！",
			// Toast.LENGTH_SHORT).show();
		} else {
			ProgressDialogHelper.show(context, "正在生成明日计划...");
			try {
				final 任务 item = new 任务();
				item.Title = taskContent;
				item.Content = taskContent;
				item.Executor = Integer.parseInt(Global.mUser.Id);
				item.Publisher = Integer.parseInt(Global.mUser.Id);
				item.Status = 1;
				// 初始化时间为第二天 09:00:01
				Date today = ViewHelper.getTomorrow(new Date());
				String taskAssignTime = ViewHelper.getDateString(today)
						+ " 09:00:01";
				item.AssignTime = taskAssignTime;
				new Thread(new Runnable() {

					@Override
					public void run() {
						boolean isSuccess = zlServiceHelper.EditTask(item);
						if (isSuccess) {
							handler.sendEmptyMessage(HandlerNewContact.SAVE_PLAN_SUCCESS);
						} else {
							handler.sendEmptyMessage(HandlerNewContact.SAVE_PLAN_FAILED);
						}
					}
				}).start();

			} catch (Exception e) {
				handler.sendEmptyMessage(HandlerNewContact.SAVE_PLAN_FAILED);
			}
		}
	}

	private void publishWorkLog(String log, final String planContent) {
		ProgressDialogHelper.show(context);
		final String logSave = log;
		new Thread(new Runnable() {
			public void run() {
				try {
					String time = "";
					if (mLog != null) {
						time = mLog.getTime();
					}
					if (TextUtils.isEmpty(time)) {
						time = ViewHelper.getDateString();
					}
					boolean result = zlServiceHelper.UpdateLog(logSave,
							planContent);
					if (result) {
						handler.sendEmptyMessage(HandlerNewContact.SAVE_LOG_SUCCESS);
					} else {
						handler.sendEmptyMessage(HandlerNewContact.SAVE_LOG_FAILED);
					}
				} catch (Exception e) {
					LogUtils.i(TAG, "" + e);
					handler.sendEmptyMessage(HandlerNewContact.SAVE_LOG_FAILED);
				}
			}
		}).start();
	}

	private void back() {
		if (isCommentSuccessed) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable(WorkLogFragment.TAG, mLog);
			intent.putExtras(bundle);
			setResult(WorkLogFragment.RESULT_COMMENT_SUCCESS, intent);
		}
		finish();
	}

	/***
	 * 设置返回结果
	 */
	private void setRetrunResult() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		LogUtils.i("count", mLog.MyDiamondCount + "--" + mLog.DiamondCount
				+ "====" + mLog.MySupportCount + "--" + mLog.SupportCount);
		bundle.putSerializable(WorkLogFragment.TAG, mLog);
		intent.putExtras(bundle);
		setResult(WorkLogFragment.RESULT_UPDATE_SUCCESS, intent);
	}

	public class HandlerNewContact extends Handler {
		public static final int SAVE_LOG_SUCCESS = 0;
		public static final int SAVE_LOG_FAILED = 1;

		public static final int PUBLISH_DISCUSS_SUCCESS = 2;
		public static final int PUBLISH_DISCUSS_FAILED = 3;
		private final int GET_DISCUSS_SUCCESS = 5; // 获得评论列表成功
		private final int GET_DISCUSS_FAILED = 6; // 获得评论列表失败
		public final int GET_DYNAMIC_SUCCESS = 7; // 点击动态，获取日志成功
		public final int GET_DYNAMIC_FAILED = 8; // 点击动态，获取日志失败

		/***
		 * 保存明日计划成功
		 */
		public static final int SAVE_PLAN_SUCCESS = 9;

		/*** 保存明日计划失败 */
		public static final int SAVE_PLAN_FAILED = 10;

		/** 点赞成功 */
		public static final int SUPPORT_SUCCESS = 25;
		/** 点赞失败 */
		public static final int SUPPORT_FAILED = 26;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LogUtils.i("GET_DISCUSS_SUCCESS", " handleMessage--what--"
					+ msg.what);
			switch (msg.what) {
			case SAVE_LOG_SUCCESS:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "日志保存成功！", Toast.LENGTH_SHORT).show();
				setRetrunResult();
				finish();
				break;
			case SAVE_LOG_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "日志保存失败！", Toast.LENGTH_SHORT).show();
				break;
			case PUBLISH_DISCUSS_SUCCESS:
				ProgressDialogHelper.dismiss();
				// 评论成功
				MessageUtil.ToastMessage(context, "评论成功！");
				getLogCommentList();
				etDiscuss.setText("");
				isCommentSuccessed = true;
				mLog.DiscussCount += 1;
				InputSoftHelper.hiddenSoftInput(context, etDiscuss);
				setRetrunResult();
				break;
			case PUBLISH_DISCUSS_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "评论失败！");
				break;
			case GET_DISCUSS_SUCCESS:
				// 获得评论列表成功
				listDiscuss = (List<日志评论>) msg.obj;
				LogUtils.i("GET_DISCUSS_SUCCESS",
						" 获得评论列表成功:" + listDiscuss.size());
				new BoeryunCommentItemView(context, listDiscuss,
						ll_discuss_root).createCommentView();
				break;
			case GET_DISCUSS_FAILED:
				// 获得评论列表失败
				// rlDiscussContent.setVisibility(View.GONE);
				break;
			case GET_DYNAMIC_SUCCESS: // 点击动态，获取日志成功
				ProgressDialogHelper.dismiss();
				动态 dynamic = (动态) msg.obj;
				mLog = dynamic.Log;
				if (mLog != null) {
					initLog();
				}
				break;
			case GET_DYNAMIC_FAILED: // 获取日志失败
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "加载日志失败，请稍后重试", Toast.LENGTH_LONG)
						.show();
				break;
			case GIVE_DIAMOND_SUCCESS:
				ProgressDialogHelper.dismiss();
				MediaUtils.startMusic(context, R.raw.collect_diamonds_02);
				addCount(tv_zs_count);
				mLog.DiamondCount += 1;
				mLog.MyDiamondCount += 1;
				initDiamondStyle(mLog.MyDiamondCount);
				setRetrunResult();
				break;
			case GIVE_DIAMOND_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "发钻失败！");
				break;
			case SUPPORT_SUCCESS: // 点赞成功
				ProgressDialogHelper.dismiss();
				addCount(tv_support_count);
				// getLogCommentList();
				mLog.SupportCount += 1;
				mLog.MySupportCount += 1;
				initSupportStyle(mLog.MySupportCount);
				setRetrunResult();
				break;
			case SUPPORT_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "点赞失败！");
				break;
			case SAVE_PLAN_SUCCESS:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "明日计划保存成功！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case SAVE_PLAN_FAILED:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "明日计划保存失败！", Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		}
	}

}