package com.cedarhd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.helpers.DateAndTimePicker;
import com.cedarhd.helpers.Global;
import com.cedarhd.helpers.Util;
import com.cedarhd.helpers.ViewHelper;
import com.cedarhd.models.changhui.CaculateProduct;
import com.cedarhd.models.changhui.字典项;
import com.cedarhd.models.changhui.理财产品购买合同收益;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.utils.okhttp.StringRequest;
import com.cedarhd.utils.okhttp.StringResponseCallBack;
import com.squareup.okhttp.Request;

import java.util.List;

/**
 * 收益计算器页面
 * 
 * @author Win
 * 
 */
public class ProfitCaculatorActivity extends BaseActivity {
	private Context context;

	private int productID = -1;// 产品id，默认值为-1

	private ImageView iv_back;

	private DateAndTimePicker mTimePicker;

	private TextView tv_date;
	private TextView tv_product_name;// 产品名称
	private EditText et_jine;// 投资金额
	private EditText et_qixian;// 投资期限
	private Button btn_caculate;// 计算按钮

	private SelectProductWindow popWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profit_calculator);

		initData();
		initViews();
		setOnClickListener();

	}

	private void initData() {
		context = ProfitCaculatorActivity.this;
		popWindow = new SelectProductWindow(context);
		mTimePicker = new DateAndTimePicker(context);
	}

	private void initViews() {
		iv_back = (ImageView) findViewById(R.id.iv_cancel_calculator);
		tv_date = (TextView) findViewById(R.id.tv_profit_calculator);
		tv_product_name = (TextView) findViewById(R.id.tv_profit_caculator_product_name);
		et_jine = (EditText) findViewById(R.id.et_caculate_touzi_jine);
		et_qixian = (EditText) findViewById(R.id.et_caculate_touzi_qixian);
		btn_caculate = (Button) findViewById(R.id.btn_caculate_product_profit);

	}

	private void setOnClickListener() {
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tv_date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mTimePicker.showDateWheel(tv_date, false);
				mTimePicker
						.setOnSelectedListener(new DateAndTimePicker.ISelected() {
							@Override
							public void onSelected(String date) {
								String str = ViewHelper
										.convertStrToFormatDateStr(date,
												"yyyy-MM-dd");
								tv_date.setText(str);
							}
						});
			}
		});

		tv_product_name.setOnClickListener(new OnClickListener() { // 弹出选择产品名称的popupwindow

					@Override
					public void onClick(View arg0) {
						popWindow.show();
					}
				});

		btn_caculate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				caculate();
			}
		});

	}

	private void caculate() {

		String url = Global.BASE_URL + "Wealth/GetProductProfit";

		String productName = tv_product_name.getText().toString().trim();
		String productJine = et_jine.getText().toString().trim();
		String productQixian = et_qixian.getText().toString().trim();
		String productDate = tv_date.getText().toString().trim();

		if (TextUtils.isEmpty(productName)) {
			showShortToast("产品名称不能为空！");
			return;
		} else if (TextUtils.isEmpty(productJine)) {
			showShortToast("投资金额不能为空！");
			return;
		} else if (TextUtils.isEmpty(productQixian)) {
			showShortToast("投资期限不能为空！");
			return;
		} else if (TextUtils.isEmpty(productQixian)) {
			showShortToast("打款日期不能为空！");
			return;
		}

		CaculateProduct product = new CaculateProduct();
		if (productID != -1) {
			product.setProductId(productID);
		}
		product.setPeriod(Integer.valueOf(productQixian));
		product.setPayingTime(productDate);
		product.setAmount(Double.valueOf(productJine));

		StringRequest.postAsyn(url, product, new StringResponseCallBack() {

			@Override
			public void onResponseCodeErro(String result) {
				result = JsonUtils.pareseMessage(result);
				showShortToast(result);

			}

			@Override
			public void onResponse(String response) {
				response = response.substring(0, 34) + response.substring(85, response.length() - 2);
				LogUtils.i(TAG, response);
				理财产品购买合同收益 profit = JsonUtils.ConvertJsonObject(response,
						理财产品购买合同收益.class);
				if (profit != null) {
					Intent intent = new Intent(context,ProfitCaculatorInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("caculateProfitInfo", profit);
					intent.putExtra("caculateProfit", bundle);
					startActivity(intent);
				}
			}

			@Override
			public void onFailure(Request request, Exception ex) {

			}
		});
	}

	private class SelectProductWindow extends PopupWindow {

		private Context context;
		private List<字典项> list;
		private ListView lv;
		private MyAdapter adapter;
		private PopupWindow popupWindow;
		private View parentView;

		public SelectProductWindow(Context context) {
			this.context = context;
			findViews(context);
			init(context);
		}

		private void findViews(Context context) {
			parentView = View.inflate(context,
					R.layout.popup_profit_caculator_select_product, null);
			popupWindow = new PopupWindow(parentView,
					LayoutParams.MATCH_PARENT, Util.dip2px(context, 200));
			initPopupWindow(parentView, popupWindow);
			lv = (ListView) parentView.findViewById(R.id.lv_select_product);
		}

		private void initPopupWindow(View parentView,
				final PopupWindow popupWindow) {
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					setBackgroundAlpha(1);
				}
			});

			popupWindow.setAnimationStyle(R.style.AnimationFadeBottom);
			// setBackgroundAlpha(0.5f);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true);
			// popupWindow.showAtLocation(parentView, Gravity.BOTTOM |
			// Gravity.LEFT,
			// 0, 0);

		}

		private void show() {
			popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
		}

		/**
		 * 设置添加屏幕的背景透明度
		 * 
		 * @param bgAlpha
		 */
		public void setBackgroundAlpha(float bgAlpha) {
			WindowManager.LayoutParams lp = ((Activity) context).getWindow()
					.getAttributes();
			lp.alpha = bgAlpha;// 0.0-1.0
			((Activity) context).getWindow().setAttributes(lp);
		}

		private void init(final Context context) {

			/**
			 * 0:所有 1:已上线 2:在售
			 */
			String url = Global.BASE_URL
					+ "Wealth/GetSimpleProductList?status=1";

			StringRequest.getAsyn(url, new StringResponseCallBack() {

				@Override
				public void onResponseCodeErro(String result) {

				}

				@Override
				public void onResponse(String response) {
					list = JsonUtils.ConvertJsonToList(response, 字典项.class);
					adapter = new MyAdapter(list, context);
					lv.setAdapter(adapter);

					lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if (list != null && list.size() > 0) {
								字典项 item = list.get(arg2);
								tv_product_name.setText(item.名称);
								productID = item.编号;
								popupWindow.dismiss();
							}
						}
					});
				}

				@Override
				public void onFailure(Request request, Exception ex) {

				}
			});
		}
	}

	private class MyAdapter extends BaseAdapter {
		private List<字典项> list;
		private Context context;

		public MyAdapter(List<字典项> list, Context context) {
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			View view = View.inflate(context,
					R.layout.item_lv_caculator_select_product, null);

			TextView tv_name = (TextView) view
					.findViewById(R.id.tv_caculator_product_name);

			字典项 item = list.get(position);

			tv_name.setText(item.名称);
			return view;
		}

	}
}
