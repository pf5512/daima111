package com.iLoong.launcher.HotSeat3D;


import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.Desktop3D.Root3D;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;
import com.iLoong.launcher.UI3DEngine.ViewGroupOBJ3D;
import com.iLoong.launcher.desktop.iLoongLauncher;


public class DockbarObjcetGroup extends ViewGroup3D
{
	
	//this flag indicates the menu opening animal 
	public static boolean isStart = false;
	private float yScale;
	
	public DockbarObjcetGroup(
			String name )
	{
		super( name );
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		yScale = 0;
		if( x >= 0 && x < width && y >= 0 && y < height )
		{
			Log.v( "hotseat" , "onTouchDown  on other size" );
			return super.onTouchDown( x , y , pointer );
		}
		else
		{
			isStart = true;
			Root3D.getInstance().workspaceAnimWhenHotseatRotation();
			HotObjMenuFront.getInstance().startRotation( 0.5f );
			Log.v( "hotseat" , "onTouchDown  start to close menu" );
			return true;
		}
	}
	
	@Override
	public boolean onTouchUp(
			float x ,
			float y ,
			int pointer )
	{
		releaseAllButtonDark();
		return super.onTouchUp( x , y , pointer );
	}
	
	@Override
	public boolean scroll(
			float x ,
			float y ,
			float deltaX ,
			float deltaY )
	{
		// TODO Auto-generated method stub
		if( iLoongLauncher.getInstance().getD3dListener().getDragLayer().isVisible() )
		{
			return super.scroll( x , y , deltaX , deltaY );
		}
		yScale = yScale + deltaY / this.height;
		if( yScale > 0.15f )
		{
			ObjButton.hasTouchDown = false;
		}
		if( yScale > 0.3f )
		{
			isStart = true;
			releaseAllButtonDark();
			Root3D.getInstance().workspaceAnimWhenHotseatRotation();
			HotObjMenuFront.getInstance().startRotation( 0.5f );
			yScale = 0;
			return true;
		}
		return false;
	}
	
	private void releaseAllButtonDark()
	{
		for( int i = 0 ; i < this.getChildCount() ; i++ )
		{
			View3D child = this.getChildAt( i );
			if( child instanceof ViewGroupOBJ3D )
			{
				for( int j = 0 ; j < ( (ViewGroup3D)child ).getChildCount() ; j++ )
				{
					View3D childView = ( (ViewGroup3D)child ).getChildAt( j );
					if( childView instanceof ObjButton )
					{
						childView.releaseDark();
					}
				}
			}
		}
	}
}
