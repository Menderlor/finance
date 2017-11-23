package com.cedarhd.control;

/**
 * Item definition including the section.
 */
public class SectionListItem {
	// public Object item;
	public String Id;
	public String customerName;
	public String content;
	public String classificationName;
	public String contacts;
	public String section;
	public String status;
	public String statusId;
	public String phone;

	public SectionListItem(final String id, final String customerNameParam,
			final String contentParam, final String classificationName,
			final String contacts, final String section, String status,
			String statusId, String phone) {
		super();
		this.Id = id;
		this.customerName = customerNameParam;
		this.content = contentParam;
		this.classificationName = classificationName;
		this.contacts = contacts;
		this.section = section;
		this.status = status;
		this.statusId = statusId;
		this.phone = phone;
	}

	// @Override
	// public String toString() {
	// return item.toString();
	// }

}
