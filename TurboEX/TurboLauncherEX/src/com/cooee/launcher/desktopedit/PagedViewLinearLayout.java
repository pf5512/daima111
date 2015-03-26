package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class PagedViewLinearLayout extends ViewGroup {
	private static final String TAG = "PagedViewLinearLayout";
	float scale = 0;
	private int mCellCountX = 3;

	public PagedViewLinearLayout(Context context) {
		super(context);
	}

	public PagedViewLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagedViewLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		scale = widthSpecSize / 720.0f;

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
			throw new RuntimeException(
					"PagedViewLinearLayout cannot hava UNSPECIFIED dimensions");
		}

		int newWidth = widthSpecSize;
		int newHeight = heightSpecSize;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
					(int) (200 * scale), MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
					(int) (334 * scale), MeasureSpec.EXACTLY);
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

		}
		setMeasuredDimension(newWidth, newHeight);
	}

	private void measureViews(int widthMeasureSpec, int heightMeasureSpec) {

		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			view.measure((int) (200 * scale), (int) (334 * scale));
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.i(TAG, "onLayout()-->changed:" + changed + ";l:" + l + ";t:" + t
				+ ";r:" + r + ";b:" + b);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			child.layout((int) ((i * 228 * scale) + 32 * scale),
					(int) (71 * scale), child.getMeasuredWidth()
							+ (int) (i * 228 * scale + 32 * scale),
					child.getMeasuredHeight() + (int) (71 * scale));
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		View child;
		for (int i = 0; i < getChildCount(); i++) {
			child = getChildAt(i);
			child.setFocusable(true);
			child.setClickable(true);
		}
	}

	public void setCellCountX(int cellCountX) {
		mCellCountX = cellCountX;
	}
}
