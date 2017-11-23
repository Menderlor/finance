package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.产品订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车列表
 * 
 * @author K
 * 
 */
public class ShoppingCarListActivity extends BaseActivity {

	private boolean isCheckAll = false;
	private Context context;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<产品订单明细> mListViewHelperNet;
	private ImageView ivBack;
	private PullToRefreshListView lvProduct;
	private MyProgressBar pbar;
	/*** 添加到购物车列表 */
	private TextView tvSubmitOrder;
	/** 查看购物车列表 */
	private RelativeLayout rlShopCar;

	/*** 选择全部 */
	private LinearLayout llCheckAll;
	private ImageView ivCheckAll;
	/** 合计金额 */
	private TextView tvtotal;
	private CommanAdapter<产品订单明细> mAdaper;
	private List<产品订单明细> mDetailList;

	private HttpUtils mhHttpUtils;

	private final int SUCCEED_SAVE_ORDER = 1;
	private final int FAILURE_SAVE_ORDER = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_SAVE_ORDER:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "下单成功", Toast.LENGTH_LONG).show();
				mAdaper.notifyDataSetChanged();
				break;
			case FAILURE_SAVE_ORDER:
				ProgressDialogHelper.dismiss();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopcar);

		initViews();

		initData();
	}

	private void initData() {
		context = ShoppingCarListActivity.this;
		mDetailList = new ArrayList<产品订单明细>();
		mhHttpUtils = new HttpUtils();

		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "SaleStore/getShoppingCarList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 120;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdaper = getProductAdapter();
		lvProduct.setAdapter(mAdaper);
		mListViewHelperNet = new ListViewHelperNet<产品订单明细>(this, 产品订单明细.class,
				demand, lvProduct, mDetailList, mAdaper, pbar, queryDemand);
		mListViewHelperNet.hiddenFootView(); // 隐藏加载更多
		reload();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_shopcar_list);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_shopcar_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_shopcar_list);
		tvSubmitOrder = (TextView) findViewById(R.id.tv_add_shopcar_shopcar_list);
		rlShopCar = (RelativeLayout) findViewById(R.id.rl_shopcar_shopcar_list);
		llCheckAll = (LinearLayout) findViewById(R.id.ll_checkall_shopcar_list);
		ivCheckAll = (ImageView) findViewById(R.id.iv_checked_shopcar_list);
		tvtotal = (TextView) findViewById(R.id.tv_total_shopcar_list);
		setOnTouchListener();
	}

	private void setOnTouchListener() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lvProduct.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				lvProduct.onRefreshComplete();
			}
		});

		lvProduct.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// int pos = position - 1;
				// if (pos < mProducts.size()) {
				// 产品订单明细 item产品订单明细 = mProducts.get(pos);
				// mProducts.get(pos).setCheck(!item产品订单明细.isCheck());
				// Log.i("item", position + "---" + item产品订单明细.名称);
				// }
				// adaper.notifyDataSetChanged();
			}
		});

		tvSubmitOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<产品订单明细> list = getOrderDetails();
				if (list.size() == 0) {
					Toast.makeText(context, "还没有选择产品哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					ProgressDialogHelper.show(context, "提交订单..");
					submitOrder(list);
				}
			}
		});

		rlShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		/** 全选 */
		llCheckAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isCheckAll = !isCheckAll;
				if (isCheckAll) {
					ivCheckAll.setImageResource(R.drawable.ico_select_shopcar);
				} else {
					ivCheckAll
							.setImageResource(R.drawable.ico_unselect_shopcar);
				}
				for (int i = 0; i < mDetailList.size(); i++) {
					mDetailList.get(i).setCheced(isCheckAll);
				}
				initTotal();
				mAdaper.notifyDataSetChanged();
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
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<产品订单明细> getProductAdapter() {
		return new CommanAdapter<产品订单明细>(mDetailList, context,
				R.layout.item_shopcar_list) {
			@Override
			public void convert(final int position, final 产品订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_name_shopcar_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_shopcar_item);
				LinearLayout llChecked = viewHolder
						.getView(R.id.ll_checked_shopcar_item);
				ImageView ivChecked = viewHolder
						.getView(R.id.iv_checked_shopcar_item);
				BoeryunSelectCountView selectNumView = viewHolder
						.getView(R.id.select_countview_shopcar_item);

				tvName.setText(item.产品名称 + "");
				tvPrice.setText("￥" + item.单价);
				selectNumView.setNum(item.数量);
				if (item.isCheced()) {
					ivChecked.setImageResource(R.drawable.ico_select_shopcar);
				} else {
					ivChecked.setImageResource(R.drawable.ico_unselect_shopcar);
				}

				llChecked.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean isChecked = !item.isCheced();
						mDetailList.get(position).setCheced(isChecked);
						mAdaper.notifyDataSetChanged();

						initTotal();
					}
				});

				selectNumView.setOnNumChangedeListener(new OnNumChanged() {
					@Override
					public void onchange(int value) {
						mDetailList.get(position).数量 = value;
						if (mDetailList.get(position).isCheced()) {
							initTotal();
						}
					}
				});
			}
		};
	}

	/***
	 * 保存到购物车
	 * 
	 * @param list
	 */
	private void submitOrder(final List<产品订单明细> list) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// String url = Global.BASE_URL + "SaleStore/submitOrder";
				String url = Global.BASE_URL + "SaleStore/submitJYOrder";
				try {
					JSONObject jo = new JSONObject();
					jo.put("content", JsonUtils.initJsonString(list));
					String result = mhHttpUtils.postSubmit(url, jo);
					Log.i("succ", result);
					mDetailList.removeAll(list);
					handler.sendEmptyMessage(SUCCEED_SAVE_ORDER);

				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.e("erro", e.toString() + "");
					handler.sendEmptyMessage(FAILURE_SAVE_ORDER);
				}
			}
		}).start();
	}

	/***
	 * 获取选中订单明细列表
	 * 
	 * @return
	 */
	private List<产品订单明细> getOrderDetails() {
		List<产品订单明细> detailList = new ArrayList<产品订单明细>();
		for (int i = 0; i < mDetailList.size(); i++) {
			产品订单明细 detail = mDetailList.get(i);
			detail.小计 = detail.单价 * detail.数量;
			if (detail != null && detail.isCheced()) {
				detailList.add(detail);
			}
		}
		return detailList;
	}

	/***
	 * 计算订单合计金额
	 * 
	 * @return
	 */
	private double getOrderTotal(List<产品订单明细> detailList) {
		double total = 0;
		for (int i = 0; i < detailList.size(); i++) {
			产品订单明细 detail = detailList.get(i);
			if (detail != null && detail.isCheced()) {
				total += detail.单价 * detail.数量;
			}
		}
		return total;
	}

	/**
	 * 初始化合计金额
	 */
	private void initTotal() {
		List<产品订单明细> detailList = getOrderDetails();
		double total = getOrderTotal(detailList);
		tvtotal.setText("合计：￥" + total);
	}
}
