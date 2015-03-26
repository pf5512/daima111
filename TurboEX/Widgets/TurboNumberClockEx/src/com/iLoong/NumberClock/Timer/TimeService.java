package com.iLoong.NumberClock.Timer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class TimeService extends Service {

	private Thread thread;
	private boolean runFlag = true;
	private TimerReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		receiver = new TimerReceiver(this);
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_CHANGED));
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_DATE_CHANGED));
		registerReceiver(receiver, new IntentFilter(
				Intent.ACTION_TIMEZONE_CHANGED));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	@Override
	public void onCreate() {
		Log.d("mytag", "起线程");
		super.onCreate();
		if (thread == null) {
			thread = new Thread() {
				public void run() {
					runFlag = true;
					while (getRunFlag()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			};
		}
	}

	public synchronized boolean getRunFlag() {
		return runFlag;
	}

	public synchronized void stopThread() {
		runFlag = false;
	}
}
