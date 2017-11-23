package com.cedarhd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DateDeserializer;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.产品订单;
import com.cedarhd.models.产品订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 购物车订单列表页面
 * 
 * @author K
 * 
 */
public class ShopOrderListActivity extends BaseActivity {
	private final String TAG = "ShopOrderListActivity";
	private Context context;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<产品订单> mListViewHelperNet;
	private ImageView ivBack;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private CommanAdapter<产品订单> adaper;
	private List<产品订单> mOrders;
	private int deleteAtPos;
	private HttpUtils mhHttpUtils;

	private final int SUCCEED_DELETE_ORDER = 1;
	private final int FAILURE__DELETE_ORDER = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_DELETE_ORDER:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "成功删除订单", Toast.LENGTH_LONG).show();

				if (deleteAtPos < mOrders.size()) {
					mOrders.remove(deleteAtPos);
					adaper.notifyDataSetChanged();
				}
				break;
			case FAILURE__DELETE_ORDER:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "删除订单失败", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_orderlist);
		initViews();
		initData();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		context = ShopOrderListActivity.this;
		mOrders = new ArrayList<产品订单>();
		mhHttpUtils = new HttpUtils();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "SaleStore/getJYOrderList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "最后更新";
		queryDemand.sortFildName = "最后更新";
		adaper = getOrderAdapter();
		lv.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<产品订单>(this, 产品订单.class,
				demand, lv, mOrders, adaper, pbar, queryDemand);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_orderlist);
		lv = (PullToRefreshListView) findViewById(R.id.lv_order_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_orderlist);
		setOnTouchListener();
	}

	private void setOnTouchListener() {

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
				// // 产品订单 item产品型号 = mProducts.get(pos);
				// // mProducts.get(pos).setCheck(!item产品型号.isCheck());
				// // Log.i("item", position + "---" + item产品型号.名称);
				// }
				// adaper.notifyDataSetChanged();
			}
		});
	}

	private void reload() {
		boolean isConnectedInternet = HttpUtils.IsHaveInternet(context);
		if (!isConnectedInternet) {
			Toast.makeText(context, "需要连接到3G或者wifi因特网才能获取最新信息！",
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
	private CommanAdapter<产品订单> getOrderAdapter() {
		return new CommanAdapter<产品订单>(mOrders, context,
				R.layout.item_shoporder_list) {
			@Override
			public void convert(final int position, final 产品订单 item,
					BoeryunViewHolder viewHolder) {
				TextView tvId = viewHolder.getView(R.id.tv_id_order_item);
				TextView tvTime = viewHolder.getView(R.id.tv_time_order_item);
				TextView tvTotal = viewHolder.getView(R.id.tv_total_order_item);
				ImageView ivDelete = viewHolder
						.getView(R.id.iv_delete_order_item);
				ListView lv = viewHolder.getView(R.id.lv_product_order_item);

				// tvId.setText(String.format("订单号：%5$d", item.编号));

				String orderNo = getResources().getString(R.string.order_no);
				String totalStr = getResources()
						.getString(R.string.order_total);
				tvId.setText(String.format(orderNo, item.编号));
				tvTime.setText(DateDeserializer.getFormatTime(ViewHelper
						.formatDateToStr(item.制单时间)));
				// 统计总数量
				int sum = 0;
				for (int i = 0; i < item.orderDetails.size(); i++) {
					sum += item.orderDetails.get(i).数量;
				}
				tvTotal.setText(String.format(totalStr, sum, item.合计));
				lv.setAdapter(getOrderDetailsAdapter(item.orderDetails));

				ivDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(context)
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												deleteAtPos = position;
												ProgressDialogHelper
														.show(context);
												deleteOrder(item.编号);
												dialog.dismiss();
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).setMessage("确认删除此订单？").create()
								.show();

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
	private CommanAdapter<产品订单明细> getOrderDetailsAdapter(
			List<产品订单明细> orderDetails) {
		return new CommanAdapter<产品订单明细>(orderDetails, context,
				R.layout.item_order_details_list) {
			@Override
			public void convert(int position, 产品订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_orderdetails_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_orderdetails_item);
				TextView tvNum = viewHolder
						.getView(R.id.tv_num_orderdetails_item);
				tvName.setText(item.产品名称 + "");
				tvPrice.setText("￥" + item.单价);
				tvNum.setText("x" + item.数量);
			}
		};
	}

	private void deleteOrder(int orderId) {
		final String url = Global.BASE_URL + "SaleStore/deleteOrder/" + orderId;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String jsonString = mhHttpUtils.httpGet(url);
				String status = JsonUtils.parseStatus(jsonString);
				if ("0".equals(status)) {
					handler.sendEmptyMessage(FAILURE__DELETE_ORDER);
				} else {
					handler.sendEmptyMessage(SUCCEED_DELETE_ORDER);
				}
			}
		}).start();
	}
}