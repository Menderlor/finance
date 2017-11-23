package com.cedarhd.slt;

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
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.SltProductBiz;
import com.cedarhd.control.MyProgressBar;
import com.cedarhd.control.listview.ListViewHelperNet;
import com.cedarhd.control.listview.PullToRefreshListView;
import com.cedarhd.control.listview.PullToRefreshListView.OnRefreshListener;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Demand;
import com.cedarhd.models.QueryDemand;
import com.cedarhd.models.slt.Slt产品字典;
import com.cedarhd.models.slt.Slt型号;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.Request;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/***
 * 森拉特特供版 商品型号列表
 * 
 * @author new
 * 
 */
public class SltProductTypeListActivity extends BaseActivity {
	/** 产品型号实体 */
	public static final String TYPE_INFO = "type_info";

	/** 产品型号实体 */
	public static final String TYPE_SELECT = "type_select";
	private Context mContext;
	private boolean mIsSelectProduct;

	private Demand demand;
	private QueryDemand queryDemand;
	private ListViewHelperNet<Slt型号> mListViewHelperNet;
	private ImageView ivBack;
	private ImageView ivClear;
	private PullToRefreshListView lvProduct;
	private MyProgressBar pbar;
	private TextView tvType;
	private TextView tvColor;
	private TextView tvKoujing;
	private TextView tvJinShui;
	private TextView tvShopCar;
	private TextView tvOrder;

	private TextView tvCount;

	private DictIosPicker mDictIosPicker;
	// /** 查看购物车列表 */
	// private LinearLayout llShopCar;

	// /** 查看订单列表 */
	// private LinearLayout llOrderList;

	/** 搜索文本框 */
	private EditText etSearch;

	private CommanAdapter<Slt型号> mAdaper;
	private List<Slt型号> mProducts;

	/** 过滤条件 */
	private int mTypeFilter = -1;
	private int mColorFilter = -1;
	private int mKoujingFilter = -1;
	private int mJinshuiFilter = -1;
	private String mSearchFilter;

	/***
	 * 显示图片配置信息
	 */
	private DisplayImageOptions options;

	private final int SUCCEED_SAVE_SHOPCAR = 1;
	private final int FAILURE_SAVE_SHOPCAR = 2;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED_SAVE_SHOPCAR:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "成功添加到购物车", Toast.LENGTH_LONG).show();
				break;
			case FAILURE_SAVE_SHOPCAR:
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
		setContentView(R.layout.activity_slt_productlist);
		initViews();

		initData();
		reload();

		SltProductBiz.downloadProductDicts(mContext);
		// RadProductBiz.downloadProductFieldDescrip(context, "商品");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initData() {
		mContext = SltProductTypeListActivity.this;
		mIsSelectProduct = getIntent().getBooleanExtra(TYPE_SELECT, false);

		mProducts = new ArrayList<Slt型号>();
		mDictIosPicker = new DictIosPicker(mContext);
		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true)
				.showImageForEmptyUri(R.drawable.ico_rad_product_jd) //
				.showImageOnFail(R.drawable.ico_rad_product) //
				.cacheOnDisk(true)//
				.bitmapConfig(Config.RGB_565)//
				.build();

		queryDemand = new QueryDemand();
		demand = new Demand();
		demand.表名 = "";
		// demand.方法名 = "查询_分页";
		// demand.方法名 = "SaleStore/getProductList";
		demand.方法名 = "slt/GetTypeList";
		demand.条件 = "";
		demand.附加条件 = "";
		demand.每页数量 = 20;
		demand.偏移量 = 0;
		queryDemand.fildName = "编号";
		queryDemand.sortFildName = "编号";
		mAdaper = getProductAdapter();
		lvProduct.setAdapter(mAdaper);
		mListViewHelperNet = new ListViewHelperNet<Slt型号>(this, Slt型号.class,
				demand, lvProduct, mProducts, mAdaper, pbar, queryDemand);
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_slt_product_list);
		ivClear = (ImageView) findViewById(R.id.iv_clear_slt_product_list);
		lvProduct = (PullToRefreshListView) findViewById(R.id.lv_comman_old_style);
		pbar = (MyProgressBar) findViewById(R.id.progress_comman_old_style);

		tvType = (TextView) findViewById(R.id.tv_select_type_slt_productlist);
		tvColor = (TextView) findViewById(R.id.tv_select_color_slt_productlist);
		tvKoujing = (TextView) findViewById(R.id.tv_select_koujing_slt_productlist);
		tvJinShui = (TextView) findViewById(R.id.tv_select_jinshui_slt_productlist);
		tvShopCar = (TextView) findViewById(R.id.tv_shopcar_slt_productlist);
		tvOrder = (TextView) findViewById(R.id.tv_order_shopcar_slt_productlist);
		tvCount = (TextView) findViewById(R.id.tv_count_shopcar_slt_productlist);
		// llShopCar = (LinearLayout)
		// findViewById(R.id.ll_shopcar_rad_product_list);
		// llOrderList = (LinearLayout)
		// findViewById(R.id.ll_shopcar_rad_order_list);
		etSearch = (EditText) findViewById(R.id.et_search_slt_product_list);
		setOnTouchListener();
	}

	private void setOnTouchListener() {

		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ivClear.setOnClickListener(new OnClickListener() {
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

		tvShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, SltShopCarListActivity.class));
			}
		});
		tvOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext,
						SltShopOrderListActivity.class));
			}
		});

		lvProduct.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;

				if (mIsSelectProduct) {
					// 选择产品
					Slt型号 item产品型号 = mProducts.get(pos);
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(TYPE_INFO, item产品型号);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else if (pos < mProducts.size() && pos >= 0) {
					Slt型号 item产品型号 = mProducts.get(pos);
					Intent intent = new Intent(mContext,
							SltProductInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(SltProductInfoActivity.TYPE_INFO,
							item产品型号);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});

		// llShopCar.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// startActivity(new Intent(context, RadShopCarListActivity.class));
		// }
		// });
		//
		// /***
		// * 打开订单列表
		// */
		// llOrderList.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// startActivity(new Intent(context,
		// RadShopOrderListActivity.class));
		// }
		// });

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
				// String filter = s.toString();
				// if (TextUtils.isEmpty(filter)) {
				// demand.条件 = "";
				// } else {
				// demand.条件 = "名称 like '%" + filter + "%'";
				// }
				// mListViewHelperNet.setmDemand(demand);
				// reload();
				mSearchFilter = s.toString();
				reloadByFilter();
			}
		});

		tvType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final List<Slt产品字典> list = SltProductBiz
							.getDictListByDictTableName(mContext, "型号_系列");
					mDictIosPicker.show(R.id.root_slt_product_list, list,
							"Name");
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									ivClear.setVisibility(View.VISIBLE);
									mTypeFilter = list.get(index).getId();
									tvType.setText(list.get(index).getName()
											+ "");
									tvType.setBackgroundResource(R.drawable.flowlayout_single_tag_checked_bg);
									tvType.setTextColor(getResources()
											.getColor(R.color.white));

									reloadByFilter();
								}
							});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvColor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final List<Slt产品字典> list = SltProductBiz
							.getDictListByDictTableName(mContext, "型号_颜色");
					mDictIosPicker.show(R.id.root_slt_product_list, list,
							"Name");
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									ivClear.setVisibility(View.VISIBLE);
									mColorFilter = list.get(index).getId();
									tvColor.setText(list.get(index).getName()
											+ "");
									tvColor.setBackgroundResource(R.drawable.flowlayout_single_tag_checked_bg);
									tvColor.setTextColor(getResources()
											.getColor(R.color.white));

									reloadByFilter();
								}
							});
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		tvJinShui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final List<Slt产品字典> list = SltProductBiz
							.getDictListByDictTableName(mContext, "型号_进水方式");
					mDictIosPicker.show(R.id.root_slt_product_list, list,
							"Name");
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									ivClear.setVisibility(View.VISIBLE);
									mJinshuiFilter = list.get(index).getId();
									tvJinShui.setText(list.get(index).getName()
											+ "");

									tvJinShui
											.setBackgroundResource(R.drawable.flowlayout_single_tag_checked_bg);
									tvJinShui.setTextColor(getResources()
											.getColor(R.color.white));

									reloadByFilter();
								}
							});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tvKoujing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final List<Slt产品字典> list = SltProductBiz
							.getDictListByDictTableName(mContext, "型号_接口口径");
					mDictIosPicker.show(R.id.root_slt_product_list, list,
							"Name");
					mDictIosPicker
							.setOnSelectedListener(new OnSelectedListener() {
								@Override
								public void onSelected(int index) {
									ivClear.setVisibility(View.VISIBLE);

									mKoujingFilter = list.get(index).getId();
									showShortToast(list.get(index).getName()
											+ "");
									tvKoujing.setText(list.get(index).getName()
											+ "");
									tvKoujing
											.setBackgroundResource(R.drawable.flowlayout_single_tag_checked_bg);
									tvKoujing.setTextColor(getResources()
											.getColor(R.color.white));

									reloadByFilter();
								}
							});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		ivClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivClear.setVisibility(View.GONE);
				initFilter();
				reloadByFilter();
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
			mProducts.clear();
			mListViewHelperNet.setNotifyDataSetChanged();
			// mListViewHelperKjx.loadServerData(true);
			mListViewHelperNet.loadServerData(true);

			fetchCountFromServer();
		}
	}

	/***
	 * 获取产品列表适配器
	 * 
	 * @return
	 */
	private CommanAdapter<Slt型号> getProductAdapter() {
		return new CommanAdapter<Slt型号>(mProducts, mContext,
				R.layout.item_slt_product_list) {
			@Override
			public void convert(int position, Slt型号 item,
					BoeryunViewHolder viewHolder) {
				TextView tvName = viewHolder
						.getView(R.id.tv_name_slt_product_item);
				TextView tvPrice = viewHolder
						.getView(R.id.tv_price_slt_product_item);
				TextView tvType = viewHolder
						.getView(R.id.tv_type_slt_product_item);
				ImageView ivProduct = viewHolder
						.getView(R.id.iv_slt_product_item);
				// ivProduct.setImageResource(R.drawable.ico_rad_product_jd);

				tvName.setText(item.名称 + "");
				tvPrice.setText("￥" + item.最小销售价格 + "~" + "￥" + item.最大销售价格);
				tvType.setText("图号：" + StrUtils.pareseNull(item.图号) + "\n"
						+ "系列：" + StrUtils.pareseNull(item.系列名称)
						+ StrUtils.pareseNull(item.颜色名称) + "\n"
						+ StrUtils.pareseNull(item.接口口径名称) + "\n"
						+ StrUtils.pareseNull(item.规格高宽厚) + "\n");
				// item.图片路径 = Global.BASE_URL
				// +
				// "Upload/Upload2015/1142/5/12/1/144898044914369793531435591820.jpg";
				ImageLoader.getInstance().displayImage(item.图片路径, ivProduct,
						options, Global.getPassport());
				// if (!TextUtils.isEmpty(item.图片)) {
				// String attachFirstId = "0";
				// if (item.图片.contains(",")) {
				// attachFirstId = item.图片.split(",")[0];
				// } else {
				// attachFirstId = item.图片;
				// }
				// }
			}
		};
	}

	/** 重新获取查询过滤条件 */
	private void reloadByFilter() {
		if (TextUtils.isEmpty(mSearchFilter)) {
			demand.条件 = "1=1";
		} else {
			demand.条件 = "名称 like '%" + mSearchFilter + "%'";
		}

		if (mTypeFilter != -1) {
			demand.条件 += " and 系列 =" + mTypeFilter;
		}
		if (mColorFilter != -1) {
			demand.条件 += " and 颜色 =" + mColorFilter;
		}
		if (mJinshuiFilter != -1) {
			demand.条件 += " and 进水方式 =" + mJinshuiFilter;
		}
		if (mKoujingFilter != -1) {
			demand.条件 += " and 接口口径 =" + mKoujingFilter;
		}
		mListViewHelperNet.setmDemand(demand);
		reload();
	}

	private void fetchCountFromServer() {
		String url = Global.BASE_URL + "slt/getMyShopcarPrdoductCount";
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				LogUtils.i(TAG, "onResponseCodeErro");
			}

			@Override
			public void onResponse(String response) {
				LogUtils.i(TAG, response);
				String json = StrUtils.removeRex(JsonUtils.pareseData(response));
				if (!TextUtils.isEmpty(json)) {
					Log.i(TAG, json);
					try {
						int count = Integer.parseInt(json);
						if (count > 0) {
							tvCount.setVisibility(View.VISIBLE);
							tvCount.setText("" + count);
						}
					} catch (Exception e) {
						tvCount.setVisibility(View.INVISIBLE);
						LogUtils.e(TAG, e.getMessage() + "");
					}
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				LogUtils.i(TAG, ex.getMessage() + "");
			}
		});
	}

	private void initFilter() {
		mTypeFilter = -1;
		mColorFilter = -1;
		mKoujingFilter = -1;
		mJinshuiFilter = -1;

		tvType.setBackgroundResource(R.drawable.flowlayout_single_tag_bg);
		tvType.setTextColor(getResources().getColor(R.color.text_black));

		tvColor.setBackgroundResource(R.drawable.flowlayout_single_tag_bg);
		tvColor.setTextColor(getResources().getColor(R.color.text_black));

		tvJinShui.setBackgroundResource(R.drawable.flowlayout_single_tag_bg);
		tvJinShui.setTextColor(getResources().getColor(R.color.text_black));

		tvKoujing.setBackgroundResource(R.drawable.flowlayout_single_tag_bg);
		tvKoujing.setTextColor(getResources().getColor(R.color.text_black));
	}
}
