package com.cooee.app.cooeeweather.component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;

public class cityButton extends Button {

	 private DisplayMetrics dm = new DisplayMetrics();
	 
	public cityButton(Context context) {
		super(context);
		
		WindowManager mWm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        
	}
	
	public cityButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获得屏幕大小
        WindowManager mWm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
    }
	

	 @Override
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	       /* if (dm.widthPixels == 480 && dm.heightPixels == 854) {
	            setMeasuredDimension(480+43, 480+43);
	        } else if(dm.widthPixels == 800) {
	        	 //setMeasuredDimension(80, 80);
	        	// setBackgroundResource(R.drawable.city_item_bg);
	            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        }
	        else{
	        	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        }
	    }*/
		 //20130709-liuhailin-<change the resolution of screen>
		 if(dm.widthPixels == 800){
			 setMeasuredDimension(206, 260);
		 }
		 else if(dm.widthPixels == 0){
			 setMeasuredDimension(130, 168);
		 } 
		 else if(dm.widthPixels == 720 && dm.heightPixels == 1280){
			 setMeasuredDimension(190, 260);
		 }
		 else if(dm.widthPixels == 540 && dm.heightPixels == 960){
			 setMeasuredDimension(160, 210);
		 }
		 else if(dm.widthPixels == 320 && dm.heightPixels == 480){
			 setMeasuredDimension(90, 120); 
		 }
		 else{
			 setMeasuredDimension(130, 168);
			 //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		 }
		 /*
		 if(dm.widthPixels == 800){
			 setMeasuredDimension(206, 260);
		 }
		 else if(dm.widthPixels == 0){
			 setMeasuredDimension(130, 168);
		 }
		 else{
			 setMeasuredDimension(130, 168);
			 //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		 }
		 */
		//20130709-liuhailin
		 
	 //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 }
	 
}
