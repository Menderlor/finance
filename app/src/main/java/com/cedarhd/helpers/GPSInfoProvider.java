package com.cedarhd.helpers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

//本类用于获得我的位置（地理信息）
//使用方法：String add=GPSInfoProvider.getInstance(getApplicationContext()).getLocation();
//string为返回的地理位置信息

//获得未进行转换的经纬度的方法如下：
//String=GPSInfoProvider.getInstance(getApplicationContext()).getLatiandLong()
//String为经纬度信息

public class GPSInfoProvider {

	// 保证当前GPSInfoProvider 只能有一个实例存在
	private static GPSInfoProvider mGPSInfoProvider;
	private static Context mContext;
	private static LocationManager lm;
	Handler handler;
	int classifi;
	public LocationManager locationManager;

	private GPSInfoProvider() {
	};

	public synchronized static GPSInfoProvider getInstance(Context context) {
		if (mGPSInfoProvider == null) {
			mGPSInfoProvider = new GPSInfoProvider();
			mContext = context;
		}
		return mGPSInfoProvider;
	}

	public String getLatiandLong() {
		String latitude = mContext.getSharedPreferences("config",
				Context.MODE_PRIVATE).getString("lastlocationlati", "");
		String longtitude = mContext.getSharedPreferences("config",
				Context.MODE_PRIVATE).getString("lastlocationlong", "");
		return latitude + longtitude;
	}

	/**
	 * 获取手机最后请求到的位置
	 */
	public void getLocation(Handler handler, int classifi) {
		this.handler = handler;
		this.classifi = classifi;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) mContext
				.getSystemService(serviceName);
		// String provider = LocationManager.GPS_PROVIDER;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		// Location location =
		// locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		// updateWithNewLocation(location);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
	}

	public LocationListener locationListener = new LocationListener() {

		/**
		 * 当手机位置发生改变的时候 调用
		 */
		public void onLocationChanged(Location location) {
			location.getAccuracy();
			// String latitude =Double.toString(location.getLatitude()); // 纬度
			// String longitude =Double.toString(location.getLongitude()); // 经度
			Bundle bundle = new Bundle();
			bundle.putDouble("Latitude", location.getLatitude());
			bundle.putDouble("Longitude", location.getLongitude());
			Message msg = new Message();
			msg.what = classifi;
			msg.setData(bundle);
			handler.sendMessage(msg);
			// SharedPreferences sp =mContext.getSharedPreferences("config",
			// Context.MODE_PRIVATE);
			// Editor editor =sp.edit();
			// editor.putString("lastlocationlati", Latitude);
			// editor.putString("lastlocationlong", Longitude);
			// editor.commit();
		}

		/**
		 * 当某个位置提供者的状态发生改变的时候
		 */
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		/**
		 * 某一个设备被打开的时候
		 */
		public void onProviderEnabled(String provider) {

		}

		/**
		 * 某一个设备被关闭的时候
		 */
		public void onProviderDisabled(String provider) {

		}

	};

}
