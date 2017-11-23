package com.cedarhd.utils.okhttp;

import com.squareup.okhttp.Request;

/*** 请求响应回调接口 */
public interface StringResponseCallBack {
	/**
	 * 请求成功回调函数
	 * 
	 * @param response
	 *            响应结果：服务器返回的Json字符串
	 */
	abstract void onResponse(String response);

	/***
	 * 访问网络失败，请求失败回调函数
	 * 
	 * @param response
	 */
	abstract void onFailure(Request request, Exception ex);

	/**
	 * 服务器返回状态码错误，访问服务器成功，执行操作失败
	 * 
	 * @param result
	 *            响应结果
	 */
	abstract void onResponseCodeErro(String result);

}