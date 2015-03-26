package com.cooee.app.cooeejewelweather3D.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class citymanageFrame extends FrameLayout{

	 private DisplayMetrics dm = new DisplayMetrics();
	 
	public citymanageFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		 WindowManager mWm = (WindowManager) context
	                .getSystemService(Context.WINDOW_SERVICE);
	        mWm.getDefaultDisplay().getMetrics(dm);
	       setBackgroundColor(0x00000000);
	}


	 @Override
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		 if(dm.widthPixels == 800){
			 setMeasuredDimension(widthMeasureSpec, 190);
		 }
		 else if(dm.widthPixels == 0){
			 setMeasuredDimension(widthMeasureSpec, 190);
		 }
		 else{
			 setMeasuredDimension(160, 193);
			 //super.onMeasure(160, 183);
		 }
	 }
}
