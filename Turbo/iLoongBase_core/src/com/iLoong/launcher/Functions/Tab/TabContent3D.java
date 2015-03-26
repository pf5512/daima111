package com.iLoong.launcher.Functions.Tab;


import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class TabContent3D extends ViewGroup3D
{
	
	protected TabContext mTabContext;
	protected TabContentPlugin3D mTabContentPlugin;
	public TabPluginMetaData pluginMetaData;
	
	public TabContent3D(
			String name ,
			TabContentPlugin3D tabContentPlugin )
	{
		super( name );
		this.mTabContentPlugin = tabContentPlugin;
		this.pluginMetaData = tabContentPlugin.mPluginMetaData;
		addView( tabContentPlugin );
		// 这一步必须要做，否则生成的View宽度和高度比较大，点击焦点位置会失灵
		this.width = tabContentPlugin.width;
		this.height = tabContentPlugin.height;
		this.setOrigin( this.width / 2 , this.height / 2 );
		transform = true;
	}
	
	public void onDelete()
	{
		mTabContentPlugin.onDelete();
	}
	
	public void onStart()
	{
		mTabContentPlugin.onStart();
	}
	
	public void onResume()
	{
		mTabContentPlugin.onResume();
	}
	
	public void onPause()
	{
		mTabContentPlugin.onPause();
	}
	
	public void onStop()
	{
		mTabContentPlugin.onStop();
	}
	
	public void onDestroy()
	{
		mTabContentPlugin.onDestroy();
	}
	
	public void onKeyEvent(
			int keycode ,
			int keyEventCode )
	{
		mTabContentPlugin.onKeyEvent( keycode , keyEventCode );
	}
	
	public void dispose()
	{
		mTabContentPlugin.dispose();
	}
	
	public void onUninstall()
	{
		mTabContentPlugin.onUninstall();
	}
	
	public void onThemeChanged()
	{
		// TODO Auto-generated method stub
		mTabContentPlugin.onThemeChanged();
	}
	
	public void onEntry()
	{
		Log.v( "TabContent3D" , "TabContent3D onEntry:" + pluginMetaData.pluginId + " package:" + pluginMetaData.packageName );
		mTabContentPlugin.onEntry();
	}
	
	public void onLeave()
	{
		// TODO Auto-generated method stub
		Log.v( "TabContent3D" , "TabContent3D onLeave:" + pluginMetaData.pluginId + " package:" + pluginMetaData.packageName );
		mTabContentPlugin.onLeave();
	}
}
