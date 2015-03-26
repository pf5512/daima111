package com.iLoong.NumberClock.Timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ClockTimerService extends Service {
	private Timer mTimer = new Timer();

	private Calendar mCalendar = null;

	private TimerTask mTimerTask = null;

	private final ClockTimerServiceBinder mBinder = new ClockTimerServiceBinder();

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.v("ClockTimeService", "onCreate");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.v("ClockTimeService", "onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("ClockTimeService", "onStartCommand");
		// TODO Auto-generated method stub
		if (intent == null) {
			startTimer();
		} else {
			String action = intent.getAction();
			if (action.equals("com.iLoong.widget.Clock.start")) {
				startTimer();
			} else if (action.equals("com.iLoong.Clock.stop")) {
				if (this.mTimerTask != null) {
					this.mTimerTask.cancel();
				}
				if (this.mTimer != null) {
					this.mTimer.cancel();
				}
			}
		}
		return START_NOT_STICKY;
	}

	private void startTimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		mTimer = new Timer();
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		mTimerTask = new TimeTask();
		mTimer.schedule(mTimerTask, 60L, 6000L);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.mTimerTask.cancel();
		this.mTimer.cancel();
		Log.e("ClockTimeService", "onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.e("ClockTimeService", "onBind");
		return this.mBinder;
	}

	private class TimeTask extends TimerTask {
		public void run() {
			mCalendar = Calendar.getInstance();
			mCalendar.setTimeInMillis(System.currentTimeMillis());
			Intent intent = new Intent("com.iLoong.widget.clock.change");
			ClockTimerService.this.sendBroadcast(intent);
		}
	}

	public class ClockTimerServiceBinder extends Binder {
		ClockTimerService getService() {
			return ClockTimerService.this;
		}
	}
}
