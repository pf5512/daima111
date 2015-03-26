package com.cooee.uiengine.util.network;

import com.cooee.uiengine.util.network.HttpAsync.HttpAsyncListener;
import com.cooee.uiengine.util.network.HttpAsync.HttpResolveListener;

/**
 * 
 * @author Zhongqihong
 * 
 *         对网络连接的描述
 */

public class HttpDiscriptor {
	public enum CONTENT_TYPE {
		CONTENT_TEXT, CONTENT_MD5,
	}

	public enum REQUEST_TYPE {
		REQUEST_POST, REQUEST_GET
	}

	private CONTENT_TYPE contType;

	private REQUEST_TYPE reqType;

	private String key;

	private String url;

	private String content;

	private String md5_key;

	private Object result;

	private HttpResolveListener httpRosolveListener;

	private HttpAsyncListener httpRequestListener;

	public HttpDiscriptor() {

	}

	public HttpDiscriptor(String key, CONTENT_TYPE contType,
			REQUEST_TYPE reqType, String url, String content) {
		this.key = key;
		this.contType = contType;
		this.reqType = reqType;
		this.url = url;
		this.content = content;
	}

	public CONTENT_TYPE getContType() {
		return contType;
	}

	public void setContType(CONTENT_TYPE contType) {
		this.contType = contType;
	}

	public REQUEST_TYPE getReqType() {
		return reqType;
	}

	public void setReqType(REQUEST_TYPE reqType) {
		this.reqType = reqType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMd5_key() {
		return md5_key;
	}

	public void setMd5_key(String md5_key) {
		this.md5_key = md5_key;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public HttpResolveListener getHttpResolveListener() {
		return httpRosolveListener;
	}

	public void setHttpRosloveListener(HttpResolveListener httpRosolveListener) {
		this.httpRosolveListener = httpRosolveListener;
	}

	public HttpAsyncListener getHttpRequestListener() {
		return httpRequestListener;
	}

	public void setHttpRequestListener(HttpAsyncListener httpRequestListener) {
		this.httpRequestListener = httpRequestListener;
	}

}
