package com.cedarhd.crm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.control.BoeryunDateSelectView;
import com.cedarhd.control.BoeryunDictSelectView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.User;
import com.cedarhd.models.crm.工作计划Crm;
import com.cedarhd.models.字典;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/***
 * 新建工作计划
 * 
 * @author K
 * 
 */
public class CRMAddWorkplanActivity extends BaseActivity {

	private Context mContext;

	private 工作计划Crm mWorkplan;

	private ImageView ivBack;
	private ImageView ivSave;

	private EditText etContent;
	private EditText etExecutor;
	private EditText etParticipant;
	private BoeryunDateSelectView selectStartTime;
	private BoeryunDateSelectView selectEndTime;
	private EditText etSalechance;
	private BoeryunDictSelectView dictSelectProject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_workplan_crm);

		initData();
		initViews();
		setOnEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case UserBiz.SELECT_SINAL_USER_REQUEST_CODE:
			// 返回选中执行人
			User signalUser = UserBiz.onActivityUserSelected(requestCode,
					resultCode, data);
			if (signalUser != null && !TextUtils.isEmpty(signalUser.Id)) {
				etExecutor.setText(signalUser.getUserName() + "");
				etExecutor.setTag(signalUser);
				try {
					mWorkplan.执行人 = Integer.parseInt(signalUser.getId());
				} catch (Exception e) {
					LogUtils.e(TAG, e + "");
				}
			}
			break;
		case UserBiz.SELECT_MULTI_USER_REQUEST_CODE:
			User multiUser = UserBiz.onActivityMultiUserSelected(requestCode,
					resultCode, data);
			if (multiUser != null && !TextUtils.isEmpty(multiUser.getUserIds())) {
				etParticipant.setText(multiUser.getUserNames());
				etParticipant.setTag(multiUser);
				mWorkplan.参与人 = multiUser.getUserIds();
			}
			break;
		default:
			break;
		}

	}

	private void initData() {
		mContext = CRMAddWorkplanActivity.this;
		mWorkplan = new 工作计划Crm();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_add_workplan);
		ivSave = (ImageView) findViewById(R.id.iv_save_add_workplan);

		etContent = (EditText) findViewById(R.id.et_content_add_workplan);
		etExecutor = (EditText) findViewById(R.id.et_executor_add_workplan);
		etParticipant = (EditText) findViewById(R.id.et_paticipant_add_workplan);

		selectStartTime = (BoeryunDateSelectView) findViewById(R.id.date_starttime_add_workplan);
		selectEndTime = (BoeryunDateSelectView) findViewById(R.id.date_endtime_add_workplan);

		etSalechance = (EditText) findViewById(R.id.et_salechance_add_workplan);

		dictSelectProject = (BoeryunDictSelectView) findViewById(R.id.dictSelect_project_add_workplan);
	}

	private void setOnEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		etExecutor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UserBiz.selectSinalUser(mContext);
			}
		});

		etParticipant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserBiz.selectMultiUser(mContext, "");
			}
		});

		ivSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!HttpUtils.IsHaveInternet(mContext)) {
					showShortToast("请检查网络连接");
				} else {
					if (isCheck()) {
						save();
					}
				}
			}
		});
	}

	/** 空值校验 */
	private boolean isCheck() {
		String content = etContent.getText().toString();
		if (TextUtils.isEmpty(content)) {
			showShortToast("请输入计划内容");
			return false;
		} else {
			mWorkplan.内容 = content;
		}

		if (mWorkplan.执行人 == 0) {
			showShortToast("请选择执行人");
			return false;
		}

		if (TextUtils.isEmpty(mWorkplan.参与人)) {
			showShortToast("请选择参与人");
			return false;
		}

		字典 dict = (字典) dictSelectProject.getTag();
		if (dict == null) {
			showShortToast("请选择项目");
			return false;
		} else {
			mWorkplan.项目 = dict.getId();
		}

		return true;
	}

	private void save() {
		ProgressDialogHelper.show(mContext, false);
		String url = Global.BASE_URL + "Workplan/saveWorkPlan";
		StringRequest.postAsyn(url, mWorkplan, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("网络访问异常");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功");
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
