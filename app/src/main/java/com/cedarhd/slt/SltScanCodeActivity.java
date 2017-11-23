package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cedarhd.CaptureActivity;
import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickRightListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.slt.Slt条码;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

/** 森盟到货扫描 */
public class SltScanCodeActivity extends BaseActivity {
	/** 请求扫描 */
	private final int CODE_REQUEST_SCAN = 1;
	private Context mContext;

	private BoeryunHeaderView headerView;
	private TextView tvScan;
	private TextView tvClient;// 客户信息
	private TextView tvProduct;// 商品信息
	private TextView tvTotal; // 金额
	private TextView tvResult;
	private EditText etCode; // 条码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_scan_code);
		initData();
		initViews();
		setOnEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (data != null) {
				String scanCode = data
						.getStringExtra(CaptureActivity.RESULT_SCAN_CODE);
				showShortToast("扫描条码：" + scanCode);
				etCode.setText(scanCode + "");
				// scanCode = "0000476279";
				fetchSscanCode(scanCode);
			}
		}
	}

	private void initData() {
		mContext = this;
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_scan_code);
		tvScan = (TextView) findViewById(R.id.tv_scan_rad_code);
		tvClient = (TextView) findViewById(R.id.tv_client_rad_scan_code);
		tvProduct = (TextView) findViewById(R.id.tv_procuct_rad_scan_code);
		tvTotal = (TextView) findViewById(R.id.tv_total_rad_scan_code);
		tvResult = (TextView) findViewById(R.id.tv_result_rad_scan_code);
		etCode = (EditText) findViewById(R.id.et_code_rad_scan_code);
	}

	private void setOnEvent() {
		tvScan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startScanCodeActivity();
			}
		});

		headerView
				.setmButtonClickRightListener(new OnButtonClickRightListener() {
					@Override
					public void onClickSaveOrAdd() {

					}

					@Override
					public void onClickFilter() {

					}

					@Override
					public void onClickBack() {
						finish();
					}

					@Override
					public void onRightTextClick() {
						startActivity(new Intent(mContext,
								SltScanRecordListActivity.class));
					}
				});

		etCode.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					String scanCode = etCode.getText().toString();
					fetchSscanCode(scanCode);
				}
				return false;
			}
		});
	}

	private void fetchSscanCode(final String code) {
		ProgressDialogHelper.show(mContext, "检索中..");
		String url = Global.BASE_URL + "slt/scanCode/" + code;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("服务器访问异常");
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				Slt条码 slt条码 = JsonUtils
						.ConvertJsonObject(response, Slt条码.class);
				if (slt条码 != null) {
					showShortToast("" + slt条码.扫描结果);
					tvClient.setText("合同号：" + slt条码.合同号 + "\n单号：" + slt条码.单号);
					tvProduct.setText("型号：" + slt条码.型号名称 + "\n图号：" + slt条码.图号);
					tvResult.setText(slt条码.扫描结果 + "\t");
				}
				if (slt条码.slt扫描记录 != null) {
					tvTotal.setText("￥" + slt条码.slt扫描记录.奖励金额);
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("网络访问异常");
			}
		});
	}

	private void startScanCodeActivity() {
		Intent intent = new Intent(mContext, CaptureActivity.class);
		startActivityForResult(intent, CODE_REQUEST_SCAN);
	}
}
