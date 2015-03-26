package com.cooee.app.cooeejewelweather3D.view;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.cooee.app.cooeejewelweather3D.filehelp.Log;

public class WeatherObserver extends ContentObserver {
	private final String TAG = "com.cooee.weather.WeatherObserver";
	//private Context mContext;
	private Handler mHandler;

	public final static int MSG_REFRESH = 1;

	WeatherObserver(Context context, Handler handler) {
		super(handler);
		//mContext = context;
		mHandler = handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		Log.v(TAG, "onChange selfChange = " + selfChange);
		mHandler.obtainMessage(MSG_REFRESH, null).sendToTarget();
	}
}