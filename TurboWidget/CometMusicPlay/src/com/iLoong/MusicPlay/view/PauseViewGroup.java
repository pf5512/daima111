package com.iLoong.MusicPlay.view;

import com.badlogic.gdx.math.Vector2;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class PauseViewGroup extends ViewGroup3D
{
	public PauseViewGroup(String name)
	{
		super(name);
		this.transform = true;
	}

	public boolean is3dRotation() {
		return true;
	}
	
	@Override
	public boolean pointerInParent(
			float x ,
			float y )
	{
		// TODO Auto-generated method stub
		boolean ret = view3dTouchCheck.isPointIn( this , this.getX() ,this.getY() , this.getWidth() , this.getHeight() , x , y );
		return ret;
	}
	
	@Override
	public boolean toLocalCoordinates(
			View3D descendant ,
			Vector2 point )
	{
		return true;
	}
}
