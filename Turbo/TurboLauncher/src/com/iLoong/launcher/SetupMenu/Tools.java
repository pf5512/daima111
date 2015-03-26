package com.iLoong.launcher.SetupMenu;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.UI3DEngine.BitmapTexture;
import com.iLoong.launcher.desktop.iLoongApplication;
import com.iLoong.launcher.theme.ThemeManager;


public class Tools
{
	
	private static float oldnum = 0;
	private static float newnum = 0;
	
	public static Bitmap getImageFromSDCardFile(
			final String foldname ,
			final String filename )
	{
		Bitmap image = null;
		String file = Environment.getExternalStorageDirectory() + File.separator + foldname + File.separator + filename;
		try
		{
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = 1;
			image = BitmapFactory.decodeFile( file , opts );
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	public static Bitmap getImageFromInStream(
			InputStream is )
	{
		Bitmap image = null;
		try
		{
			image = BitmapFactory.decodeStream( is );
			is.close();
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	public static Bitmap getImageFromInStream(
			InputStream is ,
			Bitmap.Config config )
	{
		Bitmap image = null;
		try
		{
			Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = config;
			image = BitmapFactory.decodeStream( is , null , opts );
			is.close();
		}
		catch( Exception e )
		{
		}
		return image;
	}
	
	public static Drawable zoomDrawable(
			Drawable drawable ,
			float scale )
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap( drawable ); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		matrix.postScale( scale , scale ); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap( oldbmp , 0 , 0 , width , height , matrix , true ); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable( newbmp ); // 把bitmap转换成drawable并返回
	}
	
	public static Drawable zoomDrawable(
			Drawable drawable ,
			int w ,
			int h )
	{
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap( drawable ); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = ( (float)w / width ); // 计算缩放比例
		float scaleHeight = ( (float)h / height );
		matrix.postScale( scaleWidth , scaleHeight ); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap( oldbmp , 0 , 0 , width , height , matrix , true ); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable( newbmp ); // 把bitmap转换成drawable并返回
	}
	
	// drawable 转换成bitmap
	public static Bitmap drawableToBitmap(
			Drawable drawable )
	{
		int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565; // 取drawable的颜色格式
		Bitmap bitmap = Bitmap.createBitmap( width , height , config ); // 建立对应bitmap
		Canvas canvas = new Canvas( bitmap ); // 建立对应bitmap的画布
		drawable.setBounds( 0 , 0 , width , height );
		drawable.draw( canvas ); // 把drawable内容画到画布中
		return bitmap;
	}
	
	/**
	 * 图片旋转
	 * 
	 * @param bmp
	 *            要旋转的图片
	 * @param degree
	 *            图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
	 * @return
	 */
	public static Bitmap rotateBitmap(
			Bitmap bmp ,
			float degree )
	{
		Matrix matrix = new Matrix();
		matrix.postRotate( degree );
		return Bitmap.createBitmap( bmp , 0 , 0 , bmp.getWidth() , bmp.getHeight() , matrix , true );
	}
	
	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param scale
	 *            值小于则为缩小，否则为放大
	 * @return
	 */
	public static Bitmap resizeBitmap(
			Bitmap bm ,
			float scale )
	{
		//		Matrix matrix = new Matrix();
		//		matrix.postScale(scale, scale);
		if( scale == 1 )
			return bm;
		if( (int)bm.getWidth() * scale < 1 || (int)bm.getHeight() * scale < 1 )
		{
			return bm;
		}
		Bitmap tmp = Bitmap.createScaledBitmap( bm , (int)( bm.getWidth() * scale ) , (int)( bm.getHeight() * scale ) , true );
		if( tmp != bm )
			bm.recycle();
		return tmp;
		//		return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
	}
	
	/**
	 * 图片缩放
	 * 
	 * @param bm
	 * @param w
	 *            缩小或放大成的宽
	 * @param h
	 *            缩小或放大成的高
	 * @return
	 */
	public static Bitmap resizeBitmap(
			Bitmap bm ,
			int w ,
			int h )
	{
		Bitmap BitmapOrg = bm;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		if( width == w && height == h )
			return bm;
		float scaleWidth = ( (float)w ) / width;
		float scaleHeight = ( (float)h ) / height;
		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth , scaleHeight );
		Bitmap tmp = Bitmap.createBitmap( BitmapOrg , 0 , 0 , width , height , matrix , true );
		bm.recycle();
		return tmp;
	}
	
	public static Bitmap scaleBitmap(
			Bitmap bm ,
			int w ,
			int h )
	{
		Bitmap BitmapOrg = bm;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		if( width == w && height == h )
			return bm;
		float scaleWidth = ( (float)w ) / width;
		float scaleHeight = ( (float)h ) / height;
		float scale = Math.min( scaleWidth , scaleHeight );
		if( scale < 1 )
		{
			Bitmap tmp = Bitmap.createScaledBitmap( bm , (int)( width * scale ) , (int)( height * scale ) , true );
			if( tmp != bm )
				bm.recycle();
			return tmp;
		}
		else
			return bm;
	}
	
	/**
	 * 图片反转
	 * 
	 * @param bm
	 * @param flag
	 *            0为水平反转，1为垂直反转
	 * @return
	 */
	public static Bitmap reverseBitmap(
			Bitmap bmp ,
			int flag )
	{
		float[] floats = null;
		switch( flag )
		{
			case 0: // 水平反转
				floats = new float[]{ -1f , 0f , 0f , 0f , 1f , 0f , 0f , 0f , 1f };
				break;
			case 1: // 垂直反转
				floats = new float[]{ 1f , 0f , 0f , 0f , -1f , 0f , 0f , 0f , 1f };
				break;
		}
		if( floats != null )
		{
			Matrix matrix = new Matrix();
			matrix.setValues( floats );
			return Bitmap.createBitmap( bmp , 0 , 0 , bmp.getWidth() , bmp.getHeight() , matrix , true );
		}
		return null;
	}
	
	public static int dip2px(
			Context context ,
			float dipValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( dipValue * scale + 0.5f );
	}
	
	public static int px2dip(
			Context context ,
			float pxValue )
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)( pxValue / scale + 0.5f );
	}
	
	public static TextureRegion getTextureByPicName(
			String picname ,
			int width ,
			int height )
	{
		Bitmap bmp = null;
		bmp = ThemeManager.getInstance().getBitmap( picname );
		//		try
		//		{
		//			bmp = BitmapFactory.decodeStream( iLoongApplication.getInstance().getAssets().open( picname ) );
		//		}
		//		catch( IOException e )
		//		{
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		TextureRegion textureRegion = null;
		if( bmp != null )
		{
			Texture texture = null;
			if( width < bmp.getWidth() && height < bmp.getHeight() )
			{
				oldnum += ( ( bmp.getWidth() * bmp.getHeight() * 4 ) / 1024f );
				//Log.v( "" , "oldbitmap width is " + bmp.getWidth() + " height is " + bmp.getHeight() + " oldnum is " + oldnum );
				Bitmap newBmp = Bitmap.createScaledBitmap( bmp , width , height , true );
				texture = new BitmapTexture( newBmp );
				newnum += ( ( newBmp.getWidth() * newBmp.getHeight() * 4 ) / 1024f );
				//Log.v( "" , "newbitmap width is " + newBmp.getWidth() + " height is " + newBmp.getHeight() + " newnum is " + newnum );
				newBmp.recycle();
				if( newBmp != bmp )
				{
					bmp.recycle();
				}
				newBmp = null;
				bmp = null;
				//				texture = new BitmapTexture( bmp );
			}
			else
			{
				texture = new BitmapTexture( bmp );
				bmp.recycle();
				bmp = null;
			}
			if( texture != null )
			{
				texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				textureRegion = new TextureRegion( texture );
			}
		}
		return textureRegion;
	}
	
	public static TextureRegion getTextureByPicName(
			String picname ,
			int width ,
			int height,boolean ifThemePic )
	{
		Bitmap bmp = null;
		bmp = ThemeManager.getInstance().getBitmap( picname ,ifThemePic);
		TextureRegion textureRegion = null;
		if( bmp != null )
		{
			Texture texture = null;
			if( width < bmp.getWidth() && height < bmp.getHeight() )
			{
				oldnum += ( ( bmp.getWidth() * bmp.getHeight() * 4 ) / 1024f );
				Bitmap newBmp = Bitmap.createScaledBitmap( bmp , width , height , true );
				texture = new BitmapTexture( newBmp );
				newnum += ( ( newBmp.getWidth() * newBmp.getHeight() * 4 ) / 1024f );
				newBmp.recycle();
				if( newBmp != bmp )
				{
					bmp.recycle();
				}
				newBmp = null;
				bmp = null;
			}
			else
			{
				texture = new BitmapTexture( bmp );
				bmp.recycle();
				bmp = null;
			}
			if( texture != null )
			{
				texture.setFilter( TextureFilter.Linear , TextureFilter.Linear );
				textureRegion = new TextureRegion( texture );
			}
		}
		return textureRegion;
	}
	
	public static Bitmap getBitmapByPicName(
			String picname ,
			int width ,
			int height )
	{
		Bitmap bmp = null;
		try
		{
			bmp = BitmapFactory.decodeStream( iLoongApplication.getInstance().getAssets().open( picname ) );
		}
		catch( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( bmp != null )
		{
			if( width < bmp.getWidth() && height < bmp.getHeight() )
			{
				//oldnum += ( ( bmp.getWidth() * bmp.getHeight() * 4 ) / 1024f );
				//Log.v( "" , "oldbitmap width is " + bmp.getWidth() + " height is " + bmp.getHeight() +" oldnum is "+ oldnum);
				Bitmap newBmp = Bitmap.createScaledBitmap( bmp , width , height , true );
				//newnum += ( ( newBmp.getWidth() * newBmp.getHeight() * 4 ) / 1024f );
				//Log.v( "" , "newbitmap width is " + newBmp.getWidth() + " height is " + newBmp.getHeight()+" newnum is " + newnum );
				if( newBmp != bmp )
				{
					bmp.recycle();
				}
				bmp = newBmp;
				//				texture = new BitmapTexture( bmp );
			}
		}
		return bmp;
	}
}
