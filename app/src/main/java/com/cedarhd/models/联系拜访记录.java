package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 联系拜访记录 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2939119017286321671L;

	@DatabaseField(generatedId = true, unique = true)
	int _Id;

	@DatabaseField
	int Id;

	@DatabaseField
	int CustomerId;

	@DatabaseField
	int Saler;

	@DatabaseField
	int SalesChance;

	@DatabaseField
	String Time;

	@DatabaseField
	String CustomerName;

	@DatabaseField
	String SalesmanName;

	@DatabaseField
	String Address;

	@DatabaseField
	double Latitude;

	@DatabaseField
	double Longitude;

	public int get_Id() {
		return _Id;
	}

	public void set_Id(int _Id) {
		this._Id = _Id;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(int customerId) {
		CustomerId = customerId;
	}

	public int getSalesman() {
		return Saler;
	}

	public void setSalesman(int salesman) {
		Saler = salesman;
	}

	public int getSalesChance() {
		return SalesChance;
	}

	public void setSalesChance(int salesChance) {
		SalesChance = salesChance;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getSalesmanName() {
		return SalesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		SalesmanName = salesmanName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
}
