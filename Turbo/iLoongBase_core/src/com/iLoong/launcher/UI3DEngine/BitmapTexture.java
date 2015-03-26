package com.iLoong.launcher.UI3DEngine;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.iLoong.launcher.Desktop3D.Log;


public class BitmapTexture extends Texture
{
	
	private static List<BitmapTexture> dynamicTextures;
	private static boolean needDynamicLoad = false;
	private BitmapTextureData data;
	public static int totalSize = 0;
	public static int num = 0;
	public static long totalDecodeTime;
	public static long totalSaveTime;
	private boolean dynamicLoad = false;
	private ReloadCallback callback = null;
	public static long dynamicLoadTime = 0;
	
	public BitmapTexture(
			Gdx gdx ,
			Bitmap bmp )
	{
		this( bmp , false , false , null );
	}
	
	public BitmapTexture(
			Bitmap bmp )
	{
		this( bmp , false , false , null );
	}
	
	public BitmapTexture(
			Bitmap bmp ,
			boolean recycle )
	{
		this( bmp , recycle , false , null );
	}
	
	public BitmapTexture(
			Bitmap bmp ,
			boolean recycle ,
			boolean dynamicLoad ,
			ReloadCallback callback )
	{
		//		super( new MyPixmapTextureData( UtilsBase.bmp2Pixmap( bmp , recycle ) , null , false , false ) );
		//		setFilter( TextureFilter.Linear , TextureFilter.Linear );
		super( new BitmapTextureData( bmp , dynamicLoad ) );
		data = (BitmapTextureData)this.getTextureData();
		setFilter( ConfigBase.filter , ConfigBase.Magfilter );
		//String name = System.currentTimeMillis() + "";
		//totalSaveTime += UtilsBase.saveBmp( bmp , name );
		//		long time = System.currentTimeMillis();
		//		Bitmap tmp = BitmapFactory.decodeFile( "/mnt/sdcard/" + name + ".png" );
		//		totalDecodeTime += System.currentTimeMillis() - time;
		//		Log.d( "opt" , "opt save:" + name + ",totalTime:" + totalSaveTime );
		//		Log.d( "opt" , "opt decode:" + name + ",time:" + ( System.currentTimeMillis() - time ) + ",totalTime:" + totalDecodeTime );
		//		tmp.recycle();
		//		tmp = null;
		if( recycle )
			bmp.recycle();
		//		int size = bmp.getWidth() * bmp.getHeight() * 4;
		//		totalSize += size;
		//		num++;
		//		if( size < 50000 || ( size == 57276 ) || dynamicLoad )
		//			Log.d( "opt" , "opt width,height=" + bmp.getWidth() + "," + bmp.getHeight() + "," + size + "," + totalSize + "," + num );
		//		else
		//			Log.i( "opt" , "opt width,height=" + bmp.getWidth() + "," + bmp.getHeight() + "," + size + "," + totalSize + "," + num );
		if( dynamicTextures == null )
		{
			if( ConfigBase.releaseGL )
				dynamicTextures = new ArrayList<BitmapTexture>();
		}
		if( dynamicTextures != null )
		{
			if( dynamicLoad )
			{
				this.dynamicLoad = dynamicLoad;
				this.callback = callback;
				dynamicTextures.add( this );
			}
		}
	}
	
	public static void onTrimMemory()
	{
		if( dynamicTextures == null )
			return;
		long time = System.currentTimeMillis();
		BitmapTexture tmp;
		for( int i = 0 ; i < dynamicTextures.size() ; i++ )
		{
			tmp = dynamicTextures.get( i );
			if( !tmp.isDisposed() )
			{
				Log.i( "opt" , "opt dispose:" + tmp.getWidth() + "," + tmp.getHeight() + "," + ( tmp.getWidth() * tmp.getHeight() * 4 / 1000 ) );
				tmp.dispose();
				needDynamicLoad = true;
				BitmapTexture.dynamicLoadTime = 0;
			}
		}
		Log.i( "opt" , "opt onTrimMemory time:" + ( System.currentTimeMillis() - time ) );
	}
	
	public static void onReloadTextures()
	{
		if( !needDynamicLoad )
			return;
		if( dynamicTextures == null || dynamicTextures.size() <= 0 )
		{
			needDynamicLoad = false;
			return;
		}
		if( System.currentTimeMillis() < dynamicLoadTime )
		{
			Gdx.graphics.requestRendering();
			return;
		}
		BitmapTexture tmp;
		needDynamicLoad = false;
		long start = System.currentTimeMillis();
		for( int i = 0 ; i < dynamicTextures.size() ; i++ )
		{
			tmp = dynamicTextures.get( i );
			if( tmp.isDisposed() )
			{
				tmp.dynamicLoad();
				needDynamicLoad = true;
				if( System.currentTimeMillis() - start > 500 )
				{
					dynamicLoadTime = System.currentTimeMillis() + 1000;
					return;
				}
			}
		}
	}
	
	public void dynamicLoad()
	{
		if( ConfigBase.releaseGL && dynamicLoad && data.disposed && callback != null )
		{
			long time = System.currentTimeMillis();
			Bitmap tmp = callback.reload();
			if( tmp != null )
			{
				data.setBitmap( tmp );
				reload();
				Log.i( "opt" , "opt load:" + tmp.getWidth() + "," + tmp.getHeight() + "," + ( tmp.getWidth() * tmp.getHeight() * 4 / 1000 ) + "," + ( System.currentTimeMillis() - time ) );
				tmp.recycle();
				tmp = null;
			}
		}
	}
	
	public void changeBitmap(
			Bitmap bmp )
	{
		changeBitmap( bmp , false );
	}
	
	public void changeBitmap(
			Bitmap bmp ,
			boolean recycle )
	{
		data.setBitmap( bmp );
		if( data.disposed )
		{
			reload();
		}
		else
		{
			Gdx.gl.glBindTexture( GL10.GL_TEXTURE_2D , this.getTextureObjectHandle() );
			GLUtils.texImage2D( GL10.GL_TEXTURE_2D , 0 , bmp , 0 );
		}
		if( recycle )
			bmp.recycle();
	}
	
	public void changeFilter(
			TextureFilter minFilter ,
			TextureFilter magFilter )
	{
		if( minFilter == this.getMinFilter() && magFilter == this.getMagFilter() )
			return;
		setFilter( minFilter , magFilter );
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		data.dispose();
	}
	
	public boolean isDisposed()
	{
		return data.disposed;
	}
}
