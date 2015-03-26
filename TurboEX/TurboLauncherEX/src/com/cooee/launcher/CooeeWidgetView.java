package com.cooee.launcher;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * liangxiaoling
 */
public class CooeeWidgetView extends RelativeLayout {
	private CheckLongPressHelper mLongPressHelper;

	public CooeeWidgetView(Context context) {
		super(context);
		mLongPressHelper = new CheckLongPressHelper(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mLongPressHelper.hasPerformedLongPress()) {
			mLongPressHelper.cancelLongPress();
			return true;
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			mLongPressHelper.postCheckForLongPress();
			break;
		}

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLongPressHelper.cancelLongPress();
			break;
		}
		return false;
	}

	@Override
	public void cancelLongPress() {
		super.cancelLongPress();
		mLongPressHelper.cancelLongPress();
	}

	@Override
	public int getDescendantFocusability() {
		return ViewGroup.FOCUS_BLOCK_DESCENDANTS;
	}

}
