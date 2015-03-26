package com.iLoong.Clean;


import com.iLoong.Clean.view.WidgetClean;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;


public class iLoongClean implements IWidget3DPlugin
{
	
	@Override
	public View3D getWidget(
			MainAppContext context ,
			int widgetId )
	{
		return new WidgetClean( "widgetClean" , context , widgetId );
	}
	
	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext arg0 ,
			int arg1 )
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void preInitialize(
			MainAppContext arg0 )
	{
		// TODO Auto-generated method stub
	}
}
