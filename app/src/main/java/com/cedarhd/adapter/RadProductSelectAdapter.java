package com.cedarhd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.biz.RadProductBiz;
import com.cedarhd.models.rad.Rad产品字典;
import com.cedarhd.models.rad.Rad产品选项;
import com.cedarhd.models.rad.Rad商品;
import com.cedarhd.models.字段描述;
import com.cedarhd.utils.LogUtils;
import com.cedarhd.view.FlowLayout;
import com.cedarhd.view.TagFlowLayout;
import com.cedarhd.view.TagFlowLayout.OnSelectListener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RadProductSelectAdapter extends CommanAdapter<Rad产品选项> {

	private final String TAG = "RadProductSelectAdapter";
	private List<Rad产品选项> mList;
	private List<Rad商品> mProductList;
	private List<字段描述> mField字段描述s;

	private LayoutInflater mInflater;

	public RadProductSelectAdapter(Context context, List<Rad产品选项> list,
			List<Rad商品> productList, List<字段描述> field字段描述s, int mLayoutId) {
		super(list, context, mLayoutId);
		mInflater = LayoutInflater.from(context);
		this.mList = list;
		this.mField字段描述s = field字段描述s;
		this.mProductList = productList;
	}

	@Override
	public void convert(final int position, final Rad产品选项 item,
			BoeryunViewHolder viewHolder) {
		TextView tvTitle = (TextView) viewHolder
				.getView(R.id.tv_title_select_product_type_item);
		final TagFlowLayout tagFlowLayout = (TagFlowLayout) viewHolder
				.getView(R.id.tagflow_select_product_type_item);
		String title = TextUtils.isEmpty(item.fieldDescribe.字段显示名) ? item.fieldDescribe.字段名
				: item.fieldDescribe.字段显示名;
		tvTitle.setText(title);

		tagFlowLayout.setAdapter(new TagAdapter<Rad产品字典>(item.dictionaries) {
			@Override
			public View getView(FlowLayout parent, int position, Rad产品字典 t) {
				LogUtils.i("tagA", position + "--" + t);
				TextView tv = (TextView) mInflater.inflate(
						R.layout.item_tag_single_flowlayout, tagFlowLayout,
						false);
				tv.setText(item.dictionaries.get(position).getName() + "");
				return tv;
			}
		});

		tagFlowLayout.setOnSelectListener(new OnSelectListener() {
			@Override
			public void onSelected(Set<Integer> selectPosSet) {
				if (selectPosSet != null && selectPosSet.size() > 0) {
					Iterator<Integer> iteratorSelectPoss = selectPosSet
							.iterator();
					while (iteratorSelectPoss.hasNext()) {
						item.checkedDictPos = iteratorSelectPoss.next();
						LogUtils.i(TAG, "checkedDictId=" + item.checkedDictPos);
						break;
					}
				} else {
					item.checkedDictPos = -1;
					item.checkedDictId = 0;
				}

				// toast选中信息
				if (item.checkedDictPos >= 0
						&& item.checkedDictPos < item.dictionaries.size()) {
					Rad产品字典 slt产品字典 = item.dictionaries
							.get(item.checkedDictPos);
					// showShortToast(item.fieldDescribe.字段名 + "-"
					// + item.fieldDescribe.字段显示名 + ",选中："
					// + slt产品字典.getName());
				}

				// 遍历选择项
				for (int i = 0; i < mList.size(); i++) {
					Rad产品选项 slt产品选项 = mList.get(i);
					if (slt产品选项.checkedDictPos >= 0
							&& slt产品选项.checkedDictPos < slt产品选项.dictionaries
									.size()) {
						Rad产品字典 slt产品字典 = slt产品选项.dictionaries
								.get(slt产品选项.checkedDictPos);
						LogUtils.i("select", slt产品选项.fieldDescribe.字段名 + "-"
								+ slt产品选项.fieldDescribe.字段显示名 + ",选中："
								+ slt产品字典.getName());
						slt产品选项.checkedDictId = slt产品字典.getId();
					}
				}

				// 各个选项之间相互影响 下一项的字典集合
				// List<slt产品字典> dicts = null;
				// 保存每个选项 值的集合
				HashMap<String, HashSet<Integer>> productSetHashMap = new HashMap<String, HashSet<Integer>>();
				boolean isAllSelect = true;
				Class productClassType = Rad商品.class;
				for (Rad产品选项 slt产品选项 : mList) {
					if (slt产品选项.checkedDictId != 0) {
						for (Rad商品 item商品 : mProductList) {
							for (字段描述 field字段描述 : mField字段描述s) {
								try {
									Field field = productClassType
											.getField(field字段描述.字段名);
									int fieldValue = field.getInt(item商品);
									if (fieldValue == slt产品选项.checkedDictId) {
										LogUtils.i("dict_select", field字段描述.字段名
												+ "--" + field字段描述.字段显示名
												+ "-->" + item商品.编号);
										if (productSetHashMap
												.get(field字段描述.字段名) == null) {
											productSetHashMap.put(
													field字段描述.字段名,
													new HashSet<Integer>());
										}

										// 记录选择项的id集合
										productSetHashMap.get(field字段描述.字段名)
												.add(item商品.编号);
									}
								} catch (NoSuchFieldException e) {
									e.printStackTrace();
									LogUtils.e(TAG, "" + e.getMessage());
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									LogUtils.e(TAG, "" + e.getMessage());
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									LogUtils.e(TAG, "" + e.getMessage());
								}
							}
						}
					} else {
						isAllSelect = false;
					}
				}

				/** 选中的下单商品 */
				Rad商品 mSelectedProduct;
				LogUtils.i("isAllSelect", isAllSelect + "");
				if (isAllSelect) {
					mSelectedProduct = RadProductBiz.getRelateProductInfo(
							productSetHashMap, mProductList);
					if (mSelectedProduct != null) {
						StringBuilder sBuilder = new StringBuilder("已选 ");
						for (Rad产品选项 item产品选项 : mList) {
							String fieldName = TextUtils
									.isEmpty(item产品选项.fieldDescribe.字段显示名) ? item产品选项.fieldDescribe.字段名
									: item产品选项.fieldDescribe.字段显示名;
							String fieldValue = item产品选项.dictionaries.get(
									item产品选项.checkedDictPos).getName();
							sBuilder.append(fieldName + ":" + fieldValue + ";");
						}
					} else {
						mSelectedProduct = null;
					}
				} else {
					mSelectedProduct = null;
				}

				if (mOnCheckedListener != null) {
					mOnCheckedListener.onChecked(mSelectedProduct);
				}
			}
		});
	}

	private OnCheckedListener mOnCheckedListener;

	public void setCheckedListener(OnCheckedListener onCheckedListener) {
		this.mOnCheckedListener = onCheckedListener;
	}

	/***
	 * 监听选中商品选项
	 * 
	 */
	public interface OnCheckedListener {
		void onChecked(Rad商品 selectedProduct);
	}
}
