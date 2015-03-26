package com.cooee.uiengine.util.network;

/**
 * 
 * @author Zhongqihong
 * 
 *         这是一个测试应用
 * 
 */

public class HttpAsyncBiz implements HttpAsync {
	private final int REL_OK = 1;

	@Override
	public void loadAsync(HttpDiscriptor httpDisc) {

		if (httpDisc.getHttpRequestListener() != null)
			httpDisc.getHttpRequestListener().request(httpDisc);

	}

	@Override
	public Object resolve(HttpDiscriptor httpDisc) {
		if (httpDisc.getHttpResolveListener() != null)
			return httpDisc.getHttpResolveListener().complete(httpDisc);
		else
			return REL_OK;
	}

}
