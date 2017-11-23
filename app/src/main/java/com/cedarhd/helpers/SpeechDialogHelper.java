package com.cedarhd.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cedarhd.R;
import com.cedarhd.helpers.server.JsonParser;
import com.cedarhd.utils.LogUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * 讯飞语音识别帮助类
 * 
 */
public class SpeechDialogHelper {

	private Context context;
	private SpeechRecognizer mIat;
	private Boolean isDirect;// 是否直接说话

	/*** 是否追加 */
	private boolean isAppend;//
	private String content;
	private EditText etText; // activity上输入框内容

	private EditText etContentDialog; // 对话框的输入框
	private Button btnSpeek; // 按住说话
	private Button btnSave; // 完成

	/**
	 * 语音对话框帮助
	 * 
	 * @param context
	 * @param activity
	 *            无用参数，可为空
	 * @param etText
	 *            语音输入的文本框
	 * @param isDirect
	 *            是否直接开始语音输入
	 */
	@Deprecated
	public SpeechDialogHelper(Context context, Activity activity,
			EditText etText, Boolean isDirect) {
		super();
		this.context = context;
		this.etText = etText;
		this.isDirect = isDirect;
		this.content = etText.getText().toString();
		initSpeek();
		if (isDirect) {
			startSpeek();
		} else {
			showSpeechDialog();
		}
	}

	/**
	 * 语音对话框帮助
	 * 
	 * @param context
	 * @param etText
	 *            语音输入的文本框
	 * @param isDirect
	 *            是否直接开始语音输入
	 */
	public SpeechDialogHelper(Context context, EditText etText, Boolean isDirect) {
		super();
		this.context = context;
		this.isDirect = isDirect;
		this.etText = etText;
		if (etText != null) {
			this.content = etText.getText().toString();
		}
		initSpeek();
		if (isDirect) {
			startSpeek();
		} else {
			showSpeechDialog();
		}
	}

	/**
	 * 语音对话框帮助
	 * 
	 * @param context
	 * @param etText
	 *            语音输入的文本框
	 * @param isDirect
	 *            是否直接开始语音输入
	 */
	public SpeechDialogHelper(Context context, Boolean isDirect) {
		this(context, null, isDirect);
	}

	/**
	 * 语音对话框帮助
	 * 
	 * @param context
	 * @param activity
	 * @param etText
	 *            语音输入的文本框
	 * @param isDirect
	 *            是否直接开始语音输入
	 * @param isAppend
	 *            是否追加的方式把新输入内容拼接到原有内容后面
	 */
	public SpeechDialogHelper(Context context, EditText etText,
			Boolean isDirect, boolean isAppend) {
		super();
		this.context = context;
		this.etText = etText;
		this.isDirect = isDirect;
		this.isAppend = isAppend;
		this.content = etText.getText().toString();
		initSpeek();
		if (!isAppend) {
			etText.setText("");
		}
		if (isDirect) {
			startSpeek();
		} else {
			showSpeechDialog();
		}
	}

	/**
	 * 弹出语音输入对话框
	 */
	private void showSpeechDialog() {
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		Dialog dialog = new Dialog(context, R.style.style_dialog_normal);
		// builder.setTitle("语音输入");
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_speech, null);
		// builder.setView(view);
		// AlertDialog dialog = builder.create();
		dialog.setContentView(view);
		findViews(view, dialog);
		// WindowManager windowManager = activity.getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		// WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// lp.width = (int) (display.getWidth()); // 设置宽度
		// dialog.getWindow().setAttributes(lp);
		// dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	private void findViews(View view, final Dialog dialog) {
		etContentDialog = (EditText) view
				.findViewById(R.id.etContent_dialog_speech);
		btnSpeek = (Button) view.findViewById(R.id.btn_speek_dialog);
		btnSave = (Button) view.findViewById(R.id.btn_speech_over_dialog);

		if (!TextUtils.isEmpty(content)) {
			etContentDialog.setText(content);
		}
		// 按住说话按钮
		btnSpeek.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSpeek();
			}
		});

		// 完成 关闭对话框
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 历史问题，此处需要调整
				if (etText != null) {
					etText.setText(etContentDialog.getText().toString());
				}

				if (mOnCompleteListener != null) {
					mOnCompleteListener.onComplete(etContentDialog.getText()
							.toString());
				}
				dialog.dismiss();
			}
		});

	}

	/**
	 * 带Ui界面的语音监听
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		@Override
		public void onResult(RecognizerResult results, boolean arg1) {
			LogUtils.d("Result:", results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString())
					.replace("。", "");
			if (isDirect) {
				int index = etText.getSelectionStart(); // 获得光标所在位置
				Editable editable = etText.getText();
				editable.insert(index, text); // 在光标处插入
				// etText.append(text);
				etText.setSelection(etText.length());
			} else {
				int index = etContentDialog.getSelectionStart(); // 获得光标所在位置
				// etContentDialog.append(text);
				Editable editable = etContentDialog.getText();
				editable.insert(index, text); // 在光标处插入
				etContentDialog.setSelection(etContentDialog.length());
			}
		}

		@Override
		public void onError(SpeechError arg0) {

		}

	};

	private void initSpeek() {
		// 1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
		mIat = SpeechRecognizer.createRecognizer(context, null);
		// 2.设置听写参数，
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
	}

	/**
	 * 开启说话
	 */
	private void startSpeek() {
		setParam();
		RecognizerDialog iatDialog2 = new RecognizerDialog(context,
				mInitListener);
		iatDialog2.setListener(recognizerDialogListener);
		iatDialog2.show();

		iatDialog2.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mOnCompleteListener != null) {
					mOnCompleteListener.onComplete(etContentDialog.getText()
							.toString());

					if (etText != null) {
						mOnCompleteListener.onComplete(etText.getText()
								.toString());
					}
				}
			}
		});
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			LogUtils.d("InitListener", "SpeechRecognizer init() code = " + code);
			if (code == ErrorCode.SUCCESS) {
				// btnSpeek.setEnabled(true);
			}
		}
	};

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public void setParam() {
		// 设置语言区域
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				"/sdcard/iflytek/wavaudio.pcm");

	}

	private OnCompleteListener mOnCompleteListener;

	/** 设置监听完毕 */
	public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
		this.mOnCompleteListener = onCompleteListener;
	}

	public interface OnCompleteListener {
		void onComplete(String result);
	}
}
