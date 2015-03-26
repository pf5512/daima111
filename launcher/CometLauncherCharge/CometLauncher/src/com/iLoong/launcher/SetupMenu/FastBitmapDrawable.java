package com.iLoong.launcher.SetupMenu;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


public class FastBitmapDrawable extends Drawable
{
	
	private Bitmap mBitmap;
	private Bitmap mBackground;
	private int mAlpha;
	private int mWidth;
	private int mHeight;
	private final Paint mPaint = new Paint();
	
	public FastBitmapDrawable(
			Bitmap b ,
			Bitmap bg )
	{
		mAlpha = 255;
		mBitmap = b;
		if( b != null )
		{
			mWidth = mBitmap.getWidth();
			mHeight = mBitmap.getHeight();
		}
		else
		{
			mWidth = mHeight = 0;
		}
		mBackground = bg;
	}
	
	public FastBitmapDrawable(
			Bitmap b )
	{
		mAlpha = 255;
		mBitmap = b;
		if( b != null )
		{
			mWidth = mBitmap.getWidth();
			mHeight = mBitmap.getHeight();
		}
		else
		{
			mWidth = mHeight = 0;
		}
	}
	
	public FastBitmapDrawable(
			Bitmap b ,
			float scale )
	{
		mAlpha = 255;
		if( b != null )
		{
			mBitmap = Tools.resizeBitmap( b , scale );
			mWidth = mBitmap.getWidth();
			mHeight = mBitmap.getHeight();
		}
		else
		{
			mWidth = mHeight = 0;
		}
	}
	
	@Override
	public void draw(
			Canvas canvas )
	{
		final Rect r = getBounds();
		if( mBackground != null )
			canvas.drawBitmap( mBackground , null , r , mPaint );
		if( mBitmap != null )
			canvas.drawBitmap( mBitmap , r.left , r.top , mPaint );
	}
	
	@Override
	public void setColorFilter(
			ColorFilter cf )
	{
		mPaint.setColorFilter( cf );
	}
	
	@Override
	public int getOpacity()
	{
		return PixelFormat.TRANSLUCENT;
	}
	
	@Override
	public void setAlpha(
			int alpha )
	{
		mAlpha = alpha;
		mPaint.setAlpha( alpha );
	}
	
	public void setFilterBitmap(
			boolean filterBitmap )
	{
		mPaint.setFilterBitmap( filterBitmap );
	}
	
	public int getAlpha()
	{
		return mAlpha;
	}
	
	@Override
	public int getIntrinsicWidth()
	{
		return mWidth;
	}
	
	@Override
	public int getIntrinsicHeight()
	{
		return mHeight;
	}
	
	@Override
	public int getMinimumWidth()
	{
		return mWidth;
	}
	
	@Override
	public int getMinimumHeight()
	{
		return mHeight;
	}
	
	public void setBitmap(
			Bitmap b )
	{
		mBitmap = b;
		if( b != null )
		{
			mWidth = mBitmap.getWidth();
			mHeight = mBitmap.getHeight();
		}
		else
		{
			mWidth = mHeight = 0;
		}
	}
	
	public void setBackgrounBmp(
			Bitmap b )
	{
		mBackground = b;
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
}
