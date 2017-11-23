package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.constants.PreferencesConfig;
import com.cedarhd.helpers.SharedPreferencesHelper;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.helpers.SpeechDialogHelper.OnCompleteListener;

/***
 * 语音菜单页面，模仿百度地图的语音菜单，比如说赵客户，波尔公司，就能打开客户的详细页面。说发任务，进一步提示说出任务的内容，再说说内容的这时候就发出来了。
 * 
 * @author K
 * 
 */
public class SpeechMenuActivity extends BaseActivity {
	private Context mContext;

	/** 标志位：是否是详细信息 */
	private boolean isSummaryInfo;

	private SharedPreferencesHelper sharedPreferencesHelper;

	private EditText etContent;

	private ImageView ivBack;

	/** 查看详细信息 */
	private ImageView ivInfo;

	/** 语音输入按钮 */
	private ImageView ivSpeech;

	/** 点击问号弹出详细信息部分 */
	private ScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech_menu);

		initData();
		initViews();
		setOnEvent();
	}

	private void initData() {
		mContext = this;
		sharedPreferencesHelper = new SharedPreferencesHelper(mContext,
				PreferencesConfig.APP_USER_INFO);
	}

	private void initViews() {
		etContent = (EditText) findViewById(R.id.et_content_speech_menu);
		ivBack = (ImageView) findViewById(R.id.iv_cancel_speech_menu);
		ivInfo = (ImageView) findViewById(R.id.iv_info_speech_menu);
		ivSpeech = (ImageView) findViewById(R.id.iv_voice_speech_menu);
		scrollView = (ScrollView) findViewById(R.id.scrollView_info_speech_menu);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSummaryInfo = !isSummaryInfo;
				if (isSummaryInfo) {
					scrollView.setVisibility(View.GONE);
					ivInfo.setImageResource(R.drawable.ico_wenhao_normal);
				} else {
					scrollView.setVisibility(View.VISIBLE);
					ivInfo.setImageResource(R.drawable.ico_wenhao_press);
				}
			}
		});

		ivSpeech.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SpeechDialogHelper helper = new SpeechDialogHelper(mContext,
						etContent, true, false);
				helper.setOnCompleteListener(new OnCompleteListener() {
					@Override
					public void onComplete(String result) {
						showShortToast(result);
						startActivityByKey(result);
					}
				});
			}
		});
	}

	/***
	 * 根据关键词打开对应的页面
	 * 
	 * @param key
	 *            关键词
	 */
	private void startActivityByKey(String key) {
		if (TextUtils.isEmpty(key)) {
			etContent.setText("请点击麦克风说话");
		} else {
			if (key.contains("任务")) { // 任务模块
				if (key.contains("新建") || key.contains("写")
						|| key.contains("发布")) {
					startActivity(new Intent(mContext, TaskNewActivity.class));
				} else {
					startTaskList();
				}
			} else if (key.contains("通知")) { // 通知模块

			} else if (key.contains("客户")) { // 通知模块

			} else if (key.contains("通知")) { // 通知模块

			}
		}
	}

	/***
	 * 任务列表
	 */
	private void startTaskList() {
		//
		boolean isCalendar = sharedPreferencesHelper.getBooleanValue(
				PreferencesConfig.IS_CALENDER_MODE_OPEN_TASK, true);
		if (isCalendar) {
			startActivity(new Intent(mContext, TaskCalenderActivity.class));
		} else {
			startActivity(new Intent(mContext, TaskCalenderActivity.class));
		}
	}
}
