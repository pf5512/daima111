package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class ViewCircled3D extends View3D
{
	
	private boolean bCircled = false;
	private boolean canCircled = true;
	
	public ViewCircled3D(
			String name )
	{
		super( name );
		bCircled = false;
		// TODO Auto-generated constructor stub
	}
	
	public ViewCircled3D(
			String name ,
			Texture texture )
	{
		super( name , texture );
		bCircled = false;
	}
	
	public ViewCircled3D(
			String name ,
			TextureRegion region )
	{
		super( name , region );
		bCircled = false;
	}
	
	public boolean getCircled()
	{
		return bCircled;
	}
	
	public void setCircled(
			boolean bCircled )
	{
		this.bCircled = bCircled;
	}
	
	public void setCanCircle(
			boolean b )
	{
		canCircled = b;
	}
	
	public boolean canCircle()
	{
		return canCircled;
	}
}
