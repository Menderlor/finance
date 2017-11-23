package com.cedarhd.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class 销售机会 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1285022850527128845L;
	@DatabaseField(generatedId = true, unique = true)
	public int _Id;
	@DatabaseField
	public int Id;

	@DatabaseField
	public int CustomerId;

	@DatabaseField
	public String CustomerName;

	@DatabaseField
	public String Contacts;

	@DatabaseField
	public String RegisterTime;

	@DatabaseField
	public int Salesman;

	@DatabaseField
	public int CorpId;

	@DatabaseField
	public int UserId;

	@DatabaseField
	public int Classification;

	@DatabaseField
	public String ClassificationName;

	@DatabaseField
	int Trade;

	@DatabaseField
	public String TradeName;

	@DatabaseField
	public String SalesmanName;

	@DatabaseField
	public String LastContactTime;

	@DatabaseField
	public String PlanContactTime;

	@DatabaseField
	public String Attachment;

	@DatabaseField
	public int ToContact;

	@DatabaseField
	public String ContactState;

	@DatabaseField
	int Stutus;

	@DatabaseField
	public String StutusName;

	@DatabaseField
	public int Province;

	@DatabaseField
	public String ProvinceName;

	@DatabaseField
	public int City;

	public String getEstimatedAmount() {
		return EstimatedAmount;
	}

	public void setEstimatedAmount(String estimatedAmount) {
		EstimatedAmount = estimatedAmount;
	}

	public String getActualAmount() {
		return ActualAmount;
	}

	public void setActualAmount(String actualAmount) {
		ActualAmount = actualAmount;
	}

	@DatabaseField
	public String CityName;

	@DatabaseField
	public String Phone;

	@DatabaseField
	public String Address;

	@DatabaseField
	public String UpdateTime;

	@DatabaseField
	public String Content;

	@DatabaseField
	public String Readed;

	@DatabaseField
	public String ReadTime;
	/**
	 * 预计金额
	 */
	@DatabaseField
	public String EstimatedAmount;

	/**
	 * 实际金额
	 */
	@DatabaseField
	private String ActualAmount;

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

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getContacts() {
		return Contacts;
	}

	public void setContacts(String contacts) {
		Contacts = contacts;
	}

	public String getRegisterTime() {
		return RegisterTime;
	}

	public void setRegisterTime(String registerTime) {
		RegisterTime = registerTime;
	}

	public int getSalesman() {
		return Salesman;
	}

	public void setSalesman(int salesman) {
		Salesman = salesman;
	}

	public int getCorpId() {
		return CorpId;
	}

	public void setCorpId(int corpId) {
		CorpId = corpId;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public int getClassification() {
		return Classification;
	}

	public void setClassification(int classification) {
		Classification = classification;
	}

	public String getClassificationName() {
		return ClassificationName;
	}

	public void setClassificationName(String classificationName) {
		ClassificationName = classificationName;
	}

	public int getTrade() {
		return Trade;
	}

	public void setTrade(int trade) {
		Trade = trade;
	}

	public String getTradeName() {
		return TradeName;
	}

	public void setTradeName(String tradeName) {
		TradeName = tradeName;
	}

	public String getSalesmanName() {
		return SalesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		SalesmanName = salesmanName;
	}

	public String getLastContactTime() {
		return LastContactTime;
	}

	public void setLastContactTime(String lastContactTime) {
		LastContactTime = lastContactTime;
	}

	public String getPlanContactTime() {
		return PlanContactTime;
	}

	public void setPlanContactTime(String planContactTime) {
		PlanContactTime = planContactTime;
	}

	public String getAttachment() {
		return Attachment;
	}

	public void setAttachment(String attachment) {
		Attachment = attachment;
	}

	public int getToContact() {
		return ToContact;
	}

	public void setToContact(int toContact) {
		ToContact = toContact;
	}

	public String getContactState() {
		return ContactState;
	}

	public void setContactState(String contactState) {
		ContactState = contactState;
	}

	public int getProvince() {
		return Province;
	}

	public void setProvince(int province) {
		Province = province;
	}

	public String getProvinceName() {
		return ProvinceName;
	}

	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}

	public int getCity() {
		return City;
	}

	public void setCity(int city) {
		City = city;
	}

	public String getCityName() {
		return CityName;
	}

	public void setCityName(String cityName) {
		CityName = cityName;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(String updateTime) {
		UpdateTime = updateTime;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getRead() {
		return Readed;
	}

	public void setRead(String read) {
		Readed = read;
	}

}
