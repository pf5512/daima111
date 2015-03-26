package com.cooee.launcher.appcollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cooee.uiengine.util.network.HttpAsync;
import com.cooee.uiengine.util.network.HttpDiscriptor;
import com.cooee.uiengine.util.network.HttpFactory;
import com.cooee.uiengine.util.network.HttpHelper;
import com.cooee.uiengine.util.network.HttpDiscriptor.CONTENT_TYPE;
import com.cooee.uiengine.util.network.HttpDiscriptor.REQUEST_TYPE;

public class AppTransfer extends BroadcastReceiver implements
		HttpAsync.HttpResolveListener {

	public static final String ACTION_SEND_PACKAGE_INFO = "retrieve.action.send.package.info";

	private Context mContext;
	private AppColloctor retriver;

	public AppTransfer(Context context, AppColloctor retriver) {
		this.mContext = context;
		this.retriver = retriver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ACTION_SEND_PACKAGE_INFO)) {
			// just for initializing the context of HttpHelper

			HttpHelper http = new HttpHelper(context);
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
			if (AppColloctor.appInfo != null
					&& !AppColloctor.appInfo.equals(""))
				update(AppColloctor.appInfo);

		}

	}

	public void update(String content) {
		HttpDiscriptor httpDisc = new HttpDiscriptor();
		httpDisc.setContent(content);
		httpDisc.setUrl("http://www.coolauncher.cn/getIntoFileV2.php");
		httpDisc.setContType(CONTENT_TYPE.CONTENT_MD5);
		httpDisc.setMd5_key("XmqyjcJ8LsZCsm5CAzDp7G0WberkNeqw68GG0OHyrqQGK6OhMWdDgmkGkNJeUYZT2bELWuDda2qZOxXWpBwbYIzu8npizDg6DFlmhYZcMOjBhhiF8OPs6LXiEDKdxvie");
		httpDisc.setReqType(REQUEST_TYPE.REQUEST_POST);
		httpDisc.setHttpRosloveListener(this);
		HttpFactory.getInstance().upload(httpDisc);

		HttpFactory.getInstance().start();

	}

	@Override
	public Object complete(Object result) {
		if (result != null) {
			HttpDiscriptor hd = (HttpDiscriptor) result;
			boolean ret = (hd.getResult() != null);
			retriver.httpResponseCallback(ret);
		}

		return 1;
	}
}
