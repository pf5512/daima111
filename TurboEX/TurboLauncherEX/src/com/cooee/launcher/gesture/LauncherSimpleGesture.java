package com.cooee.launcher.gesture;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cooee.launcher.BubbleTextView;
import com.cooee.launcher.Launcher;
import com.cooee.launcher.gesture.LauncherGestureDetector.SimpleOnGestureListener;

/**
 * 
 * @author zhongqihong
 * 
 * **/
public class LauncherSimpleGesture extends SimpleOnGestureListener {

	final String TAG = "LauncherSimpleGesture";

	public void LOG(String msg) {

		Log.v(TAG, "LauncherSimpleGesture:" + msg);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		LOG("onDoubleTap");
		return super.onDoubleTap(e);
	}

	@Override
	public boolean onDoubleTapEvent(Context context,View view,MotionEvent e) {
		
		
		if(view instanceof BubbleTextView ){
			
			BubbleTextView bv=(BubbleTextView)view;
			bv.onDoubleTap();
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		LOG("onDown");
		return super.onDown(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return super.onFling(e1, e2, velocityX, velocityY);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		LOG("onLongPress");
		super.onLongPress(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		super.onShowPress(e);
	}

	@Override
	public boolean onSingleTapConfirmed(Context context, View view,
			MotionEvent e) {
		LOG("onSingleTapConfirmed");
		
		if(view instanceof BubbleTextView ){
			
			BubbleTextView bv=(BubbleTextView)view;
			bv.onSingleTapUp(context);
		}
		else{
			Launcher launcher = (Launcher) context;
			launcher.onClick(view);
		}
		

		return true;
		// return super.onSingleTapConfirmed(context, view, e);
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		LOG("onSingleTapUp");
		return true;
	}

}
