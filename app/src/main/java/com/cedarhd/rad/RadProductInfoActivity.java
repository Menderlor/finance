package com.cedarhd.rad;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.biz.RadProductBiz;
import com.cedarhd.helpers.DictIosPicker;
import com.cedarhd.helpers.DictIosPicker.OnSelectedListener;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.rad.Rad产品选项;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.rad.Rad商品型号;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.models.rad.Rad订单;
import com.cedarhd.models.rad.Rad订单明细;
import com.cedarhd.models.字段描述;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.widget.RadAddClientDialog;
import com.cedarhd.widget.RadAddClientDialog.OnSaveSuccessedListener;
import com.cedarhd.widget.RadSelectProductPopupWindow;
import com.cedarhd.widget.RadSelectProductPopupWindow.OnCheckedCompleteListener;
import com.squareup.okhttp.Request;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/***
 * 产品详情
 * 
 * @author K
 * 
 */
public class RadProductInfoActivity extends BaseActivity {

	private Context mContext;
	/** 产品型号编号 */
	public static final String TYPE_ID = "type_id";
	private int mProductTypeId = 0;

	/** 选中的下单商品 */
	private Rad商品 mSelectedProduct;

	/** 选中下单的数量 */
	private int mSelectedCount = 1;

	private Rad商品型号 mProduct型号;
	/** 所有该型号的商品 */
	private List<Rad商品> mProductList;
	private Rad订单 mOrder;

	private HashMap<String, Rad产品选项> mProductSelectHashMap;
	private List<字段描述> mField字段描述s;
	private List<Rad产品选项> select产品选项list;

	private RadAddClientDialog mAddClientDialog;
	private DictIosPicker mDictIosPicker;

	private ImageView ivHead;
	private TextView tvName;
	private TextView tvPrice;
	private TextView tvChoice;
	private View anchorView;
	private TextView tvAddShopCar;
	private TextView tvSaveOrder;

	private RadSelectProductPopupWindow mSelectProductPopupWindow;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rad_product_info);

		initData();
		initView();

		fetchProduct();
	};

	@Override
	protected void onStart() {
		super.onStart();

		setOnEvent();
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
		mContext = RadProductInfoActivity.this;
		mProductTypeId = getIntent().getIntExtra(TYPE_ID, 0);
		select产品选项list = new ArrayList<Rad产品选项>();
		mOrder = new Rad订单();
		mDictIosPicker = new DictIosPicker(mContext);
		mAddClientDialog = new RadAddClientDialog(mContext);
		try {
			mField字段描述s = RadProductBiz.getTypeFieldDescribList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initView() {
		ivHead = (ImageView) findViewById(R.id.iv_img_rad_product_info);
		tvName = (TextView) findViewById(R.id.tv_name_rad_product_info);
		tvPrice = (TextView) findViewById(R.id.tv_price_rad_product_info);
		tvChoice = (TextView) findViewById(R.id.tv_choice_rad_product_info);
		anchorView = findViewById(R.id.root_rad_product_info);
		tvAddShopCar = (TextView) findViewById(R.id.tv_add_shopcar_product_info);
		tvSaveOrder = (TextView) findViewById(R.id.tv_saveOrder_shopcar_product_info);

	}

	private void setOnEvent() {
		tvChoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// showSelectProductPop();
				mSelectProductPopupWindow.showSelectProductPop();
			}
		});

		tvAddShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelected()) {
					saveToShopCar();
				} else {
					showShortToast("请先选择产品");
				}
			}
		});

		// 直接购买
		tvSaveOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelected()) {
					mOrder.明细列表 = initOderDetailList();
					mOrder.合计 = mSelectedCount * mSelectedProduct.销售单价;
					mDictIosPicker.show(R.id.root_rad_product_info,
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
				} else {
					showShortToast("请先选择产品");
				}
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

	private void fetchProduct() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "SltRad/getProductInfo/"
				+ mProductTypeId;
		StringRequest.getAsyn(url, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
				showShortToast("加载商品错误");
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				List<Rad商品型号> list = JsonUtils.ConvertJsonToList(response,
						Rad商品型号.class);
				if (list != null && list.size() > 0) {
					mProduct型号 = list.get(0);
					tvName.setText(mProduct型号.编号 + "-" + mProduct型号.名称);
					tvPrice.setText("￥" + mProduct型号.销售单价);
					mProductList = mProduct型号.商品列表s;
					try {
						mProductSelectHashMap = RadProductBiz
								.getProductSelectHashmap(mContext,
										mProductList, mField字段描述s);

						initProductSelectPopupWindow();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					} catch (IOException e) {
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					} catch (SQLException e) {
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					} catch (Exception e) {
						e.printStackTrace();
						LogUtils.e(TAG, "" + e);
					}
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
				showShortToast("网络异常");
			}
		});
	}

	/** 是否已选择商品 */
	private boolean isSelected() {
		return (mSelectedProduct != null) && mSelectedCount > 0;
	}

	private void saveToShopCar() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "SltRad/saveToShoppingCar";
		List<Rad订单明细> postList = initOderDetailList();
		StringRequest.postAsyn(url, postList, new StringResponseCallBack() {
			@Override
			public void onResponseCodeErro(String result) {
				ProgressDialogHelper.dismiss();
			}

			@Override
			public void onResponse(String response) {
				ProgressDialogHelper.dismiss();
				showShortToast("已成功添加到购物车");
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				ProgressDialogHelper.dismiss();
			}
		});
	}

	/** 根据选中产品生成一个订单明细列表订单明细 */
	private List<Rad订单明细> initOderDetailList() {
		List<Rad订单明细> postList = new ArrayList<Rad订单明细>();
		Rad订单明细 item订单明细 = new Rad订单明细();
		item订单明细.单价 = mSelectedProduct.销售单价;
		item订单明细.数量 = mSelectedCount;
		item订单明细.商品 = mSelectedProduct.编号;
		item订单明细.小计 = item订单明细.单价 * item订单明细.数量;
		postList.add(item订单明细);
		return postList;
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
			}

			@Override
			public void onFailure(Request request, Exception ex) {
				showShortToast("下单失败");

			}
		});
	}

	/** 初始化选择产品对话框 */
	private void initProductSelectPopupWindow() {
		Iterator<Entry<String, Rad产品选项>> iter = mProductSelectHashMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Rad产品选项> entry = iter.next();
			select产品选项list.add(entry.getValue());
		}
		mSelectProductPopupWindow = new RadSelectProductPopupWindow(mContext,
				mProduct型号, mProductSelectHashMap, mField字段描述s, mProductList,
				anchorView);

		mSelectProductPopupWindow
				.setOnCheckedCompleteListener(new OnCheckedCompleteListener() {
					@Override
					public void onChecked(int count, Rad商品 product) {
						mSelectedCount = count;
						mSelectedProduct = product;
						tvPrice.setText("￥" + product.销售单价);
						StringBuilder sBuilder = new StringBuilder("已选 ");
						for (Rad产品选项 item产品选项 : select产品选项list) {
							String fieldName = TextUtils
									.isEmpty(item产品选项.fieldDescribe.字段显示名) ? item产品选项.fieldDescribe.字段名
									: item产品选项.fieldDescribe.字段显示名;
							String fieldValue = item产品选项.dictionaries.get(
									item产品选项.checkedDictPos).getName();
							sBuilder.append(fieldName + ":" + fieldValue + ";");
						}
						tvChoice.setText(sBuilder.toString() + "");

						mSelectProductPopupWindow.dismissPop();
					}
				});
	}
}
