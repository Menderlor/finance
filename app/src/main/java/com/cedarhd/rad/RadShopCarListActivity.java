package com.cedarhd.rad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.RadProductBiz;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.models.rad.Rad订单;
import com.cedarhd.models.rad.Rad订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.widget.RadAddClientDialog;
import com.cedarhd.widget.RadAddClientDialog.OnSaveSuccessedListener;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 森盟购物车列表
 * 
 * @author K
 * 
 */
public class RadShopCarListActivity extends BaseActivity {

	private boolean isCheckAll = false;
	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Rad订单明细> mListViewHelperNet;
	private RadAddClientDialog mAddClientDialog;
	private DictIosPicker mDictIosPicker;
	private Rad订单 mOrder;

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
	private CommanAdapter<Rad订单明细> mAdaper;
	private List<Rad订单明细> mDetailList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_shopcarlist);

		initViews();
		initData();
	}

	@Override
	protected void onStart() {
		super.onStart();
		setOnTouchListener();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ClientBiz.SELECT_CLIENT_CODE:
				Client client = ClientBiz
						.onActivityGetClient(requestCode, data);
				if (client != null && client.getId() != 0) {
					showShortToast("选中客户：" + client.getCustomerName() + "-"
							+ client.getPhone());
					mOrder.客户 = client.getId();
					submitOrder();
				}
				break;
			default:
				break;
			}
		}
	}

	private void initData() {
		mContext = RadShopCarListActivity.this;
		mDetailList = new ArrayList<Rad订单明细>();
		mDictIosPicker = new DictIosPicker(mContext);
		mAddClientDialog = new RadAddClientDialog(mContext);
		mOrder = new Rad订单();

		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "SltRad/getShoppingCarDetails";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdaper = getProductAdapter();
		lvProduct.setAdapter(mAdaper);
		mListViewHelperNet = new ListViewHelperNet<Rad订单明细>(this,
				Rad订单明细.class, demand, lvProduct, mDetailList, mAdaper, pbar,
				queryDemand);
		mListViewHelperNet.hiddenFootView(); // 隐藏加载更多
		reload();
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_rad_shopcar_list);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_rad_shopcar_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_rad_shopcar_list);
		tvSubmitOrder = (TextView) findViewById(R.id.tv_add_rad_shopcar_rad_shopcar_list);
		rlShopCar = (RelativeLayout) findViewById(R.id.rl_rad_shopcar_rad_shopcar_list);
		llCheckAll = (LinearLayout) findViewById(R.id.ll_checkall_rad_shopcar_list);
		ivCheckAll = (ImageView) findViewById(R.id.iv_checked_rad_shopcar_list);
		tvtotal = (TextView) findViewById(R.id.tv_total_rad_shopcar_list);
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
				// slt订单明细 itemslt订单明细 = mProducts.get(pos);
				// mProducts.get(pos).setCheck(!itemslt订单明细.isCheck());
				// Log.i("item", position + "---" + itemslt订单明细.名称);
				// }
				// adaper.notifyDataSetChanged();
			}
		});

		tvSubmitOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Rad订单明细> list = getOrderDetails();
				if (list.size() == 0) {
					Toast.makeText(mContext, "还没有选择产品哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					mOrder.明细列表 = list;
					double total = 0;
					for (Rad订单明细 orderDetail : mOrder.明细列表) {
						total += orderDetail.单价 * orderDetail.数量;
					}
					mOrder.合计 = total;

					mDictIosPicker.show(R.id.root_rad_shopcar_list,
							new String[] { "新建客户", "已有客户" });
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									switch (index) {
									case 0: // 新建客户
										mAddClientDialog.show();
										break;
									case 1: // 已有客户
										ClientBiz.selectClient(mContext);
										break;
									default:
										break;
									}
								}
							});
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
					mDetailList.get(i).setChecked(isCheckAll);
				}
				initTotal();
				mAdaper.notifyDataSetChanged();
			}
		});

		mAddClientDialog
				.setOnSaveSuccessedListener(new OnSaveSuccessedListener() {
					@Override
					public void onSaved(Rad客户 client) {
						showShortToast("新建客户成功");
						mOrder.客户 = client.编号;
						submitOrder();
					}

					@Override
					public void onErro() {
						showShortToast("新建客户失败");
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
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Rad订单明细> getProductAdapter() {
		return new CommanAdapter<Rad订单明细>(mDetailList, mContext,
				R.layout.item_rad_shopcar_list) {
			@Override
			public void convert(final int position, final Rad订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_rad_shopcar_item);
				TextView tvCategray = viewHolder
						.getView(R.id.tv_categray_rad_shopcar_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_rad_shopcar_item);
				LinearLayout llChecked = viewHolder
						.getView(R.id.ll_checked_rad_shopcar_item);
				ImageView ivChecked = viewHolder
						.getView(R.id.iv_checked_rad_shopcar_item);
				BoeryunSelectCountView selectNumView = viewHolder
						.getView(R.id.select_countview_rad_shopcar_item);
				try {
					String productName = RadProductBiz.getDictValue(mContext,
							"商品型号", item.商品详细.型号);
					tvName.setText(productName);

					String productInfo = RadProductBiz.getProductDetailInfo(
							mContext, item.商品详细);
					tvCategray.setText(productInfo + "");
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// tvName.setText(item.商品 + " " + item.制单时间);
				tvPrice.setText("￥" + item.单价);
				selectNumView.setNum(item.数量);
				if (item.isChecked()) {
					ivChecked.setImageResource(R.drawable.ico_select_shopcar);
				} else {
					ivChecked.setImageResource(R.drawable.ico_unselect_shopcar);
				}

				llChecked.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean isChecked = !item.isChecked();
						mDetailList.get(position).setChecked(isChecked);
						mAdaper.notifyDataSetChanged();
						initTotal();
					}
				});

				selectNumView.setOnNumChangedeListener(new OnNumChanged() {
					@Override
					public void onchange(int value) {
						mDetailList.get(position).数量 = value;
						if (mDetailList.get(position).isChecked()) {
							initTotal();
						}
					}
				});
			}
		};
	}

	/***
	 * 下订单
	 * 
	 * @param list
	 */
	private void submitOrder() {
		String url = Global.BASE_URL + "SltRad/saveOrder";

		ProgressDialogHelper.show(mContext);
		StringRequest.postAsyn(url, mOrder, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("下单失败");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("下单成功");
				mAdaper.removeList(mOrder.明细列表);
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("下单失败");

			}
		});
	}

	/***
	 * 获取选中订单明细列表
	 * 
	 * @return
	 */
	private List<Rad订单明细> getOrderDetails() {
		List<Rad订单明细> detailList = new ArrayList<Rad订单明细>();
		for (int i = 0; i < mDetailList.size(); i++) {
			Rad订单明细 detail = mDetailList.get(i);
			detail.小计 = detail.单价 * detail.数量;
			if (detail != null && detail.isChecked()) {
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
	private double getOrderTotal(List<Rad订单明细> detailList) {
		double total = 0;
		for (int i = 0; i < detailList.size(); i++) {
			Rad订单明细 detail = detailList.get(i);
			if (detail != null && detail.isChecked()) {
				total += detail.单价 * detail.数量;
			}
		}
		return total;
	}

	/**
	 * 初始化合计金额
	 */
	private void initTotal() {
		List<Rad订单明细> detailList = getOrderDetails();
		double total = getOrderTotal(detailList);
		tvtotal.setText("合计：￥" + total);
	}
}
