package com.cooee.app.cooeeweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class WeatherButton extends FrameLayout {

	@SuppressWarnings("unused")
	private final String TAG = "com.cooee.weather.WeatherButton";

	private boolean pressed = false;
	private Rect mRect;

	public WeatherButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		mRect = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (pressed) {
			canvas.scale(0.8f, 0.8f, mRect.centerX(), mRect.centerY());
		} else {
			canvas.scale(1.0f, 1.0f, 0.0f, 0.0f);
		}

		super.onDraw(canvas);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		mRect.set(0, 0, r - l, b - t);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			pressed = true;
			invalidate();
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			pressed = false;
			invalidate();
		}
		else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			int x = (int)ev.getX();
			int y = (int)ev.getY();
			if (pressed) {
				if (!mRect.contains(x, y))
				{
					pressed = false;
					invalidate();
				}
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// return true;
		return super.onInterceptTouchEvent(ev);
	}
}