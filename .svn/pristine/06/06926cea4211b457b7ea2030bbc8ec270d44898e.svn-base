package com.cedarhd.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cedarhd.R;
import com.cedarhd.models.Dict;
import com.cedarhd.models.Province;
import com.cedarhd.models.SelectedProvince;
import com.cedarhd.utils.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

/***
 * 底部城市选择
 * 
 * @author K 2015/10/01 10:10
 */
public class CityPicker {
	private static CityPicker mCityPicker;
	private Context mContext;
	private Dialog dialog;
	private OnCheckedListener mListener;

	/** 省市县数据源集合 */
	private Province mProvince;

	private List<Dict> mProvinceDicts = new ArrayList<Dict>();
	private List<Dict> mCityDicts = new ArrayList<Dict>();
	private List<Dict> mCountryDicts = new ArrayList<Dict>();

	/** 选中省市县实体 */
	private SelectedProvince mSelectedCity;

	private WheelView wheelProvince;
	private WheelView wheelCity;
	private WheelView wheelCounty;
	private TextView tvAddress;

	/**
	 * @param mContext
	 */
	private CityPicker(Context mContext) {
		this.mContext = mContext;
		mSelectedCity = new SelectedProvince();
	}

	public static CityPicker getInstance(Context mContext) {
		if (mCityPicker == null) {
			mCityPicker = new CityPicker(mContext);
		}
		return mCityPicker;
	}

	/**
	 * 弹出IOS风格的底部字典选择
	 * 
	 */
	public void show() {
		View view = View.inflate(mContext, R.layout.pop_city_picker, null);
		dialog = new Dialog(mContext, R.style.styleNoFrameDialog);
		initViews(view);
		dialog.setContentView(view);
		dialog.show();

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// lp.width = (int) (ViewHelper.getScreenWidth(mContext)); // 设置宽度
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		dialog.getWindow().setAttributes(lp);
		parseProvince();
	}

	private void initViews(View view) {
		View top = (View) view.findViewById(R.id.top_city_picker);
		Button btnSure = (Button) view.findViewById(R.id.btn_sure_city_picker);
		tvAddress = (TextView) view.findViewById(R.id.tv_address_city_picker);
		wheelProvince = (WheelView) view.findViewById(R.id.wheel_province);
		wheelCity = (WheelView) view.findViewById(R.id.wheel_city);
		wheelCounty = (WheelView) view.findViewById(R.id.wheel_county);

		// 默认显示个数为5
		wheelProvince.setVisibleItems(5);
		wheelCity.setVisibleItems(5);
		wheelCounty.setVisibleItems(5);

		mProvinceDicts.add(new Dict(0, "初次载入"));
		mCityDicts.add(new Dict(0, "加载中.."));
		mCountryDicts.add(new Dict(0, "加载中.."));
		wheelProvince.setViewAdapter(new CountryAdapter(mContext,
				mProvinceDicts));
		wheelCity.setViewAdapter(new CountryAdapter(mContext, mCityDicts));
		wheelCounty.setViewAdapter(new CountryAdapter(mContext, mCountryDicts));

		top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					dialog.dismiss();
					mListener.onChecked(mSelectedCity);
				}
			}
		});

		wheelProvince.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Dict dict = mProvinceDicts.get(newValue); // 选中省
				mSelectedCity.省 = dict;
				Log.i("selectA", "省：" + dict.名称);
				updateCites(dict.编号);
				if (mCityDicts != null && mCityDicts.size() > 0) {
					updateCountry(mCityDicts.get(0).编号);
				} else {
					wheelCounty.setViewAdapter(new CountryAdapter(mContext,
							new ArrayList<Dict>()));
				}
			}
		});

		wheelCity.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Dict dict = mCityDicts.get(newValue); // 选中市
				mSelectedCity.市 = dict;
				updateCountry(dict.编号);
				Log.i("selectA", "市：" + dict.名称);
			}
		});

		wheelCounty.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (mCountryDicts != null && mCountryDicts.size() > 0) {
					Dict dict = mCountryDicts.get(newValue); // 选中市
					mSelectedCity.县 = dict;
					Log.i("selectA", "县：" + dict.名称);
				} else {
					mSelectedCity.县 = new Dict();
				}
				tvAddress.setText(mSelectedCity.省.名称 + " " + mSelectedCity.市.名称
						+ " " + mSelectedCity.县.名称);
			}
		});
	}

	private void bindData() {
		if (mProvince != null && mProvince.省 != null && mProvince.省.size() > 0) {

			wheelProvince.setViewAdapter(new CountryAdapter(mContext,
					mProvince.省));
			wheelProvince.setCurrentItem(0);
			mSelectedCity.省 = mProvince.省.get(0);

			updateCites(mProvinceDicts.get(0).编号);
			if (mCityDicts != null && mCityDicts.size() > 0) {
				updateCountry(mCityDicts.get(0).编号);
			}
		}
	}

	/** 解析省市县字典集合 */
	private void parseProvince() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream in = mContext.getAssets().open("province.txt");
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = in.read(b)) != -1) {
						out.write(b, 0, len);
					}
					String provStr = out.toString("utf-8");
					Log.d("province", provStr);
					List<Province> lProvinces = JsonUtils.ConvertJsonToList(
							provStr, Province.class);
					if (lProvinces != null && lProvinces.size() > 0) {
						mProvince = lProvinces.get(0);
						mProvinceDicts = mProvince.省;

						handler.sendEmptyMessage(CEDE_PARESE_COMPLETED);
						// ((Activity) mContext).runOnUiThread(new Runnable() {
						// @Override
						// public void run() {
						// bindData();
						// }
						// });

						// bindData();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private List<Dict> getListByParentId(int parentId, List<Dict> rsList) {
		List<Dict> list = new ArrayList<Dict>();
		for (Dict dict : rsList) {
			if (dict.上级字典 == parentId) {
				list.add(dict);
			}
		}
		return list;
	}

	/** 上级字典编号 */
	private void updateCountry(int parentId) {
		mCountryDicts = getListByParentId(parentId, mProvince.县);
		wheelCounty.setViewAdapter(new CountryAdapter(mContext, mCountryDicts));
		wheelCounty.setCurrentItem(0);
		// mSelectedCity.县 = mCountryDicts.get(0);
		setCountryDict();
		tvAddress.setText(mSelectedCity.省.名称 + " " + mSelectedCity.市.名称 + " "
				+ mSelectedCity.县.名称);
	}

	private void setCountryDict() {
		if (mCountryDicts != null && mCountryDicts.size() > 0) {
			Dict dict = mCountryDicts.get(0); // 选中市
			mSelectedCity.县 = dict;
			Log.i("selectA", "县：" + dict.名称);
		} else {
			mSelectedCity.县 = new Dict();
		}
	}

	/** 上级字典编号 */
	private void updateCites(int parentId) {
		mCityDicts = getListByParentId(parentId, mProvince.市);
		wheelCity.setViewAdapter(new CountryAdapter(mContext, mCityDicts));
		wheelCity.setCurrentItem(0);

		if (mCityDicts != null && mCityDicts.size() > 0) {
			mSelectedCity.市 = mCityDicts.get(0);
		} else {
			mSelectedCity.市 = new Dict();
		}
	}

	private class CountryAdapter extends AbstractWheelTextAdapter {
		private List<Dict> countries;

		/**
		 * Constructor
		 */
		protected CountryAdapter(Context context, List<Dict> countries) {
			super(context, R.layout.country_layout, NO_RESOURCE);
			this.countries = countries;
			setItemTextResource(R.id.country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries.get(index).名称;
		}

	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void setBackgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
				.getAttributes();
		lp.alpha = bgAlpha;// 0.0-1.0
		((Activity) mContext).getWindow().setAttributes(lp);
	}

	public void setOnCheckedListener(OnCheckedListener onSelectedListener) {
		this.mListener = onSelectedListener;
	}

	public interface OnCheckedListener {
		/** 监听选中字典集合的序号 */
		void onChecked(SelectedProvince selectedCity);
	}

	private final int CEDE_PARESE_COMPLETED = 19;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CEDE_PARESE_COMPLETED:
				bindData();
				break;
			default:
				break;
			}
		};
	};
}
