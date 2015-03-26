package com.cooee.widget3D.JewelWeather.Common;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FrameAnimation {
	private static String TAG = "FrameAnimation";
	// 动画需要的图片，按顺序排好
	private TextureRegion[] mFrameTextures;

	// 一帧一帧间隔时间
	private float mFrameDuration;

	// 帧率，每秒播放多少帧动画
	private int mFrameFrequency = 20;

	// 动画持续总时间
	private float mAnimationTime = 1000;// 毫秒

	// 一组动画帧循环播放次数
	private int mAnimationLoopCount = 1;

	private Timer mFrameTimer = null;

	private float mSingleFrameTime = 0;

	public IFrameRefreshCallback callback = null;

	private TimeTask mTimeTask = null;

	public boolean isTaskPaused = true;

	public boolean isTaskStopped = true;

	public FrameAnimation(List<TextureRegion> frameTextures) {
		this.mFrameTextures = new TextureRegion[frameTextures.size()];
		for (int i = 0, n = frameTextures.size(); i < n; i++) {
			this.mFrameTextures[i] = (TextureRegion) frameTextures.get(i);
		}
	}

	public FrameAnimation(TextureRegion... keyFrames) {
		this.mFrameTextures = keyFrames;
		mFrameDuration = (float) 1 * 1000 / (float) mFrameFrequency;
		mSingleFrameTime = mAnimationTime / mFrameTextures.length;
		mAnimationLoopCount = (int) (mAnimationTime / (mFrameDuration * mFrameTextures.length));
		createTimerTask();
	}

	public TextureRegion getKeyFrame(float stateTime, boolean looping) {
		int frameNumber = (int) (stateTime / mSingleFrameTime);
		if (!looping) {
			frameNumber = Math.min(mFrameTextures.length - 1, frameNumber);
		} else {
			frameNumber = frameNumber % mFrameTextures.length;
		}
		Log.v("aq", "frameNumber:" + frameNumber);

		return mFrameTextures[frameNumber];
	}

	private class TimeTask extends TimerTask {
		private float execTime = 0;

		public void run() {
			if (!isTaskPaused) {
				
				// Log.e("aq", "frame TimeTask run");
				if (execTime >= mAnimationTime) {
					execTime = 0;
				//	 Log.e("aq", "kill frame timer: " +
				//	 "execTime:"
				//	 + execTime + " mAnimationTime:" + mAnimationTime);
					isTaskPaused = true;
					isTaskStopped = true;
					callback.endRefreshRegion();
					mFrameTimer.cancel();
					mFrameTimer = null;
					mTimeTask.cancel();
					mTimeTask = null;
				} else {
					callback.beginRefreshRegion();
					TextureRegion curFrameRegion = getKeyFrame(execTime, true);
					execTime += mFrameDuration;
					if (callback != null) {
						callback.refreshRegion(curFrameRegion);
					}
					// Log.e("test", "**********kill**************: " +
					// "execTime:"
					// + execTime + " mAnimationTime:" + mAnimationTime);
				}
			}
		}
	}

	public void start() {
		// Log.v(TAG, "start");
		if (isTaskStopped) {
			createTimerTask();
		}
		isTaskPaused = false;
	}

	public void stop() {
		Log.v(TAG, "stop");
		if (mFrameTimer != null) {
			mFrameTimer.cancel();
			mFrameTimer = null;
		}
		if (mTimeTask != null) {
			mTimeTask.cancel();
			mTimeTask = null;
		}
		isTaskPaused = true;
		isTaskStopped = true;
	}

	public void pause() {
		Log.v(TAG, "pause");
		isTaskPaused = true;
	}

	public void resume() {
		Log.v(TAG, "resume");
		isTaskPaused = false;
	}

	public static interface IFrameRefreshCallback {
		void refreshRegion(TextureRegion region);

		void endRefreshRegion();

		void beginRefreshRegion();
	}

	private void createTimerTask() {
		// Log.v(TAG, "start isTaskPaused:" + isTaskPaused);
		isTaskStopped = false;
		if (mFrameTimer != null) {
			mFrameTimer.cancel();
		}
		if (mTimeTask != null) {
			mTimeTask.cancel();
		}
		mFrameTimer = new Timer();
		mTimeTask = new TimeTask();
		mFrameTimer.schedule(mTimeTask, 0, (long) mFrameDuration);

	}
}
