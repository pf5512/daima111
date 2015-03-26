package com.cooee.widget3D.JewelWeather.Common;

import java.util.Timer;
import java.util.TimerTask;

import com.iLoong.launcher.Desktop3D.Log;


public class WidgetTimer {

	private String TAG= "WidgetTimer";
	
	private int mInterval = 1000;

	private Timer mTimer = new Timer();

	private TimerTask mTimerTask = null;

	private WidgetTimerListener mTimerListener;

	private boolean mIsTaskPaused = true;

	private boolean mIsTaskStopped = true;
	
	public WidgetTimer(String name,WidgetTimerListener listener,int millisecond) {
		this.TAG = name;
		this.mInterval = millisecond;
		this.mTimer = new Timer();
		this.mTimerTask = new TimeTask();
		this.mTimerListener = listener;
		
	}

	public void setInterval(int millisecond) {
		mInterval = millisecond;
	}

	public void start() {
		if(mIsTaskStopped)
		{
			if (mTimer != null && mTimerTask != null) {
				mTimer.schedule(this.mTimerTask, 0, mInterval);
			} else {
				if (mTimer == null) {
					this.mTimer = new Timer();
				}
				if (mTimerTask == null) {
					this.mTimerTask = new TimeTask();
				}
				mTimer.schedule(this.mTimerTask, 0, mInterval);
			}
			mIsTaskStopped = false;
		}
		mIsTaskPaused = false;
	}
	public void pause() {
		Log.v("ClockTimer", "pause");
		mIsTaskPaused = true;
	}

	public void resume() {
		Log.v("ClockTimer", "resume");
		mIsTaskPaused = false;
	}
	
	public void stop() {
		Log.v("ClockTimer", "stop ClockTimer");
		if (this.mTimerTask != null) {
			this.mTimerTask.cancel();
			this.mTimerTask = null;
		}
		if (this.mTimer != null) {
			this.mTimer.cancel();
			this.mTimer = null;
		}
		mIsTaskStopped = true;
	}

	private class TimeTask extends TimerTask {
		public void run() {
			if (!mIsTaskPaused) {
				mTimerListener.timeChanged();
			}
		}
	}
}