package com.cooee.launcher.common;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;

public class DecorateUtils {

	public static void getCurrentSizeRange(Activity activity,
			Point outSmallestSize, Point outLargestSize) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point realSize = new Point();
		display.getSize(realSize);
		//
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		//
		outSmallestSize.x = realSize.x;
		outSmallestSize.y = realSize.x - statusBarHeight;
		//
		outLargestSize.x = realSize.y;
		outLargestSize.y = realSize.y - statusBarHeight;
	}
}
