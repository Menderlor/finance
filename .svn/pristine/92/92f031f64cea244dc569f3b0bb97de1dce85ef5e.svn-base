package com.cedarhd.crm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanCrmAdapter;
import com.cedarhd.constants.enums.EnumSalesChanceFilterType;
import com.cedarhd.control.listview.ListViewHelper2015;
import com.cedarhd.control.listview.ListViewHelper2015.OnFetchServerDataListener;
import com.cedarhd.control.listview.LoadMoreListView;
import com.cedarhd.control.listview.LoadMoreListView.OnLoadMoreListener;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.VmCRMHomeFunnelData;
import com.cedarhd.models.crm.QmCustomer;
import com.cedarhd.models.crm.VmBase;
import com.cedarhd.models.客户;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/** 销售机会漏斗图统计页面 */
public class SaleFunnelActivity extends BaseActivity {
	/***
	 * 选择客户过滤类型
	 */
	String[] filterTypeArrs = { "最近联系", "计划提醒", "关注客户", "最新客户", "一周未联系",
			"半月未联系", "一月未联系", "三月未联系", "半年以上未联系", "N天以上未联系" };

	/** 新建类型 */
	String[] addTypeArry = { "联系记录", "工作计划", "合同", "收款", "报销" };

	private Context mContext;
	private LayoutInflater miInflater;
	private FetchServerDelegate mServerDelegate;
	private DictIosPicker mDictIosPicker;

	private LinearLayout llRootCharts;
	private LinearLayout llRootInfos;
	private LinearLayout llRootSalechances;
	private ImageView ivAdd;
	private TextView tvBack;
	private TextView tvCount;
	private LoadMoreListView lv;
	private CommanCrmAdapter<客户> mAdapter;
	private List<客户> mList;

	private ImageView ivAddClient;
	/** 选择过滤条件，过滤选择员工 */
	private LinearLayout llFilter;
	private TextView tvFilter;

	/** 客户查询条件 */
	private QmCustomer mQmCustomer;

	private QueryDemand mQueryDemand;

	ListViewHelper2015<客户> mListViewHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale_summary);
		init();
		initViews();

		setOnEvent();

		ProgressDialogHelper.show(mContext);
		mServerDelegate.fetchFunnel();
		mServerDelegate.fetchClientCount();
	}

	private void init() {
		mContext = SaleFunnelActivity.this;
		miInflater = LayoutInflater.from(mContext);
		mServerDelegate = new FetchServerDelegate();
		mDictIosPicker = new DictIosPicker(mContext);
		mQmCustomer = new QmCustomer();
		// mQmCustomer.pageIndex = 0;
		mQmCustomer.PageSize = 10;
		mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.最近联系
				.getValue();
		mList = new ArrayList<客户>();
		mQueryDemand = new QueryDemand("最后更新");
	}

	private void initViews() {
		llRootCharts = (LinearLayout) findViewById(R.id.ll_root_charts);
		llRootInfos = (LinearLayout) findViewById(R.id.ll_root_infos);
		llRootSalechances = (LinearLayout) findViewById(R.id.ll_root_salechances);
		ivAdd = (ImageView) findViewById(R.id.iv_add_sale_summary);
		ivAddClient = (ImageView) findViewById(R.id.iv_add_client_sale_summary);
		tvBack = (TextView) findViewById(R.id.tv_back_sale_summary);
		llFilter = (LinearLayout) findViewById(R.id.ll_filter_sale_summary);
		tvFilter = (TextView) findViewById(R.id.tv_filter_sale_summary);
		tvCount = (TextView) findViewById(R.id.tv_clientcount_sale_summary);
		lv = (LoadMoreListView) findViewById(R.id.lv_client_sale_sumary);

		ivAdd.setVisibility(View.GONE);

		mAdapter = getClientAdapter(mList);
		// lv.setAdapter(mAdapter);
		mListViewHelper = new ListViewHelper2015<客户>(mContext,
				"SaleSummary/GetCustomerList", lv, mAdapter, mQmCustomer, null,
				客户.class);
	}

	/** 设置监听事件 */
	private void setOnEvent() {
		ivAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext,
						SaleChanceSummaryInfoActivity.class));
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		/** 新建客户（老客户/新客户） */
		ivAddClient.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext,
						CRMAddSaleChanceActivity.class));
			}
		});

		lv.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				showShortToast("加载更多");
				// mQmCustomer.pageIndex = mList.size();
				// mServerDelegate.fetchClientList(true);
				mListViewHelper.loadMore();
			}
		});

		llFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDictIosPicker.show(R.id.ll_main_sale_summary, filterTypeArrs);
				mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						switch (index) {
						case 0: // 最近联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("最近联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.最近联系
									.getValue();
							break;
						case 1: // 计划提醒
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("计划提醒");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.计划提醒
									.getValue();
							break;
						case 2: // 关注客户
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("关注客户");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.计划提醒
									.getValue();
							break;
						case 3: // 最新客户
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("最新客户");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.最新客户
									.getValue();
							break;
						case 4: // 一周未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("一周未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.一周未联系
									.getValue();
							break;
						case 5: // 半月未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("半月未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.半月未联系
									.getValue();
							break;
						case 6: // 一月未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("一月未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.一月未联系
									.getValue();
							break;
						case 7: // 三月未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("三月未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.三月未联系
									.getValue();
							break;
						case 8: // 半年以上未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("半年以上未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.半年已上未联系
									.getValue();
							break;
						case 9: // N天以上未联系
							LogUtils.i(TAG, filterTypeArrs[index]);
							tvFilter.setText("N天以上未联系");
							mQmCustomer.SalesChanceFilterType = EnumSalesChanceFilterType.N天以上未联系
									.getValue();
							break;
						default:
							break;
						}
						// ProgressDialogHelper.show(mContext);
						// mServerDelegate.fetchClientList(false);
						mListViewHelper.refreshMore(true);
						mServerDelegate.fetchClientCount();
					}
				});
			}
		});

		/** 数据更新完毕 */
		mListViewHelper
				.setFetchServerDataListener(new OnFetchServerDataListener<客户>() {
					@Override
					public void onFetched(VmBase<客户> vmBase, boolean isLoadmore) {
						// int count = mAdapter.getCount();
						// tvCount.setText("" + count);
						if (isLoadmore) {
							lv.loadCompleted();
						}
					}

					@Override
					public void onFailure() {

					}
				});
	}

	/** 初始化漏斗 */
	private void initCharts(final List<VmCRMHomeFunnelData> list) {
		int count = list.size();
		DisplayMetrics displayMetrics = mContext.getResources()
				.getDisplayMetrics();
		int height = (int) ViewHelper.dip2px(mContext, 20); // 漏斗行高度
		int screenWidth = displayMetrics.widthPixels;
		int chartsWidth = (int) ((screenWidth - (int) ViewHelper.dip2px(
				mContext, 16)) * 0.55); // 漏斗图区域的宽度
		int perMargin = (chartsWidth - height) / count / 2;
		int headerLineHeight = (int) ViewHelper.dip2px(mContext, 1);

		llRootCharts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedStatus(list);
			}
		});

		llRootInfos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedStatus(list);
			}
		});

		llRootCharts.removeAllViews();
		llRootInfos.removeAllViews();

		for (int i = 0; i < count; i++) {
			VmCRMHomeFunnelData funnelData = list.get(i);
			LinearLayout.LayoutParams paramsInfos = new LayoutParams(
					LayoutParams.MATCH_PARENT, height);
			// 16777216
			int color = getRandomColor();

			// 右半数量描述
			View view = miInflater.inflate(R.layout.item_chart_info, null);
			paramsInfos.topMargin = headerLineHeight;
			view.setLayoutParams(paramsInfos);
			TextView tvColor = (TextView) view
					.findViewById(R.id.tv_color_charts_item);
			TextView tvName = (TextView) view
					.findViewById(R.id.tv_name_charts_item);
			TextView tvNum = (TextView) view
					.findViewById(R.id.tv_num_charts_item);
			tvColor.setBackgroundColor(color);
			tvName.setText(funnelData.Status);
			tvNum.setText(funnelData.Count + "");
			llRootInfos.addView(view);

			// 左半漏斗部分生成
			LinearLayout.LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, height);
			TextView tv = new TextView(mContext);
			params.leftMargin = perMargin * (i + 1);
			params.rightMargin = perMargin * (i + 1);
			params.topMargin = headerLineHeight;
			tv.setPadding(0, 20, 0, 20);
			tv.setLayoutParams(params);
			tv.setGravity(Gravity.CENTER);
			tv.setBackgroundColor(color);
			llRootCharts.addView(tv);
		}
	}

	/** 获取随机颜色 */
	private int getRandomColor() {
		double colorHex = 0 - Math.random() * (16777216);
		String hex = "#" + Integer.toHexString((int) colorHex);
		Log.i("color", hex);
		int color = Color.parseColor(hex);
		return color;
	}

	/** 客户列表 */
	private CommanCrmAdapter<客户> getClientAdapter(List<客户> list) {
		return new CommanCrmAdapter<客户>(list, mContext,
				R.layout.item_client_sale_summary) {
			@Override
			public void convert(int position, final 客户 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_item_sale_summary);
				TextView tvTime = viewHolder
						.getView(R.id.tv_date_item_sale_summary);
				TextView tvUserName = viewHolder
						.getView(R.id.tv_user_item_sale_summary);
				TextView tvStatus = viewHolder
						.getView(R.id.tv_status_item_sale_summary);
				LinearLayout llAdd = viewHolder
						.getView(R.id.ll_add_item_sale_summary);
				String userName = this.getDictName("业务员", item.业务员);
				String statusName = this.getDictName("状态", item.业务员);
				// String province = getDictName("省", item.省) + " "
				// + getDictName("市", item.市) + " "
				// + getDictName("县", item.县);
				String contactDate = DateDeserializer
						.getFormatTime(item.最后联系时间);
				tvName.setText(StrUtils.pareseNull(item.名称));
				tvTime.setText(contactDate);
				tvUserName.setText(userName);
				tvStatus.setText(statusName);
				llAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						selectCreateTypeDialog(item);
					}
				});
			}
		};
	}

	/** 选择 新建客户 类型，进入对应页面 */
	private void selectCreateTypeDialog(客户 item) {
		mDictIosPicker.show(R.id.ll_main_sale_summary, addTypeArry);
		mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				// "联系记录", "工作计划", "合同", "收款", "报销"
				switch (index) {
				case 0:// 联系记录
					startActivity(new Intent(mContext,
							CrmAddClientContactsActivity.class));
					break;
				case 1:// 工作计划
					startActivity(new Intent(mContext,
							CRMAddWorkplanActivity.class));
					break;
				case 2:// 合同
					startActivity(new Intent(mContext,
							CRMSelectConpactCategoryActivity.class));
					break;
				case 3:// 收款
					startActivity(new Intent(mContext,
							CRMSelectConpactListActivity.class));
					break;
				case 4:// 报销
					startActivity(new Intent(mContext,
							CRMSelectConpactListActivity.class));
					break;
				default:
					break;
				}

			}
		});
	}

	/**
	 * 点击漏斗模块弹出状态选择列表
	 * 
	 * @param list
	 *            状态列表
	 */
	private void selectedStatus(final List<VmCRMHomeFunnelData> list) {
		mDictIosPicker.show(R.id.ll_main_sale_summary, list, "Status");
		mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
			@Override
			public void onSelected(int index) {
				mQmCustomer.Status = list.get(index).Id;
				mListViewHelper.refreshMore(true);
				mServerDelegate.fetchClientCount();
			}
		});
	}

	/**
	 * 访问网络的委托类
	 */
	private class FetchServerDelegate {
		/* 隐藏对话框 */
		private void dissmissDialog() {
			ProgressDialogHelper.dismiss();
		}

		/**
		 * 获取漏斗图字典
		 */
		public void fetchFunnel() {
			String url = Global.BASE_URL + "SaleSummary/GetFunnel";
			StringRequest.getAsyn(url, new StringResponseCallBack() {
				@Override
				public void onResponseCodeErro(String result) {
					dissmissDialog();
				}

				@Override
				public void onResponse(String response) {
					dissmissDialog();
					List<VmCRMHomeFunnelData> list = JsonUtils
							.ConvertJsonToList(response,
									VmCRMHomeFunnelData.class);
					initCharts(list);

				}

				@Override
				public void onFailure(Request request, Exception ex) {
					dissmissDialog();
				}
			});
		}

		/***
		 * 获取客户数量
		 */
		public void fetchClientCount() {
			String url = Global.BASE_URL + "SaleSummary/GetCustomerCount";
			StringRequest.postAsyn(url, mQmCustomer,
					new StringResponseCallBack() {
						@Override
						public void onResponseCodeErro(String result) {

						}

						@Override
						public void onResponse(String response) {
							String count = "0";
							try {
								count = JsonUtils.getStringValue(response,
										"Data");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							tvCount.setText(count + "");
						}

						@Override
						public void onFailure(Request request, Exception ex) {

						}
					});
		}

		/**
		 * 获取客户列表
		 * 
		 * @param isLoadMore
		 *            是否是加载更多
		 */
		public void fetchClientList(final boolean isLoadMore) {

			String url = Global.BASE_URL + "SaleSummary/GetCustomerList";
			StringRequest.postAsyn(url, mQmCustomer,
					new StringResponseCallBack() {
						@Override
						public void onResponseCodeErro(String result) {
							dissmissDialog();
						}

						@Override
						public void onResponse(String response) {
							dissmissDialog();
							lv.loadCompleted();
							try {
								VmBase<客户> vmBase = JsonUtils
										.convertJsonToVmBase(response, 客户.class);
								if (vmBase != null && vmBase.Data != null) {
									mAdapter.addBottom(vmBase.Data, !isLoadMore);
									// tvCount.setText(vmBase.Data.size() + "");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(Request request, Exception ex) {
							dissmissDialog();
						}
					});
		}

	}
}
