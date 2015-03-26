package com.iLoong.launcher.menu3D;


import com.coco.theme.themebox.util.Log;
import com.iLoong.launcher.UI3DEngine.View3D;


public class SystemSetting extends View3D
{
	
	@Override
	public boolean onClick(
			float x ,
			float y )
	{
		Log.v( "Hotseat" , "SystemSetting on onClick" );
		return true;
	}
	
	@Override
	public boolean onTouchDown(
			float x ,
			float y ,
			int pointer )
	{
		Log.v( "Hotseat" , "SystemSetting on touchdown" );
		return true;
	}
	
	public SystemSetting(
			String name )
	{
		super( name );
	}
}
