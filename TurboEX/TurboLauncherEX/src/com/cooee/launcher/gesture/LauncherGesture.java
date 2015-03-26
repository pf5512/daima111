package com.cooee.launcher.gesture;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author zhongqihong
 * **/
public class LauncherGesture extends LauncherGestureDetector {

	private View touchedView;

	public LauncherGesture(Context context, OnGestureListener listener) {
		super(context, listener);

	}

	public boolean onTouchEvent(View view, MotionEvent ev) {

		touchedView = view;
		return super.onTouchEvent(view, ev);
	}
}
