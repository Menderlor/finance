package com.cedarhd.rad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.rad.Rad上报数据;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

/** 森盟上报页面 2015/11/16 11:21 */
public class RadReportInfoActivity extends BaseActivity {
	public static final String TAG_REPORT_INFO = "reportInfo";

	private boolean isEdit = true;

	private final String SP_VISITE = "sp_visite";
	private final String SP_ASK = "sp_ask";
	private final String SP_DEAL = "sp_deal";

	private Context mContext;

	private DictionaryHelper mDictionaryHelper;
	// private SharedPreferencesHelper mSharedPreferencesHelper;

	private Rad上报数据 mReportData;

	private BoeryunHeaderView headerView;
	private TextView tvUserInfo;
	private TextView tvTime;
	private BoeryunSelectCountView countViewVisite;
	private BoeryunSelectCountView countViewAsk;
	private BoeryunSelectCountView countViewDeal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_report_info);
		initViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		setOnEvent();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadInfo();
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isEdit) {
				// 如果可编辑则记录当前数据
				// saveLocalCount((int) mReportData.进店人数, (int)
				// mReportData.咨询人数,
				// (int) mReportData.成交人数);

				saveReport();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 
	 * @param visiteCount
	 *            进店人数
	 * @param askCount
	 *            咨询人数
	 * @param dealCount
	 *            成交人数
	 */
	private void saveLocalCount(int visiteCount, int askCount, int dealCount) {
		// mSharedPreferencesHelper.putIntValue(SP_VISITE, visiteCount);
		// mSharedPreferencesHelper.putIntValue(SP_ASK, askCount);
		// mSharedPreferencesHelper.putIntValue(SP_DEAL, dealCount);
	}

	private void initData() {
		mContext = RadReportInfoActivity.this;
		mDictionaryHelper = new DictionaryHelper(mContext);
		// mSharedPreferencesHelper = new SharedPreferencesHelper(mContext,
		// PreferencesConfig.APP_USER_INFO);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			// 1.点击列表进入，2.或 已存在今天数据 都直接传递上报数据
			mReportData = (Rad上报数据) bundle.getSerializable(TAG_REPORT_INFO);

			String reportDate = DateDeserializer
					.getFormatDate(mReportData.上报时间);
			String todayDate = DateDeserializer.getFormatDate(ViewHelper
					.getDateToday());
			showShortToast(reportDate + "--" + todayDate);

			if (!todayDate.equals(reportDate)
					|| !(mReportData.上报人 + "").equals(Global.mUser.getId())) {
				isEdit = false;
			}
		}

		if (mReportData == null) {
			// 不存在当天数据，新建一条记录
			mReportData = new Rad上报数据();
			mReportData.上报时间 = ViewHelper.getDateToday();
			mReportData.上报人 = Integer.parseInt(Global.mUser.getId());
			// mReportData.进店人数 =
			// mSharedPreferencesHelper.getIntValue(SP_VISITE);
			// mReportData.咨询人数 = mSharedPreferencesHelper.getIntValue(SP_ASK);
			// mReportData.成交人数 = mSharedPreferencesHelper.getIntValue(SP_DEAL);
		}

		if (!isEdit) {
			// 如果上报日期不等于今天则不可编辑
			headerView.ivSave.setVisibility(View.GONE);
			countViewVisite.setEnabled(isEdit);
			countViewAsk.setEnabled(isEdit);
			countViewDeal.setEnabled(isEdit);
		}
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_rad_report_info);
		tvUserInfo = (TextView) findViewById(R.id.tv_userInfo_rad_report_info);
		tvTime = (TextView) findViewById(R.id.tv_time_rad_report_info);
		countViewVisite = (BoeryunSelectCountView) findViewById(R.id.select_countview_visite_rad_report_info);
		countViewAsk = (BoeryunSelectCountView) findViewById(R.id.select_countview_ask_rad_report_info);
		countViewDeal = (BoeryunSelectCountView) findViewById(R.id.select_countview_deal_rad_report_info);
	}

	private void setOnEvent() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {
				saveReport();
			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
				saveReport();
				finish();
			}
		});

		countViewVisite.setOnNumChangedeListener(new OnNumChanged() {
			@Override
			public void onchange(int value) {
				mReportData.进店人数 = value;
			}
		});

		countViewAsk.setOnNumChangedeListener(new OnNumChanged() {
			@Override
			public void onchange(int value) {
				mReportData.咨询人数 = value;
			}
		});

		countViewDeal.setOnNumChangedeListener(new OnNumChanged() {
			@Override
			public void onchange(int value) {
				mReportData.成交人数 = value;
			}
		});

	}

	private void loadInfo() {
		tvTime.setText(DateDeserializer.getFormatDate(mReportData.上报时间));
		try {
			String department = mDictionaryHelper
					.getDepartNameById(Global.mUser.Department + "");
			String userName = mDictionaryHelper
					.getUserNameById(mReportData.上报人);
			tvUserInfo.setText(department + ">" + userName);
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage() + "");
		}
		countViewVisite.setNum((int) mReportData.进店人数);
		countViewAsk.setNum((int) mReportData.咨询人数);
		countViewDeal.setNum((int) mReportData.成交人数);
	}

	private void saveReport() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "ShopReport/ReportData";
		StringRequest.postAsyn(url, mReportData, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("保存失败");
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				showShortToast("保存成功");
				// saveLocalCount(0, 0, 0);
				ProgressDialogHelper.dismiss();

				if (mReportData.编号 == 0) {
					try {
						String idStr = StrUtils.removeRex(JsonUtils
								.getStringValue(response, "Data"));
						mReportData.编号 = Integer.parseInt(idStr);
					} catch (JSONException e) {
						e.printStackTrace();
						LogUtils.e(TAG, e + "");
					}
				}

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(TAG_REPORT_INFO, mReportData);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("服务器访问异常");
				ProgressDialogHelper.dismiss();
			}
		});
	}
}
