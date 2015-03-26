package com.iLoong.launcher.Functions.Tab;


public interface ITabContentPlugin
{
	
	// public TabIndicatorPlugin3D getTabIndicator(TabContext tabContext);
	public TabContentPlugin3D getTabContent(
			TabContext tabContext ,
			TabPluginMetaData pluginMetaData );
}
