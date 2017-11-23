package com.cedarhd.slt;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.constants.enums.EnumSltOrderStatus;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSearchView;
import com.cedarhd.control.BoeryunSearchView.OnSearchedListener;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
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
 * 森盟订单列表页面
 * 
 * @author K
 * 
 */
public class SltShopOrderListActivity extends BaseActivity {
	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Slt订单> mListViewHelperNet;

	private DictIosPickerBottomDialog mDictIosPicker;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;
	private BoeryunSearchView searchView;

	private CommanAdapter<Slt订单> adaper;
	private List<Slt订单> mOrders;
	private int deleteAtPos;

	private final int SUCCEED_DELETE_slt_order = 1;
	private final int FAILURE__DELETE_slt_order = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_DELETE_slt_order:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "成功删除订单", Toast.LENGTH_LONG).show();

				if (deleteAtPos < mOrders.size()) {
					mOrders.remove(deleteAtPos);
					adaper.notifyDataSetChanged();
				}
				break;
			case FAILURE__DELETE_slt_order:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "删除订单失败", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

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
		mContext = SltShopOrderListActivity.this;
		mDictIosPicker = new DictIosPickerBottomDialog(mContext);
		mOrders = new ArrayList<Slt订单>();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/GetMyOrderList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		adaper = getOrderAdapter();
		lv.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<Slt订单>(this, Slt订单.class,
				demand, lv, mOrders, adaper, pbar, queryDemand);
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_order_list);
		lv = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);
		searchView = (BoeryunSearchView) findViewById(R.id.searchview_slt_order_list);
		setOnTouchListener();
	}

	private void setOnTouchListener() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClickSaveOrAdd() {
				// TODO Auto-generated method stub

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
				showShortToast("setOnItemClickListener" + position);
			}
		});

		// lv.setOnItemLongClickListener(new OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// showShortToast("setOnItemLongClickListener" + position);
		// int pos = position - 1;
		// if (pos < mOrders.size()) {
		// setOnItemLongClickListener(pos);
		// }
		// return false;
		// }
		// });

		searchView.setOnSearchedListener(new OnSearchedListener() {
			@Override
			public void OnSearched(String str) {
				demand.附加条件 = "终端客户名称 like '%" + str + "%' or 终端客户地址 like '%"
						+ str + "%' or 终端客户联系方式 like '%" + str + "%'";
				reload();
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
				LinearLayout root = viewHolder
						.getView(R.id.root_slt_order_list_item);
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_slt_order_item);
				TextView tvStatus = viewHolder
						.getView(R.id.tv_status_slt_order_item);
				TextView tvTotal = viewHolder
						.getView(R.id.tv_total_slt_order_item);
				ImageView ivDelete = viewHolder
						.getView(R.id.iv_approval_slt_order_item);
				ListView lv = viewHolder
						.getView(R.id.lv_product_slt_order_item);
				viewHolder.setTextValue(R.id.tv_orderno_slt_order_item,
						StrUtils.pareseNull(item.单号));
				viewHolder.setImageResoure(R.id.iv_approval_slt_order_item,
						R.drawable.ico_delete);

				String totalStr = getResources()
						.getString(R.string.order_total);
				tvStatus.setVisibility(View.VISIBLE);
				tvClient.setText("收货人：" + item.终端客户名称 + " \n联系方式："
						+ item.终端客户联系方式 + " \n收货地址：" + item.终端客户地址);
				tvStatus.setText(item.订单状态名称 + "");
				// 统计总数量
				int sum = 0;
				int count = 0;
				for (int i = 0; i < item.订单明细列表.size(); i++) {
					count += item.订单明细列表.get(i).数量;
					sum += item.订单明细列表.get(i).金额小计;
				}
				tvTotal.setText(String.format(totalStr, count, item.金额合计));
				lv.setAdapter(getOrderDetailsAdapter(item.订单明细列表));

				if (item.订单状态 <= EnumSltOrderStatus.店员下单.getValue()) {
					ivDelete.setVisibility(View.VISIBLE);
					ivDelete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							setOnItemLongClickListener(item);
						}
					});
				} else {
					ivDelete.setVisibility(View.GONE);
				}
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

				tvName.setText(StrUtils.pareseNull(item.型号名称) + "\n"
						+ StrUtils.pareseNull(item.图号));
				tvPrice.setText("￥" + item.单价);
				tvNum.setText("x " + item.片数 + "(片)");
				tvZuNum.setText("x " + item.数量 + "(组)");
			}
		};
	}

	private void removeOrder(int orderId) {
		String url = Global.BASE_URL + "slt/removeOrder/" + orderId;
		StringRequest.getAsyn(url, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("删除失败");
			}

			@Override
			public void onResponse(String response) {
				showShortToast("删除成功");
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast(INFO_ERRO_SERVER);
			}
		});
	}

	private void setOnItemLongClickListener(final Slt订单 order) {
		if (order.订单状态 <= EnumSltOrderStatus.店员下单.getValue()) {
			mDictIosPicker.show("删除订单" + order.单号);
			mDictIosPicker.setOnSelectedListener(new OnSelectedListener() {
				@Override
				public void onSelected(int index) {
					if (index == 0) {
						adaper.remove(order);
						removeOrder(order.编号);
					}
				}
			});
		} else {
			showShortToast("经销商已审核，不能删除");
		}
	}
}