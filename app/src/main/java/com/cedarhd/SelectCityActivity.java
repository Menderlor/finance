package com.cedarhd;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cedarhd.base.BaseActivity;
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
 * 省市县三级联动 城市选择器
 * 
 * @author K
 * 
 */
public class SelectCityActivity extends BaseActivity {
	private Context mContext;
	/** 省市县数据源集合 */
	private Province mProvince;

	private List<Dict> mProvinceDicts;
	private List<Dict> mCityDicts;
	private List<Dict> mCountryDicts;

	/** 选中省市县实体 */
	private SelectedProvince mSelectedCity;

	private WheelView wheelProvince;
	private WheelView wheelCity;
	private WheelView wheelCounty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selector);
		initData();
		parseProvince();
		intView();
	}

	private void initData() {
		mContext = this;
		mSelectedCity = new SelectedProvince();
	}

	private void intView() {
		wheelProvince = (WheelView) findViewById(R.id.wheel_province);
		wheelCity = (WheelView) findViewById(R.id.wheel_city);
		wheelCounty = (WheelView) findViewById(R.id.wheel_county);

		// 默认显示个数为5
		wheelProvince.setVisibleItems(5);
		wheelCity.setVisibleItems(5);
		wheelCounty.setVisibleItems(5);

		if (mProvince != null) {
			wheelProvince.setViewAdapter(new CountryAdapter(mContext,
					mProvinceDicts));
			mSelectedCity.省 = mProvince.省.get(0);

			updateCites(mProvinceDicts.get(0).编号);

			if (mCityDicts != null && mCityDicts.size() > 0) {
				updateCountry(mCityDicts.get(0).编号);
			}
		}

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
			}
		});
	}

	/** 解析省市县字典集合 */
	private void parseProvince() {
		try {
			InputStream in = getAssets().open("province.txt");
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			String provStr = out.toString("utf-8");
			Log.d("province", provStr);
			List<Province> lProvinces = JsonUtils.ConvertJsonToList(provStr,
					Province.class);
			if (lProvinces != null && lProvinces.size() > 0) {
				mProvince = lProvinces.get(0);
				mProvinceDicts = mProvince.省;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	/** 根据省、市、县名称 反向设置滚轮显示地址 */
	private void setProvince(String prov, String city, String country) {

	}

	/** 上级字典编号 */
	private void updateCountry(int parentId) {
		mCountryDicts = getListByParentId(parentId, mProvince.县);
		wheelCounty.setViewAdapter(new CountryAdapter(mContext, mCountryDicts));
		wheelCounty.setCurrentItem(0);

		setCountryDict();
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
}
