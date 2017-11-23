package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.constants.enums.EnumSltOrderStatus;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictIosPickerBottomDialog;
import com.cedarhd.helpers.DictIosPickerBottomDialog.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.models.slt.Slt订单;
import com.cedarhd.models.slt.Slt订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.widget.RadAddClientDialog;
import com.cedarhd.widget.RadAddClientDialog.OnSaveSuccessedListener;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * 森盟定制购物车列表
 * 
 * @author K 2015-12-07
 */
public class SltShopCarListActivity extends BaseActivity {

	private boolean isCheckAll = false;
	private Context mContext;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Slt订单明细> mListViewHelperNet;
	private RadAddClientDialog mAddClientDialog;
	private DictIosPickerBottomDialog mdictIosPicker;
	private Slt订单 mOrder;

	private BoeryunHeaderView headerView;
	private PullToRefreshListView lvProduct;
	private MyProgressBar pbar;

	/*** 下单 */
	private TextView tvSubmitOrder;

	/** 移除购物车 */
	private TextView tvRemove;

	/*** 选择全部 */
	private LinearLayout llCheckAll;
	private ImageView ivCheckAll;
	/** 合计金额 */
	private TextView tvtotal;
	private CommanAdapter<Slt订单明细> mAdaper;
	private List<Slt订单明细> mDetailList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slt_shopcarlist);

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
		mContext = SltShopCarListActivity.this;
		mDetailList = new ArrayList<Slt订单明细>();
		mAddClientDialog = new RadAddClientDialog(mContext);
		mdictIosPicker = new DictIosPickerBottomDialog(mContext);
		mOrder = new Slt订单();

		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		demand.方法名 = "slt/GetOrderDetails";
		demand.条件 = "";
		demand.附加条件 = "订单状态=" + EnumSltOrderStatus.购物车.getValue();
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdaper = getProductAdapter();
		lvProduct.setAdapter(mAdaper);
		mListViewHelperNet = new ListViewHelperNet<Slt订单明细>(this,
				Slt订单明细.class, demand, lvProduct, mDetailList, mAdaper, pbar,
				queryDemand);
		mListViewHelperNet.hiddenFootView(); // 隐藏加载更多
		reload();
	}

	private void initViews() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_shopcarlist);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);

		tvSubmitOrder = (TextView) findViewById(R.id.tv_add_slt_shopcar_slt_shopcar_list);
		tvRemove = (TextView) findViewById(R.id.tv_remove_slt_shopcar_slt_shopcar_list);
		llCheckAll = (LinearLayout) findViewById(R.id.ll_checkall_slt_shopcar_list);
		ivCheckAll = (ImageView) findViewById(R.id.iv_checked_slt_shopcar_list);
		tvtotal = (TextView) findViewById(R.id.tv_total_slt_shopcar_list);

	}

	private void setOnTouchListener() {
		headerView.setOnButtonClickListener(new OnButtonClickListener() {
			@Override
			public void onClickSaveOrAdd() {

			}

			@Override
			public void onClickFilter() {

			}

			@Override
			public void onClickBack() {
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
				List<Slt订单明细> list = getOrderDetails();
				if (list.size() == 0) {
					Toast.makeText(mContext, "还没有选择产品哦！", Toast.LENGTH_SHORT)
							.show();
				} else {
					mOrder.订单明细列表 = list;
					double total = 0;
					int pianShu = 0;
					int zuShu = 0;
					for (Slt订单明细 orderDetail : mOrder.订单明细列表) {
						total += orderDetail.单价 * orderDetail.数量
								* orderDetail.数量;
						pianShu += orderDetail.片数 * orderDetail.数量;
						zuShu = +orderDetail.数量;
					}
					mOrder.金额合计 = total;
					mOrder.片数合计 = pianShu;
					mOrder.数量合计 = zuShu;
					mOrder.订单状态 = EnumSltOrderStatus.店员下单.getValue();
					mAddClientDialog.show();
				}
			}
		});

		tvRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<Slt订单明细> list = getOrderDetails();
				if (list.size() == 0) {
					Toast.makeText(mContext, "还没有选择产品哦！", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				mdictIosPicker.show(new String[] { "将选中商品移除购物车" });
				mdictIosPicker.setOnSelectedListener(new OnSelectedListener() {
					@Override
					public void onSelected(int index) {
						if (index == 0) {
							removeShopcar(list);
						}
					}
				});

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
						// showShortToast("新建客户成功");
						// mOrder.客户 = client.编号;
						mOrder.终端客户名称 = client.名称;
						mOrder.终端客户地址 = client.地址;
						mOrder.终端客户联系方式 = client.电话;
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
	private CommanAdapter<Slt订单明细> getProductAdapter() {
		return new CommanAdapter<Slt订单明细>(mDetailList, mContext,
				R.layout.item_slt_shopcar_list) {
			@Override
			public void convert(final int position, final Slt订单明细 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_slt_shopcar_item);
				TextView tvCategray = viewHolder
						.getView(R.id.tv_categray_slt_shopcar_item);
				TextView tvPianPrice = viewHolder
						.getView(R.id.tv_p_price_slt_shopcar_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_slt_shopcar_item);

				LinearLayout llChecked = viewHolder
						.getView(R.id.ll_checked_slt_shopcar_item);
				ImageView ivChecked = viewHolder
						.getView(R.id.iv_checked_slt_shopcar_item);
				BoeryunSelectCountView selectNumView = viewHolder
						.getView(R.id.select_countview_slt_shopcar_item);

				tvName.setText(item.型号名称);
				tvCategray.setText(StrUtils.pareseNull(item.图号) + "\n中心距："
						+ StrUtils.pareseNull(item.中心距) + "\n排档方式:"
						+ StrUtils.pareseNull(item.排档方式));
				tvPianPrice.setText("每片单价：" + item.单价 + "*\t片数：" + item.片数);
				// tvName.setText(item.商品 + " " + item.制单时间);
				tvPrice.setText("￥" + item.每组价格);
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
						if (position < mDetailList.size()) {
							mDetailList.get(position).数量 = value;
							if (mDetailList.get(position).isChecked()) {
								initTotal();
							}
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
		String url = Global.BASE_URL + "slt/saveOrder";

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
				// 更新列表
				// mAdaper.removeList(mOrder.订单明细列表);

				// 跳转到订单列表
				finish();
				startActivity(new Intent(mContext,
						SltShopOrderListActivity.class));
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
	private List<Slt订单明细> getOrderDetails() {
		List<Slt订单明细> detailList = new ArrayList<Slt订单明细>();
		for (int i = 0; i < mDetailList.size(); i++) {
			Slt订单明细 detail = mDetailList.get(i);
			detail.金额小计 = detail.单价 * detail.片数 * detail.数量;
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
	private double getOrderTotal(List<Slt订单明细> detailList) {
		double total = 0;
		for (int i = 0; i < detailList.size(); i++) {
			Slt订单明细 detail = detailList.get(i);
			if (detail != null && detail.isChecked()) {
				total += detail.每组价格 * detail.数量;
			}
		}
		return total;
	}

	/**
	 * 初始化合计金额
	 */
	private void initTotal() {
		List<Slt订单明细> detailList = getOrderDetails();
		double total = getOrderTotal(detailList);
		String totalStr = getResources().getString(R.string.order_total_1);
		tvtotal.setText(String.format(totalStr, total));
	}

	private void removeShopcar(final List<Slt订单明细> list) {
		List<Integer> ids = new ArrayList<Integer>();
		for (Slt订单明细 detail : list) {
			ids.add(detail.编号);
		}
		mAdaper.removeList(list);
		String url = Global.BASE_URL + "slt/removeShoppingCar";
		StringRequest.postAsyn(url, ids, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				showShortToast("删除失败");
				mAdaper.addTop(list, false);
			}

			@Override
			public void onResponse(String response) {
				showShortToast("已移除购物车");
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast(INFO_ERRO_SERVER);
			}
		});
	}
}
