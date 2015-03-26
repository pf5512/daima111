package com.cooee.launcher.desktopedit;

import android.content.Context;
import android.util.AttributeSet;

import com.cooee.launcher.drawer.PagedViewCellLayout;

public class ThemeWallpaperLayout extends PagedViewCellLayout {

	public ThemeWallpaperLayout(Context context) {
		super(context);
	}

	public ThemeWallpaperLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ThemeWallpaperLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
