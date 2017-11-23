package com.cedarhd.slt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BaseActivity;
import com.cedarhd.biz.ClientBiz;
import com.cedarhd.control.BoeryunHeaderView;
import com.cedarhd.control.BoeryunHeaderView.OnButtonClickListener;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.models.Client;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.rad.Rad客户;
import com.cedarhd.models.slt.Slt型号;
import com.cedarhd.models.slt.Slt订单;
import com.cedarhd.models.slt.Slt订单明细;
import com.cedarhd.utils.StrUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.cedarhd.widget.RadAddClientDialog;
import com.cedarhd.widget.RadAddClientDialog.OnSaveSuccessedListener;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/***
 * 森拉特产品详情
 * 
 * @author K 2015-12-04
 */
public class SltProductInfoActivity extends BaseActivity {

	private Context mContext;
	/** 产品型号编号 */
	public static final String TYPE_ID = "type_id";

	/** 产品型号实体 */
	public static final String TYPE_INFO = "type_info";

	private Slt型号 mProduct;

	/** 选中的下单商品 */
	private Rad商品 mSelectedProduct;

	// private int mSelectedCount = 1;

	/** 选中下单的数量 */
	private int mSelectedPianshu;
	private int mSelectedZushu;
	private Slt订单 mOrder;

	private RadAddClientDialog mAddClientDialog;

	private BoeryunHeaderView headerView;
	private ImageView ivHead;
	private TextView tvName;
	private TextView tvPrice;
	private TextView tvDetails;
	private TextView tvAddShopCar;
	private TextView tvSaveOrder;
	private BoeryunSelectCountView selectCountPianshu;
	private BoeryunSelectCountView selectCountZushu;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slt_product_info);

		initData();
		initView();

	};

	@Override
	protected void onStart() {
		super.onStart();

		setOnEvent();
		showData();
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
		mContext = SltProductInfoActivity.this;
		mOrder = new Slt订单();
		mAddClientDialog = new RadAddClientDialog(mContext);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mProduct = (Slt型号) bundle.getSerializable(TYPE_INFO);
		}
	}

	private void initView() {
		headerView = (BoeryunHeaderView) findViewById(R.id.header_slt_product_info);
		ivHead = (ImageView) findViewById(R.id.iv_img_slt_product_info);
		tvName = (TextView) findViewById(R.id.tv_name_slt_product_info);
		tvPrice = (TextView) findViewById(R.id.tv_price_slt_product_info);
		tvDetails = (TextView) findViewById(R.id.tv_details_slt_product_info);
		tvAddShopCar = (TextView) findViewById(R.id.tv_add_shopcar_slt_product_info);
		tvSaveOrder = (TextView) findViewById(R.id.tv_saveOrder_shopcar_slt_product_info);
		selectCountPianshu = (BoeryunSelectCountView) findViewById(R.id.select_count_pianshu_slt_product_info);
		selectCountZushu = (BoeryunSelectCountView) findViewById(R.id.select_count_zushu_slt_product_info);
	}

	private void showData() {
		if (mProduct != null) {
			tvName.setText(mProduct.名称 + "");
			tvPrice.setText("￥" + mProduct.单价 + "");
			String details = "图号：" + StrUtils.pareseNull(mProduct.图号) + "\n"
					+ "系列：" + StrUtils.pareseNull(mProduct.系列名称) + "\n" + "颜色："
					+ StrUtils.pareseNull(mProduct.颜色名称) + "\n" + "中心距："
					+ StrUtils.pareseNull(mProduct.中心距) + "\n" + "进水方式："
					+ StrUtils.pareseNull(mProduct.进水方式名称) + "\n" + "接口口径："
					+ StrUtils.pareseNull(mProduct.接口口径名称) + "\n" + "挡管规格："
					+ StrUtils.pareseNull(mProduct.挡管规格) + "\n" + "主管规格："
					+ StrUtils.pareseNull(mProduct.主管规格) + "\n" + "规格高宽厚："
					+ StrUtils.pareseNull(mProduct.规格高宽厚) + "\n"
					+ "档距_头距_空位间距：" + StrUtils.pareseNull(mProduct.档距_头距_空位间距);
			tvDetails.setText(details);
		}
	}

	private void setOnEvent() {
		tvDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// showSelectProductPop();
			}
		});

		tvAddShopCar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelectedCount()) {
					saveToShopCar();
				} else {
					showShortToast("请先选择数量");
				}
			}
		});

		// 直接购买
		tvSaveOrder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isSelectedCount()) {
					mAddClientDialog.show();
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
	}

	/** 是否已选择商品 */
	private boolean isSelectedCount() {
		return (selectCountPianshu.getNum() > 0)
				&& selectCountZushu.getNum() > 0;
	}

	private void saveToShopCar() {
		ProgressDialogHelper.show(mContext);
		String url = Global.BASE_URL + "slt/saveToShoppingCar";
		List<Slt订单明细> postList = initOderDetailList();
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
	private List<Slt订单明细> initOderDetailList() {
		List<Slt订单明细> postList = new ArrayList<Slt订单明细>();
		Slt订单明细 item订单明细 = new Slt订单明细();
		item订单明细.型号 = mProduct.编号;
		item订单明细.单价 = mProduct.单价;
		item订单明细.中心距 = mProduct.中心距;
		item订单明细.主管规格 = mProduct.主管规格;
		item订单明细.图号 = mProduct.图号;
		item订单明细.排档方式 = mProduct.排档方式;
		item订单明细.接口口径 = mProduct.接口口径;
		item订单明细.是否防腐 = mProduct.是否防腐;
		item订单明细.颜色 = mProduct.颜色;
		item订单明细.主管规格 = mProduct.主管规格;
		item订单明细.片数 = selectCountPianshu.getNum();
		item订单明细.数量 = selectCountZushu.getNum();
		item订单明细.每组价格 = item订单明细.单价 * selectCountPianshu.getNum();
		item订单明细.片数小计 = selectCountPianshu.getNum() * selectCountZushu.getNum();
		item订单明细.金额小计 = item订单明细.单价 * item订单明细.片数小计;
		// item订单明细.数量 = mSelectedCount;
		// item订单明细.商品 = mSelectedProduct.编号;
		// item订单明细.小计 = item订单明细.单价 * item订单明细.数量;
		postList.add(item订单明细);
		return postList;
	}

	/***
	 * 下订单
	 * 
	 * @param list
	 */
	private void submitOrder() {
		String url = Global.BASE_URL + "slt/saveOrder";

		mOrder.订单明细列表 = initOderDetailList();
		mOrder.金额合计 = mProduct.单价 * selectCountPianshu.getNum()
				* selectCountZushu.getNum();
		mOrder.数量合计 = selectCountZushu.getNum();
		mOrder.片数合计 = selectCountPianshu.getNum() * selectCountZushu.getNum();

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

}
