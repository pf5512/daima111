package com.cooee.app.cooeeweather.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;

public class WeatherImageView extends ImageView {

    private DisplayMetrics dm = new DisplayMetrics();

    public WeatherImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获得屏幕大小
        WindowManager mWm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (dm.widthPixels == 480 && dm.heightPixels == 854) {
            setMeasuredDimension(480+43, 480+43);
        } 
        else if (dm.widthPixels == 540 && dm.heightPixels == 960) {
            setMeasuredDimension(dm.widthPixels, dm.widthPixels+60);
        }
        else if (dm.widthPixels == 720 && dm.heightPixels == 1280) {
            setMeasuredDimension(dm.widthPixels, dm.widthPixels+70);
        } else {
        	 setMeasuredDimension(dm.widthPixels, dm.widthPixels);
            //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}