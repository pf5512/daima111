package com.iLoong.NumberClock.Timer;

import android.os.Handler;
import android.util.Log;

public class ClockTimerHandler {

	private static int INTERVAL = 1000;

	public Handler mHandler = null;

	private ClockTimerListener mTimerListener;

	public ClockTimerHandler(Handler handler, ClockTimerListener listener) {
		this.mHandler = handler;
		this.mTimerListener = listener;
	}

	public void start() {
		mHandler.postDelayed(r, INTERVAL);
	}

	public void stop() {
		mHandler.removeCallbacks(r);
	}

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mTimerListener.clockTimeChanged();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.postDelayed(this, INTERVAL);
		}
	};
}
