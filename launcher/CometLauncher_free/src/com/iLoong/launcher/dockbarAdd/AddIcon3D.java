package com.iLoong.launcher.dockbarAdd;


import android.graphics.Bitmap;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.iLoong.launcher.Desktop3D.Icon3D;


public class AddIcon3D extends Icon3D
{
	
	public AddIcon3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
	}
	
	public AddIcon3D(
			String name ,
			TextureRegion t )
	{
		super( name , t );
		// TODO Auto-generated constructor stub
	}
	
	public AddIcon3D(
			String name ,
			Bitmap b ,
			String title )
	{
		super( name , b , title );
		// TODO Auto-generated constructor stub
	}
	
	public AddIcon3D(
			String name ,
			Bitmap bmp ,
			String title ,
			Bitmap IconBg )
	{
		super( name , bmp , title , IconBg );
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onParticleCallback(
			int type )
	{
		// TODO Auto-generated method stub
	}
}
