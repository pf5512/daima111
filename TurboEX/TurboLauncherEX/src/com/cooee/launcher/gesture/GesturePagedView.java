package com.cooee.launcher.gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class GesturePagedView extends FrameLayout {

	private final int pointerMax = 2;

	private int pointerNum = 0;

	private boolean mGesturing = false;

	public float mDownMotionX = 0;
	public float mDownMotionX1 = 0;
	public float mDownMotionY = 0;
	public float mDownMotionY1 = 0;
	public float mTotalMotionX = 0;

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"Workspace can only be used in EXACTLY mode.");
		}
		// Return early if we aren't given a proper dimension
		if (widthSize <= 0 || heightSize <= 0) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int maxChildHeight = 0;
		final int verticalPadding = getPaddingTop() + getPaddingBottom();
		final int horizontalPadding = getPaddingLeft() + getPaddingRight();
		// The children are given the same width and height as the workspace
		// unless they were set to WRAP_CONTENT

		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			// disallowing padding in paged view (just pass 0)
			final View child = getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			int childWidthMode;
			if (lp.width == LayoutParams.WRAP_CONTENT) {
				childWidthMode = MeasureSpec.AT_MOST;
			} else {
				childWidthMode = MeasureSpec.EXACTLY;
			}
			int childHeightMode;
			if (lp.height == LayoutParams.WRAP_CONTENT) {
				childHeightMode = MeasureSpec.AT_MOST;
			} else {
				childHeightMode = MeasureSpec.EXACTLY;
			}
			final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
					widthSize - horizontalPadding, childWidthMode);
			final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
					heightSize - verticalPadding, childHeightMode);
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}
		if (heightMode == MeasureSpec.AT_MOST) {
			heightSize = maxChildHeight + verticalPadding;
		}
		setMeasuredDimension(widthSize, heightSize);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		}

	}

	public GesturePagedView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public GesturePagedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LauncherMultiTouchUtil.init(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			LauncherMultiTouchUtil.reset();
			mDownMotionX = ev.getX();
			mDownMotionY = ev.getY();

			pointerNum = 1;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			pointerNum++;
			mDownMotionX1 = ev.getX(1);
			mGesturing = true;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			pointerNum--;
			break;

		}
		if (mGesturing) {
			// setMotionEventSplittingEnabled(false);
			return true;
		} else {
			// setMotionEventSplittingEnabled(true);
			return super.onInterceptTouchEvent(ev);
		}

	}

	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_POINTER_DOWN:

			break;
		case MotionEvent.ACTION_MOVE:
			if (mGesturing) {
				if (pointerNum >= pointerMax) {

					LauncherMultiTouchUtil.performGesture(ev);

				}

			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			reset();
			mGesturing = false;
			pointerNum = 0;
			LauncherMultiTouchUtil.reset();
			break;
		}


		return true;
	}

	public void reset() {
		mDownMotionX1 = 0;
		mDownMotionX = 0;
		mDownMotionY = 0;
		mDownMotionY1 = 0;
	}

	

	

}
