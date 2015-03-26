package com.iLoong.Clean4;


import com.iLoong.Clean4.view.WidgetClean4;
import com.iLoong.launcher.UI3DEngine.View3D;
import com.iLoong.launcher.Widget3D.IWidget3DPlugin;
import com.iLoong.launcher.Widget3D.MainAppContext;
import com.iLoong.launcher.Widget3D.WidgetPluginViewMetaData;


public class iLoongClean4 implements IWidget3DPlugin
{
	
	@Override
	public View3D getWidget(
			MainAppContext context ,
			int widgetId )
	{
		return new WidgetClean4( "widgetClean4" , context , widgetId );
	}
	
	@Override
	public WidgetPluginViewMetaData getWidgetPluginMetaData(
			MainAppContext arg0 ,
			int arg1 )
	{
		return null;
	}
	
	@Override
	public void preInitialize(
			MainAppContext arg0 )
	{
		// TODO Auto-generated method stub
	}
}
