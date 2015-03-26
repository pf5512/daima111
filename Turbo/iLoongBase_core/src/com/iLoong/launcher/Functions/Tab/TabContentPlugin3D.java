package com.iLoong.launcher.Functions.Tab;


import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class TabContentPlugin3D extends ViewGroup3D
{
	
	protected TabContext mTabContext;
	protected TabPluginMetaData mPluginMetaData;
	
	public TabContentPlugin3D(
			TabContext tabContext ,
			TabPluginMetaData pluginMetaData )
	{
		super( "TabPlugin" );
		this.mTabContext = tabContext;
		this.mPluginMetaData = pluginMetaData;
	}
	
	public void onDelete()
	{
	}
	
	public void onStart()
	{
	}
	
	public void onResume()
	{
	}
	
	public void onPause()
	{
	}
	
	public void onStop()
	{
	}
	
	public void onDestroy()
	{
	}
	
	public void onKeyEvent(
			int keycode ,
			int keyEventCode )
	{
	}
	
	public void dispose()
	{
	}
	
	public void onUninstall()
	{
	}
	
	public void onThemeChanged()
	{
		// TODO Auto-generated method stub
	}
	
	public void onEntry()
	{
		// TODO Auto-generated method stub
	}
	
	public void onLeave()
	{
		// TODO Auto-generated method stub
	}
}
