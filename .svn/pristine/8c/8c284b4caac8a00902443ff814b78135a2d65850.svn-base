package com.cedarhd.utils;


/***
 * 地球经纬度计算工具类
 * 
 * @author K
 * 
 */
public class EarthMapUtils {
	/** 地球半径（单位：米） */
	private final static double EARTH_RADIUS = 6371393;

	/***
	 * 获取指定点在对应所在纬度线上每米对应的经度
	 */
	public static double getLongitudeDistance() {
		double yCircle = Math.PI * EARTH_RADIUS * 2; // 纬度线周长=地球周长
		// 一米 在对应的经度
		double distanceY = 1 / yCircle * 360;
		return distanceY;
	}

	/***
	 * 获取指定点在对应所在经线上每米对应的纬度
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 */
	public static double getLatitudeDistance(double latitude, double longitude) {
		double radLat = rad(latitude);
		// double radLong = rad(longitude - 90);
		double r = EARTH_RADIUS * Math.cos(radLat);
		double xCircle = Math.PI * r * 2; // 经度轴上周长
		// System.out.println("r=" + r);
		// System.out.println("xCircle=" + xCircle);
		// 一米 在对应的经度
		double distanceX = 1 / xCircle * 360;
		return distanceX;
	}

	/***
	 * 经纬度转化为角度
	 * 
	 * @param d
	 * @return
	 */
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/***
	 * 根据经纬度计算以该点为中心的一个正放心区域区域
	 * 
	 * @param latitude
	 *            纬度
	 * @param longitude
	 *            经度
	 * @param radius
	 *            中心到每条边的距离
	 * @return 两个经纬度构成一个矩形，格式如下(40.034722,116.350448,40.038722,116.354448)
	 */
	public static String getLocationRect(double latitude, double longitude,
			int radius) {
		// 左下角经纬度
		double leftLongitude = longitude - EarthMapUtils.getLongitudeDistance()
				* radius;
		double leftLatidude = latitude - EarthMapUtils.getLongitudeDistance()
				* radius;

		// 右上角经纬度
		double rightLongitude = longitude
				+ EarthMapUtils.getLongitudeDistance() * radius;
		double rightLatidude = latitude + EarthMapUtils.getLongitudeDistance()
				* radius;
		String rectLoaction = leftLatidude + "," + leftLongitude + ","
				+ rightLatidude + "," + rightLongitude;
		return rectLoaction;
	}

}
