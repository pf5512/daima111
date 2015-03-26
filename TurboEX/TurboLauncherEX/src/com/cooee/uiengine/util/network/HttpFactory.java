package com.cooee.uiengine.util.network;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.util.Log;

import com.cooee.uiengine.util.network.HttpDiscriptor.CONTENT_TYPE;
import com.cooee.uiengine.util.network.HttpDiscriptor.REQUEST_TYPE;

/**
 * 
 * @author Zhongqihong
 * 
 *         网络连接的对外接口
 * 
 */
public class HttpFactory {

	public static Object lock = new Object();
	final String TAG = "HttpFactory";
	int loaded = 0;
	int toLoad = 0;
	final ExecutorService threadPool;
	LinkedList<HttpDiscriptor> loadQueue;
	Stack<HttpTask> tasks;
	public static HttpFactory instance;
	public static int threadId = 0;

	public static HttpFactory getInstance() {
		synchronized (lock) {
			if (instance == null) {
				synchronized (lock) {
					instance = new HttpFactory();
				}
			}
		}
		return instance;
	}

	private HttpFactory() {
		tasks = new Stack<HttpTask>();
		loadQueue = new LinkedList<HttpDiscriptor>();
		threadPool = Executors.newFixedThreadPool(3, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				threadId++;
				Thread thread = new Thread(r, "HttpFactory-Thread-" + threadId);
				thread.setDaemon(true);
				Log.v(TAG, "yield a new thread : " + "HttpFactory-Thread-"
						+ threadId);
				return thread;
			}
		});
	}

	public void upload(String key, String url, String content,
			CONTENT_TYPE contType, REQUEST_TYPE reqType) {
		HttpDiscriptor httpDisc = new HttpDiscriptor(key, contType, reqType,
				url, content);
		upload(httpDisc);
	}

	public void upload(HttpDiscriptor httpDisc) {
		verify(httpDisc.getKey());
		if (loadQueue.size() == 0) {
			loaded = 0;
			toLoad = 0;
		}
		loadQueue.add(httpDisc);
		toLoad++;
	}

	public boolean verify(String key) {
		if (key != null && !key.equals(" ")) {
			for (int i = 0; i < loadQueue.size(); i++) {
				HttpDiscriptor httpDis = loadQueue.get(i);
				if (httpDis.getKey() != null && !httpDis.getKey().equals(" ")) {
					if (httpDis.getKey().equals(key)) {
						throw new RuntimeException(
								"this task has already been requested");
					}
				}
			}
		}
		return true;
	}

	public void nextTask() {
		HttpDiscriptor httpDis = loadQueue.remove(0);
		addTask(httpDis);
	}

	public void addTask(HttpDiscriptor httpDis) {
		tasks.push(new HttpTask(httpDis, httpReslover(httpDis), threadPool));
	}

	public synchronized boolean update() {
		if (tasks.size() == 0) {
			// loop until we have a new task ready to be processed
			while (loadQueue.size() != 0 && tasks.size() == 0) {
				nextTask();
			}
			// have we not found a task? We are done!
			if (tasks.size() == 0)
				return true;
		}
		return updateTask() && loadQueue.size() == 0 && tasks.size() == 0;

	}

	public boolean updateTask() {
		HttpTask task = tasks.peek();
		if (task.update()) {
			if (tasks.size() == 1)
				loaded++;
			tasks.pop();
			if (task.cancel) {
				unload(task);
			}
			return true;
		}
		return false;
	}

	public synchronized void unload(HttpTask task) {
	}

	public HttpAsync httpReslover(HttpDiscriptor httpDis) {
		HttpAsync httpAsync = null;
		if (httpDis == null || httpDis.getContType() == null) {
			throw new RuntimeException(
					TAG
							+ " you have to specify a type{#CONTENT_TYPE} for your request");
		}
		switch (httpDis.getContType()) {
		case CONTENT_TEXT:
			httpAsync = new HttpText();
			break;
		case CONTENT_MD5:
			httpAsync = new HttpMD5();
			break;
		}
		return httpAsync;
	}

	public synchronized void start() {
		while (!update()) {
		}
		release();
	}

	public synchronized void release() {
		loadQueue.clear();
		tasks.clear();
		toLoad = 0;
		loaded = 0;
		threadId = 0;
	}
}
