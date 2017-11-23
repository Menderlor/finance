package com.cedarhd.models;

import java.io.Serializable;

/***
 * 百度地图定位地址
 * 
 * @author K
 * 
 */
public class BaiduPlace implements Serializable {
	/**
	 */
	private static final long serialVersionUID = 8222032401928737469L;
	public String name;
	public String address;
	public Location location;

	private double distance;

	public class Location implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6297717555828255574L;
		public double lat;
		public double lng;
	}

	/** 获得距离和指定经纬度的距离，用于排序 */
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	// "status":0,
	// "message":"ok",
	// "results":[
	// {
	// "name":"朗丽兹西山花园酒店",
	// "location":{
	// "lat":40.073375,
	// "lng":116.254752
	// },
	// "address":"海淀区丰智东路13号",
	// "street_id":"c5c0c2010205f2dd0c0e6b45",
	// "telephone":"(010)82868888",
	// "detail":1,
	// "uid":"c5c0c2010205f2dd0c0e6b45"
	// },

}
