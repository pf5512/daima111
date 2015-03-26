package com.cooee.uiengine.util.network;

import com.cooee.uiengine.util.network.HttpDiscriptor.REQUEST_TYPE;

public class HttpText extends HttpAsyncBiz {

	public void loadAsync(HttpDiscriptor httpDisc) {

		if (httpDisc.getReqType() == REQUEST_TYPE.REQUEST_POST) {
			post(httpDisc);
		} else
			super.loadAsync(httpDisc);

	}

	public void get(HttpDiscriptor httpDisc) {

	}

	public void post(HttpDiscriptor httpDisc) {

	}

	@Override
	public Object resolve(HttpDiscriptor httpDisc) {

		return super.resolve(httpDisc);

	}
}
