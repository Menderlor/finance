package com.cedarhd.slt;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictionaryHelper;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.slt.Slt订单;
import com.cedarhd.models.slt.Slt订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/***
 * 森盟待我审批订单列表页面
 * 
 * @author K
 * 
 */
public class SltApproveOrderListActivity extends BaseActivity {
	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Slt订单> mListViewHelperNet;

	private DictionaryHelper mDictionaryHelper;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private CommanAdapter<Slt订单> mOrderAdaper;
	private List<Slt订单> mOrders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slt_orderlist);
		initViews();
		initData();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		mContext = SltApproveOrderListActivity.this;
		mOrders = new ArrayList<Slt订单>();
		queryDemand = new QueryDemand();
		mDictionaryHelper = new DictionaryHelper(mContext);
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/GetMyApproveOrderList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mOrderAdaper = getOrderAdapter();
		lv.setAdapter(mOrderAdaper);
		mListViewHelperNet = new ListViewHelperNet<Slt订单>(this, Slt订单.class,
				demand, lv, mOrders, mOrderAdaper, pbar, queryDemand);
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_order_list);
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);

		headerView.setTitle("待我审批订单");
		setOnTouchListener();
	}

	private void setOnTouchListener() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {

			}

			@Override
			public void onClickFilter() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onClickBack() {
				finish();
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				lv.onRefreshComplete();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// int pos = position - 1;
				// if (pos < mProducts.size()) {
				// // slt订单 item产品型号 = mProducts.get(pos);
				// // mProducts.get(pos).setCheck(!item产品型号.isCheck());
				// // Log.i("item", position + "---" + item产品型号.名称);
				// }
				// adaper.notifyDataSetChanged();
			}
		});
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(mContext);
		if (!isConnectedInternet) {
			Toast.makeText(mContext, "需要连接到3G或者wifi因特网才能获取最新信息！",
					Toast.LENGTH_LONG).show();
			// mListViewHelperKjx.loadLocalData();
		} else {
			mOrders.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取订单列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Slt订单> getOrderAdapter() {
		return new CommanAdapter<Slt订单>(mOrders, mContext,
				R.layout.item_slt_order_list) {
			@Override
			public void convert(final int position, final Slt订单 item,
					BoeryunViewHolder viewHolder) {
				ImageView ivApprove = viewHolder
						.getView(R.id.iv_approval_slt_order_item);
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_slt_order_item);
				TextView tvStatus = viewHolder
						.getView(R.id.tv_status_slt_order_item);
				TextView tvUser = viewHolder
						.getView(R.id.tv_user_slt_order_item);
				TextView tvTotal = viewHolder
						.getView(R.id.tv_total_slt_order_item);
				ListView lv = viewHolder
						.getView(R.id.lv_product_slt_order_item);
				viewHolder.setTextValue(R.id.tv_orderno_slt_order_item,
						StrUtils.pareseNull(item.单号));

				String totalStr = getResources()
						.getString(R.string.order_total);

				tvClient.setText("收货人：" + item.终端客户名称 + " \n联系方式："
						+ item.终端客户联系方式 + " \n收货地址：" + item.终端客户地址);
				tvStatus.setText(item.订单状态名称 + "");

				tvUser.setVisibility(View.VISIBLE);
				tvUser.setText("店员:"
						+ mDictionaryHelper.getUserNameById(item.终端员工));
				// 统计总数量
				int sum = 0;
				int count = 0;
				for (int i = 0; i < item.订单明细列表.size(); i++) {
					count += item.订单明细列表.get(i).数量;
					sum += item.订单明细列表.get(i).金额小计;
				}
				tvTotal.setText(String.format(totalStr, count, item.金额合计));
				lv.setAdapter(getOrderDetailsAdapter(item.订单明细列表));
				ivApprove.setVisibility(View.VISIBLE);
				ivApprove.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						approveOrder(item);
					}
				});
			}
		};
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Slt订单明细> getOrderDetailsAdapter(
			List<Slt订单明细> orderDetails) {
		return new CommanAdapter<Slt订单明细>(orderDetails, mContext,
				R.layout.item_slt_order_details_list) {
			@Override
			public void convert(int position, Slt订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_slt_orderdetails_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_slt_orderdetails_item);
				TextView tvNum = viewHolder
						.getView(R.id.tv_num_slt_orderdetails_item);
				TextView tvZuNum = viewHolder
						.getView(R.id.tv_zu_num_slt_orderdetails_item);
				ImageView ivProduct = viewHolder
						.getView(R.id.iv_product_slt_order_item);

				tvName.setText(item.型号名称 + "\n" + item.图号);
				tvPrice.setText("￥" + item.单价);
				tvNum.setText("x " + item.片数 + "(片)");
				tvZuNum.setText("x " + item.数量 + "(组)");
			}
		};
	}

	/**
	 * 审批订单
	 * 
	 * @param orderId
	 *            订单编号
	 */
	private void approveOrder(final Slt订单 order) {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "slt/approvalOrderById/" + order.编号;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("审批成功");
				mOrderAdaper.remove(order);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast(INFO_ERRO_SERVER);
			}
		});
	}
}