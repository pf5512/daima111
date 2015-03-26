package com.iLoong.launcher.SetupMenu;


import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;


public class ImageUtils
{
	
	public static Bitmap createBitmap(
			InputStream is ,
			int w ,
			int h )
	{
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream( is , null , opts );
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int destWidth = 0;
			int destHeight = 0;
			double ratio = 0.0;
			if( srcWidth < w || srcHeight < h )
			{
				ratio = 0.0;
				destWidth = srcWidth;
				destHeight = srcHeight;
			}
			else if( srcWidth > srcHeight )
			{// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
				ratio = (double)srcWidth / w;
				destWidth = w;
				destHeight = (int)( srcHeight / ratio );
			}
			else
			{
				ratio = (double)srcHeight / h;
				destHeight = h;
				destWidth = (int)( srcWidth / ratio );
			}
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			newOpts.inSampleSize = (int)ratio + 1;
			// inJustDecodeBounds设为false表示把图片读进内存中
			newOpts.inJustDecodeBounds = false;
			// 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
			newOpts.outHeight = destHeight;
			newOpts.outWidth = destWidth;
			// 获取缩放后图片
			return BitmapFactory.decodeStream( is , null , newOpts );
		}
		catch( Exception e )
		{
			// TODO: handle exception
			return null;
		}
	}
	
	public static Bitmap zoomBitmap(
			InputStream is ,
			int w ,
			int h )
	{
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream( is , null , opts );
			int srcWidth = opts.outWidth;// 获取图片的原始宽度
			int srcHeight = opts.outHeight;// 获取图片原始高度
			int deltaX = srcWidth - w;
			int deltaY = srcHeight - h;
			//			if( !false && ( deltaX < 0 || deltaY < 0 ) )
			//			{
			//				/*
			//
			//				* In this case the bitmap is smaller, at least in one dimension,
			//
			//				* than the target. Transform it by placing as much of the image as
			//
			//				* possible into the target and leaving the top/bottom or left/right
			//
			//				* (or both) black.
			//
			//				*/
			//				Bitmap b2 = Bitmap.createBitmap( w , h , Bitmap.Config.ARGB_8888 );
			//				Canvas c = new Canvas( b2 );
			//				int deltaXHalf = Math.max( 0 , deltaX / 2 );
			//				int deltaYHalf = Math.max( 0 , deltaY / 2 );
			//				Rect src = new Rect( deltaXHalf , deltaYHalf , deltaXHalf + Math.min( w , srcWidth ) , deltaYHalf + Math.min( h , srcHeight ) );
			//				int dstX = ( w - src.width() ) / 2;
			//				int dstY = ( h - src.height() ) / 2;
			//				Rect dst = new Rect( dstX , dstY , w - dstX , h - dstY );
			//				c.drawBitmap( Bitmap.createBitmap( BitmapFactory.decodeStream( is ) ) , src , dst , null );
			//				return b2;
			//			}
			float srcAspect = srcWidth / srcHeight;
			float targetAspect = w / h;
			Matrix matrix = new Matrix();
			if( srcAspect > targetAspect )
			{
				float scale = (float)h / (float)srcHeight;
				if( scale < .9F || scale > 1F )
				{
					matrix.setScale( scale , scale );
				}
				else
				{
					matrix = null;
				}
			}
			else
			{
				float scale = (float)w / (float)srcWidth;
				if( scale < .9F || scale > 1F )
				{
					matrix.setScale( scale , scale );
				}
				else
				{
					matrix = null;
				}
			}
			Bitmap b1;
			if( matrix != null )
			{
				// this is used for minithumb and crop, so we want to filter here.  
				b1 = Bitmap.createBitmap( BitmapFactory.decodeStream( is ) , 0 , 0 , srcWidth , srcHeight , matrix , true );
			}
			else
			{
				b1 = BitmapFactory.decodeStream( is );
			}
			int dx1 = Math.max( 0 , b1.getWidth() - w );
			int dy1 = Math.max( 0 , b1.getHeight() - h );
			if( b1.getWidth() - w < 0 || b1.getHeight() - h < 0 )
			{
				return b1;
			}
			Bitmap b2 = Bitmap.createBitmap( b1 , dx1 / 2 , dy1 / 2 , w , h );
			//  ShowDesktop.saveBmp( b2 , Environment.getExternalStorageDirectory() + File.separator + "turbo" + File.separator + "share" + File.separator + "cooeeShare.png" );
			if( matrix != null )
				b1.recycle();
			return b2;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			return null;
		}
	}
	public static Bitmap getHorizontalImage(Bitmap orgBmp,boolean recycle){

        Bitmap  destBmp =Bitmap.createBitmap(orgBmp.getWidth(), orgBmp.getHeight(), Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(destBmp);
        
        Matrix orig = canvas.getMatrix(); 
        orig.setScale(-1, 1);                     
        orig.postTranslate(orgBmp.getWidth(), 0);
        canvas.drawBitmap(orgBmp, orig, null); 
        
        if(recycle){
       	 orgBmp.recycle();
        }
        
        return destBmp;
      
}
}
