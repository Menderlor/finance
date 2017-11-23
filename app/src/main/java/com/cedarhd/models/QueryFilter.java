package com.cedarhd.models;

/***
 * 列表过滤条件
 * 
 * @author K
 * 
 */
public class QueryFilter {
	public int userId;
	public String userHint;
	public String userName;

	public int clientId;
	public String clientHint;
	public String clientName;

	public String startTime;
	public String endTime;

	public QueryFilter() {
		this("业务员", "客户");
	}

	public QueryFilter(String userHint, String clientHint) {
		super();
		this.userHint = userHint;
		this.clientHint = clientHint;
	}
}
