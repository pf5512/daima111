package com.iLoong.launcher.Functions.Tab;


import com.iLoong.launcher.Desktop3D.Log;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class TabTitle3D extends ViewGroup3D
{
	
	protected TabTitlePlugin3D mTabTitlePlugin;
	public TabContext tabContext;
	public TabPluginMetaData pluginMetaData;
	
	public TabTitle3D(
			String name ,
			TabTitlePlugin3D tabTitlePlugin )
	{
		super( name );
		this.mTabTitlePlugin = tabTitlePlugin;
		addView( tabTitlePlugin );
		this.tabContext = tabTitlePlugin.mTabContext;
		this.pluginMetaData = tabTitlePlugin.mPluginMetaData;
		// 这一步必须要做，否则生成的View宽度和高度比较大，点击焦点位置会失灵
		this.width = tabTitlePlugin.width;
		this.height = tabTitlePlugin.height;
		this.setOrigin( this.width / 2 , this.height / 2 );
		transform = true;
	}
	
	public void onDelete()
	{
		mTabTitlePlugin.onDelete();
	}
	
	public void onStart()
	{
		mTabTitlePlugin.onStart();
	}
	
	public void onResume()
	{
		mTabTitlePlugin.onResume();
	}
	
	public void onPause()
	{
		mTabTitlePlugin.onPause();
	}
	
	public void onStop()
	{
		mTabTitlePlugin.onStop();
	}
	
	public void onDestroy()
	{
		mTabTitlePlugin.onDestroy();
	}
	
	public void dispose()
	{
		mTabTitlePlugin.dispose();
	}
	
	public void onUninstall()
	{
		mTabTitlePlugin.onUninstall();
	}
	
	public void onThemeChanged()
	{
		mTabTitlePlugin.onThemeChanged();
	}
	
	public void setPadding(
			float paddingLeft ,
			float paddingTop ,
			float paddingRight ,
			float paddingBottom )
	{
		mTabTitlePlugin.setPadding( paddingLeft , paddingTop , paddingRight , paddingBottom );
	}
	
	public void onEntry()
	{
		Log.v( "TabTitle3D" , "TabTitle3D onEntry:" + pluginMetaData.pluginId + " package:" + pluginMetaData.packageName );
		mTabTitlePlugin.onEntry();
	}
	
	public void onLeave()
	{
		// TODO Auto-generated method stub
		Log.v( "TabTitle3D" , "TabTitle3D onLeave:" + pluginMetaData.pluginId + " package:" + pluginMetaData.packageName );
		mTabTitlePlugin.onLeave();
	}
}
