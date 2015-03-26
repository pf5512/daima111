package com.iLoong.Calender.common;


import android.graphics.Bitmap;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;


public class MyBitmapTexture extends Texture
{
	
	MyBitmapTextureData data;
	
	public MyBitmapTexture(
			Bitmap bmp )
	{
		super( new MyBitmapTextureData( bmp ) );
		data = (MyBitmapTextureData)this.getTextureData();
		setFilter( MyConfigBase.filter , MyConfigBase.Magfilter );
	}
	
	public MyBitmapTexture(
			Bitmap bmp ,
			boolean recycle )
	{
		super( new MyBitmapTextureData( bmp ) );
		data = (MyBitmapTextureData)this.getTextureData();
		setFilter( MyConfigBase.filter , MyConfigBase.Magfilter );
		if( recycle )
			bmp.recycle();
	}
	
	public void changeBitmap(
			Bitmap bmp )
	{
		data.setBitmap( bmp );
		Gdx.gl.glBindTexture( GL10.GL_TEXTURE_2D , this.getTextureObjectHandle() );
		GLUtils.texSubImage2D( GL10.GL_TEXTURE_2D , 0 , 0 , 0 , bmp );
	}
	
	public void changeFilter(
			TextureFilter minFilter ,
			TextureFilter magFilter )
	{
		if( minFilter == this.getMinFilter() && magFilter == this.getMagFilter() )
			return;
		setFilter( minFilter , magFilter );
	}
	
	public boolean isDisposed()
	{
		return data.disposed;
	}
}
