package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.SpeechDialogHelper;
import com.cedarhd.utils.LogUtils;

public class SuggestContentActivity extends BaseActivity {
	private ImageView ivcancel;
	private ImageView ivsubmit;
	private EditText etContent;
	private Button speek;
	private Context context;
	public static final String EDITECONTENT = "EditContent";
	// private boolean flag = false; // 标志位：判断是否是其他页面打开
	public static final String Content = "content";
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_content);
		// flag = getIntent().getBooleanExtra(EDITECONTENT, false);
		Bundle bundle = getIntent().getExtras();
		content = bundle.getString(SuggestActivity.TAG);
		findviews();
		setonclicklistener();
	}

	private void findviews() {
		ivcancel = (ImageView) findViewById(R.id.ivCancel_taskinfo_content);
		ivsubmit = (ImageView) findViewById(R.id.ivSubmit_taskinfo_content);
		etContent = (EditText) findViewById(R.id.etContent_taskinfo_content);
		etContent.setText(content);
		speek = (Button) findViewById(R.id.btn_speek2_taskinfo_content);
		context = SuggestContentActivity.this;
	}

	private void setonclicklistener() {
		ivcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ivsubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// if (flag) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(Content, etContent.getText().toString());
				LogUtils.i("pytaskcontent", etContent.getText().toString());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
				// }
				// finish();
			}
		});
		speek.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new SpeechDialogHelper(context, SuggestContentActivity.this,
						etContent, true);
			}
		});

	}

}
