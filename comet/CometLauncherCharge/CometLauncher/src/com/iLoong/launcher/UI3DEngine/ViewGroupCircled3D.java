package com.iLoong.launcher.UI3DEngine;


import com.badlogic.gdx.graphics.Color;


public class ViewGroupCircled3D extends ViewGroup3D
{
	
	private boolean bCircled = false;
	private boolean canCircled = true;
	
	public ViewGroupCircled3D()
	{
		this( null );
		bCircled = false;
		// TODO Auto-generated constructor stub
	}
	
	public ViewGroupCircled3D(
			String name )
	{
		super( name );
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
	
	/* (non-Javadoc)
	 * @see com.iLoong.launcher.UI3DEngine.View3D#setColor(com.badlogic.gdx.graphics.Color)
	 */
	public void setGroupColor(
			Color color )
	{
		// TODO Auto-generated method stub
		int Count = getChildCount();
		View3D myActor;
		for( int i = 0 ; i < Count ; i++ )
		{
			myActor = getChildAt( i );
			myActor.setColor( color );
		}
		//super.setColor(color);
	}
}
