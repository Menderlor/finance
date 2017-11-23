package com.cedarhd.crm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cedarhd.CreateVmFormActivity;
import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.crm.流程分类表设置;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.List;

/** 选择合同分类 */
public class CRMSelectConpactCategoryActivity extends BaseActivity {

	private Context mContext;
	private List<流程分类表设置> mList;

	private ListView lv;
	private ImageView ivBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_conpact);
		initData();
		initView();
		setEvent();

		ProgressDialogHelper.show(mContext);
		fetchConpactList();
	}

	private void initData() {
		mContext = this;
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_back_select_conpact);
		lv = (ListView) findViewById(R.id.lv_select_conpact);
	}

	private void setEvent() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				流程分类表设置 item = mList.get(position);
				// xml生成表单
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("id", 0);
				bundle.putInt("typeId", item.流程分类表);// 115表示报销申请单
				bundle.putString("dataId", "0"); // 新建
				bundle.putString("typeName", item.表名);
				intent.putExtras(bundle);
				intent.setClass(mContext, CreateVmFormActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/** 加载合同列表 */
	private void fetchConpactList() {
		String url = Global.BASE_URL + "SaleSummary/getContactCategory";
		StringRequest.getAsyn(url, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				mList = JsonUtils.ConvertJsonToList(response, 流程分类表设置.class);
				lv.setAdapter(getAdapter(mList));
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
			}
		});
	}

	protected CommanAdapter<流程分类表设置> getAdapter(List<流程分类表设置> list) {
		return new CommanAdapter<流程分类表设置>(list, mContext,
				R.layout.item_textview) {
			@Override
			public void convert(int position, 流程分类表设置 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_name_item);
				tvName.setText(item.表名);
			}
		};
	}
}
