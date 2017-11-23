package com.cedarhd.helpers.server;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.cedarhd.helpers.BaiduLocator;
import com.cedarhd.utils.LogUtils;

/**
 * 百度定位帮助类（封装定位）
 * 
 * @author KJX
 * 
 */
public class BDLocationHelper implements BDLocationListener {
	private final String TAG = "BDLocationHelper";
	private Context context;
	public static String mLocation;
	public static double mLatitude;
	public static double mLongitude;
	private EditText mTextViewLocation;

	private OnReceivedLocationListerner onLocationListerner;

	/**
	 * @param context
	 * @param mTextViewLocation
	 *            要显示定位地址文本控件
	 */
	public BDLocationHelper(Context context, EditText mTextViewLocation) {
		this.context = context;
		this.mTextViewLocation = mTextViewLocation;
		requestLocating(context);
	}

	/**
	 * @param context
	 * @param mTextViewLocation
	 *            要显示定位地址文本控件
	 */
	public BDLocationHelper(Context context) {
		this.context = context;
		requestLocating(context);
	}

	/**
	 * 启动定位
	 * 
	 * @param context
	 */
	private void requestLocating(Context context) {
		try {
			BaiduLocator.requestLocation(context, this);
		} catch (Exception e) {
			LogUtils.i(TAG, "" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 开始定位
	 */
	public void startLocating() {
		requestLocating(context);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null) {
			LogUtils.i("BDLocationListener", "onReceiveLocation is null");
		} else {
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			mLatitude = location.getLatitude();
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			mLongitude = location.getLongitude();
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			Log.v("BDLocationListener", sb.toString());
			if (location.getLocType() == BDLocation.TypeNetWorkException) {
				CharSequence text = "需要连接到3G或者wifi因特网！";
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				BaiduLocator.stop();
			}
			mLocation = location.getAddrStr();
			if (mTextViewLocation != null) {
				mTextViewLocation.setText(mLocation);
			}
			// TODO
			Log.e("BDLocationListener", sb.toString());
			if (onLocationListerner != null) {
				onLocationListerner
						.onReceived(mLocation, mLongitude, mLatitude);
			}
			BaiduLocator.stop();
		}
	}

	/**
	 * 监听定位方法
	 * 
	 * @param onLocationListerner
	 */
	public void setOnReceivedLocationListener(
			OnReceivedLocationListerner onLocationListerner) {
		this.onLocationListerner = onLocationListerner;
	}

	public interface OnReceivedLocationListerner {
		/**
		 * 
		 * @param mLoc
		 *            地址
		 * @param mLong
		 *            经度
		 * @param mLati
		 *            纬度
		 */
		public abstract void onReceived(String mLoc, double mLong, double mLati);
	}
}
