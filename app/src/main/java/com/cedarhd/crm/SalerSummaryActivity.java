package com.cedarhd.crm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.constants.enums.EnumDateType;
import com.cedarhd.constants.enums.EnumSaleType;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand_销售统计;
import com.cedarhd.models.ReturnModel;
import com.cedarhd.models.User;
import com.cedarhd.models.业绩;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

import java.util.List;

/**
 * 销售员 销售业绩业绩排行榜
 * 
 * @author K at 2015/11/03 09:57
 * */
public class SalerSummaryActivity extends BaseActivity implements
		OnClickListener {

	/***
	 * 选择客户过滤类型
	 */
	String[] filterTypeArrs = { "签单", "回款" };

	private DictIosPicker mIosPicker;

	private Context mContext;
	private TextView tvBack;
	private TextView tv_today;
	private TextView tv_week;
	private TextView tv_month;
	private TextView tv_quarter;
	private TextView tv_year;

	private BoeryunNoScrollListView lv;

	private LinearLayout llSelectSaleType;
	private TextView tv_saletype_summary_info;

	private ImageView ivSelectUser;
	private ImageView ivSelectDept;
	private ImageView ivSort;

	/** 查询条件 */
	private Demand_销售统计 mDemand;
	private HttpUtils mHttpUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saler_summay);
		initData();
		initViews();
		setOnEvent();

		fectchNetData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case UserBiz.SELECT_SINAL_USER_REQUEST_CODE:
			User user = UserBiz.onActivityUserSelected(requestCode, resultCode,
					data);
			LogUtils.i(TAG, user.Id + "---" + user.UserName);
			tvBack.setText("销售详情(" + user.UserName + ")");

			if (!TextUtils.isEmpty(user.Id)) {
				mDemand.userId = Integer.parseInt(user.Id);
				fectchNetData();
			}
			break;
		}
	}

	private void initData() {
		mContext = SalerSummaryActivity.this;
		mHttpUtils = new HttpUtils();
		mDemand = new Demand_销售统计();
		mDemand.dateType = EnumDateType.今日.getValue();
		mDemand.saleType = EnumSaleType.回款.getValue();
		mIosPicker = new DictIosPicker(mContext);

	}

	private void initViews() {
		tvBack = (TextView) findViewById(R.id.tv_back_salechance_summary_info);
		tv_today = (TextView) findViewById(R.id.tv_today_salechance_summary_info);
		tv_week = (TextView) findViewById(R.id.tv_week_salechance_summary_info);
		tv_month = (TextView) findViewById(R.id.tv_month_salechance_summary_info);
		tv_quarter = (TextView) findViewById(R.id.tv_quarter_salechance_summary_info);
		tv_year = (TextView) findViewById(R.id.tv_year_salechance_summary_info);
		lv = (BoeryunNoScrollListView) findViewById(R.id.lv_summary_info);

		llSelectSaleType = (LinearLayout) findViewById(R.id.ll_select_saletype_summary_info);
		tv_saletype_summary_info = (TextView) findViewById(R.id.tv_saletype_summary_info);

		// 底部过滤按钮
		ivSelectUser = (ImageView) findViewById(R.id.iv_select_user_summary_info);
		ivSelectDept = (ImageView) findViewById(R.id.iv_select_dept_summary_info);
		ivSort = (ImageView) findViewById(R.id.iv_select_sort_summary_info);
	}

	private void setOnEvent() {
		tvBack.setOnClickListener(this);
		tv_today.setOnClickListener(this);
		tv_week.setOnClickListener(this);
		tv_month.setOnClickListener(this);
		tv_quarter.setOnClickListener(this);
		tv_year.setOnClickListener(this);

		llSelectSaleType.setOnClickListener(this);

		ivSelectUser.setOnClickListener(this);
		ivSelectDept.setOnClickListener(this);
		ivSort.setOnClickListener(this);
	}

	/** 获取网络数据 */
	private void fectchNetData() {
		ProgressDialogHelper.show(mContext);
		getPerformanceList();
	}

	/** 业绩排行榜 */
	private void getPerformanceList() {
		final String url = Global.BASE_URL + "SaleSummary/getPerformanceList";
		LogUtils.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo2;
				try {
					mDemand.pageSize = 10;
					mDemand.pageIndex = 0;
					mDemand.Offset = 0;
					jo2 = JsonUtils.initJsonObj(mDemand);
					Log.i("JsonF", jo2.toString());
					String result = mHttpUtils.postSubmit(url, jo2);
					ReturnModel<String> returnModel = JsonUtils
							.pareseResult(result);
					if (returnModel.Status == 1) {
						Message msg = handler.obtainMessage();
						msg.what = SUCCESS_GET_PERFORMANCELIST;
						msg.obj = JsonUtils.ConvertJsonToList(result, 业绩.class);
						handler.sendMessage(msg);
					}
					LogUtils.i(TAG, result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_back_salechance_summary_info:
			finish();
			break;
		case R.id.tv_today_salechance_summary_info:
			setSelectedTab(tv_today, R.drawable.ico_left_press);
			mDemand.dateType = EnumDateType.今日.getValue();
			fectchNetData();
			break;
		case R.id.tv_week_salechance_summary_info:
			setSelectedTab(tv_week, R.drawable.ico_center_press);
			mDemand.dateType = EnumDateType.本周.getValue();
			fectchNetData();
			break;
		case R.id.tv_month_salechance_summary_info:
			setSelectedTab(tv_month, R.drawable.ico_center_press);
			mDemand.dateType = EnumDateType.本月.getValue();
			fectchNetData();
			break;
		case R.id.tv_quarter_salechance_summary_info:
			setSelectedTab(tv_quarter, R.drawable.ico_center_press);
			mDemand.dateType = EnumDateType.本季度.getValue();
			fectchNetData();
			break;
		case R.id.tv_year_salechance_summary_info:
			setSelectedTab(tv_year, R.drawable.ico_right_press);
			mDemand.dateType = EnumDateType.本年.getValue();
			fectchNetData();
			break;
		case R.id.ll_select_saletype_summary_info:
			selectSaleType();
			break;
		/** 底部过滤条件 */
		case R.id.iv_select_user_summary_info: // 选择员工过滤
			UserBiz.selectSinalUser(mContext);
			break;
		case R.id.iv_select_dept_summary_info: // 选择部门过滤

			break;
		case R.id.iv_select_sort_summary_info: // 选择排序过滤

			break;

		default:
			break;
		}
	}

	/***
	 * 选择销售类型 签单/回款
	 */
	private void selectSaleType() {
		mIosPicker.show(R.id.root_salechance_summary_info, filterTypeArrs);
		mIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				switch (index) {
				case 0: // 签单
					mDemand.saleType = EnumSaleType.签单.getValue();
					tv_saletype_summary_info.setText("签单");
					break;
				case 1: // 回款
					mDemand.saleType = EnumSaleType.回款.getValue();
					tv_saletype_summary_info.setText("回款");
					break;
				}
				fectchNetData();
			}
		});
	}

	private void setSelectedTab(TextView tView, int bgResource) {
		initTabColor();
		int textColor = getResources().getColor(R.color.white);
		tView.setTextColor(textColor);
		tView.setBackgroundResource(bgResource);
	}

	/***
	 * 所有Tab初始化
	 */
	private void initTabColor() {
		int textColor = getResources().getColor(R.color.text_info);
		tv_today.setTextColor(textColor);
		tv_week.setTextColor(textColor);
		tv_month.setTextColor(textColor);
		tv_quarter.setTextColor(textColor);
		tv_year.setTextColor(textColor);

		tv_today.setBackgroundResource(R.drawable.ico_left_normal);
		tv_week.setBackgroundResource(R.drawable.ico_center_normal);
		tv_month.setBackgroundResource(R.drawable.ico_center_normal);
		tv_quarter.setBackgroundResource(R.drawable.ico_center_normal);
		tv_year.setBackgroundResource(R.drawable.ico_right_normal);
	}

	private CommanAdapter<业绩> getAdapter(List<业绩> list) {
		return new CommanAdapter<业绩>(list, mContext,
				R.layout.item_performance_sumary_info) {
			@Override
			public void convert(int position, 业绩 item,
					BoeryunViewHolder viewHolder) {
				TextView tvId = viewHolder.getView(R.id.tv_id_performanc_item);
				TextView tvName = viewHolder
						.getView(R.id.tv_name_performanc_item);
				TextView tvNum = viewHolder
						.getView(R.id.tv_num_performanc_item);
				int id = position + 1;
				switch (id) {
				case 1:
					tvId.setBackgroundResource(R.drawable.ico_1st);
					break;
				case 2:
					tvId.setBackgroundResource(R.drawable.ico_circle_red);
					break;
				case 3:
					tvId.setBackgroundResource(R.drawable.ico_circle_bule);
					break;
				// case 4:
				// tvId.setBackgroundResource(R.drawable.ico_circle_yellow);
				// break;
				default:
					tvId.setBackgroundResource(R.drawable.ico_circle_gray);
					break;
				}

				if (id == 1) {
					tvId.setText(" ");
				} else {
					tvId.setText(id + "");
				}
				tvName.setText(item.name + "");
				tvNum.setText(item.sum + "");
			}
		};
	}

	/** 获取排行榜列表 */
	private final int SUCCESS_GET_PERFORMANCELIST = 3;
	/** 获取销售业绩排行榜列表失败 */
	private final int FAILURE_GET_PERFORMANCELIST = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_PERFORMANCELIST:
				ProgressDialogHelper.dismiss();
				List<业绩> list = (List<业绩>) msg.obj;
				if (list.size() > 0) {
					lv.setAdapter(getAdapter(list));
				}
				break;
			}
		};
	};
}
