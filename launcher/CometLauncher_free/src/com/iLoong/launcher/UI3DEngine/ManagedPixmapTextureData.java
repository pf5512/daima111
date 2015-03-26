package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;


public class ManagedPixmapTextureData extends PixmapTextureData
{
	
	Pixmap pixmap;
	
	public ManagedPixmapTextureData(
			Pixmap pixmap ,
			Format format ,
			boolean useMipMaps )
	{
		super( pixmap , format , useMipMaps , false );
		this.pixmap = pixmap;
	}
	
	@Override
	public boolean isManaged()
	{
		return true;
	}
	
	public void setPixmap(
			Pixmap _pixmap )
	{
		pixmap = _pixmap;
	}
	
	@Override
	public Pixmap consumePixmap()
	{
		return pixmap;
	}
	
	@Override
	public int getWidth()
	{
		return pixmap.getWidth();
	}
	
	@Override
	public int getHeight()
	{
		return pixmap.getHeight();
	}
}
