package com.iLoong.Calender.common;


import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Pixmap.Format;


public class MyBitmapTextureData implements TextureData
{
	
	private Bitmap bmp;
	public boolean disposed = false;
	public boolean change = false;
	private int width = 0;
	private int height = 0;
	
	public MyBitmapTextureData(
			Bitmap bitmap )
	{
		if( MyConfigBase.releaseGL )
			this.bmp = bitmap.copy( bitmap.getConfig() , true );
		else
			this.bmp = bitmap;
		width = bmp.getWidth();
		height = bmp.getHeight();
	}
	
	public void setBitmap(
			Bitmap bitmap )
	{
		if( MyConfigBase.releaseGL )
			this.bmp = bitmap.copy( bitmap.getConfig() , true );
		else
			this.bmp = bitmap;
		width = bmp.getWidth();
		height = bmp.getHeight();
		disposed = false;
	}
	
	public void changeBitmap(
			Bitmap bitmap )
	{
		if( MyConfigBase.releaseGL )
			this.bmp = bitmap.copy( bitmap.getConfig() , true );
		else
			this.bmp = bitmap;
		width = bmp.getWidth();
		height = bmp.getHeight();
		change = true;
	}
	
	@Override
	public int getWidth()
	{
		//if(bmp == null || bmp.isRecycled())return 0;
		return width;
	}
	
	@Override
	public int getHeight()
	{
		//if(bmp == null || bmp.isRecycled())return 0;
		return height;
	}
	
	@Override
	public TextureDataType getType()
	{
		return TextureDataType.Compressed;
	}
	
	@Override
	public boolean isPrepared()
	{
		return true;
	}
	
	@Override
	public void prepare()
	{
	}
	
	@Override
	public Pixmap consumePixmap()
	{
		return null;
	}
	
	@Override
	public boolean disposePixmap()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void consumeCompressedData()
	{
		// TODO Auto-generated method stub
		if( bmp != null && !bmp.isRecycled() )
		{
			if( !change )
				GLUtils.texImage2D( GL10.GL_TEXTURE_2D , 0 , bmp , 0 );
			else
				GLUtils.texSubImage2D( GL10.GL_TEXTURE_2D , 0 , 0 , 0 , bmp );
			bmp = null;
		}
		else
		{
//			Log.e( "launcher" , "consumeCompressedData:dispose" );
			disposed = true;
		}
	}
	
	@Override
	public Format getFormat()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean useMipMaps()
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isManaged()
	{
		// TODO Auto-generated method stub
		return true;
	}
}
