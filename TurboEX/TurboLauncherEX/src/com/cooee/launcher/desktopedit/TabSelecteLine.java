package com.cooee.launcher.desktopedit;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.cooeeui.brand.turbolauncher.R;

public class TabSelecteLine extends ViewGroup {

	private static final String TAG = "TabSelecteLine";

	private float scale;

	private int mTabCount = 3;
	private ImageView mBackground;
	private ImageView mSelecteLine;

	private float mSelecteLineWidth;

	public TabSelecteLine(Context context) {
		super(context);
	}

	public TabSelecteLine(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TabSelecteLine(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mBackground = new ImageView(context);
		mBackground.setBackgroundResource(R.drawable.desktop_edit_line);

		mSelecteLine = new ImageView(context);
		mSelecteLine.setBackgroundResource(R.drawable.desktop_edit_tab_line);

		addView(mBackground);
		addView(mSelecteLine);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		scale = widthSpecSize / 720f;
		int newWidth = widthSpecSize;
		int newHeight = heightSpecSize;
		mSelecteLineWidth = newWidth / mTabCount;
		mBackground.measure(MeasureSpec.makeMeasureSpec(newWidth,
				MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
				(int) (2 * scale), MeasureSpec.EXACTLY));
		mSelecteLine.measure(MeasureSpec.makeMeasureSpec(
				(int) mSelecteLineWidth, MeasureSpec.EXACTLY), MeasureSpec
				.makeMeasureSpec((int) (2 * scale), MeasureSpec.EXACTLY));

		setMeasuredDimension(newWidth, newHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mBackground.layout(l, (int) (b - 2 * scale), r, b);
		mSelecteLine.layout(l, (int) (b - 2 * scale),
				l + mSelecteLine.getMeasuredWidth(), b);
	}

	public void setTabCount(int tabCount) {
		mTabCount = tabCount;
	}

	public void onTabChange(int tabId) {
		Log.i(TAG, "tabId : " + tabId);
		final float translationX = tabId * mSelecteLineWidth;
		ObjectAnimator anim = ObjectAnimator.ofFloat(mSelecteLine,
				"translationX", translationX);
		anim.setDuration(300);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.start();
	}
}
