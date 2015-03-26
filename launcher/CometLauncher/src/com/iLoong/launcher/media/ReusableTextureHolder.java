package com.iLoong.launcher.media;


import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.Pixmap;
import com.iLoong.launcher.media.ThumbnailThread.ThumbnailClient;


public class ReusableTextureHolder implements ThumbnailClient
{
	
	public ReusableTexture texture;
	public Pixmap pixmap;
	public boolean disposed = true;
	public boolean free = true;
	
	public void prepare(
			int priority )
	{
		if( !free || !disposed )
			return;
		free = false;
		ThumbnailThread.getInstance().push( this , priority );
	}
	
	public void free()
	{
		if( free )
			return;
		ThumbnailThread.getInstance().delete( this );
		free = true;
		if( texture != null )
			ReusableTexturePool.getInstance().free( texture );
		if( !disposed )
		{
			pixmap.dispose();
			disposed = true;
		}
		texture = null;
	}
	
	@Override
	public long getThumbnailId()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getThumbnailPath()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setThumbnailBmp(
			Bitmap bmp )
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public int getResType()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
