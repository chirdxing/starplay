package com.star.common.utils.http;

import java.util.HashMap;

import org.apache.http.Header;

/**
 * http请求返回结果封装类
 * @date 2020年1月7日
 * @version 1.0
 */
public class HttpResult {
	
	// http响应状态码
	private int statusCode;
	
	// http响应头
	private HashMap<String, Header> headers;
	
	// http响应内容字符串
	private String response;
	
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public HashMap<String, Header> getHeaders() {
		return headers;
	}
	public void setHeaders(Header[] headers) {
		this.headers = new HashMap<String, Header>();
		for (Header header : headers) {
			this.headers.put(header.getName(), header);
		}
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
}
