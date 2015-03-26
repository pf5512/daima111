package com.cooee.uiengine.util.network;

/**
 * 
 * @author Zhongqihong
 * 
 * 
 *         访问方式和数据处理的接口
 */
public interface HttpAsync {

	public void loadAsync(HttpDiscriptor httpDisc);

	public Object resolve(HttpDiscriptor httpDisc);

	public static interface HttpResolveListener {

		public Object complete(Object result);

	}

	public static interface HttpAsyncListener {

		public void request(Object obj);

	}

}
