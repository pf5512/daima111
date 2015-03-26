
package com.cooee.app.cooeejewelweather3D.view;


//import com.cooee.widget.samweatherclock.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cooee.app.cooeejewelweather3D.filehelp.Log;

public class WeatherReceiver extends BroadcastReceiver {

    private static String TAG = "com.cooee.weather.WeatherReceiver";

    public final static int MSG_REFRESH = 2;
    public final static int MSG_FAILED = 3;
    public final static int MSG_AVAILABLE = 4;
    public final static int MSG_INVILIDE = 5;

	private static Handler mHandler = null;
	private static Handler mEditHandler = null;
	private final String BROATCAST_URI = "com.cooee.weather.data.action.UPDATE_RESULT";

	public static void setHandler(Handler h) {
		mHandler = h;
	}

	public static void setEditHandler(Handler h) {
		mEditHandler = h;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(BROATCAST_URI)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String result = bundle.getString("cooee.weather.updateResult");
				if (result != null) {
					if (result.equals("UPDATE_SUCCESED")
							|| result.equals("AVAILABLE_DATA")) {
						Log.v(TAG, "receive " + result);
						if (mHandler != null) {
							mHandler.obtainMessage(MSG_REFRESH, null)
									.sendToTarget();
							if (mEditHandler != null) {
								mEditHandler.obtainMessage(MSG_REFRESH, null)
										.sendToTarget();
							}
						}
						} else if (result.equals("UPDATE_FAILED")) {
							Log.v(TAG, "receive UPDATE_FAILED");
							if (mHandler != null) {
								mHandler.obtainMessage(MSG_FAILED, null)
										.sendToTarget();
							}
							if (mEditHandler != null) {
								mEditHandler.obtainMessage(MSG_FAILED, null)
										.sendToTarget();
							}
						} else if (result.equals("INVILIDE_DATA")) {
							Log.v(TAG, "my receive INVILIDE_DATA");
							if (mHandler != null) {
								mHandler.obtainMessage(MSG_INVILIDE, null)
										.sendToTarget();
							}
							if (mEditHandler != null) {
								mEditHandler.obtainMessage(MSG_INVILIDE, null)
										.sendToTarget();
							}
						}
				}
			}
		}
	}
}
