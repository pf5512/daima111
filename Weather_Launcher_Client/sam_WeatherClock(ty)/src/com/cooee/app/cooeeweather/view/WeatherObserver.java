package com.cooee.app.cooeeweather.view;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

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