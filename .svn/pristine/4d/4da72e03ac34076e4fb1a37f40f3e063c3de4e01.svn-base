package com.cedarhd.changhui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Dict;
import com.cedarhd.models.客户工作计划;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

public class ChWorkPlanInfoActivity extends BaseActivity {
	public static final String TAG_INFO = "tag_info";
	public static final String TAG_DICTS = "tag_dicts";

	private Context mContext;
	private DictIosPicker mDictIosPicker;
	private 客户工作计划 mWorkPlan;
	private HashMap<String, List<Dict>> mDicts;

	private BoeryunHeaderView headerView;
	private TextView tvPlanType;
	private TextView tvClient;
	private TextView tvZzType;
	private EditText etPlanContent;
	private EditText etZzContent;
	private EditText etQuestion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ch_work_plan_info);
		initViews();
		initDatas();
		setOnEvent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Client client = ClientBiz.onActivityGetClient(mContext, requestCode,
				data);
		if (client != null && client.getId() != 0) {
			mWorkPlan.客户 = client.getId();
			tvClient.setText(client.getCustomerName() + "");
		}
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_work_plan_list);
		tvPlanType = (TextView) findViewById(R.id.tv_plan_type_work_plan_info);
		tvClient = (TextView) findViewById(R.id.tv_client_work_plan_info);
		tvZzType = (TextView) findViewById(R.id.tv_zhuizong_type_work_plan_info);
		etPlanContent = (EditText) findViewById(R.id.et_content_work_plan_info);
		etZzContent = (EditText) findViewById(R.id.et_zhuizong_content_work_plan_info);
		etQuestion = (EditText) findViewById(R.id.et_question_work_plan_info);
	}

	private void initDatas() {
		mContext = this;
		mDictIosPicker = new DictIosPicker(mContext);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mWorkPlan = (客户工作计划) bundle.getSerializable(TAG_INFO);
			mDicts = (HashMap<String, List<Dict>>) bundle
					.getSerializable(TAG_DICTS);
		}

		if (mWorkPlan != null) {
			showDatas();
		} else {
			mWorkPlan = new 客户工作计划();
		}
	}

	private void showDatas() {
		etPlanContent.setText(mWorkPlan.内容 + "");
		etZzContent.setText(mWorkPlan.追踪内容 + "");
		etQuestion.setText(mWorkPlan.困难问题 + "");

		tvPlanType.setText(getDictValue("客户工作计划_工作类型", mWorkPlan.工作类型) + "");
		tvClient.setText(getDictValue("客户", mWorkPlan.客户));
		tvZzType.setText(getDictValue("客户工作计划_追踪方式", mWorkPlan.追踪方式));

	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				if (mWorkPlan != null) {
					mWorkPlan.创建时间 = ViewHelper.getDateString();
					if (mWorkPlan.工作类型 == 0) {
						showShortToast("请选择工作类型");
						return;
					}

					mWorkPlan.内容 = etPlanContent.getText().toString();
					if (TextUtils.isEmpty(mWorkPlan.内容)) {
						showShortToast("请输入计划内容");
						return;
					}

					mWorkPlan.追踪内容 = etZzContent.getText().toString();
					mWorkPlan.困难问题 = etQuestion.getText().toString();
					saveWorkPlan();
				}
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		tvPlanType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Dict> dicts = getDictList("客户工作计划_工作类型");
				mDictIosPicker.show(R.id.root_ch_work_plan_info, dicts, "名称");
				mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						mWorkPlan.工作类型 = dicts.get(index).编号;
						tvPlanType.setText(dicts.get(index).名称 + "");
					}
				});
			}
		});

		tvZzType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Dict> dicts = getDictList("客户工作计划_追踪方式");
				mDictIosPicker.show(R.id.root_ch_work_plan_info, dicts, "名称");
				mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						mWorkPlan.追踪方式 = dicts.get(index).编号;
						tvZzType.setText(dicts.get(index).名称 + "");
					}
				});
			}
		});

		tvClient.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ClientBiz.selectClient(mContext);
			}
		});
	}

	private void saveWorkPlan() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "CustomerWorkPlan/UpdateWorkPlan";
		StringRequest.postAsyn(url, mWorkPlan, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("保存成功");
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("服务器访问异常");
			}
		});
	}

	private String getDictValue(String dictName, int dictId) {
		List<Dict> list = getDictList(dictName);
		if (list != null && list.size() >= 0) {
			for (Dict dict : list) {
				if (dictId == dict.编号) {
					return dict.名称;
				}
			}
		}
		return "";
	}

	private List<Dict> getDictList(String dictName) {
		if (mDicts == null)
			return null;

		List<Dict> list = mDicts.get(dictName);
		return list;
	}
}
