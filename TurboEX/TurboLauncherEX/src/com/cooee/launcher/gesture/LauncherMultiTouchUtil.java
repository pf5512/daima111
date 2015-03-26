package com.cooee.launcher.gesture;

import java.util.ArrayList;

import android.content.Context;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Toast;

/**
 * 
 * @author zhongqihong
 * 
 * **/
public class LauncherMultiTouchUtil {
	public static Context mContext;
	private static final int FLING_THRESHOLD_VELOCITY = 500;
	private static final int MIN_SNAP_VELOCITY = 1500;
	private static final int MIN_FLING_VELOCITY = 250;
	static final int AUTOMATIC_PAGE_SPACING = -1;
	public static float mTouchSlop;
	protected float mDensity;
	protected int mFlingThresholdVelocity;
	protected int mMinFlingVelocity;
	protected int mMinSnapVelocity;
	private int mPagingTouchSlop;
	private int mMaximumVelocity;
	private int mMinimumWidth;
	protected int mPageSpacing;
	public static float pointerDistance = 0;
	public static int mState;

	public final static int STATE_SLIDE_LEFT = 0;
	public final static int STATE_SLIDE_RIGHT = 1;
	public final static int STATE_ZOOM_OUT = 2;
	public final static int STATE_ZOOM_IN = 3;
	public final static int STATE_NONE = 4;
	public final static int STATE_PROCEEDED = 5;

	public static ArrayList<Zoom> zooms = new ArrayList<LauncherMultiTouchUtil.Zoom>();
	public static ArrayList<Slide> slides = new ArrayList<Slide>();
	public static final String TAG = "LauncherMultiTouchUtil";
	public static float mDistance = 0;
	public static float mDistance1 = 0;
	public static float mOldDistance = 0;
	public static float mMotionDownX0 = 0;
	public static float mMotionDownX1 = 0;
	public static float mMotionDownY0 = 0;
	public static float mMotionDownY1 = 0;
	public static GesturePagedView mGestureView;

	public static Context getContext() {
		return mContext;
	}

	public static void init(GesturePagedView view) {
		mGestureView = view;
		mContext = mGestureView.getContext();

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop() * 2;
		reset();
		// mPagingTouchSlop = configuration.getScaledPagingTouchSlop();
		// mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		// mDensity = getContext().getResources().getDisplayMetrics().density;
		// mFlingThresholdVelocity = (int) (FLING_THRESHOLD_VELOCITY *
		// mDensity);
		// mMinFlingVelocity = (int) (MIN_FLING_VELOCITY * mDensity);
		// mMinSnapVelocity = (int) (MIN_SNAP_VELOCITY * mDensity);
	}

	public static enum GESTURE_DIR {
		GESTURE_L, GESTURE_V
	}

	public static void addZoom(Zoom zoom) {
		zooms.add(zoom);
	}

	public static void addSlide(Slide slide) {
		slides.add(slide);
	}

	public static void performZoom(MotionEvent ev) {
		float dis = measureDistance(ev);
		float temp = pointerDistance;
		if (dis != pointerDistance) {
			pointerDistance = dis;
			if (dis < temp) {
				zoomIn();
			} else {
				zoomOut();
			}
		}

	}

	public static void zoomOut() {
		Log.v(TAG, "zoomOut");
	}

	public static void zoomIn() {
		Log.v(TAG, "zoomIn");
	}

	public static void slideLeft() {
		// for (int i = 0; i < slides.size(); i++)
		{
			Log.v(TAG, "slideLeft");
		}
	}

	public static void slideRight() {
		Log.v(TAG, "slideRight");
	}

	private int getZoomSize() {
		return zooms.size();
	}

	private int getSlideSize() {
		return slides.size();
	}

	public static interface Zoom {

		public void zoomOut(MotionEvent ev);

		public void zoomIn(MotionEvent ev);
	}

	public static interface Slide {

		public void slideLeft(int pointerNum);

		public void slideRight(int pointerNum);
	}

	public static interface Click {

	}

	public static void performGesture(MotionEvent ev) {
		if (ev.getPointerCount() < 2 || mState != STATE_NONE) {
			return;
		}

		determineGesture(ev);
		String msg = "aaa";
		switch (mState) {
		case STATE_SLIDE_LEFT:
			slideLeft();
			msg = "slide Left ";
			break;
		case STATE_SLIDE_RIGHT:
			slideRight();
			msg = "slide right ";
			break;
		case STATE_ZOOM_IN:
			zoomIn();
			msg = "zoom in ";
			break;
		case STATE_ZOOM_OUT:
			zoomOut();
			msg = "zoom out ";
			break;
		case STATE_NONE:
			Log.v(TAG, "无定义手势");
		}
		if (!msg.equals("aaa"))
			message(msg);
	}

	public static float measureDistance(MotionEvent ev) {

		int pointer = ev.getPointerCount();
		if (pointer < 2)
			return 0;
		float disX = ev.getX(0) - ev.getX(1);
		float disY = ev.getY(0) - ev.getY(1);

		return FloatMath.sqrt(disX * disX + disY * disY);
	}

	public static void initMotionDown(MotionEvent ev) {
		mMotionDownX0 = ev.getX(0);
		mMotionDownY0 = ev.getY(0);
		mMotionDownX1 = ev.getX(1);
		mMotionDownY1 = ev.getY(1);
	}

	public static void determineGesture(MotionEvent ev) {
		mDistance = ev.getX() - mGestureView.mDownMotionX;
		mDistance1 = ev.getX(1) - mGestureView.mDownMotionX1;
		Log.v(TAG, "mDistance=" + mDistance + " mDownMotionX="
				+ mGestureView.mDownMotionX + " mTouchSlop=" + mTouchSlop);
		mState = STATE_NONE;
		if (Math.abs(mDistance) > mTouchSlop
				&& Math.abs(mDistance1) > mTouchSlop) {
			if (mDistance < 0) {
				mState = STATE_SLIDE_LEFT;
			}

			else {
				mState = STATE_SLIDE_RIGHT;
			}
			return;
		}
		if (mMotionDownX0 == 0 || mMotionDownX1 == 0 || mMotionDownY0 == 0
				|| mMotionDownY1 == 0) {
			initMotionDown(ev);
		}
		if (mOldDistance == 0) {
			mOldDistance = mDistance = measureDistance(ev);

		} else {
			mDistance = measureDistance(ev);
		}
		Log.v(TAG, "mOldDistance=" + mOldDistance + " mDistance=" + mDistance);
		if (mOldDistance != mDistance) {
			// 首先要判断两个手指的方向是不是相同的，如果是同方向表示双指滑动事件而不是缩放事件。
			// 即：如果一个手指向上滑，另外一个手指必须向下滑。同理，如果一个手指向左划，另外一个手指必须向右滑，
			// 否则不算是缩放，只能算是同向滑动。

			if (!((ev.getX(0) - mMotionDownX0) * (ev.getX(1) - mMotionDownX1) <= 0 || (ev
					.getY(0) - mMotionDownY0) * (ev.getY(1) - mMotionDownY1) <= 0)) {
				message("方向相同");
				return;
			}

			if (Math.abs(mDistance - mOldDistance) > mTouchSlop) {

				if (mOldDistance < mDistance) {
					mState = STATE_ZOOM_OUT;
				}

				else {
					mState = STATE_ZOOM_IN;
				}
				return;
			}
		}

	}

	public static void reset() {
		mDistance = 0;
		mState = STATE_NONE;
		mOldDistance = 0;
		mMotionDownX0 = 0;
		mMotionDownX1 = 0;
		mMotionDownY0 = 0;
		mMotionDownY1 = 0;
		mDistance1 = 0;
	}

	public static Toast toast = null;

	public static void message(String str) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(getContext(), str, Toast.LENGTH_LONG);
		toast.show();
	}

}
