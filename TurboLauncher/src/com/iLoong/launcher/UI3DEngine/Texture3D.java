package com.iLoong.launcher.UI3DEngine;


import android.graphics.Bitmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.iLoong.launcher.Desktop3D.R3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class Texture3D extends Texture
{
	
	public Texture3D(
			Gdx gdx ,
			Pixmap pixmap )
	{
		this( pixmap );
	}
	
	public Texture3D(
			Bitmap bmp ,
			boolean usePixmap )
	{
		super( new BitmapTextureData( bmp ) );
		setFilter( R3D.filter , R3D.filter );
		//		MyPixmapPacker.texture3DSize += ((float)bmp.getWidth()*bmp.getHeight()*4)/(float)1000/(float)1000;
	}
	
	public Texture3D(
			Bitmap bmp )
	{
		this( Utils3D.bmp2Pixmap( bmp ) );
		//		super(new BitmapTextureData(bmp));
		//		setFilter(R3D.filter,R3D.filter);
	}
	
	public Texture3D(
			Pixmap pixmap )
	{
		super( new MyPixmapTextureData( pixmap , null , false , !iLoongLauncher.releaseTexture ) );
		setFilter( R3D.filter , R3D.filter );
		//		float tmp = ((float)pixmap.getWidth()*pixmap.getHeight()*4)/(float)1000/(float)1000;
		//		Log.d("pack", "texture3D:"+tmp);
		//		MyPixmapPacker.texture3DSize += tmp;
		//PixmapIO.writePNG(new FileHandle("/mnt/sdcard/" + "pack/" + System.currentTimeMillis() + ".png"), pixmap);
	}
	//	public Texture3D(Pixmap pixmap,boolean disposePixmap) {
	//		super(new MyPixmapTextureData(pixmap,null,false ,disposePixmap));
	//		setFilter(R3D.filter,R3D.filter);
	//	}
	//	
	//	public Texture3D (Pixmap pixmap, Format format, boolean useMipMaps) {
	//		super(new MyPixmapTextureData(pixmap, format, useMipMaps, false));
	//	}
	//
	//	public Texture3D (int width, int height, Format format) {
	//		super(new MyPixmapTextureData(new Pixmap(width, height, format), null, false, true));
	//	}	
}

class MyPixmapTextureData extends PixmapTextureData
{
	
	boolean managed;
	
	public MyPixmapTextureData(
			Pixmap pixmap ,
			Format format ,
			boolean useMipMaps ,
			boolean disposePixmap )
	{
		super( pixmap , format , useMipMaps , disposePixmap );
		managed = true;
	}
	
	@Override
	public boolean isManaged()
	{
		return managed;
	}
}
