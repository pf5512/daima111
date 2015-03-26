package com.iLoong.launcher.UI3DEngine;


import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class ImageView3D extends View3D
{
	
	public ImageView3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public ImageView3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
	}
	
	public ImageView3D(
			String name ,
			Texture texture ,
			int width ,
			int height )
	{
		super( name , texture );
		this.setSize( width , height );
		// TODO Auto-generated constructor stub
	}
	
	public ImageView3D(
			String name ,
			TextureRegion textureRegion )
	{
		super( name , textureRegion );
		// TODO Auto-generated constructor stub
	}
	
	public ImageView3D(
			String name ,
			TextureRegion textureRegion ,
			int width ,
			int height )
	{
		super( name , textureRegion );
		this.setSize( width , height );
		// TODO Auto-generated constructor stub
	}
	
	public ImageView3D(
			String name ,
			Bitmap bmp )
	{
		super( name , new BitmapTexture( bmp ) );
	}
}
