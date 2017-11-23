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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cedarhd.ClientConstactListActivity;
import com.cedarhd.ClientListActivity;
import com.cedarhd.ClientNewActivity;
import com.cedarhd.R;
import com.cedarhd.SaleChanceListActivity;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.UserBiz;
import com.cedarhd.biz.VmFormBiz;
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
import com.cedarhd.models.销售统计;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 排行榜新版销售机会 统计详情，包括关联的客户、线索、合同、收款等
 * 
 * @author K at 2015/09/09 18:56
 * */
public class SaleChanceSummaryInfoActivity extends BaseActivity implements
		OnClickListener {

	/***
	 * 选择客户过滤类型
	 */
	String[] filterTypeArrs = { "签单", "回款" };

	/***
	 * 是否关联合同，直接新建或关联合同
	 */
	String[] mRelateConpactArrs = { "直接新建", "关联合同" };

	/***
	 * 新建销售机会类型，区分新客户还是老客户
	 */
	String[] mSaleChanceTypeArrs = { "新客户", "已有客户" };

	private DictIosPicker mIosPicker;

	private Context mContext;
	private TextView tvBack;
	private TextView tv_today;
	private TextView tv_week;
	private TextView tv_month;
	private TextView tv_quarter;
	private TextView tv_year;
	private TextView tvProgress;
	private TextView tvProgressMax;
	private TextView tvPercentage;

	/** 数量显示模块 */
	private TextView tvXiansuo;
	private TextView tvKehu;
	private TextView tvHetong;
	private TextView tvShoukuan;
	private TextView tvBaoxiao;
	private TextView tvXiaoshoujihui;
	private TextView tvLianxijilu;

	/** 一系列新建按钮 */
	private ImageView ivXiansuo;
	private ImageView ivAddKehu;
	private ImageView ivAddHetong;
	private ImageView ivAddShoukuan;
	private ImageView ivAddBaoxiao;
	private ImageView ivAddXiaoshoujihui;
	private ImageView ivAddLianxijilu;

	private ProgressBar pBar;
	private BoeryunNoScrollListView lv;

	private LinearLayout llSelectSaleType;
	private TextView tv_saletype_summary_info;
	private TextView tv_more_summary_info;

	private ImageView ivSelectUser;
	private ImageView ivSelectDept;
	private ImageView ivSort;

	/** 查询条件 */
	private Demand_销售统计 mDemand;
	private HttpUtils mHttpUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salechance_summary_info);
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
		mContext = SaleChanceSummaryInfoActivity.this;
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
		tvProgress = (TextView) findViewById(R.id.tv_progress_salechance_summary_info);
		tvProgressMax = (TextView) findViewById(R.id.tv_max_salechance_summary_info);
		tvPercentage = (TextView) findViewById(R.id.tv_percentage_salechance_summary_info);
		pBar = (ProgressBar) findViewById(R.id.pbar_salechance_summary_info);
		lv = (BoeryunNoScrollListView) findViewById(R.id.lv_summary_info);

		llSelectSaleType = (LinearLayout) findViewById(R.id.ll_select_saletype_summary_info);
		tv_saletype_summary_info = (TextView) findViewById(R.id.tv_saletype_summary_info);
		tv_more_summary_info = (TextView) findViewById(R.id.tv_more_summary_info);

		tvXiansuo = (TextView) findViewById(R.id.tv_num_xiansuo_sumaryinfo);
		tvKehu = (TextView) findViewById(R.id.tv_num_kehu_sumaryinfo);
		tvHetong = (TextView) findViewById(R.id.tv_num_hetong_sumaryinfo);
		tvShoukuan = (TextView) findViewById(R.id.tv_num_shoukuan_sumaryinfo);
		tvBaoxiao = (TextView) findViewById(R.id.tv_num_baoxiao_sumaryinfo);
		tvXiaoshoujihui = (TextView) findViewById(R.id.tv_num_jihui_sumaryinfo);
		tvLianxijilu = (TextView) findViewById(R.id.tv_num_lianxi_sumaryinfo);

		ivXiansuo = (ImageView) findViewById(R.id.iv_add_xiansuo_sumaryinfo);
		ivAddKehu = (ImageView) findViewById(R.id.iv_add_kehu_sumaryinfo);
		ivAddHetong = (ImageView) findViewById(R.id.iv_add_hetong_sumaryinfo);
		ivAddShoukuan = (ImageView) findViewById(R.id.iv_add_shoukuan_sumaryinfo);
		ivAddBaoxiao = (ImageView) findViewById(R.id.iv_add_baoxiao_sumaryinfo);
		ivAddXiaoshoujihui = (ImageView) findViewById(R.id.iv_add_jihui_sumaryinfo);
		ivAddLianxijilu = (ImageView) findViewById(R.id.iv_add_lianxi_sumaryinfo);

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
		tv_more_summary_info.setOnClickListener(this);

		ivXiansuo.setOnClickListener(this);
		ivAddKehu.setOnClickListener(this);
		ivAddHetong.setOnClickListener(this);
		ivAddShoukuan.setOnClickListener(this);
		ivAddBaoxiao.setOnClickListener(this);
		ivAddXiaoshoujihui.setOnClickListener(this);
		ivAddLianxijilu.setOnClickListener(this);

		/** 数量模块 */
		tvXiansuo.setOnClickListener(this);
		tvKehu.setOnClickListener(this);
		tvHetong.setOnClickListener(this);
		tvShoukuan.setOnClickListener(this);
		tvBaoxiao.setOnClickListener(this);
		tvXiaoshoujihui.setOnClickListener(this);
		tvLianxijilu.setOnClickListener(this);

		ivSelectUser.setOnClickListener(this);
		ivSelectDept.setOnClickListener(this);
		ivSort.setOnClickListener(this);
	}

	/** 获取网络数据 */
	private void fectchNetData() {
		ProgressDialogHelper.show(mContext);
		getTarget();
		getPerformanceList();
		getSummaryList();
	}

	private void getTarget() {
		final String url = Global.BASE_URL + "SaleSummary/getTarget";
		LogUtils.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo2;
				try {
					jo2 = JsonUtils.initJsonObj(mDemand);
					String result = mHttpUtils.postSubmit(url, jo2);
					LogUtils.i(TAG, result);
					ReturnModel<String> returnModel = JsonUtils
							.pareseResult(result);
					if (returnModel.Status == 1) {
						Message msg = handler.obtainMessage();
						msg.what = SUCCESS_GET_TARGET;
						msg.obj = JsonUtils.pareseData(result);
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		StringRequest.postAsyn(url, mDemand, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onResponse(String response) {
				LogUtils.i("saleRes", response);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				// TODO Auto-generated method stub

			}
		});
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
					mDemand.pageSize = 1;
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

	/** 销售统计 */
	private void getSummaryList() {
		final String url = Global.BASE_URL + "SaleSummary/getSummaryList";
		LogUtils.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject jo2;
				try {
					jo2 = JsonUtils.initJsonObj(mDemand);
					String result = mHttpUtils.postSubmit(url, jo2);
					ReturnModel<String> returnModel = JsonUtils
							.pareseResult(result);
					LogUtils.i(TAG, result);
					if (returnModel.Status == 1) {
						Message msg = handler.obtainMessage();
						msg.what = SUCCESS_GET_SUMMARY_LIST;
						List<销售统计> list = JsonUtils.ConvertJsonToList(result,
								销售统计.class);
						if (list.size() > 0) {
							msg.obj = list.get(0);
							handler.sendMessage(msg);
						}
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
		case R.id.tv_more_summary_info:
			startActivity(new Intent(mContext, SalerSummaryActivity.class));
			break;
		case R.id.iv_add_xiansuo_sumaryinfo:// 添加线索
			startActivity(new Intent(mContext, CRMAddXiansuoActivity.class));
			break;
		case R.id.iv_add_kehu_sumaryinfo:// 添加客户
			startActivity(new Intent(mContext, ClientNewActivity.class));
			break;
		case R.id.iv_add_hetong_sumaryinfo:// 添加合同
			startActivity(new Intent(mContext,
					CRMSelectConpactCategoryActivity.class));
			break;
		case R.id.iv_add_shoukuan_sumaryinfo:// 添加收款

			addShoukuan();
			break;
		case R.id.iv_add_baoxiao_sumaryinfo:// 添加报销
			addBaoxiao();
			break;
		case R.id.iv_add_jihui_sumaryinfo:// 添加销售机会
			mIosPicker.show(R.id.root_salechance_summary_info,
					mSaleChanceTypeArrs);
			mIosPicker.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(int index) {
					Intent intentAddSalechance = new Intent(mContext,
							CRMAddSaleChanceActivity.class);
					intentAddSalechance.putExtra(
							CRMAddSaleChanceActivity.TYPE_CLIENT, index);
					startActivity(intentAddSalechance);
				}
			});

			break;
		case R.id.iv_add_lianxi_sumaryinfo:// 添加联系记录
			startActivity(new Intent(mContext,
					CrmAddClientContactsActivity.class));
			break;

		/** 点击数字查看列表 */
		case R.id.tv_num_xiansuo_sumaryinfo: // 线索列表
			startActivity(new Intent(mContext, CRMClewListActivity.class));
			break;
		case R.id.tv_num_kehu_sumaryinfo: // 客户列表
			startActivity(new Intent(mContext, ClientListActivity.class));
			break;
		case R.id.tv_num_hetong_sumaryinfo: // 合同列表
			startActivity(new Intent(mContext,
					CRMSelectConpactListActivity.class));
			break;
		case R.id.tv_num_shoukuan_sumaryinfo: // 收款列表
			startActivity(new Intent(mContext, CRMReceiptListActivity.class));
			break;
		case R.id.tv_num_baoxiao_sumaryinfo: // 报销单列表
			startActivity(new Intent(mContext, CRMExpenseListActivity.class));
			break;
		case R.id.tv_num_jihui_sumaryinfo: // 销售机会列表
			startActivity(new Intent(mContext, SaleChanceListActivity.class));
			break;
		case R.id.tv_num_lianxi_sumaryinfo: // 联系记录列表
			startActivity(new Intent(mContext, ClientConstactListActivity.class));
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

	/** 添加收款单 */
	private void addShoukuan() {
		mIosPicker.show(R.id.root_salechance_summary_info, mRelateConpactArrs);
		mIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				switch (index) {
				case 0: // 直接新建收款单
					VmFormBiz.startNewVmFromActivity(mContext, 217, "收款单");
					break;
				case 1: // 关联合同
					Intent intent = new Intent(mContext,
							CRMSelectConpactListActivity.class);
					intent.putExtra(
							CRMSelectConpactListActivity.IS_SELECT_MODE, true);
					intent.putExtra(CRMSelectConpactListActivity.TYPE_ID, 217);
					intent.putExtra(CRMSelectConpactListActivity.TYPE_NAME,
							"收款单");
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		});
	}

	/** 添加报销单 */
	private void addBaoxiao() {
		mIosPicker.show(R.id.root_salechance_summary_info, mRelateConpactArrs);
		mIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				switch (index) {
				case 0: // 直接新建报销单
					VmFormBiz.startNewVmFromActivity(mContext, 94, "报销申请单");
					break;
				case 1: // 关联合同
					Intent intent = new Intent(mContext,
							CRMSelectConpactListActivity.class);
					intent.putExtra(
							CRMSelectConpactListActivity.IS_SELECT_MODE, true);
					intent.putExtra(CRMSelectConpactListActivity.TYPE_ID, 94);
					intent.putExtra(CRMSelectConpactListActivity.TYPE_NAME,
							"报销申请单");
					startActivity(intent);
					break;
				default:
					break;
				}
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
					// tvId.setVisibility(View.GONE);
					break;
				case 2:
					tvId.setBackgroundResource(R.drawable.ico_circle_bule);
					break;
				case 3:
					tvId.setBackgroundResource(R.drawable.ico_circle_yellow);
					break;
				default:
					tvId.setBackgroundResource(R.drawable.ico_circle_gray);
					break;
				}
				tvId.setText(" ");
				// tvId.setText((position + 1) + "");
				tvName.setText(item.name + "");
				tvNum.setText(item.sum + "");
			}
		};
	}

	private final int SUCCESS_GET_TARGET = 1;
	private final int FAILURE_GET_TARGET = 2;

	/** 获取排行榜列表 */
	private final int SUCCESS_GET_PERFORMANCELIST = 3;
	/** 获取排行榜列表失败 */
	private final int FAILURE_GET_PERFORMANCELIST = 4;

	/** 获取排行榜统计 */
	private final int SUCCESS_GET_SUMMARY_LIST = 5;
	/** 获取排销售统计失败 */
	private final int FAILURE_GET_SUMMARY_LIST = 6;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_TARGET:
				ProgressDialogHelper.dismiss();
				String result = (String) msg.obj;
				LogUtils.d(TAG, result);
				if (result.length() > 2) {
					result = result.substring(1, result.length() - 1);
				}
				try {
					JSONObject jo = new JSONObject(result);
					double target = jo.getDouble("target");
					double current = jo.getDouble("current");
					double percent = jo.getDouble("percent");
					tvProgressMax.setText("/" + target);
					tvProgress.setText("" + current);
					tvPercentage.setText(percent + "%");
					pBar.setMax((int) target);
					pBar.setProgress((int) current);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case SUCCESS_GET_PERFORMANCELIST:
				ProgressDialogHelper.dismiss();
				List<业绩> list = (List<业绩>) msg.obj;
				if (list.size() > 0) {
					lv.setAdapter(getAdapter(list));
				}
				break;
			case SUCCESS_GET_SUMMARY_LIST:
				ProgressDialogHelper.dismiss();
				销售统计 summary = (销售统计) msg.obj;
				tvXiansuo.setText("" + summary.线索);
				tvKehu.setText("" + summary.客户);
				tvHetong.setText("" + summary.合同);
				tvShoukuan.setText("" + summary.收款);
				tvBaoxiao.setText("" + summary.报销);
				tvXiaoshoujihui.setText("" + summary.销售机会);
				tvLianxijilu.setText("" + summary.联系记录);
				break;
			}
		};
	};
}
