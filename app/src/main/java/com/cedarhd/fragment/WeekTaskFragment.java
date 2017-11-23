package com.cedarhd.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.TaskInfoActivity;
import com.cedarhd.User_SelectActivityNew_zmy;
import com.cedarhd.animation.ExpandAnimation;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictPickedDialog;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.helpers.server.ServerDataLoader;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.ViewBean;
import com.cedarhd.models.任务;
import com.cedarhd.models.字典;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** 任务周历 */
public class WeekTaskFragment extends Fragment {

	private final String TAG = WeekTaskFragment.this.getClass().getSimpleName();
	/*** 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色 */
	private int[] stateColors = new int[] { 0xFFFFFF00, 0xFFD3D3D3, 0xFF008000,
			0xFF808080, 0xFF0000FF, 0xFFFF0000 };

	/** 状态色块 */
	private int[] stateBgs = new int[] { R.drawable.ico_state_qidong,
			R.drawable.ico_state_zanting, R.drawable.ico_state_wancheng,
			R.drawable.ico_state_gezhi, R.drawable.ico_state_tijiao,
			R.drawable.ico_state_chongqi };
	public static final int UPDATE_TASK_SUCCESS = 2;
	public static final int UPDATE_TASK_FAILED = 3;

	/** 评论成功 */
	public static final int COMMENT_TASK_SUCCESS = 4;

	/** 评论失败 */
	public static final int COMMENT_TASK_FAILED = 5;

	/** 点赞成功 */
	public static final int SUPPORT_SUCCESS = 6;

	/** 点赞失败 */
	public static final int SUPPORT_FAILED = 7;

	/** 获得任务分类列表成功 */
	private static final int GET_PROJECT_SUCCESS = 8; //

	/** 获得任务分类列表失败 */
	private static final int GET_PROJECT_FAILED = 9;

	/** 发钻石成功 */
	public static final int GIVE_DIAMOND_SUCCESS = 10;

	/** 点赞失败 */
	public static final int GIVE_DIAMOND_FAILED = 11;

	/*** 选择执行人 */
	public static final int CODE_SELECT_EXECUTOR = 102;
	/** 选择参与人 */
	public static final int CODE_SELECT_PARTICIPANT = 103;

	/***
	 * 底部输入框，是否评论评论状态，如果不是，则用来新建任务
	 */
	private boolean isComment = false;

	private Context context;
	private InputMethodManager imm;
	private LayoutInflater inflater;
	private ZLServiceHelper zlServiceHelper;
	private DictionaryHelper dictionaryHelper;
	private 任务 mTask;// 任务
	private Date now;
	private Demand demand;
	private List<字典> mProjectList;
	/** 延期任务 */
	private Button btnPost;

	/** 动画状态，同时只允许播放一个动画 */
	private boolean animState = true;

	/** 动画展开时赋值 */
	private View lastView; //

	/** 展开动画 视图集合 */
	private ArrayList<ViewBean> viewList;

	/** fragment根节点 */
	private View rootView;

	/** 周一任务 */
	private LinearLayout ll_monday_root;
	private LinearLayout ll_thuesday_root;
	private LinearLayout ll_wednesday_root;
	/** 周四任务 */
	private LinearLayout ll_thursday_root;
	private LinearLayout ll_friday_root;
	private LinearLayout ll_saturday_root;
	private LinearLayout ll_sunday_root;

	// /** 周一日期 */
	// private TextView tv_monday_date;
	// private TextView tv_thuesday_date;
	// private TextView tv_wednesday_date;
	// /** 周四日期 */
	// private TextView tv_thursday_date;
	// private TextView tv_friday_date;
	// private TextView tv_saturday_date;
	// private TextView tv_sunday_date;

	/** 语音按钮 */
	private ImageView ivTalking;
	/** 保存按钮 */
	private ImageView ivSave;
	/** 快捷输入文本框 */
	private EditText etContent;

	/** 保存任务 */
	private RelativeLayout rl_save_tasktablist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		imm = (InputMethodManager) getActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inflater = LayoutInflater.from(context);
		viewList = new ArrayList<ViewBean>();
		zlServiceHelper = new ZLServiceHelper();
		dictionaryHelper = new DictionaryHelper(context);
		now = new Date();
		demand = new Demand();
		demand.方法名 = "Task/GetOtherList/";
		demand.条件 = "";
		demand.每页数量 = 100;
		demand.偏移量 = 0;

		downLoadProjectList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_weektasklist, null,
				false);
		initUI(view);
		setOnClickEvent();
		initDate();
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("onActivityResult", requestCode + "---" + resultCode);
		if (data == null) {
			return;
		}

		Bundle bundle = data.getExtras();
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CODE_SELECT_EXECUTOR:
				String excutorStr = bundle.getString("UserSelectId");
				if (mTask != null && !TextUtils.isEmpty(excutorStr)) {
					try {
						excutorStr = excutorStr.replace("'", "").replace(";",
								"");
						int executor = Integer.parseInt(excutorStr);
						mTask.Executor = executor;
						saveTask(mTask);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtils.i(TAG, e + "");
					}
				}
				break;
			case CODE_SELECT_PARTICIPANT:
				String userIds = bundle.getString("UserSelectId");
				// mUserSelectName = bundle.getString("UserSelectName");
				// 选择接收人
				if (!TextUtils.isEmpty(userIds)) {
					mTask.Participant = userIds;
					saveTask(mTask);
				}
				break;
			default:
				break;
			}
		}
	}

	private void initUI(View view) {
		rootView = view.findViewById(R.id.rl_root_weektask);
		btnPost = (Button) view.findViewById(R.id.btn_postpone_weektask);
		ll_monday_root = (LinearLayout) view
				.findViewById(R.id.ll_monday_root_weektask);
		ll_thuesday_root = (LinearLayout) view
				.findViewById(R.id.ll_tuesday_root_weektask);
		ll_wednesday_root = (LinearLayout) view
				.findViewById(R.id.ll_wednesday_root_weektask);
		ll_thursday_root = (LinearLayout) view
				.findViewById(R.id.ll_thursday_root_weektask);
		ll_friday_root = (LinearLayout) view
				.findViewById(R.id.ll_friday_root_weektask);
		ll_saturday_root = (LinearLayout) view
				.findViewById(R.id.ll_saturday_root_weektask);
		ll_sunday_root = (LinearLayout) view
				.findViewById(R.id.ll_sunday_root_weektask);

		/** 初始化日期控件 */
		// tv_monday_date = (TextView) view
		// .findViewById(R.id.tv_monday_date_weektask);
		// tv_thuesday_date = (TextView) view
		// .findViewById(R.id.tv_tuesday_date_weektask);
		// tv_wednesday_date = (TextView) view
		// .findViewById(R.id.tv_wednesday_date_weektask);
		// tv_thursday_date = (TextView) view
		// .findViewById(R.id.tv_thursday_date_weektask);
		// tv_friday_date = (TextView) view
		// .findViewById(R.id.tv_friday_date_weektask);
		// tv_saturday_date = (TextView) view
		// .findViewById(R.id.tv_saturday_date_weektask);
		// tv_sunday_date = (TextView) view
		// .findViewById(R.id.tv_sunday_date_weektask);

		/** 保存控件 */
		ivTalking = (ImageView) view.findViewById(R.id.iv_talking_weektasklist);
		ivSave = (ImageView) view.findViewById(R.id.iv_save_weektasklist);
		etContent = (EditText) view.findViewById(R.id.et_content_weektasklist);

		rl_save_tasktablist = (RelativeLayout) view
				.findViewById(R.id.rl_save_tasktablist);
	}

	/** 设置监听事件 */
	private void setOnClickEvent() {
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = rootView.getRootView().getHeight()
								- rootView.getHeight();
						LogUtils.i("heightDiff", "heightDiff--" + heightDiff);
						if (heightDiff > 200) {
							// changeEditInput();
						}
					}
				});

		ivTalking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SpeechDialogHelper(context, (Activity) context, etContent,
						true);
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String content = etContent.getText().toString();
				if (TextUtils.isEmpty(content)) {
					Toast.makeText(context, "内容不能为空哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					if (isComment) {
						// 评论状态
						publishComment(mTask, content);
					} else {// 发表任务
						ProgressDialogHelper.show(context, "任务保存中...");
						try {
							任务 item = new 任务();
							item.Title = content;
							item.Content = content;
							item.Executor = Integer.parseInt(Global.mUser.Id);
							item.Publisher = Integer.parseInt(Global.mUser.Id);
							item.Status = 1;
							item.AssignTime = ViewHelper.getDateString();
							saveTask(item);
						} catch (Exception e) {
							handler.sendEmptyMessage(UPDATE_TASK_FAILED);
						}
					}
				}
			}
		});

		btnPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 延期任务
				if (onFragmentButtonClick != null) {
					onFragmentButtonClick.onClick();
				}
			}
		});
	}

	/**
	 * 初始化周历日期
	 */
	private void initDate() {
		List<String> weekList = new ArrayList<String>();
		for (int k = 1; k <= 7; k++) {
			String date = getWeekDate(k);
			LogUtils.i(TAG, date);
			weekList.add(date);
		}

		for (int i = 0; i < weekList.size(); i++) {
			LogUtils.i("QueryWeekTasks", weekList.get(i));
		}
		LogUtils.i("QueryWeekTask", "-----" + ViewHelper.getDateString(now));
		String filter = " 执行时间 >'" + weekList.get(0) + "' and 执行时间<'"
				+ weekList.get(weekList.size() - 1) + "'";
		LogUtils.i("QueryWeekTask", "-----" + weekList.size());
		LogUtils.i("QueryWeekTask", filter);
		ProgressDialogHelper.show(context);
		new QueryWeekTask().execute(filter);
	}

	/**
	 * 将任务以周历的形式， 显示对应的日期
	 * 
	 */
	private void showTaskList(List<任务> result) {
		String date = "";
		for (int k = 1; k <= 7; k++) {
			date = getWeekDate(k);
			LogUtils.i(TAG, date);
			List<任务> list = getTaskListByDate(result, date);
			showWeekTaskList(date, k, list);
		}
		// TODO 设置展开动画效果
		// setAnimation(viewList);
	}

	/****
	 * 显示任务列表
	 * 
	 * @param date
	 *            日期
	 * @param dayOfWeek
	 *            日期在一周中第几天
	 * @param list
	 *            指定日期的任务列表
	 */
	private void showWeekTaskList(String date, int dayOfWeek, List<任务> list) {
		// 遍历一周所有任务
		switch (dayOfWeek) {
		case 1:
			// tv_monday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_monday_root);
			break;
		case 2:
			// tv_thuesday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_thuesday_root);
			break;
		case 3:
			// tv_wednesday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_wednesday_root);
			break;
		case 4:
			// tv_thursday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_thursday_root);
			break;
		case 5:
			// tv_friday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_friday_root);
			break;
		case 6:
			// tv_saturday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_saturday_root);
			break;
		case 7:
			// tv_sunday_date.setText(date + "");
			createTaskInfo(dayOfWeek, date, list, ll_sunday_root);
			break;
		default:
			break;
		}
	}

	/***
	 * 获取指定日期的任务列表
	 * 
	 * @param result
	 *            任务列表
	 * @param date
	 *            日期
	 * @return
	 */
	private List<任务> getTaskListByDate(List<任务> result, String date) {
		List<任务> list = new ArrayList<任务>();
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i).AssignTime.trim().contains(date.trim())) {
				list.add(result.get(i));
			}
		}
		return list;
	}

	/***
	 * 生成任务详情列表
	 * 
	 * @param result
	 *            任务列表
	 * @param llRoot
	 *            显示的任务区域
	 * @return
	 */
	@SuppressLint("NewApi")
	private void createTaskInfo(int dayOfWeek, String dateStr,
			List<任务> taskList, LinearLayout llRoot) {
		if (taskList == null || taskList.size() == 0) {
			return;
		}
		final String formatDate = DateDeserializer.getFormatDate(dateStr);
		for (int i = 0; i < taskList.size(); i++) {
			final 任务 task = taskList.get(i);
			View view = inflater.inflate(R.layout.item_weektask, null, false);
			/** 任务信息 （简略）,点击展开明细 */
			LinearLayout llTitle = (LinearLayout) view
					.findViewById(R.id.ll_taskinfo_title_weektask);
			ImageView ivWeekDay = (ImageView) view
					.findViewById(R.id.iv_weekday_weektask);
			TextView tvWeekDay = (TextView) view
					.findViewById(R.id.tv_weekday_weektask);
			TextView tvContent = (TextView) view
					.findViewById(R.id.tv_content_weektasklist);
			TextView tvState = (TextView) view
					.findViewById(R.id.tv_state_weektasklist);
			TextView tv_publisher = (TextView) view
					.findViewById(R.id.tv_publisher_weektasklist);
			TextView tv_executor = (TextView) view
					.findViewById(R.id.tv_executor_weektasklist);

			/** 发表评论 */
			final ImageView ivPublishComment = (ImageView) view
					.findViewById(R.id.iv_publish_comment_weektask);
			TextView tvCommentCount = (TextView) view
					.findViewById(R.id.tv_comment_count_weektask);
			TextView tvZanCount = (TextView) view
					.findViewById(R.id.tv_zan_count_weektask);
			TextView tvZsCount = (TextView) view
					.findViewById(R.id.tv_zs_count_weektask);
			/** 点赞 */
			ImageView ivSupport = (ImageView) view
					.findViewById(R.id.iv_support_weektask);
			/** 发钻石 */
			ImageView ivZs = (ImageView) view.findViewById(R.id.iv_zs_weektask);
			/** 选择执行人 */
			ImageView ivExcutor = (ImageView) view
					.findViewById(R.id.iv_executor_weektask);
			/** 对勾，点击快速修改状态为完成 */
			ImageView ivComplete = (ImageView) view
					.findViewById(R.id.iv_complete_weektask);

			/** 任务信息（明细） */
			LinearLayout llLayout = (LinearLayout) view
					.findViewById(R.id.ll_taskinfo_layout_weektask);
			/** 展开显示任务详情 */
			TextView tvContentLayout = (TextView) view
					.findViewById(R.id.tv_content_layout_weektasklist);
			final TextView tvProjectLayout = (TextView) view
					.findViewById(R.id.tv_project_layout_weektasklist);
			// TextView tvState_layout = (TextView) view
			// .findViewById(R.id.tv_state_layout_weektasklist);
			ImageView ivWeekDay_layout = (ImageView) view
					.findViewById(R.id.iv_weekday_layout_weektask);
			TextView tvWeekDay_layout = (TextView) view
					.findViewById(R.id.tv_weekday_layout_weektask);
			/** 选择项目 */
			ImageView ivProject = (ImageView) view
					.findViewById(R.id.iv_project_weektask);
			/** 选择提醒时间 */
			ImageView ivAlarm = (ImageView) view
					.findViewById(R.id.iv_alarm_weektask);
			/** 转发分享 */
			ImageView ivShare = (ImageView) view
					.findViewById(R.id.iv_share_weektask);
			/** 选择参与人 */
			ImageView ivParticipant = (ImageView) view
					.findViewById(R.id.iv_paticipant_weektask);
			/** 编辑，进入详情页面 */
			ImageView ivEdit = (ImageView) view
					.findViewById(R.id.iv_edit_weektask);

			tv_executor.setText("执行人："
					+ dictionaryHelper.getUserNameById(task.Executor) + "");
			tv_publisher.setText("发布人："
					+ dictionaryHelper.getUserNameById(task.Publisher) + "");
			tvCommentCount.setText(task.CommentCount + "");
			// tvZanCount.setText(task.SupportCount + "");
			// 点赞手势改为钻石数量
			tvZanCount.setText(task.DiamondCount + "");
			tvContent.setText(task.Content + "");
			tvZsCount.setText(task.DiamondCount + "");
			tvWeekDay.setText(formatDate + "");
			// 黄色启动，蓝色提交，绿色完成，搁置灰色，重启红色
			if (task.Status >= 1 && task.Status <= 6) {
				// tvState.setBackgroundColor(stateColors[task.Status - 1]);
				// tvState_layout.setBackgroundColor(stateColors[task.Status -
				// 1]);
				tvState.setBackground(getResources().getDrawable(
						stateBgs[task.Status - 1]));

				if (task.Status == 3) {
					ivComplete.setVisibility(View.GONE);
				}
			} else {
				tvState.setBackgroundColor(0x0000000); // 状态异常透明
				// tvState_layout.setBackgroundColor(0x0000000); // 状态异常透明
			}
			tvContentLayout.setText(task.Content + "");
			tvWeekDay_layout.setText(formatDate + "");
			if (TextUtils.isEmpty(task.CategroyName)) {
				tvProjectLayout.setVisibility(View.GONE);
			} else {
				tvProjectLayout.setText(task.CategroyName + "");
			}

			showWeekDay(dayOfWeek, ivWeekDay, ivWeekDay_layout);

			llTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startTaskInfo(task);
				}
			});

			// 评论图标
			ivPublishComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;

					// TODO 在任务底部
					// showPopComment(ivPublishComment, task);
					isComment = true;
					changeEditInput();
					etContent.requestFocus();
					etContent.setFocusable(true);
					showInput();
					//
					// isComment = true;

				}
			});
			ivSupport.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					// TODO
					Toast.makeText(context, "点赞", Toast.LENGTH_SHORT).show();
					// publishSupport(task);
					// 点赞改为发钻石
					GiveDiamond(task);
				}
			});
			ivZs.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					int userId = 0;
					try {
						userId = Integer.parseInt(Global.mUser.Id);
					} catch (Exception e) {
						LogUtils.i(TAG, e + "");
					}

					if (task.Executor == userId) {
						Toast.makeText(context, "不能给自己发钻石哦！",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "发钻+1", Toast.LENGTH_SHORT)
								.show();
						GiveDiamond(task);
					}
				}
			});
			ivExcutor.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					int userId = 0;
					try {
						userId = Integer.parseInt(Global.mUser.Id);
					} catch (Exception e) {
						LogUtils.i(TAG, e + "");
					}

					if (task.Executor == userId || task.Publisher == userId) {
						Builder builder = new Builder(context);
						builder.setTitle("是否转发任务");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										selectExecutor();
									}
								}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						AlertDialog dialog = builder.create();
						dialog.show();

					} else {
						Toast.makeText(context, "无权限转发任务", Toast.LENGTH_SHORT)
								.show();
					}

				}
			});

			ivComplete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (task.Status != 3) {// 如果状态不是完成
						task.Status = 3;
						Builder builder = new Builder(context);
						builder.setTitle("是否设置任务为完成");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										saveTask(task);
									}
								}).setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						AlertDialog dialog = builder.create();
						dialog.show();

					} else {
						Toast.makeText(context, "已完成", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});

			ivProject.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					// TODO Auto-generated method stub
					// Toast.makeText(context, "选择项目",
					// Toast.LENGTH_SHORT).show();
					new DictPickedDialog(context, mProjectList, tvProjectLayout)
							.showDicDialog();

					saveTask(task);
				}
			});

			ivAlarm.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO
					Toast.makeText(context, "设置提醒", Toast.LENGTH_SHORT).show();
				}
			});

			ivShare.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					// selectExecutor();
					shareTask(formatDate, mTask.Content);
				}
			});
			ivParticipant.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTask = task;
					selectParticipant();
				}
			});

			ivEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startTaskInfo(task);
				}
			});

			ViewBean viewBean = new ViewBean();
			viewBean.setViewTitle(llTitle);
			viewBean.setViewLayout(llLayout);
			viewList.add(viewBean);
			llRoot.addView(view);
		}
	}

	/**
	 * 根据一周所在第几天显示对应图片
	 * 
	 * @param dayOfWeek
	 * @param ivWeekDay
	 * @param ivWeekDay_layout
	 */
	private void showWeekDay(int dayOfWeek, ImageView ivWeekDay,
			ImageView ivWeekDay_layout) {
		// 遍历一周所有任务
		switch (dayOfWeek) {
		case 1:
			ivWeekDay.setImageResource(R.drawable.ico_week1);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week1);
			break;
		case 2:
			ivWeekDay.setImageResource(R.drawable.ico_week2);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week2);
			break;
		case 3:
			ivWeekDay.setImageResource(R.drawable.ico_week3);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week3);
			break;
		case 4:
			ivWeekDay.setImageResource(R.drawable.ico_week4);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week4);
			break;
		case 5:
			ivWeekDay.setImageResource(R.drawable.ico_week5);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week5);
			break;
		case 6:
			ivWeekDay.setImageResource(R.drawable.ico_week6);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week6);
			break;
		case 7:
			ivWeekDay.setImageResource(R.drawable.ico_week7);
			ivWeekDay_layout.setImageResource(R.drawable.ico_week7);
			break;
		default:
			break;
		}
	}

	/** 保存任务 */
	private void saveTask(final 任务 item) {
		ProgressDialogHelper.show(context, "保存中,请稍候...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					boolean isSuccessed = zlServiceHelper.EditTask(item);
					if (isSuccessed) {
						handler.sendEmptyMessage(UPDATE_TASK_SUCCESS);
					} else {
						handler.sendEmptyMessage(UPDATE_TASK_FAILED);
					}
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(UPDATE_TASK_FAILED);
				}
			}
		}).start();
	}

	/**
	 * 添加执行人
	 */
	private void selectExecutor() {
		Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean(User_SelectActivityNew_zmy.SELECT_EMPLOYEE, true); // 单选
		intent.putExtras(bundle);
		startActivityForResult(intent, CODE_SELECT_EXECUTOR);
	}

	/**
	 * 选择参与人
	 */
	private void selectParticipant() {
		Intent intent = new Intent(context, User_SelectActivityNew_zmy.class);
		Bundle bundle = new Bundle();
		// bundle.putString("UserSelectId", mUserSelectId);
		intent.putExtras(bundle);
		startActivityForResult(intent, CODE_SELECT_PARTICIPANT);
	}

	/**
	 * 根据星期获得 日期
	 * 
	 * @param dayOfWeek
	 *            星期
	 * @return
	 */
	private String getWeekDate(int dayOfWeek) {
		LogUtils.i("week",
				now.getYear() + "-" + now.getMonth() + "-" + now.getDate());
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		int week = calendar.DAY_OF_MONTH;// 获得当前星期几
		int beforDay = week - dayOfWeek; // 今天和输入星期的差值
		LogUtils.i("before", week + "-" + dayOfWeek + "=" + beforDay);
		int length = Math.abs(beforDay);
		for (int i = 0; i < length; i++) {
			if (beforDay < 0) { // 今天之后的
				date = ViewHelper.getTomorrow(date);
			} else if (beforDay > 0) { // 今天之前的
				date = ViewHelper.getYestody(date);
			}
		}
		return ViewHelper.getDateString(date);
	}

	/**
	 * 设置动画效果
	 * 
	 * @param viewList
	 */
	private void setAnimation(List<ViewBean> viewList) {
		// 迭代list，对每一个viewTitle对应的viewLayout设置动画
		for (ViewBean bean : viewList) {
			final View viewTitle = bean.getViewTitle();
			final View viewLayout = bean.getViewLayout();
			viewTitle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					executeAnimation(viewTitle, viewLayout);
				}
			});
		}
	}

	private void executeAnimation(View viewTitle, View viewLayout) {
		if (animState) {
			ExpandAnimation animation = new ExpandAnimation(viewLayout, 300);
			// 获得当前动画开关的状态
			boolean toggle = animation.toggle();
			if (toggle) {
				if (lastView == null) {
					// 说明之前没有打开过动画 或者 已经打开的动画都关闭了
					lastView = viewLayout;
				} else {
					// 说明之前有一个打开的动画
					if (lastView == viewLayout) {
						lastView = null;// 说明点击的是上一个打开的layout的title
					} else {// 点击的是其他的title
						executeAnimation(viewTitle, lastView); // 关闭上一个打开的title
						lastView = viewLayout;// 记住当前的viewLayout为lastView
					}
				}
			} else {
				for (ViewBean bean : viewList) {
					if (viewLayout == bean.getViewLayout()) {
						if (lastView == bean.getViewLayout()) {
							lastView = null;
						}
					}
				}
			}
			viewLayout.startAnimation(animation);
			animation.setAnimationListener(animListener);
		}
	}

	/**
	 * 动画监听
	 */
	private Animation.AnimationListener animListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			animState = false;// 动画未结束前不允许下次播放
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			animState = true;// 当前动画播放结束后再允许播放下次动画
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	};

	private class QueryWeekTask extends AsyncTask<String, Void, List<任务>> {
		@Override
		protected List<任务> doInBackground(String... params) {
			demand.附加条件 = params[0];
			List<任务> result = ServerDataLoader.getServerTaskData(demand, "");

			return result;
		}

		@Override
		protected void onPostExecute(List<任务> result) {
			super.onPostExecute(result);
			ProgressDialogHelper.dismiss();
			if (result != null) {
				LogUtils.i("QueryWeekTask", "----" + result.size());
				removeAllViews();
				showTaskList(result);
			}
		}

	}

	/** 移除页面所有控件 */
	private void removeAllViews() {
		ll_monday_root.removeAllViews();
		ll_wednesday_root.removeAllViews();
		ll_thuesday_root.removeAllViews();
		ll_thursday_root.removeAllViews();
		ll_friday_root.removeAllViews();
		ll_saturday_root.removeAllViews();
		ll_sunday_root.removeAllViews();
	}

	/** 弹出pop评论对话框 */
	private void showPopComment(final ImageView ivPublishComment, final 任务 task) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 加载指定布局作为PopupWindow的显示内容
		View contentView = inflater.inflate(R.layout.pop_input_window, null);
		// 初始化popupWindow,指定显示内容和宽高
		PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, (int) ViewHelper.dip2px(context, 50));
		setOnCommentClick(task, contentView, popupWindow);
		// 设置popupWindow获得焦点
		// 否则上面控件EditText无法获得焦点
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		// 设置popupWindow以外的区域可点击，点击后空白处，对话框消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.showAsDropDown(ivPublishComment);
		// // popupWindow.showAsDropDown(rl_save_tasktablist);
		// popupWindow.showAtLocation(rl_save_tasktablist, Gravity.BOTTOM, 0,
		// 0);
	}

	/** 发表评论按钮事件监听 */
	private void setOnCommentClick(final 任务 task, View contentView,
			final PopupWindow popupWindow) {
		final EditText etComment = (EditText) contentView
				.findViewById(R.id.et_content_pop);

		ImageView ivSpeek = (ImageView) contentView
				.findViewById(R.id.iv_talking_pop);
		ImageView ivSave = (ImageView) contentView
				.findViewById(R.id.iv_save_pop);
		ivSpeek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SpeechDialogHelper(context, getActivity(), etComment, true);
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String comment = etComment.getText().toString();
				if (TextUtils.isEmpty(comment)) {
					Toast.makeText(context, "评论内容不允许为空哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					popupWindow.dismiss();
					publishComment(task, comment);
				}
			}
		});
	}

	/** 发布评论 */
	private void publishComment(final 任务 task, final String comment) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isSuccessed = zlServiceHelper.publishTaskDiscuss(
						task.Id + "", comment);
				if (isSuccessed) {
					handler.sendEmptyMessage(COMMENT_TASK_SUCCESS);
				} else {
					handler.sendEmptyMessage(COMMENT_TASK_FAILED);
				}
			}
		}).start();
	}

	/** 点赞 */
	private void publishSupport(final 任务 task) {
		final String comment = "[已赞]";
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean isSuccessed = zlServiceHelper.publishTaskDiscuss(
						task.Id + "", comment);
				if (isSuccessed) {
					handler.sendEmptyMessage(SUPPORT_SUCCESS);
				} else {
					handler.sendEmptyMessage(SUPPORT_FAILED);
				}
			}
		}).start();
	}

	/**
	 * 给指定任务发砖石
	 */
	private void GiveDiamond(final 任务 task) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int formUserId = Integer.parseInt(Global.mUser.Id);
					boolean isSuccessed = zlServiceHelper.giveDiamond(1,
							formUserId, task.Executor, 3, task.Id);
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

	/***
	 * 转发分享
	 * 
	 * @param strInfo
	 *            分享内容
	 */
	private void shareTask(String date, String content) {
		String strInfo = "任务内容：" + content + "\n 执行时间：" + date;
		Intent share_intent = new Intent();
		share_intent.setAction(Intent.ACTION_SEND);
		share_intent.setType("text/plain");
		share_intent.putExtra(Intent.EXTRA_SUBJECT, "f分享");
		share_intent.putExtra(Intent.EXTRA_TEXT, strInfo);
		share_intent = Intent.createChooser(share_intent, "分享");
		startActivity(share_intent);
	}

	/***
	 * 下载项目列表字典
	 */
	private void downLoadProjectList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<字典> statusList = zlServiceHelper.getDictList("任务分类");
				if (statusList != null && statusList.size() > 0) {
					Message msg = handler.obtainMessage();
					msg.what = GET_PROJECT_SUCCESS;
					msg.obj = statusList;
					handler.sendMessage(msg);
				} else {
					handler.sendEmptyMessage(GET_PROJECT_FAILED);
				}
			}
		}).start();
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_TASK_SUCCESS:
				ProgressDialogHelper.dismiss();
				etContent.setText("");
				MessageUtil.ToastMessage(context, "保存成功！");
				initDate();
				break;
			case UPDATE_TASK_FAILED:
				ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "保存失败！");
				break;
			case COMMENT_TASK_SUCCESS:
				ProgressDialogHelper.dismiss();
				etContent.setText("");
				MessageUtil.ToastMessage(context, "评论成功！");
				initDate();
				isComment = false;
				changeEditInput();
				break;
			case COMMENT_TASK_FAILED:
				// ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "评论失败！");
			case SUPPORT_SUCCESS:
				MessageUtil.ToastMessage(context, "已赞！");
				initDate();
				break;
			case SUPPORT_FAILED:
				// ProgressDialogHelper.dismiss();
				// MessageUtil.ToastMessage(context, "评论失败！");
				break;
			case GIVE_DIAMOND_SUCCESS:
				// MessageUtil.ToastMessage(context, "发钻！");
				initDate();
				break;
			case GIVE_DIAMOND_FAILED:
				// ProgressDialogHelper.dismiss();
				MessageUtil.ToastMessage(context, "发钻失败！");
				break;
			case GET_PROJECT_SUCCESS:
				mProjectList = (List<字典>) msg.obj;
				break;
			case GET_PROJECT_FAILED:
				break;
			}
		};
	};

	private OnFragmentButtonClick onFragmentButtonClick;

	public void setOnButtonClick(OnFragmentButtonClick onFragmentButtonClick) {
		this.onFragmentButtonClick = onFragmentButtonClick;
	}

	private void startTaskInfo(final 任务 task) {
		mTask = task;
		Intent intent = new Intent(context, TaskInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(TaskInfoActivity.TAG, task);
		intent.putExtras(bundle);
		startActivity(intent);
		if (TextUtils.isEmpty(mTask.ReadTime)) {
			// 读任务
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						zlServiceHelper.ReadDynamic(task.Id, 3);
					} catch (Exception e) {
						LogUtils.i(TAG, "" + e);
					}
				}
			}).start();
		}
	}

	private void showInput() {

		imm.showSoftInput(etContent, InputMethodManager.SHOW_FORCED);
	}

	/***
	 * 切换文本框输入
	 */
	private void changeEditInput() {
		if (!isComment) { // 状态为评论，则收缩键盘恢复‘发任务状态’
			etContent.setHint("给自己发条任务...");
		} else {// 状态不是评论，在设置为'评论状态'
			etContent.setHint("评论一句吧...");
		}
	}

	public interface OnFragmentButtonClick {
		public abstract void onClick();
	}
}