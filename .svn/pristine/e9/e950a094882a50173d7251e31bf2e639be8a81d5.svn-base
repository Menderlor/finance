package com.cedarhd.rad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.ORMDataHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.Dict;
import com.cedarhd.models.LatestSelectedDict;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.rad.Rad商品型号;
import com.cedarhd.models.产品订单明细;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 森盟产品列表
 * 
 * 2015/11/12 11:04
 * 
 */
public class RadProductListActivity extends BaseActivity {
	private final String TAG = "ProductListActivity";
	private Context context;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Rad商品型号> mListViewHelperNet;
	private ImageView ivBack;
	private ImageView ivCategray;
	private PullToRefreshListView lvProduct;
	private MyProgressBar pbar;
	/** 查看购物车列表 */
	private LinearLayout llShopCar;

	/** 查看订单列表 */
	private LinearLayout llOrderList;

	/** 搜索文本框 */
	private EditText etSearch;

	private CommanAdapter<Rad商品型号> adaper;
	private List<Rad商品型号> mProducts;
	private List<Dict> mCategrayList;

	private Dao<LatestSelectedDict, Integer> mDao;
	private final int MAX_LATEST_VALUE = 5;

	/***
	 * 显示图片配置信息
	 */
	private DisplayImageOptions options;
	private HttpUtils mhHttpUtils;

	private final int SUCCEED_SAVE_SHOPCAR = 1;
	private final int FAILURE_SAVE_SHOPCAR = 2;
	private final int SUCCEED_DOWN_CATEGRAY = 3;
	private final int FAILURE_DOWN_CATEGRAY = 4;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_SAVE_SHOPCAR:
				ProgressDialogHelper.dismiss();
				Toast.makeText(context, "成功添加到购物车", Toast.LENGTH_LONG).show();
				break;
			case FAILURE_SAVE_SHOPCAR:
				ProgressDialogHelper.dismiss();
				break;
			case SUCCEED_DOWN_CATEGRAY:
				mCategrayList = (List<Dict>) msg.obj;
				break;
			case FAILURE_DOWN_CATEGRAY:
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_productlist);
		initViews();

		initData();
		reload();

		RadProductBiz.downloadProductDicts(context);
		RadProductBiz.downloadProductFieldDescrip(context, "商品");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		context = RadProductListActivity.this;
		mProducts = new ArrayList<Rad商品型号>();
		mhHttpUtils = new HttpUtils();
		try {
			mDao = ORMDataHelper.getInstance(context).getDao(
					LatestSelectedDict.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.ic_launcher) //
				.showImageOnFail(R.drawable.tupian_bg_tmall) //
				.cacheOnDisk(true)//
				.bitmapConfig(Config.RGB_565)//
				.build();

		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		// demand.方法名 = "SaleStore/getProductList";
		demand.方法名 = "SltRad/getProducts";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		adaper = getProductAdapter();
		lvProduct.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<Rad商品型号>(this,
				Rad商品型号.class, demand, lvProduct, mProducts, adaper, pbar,
				queryDemand);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_rad_product_list);
		ivCategray = (ImageView) findViewById(R.id.iv_filter_rad_product_list);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_rad_product_list);
		pbar = (MyProgressBar) findViewById(R.id.progress_rad_productlist);
		llShopCar = (LinearLayout) findViewById(R.id.ll_shopcar_rad_product_list);
		llOrderList = (LinearLayout) findViewById(R.id.ll_shopcar_rad_order_list);
		etSearch = (EditText) findViewById(R.id.et_search_rad_product_list);
		setOnTouchListener();
	}

	private void setOnTouchListener() {

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivCategray.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		lvProduct.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// lvProduct.onRefreshComplete();
				reload();
			}
		});

		lvProduct.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;

				if (pos < mProducts.size() && pos >= 0) {
					Rad商品型号 item产品型号 = mProducts.get(pos);
					Intent intent = new Intent(context,
							RadProductInfoActivity.class);
					intent.putExtra(RadProductInfoActivity.TYPE_ID, item产品型号.编号);
					startActivity(intent);
				}
			}
		});

		llShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, RadShopCarListActivity.class));
			}
		});

		/***
		 * 打开订单列表
		 */
		llOrderList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context,
						RadShopOrderListActivity.class));
			}
		});

		// etSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (!hasFocus) {
		// // 失去焦点时，隐藏历史记录
		// rlHistory.setVisibility(View.GONE);
		// }
		// }
		// });

		etSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if (View.VISIBLE == rlHistory.getVisibility()) {
				// rlHistory.setVisibility(View.GONE);
				// } else {
				// rlHistory.setVisibility(View.VISIBLE);
				// }
			}
		});

		// 搜索文本框
		etSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String filter = s.toString();
				if (TextUtils.isEmpty(filter)) {
					demand.条件 = "";
				} else {
					demand.条件 = "名称 like '%" + filter + "%'";
				}
				mListViewHelperNet.setmDemand(demand);
				reload();
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
			mProducts.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);
		}
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Rad商品型号> getProductAdapter() {
		return new CommanAdapter<Rad商品型号>(mProducts, context,
				R.layout.item_rad_product_list) {
			@Override
			public void convert(int position, Rad商品型号 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_rad_product_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_rad_product_item);
				ImageView ivProduct = viewHolder
						.getView(R.id.iv_rad_product_item);
				// ivProduct.setImageResource(R.drawable.ico_rad_product);
				ivProduct.setImageResource(R.drawable.ico_rad_product_jd);

				tvName.setText(item.名称 + "");
				tvPrice.setText("￥" + item.销售原价);

				if (!TextUtils.isEmpty(item.图片)) {
					String attachFirstId = "0";
					if (item.图片.contains(",")) {
						attachFirstId = item.图片.split(",")[0];
					} else {
						attachFirstId = item.图片;
					}

					// String imgUrl = Global.BASE_URL
					// + "FileUpDownLoad/downloadAttach/" + attachFirstId;
					// ImageLoader.getInstance().displayImage(imgUrl, ivProduct,
					// options);
				}

				// if (item.isCheck()) {
				// // ivChecked.setVisibility(View.VISIBLE);
				// ivChecked.setImageResource(R.drawable.ico_checked_shop_car);
				// } else {
				// // ivChecked.setVisibility(View.GONE);
				// ivChecked.setImageResource(R.drawable.ico_uncheck_shop_car);
				// }
			}
		};
	}

	/***
	 * 获取分类内容适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Dict> getCateGrayAdapter() {
		return new CommanAdapter<Dict>(mCategrayList, context,
				R.layout.item_categray) {
			@Override
			public void convert(int position, Dict item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_categray_item);
				tvName.setText(item.名称);
			}
		};
	}

	/***
	 * 保存到购物车
	 * 
	 * @param list
	 */
	private void saveProductListsToShoppingCar(final List<产品订单明细> list) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// String url = Global.BASE_URL
				// + "SaleStore/saveProductListsToShoppingCar";
				String url = Global.BASE_URL
						+ "SaleStore/saveProductsToShoppingCar";
				try {
					JSONObject jo = new JSONObject();
					String jsonContent = JsonUtils.initJsonString(list);
					jo.put("content", jsonContent);
					LogUtils.i(TAG, jsonContent);
					String result = mhHttpUtils.postSubmit(url, jo);
					Log.i("succ", result);
					handler.sendEmptyMessage(SUCCEED_SAVE_SHOPCAR);
				} catch (Exception e) {
					e.printStackTrace();
					LogUtils.e("erro", e.toString() + "");
					handler.sendEmptyMessage(FAILURE_SAVE_SHOPCAR);
				}
			}
		}).start();
	}
}
