package com.cedarhd.rad;

import android.content.Context;
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

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.RadProductBiz;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad订单;
import com.cedarhd.models.rad.Rad订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.LogUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/***
 * 森盟购物车订单列表页面
 * 
 * @author K
 * 
 */
public class RadShopOrderListActivity extends BaseActivity {
	private Context context;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Rad订单> mListViewHelperNet;
	private ImageView ivBack;
	private PullToRefreshListView lv;
	private MyProgressBar pbar;

	private CommanAdapter<Rad订单> adaper;
	private List<Rad订单> mOrders;
	private int deleteAtPos;
	private HttpUtils mhHttpUtils;

	private final int SUCCEED_DELETE_rad_order = 1;
	private final int FAILURE__DELETE_rad_order = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_DELETE_rad_order:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "成功删除订单", Toast.LENGTH_LONG).show();

				if (deleteAtPos < mOrders.size()) {
					mOrders.remove(deleteAtPos);
					adaper.notifyDataSetChanged();
				}
				break;
			case FAILURE__DELETE_rad_order:
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
		setContentView(R.layout.activity_rad_orderlist);
		initViews();
		initData();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		context = RadShopOrderListActivity.this;
		mOrders = new ArrayList<Rad订单>();
		mhHttpUtils = new HttpUtils();
		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "SltRad/getOrderList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 10;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		adaper = getOrderAdapter();
		lv.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<Rad订单>(this, Rad订单.class,
				demand, lv, mOrders, adaper, pbar, queryDemand);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_rad_orderlist);
		lv = (PullToRefreshListView) findViewById(R.id.lv_rad_order_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_rad_orderlist);
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
				// // slt订单 item产品型号 = mProducts.get(pos);
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
	private CommanAdapter<Rad订单> getOrderAdapter() {
		return new CommanAdapter<Rad订单>(mOrders, context,
				R.layout.item_rad_order_list) {
			@Override
			public void convert(final int position, final Rad订单 item,
					BoeryunViewHolder viewHolder) {
				TextView tvClient = viewHolder
						.getView(R.id.tv_client_rad_order_item);
				TextView tvStatus = viewHolder
						.getView(R.id.tv_status_rad_order_item);
				TextView tvTotal = viewHolder
						.getView(R.id.tv_total_rad_order_item);
				ListView lv = viewHolder
						.getView(R.id.lv_product_rad_order_item);

				String orderNo = getResources().getString(R.string.order_no);
				String totalStr = getResources()
						.getString(R.string.order_total);
				tvClient.setText(item.slt客户.名称 + " "
						+ String.format(orderNo, item.编号));
				tvStatus.setText(item.状态名称);
				// 统计总数量
				int sum = 0;
				int count = 0;
				for (int i = 0; i < item.明细列表.size(); i++) {
					count += item.明细列表.get(i).数量;
					sum += item.明细列表.get(i).小计;
				}
				tvTotal.setText(String.format(totalStr, count, item.合计));
				lv.setAdapter(getOrderDetailsAdapter(item.明细列表));

			}
		};
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Rad订单明细> getOrderDetailsAdapter(
			List<Rad订单明细> orderDetails) {
		return new CommanAdapter<Rad订单明细>(orderDetails, context,
				R.layout.item_rad_order_details_list) {
			@Override
			public void convert(int position, Rad订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_rad_orderdetails_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_rad_orderdetails_item);
				TextView tvNum = viewHolder
						.getView(R.id.tv_num_rad_orderdetails_item);
				try {
					String productTypeName = RadProductBiz.getDictValue(
							context, "商品型号", item.商品详细.型号);
					tvName.setText(productTypeName + "");
				} catch (SQLException e) {
					e.printStackTrace();
					LogUtils.e(TAG, e + "");
				} catch (Exception exception) {
					exception.printStackTrace();
					LogUtils.e(TAG, exception + "");
				}
				tvPrice.setText("￥" + item.单价);
				tvNum.setText("x" + item.数量);
			}
		};
	}
}