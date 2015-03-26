package com.iLoong.launcher.Desktop3D;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LoadingDialog extends View {

	private Object lock = new Object();
	private Bitmap imgBackground;
	private static final String BG_NAME = "launcher/loading/bg.jpg";
	private Matrix matrix;
	private Paint mBitmapPaint;

	public LoadingDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context);

		imgBackground = getImageFromAssetsFile(BG_NAME);
		mBitmapPaint = new Paint();
		mBitmapPaint.setFilterBitmap(true);
		mBitmapPaint.setAntiAlias(true);
	}

	public LoadingDialog(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
	}

	public LoadingDialog(Context context, AttributeSet attrs, int defStyle) {
		// TODO Auto-generated constructor stub
		super(context, attrs, defStyle);
	}	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		float scaleWidth = ((float)getWidth()) / imgBackground.getWidth();
		float scaleHeight = ((float)getHeight()) / imgBackground.getHeight();
		matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		synchronized (lock) {
			if (imgBackground != null) {
				canvas.drawBitmap(imgBackground, matrix, mBitmapPaint);
			}
		}
	}

	private Bitmap getImageFromAssetsFile(String fileName) {
		Bitmap image = null;
		AssetManager am = getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
	
	public void destory() {
		synchronized (lock) {
			if (imgBackground != null) {
				imgBackground.recycle();
				imgBackground = null;
			}
		}
	}

}
