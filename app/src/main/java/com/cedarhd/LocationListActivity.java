package com.cedarhd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarhd.base.BaseActivity;
import com.cedarhd.base.BoeryunViewHolder;
import com.cedarhd.base.CommanAdapter;
import com.cedarhd.helpers.ProgressDialogHelper;
import com.cedarhd.helpers.server.BDLocationHelper;
import com.cedarhd.helpers.server.BDLocationHelper.OnReceivedLocationListerner;
import com.cedarhd.models.BaiduPlace;
import com.cedarhd.utils.EarthMapUtils;
import com.cedarhd.utils.HttpUtils;
import com.cedarhd.utils.JsonUtils;
import com.cedarhd.utils.LogUtils;

import org.json.JSONObject;

import java.util.List;

/***
 * 定位 地址列表选择页面
 * 
 * @author K
 * 
 */
public class LocationListActivity extends BaseActivity {

	public static final String LONGITUDE = "LocationLongitude";
	public static final String LATITUDE = "LocationLatitude";
	public static final String RESULT = "LocationResult";

	private final int SUCCESS_GET_LOCATION_LIST = 1;
	private final int FAILURE_GET_LOCATION_LIST = 2;

	private int radiusMap = 100;// 默认区域半径

	private Context mContext;
	private BDLocationHelper mLocationHelper;
	private HttpUtils mHttpUtils;
	private List<BaiduPlace> mList;

	private ImageView ivBack;
	private ListView lv;
	private TextView tvSort;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCESS_GET_LOCATION_LIST:
				ProgressDialogHelper.dismiss();
				lv.setAdapter(getAdapter(mList));
				break;
			case FAILURE_GET_LOCATION_LIST:
				ProgressDialogHelper.dismiss();
				Toast.makeText(mContext, "定位失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_list);

		initViews();
		init();
		setEventsListener();
	}

	private double mLatitude;
	private double mLongitude;

	private void init() {
		mContext = this;
		mLocationHelper = new BDLocationHelper(mContext);
		mHttpUtils = new HttpUtils();
		mLatitude = getIntent().getDoubleExtra(LATITUDE, 0);
		mLongitude = getIntent().getDoubleExtra(LONGITUDE, 0);

		if (mLatitude == 0 || mLongitude == 0) {
			// 如果经纬度为空，则重新定位
			mLocationHelper
					.setOnReceivedLocationListener(new OnReceivedLocationListerner() {
						@Override
						public void onReceived(String mLoc, double mLong,
								double mLati) {
							LogUtils.i(TAG, mLoc + "--longitude:" + mLong
									+ "--lati" + mLati);
							reloadLocationList(mLong, mLati);
						}
					});
		} else {
			reloadLocationList(mLongitude, mLatitude);
		}
	}

	private void initViews() {
		ivBack = (ImageView) findViewById(R.id.iv_back_loaction_list);
		tvSort = (TextView) findViewById(R.id.tv_sort_location_list);
		lv = (ListView) findViewById(R.id.lv_locationlist);
	}

	private void setEventsListener() {
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvSort.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (radiusMap <= 100) {
					radiusMap = 200;
					tvSort.setText("200米内");
				} else {
					radiusMap = 100;
					tvSort.setText("100米内");
				}
				reloadLocationList(mLongitude, mLatitude);
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BaiduPlace place = mList.get(position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(RESULT, place);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void getLocationList(final String url) {
		ProgressDialogHelper.show(mContext, "定位中..");
		LogUtils.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result = mHttpUtils.httpGet(url);
				LogUtils.i(TAG, result);
				try {
					JSONObject jo = new JSONObject(result);
					int status = jo.getInt("status");
					String message = jo.getString("message");
					String results = jo.getString("results");
					if (status == 0 && "ok".equals(message)) {
						mList = JsonUtils.pareseJsonToList(results,
								BaiduPlace.class);
						LogUtils.i(TAG, "地址个数" + mList.size());
						handler.sendEmptyMessage(SUCCESS_GET_LOCATION_LIST);
					} else {
						handler.sendEmptyMessage(FAILURE_GET_LOCATION_LIST);
					}
				} catch (Exception e) {
					LogUtils.e(TAG, "" + e.getMessage());
					handler.sendEmptyMessage(FAILURE_GET_LOCATION_LIST);
				}
			}
		}).start();
	}

	private CommanAdapter<BaiduPlace> getAdapter(final List<BaiduPlace> list) {
		return new CommanAdapter<BaiduPlace>(list, mContext,
				R.layout.item_baiduplace) {
			@Override
			public void convert(int position, BaiduPlace item,
					BoeryunViewHolder viewHolder) {

				TextView tvName = viewHolder
						.getView(R.id.tv_name_location_item);
				TextView tvAddress = viewHolder
						.getView(R.id.tv_address_location_item);
				tvName.setText(item.name + "");
				tvAddress.setText(item.address + "");
			}
		};
	}

	/** 重新加载地址 */
	private void reloadLocationList(double mLong, double mLati) {
		String locationRect = EarthMapUtils.getLocationRect(mLati, mLong,
				radiusMap);
		String url = "http://api.map.baidu.com/place/v2/search?query=楼$酒店$大厦$公司$小区$中心$公交$银行$学校$街道$路&bounds="
				+ locationRect
				+ "&output=json&ak=60d1e3a7095dd79b5cf2b58a5a1aaaa8";
		getLocationList(url);
	}
}
