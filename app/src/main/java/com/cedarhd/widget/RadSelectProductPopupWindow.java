package com.cedarhd.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.adapter.RadProductSelectAdapter;
import com.cedarhd.adapter.RadProductSelectAdapter.OnCheckedListener;
import com.cedarhd.control.BoeryunSelectCountView;
import com.cedarhd.control.BoeryunSelectCountView.OnNumChanged;
import com.cedarhd.control.listview.BoeryunNoScrollListView;
import com.cedarhd.models.rad.Rad产品选项;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.rad.Rad商品型号;
import com.cedarhd.models.字段描述;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class RadSelectProductPopupWindow {

	private Context mContext;
	private Rad商品型号 mProduct型号;
	private HashMap<String, Rad产品选项> mProductSelectHashMap;
	private List<字段描述> mField字段描述s;
	/** 所有该型号的商品 */
	private List<Rad商品> mProductList;

	private int mSelectedCount = 1;
	private Rad商品 mSelectedProduct;

	/** popupWindow相对空控件 */
	private View anchorView;
	private TextView tvChoicePop;
	private TextView tvNamePop;
	private TextView tvPricePop;
	private BoeryunSelectCountView selectCountViewPop;
	private PopupWindow mPopupWindow;

	/** 选中按钮 */
	private TextView tvSurePop;

	public RadSelectProductPopupWindow(Context mContext, Rad商品型号 mProduct型号,
			HashMap<String, Rad产品选项> mProductSelectHashMap,
			List<字段描述> mField字段描述s, List<Rad商品> mProductList, View anchorView) {
		super();
		this.mContext = mContext;
		this.mProduct型号 = mProduct型号;
		this.mProductSelectHashMap = mProductSelectHashMap;
		this.mField字段描述s = mField字段描述s;
		this.mProductList = mProductList;
		this.anchorView = anchorView;
	}

	public void dismissPop() {
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}

	public void showSelectProductPop() {
		View view = View.inflate(mContext, R.layout.pop_select_product_type,
				null);
		mPopupWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		initPopViews(view);
		mPopupWindow.setAnimationStyle(R.style.AnimationFadeBottom);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		mPopupWindow.showAtLocation(anchorView, Gravity.BOTTOM | Gravity.LEFT,
				0, 0);
	}

	private void initPopViews(View view) {
		BoeryunNoScrollListView lv = (BoeryunNoScrollListView) view
				.findViewById(R.id.lv_select_product_type_pop);
		tvNamePop = (TextView) view
				.findViewById(R.id.tv_name_select_product_type_pop);
		tvPricePop = (TextView) view
				.findViewById(R.id.tv_price_select_product_type_pop);
		tvChoicePop = (TextView) view
				.findViewById(R.id.tv_choice_select_product_type_pop);
		tvChoicePop = (TextView) view
				.findViewById(R.id.tv_choice_select_product_type_pop);
		tvSurePop = (TextView) view
				.findViewById(R.id.tv_sure_shopcar_product_info);
		selectCountViewPop = (BoeryunSelectCountView) view
				.findViewById(R.id.count_select_product_type_pop);

		tvNamePop.setText(mProduct型号.名称 + "");
		tvPricePop.setText(mProduct型号.销售单价 + "");
		tvChoicePop.setText("请选择");

		final List<Rad产品选项> list = new ArrayList<Rad产品选项>();
		Iterator<Entry<String, Rad产品选项>> iter = mProductSelectHashMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Rad产品选项> entry = iter.next();
			list.add(entry.getValue());
		}
		RadProductSelectAdapter mAdapter = new RadProductSelectAdapter(
				mContext, list, mProductList, mField字段描述s,
				R.layout.item_select_product_type_pop);
		mAdapter.setCheckedListener(new OnCheckedListener() {
			@Override
			public void onChecked(Rad商品 selectedProduct) {
				mSelectedProduct = selectedProduct;
				if (selectedProduct != null) {
					tvPricePop.setText(selectedProduct.销售单价 + "");
					StringBuilder sBuilder = new StringBuilder("已选 ");
					for (Rad产品选项 item产品选项 : list) {
						String fieldName = TextUtils
								.isEmpty(item产品选项.fieldDescribe.字段显示名) ? item产品选项.fieldDescribe.字段名
								: item产品选项.fieldDescribe.字段显示名;
						String fieldValue = item产品选项.dictionaries.get(
								item产品选项.checkedDictPos).getName();
						sBuilder.append(fieldName + ":" + fieldValue + ";");
					}
					tvChoicePop.setText(sBuilder.toString() + "");
					tvSurePop.setBackgroundColor(mContext.getResources()
							.getColor(R.color.color_shop_car_bg));
				} else {
					// mSelectedCount = 1;
					selectedProduct = null;
					tvChoicePop.setText("未找到指定型号系列产品，请重新选择");
					tvPricePop.setText("0");
					tvSurePop.setBackgroundColor(mContext.getResources()
							.getColor(R.color.lightgray));
				}
			}
		});
		lv.setAdapter(mAdapter);

		// lv.setAdapter(new RadProductSelectAdapter(mContext, list,
		// productList, field字段描述s, mLayoutId));

		selectCountViewPop.setOnNumChangedeListener(new OnNumChanged() {
			@Override
			public void onchange(int value) {
				mSelectedCount = value;
			}
		});

		tvSurePop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSelectedProduct == null) {
					return;
				}

				if (mOnCheckedCompleteListener != null) {
					mOnCheckedCompleteListener.onChecked(mSelectedCount,
							mSelectedProduct);
				}
			}
		});
	}

	private OnCheckedCompleteListener mOnCheckedCompleteListener;

	public void setOnCheckedCompleteListener(
			OnCheckedCompleteListener onCheckedCompleteListener) {
		this.mOnCheckedCompleteListener = onCheckedCompleteListener;
	}

	public interface OnCheckedCompleteListener {
		void onChecked(int count, Rad商品 product);
	}
}
