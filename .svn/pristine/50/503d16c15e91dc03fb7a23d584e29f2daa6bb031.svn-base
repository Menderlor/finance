package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.control.MyFlowLayout;
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
import com.cedarhd.models.产品型号;
import com.cedarhd.models.产品订单明细;
import com.cedarhd.models.字典;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.j256.ormlite.dao.Dao;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/***
 * 产品列表
 * 
 * @author K 2015-7-17
 */
public class ProductListActivity extends BaseActivity {
	private final String TAG = "ProductListActivity";
	private Context context;
	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<产品型号> mListViewHelperNet;
	private ImageView ivBack;
	private ImageView ivCategray;
	private DrawerLayout drawerLayout;
	private PullToRefreshListView lvProduct;
	private ListView lvCategray;
	private MyProgressBar pbar;
	/*** 添加到购物车列表 */
	private TextView tvAddShopCar;
	/** 查看购物车列表 */
	private LinearLayout llShopCar;

	/** 查看订单列表 */
	private LinearLayout llOrderList;

	private TextView tvAllCategray;

	/** 搜索文本框 */
	private EditText etSearch;

	/** 显示历史记录布局 */
	private RelativeLayout rlHistory;
	private MyFlowLayout flowHistory;

	/***
	 * 新建订单
	 */
	private ImageView ivAdd;

	private CommanAdapter<产品型号> adaper;
	private List<产品型号> mProducts;
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
				lvCategray.setAdapter(getCateGrayAdapter());
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
		setContentView(R.layout.activity_product_list);

		initViews();

		initData();
		initHistoryLayout();
		reload();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCategrayList == null || mCategrayList.size() == 0) {
			downLoadCategray();
		}
	}

	private void initData() {
		context = ProductListActivity.this;
		mProducts = new ArrayList<产品型号>();
		mhHttpUtils = new HttpUtils();
		try {
			mDao = ORMDataHelper.getInstance(context).getDao(
					LatestSelectedDict.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		options = new DisplayImageOptions.Builder()
				//
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
		demand.方法名 = "SaleStore/getProductSimpleList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		adaper = getProductAdapter();
		lvProduct.setAdapter(adaper);
		mListViewHelperNet = new ListViewHelperNet<产品型号>(this, 产品型号.class,
				demand, lvProduct, mProducts, adaper, pbar, queryDemand);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_product_list);
		ivCategray = (ImageView) findViewById(R.id.iv_filter_product_list);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_product_list);
		lvCategray = (ListView) findViewById(R.id.lv_drawer_left_categray);
		pbar = (MyProgressBar) findViewById(R.id.progress_productlist);
		tvAddShopCar = (TextView) findViewById(R.id.tv_add_shopcar_product_list);
		llShopCar = (LinearLayout) findViewById(R.id.ll_shopcar_product_list);
		llOrderList = (LinearLayout) findViewById(R.id.ll_shopcar_order_list);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_product_list);
		tvAllCategray = (TextView) findViewById(R.id.tv_all_left_categray);
		ivAdd = (ImageView) findViewById(R.id.iv_add_product_list);
		etSearch = (EditText) findViewById(R.id.et_search_product_list);
		rlHistory = (RelativeLayout) findViewById(R.id.rl_search_history_product_list);
		flowHistory = (MyFlowLayout) findViewById(R.id.flow_layout_product_list);
		if (Global.mUser != null && !TextUtils.isEmpty(Global.mUser.Admin)) {
			ivAdd.setVisibility(View.VISIBLE);
		}
		setOnTouchListener();
	}

	/***
	 * 初始化历史记录显示布局，无历史记录则不显示
	 */
	private void initHistoryLayout() {
		List<LatestSelectedDict> localDicts = getLocalLatestList();
		for (int i = 0; i < localDicts.size(); i++) {
			final LatestSelectedDict item = localDicts.get(i);
			// 显示本地最近选择
			final TextView tvName = (TextView) LayoutInflater.from(context)
					.inflate(R.layout.tag_text, flowHistory, false);
			tvName.setText(localDicts.get(i).getName() + "");
			tvName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					etSearch.setText(item.getName());
				}
			});
			flowHistory.addView(tvName);
		}
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
				drawerLayout.openDrawer(Gravity.RIGHT);
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
				if (pos < mProducts.size()) {
					产品型号 item产品型号 = mProducts.get(pos);
					mProducts.get(pos).setCheck(!item产品型号.isCheck());
					Log.i("item", position + "---" + item产品型号.名称);

					insertDbIfNoExist(new 字典(item产品型号.编号, item产品型号.名称));
				}
				adaper.notifyDataSetChanged();
			}
		});

		lvCategray.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				drawerLayout.closeDrawer(Gravity.RIGHT);
				Dict dict = mCategrayList.get(position);
				demand.条件 = "分类=" + dict.编号;
				mListViewHelperNet.setmDemand(demand);
				reload();
			}
		});

		ivAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, ProductAddActivity.class));
			}
		});

		tvAllCategray.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.closeDrawer(Gravity.RIGHT);
				demand.条件 = "";
				mListViewHelperNet.setmDemand(demand);
				reload();
			}
		});

		tvAddShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<产品订单明细> list = new ArrayList<产品订单明细>();
				for (int i = 0; i < mProducts.size(); i++) {
					产品型号 itemProduct = mProducts.get(i);
					if (itemProduct.isCheck()) {
						LogUtils.d("item", itemProduct.名称 + "" + itemProduct.单价);
						产品订单明细 item = new 产品订单明细(itemProduct.编号,
								itemProduct.单价, 1, itemProduct.单价);
						list.add(item);
					}
				}

				if (list.size() > 0) {
					ProgressDialogHelper.show(context, "保存到购物车..");
					saveProductListsToShoppingCar(list);
				} else {
					Toast.makeText(context, "请先选择产品", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		llShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, ShoppingCarListActivity.class));
			}
		});

		/***
		 * 打开订单列表
		 */
		llOrderList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, ShopOrderListActivity.class));
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
				if (View.VISIBLE == rlHistory.getVisibility()) {
					rlHistory.setVisibility(View.GONE);
				} else {
					rlHistory.setVisibility(View.VISIBLE);
				}
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
				rlHistory.setVisibility(View.GONE);
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

	private void downLoadCategray() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				String url = Global.BASE_URL + "SaleStore/getCategoryList";
				try {
					String result = mhHttpUtils.httpGet(url);
					List<Dict> categrayList = JsonUtils.ConvertJsonToList(
							result, Dict.class);

					if (categrayList != null && categrayList.size() > 0) {
						msg.what = SUCCEED_DOWN_CATEGRAY;
						msg.obj = categrayList;
					} else {
						msg.what = FAILURE_DOWN_CATEGRAY;
					}
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = FAILURE_DOWN_CATEGRAY;
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/***
	 * 获取本地最近选择列表
	 * 
	 * @return
	 */
	private List<LatestSelectedDict> getLocalLatestList() {
		try {
			// 把当前页面名称作为字典名称
			return mDao.queryBuilder().orderBy("updateTime", false).where()
					.eq("DictName", TAG).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new ArrayList<LatestSelectedDict>();
	}

	/***
	 * 选中字典项，添加到最近选中数据库
	 * 
	 * @param dict
	 */
	private void insertDbIfNoExist(字典 dict) {
		try {
			int count = mDao.queryBuilder().where().eq("DictName", TAG).query()
					.size();
			if (count >= MAX_LATEST_VALUE) {
				// 如果超出最大数量，先删除最后更新时间小的，间隔远的
				long deleteCount = count / 2;
				mDao.delete(mDao.queryBuilder().orderBy("updateTime", true)
						.limit(deleteCount).query());
			}

			// 查询相同字典项
			LatestSelectedDict updateDict = mDao.queryBuilder().where()
					.eq("DictName", TAG).and().eq("Id", dict.getId())
					.queryForFirst();
			mDao.delete(updateDict);

			updateDict = new LatestSelectedDict(dict.getId(), dict.getName(),
					TAG);
			mDao.create(updateDict);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<产品型号> getProductAdapter() {
		return new CommanAdapter<产品型号>(mProducts, context,
				R.layout.item_product_list) {
			@Override
			public void convert(int position, 产品型号 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder.getView(R.id.tv_name_product_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_product_item);
				ImageView ivChecked = viewHolder
						.getView(R.id.iv_checked_product_item);
				ImageView ivProduct = viewHolder.getView(R.id.iv_product_item);
				ivProduct.setImageResource(R.drawable.tupian_bg_tmall);
				String imgUrl = Global.BASE_URL + item.图片;
				ImageLoader.getInstance().displayImage(imgUrl, ivProduct,
						options, Global.getPassport());
				tvName.setText(item.名称 + "");
				tvPrice.setText("￥" + item.单价);

				if (item.isCheck()) {
					// ivChecked.setVisibility(View.VISIBLE);
					ivChecked.setImageResource(R.drawable.ico_checked_shop_car);
				} else {
					// ivChecked.setVisibility(View.GONE);
					ivChecked.setImageResource(R.drawable.ico_uncheck_shop_car);
				}
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
