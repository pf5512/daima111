package com.cooee.app.cooeejewelweather3D.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
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
        else if (dm.widthPixels == 480 && dm.heightPixels == 800) {
            setMeasuredDimension(480+43, 480+43);
        } 
        else if (dm.widthPixels == 540 && dm.heightPixels == 960) {
            setMeasuredDimension(dm.widthPixels, dm.widthPixels+60);
        }
        else if (dm.widthPixels == 720 && dm.heightPixels == 1280) {
            setMeasuredDimension(dm.widthPixels, dm.widthPixels+70);
        }
        else if (dm.widthPixels == 1080 && dm.heightPixels == 1920) {
            setMeasuredDimension(dm.widthPixels, dm.widthPixels+80);
        }
        else {
        	 setMeasuredDimension(dm.widthPixels, dm.widthPixels);
            //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        
    }

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
		super.onDraw(canvas);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		// TODO Auto-generated method stub
		super.setImageDrawable(drawable);
	}
    
    
}