package com.cooee.app.cooeejewelweather3D.view;



//import com.cooee.widget.samweatherclock.R;

import com.cooeeui.weatherclient.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherComboBox extends LinearLayout {

	@SuppressWarnings("unused")
	private final String TAG = "com.cooee.weather.WeatherComboBox";

	private OnTouchListener listener;

	private boolean pressed = false;
	private Rect mRect;
	private TextView mTextView;
	private ImageView mImageView;

	public WeatherComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);

		listener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					pressed = true;
					mTextView.setTextColor(0xffa2d853);
					mImageView.setImageResource(R.drawable.combo_icon_pressed);
					invalidate();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					pressed = false;
					mTextView.setTextColor(0xffffffff);
					mImageView.setImageResource(R.drawable.combo_icon);
					invalidate();
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					if (pressed) {
						if (!mRect.contains(x, y)) {
							pressed = false;
							invalidate();
						}
					}
				}
				return false;
			}
		};
		this.setOnTouchListener(listener);

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

		mTextView = (TextView) getChildAt(0);
		mImageView = (ImageView) getChildAt(1);

		mRect.set(0, 0, r - l, b - t);
	}
}