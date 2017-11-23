package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.server.ZLServiceHelper;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.MessageUtil;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 审批页面:填写审批意见
 * 
 * @author BOHR
 * 
 */
public class Approval_Opinion extends BaseActivity {

	Context context;
	ZLServiceHelper mZLServiceHelper = new ZLServiceHelper();
	public static final String TAG = "Approval_Opinion";
	public static final String FORMID = "formId";// 表单Id
	public static final String OPINION = "opinion";// 意见内容
	public static final String TITLE = "title";// 标题

	public static final int APPROVAL_OPINION_RESULT = 201;// 跳转页面
	public static final int APPROVAL_OPINION_RESULT_SUCCEED = 202;// 审批或否决成功

	private ImageView imageViewCancel_opinionInfo;
	private ImageView imageViewDone_opinionInfo;
	private TextView textViewTitle_opinionInfo;
	private EditText et_opinionInfo;
	private Button buttonTag_opinionInfo_apprival; // 审核按钮
	private Button buttonTag_opinionInfo_votedown; // 否决按钮
	private ProgressBar pBar;// 上传突变进度条

	int formId; // 服务器的表单编号,每个item的表单编号
	String opinion = "";
	String title = "";

	public static int AUDIT_SUCCESSED = 3; // 审批成功
	public static int AUDIT_FAILURE = 4; // 审批失败

	private PopupWindow popupWindow; // 领导签名窗体
	private String signurePath;// 签名图片路径
	private String attachId; // 签名上传图片附件号
	private final int defaultPaintWeight = 6; // 默认画笔粗细
	private ImageView btnResume;
	private ImageView ivSave;
	private ImageView iv_canvas;
	private Button btnSpeek;
	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;

	String typeName = "";
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 3:
				typeName = (String) msg.obj;
				MessageUtil.ToastMessage(context, typeName + "成功！");
				setResult(APPROVAL_OPINION_RESULT_SUCCEED);
				finish();
				break;
			case 4:
				typeName = (String) msg.obj;
				MessageUtil.ToastMessage(context, typeName + "失败！");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.approval_opinion_activity);
		context = Approval_Opinion.this;
		formId = getIntent().getIntExtra(FORMID, 0);
		opinion = getIntent().getStringExtra(OPINION);
		title = getIntent().getStringExtra(TITLE);

		findViews();
		setOnClickListener();
	}

	public void findViews() {
		imageViewCancel_opinionInfo = (ImageView) findViewById(R.id.imageViewCancel_opinionInfo);
		imageViewDone_opinionInfo = (ImageView) findViewById(R.id.imageViewDone_opinionInfo);
		textViewTitle_opinionInfo = (TextView) findViewById(R.id.textViewTitle_opinionInfo);
		et_opinionInfo = (EditText) findViewById(R.id.et_opinionInfo);
		buttonTag_opinionInfo_apprival = (Button) findViewById(R.id.buttonTag_opinionInfo_apprival);
		buttonTag_opinionInfo_votedown = (Button) findViewById(R.id.buttonTag_opinionInfo_votedown);
		pBar = (ProgressBar) findViewById(R.id.pbar_approval_opinion);
		btnSpeek = (Button) findViewById(R.id.btn_speek2_oppinion);
		textViewTitle_opinionInfo.setText(title);
		et_opinionInfo.setText(opinion);
		et_opinionInfo.setSelection(opinion.length());
		btnSpeek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// new SpeechDialogHelper(context, Approval_Opinion.this,
				// et_opinionInfo, false);
				new SpeechDialogHelper(context, Approval_Opinion.this,
						et_opinionInfo, true);
			}
		});
	}

	public void setOnClickListener() {
		imageViewCancel_opinionInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				opinion = et_opinionInfo.getText().toString().trim();
				Intent intent = new Intent();
				intent.putExtra(OPINION, opinion);
				setResult(APPROVAL_OPINION_RESULT, intent);
				finish();
			}
		});

		imageViewDone_opinionInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				opinion = et_opinionInfo.getText().toString().trim();
				Intent intent = new Intent();
				intent.putExtra(OPINION, opinion);
				setResult(APPROVAL_OPINION_RESULT, intent);
				finish();
			}
		});

		// 审核按钮监听
		buttonTag_opinionInfo_apprival
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						opinion = et_opinionInfo.getText().toString().trim();
						if (TextUtils.isEmpty(opinion)) {
							// Toast.makeText(context, "请填写审核意见",
							// Toast.LENGTH_SHORT).show();
							opinion = "无意见";
						}
						ShowDialogIsSignure(v);
					}
				});
		// 否决按钮监听
		buttonTag_opinionInfo_votedown
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						opinion = et_opinionInfo.getText().toString().trim();
						boolean isSuccess = mZLServiceHelper.submitVoteDown(
								opinion, formId);
						new Thread(new Runnable() {
							public void run() {
								try {
									mZLServiceHelper.submitApprovalNew(opinion,
											"", formId, false, handler);
								} catch (Exception e) {
									Toast.makeText(context,
											"审核异常：" + e.getMessage(),
											Toast.LENGTH_SHORT).show();
								}
							}
						}).start();
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false; // 禁用回退键
	}

	/**
	 * 显示签名框
	 */
	private void showSignureWindow() {
		View popupView = getLayoutInflater().inflate(R.layout.dialog_signure,
				null);
		View v = popupView.findViewById(R.id.view_top);

		int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();

		Rect rect = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top; // 状态栏高度

		popupWindow = new PopupWindow(popupView, mScreenWidth, mScreenHeight
				- statusBarHeight);
		// popupWindow.setWidth(mScreenWidth);
		// popupWindow.setHeight(mScreenHeight);
		// 点击空白处 对话框消失
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
				(Bitmap) null));
		initPopupWindow(popupView);

		// int[] location = new int[2];
		// v.getLocationOnScreen(location);
		popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
		// popupWindow.showAsDropDown(v);
	}

	private void initPopupWindow(View view) {
		// 初始化一个画笔，笔触宽度为5
		paint = new Paint();
		paint.setStrokeWidth(defaultPaintWeight);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);// 画笔加上抗锯齿效果
		iv_canvas = (ImageView) view.findViewById(R.id.iv_canvas);
		iv_canvas.setOnTouchListener(touch);

		btnResume = (ImageView) view.findViewById(R.id.iv_clear_signure);
		ivSave = (ImageView) view.findViewById(R.id.iv_save_signure);
		btnResume.setOnClickListener(clickListener);
		ivSave.setOnClickListener(clickListener);
	}

	private View.OnTouchListener touch = new OnTouchListener() {
		// 定义手指开始触摸的坐标
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			// 用户按下动作
			case MotionEvent.ACTION_DOWN:
				// 第一次绘图初始化内存图片，指定背景为白色
				if (baseBitmap == null) {
					baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
							iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
					// // 画布上设置抗锯齿属性，效果比paint加锯齿效果更好
					// canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
					// Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
					canvas.drawColor(Color.WHITE);
				}
				// 记录开始触摸的点的坐标
				startX = event.getX();
				startY = event.getY();
				break;
			// 用户手指在屏幕上移动的动作
			case MotionEvent.ACTION_MOVE:
				// 记录移动位置的点的坐标
				float stopX = event.getX();
				float stopY = event.getY();
				// 根据两点坐标，绘制连线
				canvas.drawLine(startX, startY, stopX, stopY, paint);

				// 绘制点
				// canvas.drawPoint(stopX, stopY, paint);
				// 更新开始点的位置
				startX = event.getX();
				startY = event.getY();
				// 把图片展示到ImageView中
				iv_canvas.setImageBitmap(baseBitmap);
				break;
			case MotionEvent.ACTION_UP:

				break;
			default:
				break;
			}
			return true;
		}
	};

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_clear_signure:
				resumeCanvas();
				break;
			case R.id.iv_save_signure:
				saveBitmap();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 清除画板
	 */
	protected void resumeCanvas() {
		signurePath = ""; // 清空图片原有路径
		// 手动清除画板的绘图，重新创建一个画板
		if (baseBitmap != null) {
			baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
					iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(baseBitmap);
			canvas.drawColor(Color.WHITE);
			iv_canvas.setImageBitmap(baseBitmap);
			Toast.makeText(context, "清除成功，可以重新开始签名", 0).show();
		}
	}

	/**
	 * 保存图片到SD卡上
	 */
	protected void saveBitmap() {
		try {
			// 保存图片到SD卡上
			File file = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".png");
			FileOutputStream stream = new FileOutputStream(file);
			final boolean isSave = baseBitmap.compress(CompressFormat.PNG, 100,
					stream);
			Toast.makeText(context, "保存签名成功", 0).show();
			// Android设备Gallery应用只会在启动的时候扫描系统文件夹
			// 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			sendBroadcast(intent);
			signurePath = file.getAbsolutePath();
			popupWindow.dismiss(); // 保存签名成功并关闭画板,提交
			findViewById(R.id.ll_upload_signure).setVisibility(View.GONE);
			LogUtils.i("testkenoAudit", "isSave=" + isSave);
			if (isSave) {
				attachId = mZLServiceHelper
						.uploadAttachPhoto(signurePath, pBar);
				LogUtils.i("testkenoAudit", "attachId=" + attachId);
			}
			new Thread(new Runnable() {
				public void run() {
					try {
						LogUtils.i("testkenoAudit", "attachId2=" + attachId);
						mZLServiceHelper.submitApprovalNew(opinion, attachId,
								formId, true, handler);
					} catch (Exception e) {
						Toast.makeText(context, "审核异常：" + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
			}).start();
		} catch (Exception e) {
			Toast.makeText(context, "保存图片失败", 0).show();
			LogUtils.i("savePng", "savePng" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 是否使用签名
	 */
	private void ShowDialogIsSignure(final View view) {
		AlertDialog.Builder builderWrite = new AlertDialog.Builder(
				Approval_Opinion.this);
		builderWrite.setMessage("是否手写签字").setCancelable(false)
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// TODO 手写签字
						showSignureWindow();
						dialog.cancel();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new Thread(new Runnable() {
							public void run() {
								try {
									// 不上传电子签名附件
									mZLServiceHelper.submitApprovalNew(opinion,
											"", formId, true, handler);
								} catch (Exception e) {
									Toast.makeText(context,
											"审核异常：" + e.getMessage(),
											Toast.LENGTH_SHORT).show();
								}
							}
						}).start();
						dialog.cancel();
					}
				});
		builderWrite.create().show();
	}
}
