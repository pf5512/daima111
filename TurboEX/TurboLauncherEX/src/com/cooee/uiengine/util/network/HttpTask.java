/**
 * 
 */
package com.cooee.uiengine.util.network;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import android.util.Log;

/**
 * @author zhongqihong
 * 
 *         任务调度
 */
public class HttpTask implements Callable<Void> {
	HttpDiscriptor httpDisc;
	HttpAsync httpAsync;
	ExecutorService threadPool;
	Future<Void> loadFuture = null;
	volatile boolean asyncDone = false;
	boolean cancel = false;
	boolean completed = false;
	Object result = null;

	public HttpTask(HttpDiscriptor httpDisc, HttpAsync httpAsync,
			ExecutorService threadPool) {
		this.httpDisc = httpDisc;
		this.httpAsync = httpAsync;
		this.threadPool = threadPool;
	}

	@Override
	public Void call() throws Exception {

		if (!asyncDone) {
			httpAsync.loadAsync(httpDisc);
			asyncDone = true;
		}

		return null;
	}

	public boolean update() {
		if (completed)
			return true;
		handleAsync();
		return completed;
	}

	public void handleAsync() {
		if (loadFuture == null && !asyncDone) {
			Log.v("retrieve", "update :start ");
			loadFuture = threadPool.submit(this);
		} else {
			if (asyncDone) {
				Log.v("retrieve", "update :second ");
				result = httpAsync.resolve(httpDisc);
				completed = true;
			} else if (loadFuture.isDone()) {
				try {
					loadFuture.get();
				} catch (Exception e) {
					throw new RuntimeException(
							"network error! logged by zhongqihong");
				}
				Log.v("retrieve", "update :third ");
				completed = true;
				result = httpAsync.resolve(httpDisc);
			}
		}
	}

	public Object getResult() {
		return result;
	}
}
