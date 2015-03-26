package com.coco.theme.themebox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;
import com.iLoong.base.themebox.R;

public class PreViewGallery extends Gallery {

public PreViewGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PreViewGallery(Context context) {
		super(context);
	}

	public PreViewGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
	
	@Override
	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {

		float f = 3F * paramFloat1;

		return super.onScroll(paramMotionEvent1, paramMotionEvent2, f,
				paramFloat2);

	}
}
