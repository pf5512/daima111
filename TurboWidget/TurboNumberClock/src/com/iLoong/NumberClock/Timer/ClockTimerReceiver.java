package com.iLoong.NumberClock.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ClockTimerReceiver extends BroadcastReceiver {

	private ClockTimerListener mTimerListener;

	public ClockTimerReceiver(ClockTimerListener listener) {
		this.mTimerListener = listener;
	}

	@Override
	public IBinder peekService(Context myContext, Intent service) {
		// TODO Auto-generated method stub
		return super.peekService(myContext, service);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			if (mTimerListener != null) {
				mTimerListener.clockTimeChanged();
			}
		} else {
			Log.e("ClockTimeChangeReceiver", "intent is null");
		}
	}

}
