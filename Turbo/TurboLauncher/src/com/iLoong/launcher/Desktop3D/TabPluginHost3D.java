package com.iLoong.launcher.Desktop3D;


import java.util.ArrayList;

import android.view.KeyEvent;

import com.iLoong.launcher.Functions.Tab.Plugin;
import com.iLoong.launcher.Functions.Tab.TabContent3D;
import com.iLoong.launcher.Functions.Tab.TabPluginManager;
import com.iLoong.launcher.Functions.Tab.TabTitle3D;
import com.iLoong.launcher.UI3DEngine.Utils3D;
import com.iLoong.launcher.UI3DEngine.ViewGroup3D;


public class TabPluginHost3D extends ViewGroup3D
{
	
	public static final int TABPLUGIN_HOST_SHOW = 0;
	public static final int TABPLUGIN_HOST_HIDE = 1;
	public static final int TABPLUGIN_HOST_KEY_BACK = 2;
	public AppHost3D appHost;
	
	public TabPluginHost3D()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	public TabPluginHost3D(
			String name )
	{
		super( name );
		// TODO Auto-generated constructor stub
		x = 0;
		y = 0;
		width = Utils3D.getScreenWidth();
		height = Utils3D.getScreenHeight() - R3D.appbar_height;
	}
	
	@Override
	public void show()
	{
		// TODO Auto-generated method stub
		super.show();
		viewParent.onCtrlEvent( this , TABPLUGIN_HOST_SHOW );
	}
	
	@Override
	public void hide()
	{
		// TODO Auto-generated method stub
		super.hide();
		viewParent.onCtrlEvent( this , TABPLUGIN_HOST_HIDE );
	}
	
	public void onTabChange(
			Plugin plugin )
	{
		this.clear();
		TabContent3D tabContent = plugin.buildTabContent3D();
		TabTitle3D tabTitle = plugin.getTabTitle();
		if( tabTitle != null )
		{
			tabTitle.onEntry();
		}
		if( tabContent != null )
		{
			if( tabContent.height > this.height )
			{
				tabContent.height = this.height;
			}
			if( tabContent.width > this.width )
			{
				tabContent.width = this.width;
			}
			tabContent.onEntry();
			this.addView( tabContent );
		}
	}
	
	@Override
	public boolean keyUp(
			int keycode )
	{
		// TODO Auto-generated method stub
		if( appHost.appBar != null && ( appHost.appBar.tabIndicator.tabId == AppBar3D.TAB_PLUGIN || appHost.appBar.tabIndicator.tabId == AppBar3D.TAB_MORE ) )
		{
			if( keycode == KeyEvent.KEYCODE_BACK )
			{
				boolean result = super.keyUp( keycode );
				if( !result )
				{
					viewParent.onCtrlEvent( this , TABPLUGIN_HOST_KEY_BACK );
				}
				return true;
			}
		}
		return false;
	}
	
	public void onLeave()
	{
		// TODO Auto-generated method stub
		hide();
		ArrayList<Plugin> plugins = TabPluginManager.getInstance().getPluginList();
		for( int i = 0 ; i < plugins.size() ; i++ )
		{
			Plugin plugin = plugins.get( i );
			if( plugin.mSelected )
			{
				plugin.cancelSelected();
				break;
			}
		}
	}
}
