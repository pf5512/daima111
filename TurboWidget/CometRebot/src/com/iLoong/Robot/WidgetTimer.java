package com.iLoong.Robot;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

public class WidgetTimer {

	private String TAG = "WidgetTimer";

	private int mInterval = 1000;

	private Timer mTimer = new Timer();

	private TimerTask mTimerTask = null;

	private WidgetTimerListener mTimerListener;

	private boolean mIsTaskPaused = true;

	private boolean mIsTaskStopped = true;

	public WidgetTimer(String name, WidgetTimerListener listener,
			int millisecond) {
		this.TAG = name;
		this.mInterval = millisecond;
		this.mTimerListener = listener;
		createTimerTask();
	}

	public void start() {
		Log.v(TAG, "start");
		if (mIsTaskStopped) {
			createTimerTask();
		}
		mIsTaskPaused = false;
	}

	public void pause() {
		Log.v(TAG, "pause");
		mIsTaskPaused = true;
	}

	public void resume() {
		Log.v(TAG, "resume");
		mIsTaskPaused = false;
	}

	public void stop() {
		Log.v(TAG, "stop");
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

	private void createTimerTask() {
		if (mTimer != null) {
			mTimer.cancel();
		}
		if (mTimerTask != null) {
			mTimerTask.cancel();
		}
		this.mTimer = new Timer();
		this.mTimerTask = new TimeTask();
		mTimer.schedule(this.mTimerTask, 0, mInterval);
		mIsTaskStopped = false;
	}
}