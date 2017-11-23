package com.cedarhd;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.意见反馈;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/** 投诉建议，给波尔云提意见 */
public class GiveSuggestionActivity extends BaseActivity {
	private Context mContext;

	private 意见反馈 mOpinion;

	private ImageView ivBack;
	private TextView tvSave;
	private EditText etContent;
	private EditText etContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_give_suggestions);

		initData();
		initViews();
		setOnEvent();
	}

	private void initData() {
		mContext = GiveSuggestionActivity.this;
		mOpinion = new 意见反馈();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_suggestion);
		tvSave = (TextView) findViewById(R.id.tv_save_suggestion);
		etContent = (EditText) findViewById(R.id.et_content_suggestion);
		etContact = (EditText) findViewById(R.id.et_contact_suggestions);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = etContent.getText().toString();
				if (TextUtils.isEmpty(content)) {
					showShortToast("请填写反馈意见");
				} else if (content.length() <= 10) {
					showShortToast("反馈意见内容不少于10字");
				} else {
					saveOpinion(content);
				}
			}
		});
	}

	private void saveOpinion(String content) {
		ProgressDialogHelper.show(mContext, false);
		String contact = etContact.getText().toString();
		mOpinion.内容 = content;
		mOpinion.设备型号 = Build.MODEL;
		if (!TextUtils.isEmpty(contact)) {
			mOpinion.QQ = contact;
			mOpinion.电话 = contact;
			mOpinion.邮箱 = contact;
		}

		String url = Global.BASE_URL + "Opinion/saveOpinion";
		StringRequest.postAsyn(url, mOpinion, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("服务器访问异常");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功！");
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败");
			}
		});

	}

}
