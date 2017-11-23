package com.cedarhd.helpers;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cedarhd.utils.LogUtils;

public class BaiduLocator {
	static LocationClient mLocationClient = null;

	public static void requestLocation(Context context,
			BDLocationListener listner) throws Exception {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}

		mLocationClient = new LocationClient(context);
		// mLocationClient.setAK("HOaT2u0VLy0i2O06zb956dWM");
		mLocationClient.registerLocationListener(listner);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		// option.disableCache(true);// 禁止启用缓存定位
		// option.setPoiNumber(0); // 最多返回POI个数
		// option.setPoiDistance(100); // poi查询距离
		// option.setPoiExtraInfo(false); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			LogUtils.d("LocSDK3", "locClient is null or not started");

	}

	public static void stop() {
		if (mLocationClient != null)
			mLocationClient.stop();
	}

	public static boolean startedOrNot() {
		if (mLocationClient != null) {
			return mLocationClient.isStarted();
		}
		return false;
	}
}
