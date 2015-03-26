package com.iLoong.launcher.media;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.iLoong.launcher.Desktop3D.Log;


public class DecodeThumbHelper
{
	
	private static BitmapFactory.Options optsJustBounds;
	private static BitmapFactory.Options optsDecode;
	public static final int SCALE_MAX = 5;
	static
	{
		optsJustBounds = new BitmapFactory.Options();
		optsJustBounds.inJustDecodeBounds = true;
		optsDecode = new BitmapFactory.Options();
	}
	
	public static Bitmap DecodeFile(
			String path ,
			int width ,
			int height )
	{
		BitmapFactory.decodeFile( path , optsJustBounds );
		optsDecode.inSampleSize = computeSampleSize( optsJustBounds , -1 , width * height );
		try
		{
			Bitmap bmp = BitmapFactory.decodeFile( path , optsDecode );
			return bmp;
		}
		catch( OutOfMemoryError err )
		{
			return null;
		}
	}
	
	public static int computeSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		int initialSize = computeInitialSampleSize( options , minSideLength , maxNumOfPixels );
		int roundedSize;
		int tmp;
		if( initialSize <= 8 )
		{
			roundedSize = 1;
			tmp = roundedSize;
			while( tmp < initialSize )
			{
				roundedSize = tmp;
				tmp <<= 1;
			}
		}
		else
		{
			roundedSize = ( initialSize + 7 ) / 8 * 8;
		}
		return roundedSize;
	}
	
	private static int computeInitialSampleSize(
			BitmapFactory.Options options ,
			int minSideLength ,
			int maxNumOfPixels )
	{
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = ( maxNumOfPixels == -1 ) ? 1 : (int)Math.ceil( Math.sqrt( w * h / maxNumOfPixels ) );
		return lowerBound < 2 ? 1 : lowerBound - 1;
	}
	
	public static Bitmap resizeBmp(
			Bitmap bmp ,
			float minWidth ,
			float minHeight )
	{
		float scale = 1f;
		Bitmap res1;
		Bitmap res2;
		int bitmapWidth = bmp.getWidth();
		int bitmapHeight = bmp.getHeight();
		//Log.d("media", "w,h="+bitmapWidth+","+bitmapHeight);
		scale = minWidth / (float)bitmapWidth;
		if( bitmapHeight * scale < minHeight )
		{
			scale = minHeight / (float)bitmapHeight;
		}
		bitmapWidth = (int)( scale * bitmapWidth );
		bitmapHeight = (int)( scale * bitmapHeight );
		if( bitmapWidth > SCALE_MAX * minWidth )
			bitmapWidth = (int)( SCALE_MAX * minWidth );
		if( bitmapHeight > SCALE_MAX * minHeight )
			bitmapHeight = (int)( SCALE_MAX * minHeight );
		if( scale != 1 )
		{
			res1 = Bitmap.createScaledBitmap( bmp , bitmapWidth , bitmapHeight , false );
			bmp.recycle();
		}
		else
			res1 = bmp;
		bitmapWidth = res1.getWidth();
		bitmapHeight = res1.getHeight();
		int x = (int)( ( bitmapWidth - minWidth ) / 2 );
		int y = (int)( ( bitmapHeight - minHeight ) / 2 );
		if( x < 0 )
			x = 0;
		if( y < 0 )
			y = 0;
		int width = bitmapWidth - 2 * x;
		int height = bitmapHeight - 2 * y;
		res2 = Bitmap.createBitmap( res1 , x , y , width , height );
		res1.recycle();
		return res2;
	}
}
