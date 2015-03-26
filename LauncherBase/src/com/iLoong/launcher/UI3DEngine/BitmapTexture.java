package com.iLoong.launcher.UI3DEngine;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.iLoong.launcher.Desktop3D.Log;


public class BitmapTexture extends Texture
{
	
	BitmapTextureData data;
	
	public BitmapTexture(
			Bitmap bmp )
	{
		super( new BitmapTextureData( bmp ) );
		data = (BitmapTextureData)this.getTextureData();
		setFilter( ConfigBase.filter , ConfigBase.Magfilter );
	}
	
	public BitmapTexture(
			Bitmap bmp ,
			boolean recycle )
	{
		super( new BitmapTextureData( bmp ) );
		data = (BitmapTextureData)this.getTextureData();
		setFilter( ConfigBase.filter , ConfigBase.Magfilter );
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
