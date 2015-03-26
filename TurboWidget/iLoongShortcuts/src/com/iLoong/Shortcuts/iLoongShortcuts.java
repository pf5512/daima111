package com.iLoong.Shortcuts;


import android.content.Context;
import android.util.Log;

import com.iLoong.Shortcuts.View.WidgetShortcuts;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.UI3DEngine.adapter.CooGdx;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;


public class iLoongShortcuts implements IWidget3DPlugin
{
	
	CooGdx cooGdx = null;
	
	@Override
	public View3D getWidget(
			MainAppContext globalContext ,
			int widgetId )
	{
		// TODO Auto-generated method stub
		return new WidgetShortcuts( "Toggle" , globalContext , widgetId );
	}
	
	@SuppressWarnings( "unchecked" )
	@Override
	public void preInitialize(
			MainAppContext context )
	{
	}
	
	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext mainAppContext ,
			int widgetId )
	{
		Context mContext = mainAppContext.mWidgetContext;
		WidgetPluginViewMetaData metaData = new WidgetPluginViewMetaData();
		metaData.spanX = Integer.valueOf( mContext.getResources().getInteger( R.integer.spanX ) );
		metaData.spanY = Integer.valueOf( mContext.getResources().getInteger( R.integer.spanY ) );
		metaData.maxInstanceCount = mContext.getResources().getInteger( R.integer.max_instance );
		metaData.maxInstanceAlert = mContext.getResources().getString( R.string.max_instance_alert );
		metaData.iconResourceId = R.drawable.widget_ico;
		Log.d( "song" , "WidgetPluginViewMetaData" );
		return metaData;
	}
}
